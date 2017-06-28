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
	private String name;
	private String amount;
	private String frequency;
	private String ends_after;
	private String bill_immediately;
	private String day_map;

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

	public String getDay_map() {
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

	public void setDay_map(String day_map) {
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
