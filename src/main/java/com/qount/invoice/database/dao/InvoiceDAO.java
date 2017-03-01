package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.pdf.InvoiceReference;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceDAO {

	public Invoice save(Connection connection, Invoice invoice);
	
	public Invoice update(Connection connection, Invoice invoice);

	public Invoice get(String InvoiceID);

	public List<Invoice> getInvoiceList(String userID);
	
	public Invoice delete(Invoice invoice);
	
	public InvoiceReference getInvoiceRelatedDetails(Connection connection, InvoiceReference invoiceReference);

}
