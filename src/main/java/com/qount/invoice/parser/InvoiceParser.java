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

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
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

	public static Invoice getInvoiceObj(String userId, Invoice invoice, String companyID) {
		try {
			if (StringUtils.isEmpty(userId) || invoice == null || StringUtils.isEmpty(companyID) || StringUtils.isEmpty(invoice.getCurrency())) {
				return null;
			}
			UserCompany userCompany = null;
			userCompany = CommonUtils.getCompany(userId, companyID);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp invoice_date = convertStringToTimeStamp(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			Timestamp payment_date = convertStringToTimeStamp(invoice.getPayment_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			invoice.setUser_id(userId);
			if (StringUtils.isBlank(invoice.getId())) {
				invoice.setId(UUID.randomUUID().toString());
			}
			invoice.setInvoice_date(invoice_date != null ? invoice_date.toString() : null);
			invoice.setPayment_date(payment_date != null ? payment_date.toString() : null);
			invoice.setLast_updated_at(timestamp != null ? timestamp.toString() : null);
			invoice.setLast_updated_by(userId);
			invoice.setAmount_due(invoice.getAmount());
			invoice.setAmount_paid(0);
			setInvoiceAmountByDate(invoice, userCompany);
			List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
			if (invoiceLines == null) {
				invoiceLines = new ArrayList<>();
			}
			Iterator<InvoiceLine> invoiceLineItr = invoiceLines.iterator();
			while (invoiceLineItr.hasNext()) {
				InvoiceLine line = invoiceLineItr.next();
				if (StringUtils.isBlank(line.getId())) {
					line.setId(UUID.randomUUID().toString());
				}
				line.setInvoice_id(invoice.getId());
				line.setLast_updated_at(timestamp.toString());
				line.setLast_updated_by(userId);
			}
			invoice.setCreated_at(new Timestamp(System.currentTimeMillis()).toString());
			invoice.setRecepientsMailsArr(CommonUtils.getJsonArrayFromList(invoice.getRecepientsMails()));
			if (invoice.getPaymentSpringPlan() == null) {
				invoice.setPaymentSpringPlan(new PaymentSpringPlan());
			} else {
				PaymentSpringPlan paymentSpringPlan = invoice.getPaymentSpringPlan();
				if (paymentSpringPlan != null) {
					if (StringUtils.equals(paymentSpringPlan.getFrequency(), "daily")) {
						if (!CommonUtils.isValidStrings(paymentSpringPlan.getAmount(), paymentSpringPlan.getEnds_after(), paymentSpringPlan.getFrequency(),
								paymentSpringPlan.getName())) {
							throw new WebApplicationException(PropertyManager.getProperty("payment.spring.daily.invalid.plan.msg"));
						}
					} else {
						if (!CommonUtils.isValidStrings(paymentSpringPlan.getAmount(), paymentSpringPlan.getEnds_after(), paymentSpringPlan.getFrequency(),
								paymentSpringPlan.getName())) {
							throw new WebApplicationException(PropertyManager.getProperty("payment.spring.invalid.plan.msg"));
						}
						if (StringUtils.isEmpty(paymentSpringPlan.getDay()) && paymentSpringPlan.getDay_map() == null) {
							throw new WebApplicationException(PropertyManager.getProperty("payment.spring.day.invalid.plan.msg"));
						}
					}
				}
			}
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
			LOGGER.error(e);
		}
		return null;
	}

	public static String convertTimeStampToString(String dateStr, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

	public static Invoice convertTimeStampToString(Invoice invoice) {
		try {
			if (invoice != null) {
				invoice.setInvoice_date(convertTimeStampToString(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice.setPayment_date(convertTimeStampToString(invoice.getPayment_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return invoice;
	}

	private static List<Invoice> convertTimeStampToString(List<Invoice> invoiceLst) {
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				for (int i = 0; i < invoiceLst.size(); i++) {
					Invoice invoice = invoiceLst.get(i);
					if (invoice != null) {
						invoice.setInvoice_date(
								convertTimeStampToString(invoice.getInvoice_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
						invoice.setPayment_date(
								convertTimeStampToString(invoice.getPayment_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return invoiceLst;
	}

	public static List<InvoiceLineTaxes> getInvoiceLineTaxesList(List<InvoiceLine> invoiceLinesList) {
		List<InvoiceLineTaxes> result = new ArrayList<InvoiceLineTaxes>();
		if (invoiceLinesList == null || invoiceLinesList.isEmpty()) {
			return result;
		}
		Iterator<InvoiceLine> invoiceLineItr = invoiceLinesList.iterator();
		while (invoiceLineItr.hasNext()) {
			InvoiceLine invoiceLine = invoiceLineItr.next();
			List<InvoiceLineTaxes> lineTaxesList = invoiceLine.getInvoiceLineTaxes();
			if (lineTaxesList != null && !lineTaxesList.isEmpty()) {
				Iterator<InvoiceLineTaxes> invoiceLineTaxesItr = lineTaxesList.iterator();
				while (invoiceLineTaxesItr.hasNext()) {
					InvoiceLineTaxes invoiceLineTaxes = invoiceLineTaxesItr.next();
					if (invoiceLineTaxes != null && StringUtils.isNotBlank(invoiceLineTaxes.getTax_id())) {
						invoiceLineTaxes.setInvoice_line_id(invoiceLine.getId());
						result.add(invoiceLineTaxes);
					}
				}
			}
		}
		return result;
	}

	public static Invoice getInvoiceObjToDelete(String user_id, String companyID, String invoice_id) {
		try {
			if (StringUtils.isEmpty(user_id) && StringUtils.isEmpty(invoice_id)) {
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
					invoice.setAmount_by_date(invoiceLineDateAmount);
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
			LOGGER.error(e);
		}
		return null;
	}

	public static JSONObject createInvoiceLstResult(List<Invoice> invoiceLst, Map<String, String> badges) {
		JSONObject result = new JSONObject();
		try {
			if (invoiceLst != null && !invoiceLst.isEmpty()) {
				convertTimeStampToString(invoiceLst);
				result.put("invoices", invoiceLst);
			}
			if (badges != null && !badges.isEmpty()) {
				result.put("badges", badges);
			}
		} catch (Exception e) {
			LOGGER.error(e);
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
			invoiceMail.setInvoiceDueDate(invoice.getPayment_date());
			invoiceMail.setInvoiceId(invoice.getId());
			invoiceMail.setInvoiceNumber(invoice.getNumber());
			return invoiceMail;
		} catch (Exception e) {
			LOGGER.error(e);
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
			LOGGER.error(e);
			throw e;
		} finally {
			LOGGER.debug("exited getJsonForPaymentSpringPlan :" + paymentSpringPlan);
		}
	}

	public static void main(String[] args) throws Exception {
		PaymentSpringPlan paymentSpringPlan = new PaymentSpringPlan();
		paymentSpringPlan.setAmount("100");
		paymentSpringPlan.setDay("1");
		paymentSpringPlan.setFrequency("yearly");
		paymentSpringPlan.setName("yearly");
		System.out.println(getJsonForPaymentSpringPlan(paymentSpringPlan));
	}
}
