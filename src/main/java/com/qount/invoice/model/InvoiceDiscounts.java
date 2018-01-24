package com.qount.invoice.model;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InvoiceDiscounts {
	private String id;
	private String name;
	private String description;
	private String type;
	private String company_id;
	private String created_by;
	private String created_at;
	private String last_updated_by;
	private String last_updated_at;
	private List<DiscountsRanges> discountsRanges;

	public List<DiscountsRanges> getDiscountsRanges() {
		return discountsRanges;
	}

	public void setDiscountsRanges(List<DiscountsRanges> discountsRanges) {
		this.discountsRanges = discountsRanges;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getCompany_id() {
		return company_id;
	}

	public String getCreated_by() {
		return created_by;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public String getLast_updated_at() {
		return last_updated_at;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public void setLast_updated_at(String last_updated_at) {
		this.last_updated_at = last_updated_at;
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
