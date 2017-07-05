package com.qount.invoice.model;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Payment {
	
	private String id;
	
	@NotNull(message="please provide payment reveivedFrom.")
	private String receivedFrom;
	
	@NotNull(message="please provide paymentAmount.")
	@Min(value=0, message="please enter non negitive numeric amount.")
	private BigDecimal paymentAmount;
	
	@NotNull(message="please provide currencyCode.")
	private String currencyCode;
	
	@NotNull(message="please provide referenceNo.")
	private String referenceNo;
	
	@NotNull(message="please provide paymentDate.")
	private String paymentDate;
	
	
	private String memo;
	
	private String companyId;
	
	@NotNull(message="please provide payment type.")
	private String type;
	
	private List<PaymentLine> paymentLines;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReceivedFrom() {
		return receivedFrom;
	}
	public void setReceivedFrom(String receivedFrom) {
		this.receivedFrom = receivedFrom;
	}
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getMemo() {
		return memo;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public List<PaymentLine> getPaymentLines() {
		return paymentLines;
	}
	public void setPaymentLines(List<PaymentLine> paymentLines) {
		this.paymentLines = paymentLines;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	

}
