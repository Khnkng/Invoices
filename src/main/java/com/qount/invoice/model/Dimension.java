package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class Dimension {
	
	private String companyID;
	
	@NotNull(message = "Invalid Dimension Name")
	private String name;
	
	@NotNull(message = "Invalid Dimension values")
	private List<String> values;
	
	private String value;

	private String createdBY;

	private String modifiedBy;

	private long createdDate;

	private long modifiedDate;
	
	private String desc;
	
	private String invoiceLineID;

	public String getCompanyID() {
		return companyID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		if(values == null){
			values = new ArrayList<>();
		}
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCreatedBY() {
		return createdBY;
	}

	public void setCreatedBY(String createdBY) {
		this.createdBY = createdBY;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public long getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getInvoiceLineID() {
		return invoiceLineID;
	}

	public void setInvoiceLineID(String invoiceLineID) {
		this.invoiceLineID = invoiceLineID;
	}
	
	public String prepareJSParemeters(){
		StringBuilder journalParmBuilder = new StringBuilder();
		journalParmBuilder.append(this.name + this.value);
		return journalParmBuilder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null){
			Dimension dimension =  (Dimension) obj;
			return dimension.getName().equalsIgnoreCase(this.name);
		}
		return false;
	}

}
