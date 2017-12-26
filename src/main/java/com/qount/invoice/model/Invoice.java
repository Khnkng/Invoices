package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement
public class Invoice {

	private String id;
	private String user_id;
	private String company_id;
	private double amount;
	private double sub_total;
	private String currency;
	private String description;
	private String objectives;
	private String last_updated_by;
	private String last_updated_at;
	private String customer_id;
	private String state;
	private String invoice_date;
	private String notes;
	private double discount;
	private double deposit_amount;
	private double processing_fees;
	private boolean is_recurring;
	private String number;
	private String document_id;
	private List<InvoiceLine> invoiceLines;
	private double amount_due;
	private String due_date;
	private double amount_by_date;
	private Currencies currencies;
	private String action;
	private String actionType;
	private String created_at;
	private String payment_spring_token;
	private String ends_after;
	private String plan_id;
	private double amountToPay;
	private double amount_paid;
	private Customer customer;
	private String companyName;
	private String term;
	private boolean sendMail;
	private List<String> recepientsMails;
	private JSONArray recepientsMailsArr;
	private String payment_options;
	private String send_to;
	private String email_state;
	private String payment_method;
	private Company2 company;
	private CustomerContactDetails customerContactDetails;
	private double tax_amount;
	private String journalID;
	private String payment_type;//Credit Card || Bank
	private String proposal_id;
	private String customer_name;
	private String reference_number;
	private String payment_date;
	private String bank_account_id;
	private String remainder_job_id;
	private String remainder_name;
	private PdfData pdf_data;
	private String attachmentBase64;
	private boolean is_past_due;
	private String payment_ids;
	private String subject;
	private String from;
	private String attachments_metadata;
	private String mailSubject;
	private List<InvoiceCommission> commissions;
	private String late_fee_id;
	private String late_fee_name;
	private double late_fee_amount;
	private boolean late_fee_applied;
	private String customer_first_name;
	private String customer_last_name;
	private String journal_job_id;
	private String email_notes;
	private List<InvoiceHistory> histories;
	private boolean createHistory;
	private boolean mapping;
	private String postId;
	
	public boolean isCreateHistory() {
		return createHistory;
	}

	public void setCreateHistory(boolean createHistory) {
		this.createHistory = createHistory;
	}

	public String getLate_fee_name() {
		return late_fee_name;
	}

	public void setLate_fee_name(String late_fee_name) {
		this.late_fee_name = late_fee_name;
	}

	public List<InvoiceHistory> getHistories() {
		return histories;
	}

	public void setHistories(List<InvoiceHistory> histories) {
		this.histories = histories;
	}

	public String getEmail_notes() {
		return email_notes;
	}

	public void setEmail_notes(String email_notes) {
		this.email_notes = email_notes;
	}

	public String getJournal_job_id() {
		return journal_job_id;
	}

	public void setJournal_job_id(String journal_job_id) {
		this.journal_job_id = journal_job_id;
	}

	public String getCustomer_first_name() {
		return customer_first_name;
	}

	public void setCustomer_first_name(String customer_first_name) {
		this.customer_first_name = customer_first_name;
	}

	public String getCustomer_last_name() {
		return customer_last_name;
	}

	public void setCustomer_last_name(String customer_last_name) {
		this.customer_last_name = customer_last_name;
	}

	public boolean isLate_fee_applied() {
		return late_fee_applied;
	}

	public void setLate_fee_applied(boolean late_fee_applied) {
		this.late_fee_applied = late_fee_applied;
	}

	public List<InvoiceCommission> getCommissions() {
		return commissions;
	}

	public void setCommissions(List<InvoiceCommission> commissions) {
		this.commissions = commissions;
	}

	public String getLate_fee_id() {
		return StringUtils.isBlank(late_fee_id)?null:late_fee_id;
	}

	public void setLate_fee_id(String late_fee_id) {
		this.late_fee_id = late_fee_id;
	}

	public double getLate_fee_amount() {
		return late_fee_amount;
	}

	public void setLate_fee_amount(double late_fee_amount) {
		this.late_fee_amount = late_fee_amount;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getAttachments_metadata() {
		return attachments_metadata;
	}

	public void setAttachments_metadata(String attachments_metadata) {
		this.attachments_metadata = attachments_metadata;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPayment_ids() {
		return payment_ids;
	}

	public void setPayment_ids(String payment_ids) {
		this.payment_ids = payment_ids;
	}

	public boolean isIs_past_due() {
		return is_past_due;
	}

	public void setIs_past_due(boolean is_past_due) {
		this.is_past_due = is_past_due;
	}

	public String getAttachmentBase64() {
		return attachmentBase64;
	}

	public void setAttachmentBase64(String attachmentBase64) {
		this.attachmentBase64 = attachmentBase64;
	}

	public String getRemainder_name() {
		return remainder_name;
	}

	public void setRemainder_name(String remainder_name) {
		this.remainder_name = remainder_name;
	}

	public String getRemainder_job_id() {
		return remainder_job_id;
	}

	public void setRemainder_job_id(String remainder_job_id) {
		this.remainder_job_id = remainder_job_id;
	}

	public double getSub_total() {
		return sub_total;
	}

	public void setSub_total(double sub_total) {
		this.sub_total = sub_total;
	}

	public String getProposal_id() {
		return proposal_id;
	}

	public void setProposal_id(String proposal_id) {
		this.proposal_id = proposal_id;
	}
	public String getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}

	public double getTax_amount() {
		return tax_amount;
	}

	public void setTax_amount(double tax_amount) {
		this.tax_amount = tax_amount;
	}

	public Company2 getCompany() {
		return company;
	}

	public void setCompany(Company2 company) {
		this.company = company;
	}

	public CustomerContactDetails getCustomerContactDetails() {
		return customerContactDetails;
	}

	public void setCustomerContactDetails(CustomerContactDetails customerContactDetails) {
		this.customerContactDetails = customerContactDetails;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public String getEmail_state() {
		return email_state;
	}

	public void setEmail_state(String email_state) {
		this.email_state = email_state;
	}

	public String getSend_to() {
		return send_to;
	}

	public void setSend_to(String send_to) {
		this.send_to = send_to;
	}

	public String getPayment_options() {
		return payment_options;
	}

	public void setPayment_options(String payment_options) {
		this.payment_options = payment_options;
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

	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public double getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(double amount_paid) {
		this.amount_paid = amount_paid;
	}

	public double getAmountToPay() {
		return amountToPay;
	}

	public void setAmountToPay(double amountToPay) {
		this.amountToPay = amountToPay;
	}

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getEnds_after() {
		return ends_after;
	}

	public void setEnds_after(String ends_after) {
		this.ends_after = ends_after;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getPayment_spring_token() {
		return payment_spring_token;
	}

	public void setPayment_spring_token(String payment_spring_token) {
		this.payment_spring_token = payment_spring_token;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Currencies getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Currencies currencies) {
		this.currencies = currencies;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}

	public double getAmount_due() {
		return amount_due;
	}

	public void setAmount_due(double amount_due) {
		this.amount_due = amount_due;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public List<InvoiceLine> getInvoiceLines() {
		if (invoiceLines == null) {
			invoiceLines = new ArrayList<>();
		}
		return invoiceLines;
	}

	public void setInvoiceLines(List<InvoiceLine> invoiceLines) {
		this.invoiceLines = invoiceLines;
	}

	public double getAmount_by_date() {
		return amount_by_date;
	}

	public void setAmount_by_date(double amount_by_date) {
		this.amount_by_date = amount_by_date;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getJournalID() {
		return journalID;
	}

	public void setJournalID(String journalID) {
		this.journalID = journalID;
	}

	public String prepareJSParemeters() {
		StringBuilder journalParmBuilder = new StringBuilder();
		journalParmBuilder.append(this.customer_id).append(this.invoice_date).append(this.currency).append(this.number).append(this.amount);
		if (this.invoiceLines != null) {
			for (InvoiceLine line : invoiceLines) {
				journalParmBuilder.append(line.prepareJSParemeters());
			}
		}
		return journalParmBuilder.toString();
	}

	@Override
    public boolean equals(Object obj) {
        if (obj != null) {
            Invoice invoice = (Invoice) obj;
            return invoice.getId().equalsIgnoreCase(this.id);
        }
        return false;
    }

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", user_id=" + user_id + ", company_id=" + company_id + ", amount=" + amount + ", sub_total=" + sub_total + ", currency=" + currency
				+ ", description=" + description + ", objectives=" + objectives + ", last_updated_by=" + last_updated_by + ", last_updated_at=" + last_updated_at + ", customer_id="
				+ customer_id + ", state=" + state + ", invoice_date=" + invoice_date + ", notes=" + notes + ", discount=" + discount + ", deposit_amount=" + deposit_amount
				+ ", processing_fees=" + processing_fees + ", is_recurring=" + is_recurring + ", number=" + number + ", document_id=" + document_id + ", amount_due=" + amount_due
				+ ", due_date=" + due_date + ", amount_by_date=" + amount_by_date + ", currencies=" + currencies + ", action=" + action + ", actionType=" + actionType
				+ ", created_at=" + created_at + ", payment_spring_token=" + payment_spring_token + ", ends_after=" + ends_after + ", plan_id=" + plan_id + ", amountToPay="
				+ amountToPay + ", amount_paid=" + amount_paid + ", companyName=" + companyName + ", term=" + term + ", sendMail=" + sendMail + ", recepientsMailsArr="
				+ recepientsMailsArr + ", payment_options=" + payment_options + ", send_to=" + send_to + ", email_state=" + email_state + ", payment_method=" + payment_method
				+ ", tax_amount=" + tax_amount + ", journalID=" + journalID + ", payment_type=" + payment_type + ", proposal_id=" + proposal_id + ", customer_name=" + customer_name
				+ ", reference_number=" + reference_number + ", payment_date=" + payment_date + ", bank_account_id=" + bank_account_id + ", remainder_job_id=" + remainder_job_id
				+ ", remainder_name=" + remainder_name + "]";
	}

	public static void main(String[] args) {
		System.out.println(new Invoice());
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getReference_number() {
		return reference_number;
	}

	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	public String getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(String payment_date) {
		this.payment_date = payment_date;
	}

	public String getBank_account_id() {
		return bank_account_id;
	}

	public void setBank_account_id(String bank_account_id) {
		this.bank_account_id = bank_account_id;
	}

	public PdfData getPdf_data() {
		return pdf_data;
	}

	public void setPdf_data(PdfData pdf_data) {
		this.pdf_data = pdf_data;
	}

	public boolean isMapping() {
		return mapping;
	}

	public void setMapping(boolean mapping) {
		this.mapping = mapping;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
	
}
