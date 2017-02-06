package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceTaxes;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Feb 2016
 *
 */
public interface InvoiceTaxesDAO {

	public List<InvoiceTaxes> save(Connection connection, String invoiceID, List<InvoiceTaxes> invoiceTaxes);

	public InvoiceTaxes deleteByInvoiceId(Connection connection, InvoiceTaxes invoiceTaxes);

	public List<InvoiceTaxes> getByInvoiceID(InvoiceTaxes invoiceTaxes);
}
