package com.qount.invoice.model;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentLine {
	
	private String id;
	
	@NotNull(message="please provide invoiceId")
	private String invoiceId;
	
	@NotNull(message="please provide amount")
	@Min(value=0, message="please provide non negitive amount")
	private BigDecimal amount;
	
	private double discount;
	
	private String invoiceDate;
	
	private String term;
	
	private BigDecimal invoiceAmount;
	
	private String state;
	
	private String displayState;
	
	private String invoiceDueDate;
	
	private double amountDue;
	
	private String invoiceNumber;
	
	private String createdDate;

	
	public String getDisplayState() {
		return displayState;
	}
	public void setDisplayState(String displayState) {
		this.displayState = displayState;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
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
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
		}
		return super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			PaymentLine paymentLine = (PaymentLine) obj;
			
			return paymentLine.getId().equalsIgnoreCase(this.id);
		}
		return false;
	}
	public String getInvoiceDueDate() {
		return invoiceDueDate;
	}
	public void setInvoiceDueDate(String invoiceDueDate) {
		this.invoiceDueDate = invoiceDueDate;
	}
	public double getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
}
