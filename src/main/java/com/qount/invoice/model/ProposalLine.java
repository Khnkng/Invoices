package com.qount.invoice.model;

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
	private Item item;
	private String item_id;
	private String item_name;
	private String proposal_id;
	private String description;
	private String objectives;
	private double amount;
	private String last_updated_by;
	private String last_updated_at;
	private double quantity;
	private double price;
	private String notes;
	private Coa coa;
	private String type;
	private String tax_id;
	
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Item getItem() {
		return item;
	}


	public void setItem(Item item) {
		this.item = item;
	}


	public String getItem_id() {
		return item_id;
	}


	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}


	public String getItem_name() {
		return item_name;
	}


	public void setItem_name(String item_name) {
		this.item_name = item_name;
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


	public Coa getCoa() {
		return coa;
	}


	public void setCoa(Coa coa) {
		this.coa = coa;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTax_id() {
		return tax_id;
	}


	public void setTax_id(String tax_id) {
		this.tax_id = tax_id;
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
