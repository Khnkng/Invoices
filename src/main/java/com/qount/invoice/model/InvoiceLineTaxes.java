package com.qount.invoice.model;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 3 Feb 2017
 *
 */
public class InvoiceLineTaxes {
	private String proposal_line_id;
	private String tax_id;
	private double tax_rate;

	public String getProposal_line_id() {
		return proposal_line_id;
	}

	public void setProposal_line_id(String proposal_line_id) {
		this.proposal_line_id = proposal_line_id;
	}

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}

	public double getTax_rate() {
		return tax_rate;
	}

	public void setTax_rate(double tax_rate) {
		this.tax_rate = tax_rate;
	}

}
