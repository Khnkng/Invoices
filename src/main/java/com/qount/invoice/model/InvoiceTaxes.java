package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 3 Feb 2017
 *
 */
public class InvoiceTaxes {
	private String invoice_id;
	private String tax_id;
	private Double tax_rate;

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}

	public Double getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(Double tax_rate) {
		this.tax_rate = tax_rate;
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
