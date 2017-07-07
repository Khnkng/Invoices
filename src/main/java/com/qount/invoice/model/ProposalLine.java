package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 22 Nov 2016
 *
 */
@XmlRootElement
public class ProposalLine {

	private String id;
	private String proposal_id;
	private String description;
	private String objectives;
	private double amount;
	private String currency;
	private String last_updated_at;
	private String last_updated_by;
	private double quantity;
	private double price;
	private String notes;
	private String item_id;
	private String coa_id;
	private String tax_id;

	public String getTax_id() {
		return tax_id;
	}

	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProposal_id() {
		return proposal_id;
	}

	public void setProposal_id(String proposal_id) {
		this.proposal_id = proposal_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getObjectives() {
		return objectives;
	}

	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getCoa_id() {
		return coa_id;
	}

	public void setCoa_id(String coa_id) {
		this.coa_id = coa_id;
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

	@Override
	public boolean equals(Object obj) {
		try {
			if (null != obj && obj instanceof ProposalLine) {
				ProposalLine arg = (ProposalLine) obj;
				if (!StringUtils.isBlank(arg.getId()) && arg.getId().equals(this.getId())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.equals(obj);
	}

}
