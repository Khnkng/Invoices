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

	public List<InvoiceLine> getByInvoiceId(Connection connection, InvoiceLine invoiceLine);

	public List<InvoiceLine> save(Connection connection, List<InvoiceLine> invoiceLines) throws Exception;
	
	public InvoiceLine update(Connection connection, InvoiceLine invoiceLine) throws Exception;

	public InvoiceLine deleteByInvoiceId(Connection connection,InvoiceLine invoiceLines) throws Exception;

	public InvoiceLine deleteInvoiceLine(InvoiceLine invoiceLines) throws Exception;

}
