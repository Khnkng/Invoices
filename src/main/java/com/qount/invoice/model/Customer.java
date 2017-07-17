package com.qount.invoice.model;

import org.json.JSONArray;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Business object used in CustomerDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 2.0, 15 Nov 2016
 *
 */
public class Customer {
	private String user_id;
	private String company_id;
	private String customer_id;
	private JSONArray email_ids;
	private String customer_name;
	private String customer_ein;
	private String customer_address;
	private String customer_state;
	private String customer_city;
	private String customer_country;
	private String customer_zipcode;
	private String phone_number;
	private String coa;
	private String payment_spring_id;
	private String term;
	private String card_name;
	private String fax;
	private String street_1;
	private String street_2;

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getStreet_1() {
		return street_1;
	}

	public void setStreet_1(String street_1) {
		this.street_1 = street_1;
	}

	public String getStreet_2() {
		return street_2;
	}

	public void setStreet_2(String street_2) {
		this.street_2 = street_2;
	}

	public String getCard_name() {
		return card_name;
	}

	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getPayment_spring_id() {
		return payment_spring_id;
	}

	public void setPayment_spring_id(String payment_spring_id) {
		this.payment_spring_id = payment_spring_id;
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

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public JSONArray getEmail_ids() {
		return email_ids;
	}

	public void setEmail_ids(JSONArray email_ids) {
		this.email_ids = email_ids;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCustomer_ein() {
		return customer_ein;
	}

	public void setCustomer_ein(String customer_ein) {
		this.customer_ein = customer_ein;
	}

	public String getCustomer_address() {
		return customer_address;
	}

	public void setCustomer_address(String customer_address) {
		this.customer_address = customer_address;
	}

	public String getCustomer_state() {
		return customer_state;
	}

	public void setCustomer_state(String customer_state) {
		this.customer_state = customer_state;
	}

	public String getCustomer_city() {
		return customer_city;
	}

	public void setCustomer_city(String customer_city) {
		this.customer_city = customer_city;
	}

	public String getCustomer_country() {
		return customer_country;
	}

	public void setCustomer_country(String customer_country) {
		this.customer_country = customer_country;
	}

	public String getCustomer_zipcode() {
		return customer_zipcode;
	}

	public void setCustomer_zipcode(String customer_zipcode) {
		this.customer_zipcode = customer_zipcode;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getCoa() {
		return coa;
	}

	public void setCoa(String coa) {
		this.coa = coa;
	}
	
	public String getJournalParametersString(){
		return this.customer_id;
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
