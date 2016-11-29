package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 22 Nov 2016
 *
 */
@XmlRootElement
public class Proposal {

	private String userID;

	private String companyID;

	private String proposalID;

	private String customer_name;

	private float total_amount;

	private String currency;

	private boolean bank_account;

	private boolean credit_card;

	private List<ProposalLine> proposalLines;

	public String getCompanyID() {
		return companyID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}

	public String getProposalID() {
		return proposalID;
	}

	public void setProposalID(String proposalID) {
		this.proposalID = proposalID;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public float getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(float total_amount) {
		this.total_amount = total_amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isBank_account() {
		return bank_account;
	}

	public void setBank_account(boolean bank_account) {
		this.bank_account = bank_account;
	}

	public boolean isCredit_card() {
		return credit_card;
	}

	public void setCredit_card(boolean credit_card) {
		this.credit_card = credit_card;
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

}
