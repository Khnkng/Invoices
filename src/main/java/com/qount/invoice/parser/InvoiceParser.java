package com.qount.invoice.parser;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

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
	
	public static Invoice getInvoiceObj(Invoice invoice){
		try {
			invoice.setId(UUID.randomUUID().toString());
			List<InvoiceLines> invoiceLines = invoice.getInvoiceLines();

			for (InvoiceLines invoiceLine : invoiceLines) {
				invoiceLine.setLineID(UUID.randomUUID().toString());
				invoiceLine.setInvoiceID(invoice.getId());
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return invoice;
		
	}
}
