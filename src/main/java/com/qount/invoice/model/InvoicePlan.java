package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 28 Jun 2017
 *
 */
public class InvoicePlan {
	
	private String id;
	private String plan_id;
	private String name;
	private String amount;
	private String frequency;
	private String ends_after;
	private String bill_immediately;
	private DaysMap day_map;
	private String user_id;
	private String company_id;
	private String created_by;
	private long created_at_mills;
	private String last_updated_by;
	private long last_updated_at;
	
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
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

	public String getCreated_by() {
		return created_by;
	}

	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}

	public long getCreated_at_mills() {
		return created_at_mills;
	}

	public void setCreated_at_mills(long created_at_mills) {
		this.created_at_mills = created_at_mills;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public long getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(long last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAmount() {
		return amount;
	}

	public String getFrequency() {
		return frequency;
	}

	public String getEnds_after() {
		return ends_after;
	}

	public String getBill_immediately() {
		return bill_immediately;
	}

	public DaysMap getDay_map() {
		return day_map;
	}

	public void setId(String id) {
		this.id=id;
	}

	public void setName(String name) {
		this.name=name;
	}

	public void setAmount(String amount) {
		this.amount=amount;
	}

	public void setFrequency(String frequency) {
		this.frequency=frequency;
	}

	public void setEnds_after(String ends_after) {
		this.ends_after=ends_after;
	}

	public void setBill_immediately(String bill_immediately) {
		this.bill_immediately=bill_immediately;
	}

	public void setDay_map(DaysMap day_map) {
		this.day_map=day_map;
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
		System.out.println(new InvoicePlan());
	}
}
