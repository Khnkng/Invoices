package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceMail;
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
	
	public Invoice updateState(Connection connection, Invoice invoice);

	public Invoice get(String InvoiceID);
	
	public Map<String, String> getCount(String userID,String companyID);
	
	public InvoiceMail getInvoiceMailDetails(String InvoiceID);

	public List<Invoice> getInvoiceList(String userID,String companyID, String state);
	
	public Invoice delete(Invoice invoice);
	
	public InvoiceReference getInvoiceRelatedDetails(Connection connection, InvoiceReference invoiceReference);

}
