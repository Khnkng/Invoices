package com.qount.invoice.pdf;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoicePreference;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 10 Feb 2016
 *
 */
public class InvoiceReference {

	private Invoice invoice;
	private InvoicePreference invoicePreference;
	private String invoiceType;

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public InvoicePreference getInvoicePreference() {
		return invoicePreference;
	}

	public void setInvoicePreference(InvoicePreference invoicePreference) {
		this.invoicePreference = invoicePreference;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

}
