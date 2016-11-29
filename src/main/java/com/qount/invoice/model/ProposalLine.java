package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 22 Nov 2016
 *
 */
@XmlRootElement
public class ProposalLine {

	private String proposalID;

	private String lineID;
	
	private int line_number;

	private String description;

	private int quantity;

	private float unit_cost;

	private float total_amount;

	public String getProposalID() {
		return proposalID;
	}

	public void setProposalID(String proposalID) {
		this.proposalID = proposalID;
	}

	public String getLineID() {
		return lineID;
	}

	public void setLineID(String lineID) {
		this.lineID = lineID;
	}

	public int getLine_number() {
		return line_number;
	}

	public void setLine_number(int line_number) {
		this.line_number = line_number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getUnit_cost() {
		return unit_cost;
	}

	public void setUnit_cost(float unit_cost) {
		this.unit_cost = unit_cost;
	}

	public float getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(float total_amount) {
		this.total_amount = total_amount;
	}

}
