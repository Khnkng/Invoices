package com.qount.invoice.pdf;

import com.qount.invoice.model.Company;
import com.qount.invoice.model.Customer;
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
	private Customer customer;
	private Company company;

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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
