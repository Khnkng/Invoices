package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
@XmlRootElement
public class Invoice {

	private String invoice_date;
	private String due_date;
	private String invoice_amount;
	private String invoice_status;
	private String terms;
	private String companyID;
	private String invoiceID;
	private String customer_name;
	private int currencyID;
	private String userID;
	private boolean recurring;
	private String start_date;
	private String end_date;
	private String recurring_frequency;
	private int number_of_invoices;
	private boolean bank_account;
	private boolean credit_card;

	public boolean isCredit_card() {
		return credit_card;
	}

	public void setCredit_card(boolean credit_card) {
		this.credit_card = credit_card;
	}

	private List<InvoiceLines> invoiceLines;

	public boolean isBank_account() {
		return bank_account;
	}

	public void setBank_account(boolean bank_account) {
		this.bank_account = bank_account;
	}

	public String getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getInvoice_amount() {
		return invoice_amount;
	}

	public void setInvoice_amount(String invoice_amount) {
		this.invoice_amount = invoice_amount;
	}

	public String getInvoice_status() {
		return invoice_status;
	}

	public void setInvoice_status(String invoice_status) {
		this.invoice_status = invoice_status;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getCompanyID() {
		return companyID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}

	public String getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(String invoiceID) {
		this.invoiceID = invoiceID;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public int getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public boolean isRecurring() {
		return recurring;
	}

	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getRecurring_frequency() {
		return recurring_frequency;
	}

	public void setRecurring_frequency(String recurring_frequency) {
		this.recurring_frequency = recurring_frequency;
	}

	public int getNumber_of_invoices() {
		return number_of_invoices;
	}

	public void setNumber_of_invoices(int number_of_invoices) {
		this.number_of_invoices = number_of_invoices;
	}

	public List<InvoiceLines> getInvoiceLines() {
		if (invoiceLines == null) {
			invoiceLines = new ArrayList<>();
		}
		return invoiceLines;
	}

	public void setInvoiceLines(List<InvoiceLines> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

}
