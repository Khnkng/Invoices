package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Invoice;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceDAO {

	public boolean save(Connection connection, Invoice invoice);

	public boolean deleteAndCreateInvoice(Connection connection,String proposalId, Invoice invoice);

	public Invoice getInvoiceById(String InvoiceID,String userID);

	public List<Invoice> getInvoiceList(String userID);
	
	public Invoice delete(Invoice invoice);
	


}
