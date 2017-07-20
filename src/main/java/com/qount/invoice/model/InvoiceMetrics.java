package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InvoiceMetrics {
	
	private String avgReceivableDays;
	private String avgOutstandingAmount;
	private String invoiceCount;
	private String totalReceivableAmount;
	private String totalPastDueAmount;
	private String sentInvoices;
	private String openedInvoices;
	private String TotalReceivedLast30Days;
	private String booksBalance;
	
	public String getAvgReceivableDays() {
		return avgReceivableDays;
	}
	public void setAvgReceivableDays(String avgReceivableDays) {
		this.avgReceivableDays = avgReceivableDays;
	}
	public String getAvgOutstandingAmount() {
		return avgOutstandingAmount;
	}
	public void setAvgOutstandingAmount(String avgOutstandingAmount) {
		this.avgOutstandingAmount = avgOutstandingAmount;
	}
	public String getInvoiceCount() {
		return invoiceCount;
	}
	public void setInvoiceCount(String invoiceCount) {
		this.invoiceCount = invoiceCount;
	}
	public String getTotalReceivableAmount() {
		return totalReceivableAmount;
	}
	public void setTotalReceivableAmount(String totalReceivableAmount) {
		this.totalReceivableAmount = totalReceivableAmount;
	}
	public String getTotalPastDueAmount() {
		return totalPastDueAmount;
	}
	public void setTotalPastDueAmount(String totalPastDueAmount) {
		this.totalPastDueAmount = totalPastDueAmount;
	}
	public String getSentInvoices() {
		return sentInvoices;
	}
	public void setSentInvoices(String sentInvoices) {
		this.sentInvoices = sentInvoices;
	}
	public String getOpenedInvoices() {
		return openedInvoices;
	}
	public void setOpenedInvoices(String openedInvoices) {
		this.openedInvoices = openedInvoices;
	}
	public String getTotalReceivedLast30Days() {
		return TotalReceivedLast30Days;
	}
	public void setTotalReceivedLast30Days(String totalReceivedLast30Days) {
		TotalReceivedLast30Days = totalReceivedLast30Days;
	}
	public String getBooksBalance() {
		return booksBalance;
	}
	public void setBooksBalance(String booksBalance) {
		this.booksBalance = booksBalance;
	}
	
	

}
