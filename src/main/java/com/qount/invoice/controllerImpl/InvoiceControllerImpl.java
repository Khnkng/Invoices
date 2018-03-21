package com.qount.invoice.controllerImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.helper.InvoiceHistoryHelper;
import com.qount.invoice.helper.LateFeeHelper;
import com.qount.invoice.helper.PostHelper;
import com.qount.invoice.model.Company2;
import com.qount.invoice.model.FilterModel;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceCommission;
import com.qount.invoice.model.InvoiceFilter;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceMetrics;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.service.InvoiceDimension;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.CurrencyConverter;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.ResponseUtil;
import com.qount.invoice.utils.Utilities;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 6 Feb 2016
 *
 */
public class InvoiceControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoiceControllerImpl.class);

	public static Invoice createInvoice(String userID, String companyID, Invoice invoice) {
		LOGGER.debug("entered createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)"
				+ invoice);
		Connection connection = null;
		Invoice invoiceRecurring = new Invoice();
		try {
			if (invoice == null || StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR + ":userID and companyID are mandatory",
						Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			boolean invoiceExists = MySQLManager.getInvoiceDAOInstance().invoiceExists(connection, invoice.getNumber(),
					companyID);
			LOGGER.debug("invoiceExists:" + invoiceExists);
			if (invoiceExists) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.number.exists"), 412);
			}

			if (!"onlyonce".equalsIgnoreCase(invoice.getRecurringFrequency())) {
				BeanUtils.copyProperties(invoiceRecurring, invoice);
			}

			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, true);
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(invoice.getCompany_id());
			invoicePreference = MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection,
					invoicePreference);
			if (invoicePreference != null && StringUtils.isNotBlank(invoicePreference.getDefaultTitle())) {
				invoice.setMailSubject(invoicePreference.getDefaultTitle());
			}
			String base64StringOfAttachment = null;
			if (invoice.getPdf_data() != null) {
				String url = PropertyManager.getProperty("report.pdf.url");
				LOGGER.debug("url::" + url);
				base64StringOfAttachment = HTTPClient.postAndGetBase64StringResult(url,
						new ObjectMapper().writeValueAsString(invoice.getPdf_data()));
				if (StringUtils.isNotBlank(base64StringOfAttachment)) {
					invoice.setAttachmentBase64(base64StringOfAttachment);
				}
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			String jobId = null;
			if (StringUtils.isNotBlank(invoice.getRemainder_name())) {
				jobId = getJobId(connection, invoice);
				if (StringUtils.isNotBlank(jobId) && invoice.isSendMail()) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				}
				invoice.setRemainder_job_id(jobId);
			}
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					if (StringUtils.isBlank(invoice.getState())
							|| invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)
							|| invoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
						invoice.setState(Constants.INVOICE_STATE_SENT);
					}
				} else {
					throw new WebApplicationException("error sending email", Constants.EXPECTATION_FAILED);
				}
			} else {
				if (StringUtils.isBlank(invoice.getState())
						|| invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)) {
					invoice.setState(Constants.INVOICE_STATE_DRAFT);
				}
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						"Database Error", Status.EXPECTATION_FAILED));
			}
			connection.setAutoCommit(false);
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				if (invoice.isSendMail())
					PostHelper.createPost(userID, companyID, invoice.getId());
				List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection,
						invoiceObj.getInvoiceLines());
				if (!invoiceLineResult.isEmpty()) {
					createInvoiceCommissions(connection, invoice.getCommissions(), invoice.getUser_id(), companyID,
							invoice.getId(), invoice.getNumber(), invoice.getAmount(), invoice.getCurrency());
					new InvoiceDimension().create(connection, companyID, invoiceObj.getInvoiceLines());
					//creating invoice history 
					String description = "Amount: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount())+""
							+ " Created By:"+userID;
					InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoiceObj,description,Constants.CREATED);
					MySQLManager.getInvoice_historyDAO().create(connection, history);
					connection.commit();
					connection.setAutoCommit(true);
					// creating late fee journal
					invoice.setJournal_job_id(LateFeeHelper.scheduleJournalForLateFee(invoiceObj));
				}
				// journal should not be created for draft state invoice.
				if (invoice.isSendMail())
					// saving dimensions of journal lines
					CommonUtils.createJournal(
							new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(),
							userID, companyID);
				if (!"onlyonce".equalsIgnoreCase(invoice.getRecurringFrequency())) {
					new Thread(() -> {
						createRecurringInvoice(invoiceRecurring, userID);
					}).start();
				}
				Invoice result = InvoiceParser.convertTimeStampToString(invoiceObj);
				LOGGER.debug("result:" + result);
				return result;
			}
			throw new WebApplicationException(Constants.FAILURE_STATUS_STR, Constants.EXPECTATION_FAILED);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID
					+ " Invoice invoice)" + invoice);
		}

	}

	private static String getJobId(Connection conn, Invoice invoice) {
		try {
			LOGGER.debug("entered getJobId invoice:" + invoice);
			if (invoice == null || StringUtils.isBlank(invoice.getRemainder_name())) {
				return null;
			}
			String remainderServieUrl = Utilities.getLtmUrl(
					PropertyManager.getProperty("remainder.service.docker.hostname"),
					PropertyManager.getProperty("remainder.service.docker.port"));
			// remainderServieUrl =
			// "http://remainderservice-dev.be0c8795.svc.dockerapp.io:93/";
			// remainderServieUrl = "http://localhost:8080/";
			remainderServieUrl += "RemainderService/mail/schedule";
			LOGGER.debug("remainderServieUrl::" + remainderServieUrl);
			JSONObject remainderJsonObject = new JSONObject();
			if (invoice.getRemainder_name().equalsIgnoreCase(Constants.ON_DUE_DATE_THEN_WEEKLY_AFTERWARD)
					|| invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_UNTIL_PAID)) {
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else if (invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_START_TWO_WEEKS_BEFORE_DUE)) {
				String dueDateStr = invoice.getDue_date();
				Date dueDate = CommonUtils.getDate(dueDateStr, Constants.TIME_STATMP_TO_BILLS_FORMAT);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dueDate);
				cal.add(Calendar.DATE, 14);
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.remainder.name"), 412);
			}
			JSONObject custom_args = new JSONObject();
			custom_args.put("SERVER_INSTANCE_MODE", PropertyManager.getProperty("SERVER_INSTANCE_MODE"));
			custom_args.put("type", Constants.INVOICE);
			custom_args.put("id", invoice.getId());
			String from = Constants.QOUNT;
			String aliasName = null;
			String firstName = invoice.getCompany_contact_first_name();
			if(StringUtils.isNotBlank(firstName)) {
				String lastName = invoice.getCompany_contact_last_name();
				aliasName = firstName;
				if(StringUtils.isNotBlank(lastName)) {
					aliasName += " " + lastName;	
				} 
			}
			if(StringUtils.isNotBlank(aliasName+Constants.VIA_QOUNT)) {
				from = aliasName;
			}
			invoice.setFrom(from);
			remainderJsonObject.put("custom_args", custom_args);
			remainderJsonObject.put("from_name", from);
			remainderJsonObject.put("emails", invoice.getRecepientsMails());
			remainderJsonObject.put("type", Constants.INVOICE);
			remainderJsonObject.put("account", Constants.ACCOUNT);
			remainderJsonObject.put("from", Constants.FROM);
			String subject = PropertyManager.getProperty("invoice.remainder.mail.subject") + invoice.getCompanyName();
			if (StringUtils.isNotEmpty(invoice.getMailSubject())) {
				subject = invoice.getMailSubject();
			}
			invoice.setSubject(subject);
			remainderJsonObject.put("subject", subject);
			remainderJsonObject.put("mailBodyContentType", PropertyManager.getProperty("invoice.mailBodyContentType"));
			String mail_body = PropertyManager.getProperty("invoice.remainder.mail.template");
			String amount_due = getTwoDecimalNumberAsString(invoice.getAmount_due());
			String due_date = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
					Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			String invoiceLinkUrl = PropertyManager.getProperty("invoice.payment.link") + invoice.getId();
			String currency = StringUtils.isEmpty(invoice.getCurrency()) ? ""
					: Utilities.getCurrencySymbol(invoice.getCurrency());
			mail_body = mail_body.replace("{{invoice number}}", invoice.getNumber())
					.replace("{{amount}}", currency + amount_due).replace("{{dueDays}}", due_date)
					.replace("${invoiceLinkUrl}", invoiceLinkUrl)
					.replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"));
			remainderJsonObject.put("mail_body", mail_body);
			System.out.println(remainderJsonObject);
			if (StringUtils.isNotBlank(invoice.getAttachmentBase64())) {
				JSONArray attachments = new JSONArray();
				JSONObject attahcment = new JSONObject();
				attahcment.put("type", Constants.APPLICATION_PDF);
				attahcment.put("filename", Constants.INVOICE_PDF_NAME);
				attahcment.put("content", invoice.getAttachmentBase64());
				attachments.put(attahcment);
				remainderJsonObject.put("attachments", attachments);
			}
			remainderJsonObject.put("userId", invoice.getUser_id());
			remainderJsonObject.put("companyId", invoice.getCompany_id());
			String attachmentsMetadata = invoice.getAttachments_metadata();
			if (StringUtils.isNotBlank(attachmentsMetadata)) {
				JSONObject attachmentsMetdataObj = new JSONObject(attachmentsMetadata);
				if (CommonUtils.isValidJSON(attachmentsMetdataObj)) {
					remainderJsonObject.put("s3_attachments_sourceId", attachmentsMetdataObj.optString("sourceId"));
				}
			}
			if(StringUtils.isNotBlank(invoice.getCompany_email_id())) {
				remainderJsonObject.put("reply_to_email",invoice.getCompany_email_id());
				if(StringUtils.isNotBlank(aliasName)) {
					remainderJsonObject.put("reply_to_name",aliasName);
				}
			}
			LOGGER.debug("remainderJsonObject::" + remainderJsonObject);
			// remainderJsonObject.put("startDate",invoice.getRemainder().getDate());
			Object jobIdObj = HTTPClient.postObject(remainderServieUrl, remainderJsonObject.toString());
			return jobIdObj.toString();
		} catch (WebApplicationException e) {
			LOGGER.error("error creating job id", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error creating job id", e);
		} finally {
			LOGGER.debug("exited getJobId invoice:" + invoice);
		}
		return null;
	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		LOGGER.debug("entered updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID
				+ ": invoice" + invoice);
		Connection connection = null;
		boolean isJERequired = false;
		try {
			// journal should not be created for draft state invoice.
			Invoice dbInvoice = getInvoice(invoiceID);
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, false);
			invoiceObj.setId(invoiceID);
			validateInvoiceEdit(dbInvoice, invoiceObj, userID, companyID);
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(invoice.getCompany_id());
			connection = DatabaseUtilities.getReadWriteConnection();
			invoicePreference = MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection,
					invoicePreference);
			if (invoicePreference != null && StringUtils.isNotBlank(invoicePreference.getDefaultTitle())) {
				invoice.setMailSubject(invoicePreference.getDefaultTitle());
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			String base64StringOfAttachment = null;
			if (invoice.getPdf_data() != null) {
				String url = PropertyManager.getProperty("report.pdf.url");
				LOGGER.debug("url::" + url);
				base64StringOfAttachment = HTTPClient.postAndGetBase64StringResult(url,
						new ObjectMapper().writeValueAsString(invoice.getPdf_data()));
				if (StringUtils.isNotBlank(base64StringOfAttachment)) {
					invoice.setAttachmentBase64(base64StringOfAttachment);
				}
			}
			boolean createNewRemainder = false;
			boolean deleteOldRemainder = false;
			if (StringUtils.isBlank(dbInvoice.getRemainder_name())
					&& StringUtils.isNotBlank(invoice.getRemainder_name())) {
				// no remainder in db creating new
				createNewRemainder = true;
			}
			if (!StringUtils.isAnyBlank(dbInvoice.getRemainder_name(), invoice.getRemainder_name())
					&& !dbInvoice.getRemainder_name().equalsIgnoreCase(invoice.getRemainder_name())) {
				// different remainder for invoice
				createNewRemainder = true;
				deleteOldRemainder = true;
			}
			String jobId = null;
			if (createNewRemainder) {
				if (deleteOldRemainder) {
					String result = Utilities.unschduleInvoiceJob(dbInvoice.getRemainder_job_id());
					if (StringUtils.isNotBlank(result) && !result.trim().equalsIgnoreCase("true")) {
						throw new WebApplicationException(PropertyManager.getProperty("error.deleting.invoice.job.id"),
								Constants.EXPECTATION_FAILED);
					}
				}
				jobId = getJobId(connection, invoice);
				if (StringUtils.isNotBlank(jobId)
						&& !dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				}
				invoice.setRemainder_job_id(jobId);
			}
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					if (StringUtils.isBlank(invoice.getState())
							|| invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)
							|| invoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
						invoice.setState(Constants.INVOICE_STATE_SENT);
					}
				} else {
					throw new WebApplicationException("error sending email", Constants.EXPECTATION_FAILED);
				}
			} else {
				if (StringUtils.isBlank(invoice.getState())
						|| invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)) {
					invoice.setState(Constants.INVOICE_STATE_DRAFT);
				}
			}
			boolean invoiceExists = MySQLManager.getInvoiceDAOInstance().invoiceExists(connection, invoice.getNumber(),
					companyID, invoiceID);
			LOGGER.debug("invoiceExists:" + invoiceExists);
			if (invoiceExists) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.number.exists"), 412);
			}
			if (invoice != null) {
				invoice.setId(invoiceID);
				if (invoice.isSendMail()) {
					PostHelper.createPost(userID, companyID, invoice.getId());
					isJERequired = true;
				} else if (!Constants.INVOICE_STATE_DRAFT.equalsIgnoreCase(dbInvoice.getState())) {
					String tempDueDate = invoiceObj.getInvoice_date();
					invoiceObj.setInvoice_date(InvoiceParser.convertTimeStampToString(invoiceObj.getInvoice_date(),
							Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
					isJERequired = !invoice.prepareJSParemeters().equals(dbInvoice.prepareJSParemeters());
					invoiceObj.setInvoice_date(tempDueDate);
				}
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						"Database Error", Status.EXPECTATION_FAILED));
			}
			connection.setAutoCommit(false);
			String dbIinvoiceState = dbInvoice.getState();
			if (StringUtils.isNotBlank(dbIinvoiceState) && !dbIinvoiceState.equals(Constants.INVOICE_STATE_DRAFT)) {
				invoice.setState(dbIinvoiceState);
			}
			invoiceObj.setAmount_due((invoice.getSub_total() + invoice.getTax_amount() + invoice.getLate_fee_amount())-dbInvoice.getAmount_paid());
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				InvoiceLine invoiceLine = new InvoiceLine();
				invoiceLine.setInvoice_id(invoiceID);
				InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance()
						.deleteByInvoiceId(connection, invoiceLine);
				if (deletedInvoiceLineResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection,
							invoiceObj.getInvoiceLines());
					if (invoiceLineResult != null) {
						// updating dimensions for an invoice
						new InvoiceDimension().update(connection, companyID, invoiceObj.getInvoiceLines());
						updateInvoiceCommissions(connection, invoice.getCommissions(), invoice.getUser_id(), companyID,
								invoice.getId(), invoice.getNumber(), invoice.getAmount(), invoice.getCurrency());
						//creating invoice history 
						String description = "Amount: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount())+""
								+ " Updated By:"+userID;
						InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoiceObj,description,Constants.UPDATED);
						MySQLManager.getInvoice_historyDAO().create(connection, history);
						connection.commit();
						connection.setAutoCommit(true);
						// late fee changes
						LateFeeHelper.handleLateFeeJEChanges(dbInvoice, invoiceObj);
					}
					if (isJERequired) {
						CommonUtils.createJournal( new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
					}
					return InvoiceParser.convertTimeStampToString(invoiceResult);
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID
					+ ": invoice" + invoice);
		}
	}

	public static String toCommaSeparatedString(List<String> strings) {
		String result = null;
		if (strings != null && !strings.isEmpty()) {
			result = "";
			for (int i = 0; i < strings.size(); i++) {
				result += "" + strings.get(i) + ",";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	public static Invoice updateInvoiceState(String invoiceID, Invoice invoice, String userID, String companyID) {
		LOGGER.debug("entered updateInvoiceState invoiceID:" + invoiceID + ": invoice" + invoice);
		Connection connection = null;
		try {
			if (invoice == null || StringUtils.isAnyEmpty(invoiceID, invoice.getState())) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			invoice.setId(invoiceID);
			connection = DatabaseUtilities.getReadWriteConnection();
			Invoice dbInvoice = getInvoice(invoiceID);
			if (dbInvoice == null || StringUtils.isBlank(dbInvoice.getId())) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.not.found"), 412);
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						"Database Error", Status.EXPECTATION_FAILED));
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			switch (invoice.getState()) {
			case "sent":
				if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)
						|| dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.error.msg"), 412);
				}
				return markInvoiceAsSent(connection, invoice);
			case "paid":
				return markInvoiceAsPaid(connection, invoice, dbInvoice);
			default:
				break;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoiceState invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	private static Invoice markInvoiceAsSent(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered markInvoiceAsSent invoice:" + invoice);
		try {
			Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoice.getId());
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)
					|| dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)
					|| dbInvoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.sent.msg"), 412);
			}
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().updateState(connection, invoice);
			if (invoiceResult != null) {
				String description = invoice.getUser_id();
				InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoice,description,Constants.MARKED_AS_SENT);
				MySQLManager.getInvoice_historyDAO().create(connection, history);
				// creating late fee journal
				invoice.setJournal_job_id(LateFeeHelper.scheduleJournalForLateFee(invoice));
				CommonUtils.createJournal(
						new JSONObject().put("source", "invoicePayment").put("sourceID", invoice.getId()).toString(),
						invoice.getUser_id(), invoice.getCompany_id());
				return invoice;
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in markInvoiceAsSent invoice:" + invoice, e);
			throw e;
		} finally {
			LOGGER.debug("exited markInvoiceAsSent invoice:" + invoice);
		}
		return null;
	}

	private static Invoice markInvoiceAsPaid(Connection connection, Invoice invoice, Invoice dbInvoice)
			throws Exception {
		try {
			LOGGER.debug("entered markInvoiceAsPaid invoice:" + invoice);
			if (dbInvoice == null) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice not found"), 404);
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.msg"), 412);
			}
			if (invoice.getAmount() > dbInvoice.getAmount_due()) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_DRAFT)) {
				throw new WebApplicationException(PropertyManager.getProperty("draft.invoice.paid.validation"), 412);
			}
			if (markAsPaid(connection, invoice, dbInvoice)) {
				return invoice;
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error in markInvoiceAsPaid invoice:" + invoice, e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in markInvoiceAsPaid invoice:" + invoice, e);
			throw e;
		} finally {
			LOGGER.debug("exited markInvoiceAsPaid invoice:" + invoice);
		}
		return null;
	}

	private static boolean markAsPaid(Connection connection, Invoice invoice, Invoice dbInvoice) throws Exception {
		try {
			LOGGER.debug("entered markAsPaid invoice:" + invoice);
			connection.setAutoCommit(false);
			PaymentLine line = new PaymentLine();
			List<PaymentLine> payments = new ArrayList<PaymentLine>();
			if (dbInvoice.getAmount_paid() == 0) {
				// new payment
				if (StringUtils.isNotBlank(dbInvoice.getDiscount_id())) {
					double discount = invoice.getDiscount();
					// having discount
					// invoice_discounts = new InvoiceDiscounts();
					// invoice_discounts.setId(dbInvoice.getDiscount_id());
					// invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().get(connection,
					// invoice_discounts);
					// long daysDifference = InvoiceParser.getDateDifference(new Date(), DateUtils
					// .getDateFromString(dbInvoice.getDue_date(),
					// Constants.TIME_STATMP_TO_INVOICE_FORMAT));
					// // 10 10
					// boolean isDiscountApplicable = daysDifference >= invoice_discounts.getDays();
					// if (isDiscountApplicable) {
					if (invoice.getAmount() + discount <= dbInvoice.getAmount()) {
						if (dbInvoice.getAmount() == invoice.getAmount() + discount) {
							line.setDiscount(discount);
						}
					} else {
						throw new WebApplicationException(
								PropertyManager.getProperty("invoice.amount.greater.than.error"));
					}
				}
			}
			Payment payment = new Payment();
			payment.setCompanyId(invoice.getCompany_id());
			payment.setCurrencyCode(invoice.getCurrency());
			payment.setId(UUID.randomUUID().toString());
			payment.setPaymentAmount(new BigDecimal(invoice.getAmount()));
			payment.setPaymentDate(
					invoice.getPayment_date() == null ? DateUtils.getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT)
							: invoice.getPayment_date());

			payment.setReceivedFrom(invoice.getCustomer_id());
			payment.setReferenceNo(invoice.getReference_number());
			payment.setDepositedTo(invoice.getBank_account_id());
			payment.setType(invoice.getPayment_method());
			Timestamp invoice_date = InvoiceParser.convertStringToTimeStamp(invoice.getInvoice_date(),
					Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			line.setId(UUID.randomUUID().toString());
			line.setInvoiceId(invoice.getId());
			line.setAmount(new BigDecimal(invoice.getAmount()));
			payments.add(line);
			payment.setPayment_status(Constants.APPLIED);
			payment.setPaymentLines(payments);
			// 100 0 90 10
			invoice.setAmount_due(
					dbInvoice.getAmount() - (dbInvoice.getAmount_paid() + invoice.getAmount() + invoice.getDiscount()));
			LOGGER.debug("*********************************************");
			LOGGER.debug("invoice due amount::" + invoice.getAmount_due());
			LOGGER.debug("dbInvoice::" + dbInvoice);
			LOGGER.debug("dbInvoice job id::" + dbInvoice.getRemainder_job_id());
			LOGGER.debug("*********************************************");
			if (invoice.getAmount_due() == 0.0) {
				LOGGER.debug("amount due is 0");
				invoice.setState(Constants.INVOICE_STATE_PAID);
				// unscheduling invoice jobs if any
				Utilities.unschduleInvoiceJob(dbInvoice.getRemainder_job_id());
			}
			if (MySQLManager.getPaymentDAOInstance().save(payment, connection, false) != null) {
				//creating invoice history 
				String description = "Amount: "+Utilities.getNumberAsCurrencyStr(dbInvoice.getCurrency(), dbInvoice.getAmount())+
						",Amount Due: "+Utilities.getNumberAsCurrencyStr(dbInvoice.getCurrency(), dbInvoice.getAmount_due())+
						",Amount Paid: "+Utilities.getNumberAsCurrencyStr(dbInvoice.getCurrency(), dbInvoice.getAmount_paid())+
						",Ref Num: "+payment.getReferenceNo()+
						",State: "+InvoiceParser.getDisplayState(dbInvoice.getState());
				InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(dbInvoice,description,Constants.MARKED_AS_PAID);
				MySQLManager.getInvoice_historyDAO().create(connection, history);
				connection.commit();
				// creating commissions if any
				InvoiceCommission invoiceCommission = new InvoiceCommission();
				invoiceCommission.setInvoice_id(invoice.getId());
				if (invoice.getAmount_due() == 0.0) {
					List<InvoiceCommission> dbInvoiceCommissions = MySQLManager.getInvoiceDAOInstance()
							.getInvoiceCommissions(invoiceCommission);
					createInvoicePaidCommissions(connection, dbInvoiceCommissions, dbInvoice.getUser_id(),
							dbInvoice.getCompany_id(), dbInvoice.getId(), dbInvoice.getAmount(),
							dbInvoice.getCurrency());
				}
				CommonUtils.createJournal(
						new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(),
						invoice.getUser_id(), invoice.getCompany_id());
				return true;
			}

		} catch (WebApplicationException e) {
			LOGGER.error("error in markInvoiceAsPaid invoice:" + invoice, e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in markAsPaid invoice:" + invoice, e);
			throw e;
		} finally {
			LOGGER.debug("exited markAsPaid invoice:" + invoice);
		}
		return false;
	}

	public static List<Invoice> getInvoices(String userID, String companyID, String state) {
		List<Invoice> invoiceLst = null;
		try {
			LOGGER.debug("entered get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			Map<String, String> invoicePaymentIdMap = null;
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				invoicePaymentIdMap = MySQLManager.getInvoiceDAOInstance()
						.getInvoicePaymentsIds(InvoiceParser.getInvoiceIds(invoiceLst));
			}
			// Map<String, String> badges =
			// MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			InvoiceParser.formatInvoices(invoiceLst, invoicePaymentIdMap);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
		}
		if (invoiceLst == null) {
			invoiceLst = new ArrayList<>();
		}
		return invoiceLst;
	}

	public static Invoice getInvoice(String invoiceID) {
		try {
			LOGGER.debug("entered getInvoice invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice result = InvoiceParser
					.convertTimeStampToString(MySQLManager.getInvoiceDAOInstance().get(invoiceID));
			InvoiceCommission invoiceCommission = new InvoiceCommission();
			invoiceCommission.setInvoice_id(invoiceID);
			result.setCommissions(MySQLManager.getInvoiceDAOInstance().getInvoiceCommissions(invoiceCommission));
			Company2 company2 = CommonUtils.retrieveCompany(result.getUser_id(), result.getCompany_id());
			result.setCompany(company2);
			InvoiceParser.convertAmountToDecimal(result);
			if(result.getState().equals(Constants.INVOICE_STATE_SENT)){
				JSONObject payLoadObject = new JSONObject();
				payLoadObject.put("due_date", result.getDue_date());
				payLoadObject.put("amount", result.getAmount());
				result.setDiscount(InvoiceDiscountsControllerImpl.getDiscountAmount(result.getCompany_id(), result.getDiscount_id(), payLoadObject.toString()));
			}
			LOGGER.debug("getInvoice result:" + result);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited getInvoice invocieId:" + invoiceID);
		}

	}

	public static InvoicePreference getInvoicePreference(String invoiceID) {
		Connection connection = null;
		try {
			LOGGER.debug("entered getInvoicePreference invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice result = InvoiceParser
					.convertTimeStampToString(MySQLManager.getInvoiceDAOInstance().get(invoiceID));
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(result.getCompany_id());
			connection = DatabaseUtilities.getReadConnection();
			invoicePreference = MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection,
					invoicePreference);
			LOGGER.debug("getInvoicePreference result:" + result);
			return invoicePreference;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited getInvoicePreference invocieId:" + invoiceID);
			DatabaseUtilities.closeConnection(connection);
		}

	}

	public static Invoice deleteInvoiceById(String userID, String companyID, String invoiceID) {
		Connection connection = null;
		try {
			LOGGER.debug("entered deleteInvoiceById userID: " + userID + " companyID: " + companyID + " invoiceID"
					+ invoiceID);
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, companyID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			Invoice invoiceObj = MySQLManager.getInvoiceDAOInstance().delete(connection, invoice);
			String description = "Amount: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount());
			InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoiceObj,description,Constants.DELETED);
			MySQLManager.getInvoice_historyDAO().create(connection, history);
			CommonUtils.deleteJournal(userID, companyID, invoiceID + "@" + "invoice");
			Utilities.unschduleInvoiceJob(invoice.getRemainder_job_id());
			return InvoiceParser.convertTimeStampToString(invoiceObj);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited deleteInvoiceById userID: " + userID + " companyID: " + companyID + " invoiceID"
					+ invoiceID);
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static boolean deleteInvoicesById(String userID, String companyID, List<String> ids) {
		Connection connection = null;
		try {
			LOGGER.debug("entered deleteInvoicesById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			if (StringUtils.isAnyBlank(userID, companyID) || ids == null || ids.isEmpty()) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			CommonUtils.deleteJournalsAsync(userID, companyID, ids);
			connection = DatabaseUtilities.getReadWriteConnection();
			List<String> jobIds = MySQLManager.getInvoiceDAOInstance().getInvoiceJobsList(connection, commaSeparatedLst);
			deleteInvoiceJobsAsync(jobIds);
			//creating invoice history 
			String description = userID;
			List<InvoiceHistory> histories = InvoiceHistoryHelper.getInvoiceHistory(ids, userID, companyID,Constants.DELETED,description);
			MySQLManager.getInvoice_historyDAO().create(connection, histories);
			return MySQLManager.getInvoiceDAOInstance().deleteLst(userID, companyID, commaSeparatedLst);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited deleteInvoicesById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			DatabaseUtilities.closeConnection(connection);
		}
	}

	private static void deleteInvoiceJobsAsync(List<String> jobIds) {
		try {
			Runnable task = () -> {
				LOGGER.debug("entered deleteInvoiceJobsAsync jobIds:" + jobIds);
				try {
					Utilities.unschduleInvoiceJobs(jobIds);
				} catch (Exception e) {
					LOGGER.error(CommonUtils.getErrorStackTrace(e));
				}
			};
			new Thread(task).start();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			LOGGER.debug("exited deleteInvoiceJobsAsync jobIds:" + jobIds);
		}
	}

	public static boolean updateInvoicesAsSent(String userID, String companyID, List<String> ids) {
		Connection connection = null;
		try {
			LOGGER.debug("entered updateInvoicesAsSent userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			if (StringUtils.isAnyBlank(userID, companyID) || ids == null || ids.isEmpty()) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			List<Invoice> invoices = MySQLManager.getInvoiceDAOInstance().getInvoices(commaSeparatedLst);
			if (invoices == null || invoices.size() == 0) {
				throw new WebApplicationException(PropertyManager.getProperty("invoices.not.found"), 412);
			}
			if (InvoiceParser.isPaidStateInvoicePresent(invoices)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.sent.error.msg"), 412);
			}
			boolean isSent = MySQLManager.getInvoiceDAOInstance().updateStateAsSent(userID, companyID,
					commaSeparatedLst);
			if (isSent) {
				connection = DatabaseUtilities.getReadWriteConnection();
				for (String invoiceID : ids) {
					CommonUtils.createJournalAsync(
							new JSONObject().put("source", "invoice").put("sourceID", invoiceID).toString(), userID,
							companyID);
					// creating late fee journal
					Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoiceID);
					dbInvoice.setJournal_job_id(LateFeeHelper.scheduleJournalForLateFee(dbInvoice));
				}
				//creating invoice histories 
				String description = userID;
				List<InvoiceHistory> histories = InvoiceHistoryHelper.getInvoiceHistory(ids, userID, companyID,Constants.MARKED_AS_SENT,description);
				MySQLManager.getInvoice_historyDAO().create(connection, histories);
			}
			return isSent;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error("Error marking invoice as sent", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited updateInvoicesAsSent userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			DatabaseUtilities.closeConnection(connection);
		}
	}

	private static boolean sendInvoiceEmail(Invoice invoice) throws Exception {
		try {
			LOGGER.debug("entered sendInvoiceEmail invoice: " + invoice);
			String template = PropertyManager.getProperty("invocie.mail.template");
			String invoiceLinkUrl = PropertyManager.getProperty("invoice.payment.link") + invoice.getId();
			String dueDate = InvoiceParser.convertTimeStampToString(invoice.getDue_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			String currency = StringUtils.isEmpty(invoice.getCurrency()) ? ""
					: Utilities.getCurrencySymbol(invoice.getCurrency());
			String amount = getTwoDecimalNumberAsString(invoice.getAmount_due());
			String customerFirstName = invoice.getCustomer_first_name();
			String customerLastName = invoice.getCustomer_last_name();
			String customerFirstNameFirstLetter = StringUtils.isNotBlank(customerFirstName)
					? customerFirstName.charAt(0) + ""
					: "";
			String customerLastNameFirstLetter = StringUtils.isNotBlank(customerLastName)
					? customerLastName.charAt(0) + ""
					: "";
			template = template
					.replace("{{invoice number}}", StringUtils.isBlank(invoice.getNumber()) ? "" : invoice.getNumber())
					.replace("{{company name}}",
							StringUtils.isEmpty(invoice.getCompanyName()) ? "" : invoice.getCompanyName())
					.replace("{{amount}}", currency + amount)
					.replace("{{due date}}", StringUtils.isEmpty(dueDate) ? "" : dueDate)
					.replace("${invoiceLinkUrl}", invoiceLinkUrl)
					.replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"))
					.replace("{{customerFirstNameFirstLetter}}", customerFirstNameFirstLetter)
					.replace("{{customerLastNameFirstLetter}}", customerLastNameFirstLetter)
					.replace("{{customerFirstName}}", customerFirstName)
					.replace("{{customerLastName}}", customerLastName).replace("{{notes}}",
							StringUtils.isBlank(invoice.getEmail_notes()) ? "" : invoice.getEmail_notes());
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url + "HalfService/mails";
			// url = "https://dev-services.qount.io/HalfService/mails";
			JSONObject emailJson = getMailJson(invoice, template,
					PropertyManager.getProperty("mail.body.content.type"));
			Object result = HTTPClient.postUrlAndGetStatus(url, emailJson.toString());
			if (result != null) {
				JSONObject obj = new JSONObject(result.toString());
				if (obj.optInt("status") == 202) {
					return true;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited sendInvoiceEmail  invoice: " + invoice);
		}
		return false;
	}

	private static JSONObject getMailJson(Invoice invoice, String mail_body, String mailBodyContentType) {
		try {
			JSONObject result = new JSONObject();
			LOGGER.debug("entered getMailJson invoice: " + invoice);
			if (invoice == null) {
				throw new WebApplicationException(PropertyManager.getProperty("mail.invalid.input"), 412);
			}
			result.put("account", Constants.ACCOUNT);
			JSONArray personalizations = new JSONArray();
			result.put("personalizations", personalizations);
			JSONObject emailJson = new JSONObject();
			personalizations.put(emailJson);
			String subject = PropertyManager.getProperty("invoice.subject");
			subject += invoice.getCompanyName();
			invoice.setSubject(subject);
			JSONObject custom_args = new JSONObject();
			custom_args.put("SERVER_INSTANCE_MODE", PropertyManager.getProperty("SERVER_INSTANCE_MODE"));
			custom_args.put("type", Constants.INVOICE);
			custom_args.put("id", invoice.getId());
			emailJson.put("custom_args", custom_args);
			if (StringUtils.isNotEmpty(invoice.getMailSubject())) {
				subject = invoice.getMailSubject();
			}
			emailJson.put("subject", subject);
			JSONArray toArray = new JSONArray();
			emailJson.put("to", toArray);
			List<String> emails = invoice.getRecepientsMails();
			if (emails == null || emails.isEmpty()) {
				throw new WebApplicationException(PropertyManager.getProperty("mail.recipients.email.empty.error.msg"),
						412);
			}
			Iterator<String> emailsItr = emails.iterator();
			while (emailsItr.hasNext()) {
				String email = emailsItr.next();
				if (StringUtils.isNotBlank(email)) {
					JSONObject emailObj = new JSONObject();
					emailObj.put("email", email);
					toArray.put(emailObj);
				}
			}
			JSONObject fromObj = new JSONObject();
			fromObj.put("email", Constants.FROM);
			String from = Constants.QOUNT;
			String aliasName = null;
			String firstName = invoice.getCompany_contact_first_name();
			if(StringUtils.isNotBlank(firstName)) {
				String lastName = invoice.getCompany_contact_last_name();
				aliasName = firstName;
				if(StringUtils.isNotBlank(lastName)) {
					aliasName += " "+ lastName;	
				} 
			}
			if(StringUtils.isNotBlank(aliasName + Constants.VIA_QOUNT)) {
				from = aliasName;
			}
			invoice.setFrom(from);
			fromObj.put("name", from);
			result.put("from", fromObj);
			JSONArray contentArr = new JSONArray();
			result.put("content", contentArr);
			JSONObject contentObj = new JSONObject();
			contentArr.put(contentObj);
			if (StringUtils.isBlank(mail_body)) {
				throw new WebApplicationException(PropertyManager.getProperty("mail.invalid.input.empty.mail_body"),
						412);
			}
			contentObj.put("value", mail_body);
			if (StringUtils.isBlank(mailBodyContentType)) {
				throw new WebApplicationException(
						PropertyManager.getProperty("mail.invalid.input.empty.mail_body_content_type"), 412);
			}
			contentObj.put("type", mailBodyContentType);
			if (StringUtils.isNotBlank(invoice.getAttachmentBase64())) {
				JSONArray attachments = new JSONArray();
				JSONObject attahcment = new JSONObject();
				attahcment.put("type", Constants.APPLICATION_PDF);
				attahcment.put("filename", Constants.INVOICE_PDF_NAME);
				attahcment.put("content", invoice.getAttachmentBase64());
				attachments.put(attahcment);
				result.put("attachments", attachments);
			}
			result.put("userId", invoice.getUser_id());
			result.put("companyId", invoice.getCompany_id());
			String attachmentsMetadata = invoice.getAttachments_metadata();
			if (StringUtils.isNotBlank(attachmentsMetadata)) {
				JSONObject attachmentsMetdataObj = new JSONObject(attachmentsMetadata);
				if (CommonUtils.isValidJSON(attachmentsMetdataObj)) {
					result.put("s3_attachments_sourceId", attachmentsMetdataObj.optString("sourceId"));
				}
			}
			if(StringUtils.isNotBlank(invoice.getCompany_email_id())) {
				JSONObject reply_to = new JSONObject();
				reply_to.put("email",invoice.getCompany_email_id());
				if(StringUtils.isNotBlank(aliasName)) {
					reply_to.put("name",aliasName);
				}
				result.put("reply_to", reply_to);
			}
			return result;
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("error getMailJson invoice: " + invoice, e);
			throw e;
		} finally {
			LOGGER.debug("exited getMailJson invoice: " + invoice);
		}
	}

	private static String getTwoDecimalNumberAsString(double value) {
		try {
			String result = value + "";
			if (result.indexOf(".") != -1) {
				String resultSubStr = result.substring(result.indexOf(".") + 1, result.length());
				if (resultSubStr.length() < 2) {
					result += "0";
				}
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
		return null;
	}

	public static List<Invoice> getInvoicesByClientID(String userID, String companyID, String clientID) {
		try {
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceListByClientId(userID, companyID,
					clientID);
			InvoiceParser.convertAmountToDecimal(invoiceLst);
			return invoiceLst;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID + " clientID:" + clientID);
		}
	}

	public static Response getCount(String userID, String companyID) {
		try {
			LOGGER.debug("entered get count userID:" + userID + " companyID:" + companyID);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Map<String, String> badges = MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.formatBadges(badges);
			int unappliedCount = InvoiceDAOImpl.getInvoiceDAOImpl().getUnappliedPaymentsCount(userID, companyID);
			result.optJSONObject("badges").put("unappliedPaymentsCount", unappliedCount);
			return Response.status(200).entity(result.toString()).build();
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get count userID:" + userID + " companyID:" + companyID);
		}
	}

	public static Response getInvoiceMetrics(String userID, String companyID) {
		InvoiceMetrics convertedInvoiceMetrics = null;
		try {
			LOGGER.debug("entered get box values userID:" + userID + " companyID:" + companyID);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Company2 company2 = CommonUtils.retrieveCompany(userID, companyID);
			InvoiceDAO invoiceDAO = InvoiceDAOImpl.getInvoiceDAOImpl();
			InvoiceMetrics InvoiceMetrics = invoiceDAO.getInvoiceMetrics(companyID);
			if (InvoiceMetrics != null) {
				CurrencyConverter currencyConverter = new CurrencyConverter();
				convertedInvoiceMetrics = currencyConverter.converterValues(InvoiceMetrics, company2);
				return Response.status(200).entity(convertedInvoiceMetrics).build();
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get box values userID:" + userID + " companyID:" + companyID);
		}
	}

	public static InvoiceCommission createInvoiceCommission(Connection connection,
			InvoiceCommission invoiceCommission) {
		try {
			LOGGER.debug("entered createInvoiceCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
			if (invoiceCommission.isCreateBill()) {
				boolean isInvoiceCommissionCreated = InvoiceParser.createInvoiceCommisionBill(invoiceCommission, null);
				if (!isInvoiceCommissionCreated) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"),
							Constants.EXPECTATION_FAILED);
				}
			}
			InvoiceCommission result = MySQLManager.getInvoiceDAOInstance().createInvoiceCommission(connection,
					invoiceCommission);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error("error creating createInvoiceCommission", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error creating createInvoiceCommission", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited createInvoiceCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
		}
	}

	public static InvoiceCommission createInvoicePaidCommission(Connection connection,
			InvoiceCommission invoiceCommission) {
		try {
			LOGGER.debug(
					"entered createInvoicePaidCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
			if (invoiceCommission.isCreateBill()) {
				boolean isInvoiceCommissionCreated = InvoiceParser.createInvoiceCommisionBill(invoiceCommission,
						invoiceCommission.getId());
				if (!isInvoiceCommissionCreated) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"),
							Constants.EXPECTATION_FAILED);
				}
			}
			InvoiceCommission result = MySQLManager.getInvoiceDAOInstance().updateInvoiceCommissionBillState(connection,
					invoiceCommission);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error("error createInvoicePaidCommission", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error createInvoicePaidCommission", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited createInvoicePaidCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
		}
	}

	private static boolean createInvoiceCommissions(Connection connection, List<InvoiceCommission> commissions,
			String userId, String companyId, String invoiceId, String invoiceNumber, double invoiceAmount,
			String currency) {
		try {
			LOGGER.debug("entered createInvoiceCommissions invoiceCommisions:" + commissions);
			if (commissions != null && !commissions.isEmpty()) {
				Iterator<InvoiceCommission> commissionsItr = commissions.iterator();
				int invoiceNumberCounter = 1;
				while (commissionsItr.hasNext()) {
					InvoiceCommission commission = commissionsItr.next();
					if (commission != null) {
						String eventAt = commission.getEvent_at();
						if (StringUtils.isBlank(eventAt)) {
							throw new WebApplicationException(
									PropertyManager.getProperty("error.invoice.commission.empty.eventAt"),
									Constants.INVALID_INPUT);
						}
						if (!eventAt.equals(Constants.PAID)) {
							commission.setCreateBill(true);
						}
						commission.setUser_id(userId);
						commission.setCompany_id(companyId);
						commission.setInvoice_id(invoiceId);
						commission.setCurrency(currency);
						commission.setInvoice_amount(invoiceAmount);
						commission.setInvoice_number((invoiceNumberCounter++) + "_" + invoiceNumber);
						if (createInvoiceCommission(connection, commission) == null) {
							throw new WebApplicationException(
									PropertyManager.getProperty("error.invoice.commission.creation"),
									Constants.EXPECTATION_FAILED);
						}
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("WebApplicationException in createInvoiceCommissions", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in createInvoiceCommissions", e);
		} finally {
			LOGGER.debug("exited createInvoiceCommissions invoiceCommisions:" + commissions);
		}
		return false;
	}

	public static boolean createInvoicePaidCommissions(Connection connection, List<InvoiceCommission> commissions,
			String userId, String companyId, String invoiceId, double invoiceAmount, String currency) {
		try {
			LOGGER.debug("entered createInvoiceCommissions invoiceCommisions:" + commissions);
			if (commissions != null && !commissions.isEmpty()) {
				Iterator<InvoiceCommission> commissionsItr = commissions.iterator();
				while (commissionsItr.hasNext()) {
					InvoiceCommission commission = commissionsItr.next();
					if (commission != null) {
						String eventAt = commission.getEvent_at();
						if (StringUtils.isBlank(eventAt)) {
							throw new WebApplicationException(
									PropertyManager.getProperty("error.invoice.commission.empty.eventAt"),
									Constants.INVALID_INPUT);
						}
						if (eventAt.equals(Constants.PAID) && !commission.isBillCreated()) {
							commission.setCreateBill(true);
						}
						commission.setUser_id(userId);
						commission.setCompany_id(companyId);
						commission.setInvoice_id(invoiceId);
						commission.setCurrency(currency);
						commission.setInvoice_amount(invoiceAmount);
						if (createInvoicePaidCommission(connection, commission) == null) {
							throw new WebApplicationException(
									PropertyManager.getProperty("error.invoice.commission.creation"),
									Constants.EXPECTATION_FAILED);
						}
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("WebApplicationException in createInvoiceCommissions", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in createInvoiceCommissions", e);
		} finally {
			LOGGER.debug("exited createInvoiceCommissions invoiceCommisions:" + commissions);
		}
		return false;
	}

	private static boolean updateInvoiceCommissions(Connection connection, List<InvoiceCommission> commissions,
			String userId, String companyId, String invoiceId, String invoiceNumber, double invoiceAmount,
			String currency) {
		try {
			LOGGER.debug("entered updateInvoiceCommissions invoiceCommisions:" + commissions);
			if (commissions != null && !commissions.isEmpty()) {
				Iterator<InvoiceCommission> commissionsItr = commissions.iterator();
				int invoiceNumberCounter = 1;
				while (commissionsItr.hasNext()) {
					InvoiceCommission commission = commissionsItr.next();
					if (commission != null) {
						if (commission.isUpdateBill()) {
							String eventAt = commission.getEvent_at();
							if (StringUtils.isBlank(eventAt)) {
								throw new WebApplicationException(
										PropertyManager.getProperty("error.invoice.commission.empty.eventAt"),
										Constants.INVALID_INPUT);
							}
							if (!eventAt.equals(Constants.PAID)) {
								commission.setCreateBill(true);
							}
							commission.setUser_id(userId);
							commission.setCompany_id(companyId);
							commission.setInvoice_id(invoiceId);
							commission.setCurrency(currency);
							commission.setInvoice_amount(invoiceAmount);
							String tempInvoiceNumber = StringUtils.isBlank(commission.getInvoice_number())
									? (invoiceNumberCounter++) + "_" + invoiceNumber
									: commission.getInvoice_number();
							commission.setInvoice_number(tempInvoiceNumber);
							if (StringUtils.isNotBlank(commission.getBill_id())) {
								if (StringUtils.isBlank(commission.getId())) {
									// bill id and commission id are same
									commission.setId(commission.getBill_id());
								}
								MySQLManager.getInvoiceDAOInstance().deleteInvoiceCommission(connection, commission);
								InvoiceParser.deleteInvoivceCommissionBill(commission);
							}
							if (!commission.isDelete()) {
								if (createInvoiceCommission(connection, commission) == null) {
									throw new WebApplicationException(
											PropertyManager.getProperty("error.invoice.commission.creation"),
											Constants.EXPECTATION_FAILED);
								}
							}
						}
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("WebApplicationException in updateInvoiceCommissions", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in updateInvoiceCommissions", e);
		} finally {
			LOGGER.debug("exited updateInvoiceCommissions invoiceCommisions:" + commissions);
		}
		return false;
	}
	
	

	public static List<Invoice> getInvoiceListForPayEvent(String userID, String companyID, String customerID,
			String billId) {
		List<Invoice> invoiceList = new ArrayList<>();
		try {
			LOGGER.debug("entered InvoiceControllerImpl.getInvoiceListForPayEvent");
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			InvoiceDAO invoiceDAO = InvoiceDAOImpl.getInvoiceDAOImpl();
			if (billId == null || billId.isEmpty()) {
				invoiceList = invoiceDAO.getUnmappedInvoiceList(companyID, customerID);
			} else {
				invoiceList = invoiceDAO.getMappedUnmappedInvoiceList(companyID, customerID, billId);
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited InvoiceControllerImpl.getInvoiceListForPayEvent");
		}
		return invoiceList;
	}

	public static void createRecurringInvoice(Invoice invoice, String userID) {
		System.out.println("Initialization createRecurringInvoices : " + new JSONObject(invoice));
		LOGGER.info("Initialization createRecurringInvoices : " + new JSONObject(invoice));
		try {
			int i = 1;
			String invoiceNumber = invoice.getNumber();
			String recurrance = invoice.getRecurringFrequency();
			Date invoiceDate = DateUtils.getTimestampFromString(invoice.getInvoice_date(),
					Constants.SIMPLE_DATE_FORMAT);
			Date maininvoiceDate = DateUtils.getTimestampFromString(invoice.getInvoice_date(),
					Constants.SIMPLE_DATE_FORMAT);
			Date endDate = DateUtils.getTimestampFromString(invoice.getRecurringEnddate());
			System.out.println("invoiceDate Before calc=" + invoiceDate);
			switch (recurrance) {
			case "weekly":
				invoiceDate = org.apache.commons.lang3.time.DateUtils.addWeeks(invoiceDate, 1);
				break;
			case "monthly":
				invoiceDate = org.apache.commons.lang3.time.DateUtils.addMonths(invoiceDate, 1);
				break;
			case "quarterly":
				invoiceDate = org.apache.commons.lang3.time.DateUtils.addMonths(invoiceDate, 3);
				break;
			}
			System.out.println("billDate After calc=" + invoiceDate);
			Date invoiceDueDate = DateUtils.getTimestampFromString(invoice.getDue_date(), Constants.SIMPLE_DATE_FORMAT); 
			String terms = invoice.getTerm();
			int netTermCount = 0;
			switch (terms) {
			case "net30": netTermCount = 30 ; break;
			case "net45": netTermCount = 45 ; break;
			case "net60": netTermCount = 60 ; break;
			case "net90": netTermCount = 90 ; break;
			case "custom": netTermCount = getDifferenceDays(maininvoiceDate, invoiceDueDate) ; break;
			default:
				break;
			}
			
			while (invoiceDate.compareTo(endDate) <= 0) {
				invoice.setRecurringFrequency("onlyonce");
				invoice.setNumber(invoiceNumber + "_" + i);
				invoice.setInvoice_date(Constants.DATE_TO_INVOICE_FORMAT.format(invoiceDate));
				invoiceDueDate = org.apache.commons.lang3.time.DateUtils.addDays(invoiceDate, netTermCount) ;
				
				invoice.setDue_date(Constants.DATE_TO_INVOICE_FORMAT.format(invoiceDueDate));
				
				// invoice.setDue_date(DateUtils.formatToString(DateUtils.getTimestampFromString(invoice.getDue_date(),
				// Constants.SIMPLE_DATE_FORMAT)));
				invoice.setRecurringEnddate(null);
				invoice.setHistories(null);
				invoice.setId("");
				Invoice response = createInvoice(userID, invoice.getCompany_id(), invoice);
				System.out.println(
						"Status invoiceRequestRecurring=" + new JSONObject(invoice) + "    Response=" + response);
				LOGGER.info("Status invoiceRequestRecurring=" + new JSONObject(invoice) + "    Response=" + response);
				System.out.println("invoiceDate Before calc=" + invoiceDate);
				switch (recurrance) {
				case "weekly":
					invoiceDate = org.apache.commons.lang3.time.DateUtils.addWeeks(invoiceDate, 1);
					break;
				case "monthly":
					invoiceDate = org.apache.commons.lang3.time.DateUtils.addMonths(invoiceDate, 1);
					break;
				case "quarterly":
					invoiceDate = org.apache.commons.lang3.time.DateUtils.addMonths(invoiceDate, 3);
					break;
				}
				System.out.println("invoiceDate After calc=" + invoiceDate);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static  int getDifferenceDays(Date d1, Date d2) {
	    int daysdiff = 0;
	    long diff = d2.getTime() - d1.getTime();
	    long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
	    daysdiff = (int) diffDays;
	    return daysdiff-1;//we need exclude one day for current date
	}
	public static String searchInvoices(String userID, String companyID, InvoiceFilter invoiceFilter) {
		JSONArray invoiceLst = null;
		Connection conn = null;
		try {
			LOGGER.debug("entered searchInvoices userID:" + userID + " companyID:" + companyID );
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			conn = DatabaseUtilities.getReadConnection();
			
//			String query = SqlQuerys.Invoice.GET_INVOICE_BY_FILTERS + prepareQuery(invoiceFilter, companyID) ;
			String query =   prepareQuery(invoiceFilter, companyID) ;
			invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceListByFilter(conn, userID, companyID,query,invoiceFilter.getAsOfDate()); 
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited searchInvoices userID:" + userID + " companyID:" + companyID  );
			DatabaseUtilities.closeConnection(conn);
		}
//		if (invoiceLst == null) {
//			invoiceLst = new ArrayList<>();
//		}
//		return Response.status(Status.OK).entity(new JSONObject(invoiceLst).toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
		return invoiceLst.toString();
	}
	
	
	private static String prepareQuery(InvoiceFilter invoiceFilter, String companyID) {
		
		DateTime asOfDate = new DateTime(DateUtils.getDateFromString(invoiceFilter.getAsOfDate(), Constants.SIMPLE_DATE_FORMAT));
		String asOfDateString = DateUtils.getStringFromDate(asOfDate.toDate(), Constants.DUE_DATE_FORMAT);
		String qryString = "SELECT invoice.due_date,invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`, invoice.`email_state`,invoice.`customer_id`,"
				+ "invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,"
				+ "invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`, company.name AS company_name,`company_customers`.`customer_name`  "
				+ "FROM invoice JOIN company ON invoice.`company_id` = company.`id` JOIN `company_customers` ON invoice.`customer_id` = company_customers.`customer_id` "
				+ "WHERE invoice.company_id='"+companyID+"'  AND invoice.invoice_date <='"+asOfDateString+"'  ";
		
		StringBuilder query = new StringBuilder();
		
//		query.append("SELECT invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`, invoice.`email_state`,invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`, company.name AS company_name,`company_customers`.`customer_name`  FROM invoice JOIN company ON invoice.`company_id` = company.`id` JOIN `company_customers` ON invoice.`customer_id` = company_customers.`customer_id` WHERE   ");
		
		for (FilterModel filter : invoiceFilter.getFilters()) {
			System.out.println(filter.getFilterName());
			if (filter.getFilterName().equals("customerName")) {
				filter.getValues();
				query.append(" AND company_customers.customer_name IN (" + getCommaSeparatedStringFromList(filter.getValues(), true) + ")  ");
			} else if (filter.getFilterName().equals("invoiceDate")) {
				query.append(" AND  invoice.due_date BETWEEN '" + filter.getValues().get(0) + "' AND '" + filter.getValues().get(1) + "'   ");
			} else if (filter.getFilterName().equals("currentState")) {
				query.append(" AND  invoice.state" + ("!=".equalsIgnoreCase(filter.getOperator()) ? " NOT " : "" ) + " IN (" + getCommaSeparatedStringFromList(filter.getValues(), true) + ")");
			}
		}

		qryString += query.toString();
		
		StringBuilder query2 = new StringBuilder();
		
		for (FilterModel filter : invoiceFilter.getFilters()) {
			System.out.println(filter.getFilterName());
			if (filter.getFilterName().equals("customerName")) {
				filter.getValues();
				query2.append(" AND  company_customers.customer_name IN (" + getCommaSeparatedStringFromList(filter.getValues(), true) + ")   ");
			} else if (filter.getFilterName().equals("invoiceDate")) {
				query2.append(" AND  invoice.due_date BETWEEN '" + filter.getValues().get(0) + "' AND '" + filter.getValues().get(1) + "'  ");
			}  
		}

		
		qryString +=  "  UNION ALL (SELECT invoice.due_date,invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`, invoice.`email_state`,invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,SUM(invoice_payments.payment_amount) AS `amount_due`,invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`, company.name AS company_name,`company_customers`.`customer_name`      "
				+ "  FROM `invoice_payments`  JOIN invoice_payments_lines ON invoice_payments_lines.payment_id = invoice_payments.id "
				+ "JOIN invoice ON invoice.id =invoice_payments_lines.invoice_id  JOIN company ON invoice.`company_id` = company.`id` JOIN `company_customers`  ON company_customers.`customer_id` = invoice_payments.`received_from`    "
				+ "AND invoice_payments.company_id = '"+companyID+"'   "+query2.toString() +"  AND invoice_payments.payment_date >'"+asOfDateString+"'  AND invoice.`invoice_date` <= '"+asOfDateString+"'  GROUP BY invoice.id  )   ORDER BY 1 DESC ";

		return qryString ;
	}
	
	/**
	 * helper method used in query generation
	 * @param list
	 * @param isQuoteApplied
	 * @return
	 */
	public static String getCommaSeparatedStringFromList(List<String> list,boolean isQuoteApplied){
		String result = "";
		try {
			if(list!=null && !list.isEmpty()){
				String item = null;
				for(int i=0;i<list.size();i++){
					item = list.get(i).toString();
					if(isQuoteApplied){
						result +="'"+item+"',";
					}else{
						result +=item+",";
					}
				}
				if(!StringUtils.isEmpty(result)){
					result = result.substring(0, result.length()-1);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return result;
	}
	

	private static void validateInvoiceEdit(Invoice dbInvoice, Invoice invoiceObj, String userID, String companyID){
		try {
			LOGGER.debug("entered validateInvoiceEdit(Invoice dbInvoice:"+dbInvoice+", Invoice invoiceObj:"+invoiceObj+", String userID:"+userID+", String companyID:"+companyID);
			if (dbInvoice == null || StringUtils.isBlank(dbInvoice.getId())) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.not.found"), 412);
			}
			if (StringUtils.isNotBlank(invoiceObj.getState()) && !dbInvoice.getState().equals(invoiceObj.getState())) {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.state"),
						Constants.INVALID_INPUT_STATUS);
			}
			if (invoiceObj == null || StringUtils.isAnyBlank(userID, companyID, invoiceObj.getId())) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			if (StringUtils.isNotBlank(invoiceObj.getState()) && !invoiceObj.getState().equals(dbInvoice.getState())) {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.state"), 412);
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				validatePaidInvoiceEdit(dbInvoice, invoiceObj);
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_SENT) || dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID) || dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				if(!dbInvoice.getCustomer_id().equals(invoiceObj.getCustomer_id())){
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.customer.update"), Constants.INVALID_INPUT_STATUS);
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.debug("error validateInvoiceEdit(Invoice dbInvoice:"+dbInvoice+", Invoice invoiceObj:"+invoiceObj+", String userID:"+userID+", String companyID:"+companyID, e);
			throw e;
		} catch (Exception e) {
			LOGGER.debug("error validateInvoiceEdit(Invoice dbInvoice:"+dbInvoice+", Invoice invoiceObj:"+invoiceObj+", String userID:"+userID+", String companyID:"+companyID, e);
		} finally {
			LOGGER.debug("exited validateInvoiceEdit(Invoice dbInvoice:"+dbInvoice+", Invoice invoiceObj:"+invoiceObj+", String userID:"+userID+", String companyID:"+companyID);
		}
	}
	private static void validatePaidInvoiceEdit(Invoice dbInvoice, Invoice invoice){
		try {
			LOGGER.debug("entered in validatePaidInvoiceEdit Invoice dbInvoice:"+dbInvoice+", Invoice invoice:"+invoice);
			if (StringUtils.isBlank(dbInvoice.getRemainder_name())
					&& StringUtils.isNotBlank(invoice.getRemainder_name())) {
				// remainder for paid invoice cannot be changed
				throw new WebApplicationException(PropertyManager.getProperty("invoice.cannot.create.remainder.for.paid"), Constants.INVALID_INPUT_STATUS);
			}
			// remainder for paid invoice cannot be changed
			if(dbInvoice.getSub_total()!=invoice.getSub_total()){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.subtotal"), Constants.INVALID_INPUT_STATUS);
			}
			if(dbInvoice.getAmount_due()!=invoice.getAmount_due()){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.due.amount"), Constants.INVALID_INPUT_STATUS);
			}
			if(dbInvoice.getDiscount()!=invoice.getDiscount()){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.discount"), Constants.INVALID_INPUT_STATUS);
			}
			if(!dbInvoice.getDiscount_id().equals(invoice.getDiscount_id())){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.discount.id"), Constants.INVALID_INPUT_STATUS);
			}
			if(dbInvoice.getLate_fee_amount()!=invoice.getLate_fee_amount()){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.latefee.amount"), Constants.INVALID_INPUT_STATUS);
			}
			if(!dbInvoice.getLate_fee_id().equals(invoice.getLate_fee_id())){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.latefee.id"), Constants.INVALID_INPUT_STATUS);
			}
			if(dbInvoice.getTax_amount()!=invoice.getTax_amount()){
				throw new WebApplicationException(PropertyManager.getProperty("error.update.invoice.tax.amount"), Constants.INVALID_INPUT_STATUS);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error in validatePaidInvoiceEdit Invoice dbInvoice:"+dbInvoice+", Invoice invoice:"+invoice,e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in validatePaidInvoiceEdit Invoice dbInvoice:"+dbInvoice+", Invoice invoice:"+invoice,e);
		} finally {
			LOGGER.debug("exited in validatePaidInvoiceEdit Invoice dbInvoice:"+dbInvoice+", Invoice invoice:"+invoice);
		}
	}

	public static void main(String[] args) {
		System.out.println(Utilities.getNumberAsCurrencyStr("USD", 123));
	}
}
