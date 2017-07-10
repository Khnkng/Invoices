package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
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
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			// recurring if invoice has plan id
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
				if (invoice.isSendMail()) {
					invoiceResult.setRecepientsMailsArr(invoice.getRecepientsMailsArr());
					if (sendInvoiceEmail(invoiceResult)) {
						invoice.setState("Email Sent");
					}
					if (StringUtils.isEmpty(invoice.getState())) {
						invoice.setState("Draft");
					}
				}
				if (!invoiceLineResult.isEmpty()) {
					connection.commit();
				}
				return InvoiceParser.convertTimeStampToString(invoiceObj);
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoice(String userID:" + userID + ",companyID:" + companyID + " Invoice invoice)" + invoice);
		}

	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		LOGGER.debug("entered updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		Connection connection = null;
		try {
			invoice.setId(invoiceID);
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID);
			if (invoiceObj == null || StringUtils.isAnyBlank(userID,companyID,invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			// recurring if invoice has plan id
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				InvoiceLine invoiceLine = new InvoiceLine();
				invoiceLine.setInvoice_id(invoiceID);
				InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
				if (deletedInvoiceLineResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
					if (invoiceLineResult != null) {
						connection.commit();
						return InvoiceParser.convertTimeStampToString(invoiceResult);
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	public static Invoice updateInvoiceState(String invoiceID, Invoice invoice) {
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
			connection.setAutoCommit(false);
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
			LOGGER.error(e);
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
		if (dbInvoice.getAmount() > invoice.getAmount()) {
			throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
		}
		if (dbInvoice.getAmount() == invoice.getAmount()) {
			invoice.setState("paid");
		}
		if (dbInvoice.getAmount() < invoice.getAmount()) {
			invoice.setState("partially paid");
		}
		Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().updateState(connection, invoice);
		if (invoiceResult != null) {
			return invoice;
		}
		return null;
	}

	public static Response getInvoices(String userID, String companyID, String state) {
		try {
			LOGGER.debug("entered get invoices userID:" + userID + " companyID:" + companyID + " state:" + state);
			if (StringUtils.isAnyBlank(userID,companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			Map<String, String> badges = MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.createInvoiceLstResult(invoiceLst, badges);
			return Response.status(200).entity(result.toString()).build();
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
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
			return InvoiceParser.convertTimeStampToString(invoiceObj);
		} catch (Exception e) {
			LOGGER.error(e);
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
			return MySQLManager.getInvoiceDAOInstance().deleteLst(userID, companyID, commaSeparatedLst);
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
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
			emailJson.put("subject", PropertyManager.getProperty("invoice.subject"));
			emailJson.put("mailBodyContentType", PropertyManager.getProperty("mail.body.content.type"));
			String template = PropertyManager.getProperty("invocie.mail.template");
			String invoiceLinkUrl = PropertyManager.getProperty("invoice.payment.link")+invoice.getId();
			template = template.replace("${invoiceNumber}", StringUtils.isBlank(invoice.getNumber())?"":invoice.getNumber())
					.replace("${companyName}", StringUtils.isEmpty(invoice.getCompanyName())?"":invoice.getCompanyName())
					.replace("${currencySymbol}", StringUtils.isEmpty(Utilities.getCurrencyHtmlSymbol(invoice.getCurrency()))?"":Utilities.getCurrencyHtmlSymbol(invoice.getCurrency()))
					.replace("${amount}", StringUtils.isEmpty(invoice.getAmount()+"")?"":invoice.getAmount()+"")
					.replace("${currencyCode}", StringUtils.isEmpty(invoice.getCurrency())?"":invoice.getCurrency())
					.replace("${invoiceDate}", StringUtils.isEmpty(invoice.getDue_date())?"":invoice.getDue_date())
					.replace("${invoiceLinkUrl}", invoiceLinkUrl);
			emailJson.put("body", template);
			String hostName = PropertyManager.getProperty("half.service.docker.hostname");
			String portName = PropertyManager.getProperty("half.service.docker.port");
			String url = Utilities.getLtmUrl(hostName, portName);
			url = url+ "HalfService/emails";
//			String url = "https://dev-services.qount.io/HalfService/emails";
			Object result = HTTPClient.postObject(url,emailJson.toString());
			if(result!=null && result instanceof java.lang.String && result.equals("true")){
				return true;
			}
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			LOGGER.debug("exited sendInvoiceEmail  invoice: " + invoice);
		}
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		Currencies cur = new Currencies();
		cur.setCode("");
		
		Invoice invoice = new Invoice();
		JSONArray recepientsMailsArr = new JSONArray();
		recepientsMailsArr.put("mateen.khan@qount.io");
		invoice.setRecepientsMailsArr(recepientsMailsArr);
		System.out.println(sendInvoiceEmail(invoice));
	}
}
