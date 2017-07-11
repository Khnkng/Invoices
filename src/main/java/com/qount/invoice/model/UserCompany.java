package com.qount.invoice.model;

/**
 * @author Apurva
 * @version 1.0 Feb 17 2017
 */
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserCompany implements Cloneable {

	private String userID;

	private String id;

	private String name;

	private String einNumber;

	private String address;

	private String info;

	private String routingNumber;

	private String companyEmail;

	private Set<String> invitedUserEmails;

	private Set<String> roles;

	private String invitedBy;

	private String country;

	private String state;

	private String phoneNumber;

	private String paymentDetails;

	private Set<String> expenseCodes = new HashSet<>();

	private Set<String> itemCodes = new HashSet<>();

	private String defaultCurrency;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEinNumber() {
		return einNumber;
	}

	public void setEinNumber(String einNumber) {
		this.einNumber = einNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public Set<String> getRoles() {
		if (roles == null) {
			roles = new HashSet<>();
		}
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public String getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(String invitedBy) {
		this.invitedBy = invitedBy;
	}

	public Set<String> getInvitedUserEmails() {
		if (invitedUserEmails == null) {
			invitedUserEmails = new HashSet<>();
		}
		return invitedUserEmails;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setInvitedUserEmails(Set<String> invitedUserEmails) {
		this.invitedUserEmails = invitedUserEmails;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public String getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public Set<String> getExpenseCodes() {
		return expenseCodes;
	}

	public void setExpenseCodes(Set<String> expenseCodes) {
		this.expenseCodes = expenseCodes;
	}

	public Set<String> getItemCodes() {
		return itemCodes;
	}

	public void setItemCodes(Set<String> itemCodes) {
		this.itemCodes = itemCodes;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
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
