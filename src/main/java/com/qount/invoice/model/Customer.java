package com.qount.invoice.model;

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
	private String email_id;
	private String customer_name;
	private String customer_ein;
	private String customer_address;
	private String customer_state;
	private String customer_city;
	private String customer_country;
	private String customer_zipcode;
	private String phone_number;
	private String coa;

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

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
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

}
