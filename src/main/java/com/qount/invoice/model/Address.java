package com.qount.invoice.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Business object used in AddressesDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 12 Dec 2016
 *
 */
public class Address {

	private String address_id;
	private String source_id;
	@NotNull(message="Country is Mandatory")
	private String country;
	@NotNull(message="State is Mandatory")
	private String state;
	private String stateCode;
	@NotNull(message="City is Mandatory")
	private String city;
	private String pincode;
	@NotNull(message="ZipCode is Mandatory")
	@Min(message="ZipCode should be minimum 5 digits", value = 5)
	private String zipcode;
	
	@Size(min = 10, max = 10, message = "phone_number Length should be Min 10")
	@Pattern(message = "Invalid Phone Number Address->" +   "Valid phone Numbers:1234567890/123-456-7890/(123) 456 7890/123.456.7890 etc.", regexp = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$")
	private String phone_number;
	@NotNull(message="AddressLine is Mandatory")
	private String line;
	

	public String getAddress_id() {
		return address_id;
	}

	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}

	public String getSource_id() {
		return source_id;
	}

	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			Address address = (Address) obj;
			return address.getAddress_id().equalsIgnoreCase(this.address_id);
		}
		return false;
	}

}
