package com.qount.invoice.controllerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
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
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.model.InvoicePayment;
import com.qount.invoice.model.InvoiceTaxes;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.LTMUtils;
import com.qount.invoice.utils.ResponseUtil;

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
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice, companyID);
			if (invoiceObj == null || StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoiceObj.setCompany_id(companyID);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().save(connection, invoice);
			if (invoiceResult != null) {
				List<InvoiceTaxes> incoiceTaxesList = invoiceObj.getInvoiceTaxes();
				if (incoiceTaxesList == null) {
					incoiceTaxesList = new ArrayList<InvoiceTaxes>();
				}
				List<InvoiceTaxes> invoiceTaxResult = MySQLManager.getInvoiceTaxesDAOInstance().save(connection, invoiceObj.getId(), incoiceTaxesList);
				if (invoiceTaxResult != null) {
					List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
					if (!invoiceLineResult.isEmpty()) {
						List<InvoiceLineTaxes> invoiceLineTaxesList = InvoiceParser.getInvoiceLineTaxesList(invoiceObj.getInvoiceLines());
						List<InvoiceLineTaxes> invoiceLineTaxesResult = MySQLManager.getInvoiceLineTaxesDAOInstance().save(connection, invoiceLineTaxesList);
						if (invoiceLineTaxesResult != null) {
							if(InvoiceParser.sendInvoiceEmail(companyID, invoiceResult, invoice.getId())){
								connection.commit();
								return InvoiceParser.convertTimeStampToString(invoiceObj);
							}
						}
					}
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (WebApplicationException e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			if (invoiceObj == null || StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID) || StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoiceObj.setCompany_id(companyID);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			String actionType = invoiceObj.getActionType();
			boolean isPaymentDone = false;
			// check currency
			if (!StringUtils.isEmpty(actionType) && actionType.equals("payment")) {
				isPaymentDone = makeInvoicePayment(invoiceObj, companyID, invoiceID, connection);
			}
			boolean isInvoiceUpdated = false;
			Invoice invoiceResult = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj);
			if (invoiceResult != null) {
				List<InvoiceTaxes> invoiceTaxesList = invoiceObj.getInvoiceTaxes();
				InvoiceTaxes invoiceTax = new InvoiceTaxes();
				invoiceTax.setInvoice_id(invoiceID);
				InvoiceTaxes deletedInvoiceTaxResult = MySQLManager.getInvoiceTaxesDAOInstance().deleteByInvoiceId(connection, invoiceTax);
				if (deletedInvoiceTaxResult != null) {
					if (invoiceTaxesList == null) {
						invoiceTaxesList = new ArrayList<>();
					}
					List<InvoiceTaxes> invoiceTaxesResult = MySQLManager.getInvoiceTaxesDAOInstance().save(connection, invoiceID, invoiceTaxesList);
					if (invoiceTaxesResult != null) {
						InvoiceLine invoiceLine = new InvoiceLine();
						invoiceLine.setInvoice_id(invoiceID);
						InvoiceLine deletedInvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().deleteByInvoiceId(connection, invoiceLine);
						if (deletedInvoiceLineResult != null) {
							List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceObj.getInvoiceLines());
							if (invoiceLineResult != null) {
								List<InvoiceLineTaxes> invoiceLineTaxesList = InvoiceParser.getInvoiceLineTaxesList(invoiceObj.getInvoiceLines());
								List<InvoiceLineTaxes> invoiceLineTaxesResult = MySQLManager.getInvoiceLineTaxesDAOInstance().save(connection, invoiceLineTaxesList);
								if (invoiceLineTaxesResult != null) {
									connection.commit();
									isInvoiceUpdated = true;
									return invoiceResult;
								}
							}
						}
					}

				}
			}
			if (isPaymentDone && !!isInvoiceUpdated) {
				// revert payment spring transaction
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoice userid:" + userID + " companyID:" + companyID + " invoiceID:" + invoiceID + ": invoice" + invoice);
		}
	}

	public static Response getInvoices(String userID, String companyID, String state) {
		try {
			LOGGER.debug("entered get invoices userID:" + userID + " companyID:" + companyID+" state:"+state);
			if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			List<Invoice> invoiceLst = MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID, companyID, state);
			Map<String, String> badges = MySQLManager.getInvoiceDAOInstance().getCount(userID, companyID);
			JSONObject result = InvoiceParser.createInvoiceLstResult(invoiceLst, badges);
			return Response.status(200).entity(result.toString()).build();
			// return
			// MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID,
			// companyID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get invoices userID:" + userID + " companyID:" + companyID+" state:"+state);
		}
	}

	public static Invoice getInvoice(String invoiceID) {
		try {
			LOGGER.debug("entered getInvoice invocieId:" + invoiceID);
			if (StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			Invoice result = MySQLManager.getInvoiceDAOInstance().get(invoiceID);
			if (result != null) {
				InvoiceTaxes invoiceTax = new InvoiceTaxes();
				invoiceTax.setInvoice_id(invoiceID);
				List<InvoiceTaxes> invoiceTaxesList = MySQLManager.getInvoiceTaxesDAOInstance().getByInvoiceID(invoiceTax);
				result.setInvoiceTaxes(invoiceTaxesList);
			}
			return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited getInvoice invocieId:" + invoiceID);
		}

	}

	public static Invoice deleteInvoiceById(String userID, String invoiceID) {
		try {
			LOGGER.debug("entered deleteInvoiceById userID: " + userID + " invoiceID" + invoiceID);
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().delete(invoice);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited deleteInvoiceById userID: " + userID + " invoiceID" + invoiceID);
		}
	}

	private static JSONObject invokeChargePaymentSpringApi(String companyId, JSONObject payloadObj, String urlAction) {
		try {
			LOGGER.debug("entered invokeChargePaymentSpringApi companyId:" + companyId);
			if (StringUtils.isEmpty(companyId) || payloadObj == null || payloadObj.length() == 0) {
				throw new WebApplicationException("company id and payload object cannot be empty companyID:" + companyId + " payloadObj: " + payloadObj);
			}
			String path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port", "payment.spring.base.url");
			if (StringUtils.isEmpty(path)) {
				throw new WebApplicationException("unable to connect to qount payment server");
			}
			path = path + "PaymentSpring/companies/" + companyId + "/" + urlAction;
			path = path.replace("{comapnyID}", companyId);
			JSONObject responseJson = HTTPClient.post(path, payloadObj.toString());
			if (responseJson != null && responseJson.length() != 0) {
				return responseJson;
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			LOGGER.debug("exited invokeChargePaymentSpringApi companyId:" + companyId);
		}
		return null;
	}

	private static JSONObject getOneTimeChargePaymentSpringJson(String payment_spring_token, double amount) {
		try {
			if (StringUtils.isEmpty(payment_spring_token)) {
				throw new WebApplicationException("payment spring token cannot be empty for one time charge");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("token", payment_spring_token);
			String amountStr = amount + "";
			amountStr = amountStr.substring(0, amountStr.indexOf("."));
			payloadObj.put("amount", amountStr);
			return payloadObj;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private static JSONObject getOneTimeCustomerChargePaymentSpringJson(String customer_id, double amount) {
		try {
			if (StringUtils.isEmpty(customer_id)) {
				throw new WebApplicationException("customer_id cannot be empty for one time charge");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("customer_id", customer_id);
			String amountStr = amount + "";
			amountStr = amountStr.substring(0, amountStr.indexOf("."));
			payloadObj.put("amount", amountStr);
			return payloadObj;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private static JSONObject getSubscriptionPaymentSpringJson(String customer_id, String ends_after, String plan_id, String bill_immediately) {
		try {
			if (StringUtils.isEmpty(customer_id) || StringUtils.isEmpty(ends_after)) {
				throw new WebApplicationException("customer_id  and ends_after cannot be empty for subscription");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("ends_after", ends_after);
			payloadObj.put("plan_id", plan_id);
			payloadObj.put("customer_id", customer_id);
			payloadObj.put("bill_immediately", bill_immediately);
			return payloadObj;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private static boolean makeInvoicePayment(Invoice invoice, String companyID, String invoiceID, Connection connection) {
		try {
			JSONObject payloadObj = null;
			String urlAction = null;
			long amountToPayInCents = convertDollarToCent(invoice.getAmountToPay());
			switch (invoice.getAction()) {
			case "one_time_charge":
				if (StringUtils.isBlank(invoice.getPayment_spring_token())) {
					throw new WebApplicationException(
							ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "payment token is mandatory for one time invoice payment", Status.INTERNAL_SERVER_ERROR));
				}
				payloadObj = getOneTimeChargePaymentSpringJson(invoice.getPayment_spring_token(), amountToPayInCents);
				urlAction = "charge";
				break;
			case "one_time_customer_charge":
				payloadObj = getOneTimeCustomerChargePaymentSpringJson(invoice.getPayment_spring_customer_id(), amountToPayInCents);
				urlAction = "charge";
				break;
			case "subscription_customer_charge":
				payloadObj = getSubscriptionPaymentSpringJson(invoice.getPayment_spring_customer_id(), invoice.getEnds_after(), invoice.getPlan_id(),
						invoice.getBill_immediately());
				urlAction = "subscription";
				break;
			}
			JSONObject result = invokeChargePaymentSpringApi(companyID, payloadObj, urlAction);
			if (result == null || result.length() == 0) {
				throw new WebApplicationException(
						ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "server error while making one time invoice payment", Status.INTERNAL_SERVER_ERROR));
			}
			if (result.containsKey("errors")) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, result.optJSONArray("errors").optJSONObject(0).optString("message"),
						Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePayment invoicePayment = new InvoicePayment();
			invoicePayment.setId(UUID.randomUUID().toString());
			invoicePayment.setInvoice_id(invoiceID);
			long amount_settled = result.optLong("amount_settled");
			invoicePayment.setAmount(amount_settled);
			invoicePayment.setStatus(result.optString("status"));
			invoicePayment.setTransaction_date(CommonUtils.getGMTDateTime(new Date()));
			invoicePayment.setTransaction_id(result.optString("id"));
			InvoicePayment invoicePaymentResult = MySQLManager.getInvoicePaymentDAOInstance().save(connection, invoicePayment);
			LOGGER.debug("invoicePaymentResult:" + invoicePaymentResult);
			List<InvoicePayment> invoicePaymentLst = MySQLManager.getInvoicePaymentDAOInstance().getByInvoiceId(invoicePayment);
			long amountPaid = 0;
			if (invoicePaymentLst != null && !invoicePaymentLst.isEmpty()) {
				Iterator<InvoicePayment> invoicePaymentLstItr = invoicePaymentLst.iterator();
				while (invoicePaymentLstItr.hasNext()) {
					amountPaid += invoicePaymentLstItr.next().getAmount();
				}
			}
			double amountPaidInDollar = convertCentToDollar(amountPaid);
			if (amountPaidInDollar == invoice.getAmount()) {
				invoice.setState("paid");
			} else {
				invoice.setState("partially paid");
				invoice.setAmount_paid(amountPaidInDollar);
				double amount_due = invoice.getAmount() - amountPaidInDollar;
				invoice.setAmount_due(amount_due);
			}
			return true;
		} catch (Exception e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	private static long convertDollarToCent(String input) {
		LOGGER.debug("entered convertDollarToCent input:" + input);
		try {
			BigDecimal dollars = new BigDecimal(input);
			if (dollars.scale() > 2) {
				throw new IllegalArgumentException();
			}
			long cents = dollars.multiply(new BigDecimal(100)).intValue();
			return cents;
		} catch (Exception e) {
			throw e;
		} finally {
			LOGGER.debug("exited convertDollarToCent input:" + input);
		}
	}

	private static double convertCentToDollar(long cents) {
		LOGGER.debug("entered convertCentToDollar input:" + cents);
		try {
			BigDecimal dollars = new BigDecimal(cents);
			if (dollars.scale() > 0) {
				throw new IllegalArgumentException();
			}
			double dollarValue = dollars.divide(new BigDecimal(100)).doubleValue();
			return dollarValue;
		} catch (Exception e) {
			throw e;
		} finally {
			LOGGER.debug("exited convertCentToDollar input:" + cents);
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputValue;
		do {
			inputValue = br.readLine();
			try {
				long cent = convertDollarToCent(inputValue);
				System.out.println(cent);
				System.out.println(convertCentToDollar(cent));
			} catch (Exception e) {
				System.out.println("enter valid value");
			}
		} while (!StringUtils.isEmpty(inputValue));

	}
}
