package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;

/**
 * @author Apurva
 * @version 1.0 Jan 11 2017
 */
public class InvoiceParser {
	private static final Logger LOGGER = Logger.getLogger(InvoiceParser.class);

	public static Invoice getInvoiceObj(String userId, Invoice invoice, boolean updateFlag) {
		try {
			if (StringUtils.isEmpty(userId) && invoice == null) {
				return null;
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp invoice_date = convertStringToTimeStamp(invoice.getInvoice_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp acceptance_date = convertStringToTimeStamp(invoice.getAcceptance_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp acceptance_final_date = convertStringToTimeStamp(invoice.getAcceptance_final_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp recurring_start_date = convertStringToTimeStamp(invoice.getRecurring_start_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp recurring_end_date = convertStringToTimeStamp(invoice.getRecurring_end_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);

			invoice.setUser_id(userId);
			if (invoice.getId() == null) {
				invoice.setId(UUID.randomUUID().toString());
			}
			invoice.setInvoice_date(invoice_date.toString());
			invoice.setAcceptance_date(acceptance_date.toString());
			invoice.setAcceptance_final_date(acceptance_final_date.toString());
			invoice.setRecurring_start_date(recurring_start_date.toString());
			invoice.setRecurring_end_date(recurring_end_date.toString());
			invoice.setLast_updated_at(timestamp.toString());
			invoice.setLast_updated_by(userId);
			if (!updateFlag) {
				List<InvoiceLine> invoiceLines = invoice.getInvoiceLines();
				if (invoiceLines == null) {
					return null;
				}
				Iterator<InvoiceLine> invoiceLineItr = invoiceLines.iterator();
				while (invoiceLineItr.hasNext()) {
					InvoiceLine line = invoiceLineItr.next();
					line.setId(UUID.randomUUID().toString());
					line.setInvoice_id(invoice.getId());
					line.setLast_updated_at(timestamp.toString());
					line.setLast_updated_by(userId);
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

	public static List<InvoiceLineTaxes> getInvoiceLineTaxesList(List<InvoiceLine> invoiceLinesList) {
		List<InvoiceLineTaxes> result = new ArrayList<InvoiceLineTaxes>();
		Iterator<InvoiceLine> invoiceLineItr = invoiceLinesList.iterator();
		while (invoiceLineItr.hasNext()) {
			InvoiceLine invoiceLine = invoiceLineItr.next();
			List<InvoiceLineTaxes> lineTaxesList = invoiceLine.getInvoiceLineTaxes();
			if (lineTaxesList != null) {
				Iterator<InvoiceLineTaxes> invoiceLineTaxesItr = lineTaxesList.iterator();
				while (invoiceLineTaxesItr.hasNext()) {
					InvoiceLineTaxes invoiceLineTaxes = invoiceLineTaxesItr.next();
					invoiceLineTaxes.setInvoice_line_id(invoiceLine.getId());
					result.add(invoiceLineTaxes);
				}
			}
		}
		return result;
	}

	public static Invoice getInvoiceObjToDelete(String user_id, String invoice_id) {
		try {
			if (StringUtils.isEmpty(user_id) && StringUtils.isEmpty(invoice_id)) {
				return null;
			}
			Invoice invoice = new Invoice();
			invoice.setUser_id(user_id);
			invoice.setId(invoice_id);
			return invoice;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
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
}
