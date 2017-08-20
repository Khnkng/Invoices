package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.model.Company;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceMail;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.model.PaymentSpringPlan;
import com.qount.invoice.model.UserCompany;
import com.qount.invoice.pdf.InvoiceReference;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.CurrencyConverter;
import com.qount.invoice.utils.DateUtils;
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
				throw new WebApplicationException("userId, companyId, currency are mandatory");
			}
			UserCompany userCompany = null;
			invoice.setCompany_id(companyID);
			invoice.setIs_recurring(StringUtils.isNotEmpty(invoice.getPlan_id()));
			userCompany = CommonUtils.getCompany(companyID);
			invoice.setCompanyName(userCompany.getName());

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp invoice_date = convertStringToTimeStamp(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			Timestamp payment_date = convertStringToTimeStamp(invoice.getDue_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			invoice.setUser_id(userId);
			if (createFlag) {
				invoice.setId(UUID.randomUUID().toString());
			}
			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			invoice.setDue_date(payment_date != null ? payment_date.toString() : null);
			invoice.setLast_updated_at(timestamp != null ? timestamp.toString() : null);
			invoice.setLast_updated_by(userId);
			invoice.setAmount_due(invoice.getAmount());
			invoice.setAmount_paid(0.00d);
			setInvoiceAmountByDate(invoice, userCompany);
			List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
			if (invoiceLines == null) {
				invoiceLines = new ArrayList<>();
			}
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
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return invoice;
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
			if(StringUtils.isNotBlank(dateStr))
			return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static Invoice convertTimeStampToString(Invoice invoice) {
		try {
			if (invoice != null) {
				invoice.setInvoice_date(convertTimeStampToString(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice.setDue_date(convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return invoice;
	}

	private static List<Invoice> convertTimeStampToString(List<Invoice> invoiceLst) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				for (int i = 0; i < invoiceLst.size(); i++) {
					Invoice invoice = invoiceLst.get(i);
					if (invoice != null) {
						invoice.setInvoice_date(convertTimeStampToString(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						invoice.setDue_date(convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return invoiceLst;
	}
	
	/**
	 * method used to convert invoice amount fields to two decimals
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
						Iterator<InvoiceLine> invoiceLineIterator = invoice.getInvoiceLines()!=null?invoice.getInvoiceLines().iterator():null;
						if(invoiceLineIterator!=null){
							while(invoiceLineIterator.hasNext()){
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
	
	/**
	 * method used to convert invoice amount fields to two decimals
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
				Iterator<InvoiceLine> invoiceLineIterator = invoice.getInvoiceLines()!=null?invoice.getInvoiceLines().iterator():null;
				if(invoiceLineIterator!=null){
					while(invoiceLineIterator.hasNext()){
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
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
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
			String customerId = invoice.getCustomer() == null ? invoice.getCustomer_id() : invoice.getCustomer().getCustomer_id();
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

	public static void formatInvoices(List<Invoice> invoiceLst) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				convertTimeStampToString(invoiceLst);
				convertAmountToDecimal(invoiceLst);
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

	public static void main(String[] args) throws Exception {
		double d = getFourDecimalValue(2.23409d);
		System.out.println(d);
	}

	public static double getTwoDecimalValue(double amount){
		try{
			return Double.valueOf(new DecimalFormat("#.##").format(amount));
		}catch(Exception e){
			LOGGER.error("error in Invoice parser getTwoDecimalValue amount:"+amount,e);
		}
		return 0.00d;
	}
	
	public static double getFourDecimalValue(double amount){
		try{
			return Double.valueOf(new DecimalFormat("#.####").format(amount));
		}catch(Exception e){
			LOGGER.error("error in Invoice parser getFourDecimalValue amount:"+amount,e);
		}
		return 0.00d;
	}
}
