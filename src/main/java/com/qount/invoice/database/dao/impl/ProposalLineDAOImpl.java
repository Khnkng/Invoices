package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalLineDAO;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class ProposalLineDAOImpl implements ProposalLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalLineDAOImpl() {
	}

	private static ProposalLineDAOImpl proposalLineDAOImpl = new ProposalLineDAOImpl();

	public static ProposalLineDAOImpl getProposalLineDAOImpl() {
		return proposalLineDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO `proposal_lines` (`id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`created_by`,`created_at`,`last_updated_by`,`last_updated_at`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	private final static String GET_LINES_QRY = " SELECT `id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`created_by`,`created_at`,`last_updated_by`,`last_updated_at` FROM proposal_lines WHERE `proposal_id` = ?;";
	private final static String DELETE_PROPOSAL_LINE_QRY = "DELETE FROM `proposal_lines` WHERE `id` = ? AND `proposal_id` = ?";

	@Override
	public boolean save(Connection connection, ProposalLine proposalLine) {
		boolean result = false;
		if (proposalLine == null) {
			return result;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(INSERT_QRY);
				pstmt.setString(1, proposalLine.getId());
				pstmt.setString(2, proposalLine.getProposal_id());
				pstmt.setString(3, proposalLine.getDescription());
				pstmt.setString(4, proposalLine.getObjectives());
				pstmt.setDouble(5, proposalLine.getAmount());
				pstmt.setString(6, proposalLine.getCurrency());
				pstmt.setString(7, proposalLine.getCreated_by());
				pstmt.setLong(8, proposalLine.getCreated_at());
				pstmt.setString(9, proposalLine.getLast_updated_by());
				pstmt.setLong(10, proposalLine.getLast_updated_at());
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
	public List<ProposalLine> getLines(Connection connection, String proposalID) {
		List<ProposalLine> proposalLines = new ArrayList<>();
		if (StringUtils.isBlank(proposalID)) {
			return proposalLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_LINES_QRY);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setId(rset.getString("id"));
					proposalLine.setProposal_id(rset.getString("proposal_id"));
					proposalLine.setDescription(rset.getString("description"));
					proposalLine.setObjectives(rset.getString("objectives"));
					proposalLine.setAmount(rset.getDouble("amount"));
					proposalLine.setCurrency(rset.getString("currency"));
					proposalLine.setCreated_at(rset.getLong("created_at"));
					proposalLine.setCreated_by(rset.getString("created_by"));
					proposalLine.setLast_updated_at(rset.getLong("last_updated_at"));
					proposalLine.setLast_updated_by(rset.getString("last_updated_by"));
					proposalLines.add(proposalLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving propsal lines with ID = " + proposalID, e);
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
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				for (ProposalLine proposalLine : proposalLines) {
					pstmt.setString(1, proposalLine.getId());
					pstmt.setString(2, proposalLine.getProposal_id());
					pstmt.setString(3, proposalLine.getDescription());
					pstmt.setString(4, proposalLine.getObjectives());
					pstmt.setDouble(5, proposalLine.getAmount());
					pstmt.setString(6, proposalLine.getCurrency());
					pstmt.setString(7, proposalLine.getCreated_by());
					pstmt.setLong(8, proposalLine.getCreated_at());
					pstmt.setString(9, proposalLine.getLast_updated_by());
					pstmt.setLong(10, proposalLine.getLast_updated_at());
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
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_PROPOSAL_LINE_QRY);
				for (ProposalLine proposalLine : proposalLines) {
					pstmt.setString(1, proposalLine.getId());
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
		if (batchDelete(connection, deletionLines)) {
			return batchSave(connection, proposalLines);
		}
		return false;
	}

	@Override
	public ProposalLine deleteProposalLine(ProposalLine proposalLine) {
		Connection connection = null;
		if (proposalLine == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			pstmt = connection.prepareStatement(DELETE_PROPOSAL_LINE_QRY);
			pstmt.setString(1, proposalLine.getId());
			pstmt.setString(2, proposalLine.getProposal_id());
			int rowCount = pstmt.executeUpdate();
			LOGGER.debug("no of proposal lines deleted:" + rowCount);
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal lines:" + proposalLine.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposalLine;
	}

}
