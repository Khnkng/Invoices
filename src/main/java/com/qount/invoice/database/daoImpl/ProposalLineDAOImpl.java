package com.qount.invoice.database.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalLineDAO;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalLineDAOImpl implements ProposalLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalLineDAOImpl() {
	}

	private static ProposalLineDAOImpl proposalLineDAOImpl = new ProposalLineDAOImpl();

	public static ProposalLineDAOImpl getProposalLineDAOImpl() {
		return proposalLineDAOImpl;
	}

	@Override
	public boolean save(Connection connection, ProposalLine proposalLine) {
		boolean result = false;
		if (proposalLine == null) {
			return result;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `proposal_lines` (`proposalID`, `lineID`, `line_number`, `description`, `quantity`, `unit_cost`, `total_amount`) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `line_number` = ?, `description` = ?, `quantity` = ?, `unit_cost` = ?, `total_amount` = ?;";
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, proposalLine.getProposalID());
				pstmt.setString(2, proposalLine.getLineID());
				pstmt.setInt(3, proposalLine.getLine_number());
				pstmt.setString(4, proposalLine.getDescription());
				pstmt.setInt(5, proposalLine.getQuantity());
				pstmt.setFloat(6, proposalLine.getUnit_cost());
				pstmt.setFloat(7, proposalLine.getTotal_amount());
				pstmt.setInt(8, proposalLine.getLine_number());
				pstmt.setString(9, proposalLine.getDescription());
				pstmt.setInt(10, proposalLine.getQuantity());
				pstmt.setFloat(11, proposalLine.getUnit_cost());
				pstmt.setFloat(12, proposalLine.getTotal_amount());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug("proposal Line [" + proposalLine.getProposalID() + " : " + proposalLine.getLineID() + "]"
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
	public List<ProposalLine> getLines(Connection connection, String proposalID) {
		List<ProposalLine> proposalLines = new ArrayList<>();
		if (StringUtils.isBlank(proposalID)) {
			return proposalLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT `proposalID`,`lineID`,`line_number`,`description`,`quantity`,`unit_cost`,`total_amount` FROM proposal_lines WHERE `proposalID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setProposalID(rset.getString("proposalID"));
					proposalLine.setLineID(rset.getString("lineID"));
					proposalLine.setLine_number(rset.getInt("line_number"));
					proposalLine.setDescription(rset.getString("description"));
					proposalLine.setQuantity(rset.getInt("quantity"));
					proposalLine.setUnit_cost(rset.getFloat("unit_cost"));
					proposalLine.setTotal_amount(rset.getFloat("total_amount"));
					proposalLines.add(proposalLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving propsal lines with ID = " + proposalID + " x`", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLines;
	}

	@Override
	public boolean batchSave(Connection connection, List<ProposalLine> proposalLines) {
		if (proposalLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `proposal_lines` (`proposalID`, `lineID`, `line_number`, `description`, `quantity`, `unit_cost`,`total_amount`) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `lineID` = ?, `line_number` = ?, `description` = ?, `quantity` = ?, `unit_cost` = ?, `total_amount` = ?";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				for (ProposalLine proposalLine : proposalLines) {
					pstmt.setString(1, proposalLine.getProposalID());
					pstmt.setString(2, proposalLine.getLineID());
					pstmt.setInt(3, proposalLine.getLine_number());
					pstmt.setString(4, proposalLine.getDescription());
					pstmt.setInt(5, proposalLine.getQuantity());
					pstmt.setFloat(6, proposalLine.getUnit_cost());
					pstmt.setFloat(7, proposalLine.getTotal_amount());
					pstmt.setString(8, proposalLine.getLineID());
					pstmt.setInt(9, proposalLine.getLine_number());
					pstmt.setString(10, proposalLine.getDescription());
					pstmt.setInt(11, proposalLine.getQuantity());
					pstmt.setFloat(12, proposalLine.getUnit_cost());
					pstmt.setFloat(13, proposalLine.getTotal_amount());
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
	public boolean batchDelete(Connection connection, List<ProposalLine> proposalLines) {
		if (proposalLines.size() == 0) {
			return true;
		}
		boolean result = false;
		PreparedStatement pstmt = null;
		String sql = "DELETE FROM `proposal_lines` WHERE `proposalID` = ? AND `lineID` = ?";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				for (ProposalLine proposalLine : proposalLines) {
					pstmt.setString(1, proposalLine.getProposalID());
					pstmt.setString(2, proposalLine.getLineID());
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
	public boolean batchSaveAndDelete(Connection connection, List<ProposalLine> proposalLines,
			List<ProposalLine> deletionLines) {
		if (batchDelete(connection,deletionLines)) {
			return batchSave(connection,proposalLines);
		}
		return false;
	}

}
