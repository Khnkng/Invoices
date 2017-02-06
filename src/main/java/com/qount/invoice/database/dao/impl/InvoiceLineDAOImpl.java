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

	private final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	private final static String GET_LINES_QRY = "SELECT `id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at` FROM invoice_lines WHERE `invoice_id` = ?;";
	private final static String DELETE_INVOICE_LINE_QRY = "DELETE FROM `invoice_lines` WHERE `id` = ? AND `invoice_id` = ?";

	@Override
	public boolean save(Connection connection, InvoiceLine invoiceLine) {
		boolean result = false;
		if (invoiceLine == null) {
			return result;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(INSERT_QRY);
				pstmt.setString(1, invoiceLine.getId());
				pstmt.setString(2, invoiceLine.getInvoice_id());
				pstmt.setString(3, invoiceLine.getDescription());
				pstmt.setString(4, invoiceLine.getObjectives());
				pstmt.setDouble(5, invoiceLine.getAmount());
				pstmt.setString(6, invoiceLine.getCurrency());
				pstmt.setString(7, invoiceLine.getLast_updated_by());
				pstmt.setString(8, invoiceLine.getLast_updated_at());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
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
	public List<InvoiceLine> getLines(Connection connection, String invoiceID) {
		List<InvoiceLine> invoiceLines = new ArrayList<>();
		if (StringUtils.isBlank(invoiceID)) {
			return invoiceLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_LINES_QRY);
				pstmt.setString(1, invoiceID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setId(rset.getString("id"));
					invoiceLine.setInvoice_id(rset.getString("proposal_id"));
					invoiceLine.setDescription(rset.getString("description"));
					invoiceLine.setObjectives(rset.getString("objectives"));
					invoiceLine.setAmount(rset.getDouble("amount"));
					invoiceLine.setCurrency(rset.getString("currency"));
					invoiceLine.setLast_updated_at(rset.getString("last_updated_at"));
					invoiceLine.setLast_updated_by(rset.getString("last_updated_by"));
					invoiceLines.add(invoiceLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving invoice lines with ID = " + invoiceID, e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceLines;
	}

	@Override
	public boolean batchSave(Connection connection, List<InvoiceLine> invoiceLines) {
		if (invoiceLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<InvoiceLine> invoiceLineItr = invoiceLines.iterator();
				while (invoiceLineItr.hasNext()) {
					InvoiceLine invoiceLine = invoiceLineItr.next();
					pstmt.setString(1, invoiceLine.getId());
					pstmt.setString(2, invoiceLine.getInvoice_id());
					pstmt.setString(3, invoiceLine.getDescription());
					pstmt.setString(4, invoiceLine.getObjectives());
					pstmt.setDouble(5, invoiceLine.getAmount());
					pstmt.setString(6, invoiceLine.getCurrency());
					pstmt.setString(7, invoiceLine.getLast_updated_by());
					pstmt.setString(8, invoiceLine.getLast_updated_at());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					result = true;
				} else {
					throw new WebApplicationException("unable to create invoice lines", 500);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean batchDelete(List<InvoiceLine> invoiceLines) {
		Connection connection = null;
		if (invoiceLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_INVOICE_LINE_QRY);
				for (InvoiceLine invoiceLine : invoiceLines) {
					pstmt.setString(1, invoiceLine.getId());
					pstmt.setString(2, invoiceLine.getInvoice_id());
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				result = true;
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
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
				pstmt.setString(2, invoiceLines.getInvoice_id());
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
