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
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.Utilities;

/**
 * 
 * @author MateenAhmed 14th Dec 2017
 */
public class LateFeeHelper {

	private static final Logger LOGGER = Logger.getLogger(LateFeeHelper.class);

	public static void handleLateFeeJEChanges(Invoice dbInvoice, Invoice invoiceObj) {
		try {
			LOGGER.debug("entered handleLateFeeJEChanges dbInvoice:" + dbInvoice + " UIinvoiceObj:" + invoiceObj);
			// late fee is applied only for sent and partially paid
			if (dbInvoice.getState().endsWith(Constants.INVOICE_STATE_SENT) || dbInvoice.getState().endsWith(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
				// creating journal if late fee removed
				if (StringUtils.isNotBlank(dbInvoice.getLate_fee_id()) && StringUtils.isBlank(invoiceObj.getLate_fee_id())) {
					LOGGER.debug("late fee removed:");
					deleteLateFeeJournal(dbInvoice);
					String historyAction = String.format(PropertyManager.getProperty("invoice.history.latefee.removed"),
							StringUtils.isEmpty(dbInvoice.getLate_fee_name()) ? dbInvoice.getLate_fee_id() : dbInvoice.getLate_fee_name());
					InvoiceHistoryHelper.updateInvoiceHisotryAction(invoiceObj, historyAction);
				}
				if (StringUtils.isNotBlank(invoiceObj.getLate_fee_id())) {
					// creating journal if late fee added
					if (StringUtils.isBlank(dbInvoice.getLate_fee_id())) {
						LOGGER.debug("late fee added:");
						scheduleJournalForLateFee(invoiceObj);
						String historyAction = String.format(PropertyManager.getProperty("invoice.history.latefee.added"),
								StringUtils.isEmpty(invoiceObj.getLate_fee_name()) ? invoiceObj.getLate_fee_id() : invoiceObj.getLate_fee_name());
						InvoiceHistoryHelper.updateInvoiceHisotryAction(invoiceObj, historyAction);
					}
					// if due date is changed
					String dueDateTemp = InvoiceParser.convertTimeStampToString(invoiceObj.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
							Constants.TIME_STATMP_TO_INVOICE_FORMAT);
					if (!dueDateTemp.equals(dbInvoice.getDue_date())) {
						LOGGER.debug("due date changed:");
						deleteLateFeeJournal(dbInvoice);
						scheduleJournalForLateFee(invoiceObj);
					}
					// if late fee is changed
					if (StringUtils.isNotBlank(dbInvoice.getLate_fee_id()) && !invoiceObj.getLate_fee_id().equals(dbInvoice.getLate_fee_id())) {
						LOGGER.debug("late fee changed:");
						deleteLateFeeJournal(dbInvoice);
						scheduleJournalForLateFee(invoiceObj);
						String historyAction = String.format(PropertyManager.getProperty("invoice.history.latefee.changed"),
								StringUtils.isEmpty(dbInvoice.getLate_fee_name()) ? dbInvoice.getLate_fee_id() : dbInvoice.getLate_fee_name(),
								StringUtils.isEmpty(invoiceObj.getLate_fee_name()) ? invoiceObj.getLate_fee_id() : invoiceObj.getLate_fee_name());
						InvoiceHistoryHelper.updateInvoiceHisotryAction(invoiceObj, historyAction);
					}
					// if invoice amount is changed
					double uiInvoiceAmount = invoiceObj.getSub_total() + invoiceObj.getTax_amount() + invoiceObj.getLate_fee_amount();
					if (uiInvoiceAmount != dbInvoice.getAmount()) {
						LOGGER.debug("invoice amount changed:");
						deleteLateFeeJournal(dbInvoice);
						scheduleJournalForLateFee(invoiceObj);
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error handleLateFeeJEChanges", e);
		} catch (Exception e) {
			LOGGER.error("error handleLateFeeJEChanges", e);
		} finally {
			LOGGER.debug("exited handleLateFeeJEChanges dbInvoice:" + dbInvoice + " UIinvoiceObj:" + invoiceObj);
		}

	}

	public static String deleteLateFeeJournal(Invoice invoice) throws Exception{
		try {
			LOGGER.debug("entered deleteLateFeeJournal invoice:" + invoice);
			if(invoice==null){
				throw new WebApplicationException(Constants.PRECONDITION_FAILED_STR, Constants.INVALID_INPUT);
			}
			if (StringUtils.isNotBlank(invoice.getLate_fee_id())) {
				if (invoice.getState().equals(Constants.INVOICE_STATE_SENT) || invoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					JSONObject journalJobPayloadObj = new JSONObject();
					journalJobPayloadObj.put("jobId", invoice.getJournal_job_id());
					journalJobPayloadObj.put("userID", invoice.getUser_id());
					journalJobPayloadObj.put("companyID", invoice.getCompany_id());
					journalJobPayloadObj.put("invoiceID", invoice.getId());
					journalJobPayloadObj.put("lateFeeID", invoice.getLate_fee_id());
					journalJobPayloadObj.put("lateFeeAmount", invoice.getLate_fee_amount());
					journalJobPayloadObj.put("late_fee_journal_id", invoice.getLate_fee_journal_id());
					journalJobPayloadObj.put("amount_due", invoice.getAmount_due());
					journalJobPayloadObj.put("invoiceAmount", invoice.getAmount());
					journalJobPayloadObj.put("sub_totoal", invoice.getSub_total());
					journalJobPayloadObj.put("tax_amount", invoice.getTax_amount());
					LOGGER.debug("journalJobPayloadObj:" + journalJobPayloadObj);
					String remainderServieUrl = Utilities.getLtmUrl(PropertyManager.getProperty("remainder.service.docker.hostname"),
							PropertyManager.getProperty("remainder.service.docker.port"));
					// remainderServieUrl = "https://dev-services.qount.io/";
					remainderServieUrl += "RemainderService/journal/unschedule";
					LOGGER.debug("remainderServieUrl::" + remainderServieUrl);
					Object jobIdObj = HTTPClient.postObject(remainderServieUrl, journalJobPayloadObj.toString());
					return jobIdObj.toString();
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("error deleteLateFeeJournal", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("error deleteLateFeeJournal", e);
			throw e;
		} finally {
			LOGGER.debug("exited deleteLateFeeJournal invoice:" + invoice);
		}
		return null;
	}


	public static String scheduleJournalForLateFee(Invoice invoice) throws Exception {
		try {
			LOGGER.debug("entered scheduleJournalForLateFee invoice:" + invoice);
			String startDate = InvoiceParser.convertTimeStampToString(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			if (StringUtils.isBlank(startDate)) {
				startDate = invoice.getDue_date();
				if (StringUtils.isBlank(startDate)) {
					throw new WebApplicationException(PropertyManager.getProperty("error.invoice.journal.startDate"), Constants.INVALID_INPUT);
				}
			}
			if (StringUtils.isNotBlank(invoice.getLate_fee_id())) {
				if (invoice.getState().equals(Constants.INVOICE_STATE_SENT) || invoice.getState().equals(Constants.INVOICE_STATE_PARTIALLY_PAID)) {
					JSONObject journalJobPayloadObj = new JSONObject();
					journalJobPayloadObj.put("startDate", startDate);
					journalJobPayloadObj.put("userID", invoice.getUser_id());
					journalJobPayloadObj.put("companyID", invoice.getCompany_id());
					journalJobPayloadObj.put("invoiceID", invoice.getId());
					journalJobPayloadObj.put("lateFeeID", invoice.getLate_fee_id());
					journalJobPayloadObj.put("invoiceAmount", invoice.getAmount());
					journalJobPayloadObj.put("amount_due", invoice.getAmount_due());
					journalJobPayloadObj.put("sub_totoal", invoice.getSub_total());
					LOGGER.debug("journalJobPayloadObj:" + journalJobPayloadObj);
					String remainderServieUrl = Utilities.getLtmUrl(PropertyManager.getProperty("remainder.service.docker.hostname"),
							PropertyManager.getProperty("remainder.service.docker.port"));
//					 remainderServieUrl = "https://dev-services.qount.io/";
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

	public double getLateFeeAmount(double lateFeeAmount, double invoiceAmount) {
		return (invoiceAmount * (lateFeeAmount / 100));
	}

	public static void main(String[] args) throws ParseException {
		JSONObject journalJobPayloadObj = new JSONObject();
		String asdf = null;
		journalJobPayloadObj.put("jobId",asdf);
		System.out.println(journalJobPayloadObj);

	}
}
