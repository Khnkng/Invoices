package com.qount.invoice.controllerImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoiceMetrics;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR + ":userID and companyID are mandatory", Status.PRECONDITION_FAILED));
			}
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, true);
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				} else {
					throw new WebApplicationException("error sending email");
				}
			} else {
				invoice.setState(Constants.INVOICE_STATE_DRAFT);
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			// recurring if invoice has plan id
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
				if (!invoiceLineResult.isEmpty()) {
					connection.commit();
				}
				// journal should not be created for draft state invoice.
				if(invoice.isSendMail())
				CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
				return InvoiceParser.convertTimeStampToString(invoiceObj);
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		}

	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		LOGGER.debug("entered updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		Connection connection = null;
		boolean isJERequired = false;
		try {
//			journal should not be created for draft state invoice.
			if (invoice != null && invoice.isSendMail()) {
				invoice.setId(invoiceID);
				Invoice dbInvoice = getInvoice(invoiceID);
				if(Constants.INVOICE_STATE_DRAFT.equalsIgnoreCase(dbInvoice.getState()) && invoice.isSendMail()){
					isJERequired = true;
				} else {
					isJERequired = !invoice.prepareJSParemeters().equals(dbInvoice.prepareJSParemeters());
				}
			}
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID, false);
			if (invoiceObj == null || StringUtils.isAnyBlank(userID, companyID, invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			if (invoice.isSendMail()) {
				if (sendInvoiceEmail(invoiceObj)) {
					invoice.setState(Constants.INVOICE_STATE_SENT);
				} else {
					throw new WebApplicationException("error sending email");
				}
			} else {
				invoice.setState(Constants.INVOICE_STATE_DRAFT);
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				InvoiceLine invoiceLine = new InvoiceLine();
				invoiceLine.setInvoice_id(invoiceID);
				InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
				if (deletedInvoiceLineResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
					if (invoiceLineResult != null) {
						connection.commit();
						if (isJERequired) {
							CommonUtils.createJournal(new JSONObject().put("source", "invoice").put("sourceID", invoice.getId()).toString(), userID, companyID);
						}
						return InvoiceParser.convertTimeStampToString(invoiceResult);
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			invoice.setUser_id(userID);
			invoice.setCompany_id(companyID);
			switch (invoice.getState()) {
			case "sent":
				return markInvoiceAsSent(connection, invoice);
			case "paid":
				return markInvoiceAsPaid(connection, invoice);
			default:
				break;
			}

			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoiceState invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	private static Invoice markInvoiceAsSent(Connection connection, Invoice invoice) throws Exception {
		Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().updateState(connection, invoice);
		if (invoiceResult != null) {
			return invoice;
		}
		return null;
	}

	private static Invoice markInvoiceAsPaid(Connection connection, Invoice invoice) throws Exception {
		Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoice.getId());
		if (invoice.getAmount() > dbInvoice.getAmount()) {
			throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
		}
		if (dbInvoice.getAmount() == invoice.getAmount()) {
			invoice.setState(Constants.INVOICE_STATE_PAID);
			if (markAsPaid(connection, invoice)) {
				return invoice;
			}
		}
		if (invoice.getAmount() < dbInvoice.getAmount()) {
			invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);
			return MySQLManager.getInvoiceDAOInstance().updateState(connection, invoice);
		}
		return null;
	}

	private static boolean markAsPaid(Connection connection, Invoice invoice) throws Exception {
		try {
			connection.setAutoCommit(false);
			Payment payment = new Payment();
			payment.setCompanyId(invoice.getCompany_id());
			payment.setCurrencyCode(invoice.getCurrency());
			payment.setId(UUID.randomUUID().toString());
			payment.setPaymentAmount(new BigDecimal(invoice.getAmount()));
			payment.setPaymentDate(DateUtils.getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT));
			payment.setReceivedFrom(invoice.getCustomer_id());
			payment.setReferenceNo(invoice.getRefrence_number());
			payment.setType(invoice.getPayment_method());
			PaymentLine line = new PaymentLine();
			line.setId(UUID.randomUUID().toString());
			line.setInvoiceId(invoice.getId());
			line.setAmount(new BigDecimal(invoice.getAmount()));
			List<PaymentLine> payments = new ArrayList<PaymentLine>();
			payments.add(line);
			payment.setPaymentLines(payments);
			if (MySQLManager.getPaymentDAOInstance().save(payment, connection) != null) {
				if (MySQLManager.getInvoiceDAOInstance().updateInvoiceAsPaid(connection, invoice) != null) {
					connection.commit();
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
		return false;
	}

	public static Response getInvoices(String userID, String companyID, String state) {
		try {
			LOGGER.debug("entered get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			// Map<String, String> badges =
			// MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.createInvoiceLstResult(invoiceLst, null);
			return Response.status(200).entity(result.toString()).type(MediaType.APPLICATION_JSON).build();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
		}
	}

	public static Invoice getInvoice(String invoiceID) {
		try {
			LOGGER.debug("entered getInvoice invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Invoice result = InvoiceParser.convertTimeStampToString(MySQLManager.getInvoiceDAOInstance().get(invoiceID));
			LOGGER.debug("getInvoice result:" + result);
			return result;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			CommonUtils.deleteJournal(userID, companyID, invoiceID+ "@" + "invoice");
			return InvoiceParser.convertTimeStampToString(invoiceObj);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			return MySQLManager.getInvoiceDAOInstance().updateStateAsSent(userID, companyID, commaSeparatedLst);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			template = template.replace("{{invoice number}}", StringUtils.isBlank(invoice.getNumber()) ? "" : invoice.getNumber()).replace("{{company name}}", StringUtils.isEmpty(invoice.getCompanyName()) ? "" : invoice.getCompanyName())
					.replace("{{amount}}", currency + (StringUtils.isEmpty(invoice.getAmount() + "") ? "" : invoice.getAmount() + "")).replace("{{due date}}", StringUtils.isEmpty(dueDate) ? "" : dueDate).replace("${invoiceLinkUrl}", invoiceLinkUrl)
					.replace("${qountLinkUrl}", PropertyManager.getProperty("qount.url"));
			emailJson.put("body", template);
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url + "HalfService/emails";
//			 String url = "https://dev-services.qount.io/HalfService/emails";
			Object result = HTTPClient.postObject(url, emailJson.toString());
			if (result != null && result instanceof java.lang.String && result.equals("true")) {
				return true;
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited sendInvoiceEmail  invoice: " + invoice);
		}
		return false;
	}

	public static List<Invoice> getInvoicesByClientID(String userID, String companyID, String clientID) {
		try {
			if (StringUtils.isAnyBlank(userID, companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceListByClientId(userID, companyID, clientID);
			return invoiceLst;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			JSONObject result = InvoiceParser.createInvoiceLstResult(null, badges);
			return Response.status(200).entity(result.toString()).build();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get box values userID:" + userID + " companyID:" + companyID);
		}
	}

	public static void main(String[] args) throws Exception {
		String invoiceString = "{\"amount\":40.8,\"amount_by_date\":40.8,\"amount_due\":40.8,\"amount_paid\":0.0,\"company\":{\"active\":true,\"contact_first_name\":\"Uday\",\"contact_last_name\":\"K\",\"currency\":\"USD\",\"ein\":\"qdwm3Zlnsn8vtmxSPoVvzg==\",\"email\":\"\",\"id\":\"fa0b8c60-4347-4fb1-9982-4ebba798b108\",\"name\":\"Advanced Pain Solutions\"},\"company_id\":\"fa0b8c60-4347-4fb1-9982-4ebba798b108\",\"created_at\":\"2017-07-17 15:35:02\",\"currencies\":{\"code\":\"USD\",\"html_symbol\":\"$\",\"name\":\"US Dollar\"},\"currency\":\"USD\",\"customer\":{\"coa\":\"bcb6afab-1bb0-11e7-88d4-12a272e624d5\",\"customer_address\":\"123,Main Street\",\"customer_city\":\"New york \",\"customer_country\":\"United States\",\"customer_ein\":\"OOaoDu/uUgLx80OJmuXKTQ==\",\"customer_id\":\"1f638eac-e60f-468c-be2a-19389108b493\",\"customer_name\":\"ABC Inc\",\"customer_state\":\"New Jersey \",\"customer_zipcode\":\"07001\",\"phone_number\":\"9908990825\",\"street_1\":\"123,Main Street\",\"term\":\"net30\"},\"customerContactDetails\":{\"customer_id\":\"1f638eac-e60f-468c-be2a-19389108b493\",\"email\":\"seshu.vellanki@qount.io\",\"first_name\":\"Seshu\",\"id\":\"c1a15184-dee2-4c10-93ec-71fcd392069d\",\"last_name\":\"Vellanki\",\"mobile\":\"9908990856\"},\"customer_id\":\"1f638eac-e60f-468c-be2a-19389108b493\",\"deposit_amount\":0.0,\"discount\":0.0,\"due_date\":\"08/16/17\",\"id\":\"97db4048-afeb-4642-9862-5e12887538b9\",\"invoiceLines\":[{\"amount\":40.0,\"coa\":{\"id\":\"3cb637ba-52f0-4220-b9ac-292657fbb79e\"},\"description\":\"3.5mm jack 1 meter wire\",\"id\":\"41639232-6fbb-4083-93e5-fc100256218d\",\"invoice_id\":\"97db4048-afeb-4642-9862-5e12887538b9\",\"item\":{\"id\":\"3cb637ba-52f0-4220-b9ac-292657fbb79e\",\"name\":\"head phone\"},\"item_id\":\"3cb637ba-52f0-4220-b9ac-292657fbb79e\",\"last_updated_at\":\"2017-07-17 15:35:02\",\"last_updated_by\":\"uday.koorella@qount.io\",\"price\":20.0,\"quantity\":2.0,\"tax_id\":\"a6d35695-cd71-45b4-9567-bf2744772e63\",\"type\":\"item\"}],\"invoice_date\":\"07/17/17\",\"last_updated_at\":\"2017-07-17 15:35:02\",\"last_updated_by\":\"uday.koorella@qount.io\",\"notes\":\"\",\"number\":\"VO7302\",\"payment_options\":\"\",\"processing_fees\":0.0,\"recepientsMails\":[\"seshu.vellanki@qount.io\",\"sriuday@gmail.com\"],\"sendMail\":false,\"send_to\":\"c1a15184-dee2-4c10-93ec-71fcd392069d\",\"state\":\"sent\",\"sub_totoal\":0.0,\"tax_amount\":0.0,\"term\":\"net30\",\"user_id\":\"uday.koorella@qount.io\"}";
		Invoice invoice = new ObjectMapper().readValue(invoiceString, Invoice.class);
		Invoice newInvoice = new ObjectMapper().readValue(invoiceString, Invoice.class);
		newInvoice.getInvoiceLines().get(0).setPrice(21);
		System.out.println(invoice.prepareJSParemeters().equals(newInvoice.prepareJSParemeters()));
	}
}
