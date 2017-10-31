package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InvoiceHistory {

	private String id;
	private String invoice_id;
	private String user_id;
	private String action;
	private String action_at;
	private String company_id;
	private String email_to;
	private String email_subject;
	private String email_from;
	private String created_by;
	private String created_at;
	private String last_updated_by;
	private String last_updated_at;
	private String description;
	private String webhook_event_id;
	private double amount;
	
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getWebhook_event_id() {
		return webhook_event_id;
	}

	public void setWebhook_event_id(String webhook_event_id) {
		this.webhook_event_id = webhook_event_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getAction() {
		return action;
	}

	public String getAction_at() {
		return action_at;
	}

	public String getCompany_id() {
		return company_id;
	}

	public String getEmail_to() {
		return email_to;
	}

	public String getEmail_subject() {
		return email_subject;
	}

	public String getEmail_from() {
		return email_from;
	}

	public String getCreated_by() {
		return created_by;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setAction_at(String action_at) {
		this.action_at = action_at;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public void setEmail_to(String email_to) {
		this.email_to = email_to;
	}

	public void setEmail_subject(String email_subject) {
		this.email_subject = email_subject;
	}

	public void setEmail_from(String email_from) {
		this.email_from = email_from;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
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
		System.out.println(new InvoiceHistory());
	}
}