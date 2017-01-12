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
	private String id;
	private String company_id;
	private String customer_name;
	private String user_id;
	private String due_date;
	private String amount;
	private String status;
	private String terms;
	private String currency;
	private boolean recurring;
	private String start_date;
	private String end_date;
	private String recurring_frequency;
	private int number_of_invoices;
	private String created_at;
	private String created_by;
	private String last_updated_at;
	private String last_updated_by;
	private String payment_spring_customer_id;
	private String transaction_id;

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	private List<InvoiceLines> invoiceLines;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getInvoice_amount() {
		return amount;
	}

	public void setInvoice_amount(String invoice_amount) {
		this.amount = invoice_amount;
	}

	public String getInvoice_status() {
		return status;
	}

	public void setInvoice_status(String invoice_status) {
		this.status = invoice_status;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getPayment_spring_customer_id() {
		return payment_spring_customer_id;
	}

	public void setPayment_spring_customer_id(String payment_spring_customer_id) {
		this.payment_spring_customer_id = payment_spring_customer_id;
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
