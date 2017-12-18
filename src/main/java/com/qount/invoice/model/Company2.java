package com.qount.invoice.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company2 implements Cloneable {

	private String id;
	private String name;
	private String einNumber;
	private String companyType;
	private String phoneNumber;
	private String address;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	private String defaultCurrency;
	private String companyEmail;
	private List<BankInfo> paymentInfo;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private Set<String> roles;
	private List<String> modules;
	private String owner;
	private boolean isActive;
	private List<Address> addresses;
	private String fiscalStartDate ;
	private int taxBracket;
	private String reportCurrency;
	private float conversionValue;
	
	public int getTaxBracket() {
		return taxBracket;
	}

	public void setTaxBracket(int taxBracket) {
		this.taxBracket = taxBracket;
	}

	public List<String> getModules() {
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
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

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public List<BankInfo> getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(List<BankInfo> paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this).toString();
		} catch (JsonProcessingException e) {
			return super.toString();
		}
	}

	public String getBankInfoAsString() {
		String bankInfoString = null;
		if (paymentInfo != null) {
			bankInfoString = Constants.GSON.toJson(paymentInfo);
		}
		return bankInfoString;
	}

	@SuppressWarnings("serial")
	public List<BankInfo> getBankInfoFromString(String bankInfoString) {
		if (StringUtils.isNotBlank(bankInfoString)) {
			return Constants.GSON.fromJson(bankInfoString, new TypeToken<ArrayList<BankInfo>>() {
			}.getType());
		}
		return null;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public String getFiscalStartDate() {
		return fiscalStartDate;
	}

	public void setFiscalStartDate(String fiscalStartDate) {
		this.fiscalStartDate = fiscalStartDate;
	}

	public String getReportCurrency() {
		return reportCurrency;
	}

	public void setReportCurrency(String reportCurrency) {
		this.reportCurrency = reportCurrency;
	}

	public float getConversionValue() {
		return conversionValue;
	}

	public void setConversionValue(float conversionValue) {
		this.conversionValue = conversionValue;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			return ((Company) obj).getId().equals(this.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(id).toHashCode();
	}
}
