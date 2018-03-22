package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceCommission;
import com.qount.invoice.model.InvoiceMetrics;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
public interface InvoiceDAO {

	public Invoice save(Connection connection, Invoice invoice) throws Exception;
	
	public boolean invoiceExists(Connection connection, String invoiceNumber, String companyId) throws Exception;
	
	public boolean invoiceExists(Connection connection, String invoiceNumber, String companyId, String id) throws Exception;

	public Invoice update(Connection connection, Invoice invoice) throws Exception;;
	
	public Invoice updateEmailState(Connection connection, Invoice invoice) throws Exception;;

	public Invoice updateState(Connection connection, Invoice invoice) throws Exception;;

	public Invoice get(String InvoiceID) throws Exception;
	
	public Invoice get(Connection connection, String InvoiceID) throws Exception;

	public Map<String, String> getCount(String userID, String companyID) throws Exception;;

	public List<Invoice> getInvoiceList(String userID, String companyID, String state) throws Exception;;
	
	public List<Invoice> getInvoiceListByCustomerID(String userID, String companyID, String customerID,String state) throws Exception;
	
	public List<Invoice> getInvoiceListByClientId(String userID, String companyID, String clientID) throws Exception;;

	public Invoice delete(Invoice invoice) throws Exception;
	
	public Invoice delete(Connection connection, Invoice invoice) throws Exception;

	public boolean deleteLst(String userId, String companyId, String lst) throws Exception;

	public boolean updateStateAsSent(String userId, String companyId, String lst) throws Exception;
	
	public Invoice updateInvoiceAsPaid(Connection connection, Invoice invoice) throws Exception;
	
	public InvoiceMetrics getInvoiceMetrics(String companyID)  throws Exception;
	
	public List<Invoice> saveInvoice(Connection connection, List<Invoice> invoice) throws Exception;
	
	public Invoice markAsPaid(Connection connection, Invoice invoice) throws Exception;
	
	public List<Invoice> getInvoices(String invoiceIds) throws Exception;
	
	public List<Invoice> retrieveInvoicesByCurrentStateAndCompany(String companyId, String query);
	
	public List<Invoice> retrieveInvoicesPaidInLast30Days(String companyId, String query);
	
	public List<String> getInvoiceJobsList(String invoiceIds) throws Exception;
	
	public List<String> getInvoiceJobsList(Connection connection, String invoiceIds) throws Exception;
	
	public Map<String,String> getInvoicePaymentsIds(String invoiceIds) throws Exception;
	
	public InvoiceCommission createInvoiceCommission(Connection connection, InvoiceCommission invoiceCommission) throws Exception;
	
	public InvoiceCommission deleteInvoiceCommission(Connection connection, InvoiceCommission invoiceCommission) throws Exception;
	
	public List<InvoiceCommission> getInvoiceCommissions(InvoiceCommission invoiceCommission) throws Exception;
	
	public InvoiceCommission updateInvoiceCommissionBillState(Connection connection, InvoiceCommission invoiceCommission) throws Exception;
	
	public double getLateFeeAmount(Connection connection, String lateFeeId, double invoiceAmount);

	List<Invoice> getUnmappedInvoiceList(String companyId, String customerID);

	List<Invoice> getMappedUnmappedInvoiceList(String companyId, String customerID, String billId);

	JSONArray getInvoiceListByFilter(Connection connection, String userID, String companyID, String query,
			String asOfDate) throws Exception;

	public int getUnappliedPaymentsCount(String userID, String companyID) throws Exception;
	
	public boolean deleteRemainderJobId(Connection connection, String invoiceId, String remainderJobId) throws Exception;

	public boolean batchupdate(Connection connection, List<Invoice> invoiceList) throws Exception;

	public List<Invoice> getByInQuery(Set<String> invoiceIDs) throws Exception;
}
