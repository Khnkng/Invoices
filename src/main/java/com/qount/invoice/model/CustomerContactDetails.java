package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Business object for CustomerContactDetails
 * 
 * @author Mateen, Qount.
 * @version 1.0, 11 Jul 2017
 *
 */
public class CustomerContactDetails {

	private String id;
	private String customer_id;
	private String first_name;
	private String last_name;
	private String mobile;
	private String email;
	private String other;

	public String getId() {
		return id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public String getMobile() {
		return mobile;
	}

	public String getEmail() {
		return email;
	}

	public String getOther() {
		return other;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return super.toString();
	}

}