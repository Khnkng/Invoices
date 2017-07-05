package com.qount.invoice.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.qount.invoice.database.dao.impl.PaymentDAOImpl;
import com.qount.invoice.model.Payment;

public class PaymentService {
	
	private static PaymentService instance = new PaymentService();
	
	private PaymentService() {
		
	}
	
	public static PaymentService getInstance() {
		return instance;
	}

	public Payment createOrUpdatePayment(Payment payment, String companyId) {
		payment.setCompanyId(companyId);
		if(StringUtils.isBlank(payment.getId())) {
			payment.setId(UUID.randomUUID().toString());
		}
		return PaymentDAOImpl.getInstance().save(payment);
	}
	
	public List<Payment> getList(String companyId) {
		return PaymentDAOImpl.getInstance().list(companyId);
	}
}
