package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Payment;

public interface paymentDAO {
	
	public Payment save(Payment payment);
	public Payment save(Payment payment,Connection connection);
	public List<Payment> list(String companyId);
    public Payment getById(String paymentId);
}
