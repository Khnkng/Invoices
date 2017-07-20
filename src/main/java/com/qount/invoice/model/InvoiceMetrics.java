package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InvoiceMetrics {
	
	private Double avgReceivableDays;
	private Double avgOutstandingAmount;
	private Double invoiceCount;
	private Double totalReceivableAmount;
	
	public Double getAvgReceivableDays() {
		return avgReceivableDays;
	}
	public void setAvgReceivableDays(Double avgReceivableDays) {
		this.avgReceivableDays = avgReceivableDays;
	}
	public Double getAvgOutstandingAmount() {
		return avgOutstandingAmount;
	}
	public void setAvgOutstandingAmount(Double avgOutstandingAmount) {
		this.avgOutstandingAmount = avgOutstandingAmount;
	}
	public Double getInvoiceCount() {
		return invoiceCount;
	}
	public void setInvoiceCount(Double invoiceCount) {
		this.invoiceCount = invoiceCount;
	}
	public Double getTotalReceivableAmount() {
		return totalReceivableAmount;
	}
	public void setTotalReceivableAmount(Double totalReceivableAmount) {
		this.totalReceivableAmount = totalReceivableAmount;
	}

}
