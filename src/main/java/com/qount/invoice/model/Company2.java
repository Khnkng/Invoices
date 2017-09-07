package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.qount.invoice.utils.Constants;

/**
 * pojo for Companies table
 * 
 * @author Mateen, Qount.
 * @version 1.0, 23 Nov 2016
 *
 */
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company2 implements Cloneable {

	private String id;
	private String name;
	private String einNumber;
	private String defaultCurrency;
	private String companyEmail;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private Boolean isActive;
	private String email_id;
	private List<Address> addresses;
	private String contact_first_name;
	private String contact_last_name;

	public List<Address> getAddresses() {
		if(addresses == null){
			addresses = new ArrayList<>();
		}
		return addresses;
	}

	public void setAddresses(List<Address> address) {
		this.addresses = address;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getEinNumber() {
		return einNumber;
	}

	public void setEinNumber(String einNumber) {
		this.einNumber = einNumber;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}


	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this).toString();
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}


	@SuppressWarnings("serial")
	public List<BankInfo> getBankInfoFromString(String bankInfoString) {
		if (StringUtils.isNotBlank(bankInfoString)) {
			try {
				return Constants.GSON.fromJson(bankInfoString, new TypeToken<ArrayList<BankInfo>>() {
				}.getType());
			} catch (Exception e) {
				System.out.println("bankInfoString = " + bankInfoString);
				e.printStackTrace();
			}
		}
		return null;
	}


	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return ((Company2) obj).getId().equals(this.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).toHashCode();
	}

	public String getContact_first_name() {
		return contact_first_name;
	}

	public void setContact_first_name(String contact_first_name) {
		this.contact_first_name = contact_first_name;
	}

	public String getContact_last_name() {
		return contact_last_name;
	}

	public void setContact_last_name(String contact_last_name) {
		this.contact_last_name = contact_last_name;
	}


	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}


}