package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceDAOImpl implements InvoiceDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceDAOImpl.class);

	private InvoiceDAOImpl() {
	}

	private static InvoiceDAOImpl invoiceDAOImpl = new InvoiceDAOImpl();

	public static InvoiceDAOImpl getInvoiceDAOImpl() {
		return invoiceDAOImpl;
	}
	
	private static final String INSERT_QRY = "insert into `invoice` (`id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`invoice_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`is_recurring`,`recurring_frequency`,`recurring_frequency_value`,`recurring_start_date`,`recurring_end_date`,`is_mails_automated`,`is_cc_current_user`,`payment_spring_customer_id`,`po_number`,`document_id`,`amount_due`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private final static String DELETE_QRY = "DELETE FROM invoices WHERE `id`=?;";
	private final static String GET_QRY = " SELECT invoices.*, invoice_lines.id AS invoice_line_id, invoice_lines.invoice_id, invoice_lines.description, invoice_lines.objectives, invoice_lines.amount, invoice_lines.currency,invoice_lines.last_updated_by,invoice_lines.last_updated_at FROM invoices INNER JOIN invoice_lines ON invoices.id=invoice_lines.invoice_id WHERE invoices.`id` = ? AND invoices.`user_id` = ?;";
	private final static String GET_INVOICES_LIST_QRY =" SELECT `id`,`proposal_id`, `company_id`, `company_name`, `user_id`,`description`,`objectives`,`due_date`,`amount`,`currency`,`status`,`terms`,`recurring`,`start_date`,`end_date`,`recurring_frequency`,`number_of_invoices`,`last_updated_at`,`last_updated_by`,`payment_spring_customer_id`,`transaction_id` FROM invoices WHERE `user_id`=?;";

	@Override
	public boolean save(Connection connection, Invoice invoice) {
		boolean result = false;
		if (invoice == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr=1;
				pstmt = connection.prepareStatement(INSERT_QRY);
				pstmt.setString(ctr++,invoice.getId());
				pstmt.setString(ctr++,invoice.getUser_id());
				pstmt.setString(ctr++,invoice.getCompany_id());
				pstmt.setString(ctr++,invoice.getCompany_name());
				pstmt.setDouble(ctr++,invoice.getAmount());
				pstmt.setString(ctr++,invoice.getCurrency());
				pstmt.setString(ctr++,invoice.getDescription());
				pstmt.setString(ctr++,invoice.getObjectives());
				pstmt.setString(ctr++,invoice.getLast_updated_by());
				pstmt.setString(ctr++,invoice.getLast_updated_at());
				pstmt.setString(ctr++,invoice.getFirst_name());
				pstmt.setString(ctr++,invoice.getLast_name());
				pstmt.setString(ctr++,invoice.getState());
				pstmt.setString(ctr++,invoice.getInvoice_date());
				pstmt.setString(ctr++,invoice.getAcceptance_date());
				pstmt.setString(ctr++,invoice.getAcceptance_final_date());
				pstmt.setString(ctr++,invoice.getNotes());
				pstmt.setString(ctr++,invoice.getItem_id());
				pstmt.setString(ctr++,invoice.getItem_name());
				pstmt.setString(ctr++,invoice.getCoa_id());
				pstmt.setString(ctr++,invoice.getCoa_name());
				pstmt.setDouble(ctr++,invoice.getDiscount());
				pstmt.setDouble(ctr++,invoice.getDeposit_amount());
				pstmt.setDouble(ctr++,invoice.getProcessing_fees());
				pstmt.setString(ctr++,invoice.getRemainder_json());
				pstmt.setString(ctr++,invoice.getRemainder_mail_json());
				pstmt.setBoolean(ctr++,invoice.is_recurring());
				pstmt.setString(ctr++,invoice.getRecurring_frequency());
				pstmt.setDouble(ctr++,invoice.getRecurring_frequency_value());
				pstmt.setString(ctr++,invoice.getRecurring_start_date());
				pstmt.setString(ctr++,invoice.getRecurring_end_date());
				pstmt.setBoolean(ctr++,invoice.is_mails_automated());
				pstmt.setBoolean(ctr++,invoice.is_cc_current_user());
				pstmt.setString(ctr++,invoice.getPayment_spring_customer_id());
				pstmt.setString(ctr++,invoice.getPo_number());
				pstmt.setString(ctr++,invoice.getDocument_id());
				pstmt.setDouble(ctr++,invoice.getAmount_due());
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
	public Invoice getInvoiceById(String invoiceID, String userID) {
		Invoice invoice = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_QRY);
				pstmt.setString(1, invoiceID);
				pstmt.setString(2, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					invoice = new Invoice();
					invoice.setId(invoiceID);
					invoice.setProposal_id(rset.getString("proposal_id"));
					invoice.setCompany_id(rset.getString("company_id"));
					invoice.setCompany_name(rset.getString("company_name"));
					invoice.setCompany_id(userID);
					invoice.setDescription(rset.getString("description"));
					invoice.setObjectives(rset.getString("objectives"));
					invoice.setDue_date(rset.getString("due_date"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setStatus(rset.getString("status"));
					invoice.setTerms(rset.getString("terms"));
					invoice.setRecurring(rset.getBoolean("recurring"));
					invoice.setStart_date(rset.getString("start_date"));
					invoice.setEnd_date(rset.getString("end_date"));
					invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
					invoice.setNumber_of_invoices(rset.getInt("number_of_invoices"));
					invoice.setLast_updated_by(rset.getString("last_updated_by"));
					invoice.setLast_updated_at(rset.getString("last_updated_at"));
					invoice.setPayment_spring_customer_id(rset.getString("payment_spring_customer_id"));
					invoice.setTransaction_id(rset.getString("transaction_id"));
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setId(rset.getString("invoice_line_id"));
					invoiceLine.setInvoice_id(rset.getString("invoice_id"));
					invoiceLine.setDescription(rset.getString("description"));
					invoiceLine.setObjectives(rset.getString("objectives"));
					invoiceLine.setAmount(rset.getDouble("amount"));
					invoiceLine.setCurrency(rset.getString("currency"));
					invoiceLine.setLast_updated_by(rset.getString("last_updated_by"));
					invoiceLine.setLast_updated_at(rset.getString("last_updated_at"));
					invoice.getInvoiceLines().add(invoiceLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice for userID [ " + userID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoice;

	}

	@Override
	public List<Invoice> getInvoiceList(String userID) {
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_INVOICES_LIST_QRY);
				pstmt.setString(1, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setId(rset.getString("id"));
					invoice.setProposal_id(rset.getString("proposal_id"));
					invoice.setCompany_id(rset.getString("company_id"));
					invoice.setCompany_name(rset.getString("company_name"));
					invoice.setUser_id(rset.getString("user_id"));
					invoice.setDescription(rset.getString("description"));
					invoice.setObjectives(rset.getString("objectives"));
					invoice.setDue_date(rset.getString("due_date"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setStatus(rset.getString("status"));
					invoice.setTerms(rset.getString("terms"));
					invoice.setRecurring(rset.getBoolean("recurring"));
					invoice.setStart_date(rset.getString("start_date"));
					invoice.setEnd_date(rset.getString("end_date"));
					invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
					invoice.setNumber_of_invoices(rset.getInt("number_of_invoices"));
					invoice.setLast_updated_at(rset.getString("last_updated_at"));
					invoice.setLast_updated_by(rset.getString("last_updated_by"));
					invoice.setPayment_spring_customer_id(rset.getString("payment_spring_customer_id"));
					invoice.setTransaction_id(rset.getString("transaction_id"));
					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposals for user_id [ " + userID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoiceLst;

	}

	@Override
	public Invoice delete(Invoice invoice) {
		Connection connection = null;
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(1, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice deleted:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice:" + invoice.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoice;
	}


	@Override
	public boolean deleteAndCreateInvoice(Connection connection, String invoiceId, Invoice invoice) {
		boolean result = false;
		if (StringUtils.isBlank(invoiceId) && invoice == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			if (connection != null) {
				pstmt2 = connection.prepareStatement(DELETE_QRY);
				pstmt2.setString(1, invoiceId);
				int rowCount = pstmt2.executeUpdate();
				LOGGER.debug("no of proposal deleted:" + rowCount);
				if (rowCount > 0) {
					pstmt = connection.prepareStatement(INSERT_QRY);
					pstmt.setString(1, invoice.getId());
					pstmt.setString(2,invoice.getProposal_id());
					pstmt.setString(3, invoice.getCompany_id());
					pstmt.setString(4, invoice.getCompany_name());
					pstmt.setString(5, invoice.getUser_id());
					pstmt.setString(6, invoice.getDescription());
					pstmt.setString(7, invoice.getObjectives());
					pstmt.setString(8, invoice.getDue_date());
					pstmt.setDouble(9, invoice.getAmount());
					pstmt.setString(10, invoice.getCurrency());
					pstmt.setString(11, invoice.getStatus());
					pstmt.setString(12, invoice.getTerms());
					pstmt.setBoolean(13, invoice.isRecurring());
					pstmt.setString(14, invoice.getStart_date());
					pstmt.setString(15, invoice.getEnd_date());
					pstmt.setString(16, invoice.getRecurring_frequency());
					pstmt.setInt(17,invoice.getNumber_of_invoices());
					pstmt.setString(18, invoice.getLast_updated_at());
					pstmt.setString(19, invoice.getLast_updated_by());
					pstmt.setString(20, invoice.getPayment_spring_customer_id());
					pstmt.setString(21,invoice.getTransaction_id());
					int rowCount1 = pstmt.executeUpdate();
					result = rowCount1 != 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}
}
