package com.qount.invoice.controllerImpl;

import java.util.List;
import java.util.UUID;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.service.InvoiceService;

public class InvoiceControllerImpl {

	public static Invoice createInvoice(String userID, String companyID, Invoice invoice) {
		invoice.setCompanyID(companyID);
		invoice.setUserID(userID);
		invoice.setInvoiceID(UUID.randomUUID().toString());
		List<InvoiceLines> invoiceLines = invoice.getInvoiceLines();

		for (InvoiceLines invoiceLine : invoiceLines) {
			invoiceLine.setLineID(UUID.randomUUID().toString());
			invoiceLine.setInvoiceID(invoice.getInvoiceID());
		}
		return InvoiceService.createInvoice(companyID, invoice);
	}

	public static List<Invoice> getInvoices(String userID, String companyID) {
		return InvoiceService.getInvoices(userID, companyID);
	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		invoice.setInvoiceID(invoiceID);
		invoice.setUserID(userID);
		invoice.setCompanyID(companyID);
		List<InvoiceLines> invoiceLines = invoice.getInvoiceLines();
		for (InvoiceLines invoiceLine : invoiceLines) {
			if (invoiceLine.getLineID() == null) {
				invoiceLine.setLineID(UUID.randomUUID().toString());
			}
			invoiceLine.setInvoiceID(invoice.getInvoiceID());
		}
		return InvoiceService.updateInvoice(companyID, invoice, invoiceID);
	}

	public static Invoice getInvoice(String userID, String companyID, String invoiceID) {
		return InvoiceService.getInvoice(userID, companyID, invoiceID);

	}
}
