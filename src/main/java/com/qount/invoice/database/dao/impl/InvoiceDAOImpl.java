package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceDAOImpl implements InvoiceDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceDAOImpl.class);

	private InvoiceDAOImpl() {
	}

	private static InvoiceDAOImpl invoiceDAOImpl = new InvoiceDAOImpl();

	public static InvoiceDAOImpl getInvoiceDAOImpl() {
		return invoiceDAOImpl;
	}

	@Override
	public boolean save(Connection connection, Invoice invoice) {
		boolean result = false;
		if (invoice == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `invoices` (`invoiceID`, `companyID`, `customer_name`, `userID`, `invoice_date`, `due_date`, `invoice_amount`, `invoice_status`,`bank_account`,`credit_card`,`terms`,`currencyID`,`recurring`,`start_date`,`end_date`,`recurring_frequency`,`number_of_invoices`) VALUES (?,?,?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, invoice.getId());
				pstmt.setString(2, invoice.getCompany_id());
				pstmt.setString(3, invoice.getCustomer_name());
				pstmt.setString(4, invoice.getUser_id());
				pstmt.setString(6, invoice.getDue_date());
				pstmt.setString(7, invoice.getInvoice_amount());
				pstmt.setString(8, invoice.getInvoice_status());
				pstmt.setString(11, invoice.getTerms());
				pstmt.setString(12, invoice.getCurrency());
				pstmt.setBoolean(13, invoice.isRecurring());
				pstmt.setString(14, invoice.getStart_date());
				pstmt.setString(15, invoice.getEnd_date());
				pstmt.setString(16, invoice.getRecurring_frequency());
				pstmt.setInt(17, invoice.getNumber_of_invoices());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug("invoice [" + invoice.getCompany_id() + " : " + invoice.getId() + "]" + " created");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean update(Connection connection, Invoice invoice) {
		boolean result = false;
		if (invoice == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		String sql = "UPDATE invoices SET  `customer_name` = ?, `userID`= ?, `invoice_date`= ?, `due_date`= ?, `invoice_amount`= ?, `invoice_status`= ?,`bank_account`= ? , `credit_card` = ?,`terms`= ?,`currencyID`= ?,`recurring`= ?,`start_date`= ?,`end_date`= ?,`recurring_frequency`= ?,`number_of_invoices`= ? WHERE `invoiceID` = ? AND `companyID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, invoice.getCustomer_name());
				pstmt.setString(2, invoice.getUser_id());
				pstmt.setString(4, invoice.getDue_date());
				pstmt.setString(5, invoice.getInvoice_amount());
				pstmt.setString(6, invoice.getInvoice_status());
				pstmt.setString(9, invoice.getTerms());
				pstmt.setString(10, invoice.getCurrency());
				pstmt.setBoolean(11, invoice.isRecurring());
				pstmt.setString(12, invoice.getStart_date());
				pstmt.setString(13, invoice.getEnd_date());
				pstmt.setString(14, invoice.getRecurring_frequency());
				pstmt.setInt(15, invoice.getNumber_of_invoices());
				pstmt.setString(16, invoice.getId());
				pstmt.setString(17, invoice.getCompany_id());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug("invoice [" + invoice.getCompany_id() + " : " + invoice.getId() + "]" + " updated");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public Invoice get(Connection connection, String companyID, String invoiceID, String userID) {
		Invoice invoice = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT invoices.*, invoice_lines.lineID, invoice_lines.line_number, invoice_lines.description, invoice_lines.quantity,invoice_lines.unit_cost, invoice_lines.total_amount FROM invoices INNER JOIN invoice_lines ON invoices.invoiceID=invoice_lines.invoiceID WHERE invoices.`companyID` = ? AND invoices.`invoiceID` = ? AND invoices.`userID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, companyID);
				pstmt.setString(2, invoiceID);
				pstmt.setString(3, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					if (invoice == null) {
						invoice = new Invoice();
						invoice.setId(invoiceID);
						invoice.setCompany_id(companyID);
						invoice.setCompany_id(userID);
						invoice.setCustomer_name(rset.getString("customer_name"));
						invoice.setDue_date(rset.getString("due_date"));
						invoice.setInvoice_amount(rset.getString("invoice_amount"));
						invoice.setInvoice_status(rset.getString("invoice_status"));
						invoice.setTerms(rset.getString("terms"));
						invoice.setCurrency(rset.getString("currencyID"));
						invoice.setRecurring(rset.getBoolean("recurring"));
						invoice.setStart_date(rset.getString("start_date"));
						invoice.setEnd_date(rset.getString("end_date"));
						invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
						invoice.setNumber_of_invoices(rset.getInt("number_of_invoices"));

					}
					InvoiceLines invoiceLine = new InvoiceLines();
					invoiceLine.setInvoiceID(rset.getString("invoiceID"));
					invoiceLine.setLineID(rset.getString("lineID"));
					invoiceLine.setLine_number(rset.getInt("line_number"));
					invoiceLine.setDescription(rset.getString("description"));
					invoiceLine.setQuantity(rset.getInt("quantity"));
					invoiceLine.setUnit_cost(rset.getFloat("unit_cost"));
					invoiceLine.setTotal_amount(rset.getFloat("total_amount"));
					invoice.getInvoiceLines().add(invoiceLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice for CompanyID [ " + companyID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoice;

	}

	@Override
	public List<Invoice> getList(Connection connection, String companyID) {
		List<Invoice> invoices = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT `invoiceID`, `companyID`, `customer_name`, `userID`, `invoice_date`, `due_date`, `invoice_amount`, `invoice_status`,`bank_account`,`credit_card`,`terms`,`currencyID`,`recurring`,`start_date`,`end_date`,`recurring_frequency`,`number_of_invoices` FROM invoices WHERE `companyID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, companyID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setId(rset.getString("invoiceID"));
					invoice.setCompany_id(companyID);
					invoice.setCompany_id(rset.getString("userID"));
					invoice.setCustomer_name(rset.getString("customer_name"));
					invoice.setDue_date(rset.getString("due_date"));
					invoice.setInvoice_amount(rset.getString("invoice_amount"));
					invoice.setInvoice_status(rset.getString("invoice_status"));
					invoice.setTerms(rset.getString("terms"));
					invoice.setCurrency(rset.getString("currencyID"));
					invoice.setRecurring(rset.getBoolean("recurring"));
					invoice.setStart_date(rset.getString("start_date"));
					invoice.setEnd_date(rset.getString("end_date"));
					invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
					invoice.setNumber_of_invoices(rset.getInt("number_of_invoices"));
					invoices.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for CompanyID [ " + companyID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}

		return invoices;

	}

	@Override
	public Invoice delete(Connection connection,Invoice invoice) {
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM invoices WHERE `companyID`=? AND `invoiceID`=?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, invoice.getCompany_id());
				pstmt.setString(2, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice deleted:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice:" + invoice.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoice;
	}
}
