package com.qount.invoice.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
@XmlRootElement
public class Invoice {
	private String id;
	private String user_id;
	private String company_id;
	private double amount;
	private String currency;
	private String description;
	private String objectives;
	private String last_updated_by;
	private String last_updated_at;
	private String customer_id;
	private String state;
	private String invoice_date;
	private String acceptance_date;
	private String acceptance_final_date;
	private String notes;
	private double discount;
	private double deposit_amount;
	private double processing_fees;
	private String remainder_json;
	private String remainder_mail_json;
	private boolean is_recurring;
	private String recurring_frequency;
	private double recurring_frequency_value;
	private String recurring_start_date;
	private String recurring_end_date;
	private boolean is_mails_automated;
	private boolean is_cc_current_user;
	private String payment_spring_customer_id;
	private int number;
	private String po_number;
	private String document_id;
	private List<InvoiceLine> invoiceLines;
	private List<InvoiceTaxes> invoiceTaxes;
	private double amount_due;
	private String payment_date;
	private double sub_totoal;
	private double amount_by_date;
	private Currencies currencies;
	private String action;
	private String actionType;
	private String created_at;
	private String payment_spring_token;
	private String ends_after;
	private String bill_immediately;
	private String plan_id;
	private String amountToPay;
	private double amount_paid;
	
	public double getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(double amount_paid) {
		this.amount_paid = amount_paid;
	}

	public String getAmountToPay() {
		return amountToPay;
	}

	public void setAmountToPay(String amountToPay) {
		this.amountToPay = amountToPay;
	}

	public String getBill_immediately() {
		return bill_immediately;
	}

	public void setBill_immediately(String bill_immediately) {
		this.bill_immediately = bill_immediately;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getEnds_after() {
		return ends_after;
	}

	public void setEnds_after(String ends_after) {
		this.ends_after = ends_after;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getPayment_spring_token() {
		return payment_spring_token;
	}

	public void setPayment_spring_token(String payment_spring_token) {
		this.payment_spring_token = payment_spring_token;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Currencies getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Currencies currencies) {
		this.currencies = currencies;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(String payment_date) {
		this.payment_date = payment_date;
	}

	public double getSub_totoal() {
		return sub_totoal;
	}

	public void setSub_totoal(double sub_totoal) {
		this.sub_totoal = sub_totoal;
	}

	public String getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}

	public double getAmount_due() {
		return amount_due;
	}

	public void setAmount_due(double amount_due) {
		this.amount_due = amount_due;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObjectives() {
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAcceptance_date() {
		return acceptance_date;
	}

	public void setAcceptance_date(String acceptance_date) {
		this.acceptance_date = acceptance_date;
	}

	public String getAcceptance_final_date() {
		return acceptance_final_date;
	}

	public void setAcceptance_final_date(String acceptance_final_date) {
		this.acceptance_final_date = acceptance_final_date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getDeposit_amount() {
		return deposit_amount;
	}

	public void setDeposit_amount(double deposit_amount) {
		this.deposit_amount = deposit_amount;
	}

	public double getProcessing_fees() {
		return processing_fees;
	}

	public void setProcessing_fees(double processing_fees) {
		this.processing_fees = processing_fees;
	}

	public String getRemainder_json() {
		return remainder_json;
	}

	public void setRemainder_json(String remainder_json) {
		this.remainder_json = remainder_json;
	}

	public String getRemainder_mail_json() {
		return remainder_mail_json;
	}

	public void setRemainder_mail_json(String remainder_mail_json) {
		this.remainder_mail_json = remainder_mail_json;
	}

	public boolean is_recurring() {
		return is_recurring;
	}

	public void setIs_recurring(boolean is_recurring) {
		this.is_recurring = is_recurring;
	}

	public String getRecurring_frequency() {
		return recurring_frequency;
	}

	public void setRecurring_frequency(String recurring_frequency) {
		this.recurring_frequency = recurring_frequency;
	}

	public double getRecurring_frequency_value() {
		return recurring_frequency_value;
	}

	public void setRecurring_frequency_value(double recurring_frequency_value) {
		this.recurring_frequency_value = recurring_frequency_value;
	}

	public String getRecurring_start_date() {
		return recurring_start_date;
	}

	public void setRecurring_start_date(String recurring_start_date) {
		this.recurring_start_date = recurring_start_date;
	}

	public String getRecurring_end_date() {
		return recurring_end_date;
	}

	public void setRecurring_end_date(String recurring_end_date) {
		this.recurring_end_date = recurring_end_date;
	}

	public boolean is_mails_automated() {
		return is_mails_automated;
	}

	public void setIs_mails_automated(boolean is_mails_automated) {
		this.is_mails_automated = is_mails_automated;
	}

	public boolean is_cc_current_user() {
		return is_cc_current_user;
	}

	public void setIs_cc_current_user(boolean is_cc_current_user) {
		this.is_cc_current_user = is_cc_current_user;
	}

	public String getPayment_spring_customer_id() {
		return payment_spring_customer_id;
	}

	public void setPayment_spring_customer_id(String payment_spring_customer_id) {
		this.payment_spring_customer_id = payment_spring_customer_id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPo_number() {
		return po_number;
	}

	public void setPo_number(String po_number) {
		this.po_number = po_number;
	}

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}

	public List<InvoiceLine> getInvoiceLines() {
		return invoiceLines;
	}

	public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

	public List<InvoiceTaxes> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTaxes> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
	}

	public double getAmount_by_date() {
		return amount_by_date;
	}

	public void setAmount_by_date(double amount_by_date) {
		this.amount_by_date = amount_by_date;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.toString();
	}

	public static void main(String[] args) {
		System.out.println(new Invoice());
	}
}
