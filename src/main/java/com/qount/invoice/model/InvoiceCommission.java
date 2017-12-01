package com.qount.invoice.model;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 15 Nov 2017
 *
 */
/**
 * @author MateenAhmed
 *
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown=true)
public class InvoiceCommission {

	private String id;
	private String invoice_id;
	private double invoice_amount;
	private String event_type;
	private String event_at;
	private String event_date;
	private String bill_id;
	private String company_id;
	private String invoice_number;
	private String currency;
	private boolean billCreated;
	private String user_id;
	private long created_at;
	private long last_updated_at;
	private String last_updated_by;
	private String vendor_id;
	private boolean updateBill;
	private boolean createBill;
	private double amount;
	private String item_name;
	private String item_id;
	private String amount_type;
	private String billLineId;
	private boolean delete;
	 
	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public String getBillLineId() {
		return billLineId;
	}

	public void setBillLineId(String billLineId) {
		this.billLineId = billLineId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_id() {
		return item_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public String getAmount_type() {
		return amount_type;
	}

	public void setAmount_type(String amount_type) {
		this.amount_type = amount_type;
	}

	public boolean isCreateBill() {
		return createBill;
	}

	public void setCreateBill(boolean createBill) {
		this.createBill = createBill;
	}

	public boolean isUpdateBill() {
		return updateBill;
	}

	public void setUpdateBill(boolean updateBill) {
		this.updateBill = updateBill;
	}

	public String getVendor_id() {
		return vendor_id;
	}

	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}

	public long getLast_updated_at() {
		return last_updated_at;
	}

	public void setLast_updated_at(long last_updated_at) {
		this.last_updated_at = last_updated_at;
	}

	public String getLast_updated_by() {
		return last_updated_by;
	}

	public void setLast_updated_by(String last_updated_by) {
		this.last_updated_by = last_updated_by;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}

	public double getInvoice_amount() {
		return invoice_amount;
	}

	public void setInvoice_amount(double invoice_amount) {
		this.invoice_amount = invoice_amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isBillCreated() {
		return billCreated;
	}

	public void setBillCreated(boolean billCreated) {
		this.billCreated = billCreated;
	}

	public String getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(String invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getBill_id() {
		return bill_id;
	}

	public void setBill_id(String bill_id) {
		this.bill_id = bill_id;
	}

	public String getInvoice_number() {
		return invoice_number;
	}

	public void setInvoice_number(String invoice_number) {
		this.invoice_number = invoice_number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getEvent_at() {
		return event_at;
	}

	public void setEvent_at(String event_at) {
		this.event_at = event_at;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (null != obj && obj instanceof InvoiceCommission) {
				InvoiceCommission arg = (InvoiceCommission) obj;
				if (!StringUtils.isBlank(arg.getId()) && arg.getId().equals(this.getId())) {
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
	
	public static void main(String[] args) {
		System.out.println(new InvoiceCommission());
	}
}
