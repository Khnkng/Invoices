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
public class Proposal {

	private String id;
	private String user_id;
	private String company_id;
	private double amount;
	private String currency;
	private String description;
	private String objectives;
	private String last_updated_at;
	private String last_updated_by;
	private String state;
	private String proposal_date;
	private String acceptance_date;
	private String acceptance_final_date;
	private String notes;
	private double discount;
	private double deposit_amount;
	private double processing_fees;
	private String remainder_json;
	private String remainder_mail_json;
	private double amount_by_date;
	private List<ProposalLine> proposalLines;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getProposal_date() {
		return proposal_date;
	}

	public void setProposal_date(String proposal_date) {
		this.proposal_date = proposal_date;
	}

	public String getAcceptance_date() {
		return acceptance_date;
	}

	public void setAcceptance_date(String acceptance_date) {
		this.acceptance_date = acceptance_date;
	}

	public String getAcceptance_final_date() {
		return acceptance_final_date;
	}

	public void setAcceptance_final_date(String acceptance_final_date) {
		this.acceptance_final_date = acceptance_final_date;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getDeposit_amount() {
		return deposit_amount;
	}

	public void setDeposit_amount(double deposit_amount) {
		this.deposit_amount = deposit_amount;
	}

	public double getProcessing_fees() {
		return processing_fees;
	}

	public void setProcessing_fees(double processing_fees) {
		this.processing_fees = processing_fees;
	}

	public String getRemainder_json() {
		return remainder_json;
	}

	public void setRemainder_json(String remainder_json) {
		this.remainder_json = remainder_json;
	}

	public String getRemainder_mail_json() {
		return remainder_mail_json;
	}

	public void setRemainder_mail_json(String remainder_mail_json) {
		this.remainder_mail_json = remainder_mail_json;
	}

	public List<ProposalLine> getProposalLines() {
		if (proposalLines == null) {
			proposalLines = new ArrayList<>();
		}
		return proposalLines;
	}

	public void setProposalLines(List<ProposalLine> proposalLines) {
		this.proposalLines = proposalLines;
	}

	public double getAmount_by_date() {
		return amount_by_date;
	}

	public void setAmount_by_date(double amount_by_date) {
		this.amount_by_date = amount_by_date;
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
