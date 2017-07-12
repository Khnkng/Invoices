package com.qount.invoice.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PaymentLine {
	
	private String id;
	
	@NotNull(message="please provide invoiceId")
	private String invoiceId;
	
	@NotNull(message="please provide amount")
	@Min(value=0, message="please provide non negitive amount")
	private BigDecimal amount;
	
	private String invoiceDate;
	
	private String term;
	
	private BigDecimal invoiceAmount;
	
	private String state;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	
}
