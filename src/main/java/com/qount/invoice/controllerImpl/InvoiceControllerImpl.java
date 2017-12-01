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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Company2;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceCommission;
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
		LOGGER.debug("entered createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		Connection connection = null;
		try {
			if (invoice == null || StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR + ":userID and companyID are mandatory", Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			boolean invoiceExists = MySQLManager.getInvoiceDAOInstance().invoiceExists(connection, invoice.getNumber(), companyID);
			LOGGER.debug("invoiceExists:" + invoiceExists);
			if (invoiceExists) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.number.exists"), 412);
			}
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, true);
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(invoice.getCompany_id());
			invoicePreference = MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection, invoicePreference);
			if (invoicePreference != null && StringUtils.isNotBlank(invoicePreference.getDefaultTitle())) {
				invoice.setMailSubject(invoicePreference.getDefaultTitle());
			}
			String base64StringOfAttachment = null;
			if (invoice.getPdf_data() != null) {
				String url = PropertyManager.getProperty("report.pdf.url");
				LOGGER.debug("url::" + url);
				base64StringOfAttachment = HTTPClient.postAndGetBase64StringResult(url, new ObjectMapper().writeValueAsString(invoice.getPdf_data()));
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
					if (StringUtils.isBlank(invoice.getState()) || invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)
							|| invoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
						invoice.setState(Constants.INVOICE_STATE_SENT);
					}
				} else {
					throw new WebApplicationException("error sending email", Constants.EXPECTATION_FAILED);
				}
			} else {
				if (StringUtils.isBlank(invoice.getState()) || invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)) {
					invoice.setState(Constants.INVOICE_STATE_DRAFT);
				}
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.EXPECTATION_FAILED));
			}
			connection.setAutoCommit(false);
			// creating remainder
			// recurring if invoice has plan id
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
				if (!invoiceLineResult.isEmpty()) {
					// saving dimensions of journal lines
					createInvoiceHistory(invoice, userID, companyID, jobId, connection);
					createInvoiceCommissions(connection, invoice.getCommissions(), invoice.getUser_id(), companyID, invoice.getId(), invoice.getNumber(), invoice.getAmount(),
							invoice.getCurrency());
					connection.commit();
				}
				// journal should not be created for draft state invoice.
				if (invoice.isSendMail())
					CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
				return InvoiceParser.convertTimeStampToString(invoiceObj);
			}
			throw new WebApplicationException(Constants.FAILURE_STATUS_STR, Constants.EXPECTATION_FAILED);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		}

	}

	private static String getJobId(Connection conn, Invoice invoice) {
		try {
			LOGGER.debug("entered getJobId invoice:" + invoice);
			if (invoice == null || StringUtils.isBlank(invoice.getRemainder_name())) {
				return null;
			}
			String remainderServieUrl = Utilities.getLtmUrl(PropertyManager.getProperty("remainder.service.docker.hostname"),
					PropertyManager.getProperty("remainder.service.docker.port"));
			// remainderServieUrl =
			// "http://remainderservice-dev.be0c8795.svc.dockerapp.io:93/";
			// remainderServieUrl = "http://localhost:8080/";
			remainderServieUrl += "RemainderService/mail/schedule";
			LOGGER.debug("remainderServieUrl::" + remainderServieUrl);
			JSONObject remainderJsonObject = new JSONObject();
			if (invoice.getRemainder_name().equalsIgnoreCase(Constants.ON_DUE_DATE_THEN_WEEKLY_AFTERWARD)
					|| invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_UNTIL_PAID)) {
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else if (invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_START_TWO_WEEKS_BEFORE_DUE)) {
				String dueDateStr = invoice.getDue_date();
				Date dueDate = CommonUtils.getDate(dueDateStr, Constants.TIME_STATMP_TO_BILLS_FORMAT);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dueDate);
				cal.add(Calendar.DATE, 14);
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.remainder.name"), 412);
			}
			JSONObject custom_args = new JSONObject();
			custom_args.put("SERVER_INSTANCE_MODE", PropertyManager.getProperty("SERVER_INSTANCE_MODE"));
			custom_args.put("type", Constants.INVOICE);
			custom_args.put("id", invoice.getId());
			String from = Constants.QOUNT;
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
			String due_date = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			String invoiceLinkUrl = PropertyManager.getProperty("invoice.payment.link") + invoice.getId();
			String currency = StringUtils.isEmpty(invoice.getCurrency()) ? "" : Utilities.getCurrencySymbol(invoice.getCurrency());
			mail_body = mail_body.replace("{{invoice number}}", invoice.getNumber()).replace("{{amount}}", currency + amount_due).replace("{{dueDays}}", due_date)
					.replace("${invoiceLinkUrl}", invoiceLinkUrl).replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"));
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
		LOGGER.debug("entered updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		Connection connection = null;
		boolean isJERequired = false;
		try {
			// journal should not be created for draft state invoice.
			Invoice dbInvoice = getInvoice(invoiceID);
			if (dbInvoice == null || StringUtils.isBlank(dbInvoice.getId())) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.not.found"), 412);
			}
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, false);
			invoiceObj.setId(invoiceID);
			if (invoiceObj == null || StringUtils.isAnyBlank(userID, companyID, invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			if (StringUtils.isNotBlank(invoice.getState()) && !invoice.getState().equals(dbInvoice.getState())) {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.state"), 412);
			}
			// remainder for paid invoice
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				if (StringUtils.isBlank(dbInvoice.getRemainder_name()) && StringUtils.isNotBlank(invoice.getRemainder_name())) {
					throw new WebApplicationException(PropertyManager.getProperty("invoice.cannot.create.remainder.for.paid"), 412);
				}
			}
			if (StringUtils.isNotBlank(invoice.getState()) && !dbInvoice.getState().equals(invoice.getState())) {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.state"), Constants.INVALID_INPUT_STATUS);
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			String base64StringOfAttachment = null;
			if (invoice.getPdf_data() != null) {
				String url = PropertyManager.getProperty("report.pdf.url");
				LOGGER.debug("url::" + url);
				base64StringOfAttachment = HTTPClient.postAndGetBase64StringResult(url, new ObjectMapper().writeValueAsString(invoice.getPdf_data()));
				if (StringUtils.isNotBlank(base64StringOfAttachment)) {
					invoice.setAttachmentBase64(base64StringOfAttachment);
				}
			}
			boolean createNewRemainder = false;
			boolean deleteOldRemainder = false;
			if (StringUtils.isBlank(dbInvoice.getRemainder_name()) && StringUtils.isNotBlank(invoice.getRemainder_name())) {
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
						throw new WebApplicationException(PropertyManager.getProperty("error.deleting.invoice.job.id"), Constants.EXPECTATION_FAILED);
					}
				}
				jobId = getJobId(connection, invoice);
				if (StringUtils.isNotBlank(jobId) && !dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				}
				invoice.setRemainder_job_id(jobId);
			}
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					// if invoice is paid then sending email and returning
					// response
					if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
						createInvoiceHistory(invoice, userID, companyID, jobId, connection);
						return InvoiceParser.convertTimeStampToString(dbInvoice);
					}
					if (StringUtils.isBlank(invoice.getState()) || invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)
							|| invoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
						invoice.setState(Constants.INVOICE_STATE_SENT);
					}
				} else {
					throw new WebApplicationException("error sending email", Constants.EXPECTATION_FAILED);
				}
			} else {
				if (StringUtils.isBlank(invoice.getState()) || invoice.getState().equals(Constants.INVOICE_STATE_DRAFT)) {
					invoice.setState(Constants.INVOICE_STATE_DRAFT);
				}
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.error.msg"), 412);
			}
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
				if (invoice.getAmount() < dbInvoice.getAmount_paid()) {
					throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.less.than.paid.amount"), 412);
				}
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			boolean invoiceExists = MySQLManager.getInvoiceDAOInstance().invoiceExists(connection, invoice.getNumber(), companyID, invoiceID);
			LOGGER.debug("invoiceExists:" + invoiceExists);
			if (invoiceExists) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.number.exists"), 412);
			}
			if (invoice != null) {
				invoice.setId(invoiceID);
				if (invoice.isSendMail()) {
					isJERequired = true;
				} else if (!Constants.INVOICE_STATE_DRAFT.equalsIgnoreCase(dbInvoice.getState())) {
					isJERequired = !invoice.prepareJSParemeters().equals(dbInvoice.prepareJSParemeters());
				}
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.EXPECTATION_FAILED));
			}
			connection.setAutoCommit(false);
			String dbIinvoiceState = dbInvoice.getState();
			if (StringUtils.isNotBlank(dbIinvoiceState) && !dbIinvoiceState.equals(Constants.INVOICE_STATE_DRAFT)) {
				invoice.setState(dbIinvoiceState);
			}
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				InvoiceLine invoiceLine = new InvoiceLine();
				invoiceLine.setInvoice_id(invoiceID);
				InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
				if (deletedInvoiceLineResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
					if (invoiceLineResult != null) {
						// updating dimensions for an invoice
						new InvoiceDimension().update(connection, companyID, invoiceObj.getInvoiceLines());
						updateInvoiceCommissions(connection, invoice.getCommissions(), invoice.getUser_id(), companyID, invoice.getId(), invoice.getNumber(), invoice.getAmount(),
								invoice.getCurrency());
						connection.commit();
					}
					if (isJERequired) {
						CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
					}
					createInvoiceHistory(invoice, userID, companyID, jobId, connection);
					return InvoiceParser.convertTimeStampToString(invoiceResult);
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	public static InvoiceHistory createInvoiceHistory(Invoice invoice, String userID, String companyID, String jobId, Connection connection) {
		try {
			LOGGER.debug(
					"entered createInvoiceHistory(Invoice invoice:" + invoice + ",String userID:" + userID + ",String companyID:" + companyID + ",String jobId:" + jobId + ")");
			InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(invoice, UUID.randomUUID().toString(), userID, companyID);
			if (!invoice.isSendMail() && StringUtils.isNotBlank(jobId)) {
				invoice_history.setDescription(PropertyManager.getProperty("invoice.history.desc.no.mail.but.job"));
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			invoice_history.setAction_at(timestamp.toString());
			invoice_history.setEmail_to(toCommaSeparatedString(invoice.getRecepientsMails()));
			invoice_history.setAmount(invoice.getAmount());
			invoice_history.setCurrency(invoice.getCurrency());
			invoice_history.setAmount_by_date(invoice.getAmount_by_date());
			invoice_history.setAmount_due(invoice.getAmount_due());
			invoice_history.setAmount_paid(invoice.getAmount_paid());
			invoice_history.setSub_totoal(invoice.getSub_total());
			invoice_history.setTax_amount(invoice.getTax_amount());
			invoice_history.setAction_at_mills(new Date().getTime());
			return MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		} finally {
			LOGGER.debug("exited createInvoiceHistory(Invoice invoice:" + invoice + ",String userID:" + userID + ",String companyID:" + companyID + ",String jobId:" + jobId + ")");
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			invoice.setId(invoiceID);
			connection = DatabaseUtilities.getReadWriteConnection();
			Invoice dbInvoice = getInvoice(invoiceID);
			if (dbInvoice == null || StringUtils.isBlank(dbInvoice.getId())) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.not.found"), 412);
			}
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.EXPECTATION_FAILED));
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			switch (invoice.getState()) {
			case "sent":
				if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID) || dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.error.msg"), 412);
				}
				return markInvoiceAsSent(connection, invoice);
			case "paid":
				return markInvoiceAsPaid(connection, invoice, dbInvoice);
			default:
				break;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoiceState invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	private static Invoice markInvoiceAsSent(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered markInvoiceAsSent invoice:" + invoice);
		try {
			Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoice.getId());
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID) || dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)
					|| dbInvoice.getState().equals(Constants.INVOICE_STATE_SENT)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.sent.msg"), 412);
			}
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().updateState(connection, invoice);
			if (invoiceResult != null) {
				InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(invoice, UUID.randomUUID().toString(), invoice.getUser_id(), invoice.getCompany_id());
				MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
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

	private static Invoice markInvoiceAsPaid(Connection connection, Invoice invoice, Invoice dbInvoice) throws Exception {
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
			Payment payment = new Payment();
			payment.setCompanyId(invoice.getCompany_id());
			payment.setCurrencyCode(invoice.getCurrency());
			payment.setId(UUID.randomUUID().toString());
			payment.setPaymentAmount(new BigDecimal(invoice.getAmount()));
			payment.setPaymentDate(invoice.getPayment_date() == null ? DateUtils.getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT) : invoice.getPayment_date());

			payment.setReceivedFrom(invoice.getCustomer_id());
			payment.setReferenceNo(invoice.getReference_number());
			payment.setDepositedTo(invoice.getBank_account_id());
			payment.setType(invoice.getPayment_method());
			Timestamp invoice_date = InvoiceParser.convertStringToTimeStamp(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			PaymentLine line = new PaymentLine();
			line.setId(UUID.randomUUID().toString());
			line.setInvoiceId(invoice.getId());
			line.setAmount(new BigDecimal(invoice.getAmount()));
			List<PaymentLine> payments = new ArrayList<PaymentLine>();
			payments.add(line);
			payment.setPaymentLines(payments);
			invoice.setAmount_due(dbInvoice.getAmount() - (dbInvoice.getAmount_paid() + invoice.getAmount()));
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
				// creating commissions if any
				InvoiceCommission invoiceCommission = new InvoiceCommission();
				invoiceCommission.setInvoice_id(invoice.getId());
				if (MySQLManager.getPaymentDAOInstance().save(payment, connection, false) != null) {
					List<InvoiceCommission> dbInvoiceCommissions = MySQLManager.getInvoiceDAOInstance().getInvoiceCommissions(invoiceCommission);
					createInvoicePaidCommissions(connection, dbInvoiceCommissions, invoice.getUser_id(), dbInvoice.getCompany_id(), invoice.getId(), invoice.getNumber(), invoice.getAmount(),
							invoice.getCurrency());
					connection.commit();
					CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(), invoice.getUser_id(),
							invoice.getCompany_id());
					return true;
				}
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			Map<String, String> invoicePaymentIdMap = null;
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				invoicePaymentIdMap = MySQLManager.getInvoiceDAOInstance().getInvoicePaymentsIds(InvoiceParser.getInvoiceIds(invoiceLst));
			}
			// Map<String, String> badges =
			// MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			InvoiceParser.formatInvoices(invoiceLst, invoicePaymentIdMap);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice result = InvoiceParser.convertTimeStampToString(MySQLManager.getInvoiceDAOInstance().get(invoiceID));
			InvoiceCommission invoiceCommission = new InvoiceCommission();
			invoiceCommission.setInvoice_id(invoiceID);
			result.setCommissions(MySQLManager.getInvoiceDAOInstance().getInvoiceCommissions(invoiceCommission));
			Company2 company2 = CommonUtils.retrieveCompany(result.getUser_id(), result.getCompany_id());
			result.setCompany(company2);
			InvoiceParser.convertAmountToDecimal(result);
			LOGGER.debug("getInvoice result:" + result);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited getInvoice invocieId:" + invoiceID);
		}

	}

	public static InvoicePreference getInvoicePreference(String invoiceID) {
		Connection connection = null;
		try {
			LOGGER.debug("entered getInvoicePreference invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice result = InvoiceParser.convertTimeStampToString(MySQLManager.getInvoiceDAOInstance().get(invoiceID));
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(result.getCompany_id());
			connection = DatabaseUtilities.getReadConnection();
			invoicePreference = MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection, invoicePreference);
			LOGGER.debug("getInvoicePreference result:" + result);
			return invoicePreference;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited getInvoicePreference invocieId:" + invoiceID);
			DatabaseUtilities.closeConnection(connection);
		}

	}

	public static Invoice deleteInvoiceById(String userID, String companyID, String invoiceID) {
		Connection connection = null;
		try {
			LOGGER.debug("entered deleteInvoiceById userID: " + userID + " companyID: " + companyID + " invoiceID" + invoiceID);
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, companyID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice invoiceObj = MySQLManager.getInvoiceDAOInstance().delete(invoice);
			InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(invoice, UUID.randomUUID().toString(), userID, companyID);
			connection = DatabaseUtilities.getReadWriteConnection();
			MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
			CommonUtils.deleteJournal(userID, companyID, invoiceID + "@" + "invoice");
			Utilities.unschduleInvoiceJob(invoice.getRemainder_job_id());
			return InvoiceParser.convertTimeStampToString(invoiceObj);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited deleteInvoiceById userID: " + userID + " companyID: " + companyID + " invoiceID" + invoiceID);
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static boolean deleteInvoicesById(String userID, String companyID, List<String> ids) {
		Connection connection = null;
		try {
			LOGGER.debug("entered deleteInvoicesById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			if (StringUtils.isAnyBlank(userID, companyID) || ids == null || ids.isEmpty()) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			CommonUtils.deleteJournalsAsync(userID, companyID, ids);
			List<String> jobIds = MySQLManager.getInvoiceDAOInstance().getInvoiceJobsList(commaSeparatedLst);
			deleteInvoiceJobsAsync(jobIds);
			List<InvoiceHistory> invoice_historys = InvoiceParser.getInvoice_historys(ids, UUID.randomUUID().toString(), userID, companyID, false, Constants.INVOICE_STATE_DELETE);
			connection = DatabaseUtilities.getReadWriteConnection();
			MySQLManager.getInvoice_historyDAO().createList(connection, invoice_historys);
			return MySQLManager.getInvoiceDAOInstance().deleteLst(userID, companyID, commaSeparatedLst);
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			List<Invoice> invoices = MySQLManager.getInvoiceDAOInstance().getInvoices(commaSeparatedLst);
			if (invoices == null || invoices.size() == 0) {
				throw new WebApplicationException(PropertyManager.getProperty("invoices.not.found"), 412);
			}
			if (InvoiceParser.isPaidStateInvoicePresent(invoices)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.sent.error.msg"), 412);
			}
			boolean isSent = MySQLManager.getInvoiceDAOInstance().updateStateAsSent(userID, companyID, commaSeparatedLst);
			if (isSent) {
				connection = DatabaseUtilities.getReadWriteConnection();
				List<InvoiceHistory> invoice_historys = InvoiceParser.getInvoice_historys(ids, UUID.randomUUID().toString(), userID, companyID, true, Constants.INVOICE_STATE_SENT);
				connection = DatabaseUtilities.getReadWriteConnection();
				MySQLManager.getInvoice_historyDAO().createList(connection, invoice_historys);
				for (String invoiceID : ids) {
					CommonUtils.createJournalAsync(new JSONObject().put("source", "invoice").put("sourceID", invoiceID).toString(), userID, companyID);
				}
			}
			return isSent;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error("Error marking invoice as sent", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
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
			String dueDate = InvoiceParser.convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			String currency = StringUtils.isEmpty(invoice.getCurrency()) ? "" : Utilities.getCurrencySymbol(invoice.getCurrency());
			String amount = getTwoDecimalNumberAsString(invoice.getAmount_due());
			template = template.replace("{{invoice number}}", StringUtils.isBlank(invoice.getNumber()) ? "" : invoice.getNumber())
					.replace("{{company name}}", StringUtils.isEmpty(invoice.getCompanyName()) ? "" : invoice.getCompanyName()).replace("{{amount}}", currency + amount)
					.replace("{{due date}}", StringUtils.isEmpty(dueDate) ? "" : dueDate).replace("${invoiceLinkUrl}", invoiceLinkUrl)
					.replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"));
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url + "HalfService/mails";
			// url = "https://dev-services.qount.io/HalfService/mails";
			JSONObject emailJson = getMailJson(invoice, template, PropertyManager.getProperty("mail.body.content.type"));
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
				throw new WebApplicationException(PropertyManager.getProperty("mail.recipients.email.empty.error.msg"), 412);
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
			invoice.setFrom(from);
			fromObj.put("name", from);
			result.put("from", fromObj);
			JSONArray contentArr = new JSONArray();
			result.put("content", contentArr);
			JSONObject contentObj = new JSONObject();
			contentArr.put(contentObj);
			if (StringUtils.isBlank(mail_body)) {
				throw new WebApplicationException(PropertyManager.getProperty("mail.invalid.input.empty.mail_body"), 412);
			}
			contentObj.put("value", mail_body);
			if (StringUtils.isBlank(mailBodyContentType)) {
				throw new WebApplicationException(PropertyManager.getProperty("mail.invalid.input.empty.mail_body_content_type"), 412);
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceListByClientId(userID, companyID, clientID);
			InvoiceParser.convertAmountToDecimal(invoiceLst);
			return invoiceLst;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID + " clientID:" + clientID);
		}
	}

	public static Response getCount(String userID, String companyID) {
		try {
			LOGGER.debug("entered get count userID:" + userID + " companyID:" + companyID);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Map<String, String> badges = MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.formatBadges(badges);
			return Response.status(200).entity(result.toString()).build();
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get count userID:" + userID + " companyID:" + companyID);
		}
	}

	public static Response getInvoiceMetrics(String userID, String companyID) {
		try {
			LOGGER.debug("entered get box values userID:" + userID + " companyID:" + companyID);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			InvoiceDAO invoiceDAO = InvoiceDAOImpl.getInvoiceDAOImpl();
			InvoiceMetrics invoiceMetrics = invoiceDAO.getInvoiceMetrics(companyID);
			if (invoiceMetrics != null) {
				return Response.status(200).entity(invoiceMetrics).build();
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			if (e.getResponse().getStatus() == 412) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.PRECONDITION_FAILED));
			} else {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited get box values userID:" + userID + " companyID:" + companyID);
		}
	}

	public static InvoiceCommission createInvoiceCommission(Connection connection, InvoiceCommission invoiceCommission) {
		try {
			LOGGER.debug("entered createInvoiceCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
			if (invoiceCommission.isCreateBill()) {
				boolean isInvoiceCommissionCreated = InvoiceParser.createInvoiceCommisionBill(invoiceCommission);
				if (!isInvoiceCommissionCreated) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
				}
			}
			InvoiceCommission result = MySQLManager.getInvoiceDAOInstance().createInvoiceCommission(connection, invoiceCommission);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error("error creating createInvoiceCommission", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error creating createInvoiceCommission", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited createInvoiceCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
		}
	}
	
	public static InvoiceCommission createInvoicePaidCommission(Connection connection, InvoiceCommission invoiceCommission) {
		try {
			LOGGER.debug("entered createInvoicePaidCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
			if (invoiceCommission.isCreateBill()) {
				boolean isInvoiceCommissionCreated = InvoiceParser.createInvoiceCommisionBill(invoiceCommission);
				if (!isInvoiceCommissionCreated) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
				}
			}
			InvoiceCommission result = MySQLManager.getInvoiceDAOInstance().updateInvoiceCommissionBillState(connection, invoiceCommission);
			return result;
		} catch (WebApplicationException e) {
			LOGGER.error("error createInvoicePaidCommission", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error createInvoicePaidCommission", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited createInvoicePaidCommission(InvoiceCommission invoiceCommission:" + invoiceCommission);
		}
	}

	public static InvoiceCommission createPaidInvoiceCommissions(Connection connection, InvoiceCommission invoiceCommission) {
		try {
			LOGGER.debug("entered createPaidInvoiceCommissions(InvoiceCommission invoiceCommission:" + invoiceCommission);
			if (invoiceCommission.isCreateBill()) {
				boolean isInvoiceCommissionCreated = InvoiceParser.createInvoiceCommisionBill(invoiceCommission);
				if (!isInvoiceCommissionCreated) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
				} else {
					return MySQLManager.getInvoiceDAOInstance().updateInvoiceCommissionBillState(connection, invoiceCommission);
				}
			}
			return null;
		} catch (WebApplicationException e) {
			LOGGER.error("error in createPaidInvoiceCommissions", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in createPaidInvoiceCommissions", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited createPaidInvoiceCommissions(InvoiceCommission invoiceCommission:" + invoiceCommission);
		}
	}

	private static boolean createInvoiceCommissions(Connection connection, List<InvoiceCommission> commissions, String userId, String companyId, String invoiceId,
			String invoiceNumber, double invoiceAmount, String currency) {
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
							throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.empty.eventAt"), Constants.INVALID_INPUT);
						}
						if (!eventAt.equals(Constants.PAID)) {
							commission.setCreateBill(true);
						}
						commission.setUser_id(userId);
						commission.setCompany_id(companyId);
						commission.setInvoice_id(invoiceId);
						commission.setCurrency(currency);
						commission.setInvoice_amount(invoiceAmount);
						commission.setInvoice_number((invoiceNumberCounter++)+"_"+invoiceNumber);
						if (createInvoiceCommission(connection, commission) == null) {
							throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
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
	
	public static boolean createInvoicePaidCommissions(Connection connection, List<InvoiceCommission> commissions, String userId, String companyId, String invoiceId,
			String invoiceNumber, double invoiceAmount, String currency) {
		try {
			LOGGER.debug("entered createInvoiceCommissions invoiceCommisions:" + commissions);
			if (commissions != null && !commissions.isEmpty()) {
				Iterator<InvoiceCommission> commissionsItr = commissions.iterator();
				while (commissionsItr.hasNext()) {
					InvoiceCommission commission = commissionsItr.next();
					if (commission != null) {
						String eventAt = commission.getEvent_at();
						if (StringUtils.isBlank(eventAt)) {
							throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.empty.eventAt"), Constants.INVALID_INPUT);
						}
						if (eventAt.equals(Constants.PAID) && !commission.isBillCreated()) {
							commission.setCreateBill(true);
						}
						commission.setUser_id(userId);
						commission.setCompany_id(companyId);
						commission.setInvoice_id(invoiceId);
						commission.setCurrency(currency);
						commission.setInvoice_amount(invoiceAmount);
						commission.setInvoice_number(invoiceNumber);
						if (createInvoicePaidCommission(connection, commission) == null) {
							throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
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

	private static boolean updateInvoiceCommissions(Connection connection, List<InvoiceCommission> commissions, String userId, String companyId, String invoiceId,
			String invoiceNumber, double invoiceAmount, String currency) {
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
								throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.empty.eventAt"), Constants.INVALID_INPUT);
							}
							if (!eventAt.equals(Constants.PAID)) {
								commission.setCreateBill(true);
							}
							commission.setUser_id(userId);
							commission.setCompany_id(companyId);
							commission.setInvoice_id(invoiceId);
							commission.setCurrency(currency);
							commission.setInvoice_amount(invoiceAmount);
							commission.setInvoice_number((invoiceNumberCounter++)+"_"+invoiceNumber);
							if (StringUtils.isNotBlank(commission.getBill_id())) {
								if (StringUtils.isBlank(commission.getId())) {
									// bill id and commission id are same
									commission.setId(commission.getBill_id());
								}
								MySQLManager.getInvoiceDAOInstance().deleteInvoiceCommission(connection, commission);
								InvoiceParser.deleteInvoivceCommissionBill(commission);
							}
							if(!commission.isDelete()){
								if (createInvoiceCommission(connection, commission) == null) {
									throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.creation"), Constants.EXPECTATION_FAILED);
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
}
