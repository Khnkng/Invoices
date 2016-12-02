package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceLineDAO;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceLineDAOImpl implements InvoiceLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private InvoiceLineDAOImpl() {
	}

	private static InvoiceLineDAOImpl invoiceLineDAOImpl = new InvoiceLineDAOImpl();

	public static InvoiceLineDAOImpl getInvoiceLineDAOImpl() {
		return invoiceLineDAOImpl;
	}

	@Override
	public boolean save(Connection connection, InvoiceLines invoiceLine) {
		boolean result = false;
		if (invoiceLine == null) {
			return result;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `invoice_lines` (`invoiceID`, `lineID`, `line_number`, `description`, `quantity`, `unit_cost`, `total_amount`) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `line_number` = ?, `description` = ?, `quantity` = ?, `unit_cost` = ?, `total_amount` = ?;";
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, invoiceLine.getInvoiceID());
				pstmt.setString(2, invoiceLine.getLineID());
				pstmt.setInt(3, invoiceLine.getLine_number());
				pstmt.setString(4, invoiceLine.getDescription());
				pstmt.setInt(5, invoiceLine.getQuantity());
				pstmt.setFloat(6, invoiceLine.getUnit_cost());
				pstmt.setFloat(7, invoiceLine.getTotal_amount());
				pstmt.setInt(8, invoiceLine.getLine_number());
				pstmt.setString(9, invoiceLine.getDescription());
				pstmt.setInt(10, invoiceLine.getQuantity());
				pstmt.setFloat(11, invoiceLine.getUnit_cost());
				pstmt.setFloat(12, invoiceLine.getTotal_amount());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug("invoice Line [" + invoiceLine.getInvoiceID() + " : " + invoiceLine.getLineID() + "]"
						+ " created");
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
	public List<InvoiceLines> getLines(Connection connection, String invoiceID) {
		List<InvoiceLines> invoiceLines = new ArrayList<>();
		if (StringUtils.isBlank(invoiceID)) {
			return invoiceLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT `invoiceID`,`lineID`,`line_number`,`description`,`quantity`,`unit_cost`,`total_amount` FROM invoice_lines WHERE `invoiceID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, invoiceID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceLines invoiceLine = new InvoiceLines();
					invoiceLine.setInvoiceID(rset.getString("invoiceID"));
					invoiceLine.setLineID(rset.getString("lineID"));
					invoiceLine.setLine_number(rset.getInt("line_number"));
					invoiceLine.setDescription(rset.getString("description"));
					invoiceLine.setQuantity(rset.getInt("quantity"));
					invoiceLine.setUnit_cost(rset.getFloat("unit_cost"));
					invoiceLine.setTotal_amount(rset.getFloat("total_amount"));
					invoiceLines.add(invoiceLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving propsal lines with ID = " + invoiceID + " x`", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceLines;
	}

	@Override
	public boolean batchSave(Connection connection, List<InvoiceLines> invoiceLines) {
		if (invoiceLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `invoice_lines` (`invoiceID`, `lineID`, `line_number`, `description`, `quantity`, `unit_cost`,`total_amount`) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `lineID` = ?, `line_number` = ?, `description` = ?, `quantity` = ?, `unit_cost` = ?, `total_amount` = ?";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				for (InvoiceLines invoiceLine : invoiceLines) {
					pstmt.setString(1, invoiceLine.getInvoiceID());
					pstmt.setString(2, invoiceLine.getLineID());
					pstmt.setInt(3, invoiceLine.getLine_number());
					pstmt.setString(4, invoiceLine.getDescription());
					pstmt.setInt(5, invoiceLine.getQuantity());
					pstmt.setFloat(6, invoiceLine.getUnit_cost());
					pstmt.setFloat(7, invoiceLine.getTotal_amount());
					pstmt.setString(8, invoiceLine.getLineID());
					pstmt.setInt(9, invoiceLine.getLine_number());
					pstmt.setString(10, invoiceLine.getDescription());
					pstmt.setInt(11, invoiceLine.getQuantity());
					pstmt.setFloat(12, invoiceLine.getUnit_cost());
					pstmt.setFloat(13, invoiceLine.getTotal_amount());
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
	public boolean batchDelete(Connection connection, List<InvoiceLines> invoiceLines) {
		if (invoiceLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM `invoice_lines` WHERE `invoiceID` = ? AND `lineID` = ?";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				for (InvoiceLines invoiceLine : invoiceLines) {
					pstmt.setString(1, invoiceLine.getInvoiceID());
					pstmt.setString(2, invoiceLine.getLineID());
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
	public boolean batchSaveAndDelete(Connection connection, List<InvoiceLines> invoiceLines,
			List<InvoiceLines> deletionLines) {
		if (batchDelete(connection,deletionLines)) {
			return batchSave(connection,invoiceLines);
		}
		return false;
	}

}
