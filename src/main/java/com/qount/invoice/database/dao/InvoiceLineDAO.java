package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceLines;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceLineDAO {

	public boolean save(Connection connection, InvoiceLines invoiceLine);

	public List<InvoiceLines> getLines(Connection connection, String invoiceID);

	public boolean batchSave(Connection connection, List<InvoiceLines> invoiceLines);

	public boolean batchDelete(List<InvoiceLines> invoiceLines);

	public InvoiceLines deleteInvoiceLine(InvoiceLines invoiceLines);
	
	
}
