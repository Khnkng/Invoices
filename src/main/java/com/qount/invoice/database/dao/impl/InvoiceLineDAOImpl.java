package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceLineDAO;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceLineDAOImpl implements InvoiceLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private InvoiceLineDAOImpl() {
	}

	private static InvoiceLineDAOImpl invoiceLineDAOImpl = new InvoiceLineDAOImpl();

	public static InvoiceLineDAOImpl getInvoiceLineDAOImpl() {
		return invoiceLineDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`) values (?,?,?,?,?,?,?,?,?,?,?);";
	private final static String GET_LINES_QRY = "SELECT `id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes` FROM invoice_lines WHERE `invoice_id` = ?;";
	private final static String DELETE_INVOICE_LINE_QRY = "DELETE FROM `invoice_lines` WHERE `id` = ?";
	private final static String DELETE_INVOICE_BY_ID_QRY = "DELETE FROM `invoice_lines` WHERE `invoice_id` = ?";

	@Override
	public List<InvoiceLine> getByInvoiceId(Connection connection, InvoiceLine invoiceLine) {
		List<InvoiceLine> invoiceLines = new ArrayList<>();
		if (invoiceLine == null || StringUtils.isBlank(invoiceLine.getInvoice_id())) {
			return invoiceLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_LINES_QRY);
				pstmt.setString(1, invoiceLine.getInvoice_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					invoiceLine.setId(rset.getString("id"));
					invoiceLine.setInvoice_id(rset.getString("invoice_id"));
					invoiceLine.setDescription(rset.getString("description"));
					invoiceLine.setObjectives(rset.getString("objectives"));
					invoiceLine.setAmount(rset.getDouble("amount"));
					invoiceLine.setCurrency(rset.getString("currency"));
					invoiceLine.setLast_updated_at(rset.getString("last_updated_at"));
					invoiceLine.setLast_updated_by(rset.getString("last_updated_by"));
					invoiceLine.setQuantity(rset.getDouble("quantity"));
					invoiceLine.setPrice(rset.getDouble("price"));
					invoiceLine.setNotes(rset.getString("notes"));
					invoiceLines.add(invoiceLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving invoice lines with ID = " + invoiceLine.getInvoice_id(), e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceLines;
	}

	@Override
	public List<InvoiceLine> save(Connection connection, List<InvoiceLine> invoiceLines) {
		if (invoiceLines == null || invoiceLines.size() == 0) {
			return invoiceLines;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<InvoiceLine> invoiceLineItr = invoiceLines.iterator();
				int ctr = 1;
				while (invoiceLineItr.hasNext()) {
					InvoiceLine invoiceLine = invoiceLineItr.next();
					pstmt.setString(ctr++, invoiceLine.getId());
					pstmt.setString(ctr++, invoiceLine.getInvoice_id());
					pstmt.setString(ctr++, invoiceLine.getDescription());
					pstmt.setString(ctr++, invoiceLine.getObjectives());
					pstmt.setDouble(ctr++, invoiceLine.getAmount());
					pstmt.setString(ctr++, invoiceLine.getCurrency());
					pstmt.setString(ctr++, invoiceLine.getLast_updated_by());
					pstmt.setString(ctr++, invoiceLine.getLast_updated_at());
					pstmt.setDouble(ctr++, invoiceLine.getQuantity());
					pstmt.setDouble(ctr++, invoiceLine.getPrice());
					pstmt.setString(ctr++, invoiceLine.getNotes());
					ctr = 1;
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return invoiceLines;
				} else {
					throw new WebApplicationException("unable to create invoice lines", 500);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceLines;
	}

	@Override
	public InvoiceLine deleteByInvoiceId(InvoiceLine invoiceLine) {
		Connection connection = null;
		if (invoiceLine == null || StringUtils.isBlank(invoiceLine.getInvoice_id())) {
			return invoiceLine;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_INVOICE_BY_ID_QRY);
				pstmt.setString(1, invoiceLine.getInvoice_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return invoiceLine;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceLine;
	}

	@Override
	public InvoiceLine deleteInvoiceLine(InvoiceLine invoiceLines) {
		Connection connection = null;
		if (invoiceLines == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_INVOICE_LINE_QRY);
				pstmt.setString(1, invoiceLines.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice lines deleted:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice lines:" + invoiceLines.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoiceLines;
	}

}
