package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoicePayment;

/**
 * DAO interface for invoicePayment
 * 
 * @author Mateen, Qount.
 * @version 1.0, 22 May 2017
 *
 */
public interface InvoicePaymentDAO {

	public InvoicePayment save(Connection connection, InvoicePayment invoice);
	
	public InvoicePayment getById(InvoicePayment invoice);
	
	public List<InvoicePayment> getByInvoiceId(InvoicePayment invoice);
	
}
