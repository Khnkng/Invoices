package com.qount.invoice.controllerImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.DateUtils;
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

	public static boolean makeInvoicePayment(Invoice invoice, String invoiceID, Invoice inputInvoice) {
		Connection connection = null;
		try {
			String currency = invoice.getCurrencies() != null ? invoice.getCurrencies().getCode() : invoice.getCurrency();
			if (StringUtils.isEmpty(currency)) {
				throw new WebApplicationException("invoice currency is empty!");
			}
			if (!currency.equals(Constants.DEFAULT_INVOICE_CURRENCY)) {
				throw new WebApplicationException("non USD currency payment not supported yet");
			}
			String payment_type = inputInvoice.getPayment_type();
			if(StringUtils.isBlank(payment_type) || (!StringUtils.equals(payment_type, Constants.INVOICE_CREDIT_CARD)
					& !StringUtils.equals(payment_type, Constants.INVOICE_BANK_ACCOUNT))){
				throw new WebApplicationException("only bank and credit card payments methods supported");
			}
			JSONObject payloadObj = null;
			String urlAction = "charge";
			invoice.setAmountToPay(inputInvoice.getAmountToPay());
			String companyID = invoice.getCompany_id();
			long amountToPayInCents = 0;
			double amountToPay = Double.parseDouble(inputInvoice.getAmountToPay());
			String transactionId = null;
			String state = null;
			Payment payment = new Payment();
			PaymentLine paymentLine = new PaymentLine();
			if (amountToPay > invoice.getAmount_due()) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
			} else if (amountToPay == invoice.getAmount_due()) {
				state = Constants.INVOICE_STATE_PAID;
			} else if (amountToPay < invoice.getAmount_due()) {
				state = Constants.INVOICE_STATE_PARTIALLY_PAID;
			}
			paymentLine.setId(UUID.randomUUID().toString());
			paymentLine.setInvoiceDate(invoice.getInvoice_date());
			paymentLine.setInvoiceId(invoice.getId());
			paymentLine.setState(state);
			paymentLine.setTerm(invoice.getTerm());
			List<PaymentLine> payments = new ArrayList<PaymentLine>();
			payment.setCompanyId(invoice.getCompany_id());
			payment.setCurrencyCode(invoice.getCurrency());
			payment.setId(UUID.randomUUID().toString());
			payment.setPaymentDate(DateUtils.getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT));
			payment.setReceivedFrom(invoice.getCustomer_id());
			payment.setType(payment_type);
			float convertionValue = getConversionValue(invoice.getCurrency(), Constants.DEFAULT_INVOICE_CURRENCY);
			double convertedAmountToPay = convertInvoiceAmount(convertionValue, amountToPay);
			amountToPayInCents = convertDollarToCent(convertedAmountToPay + "");

			switch (inputInvoice.getAction()) {
			case "one_time_charge":
				if (StringUtils.isBlank(inputInvoice.getPayment_spring_token())) {
					throw new WebApplicationException(
							ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "payment token is mandatory for one time invoice payment", Status.INTERNAL_SERVER_ERROR));
				}
				payloadObj = getOneTimeChargePaymentSpringJson(inputInvoice.getPayment_spring_token(), amountToPayInCents);
				break;
			case "one_time_customer_charge":
				String payment_spring_id = invoice.getCustomer()!=null?invoice.getCustomer().getPayment_spring_id():null;
				if(StringUtils.isBlank(payment_spring_id)){
					throw new WebApplicationException("unable to find customer for invoice");
				}
				payloadObj = getOneTimeCustomerChargePaymentSpringJson(payment_spring_id, amountToPayInCents);
				break;
			case Constants.SUBSCRIPTION_CUSTOMER_CHARGE:
				// payloadObj =
				// getSubscriptionPaymentSpringJson(invoice.getPayment_spring_customer_id(),
				// invoice.getPaymentSpringPlan().getEnds_after(),
				// invoice.getPlan_id(),
				// invoice.getPaymentSpringPlan().getBill_immediately());
				// urlAction = "subscription";
				break;
			}
			JSONObject result = invokeChargePaymentSpringApi(companyID, payloadObj, urlAction);
			if (result == null || result.length() == 0) {
				throw new WebApplicationException("server error while making one time invoice payment");
			}
			if (result.has("errors")) {
				throw new WebApplicationException(result.optJSONArray("errors").optJSONObject(0).optString("message"));
			}
			long amount_settled = result.optLong("amount_settled");
//			String status = result.optString("status");
			transactionId = result.optString("id");
			if(StringUtils.isNotBlank(transactionId)){
				connection = DatabaseUtilities.getReadWriteConnection();
				connection.setAutoCommit(false);
			}else{
				throw new WebApplicationException("unable to make payment from payment gateway");
			}
			payment.setReferenceNo(invoice.getNumber());
			payment.setMemo(transactionId);
			double amountPaidInDollar = convertCentToDollar(amount_settled);
			payment.setPaymentAmount(new BigDecimal(amountPaidInDollar));
			paymentLine.setAmount(new BigDecimal(amountPaidInDollar));
			payments.add(paymentLine);
			payment.setPaymentLines(payments);
			invoice.setAmount_paid(invoice.getAmount_paid()+amountPaidInDollar);
			if (amountPaidInDollar == invoice.getAmount()) {
				invoice.setState(Constants.INVOICE_STATE_PAID);
				invoice.setAmount_due(0);
			} else {
				invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);
				double amount_due = invoice.getAmount() - invoice.getAmount_paid();
				invoice.setAmount_due(amount_due);
			}
//			Timestamp invoice_date = InvoiceParser.convertStringToTimeStamp(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
//			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(invoice.getUser_id(), invoice, companyID, false);
			boolean isInvoiceUpdated = MySQLManager.getInvoiceDAOInstance().update(connection, invoiceObj)!=null?true:false;
			if(!isInvoiceUpdated){
				throw new WebApplicationException("payment done but not saved in qount db");
				//TODO refund payment
			}
			boolean paymentCaptured =false;
			if(MySQLManager.getPaymentDAOInstance().save(payment, connection)!=null){
				paymentCaptured =true;
			}
			if(paymentCaptured){
				connection.commit();
				return true;
			}else{
				throw new WebApplicationException("payment done but not saved in qount db");
				//TODO refund payment
			}
		} catch (Exception e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	private static JSONObject invokeChargePaymentSpringApi(String companyId, JSONObject payloadObj, String urlAction) throws Exception {
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

	// private static JSONObject getSubscriptionPaymentSpringJson(String
	// customer_id, String ends_after, String plan_id, String bill_immediately)
	// {
	// try {
	// if (StringUtils.isEmpty(customer_id) || StringUtils.isEmpty(ends_after))
	// {
	// throw new WebApplicationException("customer_id and ends_after cannot be
	// empty for subscription");
	// }
	// JSONObject payloadObj = new JSONObject();
	// payloadObj.put("ends_after", ends_after);
	// payloadObj.put("plan_id", plan_id);
	// payloadObj.put("customer_id", customer_id);
	// if(StringUtils.isNotBlank(bill_immediately)){
	// payloadObj.put("bill_immediately", bill_immediately);
	// }
	// return payloadObj;
	// } catch (Exception e) {
	// LOGGER.error(e);
	// throw e;
	// }
	// }

	private static JSONObject getOneTimeChargePaymentSpringJson(String payment_spring_token, Object amount) {
		try {
			if (StringUtils.isEmpty(payment_spring_token)) {
				throw new WebApplicationException("payment spring token cannot be empty for one time charge");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("token", payment_spring_token);
			payloadObj.put("amount", amount);
			return payloadObj;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private static JSONObject getOneTimeCustomerChargePaymentSpringJson(String customer_id, Object amount) {
		try {
			if (StringUtils.isEmpty(customer_id)) {
				throw new WebApplicationException("customer_id cannot be empty for one time charge");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("customer_id", customer_id);
			payloadObj.put("amount", amount);
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

	private static double convertInvoiceAmount(float conversion, double amount) throws Exception {
		try {
			if (amount < 0.00d) {
				throw new Exception("negative amount");
			}
			amount = amount * conversion;
			amount = Double.valueOf(Constants.INVOICE_CONVERSION_DECIMALFORMAT.format(amount));
			return amount;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private static float getConversionValue(String currency_from, String currency_to) throws Exception {
		try {
			if (StringUtils.isAnyBlank(currency_from, currency_to)) {
				throw new Exception("invalid input currency_from:" + currency_from + " ,currency_to:" + currency_to);
			}
			float conversion = Constants.CURRENCY_CONVERTER.convert(currency_from, currency_to, Constants.INVOICE_CONVERSION_DATE_FORMAT.format(new Date()));
			return conversion;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}

}
