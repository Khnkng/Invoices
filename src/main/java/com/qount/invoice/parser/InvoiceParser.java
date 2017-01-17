package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;

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
			Timestamp start_date = convertStringToTimeStamp(invoice.getStart_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp end_date = convertStringToTimeStamp(invoice.getEnd_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp due_date = convertStringToTimeStamp(invoice.getDue_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT);

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
	
	public static Timestamp convertStringToTimeStamp(String dateStr, SimpleDateFormat sdf) {
		try {
			return new Timestamp(sdf.parse(dateStr).getTime());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
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
