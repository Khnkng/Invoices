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
	
	private String paymentNote;
	
	private String depositedTo;
	
	private String journalID;
	
	private String customerName;
	
	private BigDecimal amountPaid;
	
	private String depositID;
	
	private String status;
	
	
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
	public String getPaymentNote() {
		return paymentNote;
	}
	public void setPaymentNote(String paymentNote) {
		this.paymentNote = paymentNote;
	}
	public String getDepositedTo() {
		return depositedTo;
	}
	public void setDepositedTo(String depositedTo) {
		this.depositedTo = depositedTo;
	}
	public String getJournalID() {
		return journalID;
	}
	public void setJournalID(String journalID) {
		this.journalID = journalID;
	}	

	@Override
    public boolean equals(Object obj) {
        if (obj != null) {
            Payment payment = (Payment) obj;
            return payment.getId().equalsIgnoreCase(this.id);
        }
        return false;
    }
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getDepositID() {
		return depositID;
	}
	public void setDepositID(String depositID) {
		this.depositID = depositID;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
