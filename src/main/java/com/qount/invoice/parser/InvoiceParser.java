package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceCommission;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceMail;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.model.PaymentSpringPlan;
import com.qount.invoice.model.UserCompany;
import com.qount.invoice.pdf.InvoiceReference;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.CurrencyConverter;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.LTMUtils;
import com.qount.invoice.utils.ResponseUtil;

/**
 * @author Apurva
 * @version 1.0 Jan 11 2017
 */
public class InvoiceParser {
	private static final Logger LOGGER = Logger.getLogger(InvoiceParser.class);

	public static Invoice getInvoiceObj(String userId, Invoice invoice, String companyID, boolean createFlag) {
		try {
			if (invoice == null || StringUtils.isAnyBlank(userId, companyID, invoice.getCurrency())) {
				throw new WebApplicationException("userId, companyId, currency are mandatory", Constants.INVALID_INPUT);
			}
			if (StringUtils.isNotBlank(invoice.getState())
					&& !Constants.INVOICE_STATE_MAP.keySet().contains(invoice.getState())) {
				throw new WebApplicationException("Invalid invoice state", Constants.INVALID_INPUT);
			}
			UserCompany userCompany = null;
			invoice.setCompany_id(companyID);
			invoice.setIs_recurring(StringUtils.isNotEmpty(invoice.getPlan_id()));
			userCompany = CommonUtils.getCompany(companyID);
			invoice.setCompanyName(userCompany.getName());

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp invoice_date = convertStringToTimeStamp(invoice.getInvoice_date(),
					Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			Timestamp due_date = convertStringToTimeStamp(invoice.getDue_date(),
					Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			Timestamp job_date = convertStringToTimeStamp(invoice.getJob_date(),
					Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			invoice.setUser_id(userId);
			if (createFlag) {
				invoice.setId(UUID.randomUUID().toString());
			}
			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			invoice.setDue_date(due_date != null ? due_date.toString() : null);
			invoice.setJob_date(job_date != null ? job_date.toString() : null);
			invoice.setLast_updated_at(timestamp != null ? timestamp.toString() : null);
			invoice.setLast_updated_by(userId);
			if (createFlag) {
				invoice.setAmount_due(invoice.getAmount());
				invoice.setAmount_paid(0.00d);
			}
			setInvoiceAmountByDate(invoice, userCompany);
			List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
			Iterator<InvoiceLine> invoiceLineItr = invoiceLines.iterator();
			while (invoiceLineItr.hasNext()) {
				InvoiceLine line = invoiceLineItr.next();
				line.setId(UUID.randomUUID().toString());
				line.setInvoice_id(invoice.getId());
				line.setLast_updated_at(timestamp.toString());
				line.setLast_updated_by(userId);
				line.setAmount(getTwoDecimalValue(line.getAmount()));
				line.setPrice(getTwoDecimalValue(line.getPrice()));
				line.setQuantity(getFourDecimalValue(line.getQuantity()));
			}
			invoice.setCreated_at(timestamp.toString());
			invoice.setRecepientsMailsArr(CommonUtils.getJsonArrayFromList(invoice.getRecepientsMails()));
			convertAmountToDecimal(invoice);
			if(StringUtils.isEmpty(invoice.getDiscount_id())) {
				invoice.setDiscount_id(null);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return invoice;
	}

	public static void updateInvoiceAmountByDate(Invoice invoice) {
		try {
			UserCompany userCompany = CommonUtils.getCompany(invoice.getCompany_id());
			setInvoiceAmountByDate(invoice, userCompany);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static Timestamp convertStringToTimeStamp(String dateStr, SimpleDateFormat sdf) {
		try {
			return new Timestamp(sdf.parse(dateStr).getTime());
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static String convertTimeStampToString(String dateStr, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			if (StringUtils.isNotBlank(dateStr))
				return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static Invoice convertTimeStampToString(Invoice invoice) {
		try {
			if (invoice != null) {
				invoice.setInvoice_date(convertTimeStampToString(invoice.getInvoice_date(),
						Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice.setDue_date(convertTimeStampToString(invoice.getDue_date(),
						Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice.setJob_date(convertTimeStampToString(invoice.getJob_date(),
						Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice.setPayment_date(convertTimeStampToString(invoice.getPayment_date(),
						Constants.PAYMENT_DATE_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return invoice;
	}

	/**
	 * method used to convert invoice amount fields to two decimals
	 * 
	 * @param invoiceLst
	 * @return
	 */
	public static void convertAmountToDecimal(List<Invoice> invoiceLst) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				for (int i = 0; i < invoiceLst.size(); i++) {
					Invoice invoice = invoiceLst.get(i);
					if (invoice != null) {
						invoice.setAmount(InvoiceParser.getTwoDecimalValue(invoice.getAmount()));
						invoice.setAmount_by_date(InvoiceParser.getTwoDecimalValue(invoice.getAmount_by_date()));
						invoice.setAmount_due(InvoiceParser.getTwoDecimalValue(invoice.getAmount_due()));
						invoice.setAmount_paid(InvoiceParser.getTwoDecimalValue(invoice.getAmount_paid()));
						invoice.setAmountToPay(InvoiceParser.getTwoDecimalValue(invoice.getAmountToPay()));
						invoice.setProcessing_fees(InvoiceParser.getTwoDecimalValue(invoice.getProcessing_fees()));
						invoice.setSub_total(InvoiceParser.getTwoDecimalValue(invoice.getSub_total()));
						invoice.setTax_amount(InvoiceParser.getTwoDecimalValue(invoice.getTax_amount()));
						Iterator<InvoiceLine> invoiceLineIterator = invoice.getInvoiceLines() != null
								? invoice.getInvoiceLines().iterator()
								: null;
						if (invoiceLineIterator != null) {
							while (invoiceLineIterator.hasNext()) {
								InvoiceLine invoiceLine = invoiceLineIterator.next();
								invoiceLine.setAmount(getTwoDecimalValue(invoiceLine.getAmount()));
								invoiceLine.setPrice(getTwoDecimalValue(invoiceLine.getPrice()));
								invoiceLine.setQuantity(getFourDecimalValue(invoiceLine.getQuantity()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static void formatGetInvoicesResponse(List<Invoice> invoiceLst, Map<String, String> invoicePaymentIdMap) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				for (int i = 0; i < invoiceLst.size(); i++) {
					Invoice invoice = invoiceLst.get(i);
					if (invoice != null) {
						invoice.setInvoice_date(convertTimeStampToString(invoice.getInvoice_date(),
								Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						invoice.setDue_date(convertTimeStampToString(invoice.getDue_date(),
								Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						invoice.setPayment_date(convertTimeStampToString(invoice.getPayment_date(),
								new SimpleDateFormat("yyyy-MM-dd"), Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						invoice.setAmount(InvoiceParser.getTwoDecimalValue(invoice.getAmount()));
						invoice.setAmount_by_date(InvoiceParser.getTwoDecimalValue(invoice.getAmount_by_date()));
						invoice.setAmount_due(InvoiceParser.getTwoDecimalValue(invoice.getAmount_due()));
						invoice.setAmount_paid(InvoiceParser.getTwoDecimalValue(invoice.getAmount_paid()));
						invoice.setAmountToPay(InvoiceParser.getTwoDecimalValue(invoice.getAmountToPay()));
						invoice.setProcessing_fees(InvoiceParser.getTwoDecimalValue(invoice.getProcessing_fees()));
						invoice.setSub_total(InvoiceParser.getTwoDecimalValue(invoice.getSub_total()));
						invoice.setTax_amount(InvoiceParser.getTwoDecimalValue(invoice.getTax_amount()));
						invoice.setLate_fee_amount(InvoiceParser.getTwoDecimalValue(invoice.getLate_fee_amount()));
						if (StringUtils.isNoneBlank(invoice.getJob_date())) {
							invoice.setJob_date(convertTimeStampToString(invoice.getJob_date(),
									Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						}
						if (StringUtils.isNotBlank(invoice.getState()) && invoice.getState().equals(Constants.OPEN)) {
							invoice.setState(Constants.OPENED);
						}
						Iterator<InvoiceLine> invoiceLineIterator = invoice.getInvoiceLines() != null
								? invoice.getInvoiceLines().iterator()
								: null;
						if (invoiceLineIterator != null) {
							while (invoiceLineIterator.hasNext()) {
								InvoiceLine invoiceLine = invoiceLineIterator.next();
								invoiceLine.setAmount(getTwoDecimalValue(invoiceLine.getAmount()));
								invoiceLine.setPrice(getTwoDecimalValue(invoiceLine.getPrice()));
								invoiceLine.setQuantity(getFourDecimalValue(invoiceLine.getQuantity()));
							}
						}
						if (null != invoicePaymentIdMap && !invoicePaymentIdMap.isEmpty()) {
							invoice.setPayment_ids(invoicePaymentIdMap.get(invoice.getId()));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	public static void formatGetInvoiceHistoriesResponse(List<InvoiceHistory> invoiceHistoryLst) {
		try {
			if (invoiceHistoryLst != null && !invoiceHistoryLst.isEmpty()) {
				for (int i = 0; i < invoiceHistoryLst.size(); i++) {
					InvoiceHistory invoiceHistory = invoiceHistoryLst.get(i);
					if (invoiceHistory != null) {
						invoiceHistory.setAction_at(convertTimeStampToString(invoiceHistory.getAction_at(),
								Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.UI_DATE_TIME_FORMAT));
						String action = StringUtils.capitalize(invoiceHistory.getAction());
						if (StringUtils.isNoneBlank(action)) {
							action = action.replace("_", " ");
						}
						invoiceHistory.setAction(action);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	/**
	 * method used to convert invoice amount fields to two decimals
	 * 
	 * @param invoice
	 */
	public static void convertAmountToDecimal(Invoice invoice) {
		try {
			if (invoice != null) {
				invoice.setAmount(getTwoDecimalValue(invoice.getAmount()));
				invoice.setAmount_by_date(getTwoDecimalValue(invoice.getAmount_by_date()));
				invoice.setAmount_due(getTwoDecimalValue(invoice.getAmount_due()));
				invoice.setAmount_paid(getTwoDecimalValue(invoice.getAmount_paid()));
				invoice.setAmountToPay(getTwoDecimalValue(invoice.getAmountToPay()));
				invoice.setProcessing_fees(getTwoDecimalValue(invoice.getProcessing_fees()));
				invoice.setSub_total(getTwoDecimalValue(invoice.getSub_total()));
				invoice.setTax_amount(getTwoDecimalValue(invoice.getTax_amount()));
				invoice.setLate_fee_amount(getTwoDecimalValue(invoice.getLate_fee_amount()));
				Iterator<InvoiceLine> invoiceLineIterator = invoice.getInvoiceLines() != null
						? invoice.getInvoiceLines().iterator()
						: null;
				if (invoiceLineIterator != null) {
					while (invoiceLineIterator.hasNext()) {
						InvoiceLine invoiceLine = invoiceLineIterator.next();
						invoiceLine.setAmount(getTwoDecimalValue(invoiceLine.getAmount()));
						invoiceLine.setPrice(getTwoDecimalValue(invoiceLine.getPrice()));
						invoiceLine.setQuantity(getFourDecimalValue(invoiceLine.getQuantity()));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static Invoice getInvoiceObjToDelete(String user_id, String companyID, String invoice_id) {
		try {
			if (StringUtils.isAnyBlank(user_id, invoice_id)) {
				return null;
			}
			Invoice invoice = new Invoice();
			invoice.setUser_id(user_id);
			invoice.setId(invoice_id);
			invoice.setCompany_id(companyID);
			return invoice;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			// throw new WebApplicationException(e.getLocalizedMessage(), 500);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static InvoiceLine getInvoiceLineObjToDelete(String invoice_id, String invoiceLine_id) {
		try {
			if (StringUtils.isEmpty(invoice_id) && StringUtils.isEmpty(invoiceLine_id)) {
				return null;
			}
			InvoiceLine invoiceLine = new InvoiceLine();
			invoiceLine.setId(invoiceLine_id);
			invoiceLine.setInvoice_id(invoice_id);
			return invoiceLine;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
	}

	public static void setInvoiceAmountByDate(Invoice invoice, UserCompany userCompany) {
		try {
			Double amount = invoice.getAmount();
			String companyCurrency = userCompany.getDefaultCurrency();
			String invoiceLineCurrency = invoice.getCurrency();
			Double invoiceLineDateAmount = 0d;
			if (amount != null) {
				if (StringUtils.isAnyBlank(companyCurrency, invoiceLineCurrency)) {
					return;
				}
				invoice.setAmount_by_date(amount);
				if (!invoiceLineCurrency.equals(companyCurrency)) {
					CurrencyConverter converter = new CurrencyConverter();
					Date date = DateUtils.getDateFromString(invoice.getLast_updated_at(), Constants.DUE_DATE_FORMAT);
					String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					float conversion = converter.convert(invoiceLineCurrency, companyCurrency, formatedDate);
					invoiceLineDateAmount = amount * conversion;
					invoiceLineDateAmount = Double.valueOf(new DecimalFormat("#.##").format(invoiceLineDateAmount));
					invoice.setAmount_by_date(getTwoDecimalValue(invoiceLineDateAmount));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error converting currency", e);
		}

	}

	public static InvoiceReference getInvoiceReference(Invoice invoice) {
		try {
			if (StringUtils.isBlank(invoice.getId())) {
				throw new WebApplicationException("invoice Id cannot be empty");
			}
			InvoiceReference invoiceReference = new InvoiceReference();
			Company company = new Company();
			company.setId(invoice.getCompany_id());
			Customer customer = new Customer();
			String customerId = invoice.getCustomer() == null ? invoice.getCustomer_id()
					: invoice.getCustomer().getCustomer_id();
			customer.setCustomer_id(customerId);
			invoiceReference.setCompany(company);
			invoiceReference.setCustomer(customer);
			InvoicePreference invoicePreference = new InvoicePreference();
			invoiceReference.setInvoicePreference(invoicePreference);
			invoiceReference.setInvoice(invoice);
			return invoiceReference;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static void formatInvoices(List<Invoice> invoiceLst, Map<String, String> invoicePaymentIdMap) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				// convertTimeStampToString(invoiceLst);
				// convertAmountToDecimal(invoiceLst);
				formatGetInvoicesResponse(invoiceLst, invoicePaymentIdMap);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static JSONObject formatBadges(Map<String, String> badges) {
		JSONObject result = new JSONObject();
		try {
			if (badges != null && !badges.isEmpty()) {
				result.put("badges", badges);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return result;
	}

	public static InvoiceMail getInvoiceMailFromInvoice(Invoice invoice) {
		try {
			if (invoice.getCurrencies() == null || invoice.getCustomer() == null) {
				return null;
			}
			InvoiceMail invoiceMail = new InvoiceMail();
			invoiceMail.setAmount(invoice.getAmount());
			invoiceMail.setAmount_by_date(invoice.getAmount_by_date());
			invoiceMail.setCompanyName(invoice.getCompanyName());
			invoiceMail.setCurrencyCode(invoice.getCurrencies().getCode());
			invoiceMail.setCurrencyHtml_symbol(invoice.getCurrencies().getHtml_symbol());
			invoiceMail.setCustomerEmails(invoice.getCustomer().getEmail_ids());
			invoiceMail.setCustomerName(invoice.getCustomer().getCustomer_name());
			invoiceMail.setInvocieDate(invoice.getInvoice_date());
			invoiceMail.setInvoiceCreatedAt(invoice.getCreated_at());
			invoiceMail.setInvoiceDueDate(invoice.getDue_date());
			invoiceMail.setInvoiceId(invoice.getId());
			invoiceMail.setInvoiceNumber(invoice.getNumber());
			return invoiceMail;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static JSONObject getJsonForPaymentSpringPlan(PaymentSpringPlan paymentSpringPlan) throws Exception {
		try {
			LOGGER.debug("entered getJsonForPaymentSpringPlan :" + paymentSpringPlan);
			JSONObject result = new JSONObject(paymentSpringPlan.toString());
			if (StringUtils.isEmpty(result.optString("day"))) {
				result.put("day", result.optJSONObject("day_map"));
				result.remove("day_map");
			}
			CommonUtils.removeKeysIfNull(result, "bill_immediately", "ends_after", "day");
			return result;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited getJsonForPaymentSpringPlan :" + paymentSpringPlan);
		}
	}

	public static double getTwoDecimalValue(double amount) {
		try {
			return Double.valueOf(new DecimalFormat("#.##").format(amount));
		} catch (Exception e) {
			LOGGER.error("error in Invoice parser getTwoDecimalValue amount:" + amount, e);
		}
		return 0.00d;
	}

	public static double getFourDecimalValue(double amount) {
		try {
			return Double.valueOf(new DecimalFormat("#.####").format(amount));
		} catch (Exception e) {
			LOGGER.error("error in Invoice parser getFourDecimalValue amount:" + amount, e);
		}
		return 0.00d;
	}

	public static boolean isPaidStateInvoicePresent(List<Invoice> invoices) {
		LOGGER.debug("entered isPaidStateInvoicePresent invoices:" + invoices);
		try {
			if (invoices != null && invoices.size() > 0) {
				Iterator<Invoice> invoicesItr = invoices.iterator();
				while (invoicesItr.hasNext()) {
					Invoice invoice = invoicesItr.next();
					String state = invoice != null && StringUtils.isNotBlank(invoice.getState()) ? invoice.getState()
							: null;
					if (StringUtils.isNotBlank(state)) {
						if (state.equals(Constants.INVOICE_STATE_PAID)
								|| state.equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
							return true;
						}
					}
				}
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("error in Invoice parser isPaidStateInvoicePresent invoices:" + invoices, e);
		} finally {
			LOGGER.debug("exited isPaidStateInvoicePresent invoices:" + invoices);
		}
		return true;
	}

	public static List<Invoice> prepareInvoiceDashboardResponse(List<Invoice> invoiceLst) {
		List<Invoice> result = new ArrayList<Invoice>();
		try {
			if (invoiceLst == null || invoiceLst.size() == 0) {
				return result;
			}
			Iterator<Invoice> invoiceLstItr = invoiceLst.iterator();
			while (invoiceLstItr.hasNext()) {
				Invoice invoice = invoiceLstItr.next();
				Invoice invoiceResponse = new Invoice();

				Timestamp invoice_date = convertStringToTimeStamp(invoice.getInvoice_date(),
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);
				Timestamp due_date = convertStringToTimeStamp(invoice.getDue_date(),
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);

				Timestamp created_at = convertStringToTimeStamp(invoice.getCreated_at(),
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);

				Timestamp last_updated_at = convertStringToTimeStamp(invoice.getLast_updated_at(),
						Constants.TIME_STATMP_TO_INVOICE_FORMAT);

				invoiceResponse.setInvoice_date(invoice_date.toString());
				invoiceResponse.setDue_date(due_date.toString());
				invoiceResponse.setCreated_at(created_at.toString());
				invoiceResponse.setLast_updated_at(last_updated_at.toString());
				result.add(invoiceResponse);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage());
		}
		return result;
	}

	public static String getInvoiceIds(List<Invoice> invoices) {
		try {
			LOGGER.debug("entered getInvoiceIds(List<Invoice> invoices" + invoices + ")");
			if (invoices == null || invoices.isEmpty()) {
				return null;
			}
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < invoices.size(); i++) {
				result.append("'").append(invoices.get(i).getId()).append("',");
			}
			return result.substring(0, result.length() - 1);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited getInvoiceIds(List<Invoice> invoices" + invoices + ")");
		}
	}

	public static InvoiceHistory getInvoice_history(Invoice invoice, String id, String user_id, String companyId) {
		try {
			LOGGER.debug("entered getInvoice_history(Invoice invoice:" + invoice + " String id:" + id
					+ ", String user_id:" + user_id + ",String companyId:" + companyId + ")");
			if (invoice != null) {
				InvoiceHistory invoiceHistory = new InvoiceHistory();
				invoiceHistory.setAction(invoice.getState());
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				invoiceHistory.setAction_at(timestamp.toString());
				invoiceHistory.setCompany_id(companyId);
				invoiceHistory.setCreated_at(timestamp.toString());
				invoiceHistory.setCreated_by(user_id);
				invoiceHistory.setEmail_from(invoice.getFrom());
				invoiceHistory.setEmail_subject(invoice.getSubject());
				if (invoice.isSendMail()) {
					invoiceHistory.setEmail_to(new JSONArray(invoice.getRecepientsMails()).toString());
				}
				invoiceHistory.setId(id);
				invoiceHistory.setInvoice_id(invoice.getId());
				invoiceHistory.setLast_updated_at(timestamp.toString());
				invoiceHistory.setLast_updated_by(user_id);
				invoiceHistory.setUser_id(user_id);
				invoiceHistory.setAmount(invoice.getAmount());
				invoiceHistory.setAmount_by_date(invoice.getAmount_by_date());
				invoiceHistory.setAmount_due(invoice.getAmount_due());
				invoiceHistory.setAmount_paid(invoice.getAmount_paid());
				invoiceHistory.setTax_amount(invoice.getTax_amount());
				invoiceHistory.setCurrency(invoice.getCurrency());
				invoiceHistory.setSub_totoal(invoice.getSub_total());
				invoiceHistory.setAction_at_mills(new Date().getTime());
				return invoiceHistory;
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited getInvoice_history(Invoice invoice" + invoice + " String id:" + id
					+ ", String user_id:" + user_id + ",String companyId:" + companyId + ")");
		}
		return null;
	}

	public static InvoiceHistory getInvoice_history(Invoice invoice, String id, String user_id, String companyId,
			String emailState, String email) {
		try {
			LOGGER.debug("entered getInvoice_history(Invoice invoice:" + invoice + " String id:" + id
					+ ", String user_id:" + user_id + ",String companyId:" + companyId + " String emailState:"
					+ emailState + " String email:" + email + ")");
			if (invoice != null) {
				InvoiceHistory invoiceHistory = new InvoiceHistory();
				invoiceHistory.setAction(emailState);
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				invoiceHistory.setAction_at(timestamp.toString());
				invoiceHistory.setCompany_id(companyId);
				invoiceHistory.setCreated_at(timestamp.toString());
				invoiceHistory.setCreated_by(user_id);
				invoiceHistory.setEmail_from(invoice.getFrom());
				invoiceHistory.setEmail_subject(invoice.getSubject());
				invoiceHistory.setEmail_to(email);
				invoiceHistory.setId(id);
				invoiceHistory.setInvoice_id(invoice.getId());
				invoiceHistory.setLast_updated_at(timestamp.toString());
				invoiceHistory.setLast_updated_by(user_id);
				invoiceHistory.setUser_id(user_id);
				invoiceHistory.setAction_at_mills(new Date().getTime());
				return invoiceHistory;
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited getInvoice_history(Invoice invoice" + invoice + " String id:" + id
					+ ", String user_id:" + user_id + ",String companyId:" + companyId + " String emailState:"
					+ emailState + " String email:" + email + ")");
		}
		return null;
	}

	public static List<InvoiceHistory> getInvoice_historys(List<String> invoiceIds, String user_id, String companyId,
			boolean markAsSent, String state) {
		try {
			List<InvoiceHistory> result = null;
			LOGGER.debug("entered getInvoice_history(List<String> invoiceIds:" + invoiceIds + ", String user_id:"
					+ user_id + ",String companyId:" + companyId + " boolean markAsSent:" + markAsSent + ")");
			if (invoiceIds != null && !invoiceIds.isEmpty()) {
				result = new ArrayList<InvoiceHistory>();
				for (int i = 0; i < invoiceIds.size(); i++) {
					String invoiceId = invoiceIds.get(i);
					InvoiceHistory invoiceHistory = new InvoiceHistory();
					invoiceHistory.setAction(state);
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					invoiceHistory.setAction_at(timestamp.toString());
					invoiceHistory.setCompany_id(companyId);
					invoiceHistory.setCreated_at(timestamp.toString());
					invoiceHistory.setCreated_by(user_id);
					invoiceHistory.setEmail_from(null);
					invoiceHistory.setEmail_subject(null);
					invoiceHistory.setEmail_to(null);
					invoiceHistory.setId(UUID.randomUUID().toString());
					invoiceHistory.setInvoice_id(invoiceId);
					invoiceHistory.setLast_updated_at(timestamp.toString());
					invoiceHistory.setLast_updated_by(user_id);
					invoiceHistory.setUser_id(user_id);
					invoiceHistory.setAction_at_mills(new Date().getTime());
					if (markAsSent) {
						invoiceHistory.setDescription(PropertyManager.getProperty("invoice.history.mark.as.sent"));
					}
					result.add(invoiceHistory);
				}
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			LOGGER.debug("exited getInvoice_history(List<String> invoiceIds" + invoiceIds + ", String user_id:"
					+ user_id + ",String companyId:" + companyId + " boolean markAsSent:" + markAsSent + ")");
		}
		return null;
	}

	public static boolean deleteInvoivceCommissionBill(InvoiceCommission invoiceCommission) throws Exception {
		try {
			LOGGER.debug("entered deleteInvoivceCommissionBill(invoiceCommission:" + invoiceCommission);
			String apServiceUrl = LTMUtils.getHostAddress("half.service.docker.apservice.hostname",
					"half.service.docker.apservice.port");
			if (StringUtils.isBlank(apServiceUrl)) {
				LOGGER.fatal("ltm invoice->apserivce not present ltm url:" + apServiceUrl);
				return false;
			}
			// apServiceUrl = "https://dev-services.qount.io/";
			if (StringUtils.isAnyBlank(invoiceCommission.getUser_id(), invoiceCommission.getCompany_id(),
					invoiceCommission.getBill_id())) {
				throw new WebApplicationException(
						PropertyManager
								.getProperty("error.invoice.commission.delete.billl.empty.userid.companyid.billId"),
						Constants.INVALID_INPUT);
			}
			apServiceUrl += "BigPayServices/user/" + invoiceCommission.getUser_id() + "/companies/"
					+ invoiceCommission.getCompany_id() + "/bills/" + invoiceCommission.getBill_id();
			String result = HTTPClient.delete(apServiceUrl);
			if (StringUtils.isNotBlank(result)) {
				JSONObject resultObj = new JSONObject(result);
				if (CommonUtils.isValidJSON(resultObj)) {
					String message = resultObj.optString("message");
					if (StringUtils.isNotBlank(message) && message.equals("Success")) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error in deleteInvoivceCommissionBill", e);
			throw e;
		} finally {
			LOGGER.debug("exited deleteInvoivceCommissionBill(invoiceCommission:" + invoiceCommission);
		}
		return false;
	}

	public static boolean createInvoiceCommisionBill(InvoiceCommission invoiceCommission, String id) {
		try {
			LOGGER.debug("entered createInvoiceCommisionBill invoiceCommission:" + invoiceCommission);
			JSONObject billsJson = getInvoiceCommissionJson(invoiceCommission, id);
			String apServiceUrl = LTMUtils.getHostAddress("half.service.docker.apservice.hostname",
					"half.service.docker.apservice.port");
			if (StringUtils.isBlank(apServiceUrl)) {
				LOGGER.fatal("ltm invoice->apserivce not present ltm url:" + apServiceUrl);
				return false;
			}
			// apServiceUrl = "https://dev-services.qount.io/";
			apServiceUrl += "BigPayServices/user/" + invoiceCommission.getUser_id() + "/companies/"
					+ invoiceCommission.getCompany_id() + "/bills";
			JSONObject result = HTTPClient.post(apServiceUrl, billsJson.toString());
			if (CommonUtils.isValidJSON(result)) {
				String status = result.optString("status");
				if (StringUtils.isNotBlank(status) && status.equals("Failure")) {
					String message = result.optString("message");
					throw new WebApplicationException(message, 417);
				}
				invoiceCommission.setBillCreated(true);
				return true;
			}
		} catch (WebApplicationException e) {
			LOGGER.debug("WebApplicationException in createInvoiceCommisionBill", e);
			throw e;
		} catch (Exception e) {
			LOGGER.debug("error in createInvoiceCommisionBill", e);
		} finally {
			LOGGER.debug("exited createInvoiceCommisionBill invoiceCommission:" + invoiceCommission);
		}
		return false;
	}

	private static JSONObject getInvoiceCommissionJson(InvoiceCommission invoiceCommision, String id) {
		try {
			LOGGER.debug("entered getInvoiceCommissionJson invoiceCommision:" + invoiceCommision);
			if (invoiceCommision == null) {
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.empty"),
						Constants.INVALID_INPUT);
			}
			if (StringUtils.isAnyBlank(invoiceCommision.getUser_id(), invoiceCommision.getCompany_id())) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.userid.companyid"),
						Constants.INVALID_INPUT);
			}
			String eventType = invoiceCommision.getEvent_type();
			if (StringUtils.isBlank(eventType)) {
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.event.type"),
						Constants.INVALID_INPUT);
			}
			if (!eventType.equals(Constants.STRING) && !eventType.equals(Constants.DATE)) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.event.type.value"),
						Constants.INVALID_INPUT);
			}
			String eventAt = invoiceCommision.getEvent_at();
			if (StringUtils.isBlank(eventAt)) {
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.empty.eventAt"),
						Constants.INVALID_INPUT);
			}
			if (invoiceCommision.getInvoice_amount() <= 0.0) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.invoice.amount"),
						Constants.INVALID_INPUT);
			}
			if (StringUtils.isBlank(invoiceCommision.getInvoice_number())) {
				// invoice number will be used for bill number
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.number"),
						Constants.INVALID_INPUT);
			}
			String title = String.format(PropertyManager.getProperty("invoice.commission.bill.title"),
					invoiceCommision.getInvoice_number());
			id = StringUtils.isNotBlank(id) ? id : UUID.randomUUID().toString();
			invoiceCommision.setId(id);
			invoiceCommision.setBill_id(id);
			// every invoice commission will have bill with only one line
			invoiceCommision.setBillLineId(id);
			String currentDate = DateUtils.getCurrentDate(Constants.DATE_TO_COMMISSION_BILLS_UI_DATE_FORMAT);
			JSONArray lines = new JSONArray();
			JSONObject lineObj = new JSONObject();
			String itemName = invoiceCommision.getItem_name();
			if (StringUtils.isEmpty(itemName)) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.empty.itemname"),
						Constants.INVALID_INPUT);
			}
			lines.put(lineObj);
			lineObj.put("quantity", 1);
			String amountType = invoiceCommision.getAmount_type();
			if (StringUtils.isEmpty(amountType)) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.empty.amountType"),
						Constants.INVALID_INPUT);
			}
			if (!amountType.equals(Constants.PERCENTAGE) && !amountType.equals(Constants.FLAT_FEE)) {
				throw new WebApplicationException(
						PropertyManager.getProperty("error.invoice.commission.invalid.amountType"),
						Constants.INVALID_INPUT);
			}
			if (invoiceCommision.getAmount() <= 0.0) {
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.commission.percentage"),
						Constants.INVALID_INPUT);
			}
			double invoiceCommissionAmount = 0.0;
			if (invoiceCommision.getAmount_type().equals(Constants.PERCENTAGE)) {
				invoiceCommissionAmount = invoiceCommision.getInvoice_amount() * (invoiceCommision.getAmount() / 100);
			} else if (invoiceCommision.getAmount_type().equals(Constants.FLAT_FEE)) {
				invoiceCommissionAmount = invoiceCommision.getAmount();
			}
			lineObj.put("unitPrice", invoiceCommissionAmount);
			lineObj.put("amount", invoiceCommissionAmount);
			lineObj.put("itemCode", itemName);
			lineObj.put("billLineId", invoiceCommision.getBillLineId());
			JSONObject apServiceInputJson = new JSONObject();
			apServiceInputJson.put("title", title);
			apServiceInputJson.put("vendorID", invoiceCommision.getVendor_id());
			apServiceInputJson.put("amount", invoiceCommissionAmount);
			apServiceInputJson.put("companyID", invoiceCommision.getCompany_id());
			apServiceInputJson.put("id", invoiceCommision.getId());
			if (eventType.equals(Constants.DATE)) {
				String eventDate = invoiceCommision.getEvent_date();
				if (StringUtils.isBlank(eventDate)) {
					throw new WebApplicationException(
							PropertyManager.getProperty("error.invoice.commission.empty.eventDate"),
							Constants.INVALID_INPUT);
				}
				apServiceInputJson.put("dueDate", eventDate);
			} else {
				apServiceInputJson.put("dueDate", currentDate);
			}
			apServiceInputJson.put("term", "custom");
			apServiceInputJson.put("billDate", currentDate);
			apServiceInputJson.put("recurring", "onlyonce");
			apServiceInputJson.put("currency", invoiceCommision.getCurrency());
			apServiceInputJson.put("billID", "Sales Commission " + invoiceCommision.getInvoice_number());
			apServiceInputJson.put("action", "submit");
			apServiceInputJson.put("lines", lines);
			return apServiceInputJson;
		} catch (WebApplicationException e) {
			LOGGER.error("WebApplicationException in getInvoiceCommissionJson", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error in getInvoiceCommissionJson", e);
		} finally {
			LOGGER.debug("exited getInvoiceCommissionJson invoiceCommision:" + invoiceCommision);
		}
		return null;
	}

	public static long getDateDifference(Date startDate, Date endDate) {
		try {
			long diff = endDate.getTime() - startDate.getTime();
			long result = (diff) / Constants.DAYS_IN_MILLIS;
			return result;
		} catch (Exception e) {
			LOGGER.error("error in getDateDifference(Date startDate, Date endDate):", e);
		}
		return -1;
	}

	public static void main(String[] args) {
		Date d1 = new Date();
		Date d2 = new Date();
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.MONTH, 0);
		cl.set(Calendar.DATE, 31);
		cl.set(Calendar.MILLISECOND, 0);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.HOUR, 0);
		d2.setTime(cl.getTimeInMillis());
		
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.MILLISECOND, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.HOUR, 0);
		d1.setTime(c2.getTimeInMillis());
		
		
		System.out.println(d1);
		System.out.println(d2);
		System.err.println(getDateDifference(d2, d1));
	}
	
	public static String getCommaSeparatedIds(List<Payment> payments){
		try {
			LOGGER.debug("entered getCommaSeparatedIds(List<Payment> payments:"+payments);
			if(payments!=null && !payments.isEmpty()){
				String result = "";
				Iterator<Payment> paymentsItr = payments.iterator();
				while(paymentsItr.hasNext()){
					Payment payment = paymentsItr.next();
					if(payment!=null && StringUtils.isNotBlank(payment.getId())){
						result+="'"+payment.getId()+"',";
					}
				}
				if(StringUtils.isNotBlank(result)){
					result = result.substring(0, result.length()-1);
					return result;
				}
			}
		} catch (Exception e) {
			LOGGER.error("error getCommaSeparatedIds(List<Payment> payments:"+payments,e);
		} finally{
			LOGGER.debug("exited getCommaSeparatedIds(List<Payment> payments:"+payments);
		}
		return null;
	}
	
	public static void mergePayments(List<Payment> payments, Map<String, Double> paidAmountMap, boolean unapplied, boolean invoiceFlag){
		try {
			LOGGER.debug("entered mergePayments(List<Payment> payments:"+payments+", Map<String, Double> paidAmountMap:"+paidAmountMap+" unapplied:"+unapplied+ "invoiceFlag:"+invoiceFlag);
			if(payments==null || payments.isEmpty() || paidAmountMap==null || paidAmountMap.isEmpty()){
				return;
			}
			Iterator<Payment> paymentsItr = payments.iterator();
			while(paymentsItr.hasNext()){
				Payment payment = paymentsItr.next();
				if(payment!=null && StringUtils.isNotBlank(payment.getId())){
					 if (paidAmountMap.get(payment.getId()) == null) {
						 payment.setPayment_applied_amount(0.0);
					} else{
					Double assignedAmount = paidAmountMap.get(payment.getId());
					payment.setPayment_applied_amount(assignedAmount);}
					if(unapplied){
						payment.setPayment_unapplied_amount(payment.getPaymentAmount()!=null?(payment.getPaymentAmount().doubleValue()-payment.getPayment_applied_amount()):0);
						if(invoiceFlag) {
							if(payment.getPaymentAmount().doubleValue()==(payment.getPayment_applied_amount())){
								paymentsItr.remove();
						}	}
							
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error mergePayments(List<Payment> payments:"+payments+", Map<String, Double> paidAmountMap:"+paidAmountMap+" unapplied:"+unapplied+ "invoiceFlag:"+invoiceFlag,e);
		} finally{
			LOGGER.debug("exited mergePayments(List<Payment> payments:"+payments+", Map<String, Double> paidAmountMap:"+paidAmountMap+" unapplied:"+unapplied+ "invoiceFlag:"+invoiceFlag);
		}
	}

	
	public static void calculateCollectionPaymentStatus(Payment payment){
		try{
			LOGGER.debug("entered calculateCollectionPaymentStatus(Payment payment:" + payment);
			if(payment!=null){
				double paymentAmount = payment.getPaymentAmount()!=null?payment.getPaymentAmount().doubleValue():0.0d;
				if(paymentAmount>0.0d){
					List<PaymentLine> lines = payment.getPaymentLines();
					if(lines!=null && !lines.isEmpty()){
						double paymentLinesAppliedAmount = 0.0d;
						Iterator<PaymentLine> lineItr = lines.iterator();
						while(lineItr.hasNext()){
							PaymentLine line = lineItr.next();
							if(line!=null){
								double lineAmount = line.getAmount()!=null?line.getAmount().doubleValue():0.0d;
								if(lineAmount>0){
									paymentLinesAppliedAmount+=lineAmount;
								}else if(lineAmount<=0){
									//removing payment lines from the list to save if they are not applied 
									lineItr.remove();
								}
							}
						}
						if(paymentLinesAppliedAmount == 0.0d){
							payment.setPayment_status(Constants.UNAPPLIED);
						}else if(paymentLinesAppliedAmount == paymentAmount){
							payment.setPayment_status(Constants.APPLIED);
						}else if(paymentLinesAppliedAmount>0 && paymentLinesAppliedAmount<paymentAmount){
							payment.setPayment_status(Constants.PARTIALLY_APPLIED);
						} 
					}
				}
			}
		} catch (Exception e) {
			LOGGER.debug("error calculateCollectionPaymentStatus(Payment payment:" + payment, e);
		} finally{
			LOGGER.debug("exited calculateCollectionPaymentStatus(Payment payment:" + payment);
		}
	}
	
	public static String getDisplayState(String state) {
		try {
			if (StringUtils.isNotBlank(state)) {
				if (state.equalsIgnoreCase(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					return Constants.PARTIALLY_PAID;
				}
				return WordUtils.capitalize(state);
			}
		} catch (Exception e) {
			LOGGER.error("error parsing display state:",e);
		}
		return null;
	}
}
