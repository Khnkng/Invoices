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

	public boolean update(Connection connection, Invoice invoice);

	Invoice get(Connection connection, String companyID, String InvoiceID,String userID);

	List<Invoice> getList(Connection connection, String companyID);

}
