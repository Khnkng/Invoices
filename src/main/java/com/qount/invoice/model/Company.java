package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Business object used in CustomerDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 23 Feb 2016
 *
 */
public class Company {

	private String id;
	private String name;
	private String ein;
	private String type;
	private String phone_number;
	private String address;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	private String currency;
	private String email;
	private String payment_info;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private String owner;
	private boolean active;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEin() {
		return ein;
	}

	public String getType() {
		return type;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public String getCurrency() {
		return currency;
	}

	public String getEmail() {
		return email;
	}

	public String getPayment_info() {
		return payment_info;
	}

	public String getCreatedby() {
		return createdBy;
	}

	public String getModifiedby() {
		return modifiedBy;
	}

	public String getCreateddate() {
		return createdDate;
	}

	public String getModifieddate() {
		return modifiedDate;
	}

	public String getOwner() {
		return owner;
	}

	public boolean getActive() {
		return active;
	}

	public void setId(String id) {
		this.id=id;
	}

	public void setName(String name) {
		this.name=name;
	}

	public void setEin(String ein) {
		this.ein=ein;
	}

	public void setType(String type) {
		this.type=type;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number=phone_number;
	}

	public void setAddress(String address) {
		this.address=address;
	}

	public void setCity(String city) {
		this.city=city;
	}

	public void setState(String state) {
		this.state=state;
	}

	public void setCountry(String country) {
		this.country=country;
	}

	public void setZipcode(String zipcode) {
		this.zipcode=zipcode;
	}

	public void setCurrency(String currency) {
		this.currency=currency;
	}

	public void setEmail(String email) {
		this.email=email;
	}

	public void setPayment_info(String payment_info) {
		this.payment_info=payment_info;
	}

	public void setCreatedby(String createdBy) {
		this.createdBy=createdBy;
	}

	public void setModifiedby(String modifiedBy) {
		this.modifiedBy=modifiedBy;
	}

	public void setCreateddate(String createdDate) {
		this.createdDate=createdDate;
	}

	public void setModifieddate(String modifiedDate) {
		this.modifiedDate=modifiedDate;
	}

	public void setOwner(String owner) {
		this.owner=owner;
	}

	public void setActive(boolean active) {
		this.active=active;
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