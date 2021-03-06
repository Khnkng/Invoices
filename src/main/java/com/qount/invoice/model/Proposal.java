package com.qount.invoice.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.json.JSONArray;

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
	private String last_updated_by;
	private String last_updated_at;
	private String customer_id;
	private String state;
	private String proposal_date;
	private String estimate_date;
	private String notes;
	private double discount;
	private double deposit_amount;
	private double processing_fees;
	private boolean is_recurring;
	private String number;
	private String document_id;
	private List<ProposalLine> proposalLines;
	private double sub_totoal;
	private double amount_by_date;
	private Currencies currencies;
	private String action;
	private String actionType;
	private String created_at;
	private String payment_spring_token;
	private String ends_after;
	private String plan_id;
	private Customer customer;
	private String companyName;
	private String term;
	private boolean sendMail;
	private List<String> recepientsMails;
	private JSONArray recepientsMailsArr;
	private String send_to;
	private String email_state;
	private String invoice_id;
	private double tax_amount;
	private CustomerContactDetails customerContactDetails;
	private Company company;
	
	public CustomerContactDetails getCustomerContactDetails() {
		return customerContactDetails;
	}

	public void setCustomerContactDetails(CustomerContactDetails customerContactDetails) {
		this.customerContactDetails = customerContactDetails;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public double getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(double tax_amount) {
		this.tax_amount = tax_amount;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getEstimate_date() {
		return estimate_date;
	}

	public void setEstimate_date(String estimate_date) {
		this.estimate_date = estimate_date;
	}

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

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
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

	public boolean is_recurring() {
		return is_recurring;
	}

	public void setIs_recurring(boolean is_recurring) {
		this.is_recurring = is_recurring;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDocument_id() {
		return document_id;
	}

	public void setDocument_id(String document_id) {
		this.document_id = document_id;
	}

	public List<ProposalLine> getProposalLines() {
		return proposalLines;
	}

	public void setProposalLines(List<ProposalLine> proposalLines) {
		this.proposalLines = proposalLines;
	}

	public double getSub_totoal() {
		return sub_totoal;
	}

	public void setSub_totoal(double sub_totoal) {
		this.sub_totoal = sub_totoal;
	}

	public double getAmount_by_date() {
		return amount_by_date;
	}

	public void setAmount_by_date(double amount_by_date) {
		this.amount_by_date = amount_by_date;
	}

	public Currencies getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Currencies currencies) {
		this.currencies = currencies;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getPayment_spring_token() {
		return payment_spring_token;
	}

	public void setPayment_spring_token(String payment_spring_token) {
		this.payment_spring_token = payment_spring_token;
	}

	public String getEnds_after() {
		return ends_after;
	}

	public void setEnds_after(String ends_after) {
		this.ends_after = ends_after;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public List<String> getRecepientsMails() {
		return recepientsMails;
	}

	public void setRecepientsMails(List<String> recepientsMails) {
		this.recepientsMails = recepientsMails;
	}

	public JSONArray getRecepientsMailsArr() {
		return recepientsMailsArr;
	}

	public void setRecepientsMailsArr(JSONArray recepientsMailsArr) {
		this.recepientsMailsArr = recepientsMailsArr;
	}

	public String getSend_to() {
		return send_to;
	}

	public void setSend_to(String send_to) {
		this.send_to = send_to;
	}

	public String getEmail_state() {
		return email_state;
	}

	public void setEmail_state(String email_state) {
		this.email_state = email_state;
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

	public static void main(String[] args) {
		System.out.println(new Proposal());
	}
}
