package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.Payment;

public interface paymentDAO {
	
	public Payment save(Payment payment,Connection connection,boolean checkInvoiceAmountFlag);
	public List<Payment> list(String companyId);
    public Payment getById(String paymentId);
    public List<Payment> listByInvoiceId(String invoiceId);
	public List<Invoice> getIvoicesByPaymentID(String paymentId);
	public List<Payment> getUnappliedPayments(String companyID) throws Exception;
	public Payment update(Payment payment, Connection connection, String paymentID);
}
