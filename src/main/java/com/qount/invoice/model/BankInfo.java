package com.qount.invoice.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.reflect.TypeToken;
import com.qount.invoice.utils.Constants;

@XmlRootElement
public class BankInfo {

	private String name;
	
	private String nickName;
	
	private String paymentType;

	private String type;

	private String number;

	private String cvv;

	private String expiryDate;

	private String bankName;

	private String accountNumber;

	private String routingNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	/**
	 * 
	 * @param paymentInfo
	 * @return
	 */
	public static String getBankDetailsAsString(List<BankInfo> paymentInfo) {
		String bankInfoString = null;
		if (paymentInfo != null) {
			bankInfoString = Constants.GSON.toJson(paymentInfo);
		}
		return bankInfoString;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * 
	 * @param paymentInfoString
	 * @return
	 */
	public static List<BankInfo> get(String paymentInfoString) {
		List<BankInfo> bankInfo = null;
		if (StringUtils.isNotBlank(paymentInfoString)) {
			bankInfo = Constants.GSON.fromJson(paymentInfoString, new TypeToken<ArrayList<BankInfo>>() {
			}.getType());
		}
		return bankInfo;
	}

}
