package com.qount.invoice.controllerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoicePayment;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.LTMUtils;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 1 Jun 2017
 *
 */
public class InvoiceDetailControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoiceDetailControllerImpl.class);

	public static boolean makeInvoicePayment(Invoice invoice, String invoiceID,Invoice inputInvoice) {
		Connection connection = null;
		try {
			JSONObject payloadObj = null;
			String urlAction = null;
			invoice.setAmountToPay(inputInvoice.getAmountToPay());
			String companyID = invoice.getCompany_id();
			long amountToPayInCents = convertDollarToCent(invoice.getAmountToPay());
			switch (inputInvoice.getAction()) {
			case "one_time_charge":
				if (StringUtils.isBlank(inputInvoice.getPayment_spring_token())) {
					throw new WebApplicationException(
							ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "payment token is mandatory for one time invoice payment", Status.INTERNAL_SERVER_ERROR));
				}
				payloadObj = getOneTimeChargePaymentSpringJson(inputInvoice.getPayment_spring_token(), amountToPayInCents);
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
				throw new WebApplicationException("server error while making one time invoice payment");
			}
			if (result.containsKey("errors")) {
				throw new WebApplicationException(result.optJSONArray("errors").optJSONObject(0).optString("message"));
			}
			InvoicePayment invoicePayment = new InvoicePayment();
			invoicePayment.setId(UUID.randomUUID().toString());
			invoicePayment.setInvoice_id(invoiceID);
			long amount_settled = result.optLong("amount_settled");
			invoicePayment.setAmount(amount_settled);
			invoicePayment.setStatus(result.optString("status"));
			invoicePayment.setTransaction_date(CommonUtils.getGMTDateTime(new Date()));
			invoicePayment.setTransaction_id(result.optString("id"));
			connection = DatabaseUtilities.getReadWriteConnection();
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
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, e.getMessage(), Status.INTERNAL_SERVER_ERROR));
		}finally {
			DatabaseUtilities.closeConnection(connection);
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
			System.out.println("*******************************************");
			System.out.println(path);
			System.out.println(payloadObj);
			System.out.println("*******************************************");
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
