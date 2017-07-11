package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 22 May 2017
 *
 */
@XmlRootElement
public class InvoicePayment {
	private String id;
	private String invoice_id;
	private String transaction_id;
	private double amount;
	private String transaction_date;
	private String status;
	private String period_start;
	private String period_end;
	private String currency_from;
	private String currency_to;
	private float conversion;
	private String conversionDate;
	private double currency_amount;
	
	public String getCurrency_from() {
		return currency_from;
	}

	public void setCurrency_from(String currency_from) {
		this.currency_from = currency_from;
	}

	public String getCurrency_to() {
		return currency_to;
	}

	public void setCurrency_to(String currency_to) {
		this.currency_to = currency_to;
	}

	public float getConversion() {
		return conversion;
	}

	public void setConversion(float conversion) {
		this.conversion = conversion;
	}

	public String getConversionDate() {
		return conversionDate;
	}

	public void setConversionDate(String conversionDate) {
		this.conversionDate = conversionDate;
	}

	public double getCurrency_amount() {
		return currency_amount;
	}

	public void setCurrency_amount(double currency_amount) {
		this.currency_amount = currency_amount;
	}

	public String getPeriod_end() {
		return period_end;
	}

	public void setPeriod_end(String period_end) {
		this.period_end = period_end;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransaction_date() {
		return transaction_date;
	}

	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPeriod_start() {
		return period_start;
	}

	public void setPeriod_start(String period_start) {
		this.period_start = period_start;
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
}
