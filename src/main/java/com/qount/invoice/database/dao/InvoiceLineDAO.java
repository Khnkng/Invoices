package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceLine;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceLineDAO {

	public boolean save(Connection connection, InvoiceLine invoiceLine);

	public List<InvoiceLine> getLines(Connection connection, String invoiceID);

	public boolean batchSave(Connection connection, List<InvoiceLine> invoiceLines);

	public boolean batchDelete(List<InvoiceLine> invoiceLines);

	public InvoiceLine deleteInvoiceLine(InvoiceLine invoiceLines);
	
	
}
