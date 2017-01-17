package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.utils.CommonUtils;

/**
 * @author Apurva
 * @version 1.0 Jan 11 2017
 */
public class InvoiceParser {
	private static final Logger LOGGER = Logger.getLogger(InvoiceParser.class);

	public static Invoice getInvoiceObj(String userId, Invoice invoice) {
		try {
			if (StringUtils.isEmpty(userId) && invoice == null) {
				return null;
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp start_date = Timestamp.valueOf(invoice.getStart_date());
			Timestamp end_date = Timestamp.valueOf(invoice.getEnd_date());
			Timestamp due_date = Timestamp.valueOf(invoice.getDue_date());

			invoice.setUser_id(userId);
			invoice.setId(UUID.randomUUID().toString());
			invoice.setStart_date(start_date.toString());
			invoice.setEnd_date(end_date.toString());
			invoice.setDue_date(due_date.toString());
			invoice.setLast_updated_at(timestamp.toString());
			invoice.setLast_updated_by(userId);
			List<InvoiceLines> invoiceLines = invoice.getInvoiceLines();

			Iterator<InvoiceLines> invoiceLineItr = invoiceLines.iterator();
			while (invoiceLineItr.hasNext()) {
				InvoiceLines line = invoiceLineItr.next();
				line.setId(UUID.randomUUID().toString());
				line.setInvoice_id(invoice.getId());
				line.setLast_updated_at(timestamp.toString());
				line.setLast_updated_by(userId);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return invoice;
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

	public static InvoiceLines getInvoiceLineObjToDelete(String invoice_id, String invoiceLine_id) {
		try {
			if (StringUtils.isEmpty(invoice_id) && StringUtils.isEmpty(invoiceLine_id)) {
				return null;
			}
			InvoiceLines invoiceLine = new InvoiceLines();
			invoiceLine.setId(invoiceLine_id);
			invoiceLine.setInvoice_id(invoice_id);
			return invoiceLine;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
	}
}
