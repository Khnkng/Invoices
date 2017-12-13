package com.qount.invoice.helper;

import java.text.ParseException;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.Utilities;

public class LateFeeHelper {
	
	private static final Logger LOGGER = Logger.getLogger(LateFeeHelper.class);
	
	public static void handleLateFeeJEChanges(Invoice dbInvoice, Invoice invoiceObj) {
		try {
			LOGGER.debug("entered handleLateFeeJEChanges dbInvoice:" + dbInvoice + " UIinvoiceObj:" + invoiceObj);
			// late fee is applied only for sent and partially paid
			if (dbInvoice.getState().endsWith(Constants.INVOICE_STATE_SENT) || dbInvoice.getState().endsWith(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
				// creating journal if late fee removed
				if (StringUtils.isNotEmpty(dbInvoice.getLate_fee_id()) && StringUtils.isBlank(invoiceObj.getLate_fee_id())) {
					deleteLateFeeJournal(Constants.INVOICE, dbInvoice.getId(), dbInvoice.getLate_fee_id(), dbInvoice.getLate_fee_amount(), dbInvoice.getUser_id(), dbInvoice.getCompany_id());
					deleteJournalJobId(dbInvoice.getJournal_job_id());
				}
				if (StringUtils.isNotBlank(invoiceObj.getLate_fee_id())) {
					// creating journal if late fee added
					if (StringUtils.isBlank(dbInvoice.getLate_fee_id()) && StringUtils.isNotBlank(invoiceObj.getLate_fee_id())) {
						scheduleJournalForLateFee(invoiceObj);
					}
					// if due date is changed
					if (!invoiceObj.getDue_date().equals(dbInvoice.getDue_date())) {
						deleteLateFeeJournal(Constants.INVOICE, dbInvoice.getId(), dbInvoice.getLate_fee_id(), dbInvoice.getLate_fee_amount(), dbInvoice.getUser_id(), dbInvoice.getCompany_id());
						deleteJournalJobId(dbInvoice.getJournal_job_id());
						scheduleJournalForLateFee(invoiceObj);
					}
					// if late fee is changed
					if (!invoiceObj.getLate_fee_id().equals(dbInvoice.getLate_fee_id())) {
						deleteLateFeeJournal(Constants.INVOICE, dbInvoice.getId(), dbInvoice.getLate_fee_id(), dbInvoice.getLate_fee_amount(), dbInvoice.getUser_id(), dbInvoice.getCompany_id());
						deleteJournalJobId(dbInvoice.getJournal_job_id());
						scheduleJournalForLateFee(invoiceObj);
					}
					// if invoice amount is changed
					double uiInvoiceAmount = invoiceObj.getSub_total()+invoiceObj.getTax_amount()+ invoiceObj.getLate_fee_amount();
					if (uiInvoiceAmount != dbInvoice.getAmount()) {
						deleteLateFeeJournal(Constants.INVOICE, dbInvoice.getId(), dbInvoice.getLate_fee_id(), dbInvoice.getLate_fee_amount(), dbInvoice.getUser_id(), dbInvoice.getCompany_id());
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error handleLateFeeJEChanges", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error handleLateFeeJEChanges", e);
		} finally {
			LOGGER.debug("exited handleLateFeeJEChanges dbInvoice:" + dbInvoice + " UIinvoiceObj:" + invoiceObj);
		}
	}

	
	public static void deleteLateFeeJournal(String source, String sourceID, String lateFeeId, double lateFeeAmount, String userId, String companyId) {
		try {
			LOGGER.debug("entered deleteLateFeeJournal(String source:"+source+", String sourceID:"+sourceID+", String lateFeeId:"+lateFeeId+", String lateFeeAmount:"+lateFeeAmount+", String userID:"+userId+", String companyId:"+companyId);
			JSONObject journalObj = new JSONObject();
			JSONObject journalLateFeeObj = new JSONObject();
			journalObj.put("source", source);
			journalObj.put("sourceID", sourceID);
			journalLateFeeObj.put("id", lateFeeId);
			journalLateFeeObj.put("amount", lateFeeAmount);
			journalObj.put("lateFee", journalLateFeeObj);
			CommonUtils.createJournal(journalObj.toString(), userId, companyId);
		} catch (WebApplicationException e) {
			LOGGER.error("error deleteLateFeeJournal", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error deleteLateFeeJournal", e);
		} finally {
			LOGGER.debug("exited deleteLateFeeJournal(String source:"+source+", String sourceID:"+sourceID+", String lateFeeId:"+lateFeeId+", String lateFeeAmount:"+lateFeeAmount+", String userID:"+userId+", String companyId:"+companyId);
		}
	}
	
	public static boolean deleteJournalJobId(String jobId) {
		try {
			LOGGER.debug("entered deleteJournalJobId jobId:" + jobId);
			if (StringUtils.isEmpty(jobId)) {
				return false;
			}
			LOGGER.debug("unscheduling job: " + jobId);
			String remainderServieUrl = Utilities.getLtmUrl(PropertyManager.getProperty("remainder.service.docker.hostname"),
					PropertyManager.getProperty("remainder.service.docker.port"));
			LOGGER.debug("unscheduling job url:" + remainderServieUrl);
			remainderServieUrl += "RemainderService/journal/unschedule/" + jobId;
			String result = HTTPClient.delete(remainderServieUrl);
			LOGGER.debug("unscheduling result:" + result);
			if (StringUtils.isNotBlank(result) && result.trim().equalsIgnoreCase("true")) {
				return true;
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error deleteJournalJobId", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error deleteJournalJobId", e);
		} finally {
			LOGGER.debug("exited deleteJournalJobId jobId:" + jobId);
		}
		return false;
	}
	
	public static String scheduleJournalForLateFee(Invoice invoice) throws Exception{
		try {
			LOGGER.debug("entered scheduleJournalForLateFee invoice:" + invoice);
			String startDate = InvoiceParser.convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			if (StringUtils.isBlank(startDate)) {
				throw new WebApplicationException(PropertyManager.getProperty("error.invoice.journal.startDate"), Constants.INVALID_INPUT);
			}
			if (StringUtils.isNotBlank(invoice.getLate_fee_id())) {
				if (invoice.getState().equals(Constants.INVOICE_STATE_SENT) || invoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					JSONObject journalJobPayloadObj = new JSONObject();
					journalJobPayloadObj.put("source", "invoiceLateFee");
					journalJobPayloadObj.put("sourceID", invoice.getId());
					journalJobPayloadObj.put("startDate", startDate);
					journalJobPayloadObj.put("userID", invoice.getUser_id());
					journalJobPayloadObj.put("companyID", invoice.getCompany_id());
					journalJobPayloadObj.put("invoiceID", invoice.getId());
					journalJobPayloadObj.put("lateFeeID", invoice.getLate_fee_id());
					journalJobPayloadObj.put("invoiceAmount", invoice.getAmount());
					LOGGER.debug("journalJobPayloadObj:" + journalJobPayloadObj);
					String remainderServieUrl = Utilities.getLtmUrl(PropertyManager.getProperty("remainder.service.docker.hostname"),
							PropertyManager.getProperty("remainder.service.docker.port"));
					remainderServieUrl += "RemainderService/journal/schedule";
					LOGGER.debug("remainderServieUrl::" + remainderServieUrl);
					Object jobIdObj = HTTPClient.postObject(remainderServieUrl, journalJobPayloadObj.toString());
					return jobIdObj.toString();
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error scheduleJournalForLateFee", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error scheduleJournalForLateFee", e);
			throw e;
		} finally {
			LOGGER.debug("exited scheduleJournalForLateFee invoice:" + invoice);
		}
		return null;
	}
	
	public double getLateFeeAmount(double lateFeeAmount, double invoiceAmount){
		return (invoiceAmount*(lateFeeAmount/100));
	}
	
	public static void main(String[] args) throws ParseException {
		String date = "2017-12-01 00:00:00.0";
		String result = InvoiceParser.convertTimeStampToString(date, Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
//		SimpleDateFormat from = Constants.DB_DUE_DATE_FORMAT;
//		SimpleDateFormat to = Constants.TIME_STATMP_TO_INVOICE_FORMAT;
//		System.out.println(date);
//		Date temp = from.parse(date);
//		System.out.println(temp);
//		String result = to.format(temp).toString();
		System.out.println(result);
	}
}
