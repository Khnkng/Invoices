package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 15 Jun 2017
 *
 */
public class PaymentSpringPlan {
	
	private String frequency;
	private String name;
	private String amount;
	//An integer or a hash describing when in the week, month or year the plan is billed. For daily plans, this is not required. For weekly plans, should be an integer between 1 and 7 (indicating Sunday through Saturday). For monthly plans, should be either an integer between 1 and 31, or a JSON-encoded hash with keys week (between 1 and 5, inclusive) and day (between 1 and 7, inclusive) to indicate a day of the week (e.g. {‘week’:2,’day’:5} to bill on the second Thursday of every month). For quarterly plans, should be a JSON-encoded hash with keys month (between 1 and 3, inclusive) and day (between 1 and 31, inclusive) to indicate a day of a month in the quarter (e.g. {‘month’:2,’day’:5} to bill on the fifth day of the second month every quarter). For yearly plans, should be either an integer between 1 and 366, or a JSON-encoded hash with keys month (between 1 and 12, inclusive) and day (between 1 and 31, inclusive) to indicate a day of the month (e.g. {‘month’:5,’day’:15} to bill on May 15 every year).
	private String day;
	private String ends_after;
	private String bill_immediately;
	
	public String getBill_immediately() {
		return bill_immediately;
	}

	public void setBill_immediately(String bill_immediately) {
		this.bill_immediately = bill_immediately;
	}

	public String getEnds_after() {
		return ends_after;
	}

	public void setEnds_after(String ends_after) {
		this.ends_after = ends_after;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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
		System.out.println(new Invoice());
	}
}
