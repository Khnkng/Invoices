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
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.helper.InvoiceHistoryHelper;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceCommission;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.service.NotificationService;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.LTMUtils;
import com.qount.invoice.utils.ResponseUtil;
import com.qount.invoice.utils.Utilities;

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
			boolean isJobDeleted = false;
			LOGGER.debug("entered makeInvoicePayment dbInvoice:" + invoice + "uiInvoice" + inputInvoice);
			connection = DatabaseUtilities.getReadWriteConnection();
			String dbInvoiceState = invoice.getState();
			if (dbInvoiceState.equals(Constants.INVOICE_STATE_DRAFT)) {
				throw new WebApplicationException(PropertyManager.getProperty("draft.invoice.paid.validation"), 412);
			}
			boolean isCompanyRegistered = MySQLManager.getCompanyDAOInstance()
					.isCompanyRegisteredWithPaymentSpring(connection, invoice.getCompany_id());
			if (!isCompanyRegistered) {
				throw new WebApplicationException(PropertyManager.getProperty("paymentspring.company.not.registered"),
						412);
			}
			String emailToSend = invoice.getCustomerContactDetails() != null
					? invoice.getCustomerContactDetails().getEmail()
					: PropertyManager.getProperty("invoice.default.mail.address");
			String payment_spring_id = invoice.getCustomer() != null ? invoice.getCustomer().getPayment_spring_id()
					: null;
			String customerId = invoice.getCustomer() != null ? invoice.getCustomer().getCustomer_id() : null;
			boolean isPaymentSpringCustomerExists = StringUtils
					.isEmpty(getPaymentSpringCustomer(payment_spring_id, invoice.getCompany_id())) ? false : true;
			String currency = invoice.getCurrencies() != null ? invoice.getCurrencies().getCode()
					: invoice.getCurrency();
			if (StringUtils.isEmpty(currency)) {
				throw new WebApplicationException("invoice currency is empty!");
			}
			if (!currency.equals(Constants.DEFAULT_INVOICE_CURRENCY)) {
				throw new WebApplicationException("non USD currency payment not supported yet");
			}
			String payment_type = inputInvoice.getPayment_type();
			if (StringUtils.isBlank(payment_type) || (!StringUtils.equals(payment_type, Constants.INVOICE_CREDIT_CARD)
					& !StringUtils.equals(payment_type, Constants.INVOICE_BANK_ACCOUNT))) {
				throw new WebApplicationException("only bank and credit card payments methods supported");
			}
			JSONObject payloadObj = null;
			String urlAction = "charge";
			invoice.setAmountToPay(inputInvoice.getAmountToPay());
			String companyID = invoice.getCompany_id();
			long amountToPayInCents = 0;
			double amountToPay = inputInvoice.getAmountToPay();
			String transactionId = null;
			String state = null;
			Payment payment = new Payment();
			PaymentLine paymentLine = new PaymentLine();
			if (invoice.getAmount_paid() == 0) {
				// new payment
				if (StringUtils.isNotBlank(invoice.getDiscount_id())) {
					double discount = inputInvoice.getDiscount();
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
					if (amountToPay + discount <= invoice.getAmount()) {
						if (invoice.getAmount() == amountToPay + discount) {
							paymentLine.setDiscount(discount);
						}
					} else {
						throw new WebApplicationException(
								PropertyManager.getProperty("invoice.amount.greater.than.error"));
					}
				}
			}
			// 90 100 10
			if (amountToPay > invoice.getAmount_due() - paymentLine.getDiscount()) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
			} else if (amountToPay == invoice.getAmount_due() - paymentLine.getDiscount()) {
				state = Constants.INVOICE_STATE_PAID;
			} else if (amountToPay < invoice.getAmount_due() - paymentLine.getDiscount()) {
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
			String  paymentDate = DateUtils.getCurrentDate(Constants.DATE_TO_INVOICE_FORMAT);
			payment.setPaymentDate(paymentDate);
			invoice.setPayment_date(paymentDate);
			payment.setReceivedFrom(invoice.getCustomer_id());
			payment.setType(payment_type);
			float convertionValue = getConversionValue(invoice.getCurrency(), Constants.DEFAULT_INVOICE_CURRENCY);
			double convertedAmountToPay = convertInvoiceAmount(convertionValue, amountToPay);
			amountToPayInCents = convertDollarToCent(convertedAmountToPay + "");
			switch (inputInvoice.getAction()) {
			case "one_time_charge":
				if (StringUtils.isBlank(inputInvoice.getPayment_spring_token())) {
					throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
							"payment token is mandatory for one time invoice payment", Status.EXPECTATION_FAILED));
				}
				String token = inputInvoice.getPayment_spring_token();
				if (isPaymentSpringCustomerExists) {
					updatePaymentSpringCustomer(payment_spring_id, token, invoice.getCompany_id());
				} else {
					payment_spring_id = createPaymentSpringCustomer(token, invoice.getCompany_id());
					boolean updateCustomer = MySQLManager.getCustomerDAOInstance()
							.updatePaymentSpring(payment_spring_id, customerId);
					if (!updateCustomer) {
						throw new WebApplicationException("error updating customer with payment id");
					}
				}
				// payloadObj =
				// getOneTimeChargePaymentSpringJson(inputInvoice.getPayment_spring_token(),
				// amountToPayInCents);
				// break;
			case "one_time_customer_charge":
				payloadObj = getOneTimeCustomerChargePaymentSpringJson(payment_spring_id, amountToPayInCents,
						payment_type);
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
			if (payloadObj != null) {
				payloadObj.put("email_address", emailToSend);
			}
			JSONObject result = invokeChargePaymentSpringApi(companyID, payloadObj, urlAction);
			LOGGER.debug("payment spring payment result:" + result);
			if (result == null || result.length() == 0) {
				throw new WebApplicationException("server error while making one time invoice payment");
			}
			if (result.has("errors")) {
				throw new WebApplicationException(result.optJSONArray("errors").optJSONObject(0).optString("message"));
			}
			long amount_settled = result.optLong("amount_settled");
			// String status = result.optString("status");
			transactionId = result.optString("id");
			if (StringUtils.isNotBlank(transactionId)) {
				connection.setAutoCommit(false);
			} else {
				throw new WebApplicationException("unable to make payment from payment gateway");
			}
			payment.setReferenceNo(invoice.getNumber());
			payment.setMemo(transactionId);
			double amountPaidInDollar = convertCentToDollar(amount_settled);
			payment.setPaymentAmount(new BigDecimal(amountPaidInDollar));
			paymentLine.setAmount(new BigDecimal(amountPaidInDollar));
			payment.setPayment_status(Constants.APPLIED);
			payments.add(paymentLine);
			payment.setPaymentLines(payments);
			invoice.setAmount_paid(invoice.getAmount_paid() + amountPaidInDollar);
			if (invoice.getAmount_paid() + paymentLine.getDiscount() == invoice.getAmount()) {
				invoice.setState(Constants.INVOICE_STATE_PAID);
				String isJobDeletedStr = Utilities.unschduleInvoiceJob(invoice.getRemainder_job_id());
				if(StringUtils.isNotBlank(isJobDeletedStr) && isJobDeletedStr.equals("true")){
					//delete remainder job id
					isJobDeleted = true;
				}
				invoice.setAmount_due(0);
			} else {
				invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);
				double amount_due = invoice.getAmount() - invoice.getAmount_paid();
				invoice.setAmount_due(amount_due);
			}
			boolean paymentCaptured = false;
			if (MySQLManager.getPaymentDAOInstance().save(payment, connection, false) != null) {
				//creating invoice history 
				String description = "Amount: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount())+
						",Amount Due: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount_due())+
						",Amount Paid: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount_paid())+
						",Ref Num: "+payment.getReferenceNo()+
						",State: "+InvoiceParser.getDisplayState(invoice.getState());
				InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoice,description,InvoiceParser.getDisplayState(invoice.getState()));
				MySQLManager.getInvoice_historyDAO().create(connection, history);
				paymentCaptured = true;
			}
			if (paymentCaptured) {
				if (invoice.getAmount_paid() == invoice.getAmount()) {
					InvoiceCommission invoiceCommission = new InvoiceCommission();
					invoiceCommission.setInvoice_id(invoice.getId());
					List<InvoiceCommission> dbInvoiceCommissions = MySQLManager.getInvoiceDAOInstance()
							.getInvoiceCommissions(invoiceCommission);
					InvoiceControllerImpl.createInvoicePaidCommissions(connection, dbInvoiceCommissions,
							invoice.getUser_id(), invoice.getCompany_id(), invoice.getId(), invoice.getAmount(),
							invoice.getCurrency());
				}
				if(isJobDeleted){
					//updating remainder job id as null
					MySQLManager.getInvoiceDAOInstance().deleteRemainderJobId(connection, invoiceID, null);
				}
				connection.commit();
				CommonUtils.createJournal(
						new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(),
						invoice.getCompany_id());
				NotificationService.notifyOnInvoicePayment(companyID, invoiceID, invoice.getNumber(), invoice.getCustomer().getCustomer_name(), convertedAmountToPay);
				return true;
			} else {
				LOGGER.fatal("payment done but not saved in qount db");
				LOGGER.fatal("invoiceID:" + invoiceID);
				LOGGER.fatal("transactionId:" + transactionId);
				LOGGER.fatal("payment:" + payment);
				throw new WebApplicationException("payment done but not saved in qount db");
				// TODO refund payment
			}
		} catch (

		WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getMessage(), Status.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited makeInvoicePayment dbInvoice:" + invoice + "uiInvoice" + inputInvoice);
		}
	}

	private static JSONObject invokeChargePaymentSpringApi(String companyId, JSONObject payloadObj, String urlAction)
			throws Exception {
		try {
			LOGGER.debug("entered invokeChargePaymentSpringApi companyId:" + companyId + " payloadObj :" + payloadObj
					+ " urlAction :" + urlAction);
			if (StringUtils.isEmpty(companyId) || payloadObj == null || payloadObj.length() == 0) {
				throw new WebApplicationException("company id and payload object cannot be empty companyID:" + companyId
						+ " payloadObj: " + payloadObj);
			}
			String path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port",
					"payment.spring.base.url");
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
			LOGGER.debug("result invokeChargePaymentSpringApi path:" + path + " payloadObj :" + payloadObj
					+ " responseJson:" + responseJson);
			if (responseJson != null && responseJson.length() != 0) {
				return responseJson;
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited invokeChargePaymentSpringApi companyId:" + companyId + " payloadObj :" + payloadObj
					+ " urlAction :" + urlAction);
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
	// LOGGER.error(CommonUtils.getErrorStackTrace(e));
	// throw e;
	// }
	// }

	// private static JSONObject getOneTimeChargePaymentSpringJson(String
	// payment_spring_token, Object amount) {
	// try {
	// if (StringUtils.isEmpty(payment_spring_token)) {
	// throw new WebApplicationException("payment spring token cannot be empty
	// for one time charge");
	// }
	// JSONObject payloadObj = new JSONObject();
	// payloadObj.put("token", payment_spring_token);
	// payloadObj.put("amount", amount);
	// return payloadObj;
	// } catch (Exception e) {
	// LOGGER.error(CommonUtils.getErrorStackTrace(e));
	// throw e;
	// }
	// }

	private static JSONObject getOneTimeCustomerChargePaymentSpringJson(String customer_id, Object amount,
			String payment_type) {
		try {
			LOGGER.debug("entered getOneTimeCustomerChargePaymentSpringJson(customer_id:" + customer_id + ", amount"
					+ amount + ", payment_type:" + payment_type);
			if (StringUtils.isEmpty(customer_id)) {
				throw new WebApplicationException("customer_id cannot be empty for one time charge");
			}
			JSONObject payloadObj = new JSONObject();
			payloadObj.put("customer_id", customer_id);
			payloadObj.put("amount", amount);
			if (StringUtils.isNotBlank(payment_type) && payment_type.equals(Constants.INVOICE_BANK_ACCOUNT)) {
				payloadObj.put("charge_bank_account", true);
			}
			LOGGER.debug("exited getOneTimeCustomerChargePaymentSpringJson(customer_id:" + customer_id + ", amount"
					+ amount + ", payment_type:" + payment_type);
			return payloadObj;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static float getConversionValue(String currency_from, String currency_to) throws Exception {
		try {
			if (StringUtils.isAnyBlank(currency_from, currency_to)) {
				throw new Exception("invalid input currency_from:" + currency_from + " ,currency_to:" + currency_to);
			}
			float conversion = Constants.CURRENCY_CONVERTER.convert(currency_from, currency_to,
					Constants.INVOICE_CONVERSION_DATE_FORMAT.format(new Date()));
			return conversion;
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static String updatePaymentSpringCustomer(String payment_spring_id, String token, String companyId) {
		try {
			LOGGER.debug("entered updatePaymentSpringCustomer: payment_spring_id" + payment_spring_id + " token:"
					+ token + " companyId:" + companyId);
			if (StringUtils.isAnyBlank(companyId, payment_spring_id, token)) {
				throw new WebApplicationException(
						"companyId,payment_spring_id,token cannot be empty to update customer payment details");
			}
			String path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port",
					"oneapp.base.url");
			if (StringUtils.isEmpty(path)) {
				throw new WebApplicationException("internal server error unable to url for payment server ");
			}
			path = path + "PaymentSpring/companies/" + companyId + "/customers";
			path = path.replace("{comapnyID}", companyId);
			JSONObject paymentSpringObject = new JSONObject();
			paymentSpringObject.put("id", payment_spring_id);
			paymentSpringObject.put("token", token);
			JSONObject result = HTTPClient.put(path, paymentSpringObject.toString());
			if (CommonUtils.isValidJSON(result)) {
				JSONArray errors = result.optJSONArray("errors");
				if (CommonUtils.isValidJSONArray(errors)) {
					throw new WebApplicationException(errors.optJSONObject(0).optString("message"));
				}
				String payment_customer_id = result.optString("id");
				if (!StringUtils.isEmpty(payment_customer_id)) {
					return payment_customer_id;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited updatePaymentSpringCustomer: payment_spring_id" + payment_spring_id + " token:" + token
					+ " companyId:" + companyId);
		}
		return null;
	}

	private static String createPaymentSpringCustomer(String token, String companyId) throws Exception {
		try {
			LOGGER.debug("entered createPaymentSpringCustomer: token:" + token + " companyId:" + companyId);
			if (StringUtils.isAnyBlank(companyId, token)) {
				throw new WebApplicationException("companyId,token cannot be empty to create customer payment details");
			}
			String path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port",
					"oneapp.base.url");
			if (StringUtils.isEmpty(path)) {
				throw new WebApplicationException("internal server error unable to url for payment server ");
			}
			path = path + "PaymentSpring/companies/" + companyId + "/customers";
			path = path.replace("{comapnyID}", companyId);
			JSONObject paymentSpringObject = new JSONObject();
			paymentSpringObject.put("token", token);
			JSONObject result = HTTPClient.post(path, paymentSpringObject.toString());
			if (CommonUtils.isValidJSON(result)) {
				JSONArray errors = result.optJSONArray("errors");
				if (CommonUtils.isValidJSONArray(errors)) {
					throw new WebApplicationException(errors.optJSONObject(0).optString("message"));
				}
				String payment_customer_id = result.optString("id");
				if (!StringUtils.isEmpty(payment_customer_id)) {
					return payment_customer_id;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited createPaymentSpringCustomer: token:" + token + " companyId:" + companyId);
		}
		return null;
	}

	private static String getPaymentSpringCustomer(String payment_spring_id, String companyId) {
		try {
			LOGGER.debug("entered getPaymentSpringCustomer: payment_spring_id" + payment_spring_id + " companyId:"
					+ companyId);
			if (StringUtils.isBlank(payment_spring_id)) {
				return null;
			}
			if (StringUtils.isBlank(companyId)) {
				throw new WebApplicationException("companyId cannot be empty to get payment customer details");
			}
			String path = LTMUtils.getHostAddress("payment.spring.docker.hostname", "payment.spring.docker.port",
					"oneapp.base.url");
			if (StringUtils.isEmpty(path)) {
				throw new WebApplicationException("internal server error unable to url for payment server ");
			}
			// path = "http://paymentspring-dev.7f026d40.svc.dockerapp.io:85/";
			path = path + "PaymentSpring/companies/" + companyId + "/customers/" + payment_spring_id;
			path = path.replace("{comapnyID}", companyId);
			System.out.println(path);
			JSONObject result = HTTPClient.get(path);
			System.out.println(result);
			if (CommonUtils.isValidJSON(result)) {
				JSONArray errors = result.optJSONArray("errors");
				if (CommonUtils.isValidJSONArray(errors)) {
					throw new WebApplicationException(errors.optJSONObject(0).optString("message"));
				}
				String payment_customer_id = result.optString("id");
				if (!StringUtils.isEmpty(payment_customer_id)) {
					return payment_customer_id;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(),
							e.getResponse() != null ? e.getResponse().getStatus() : Constants.EXPECTATION_FAILED));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			LOGGER.debug("exited getPaymentSpringCustomer: payment_spring_id" + payment_spring_id + " companyId:"
					+ companyId);
		}
		return null;
	}

}
