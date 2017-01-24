package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

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
	private long quantity;
	private double price;
	private String notes;
	private List<ProposalLineTaxes> proposalLineTaxes;

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

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
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

	public List<ProposalLineTaxes> getProposalLineTaxes() {
		if (proposalLineTaxes == null) {
			proposalLineTaxes = new ArrayList<>();
		}
		return proposalLineTaxes;
	}

	public void setProposalLineTaxes(List<ProposalLineTaxes> proposalLineTaxes) {
		this.proposalLineTaxes = proposalLineTaxes;
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
