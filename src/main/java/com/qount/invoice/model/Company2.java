package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;
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
	private String companyType;
	@NotNull(message = "Default Currency is Mandatory")
	private String defaultCurrency;
	private String companyEmail;
	private List<BankInfo> paymentInfo;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private Set<String> roles;
	private Map<String, Boolean> canAcceptTerms =  new HashMap<String,Boolean>();;//it is in Role Model, to optimize the logic we are adding, it is not a part of this Class
	private List<String> modules;
	private List<String> owners;
	private String owner;//Only for CompanyEmail
	private List<String> accountManagers;
	private Boolean isActive;
	private String email_id;
	private List<Address> addresses;
	@NotNull(message = "Contact First Name is Mandatory")
	private String contact_first_name;
	@NotNull(message = "Contact Last Name is Mandatory")
	private String contact_last_name;
	@NotNull(message = "Contact Date Of Birth is Mandatory")
	private String contact_date_of_birth;
	private String lock_date;
	private String lock_id;
	private String fiscalStartDate;
	private String companyClassification;
	private Integer estimatedTaxPercentage;
	private Integer taxBracket;
	private String paystand_customer_id;
	private String personalTaxId;

	public List<Address> getAddresses() {
		if(addresses == null){
			addresses = new ArrayList<>();
		}
		return addresses;
	}

	public void setAddresses(List<Address> address) {
		this.addresses = address;
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

	public Set<String> getRoles() {
		if (roles == null) {
			roles = new HashSet<>();
		}
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public List<String> getOwners() {
		if (owners == null) {
			owners = new ArrayList<>();
		}
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public List<String> getAccountManagers() {
		if (accountManagers == null) {
			accountManagers = new ArrayList<>();
		}
		return accountManagers;
	}

	public void setAccountManagers(List<String> accountManagers) {
		this.accountManagers = accountManagers;
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

	public String getContact_date_of_birth() {
		return contact_date_of_birth;
	}

	public void setContact_date_of_birth(String contact_date_of_birth) {
		this.contact_date_of_birth = contact_date_of_birth;
	}

	public String getLock_date() {
		return lock_date;
	}

	public void setLock_date(String lock_date) {
		this.lock_date = lock_date;
	}

	public String getLock_id() {
		return lock_id;
	}

	public void setLock_id(String lock_id) {
		this.lock_id = lock_id;
	}

	public String getFiscalStartDate() {
		return fiscalStartDate;
	}

	public void setFiscalStartDate(String fiscalStartDate) {
		this.fiscalStartDate = fiscalStartDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCompanyClassification() {
		return companyClassification;
	}

	public void setCompanyClassification(String companyClassification) {
		this.companyClassification = companyClassification;
	}

	public Integer getEstimatedTaxPercentage() {
		return estimatedTaxPercentage;
	}

	public void setEstimatedTaxPercentage(Integer estimatedTaxPercentage) {
		this.estimatedTaxPercentage = estimatedTaxPercentage;
	}

	public Integer getTaxBracket() {
		return taxBracket;
	}

	public void setTaxBracket(Integer taxBracket) {
		this.taxBracket = taxBracket;
	}
 

	public Map<String, Boolean> getCanAcceptTerms() {
		return canAcceptTerms;
	}

	public void setCanAcceptTerms(Map<String, Boolean> canAcceptTerms) {
		this.canAcceptTerms = canAcceptTerms;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getPaystand_customer_id() {
		return paystand_customer_id;
	}

	public void setPaystand_customer_id(String paystand_customer_id) {
		this.paystand_customer_id = paystand_customer_id != null ? paystand_customer_id.trim() : null;
	}

	public String getPersonalTaxId() {
		return personalTaxId;
	}

	public void setPersonalTaxId(String personalTaxId) {
		this.personalTaxId = personalTaxId != null ? personalTaxId.trim() : null;
	}

}