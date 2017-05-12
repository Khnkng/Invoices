package com.qount.invoice.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InvoicePreference {

	private String id;
	private String companyId;
	private String templateType;
	private String companyLogo;
	private boolean displayLogo;
	private String accentColor;
	private String defaultPaymentTerms;
	private String defaultTitle;
	private String defaultSubHeading;
	private String defaultFooter;
	private String standardMemo;
	private String items;
	private String units;
	private String price;
	private String amount;
	private boolean hideItemName;
	private boolean hideItemDescription;
	private boolean hideUnits;
	private boolean hidePrice;
	private boolean hideAmount;

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public boolean isDisplayLogo() {
		return displayLogo;
	}

	public void setDisplayLogo(boolean displayLogo) {
		this.displayLogo = displayLogo;
	}

	public String getAccentColor() {
		return accentColor;
	}

	public void setAccentColor(String accentColor) {
		this.accentColor = accentColor;
	}

	public String getDefaultPaymentTerms() {
		return defaultPaymentTerms;
	}

	public void setDefaultPaymentTerms(String defaultPaymentTerms) {
		this.defaultPaymentTerms = defaultPaymentTerms;
	}

	public String getDefaultTitle() {
		return defaultTitle;
	}

	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

	public String getDefaultSubHeading() {
		return defaultSubHeading;
	}

	public void setDefaultSubHeading(String defaultSubHeading) {
		this.defaultSubHeading = defaultSubHeading;
	}

	public String getDefaultFooter() {
		return defaultFooter;
	}

	public void setDefaultFooter(String defaultFooter) {
		this.defaultFooter = defaultFooter;
	}

	public String getStandardMemo() {
		return standardMemo;
	}

	public void setStandardMemo(String standardMemo) {
		this.standardMemo = standardMemo;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public boolean isHideItemName() {
		return hideItemName;
	}

	public void setHideItemName(boolean hideItemName) {
		this.hideItemName = hideItemName;
	}

	public boolean isHideItemDescription() {
		return hideItemDescription;
	}

	public void setHideItemDescription(boolean hideItemDescription) {
		this.hideItemDescription = hideItemDescription;
	}

	public boolean isHideUnits() {
		return hideUnits;
	}

	public void setHideUnits(boolean hideUnits) {
		this.hideUnits = hideUnits;
	}

	public boolean isHidePrice() {
		return hidePrice;
	}

	public void setHidePrice(boolean hidePrice) {
		this.hidePrice = hidePrice;
	}

	public boolean isHideAmount() {
		return hideAmount;
	}

	public void setHideAmount(boolean hideAmount) {
		this.hideAmount = hideAmount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (obj != null && obj instanceof InvoicePreference) {
				InvoicePreference arg = (InvoicePreference) obj;
				if (arg.getId().equals(this.getId())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.equals(obj);
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
