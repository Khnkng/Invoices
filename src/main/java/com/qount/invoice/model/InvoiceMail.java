package com.qount.invoice.model;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Business object used in Invoice Emails
 * 
 * @author Mateen, Qount.
 * @version 1.0, 21 April 2017
 *
 */
public class InvoiceMail {

	private String invoiceNumber;
	private String customerName;
	private String invocieDate;
	private String companyName;
	private String currencySymbol;
	private String currencyHtml_symbol;
	private double amount;
	private double amount_by_date;
	private String currencyCode;
	private String invoiceDueDate;
	private JSONArray customerEmails;
	private String invoiceCreatedAt;
	private String invoiceId;
	private JSONArray recepients_mails;

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getCurrencyHtml_symbol() {
		return currencyHtml_symbol;
	}

	public void setCurrencyHtml_symbol(String currencyHtml_symbol) {
		this.currencyHtml_symbol = currencyHtml_symbol;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceCreatedAt() {
		return invoiceCreatedAt;
	}

	public void setInvoiceCreatedAt(String invoiceCreatedAt) {
		this.invoiceCreatedAt = invoiceCreatedAt;
	}

	public double getAmount_by_date() {
		return amount_by_date;
	}

	public void setAmount_by_date(double amount_by_date) {
		this.amount_by_date = amount_by_date;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getInvocieDate() {
		return invocieDate;
	}

	public void setInvocieDate(String invocieDate) {
		this.invocieDate = invocieDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getInvoiceDueDate() {
		return invoiceDueDate;
	}

	public void setInvoiceDueDate(String invoiceDueDate) {
		this.invoiceDueDate = invoiceDueDate;
	}

	public JSONArray getCustomerEmails() {
		return customerEmails;
	}

	public void setCustomerEmails(JSONArray customerEmails) {
		this.customerEmails = customerEmails;
	}

	public JSONArray getRecepients_mails() {
		return recepients_mails;
	}

	public void setRecepients_mails(JSONArray recepients_mails) {
		this.recepients_mails = recepients_mails;
	}

	@Override
	public String toString() {
		try {
			new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
		}
		return super.toString();
	}
}
