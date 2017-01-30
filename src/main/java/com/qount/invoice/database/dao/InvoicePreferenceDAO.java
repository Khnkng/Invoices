package com.qount.invoice.database.dao;

import java.sql.Connection;

import com.qount.invoice.model.InvoicePreference;

/**
 * DAO interface for InvoicePreferenceDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 25 Jan 2017
 *
 */
public interface InvoicePreferenceDAO {

	public InvoicePreference save(Connection connection, InvoicePreference invoicePreference);

	public InvoicePreference delete(Connection connection, InvoicePreference invoicePreference);

	public InvoicePreference getInvoiceByCompanyId(Connection connection, InvoicePreference invoicePreference);

	public InvoicePreference update(Connection connection, InvoicePreference invoicePreference);

}
