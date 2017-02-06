package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceLineTaxes;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Feb 2016
 *
 */
public interface InvoiceLineTaxesDAO {

	public List<InvoiceLineTaxes> save(Connection connection, List<InvoiceLineTaxes> InvoiceLinesTaxes);

	public InvoiceLineTaxes deleteByLineId(InvoiceLineTaxes InvoiceLineTaxes);

}
