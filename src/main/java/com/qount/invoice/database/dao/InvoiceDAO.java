package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.qount.invoice.model.Invoice;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceDAO {

	public Invoice save(Connection connection, Invoice invoice) throws Exception;;

	public Invoice update(Connection connection, Invoice invoice) throws Exception;;

	public Invoice updateState(Connection connection, Invoice invoice) throws Exception;;

	public Invoice get(String InvoiceID) throws Exception;;

	public Map<String, String> getCount(String userID, String companyID) throws Exception;;

	public List<Invoice> getInvoiceList(String userID, String companyID, String state) throws Exception;;

	public Invoice delete(Invoice invoice) throws Exception;

	public boolean deleteLst(String userId, String companyId, String lst) throws Exception;

	public boolean updateStateAsSent(String userId, String companyId, String lst) throws Exception;
	
	public Invoice updateInvoiceAsPaid(Connection connection, Invoice invoice) throws Exception;
}
