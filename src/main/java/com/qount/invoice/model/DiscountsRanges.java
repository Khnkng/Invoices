package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DiscountsRanges {

	private String id;
	private String discount_id;
	private int fromDay;
	private int toDay;
	private double value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiscount_id() {
		return discount_id;
	}

	public void setDiscount_id(String discount_id) {
		this.discount_id = discount_id;
	}

	public int getFromDay() {
		return fromDay;
	}

	public void setFromDay(int fromDay) {
		this.fromDay = fromDay;
	}

	public int getToDay() {
		return toDay;
	}

	public void setToDay(int toDay) {
		this.toDay = toDay;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
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

	@Override
	public boolean equals(Object obj) {
		try {
			return ((DiscountsRanges)obj).getId().equals(this.id);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return super.equals(obj);
	}
}
