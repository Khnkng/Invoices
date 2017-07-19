package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BoxValues {
	
	private Double avgReceivableDays;
	private Double avgOutstandingAmount;
	
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

}
