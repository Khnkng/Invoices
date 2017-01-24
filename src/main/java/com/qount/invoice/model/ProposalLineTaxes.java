package com.qount.invoice.model;

public class ProposalLineTaxes {

	private String id;
	private String tax_id;
	private String tax_rate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}

	public String getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(String tax_rate) {
		this.tax_rate = tax_rate;
	}

}
