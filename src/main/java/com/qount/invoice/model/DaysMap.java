package com.qount.invoice.model;

/**
 * Model object used in PaymentSpring
 * 
 * @author Mateen, Qount.
 * @version 1.0, 21 Jun 2017
 *
 */
public class DaysMap {

	private String month;
	private String day;
	private String week;
	
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	
	
	
}
