package com.qount.invoice.controllerImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceMetrics;
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
			// boolean isCompanyRegistered =
			// MySQLManager.getCompanyDAOInstance().isCompanyRegisteredWithPaymentSpring(connection,
			// companyID);
			// if (!isCompanyRegistered) {
			// throw new
			// WebApplicationException(PropertyManager.getProperty("paymentspring.company.not.registered"));
			// }
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, true);
			String jobId = null;
			if (StringUtils.isNotBlank(invoice.getRemainder_name())) {
				jobId = getJobId(invoice);
				invoice.setRemainder_job_id(jobId);
			}
			if (invoice.isSendMail() && StringUtils.isEmpty(invoice.getRemainder_job_id())) {
				if (sendInvoiceEmail(invoiceObj)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				} else {
					throw new WebApplicationException("error sending email",Constants.EXPECTATION_FAILED);
				}
			} else {
				invoice.setState(Constants.INVOICE_STATE_DRAFT);
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
					new InvoiceDimension().create(connection, companyID, invoiceObj.getInvoiceLines());
					connection.commit();
				}
				// journal should not be created for draft state invoice.
				if (invoice.isSendMail())
					CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
				return InvoiceParser.convertTimeStampToString(invoiceObj);
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
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		}

	}

	private static String getJobId(Invoice invoice) {
		try {
			if (invoice == null || StringUtils.isBlank(invoice.getRemainder_name())) {
				return null;
			}
			String remainderServieUrl = Utilities.getLtmUrl("remainder.service.docker.hostname", "remainder.service.docker.port");
			remainderServieUrl = "http://remainderservice-dev.be0c8795.svc.dockerapp.io:93/";
			// remainderServieUrl = "http://localhost:8080/";
			remainderServieUrl += "RemainderService/mail/schedule";
			JSONObject remainderJsonObject = new JSONObject();
			if (invoice.getRemainder_name().equalsIgnoreCase(Constants.ON_DUE_DATE_THEN_WEEKLY_AFTERWARD)
					|| invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_UNTIL_PAID)) {
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else if (invoice.getRemainder_name().equalsIgnoreCase(Constants.WEEKLY_START_TWO_WEEKS_BEFORE_DUE)) {
				String dueDateStr = invoice.getDue_date();
				Date dueDate = CommonUtils.getDate(dueDateStr, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				Calendar cal = Calendar.getInstance();
				cal.setTime(dueDate);
				cal.add(Calendar.DATE, 14);
				String startDate = CommonUtils.convertDate(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				remainderJsonObject.put("startDate", startDate);
			} else {
				throw new WebApplicationException(PropertyManager.getProperty("invalid.invoice.remainder.name"), 412);
			}
			remainderJsonObject.put("emails", invoice.getRecepientsMails());
			remainderJsonObject.put("type", "invoice");
			remainderJsonObject.put("number", invoice.getNumber());
			remainderJsonObject.put("amount", invoice.getAmount());
			// remainderJsonObject.put("startDate",invoice.getRemainder().getDate());
			Object jobIdObj = HTTPClient.postObject(remainderServieUrl, remainderJsonObject.toString());
			return jobIdObj.toString();
		} catch (WebApplicationException e) {
			LOGGER.error("error creating job id", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error creating job id", e);
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
			if (dbInvoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.paid.edit.error.msg"), 412);
			}
			if(dbInvoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)){
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
			if (invoice != null && invoice.isSendMail()) {
				invoice.setId(invoiceID);
				if (Constants.INVOICE_STATE_DRAFT.equalsIgnoreCase(dbInvoice.getState()) && invoice.isSendMail()) {
					isJERequired = true;
				} else {
					isJERequired = !invoice.prepareJSParemeters().equals(dbInvoice.prepareJSParemeters());
				}
			}
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, false);
			invoiceObj.setId(invoiceID);
			if (invoiceObj == null || StringUtils.isAnyBlank(userID, companyID, invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				} else {
					throw new WebApplicationException("error sending email",Constants.EXPECTATION_FAILED);
				}
			} else {
				invoice.setState(Constants.INVOICE_STATE_DRAFT);
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
						connection.commit();
						if (isJERequired) {
							CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
						}
						return InvoiceParser.convertTimeStampToString(invoiceResult);
					}
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
			if (MySQLManager.getPaymentDAOInstance().save(payment, connection, false) != null) {
				connection.commit();
				CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(), invoice.getUser_id(),
						invoice.getCompany_id());
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			// Map<String, String> badges =
			// MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			InvoiceParser.formatInvoices(invoiceLst);
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

	public static Invoice deleteInvoiceById(String userID, String companyID, String invoiceID) {
		try {
			LOGGER.debug("entered deleteInvoiceById userID: " + userID + " companyID: " + companyID + " invoiceID" + invoiceID);
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, companyID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice invoiceObj = MySQLManager.getInvoiceDAOInstance().delete(invoice);
			CommonUtils.deleteJournal(userID, companyID, invoiceID + "@" + "invoice");
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
		}
	}

	public static boolean deleteInvoicesById(String userID, String companyID, List<String> ids) {
		try {
			LOGGER.debug("entered deleteInvoicesById userID: " + userID + " companyID:" + companyID + " ids:" + ids);
			if (StringUtils.isAnyBlank(userID, companyID) || ids == null || ids.isEmpty()) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			String commaSeparatedLst = CommonUtils.toQoutedCommaSeparatedString(ids);
			CommonUtils.deleteJournalsAsync(userID, companyID, ids);
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
		}
	}

	public static boolean updateInvoicesAsSent(String userID, String companyID, List<String> ids) {
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
		}
	}

	private static boolean sendInvoiceEmail(Invoice invoice) throws Exception {
		try {
			LOGGER.debug("entered sendInvoiceEmail invoice: " + invoice);
			JSONObject emailJson = new JSONObject();
			emailJson.put("recipients", invoice.getRecepientsMailsArr());
			String subject = PropertyManager.getProperty("invoice.subject");
			subject += invoice.getCompanyName();
			emailJson.put("subject", subject);
			emailJson.put("mailBodyContentType", PropertyManager.getProperty("mail.body.content.type"));
			String template = PropertyManager.getProperty("invocie.mail.template");
			String invoiceLinkUrl = PropertyManager.getProperty("invoice.payment.link") + invoice.getId();
			String dueDate = InvoiceParser.convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			String currency = StringUtils.isEmpty(invoice.getCurrency()) ? "" : Utilities.getCurrencySymbol(invoice.getCurrency());
			String amount = getTwoDecimalNumberAsString(invoice.getAmount());
			template = template.replace("{{invoice number}}", StringUtils.isBlank(invoice.getNumber()) ? "" : invoice.getNumber())
					.replace("{{company name}}", StringUtils.isEmpty(invoice.getCompanyName()) ? "" : invoice.getCompanyName()).replace("{{amount}}", currency + amount)
					.replace("{{due date}}", StringUtils.isEmpty(dueDate) ? "" : dueDate).replace("${invoiceLinkUrl}", invoiceLinkUrl)
					.replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"));
			emailJson.put("body", template);
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url + "HalfService/emails";
			// url = "https://dev-services.qount.io/HalfService/emails";
			Object result = HTTPClient.postObject(url, emailJson.toString());
			if (result != null && result instanceof java.lang.String && result.equals("true")) {
				return true;
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

	public static void main(String[] args) {
		System.out.println(getTwoDecimalNumberAsString(21.1222));
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

}
