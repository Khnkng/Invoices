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

import com.qount.invoice.database.dao.ProposalLineDAO;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalLineDAOImpl implements ProposalLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalLineDAOImpl() {
	}

	private static ProposalLineDAOImpl proposalLineDAOImpl = new ProposalLineDAOImpl();

	public static ProposalLineDAOImpl getProposalLineDAOImpl() {
		return proposalLineDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO `proposal_lines` (`id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
	private final static String UPADTE_QRY = "UPDATE `proposal_lines` SET `description` = ?,`objectives` = ?,`amount`= ?,`currency` = ?,`last_updated_by`= ?,`last_updated_at` = ?,`quantity` = ?,`price` = ?,`notes` = ? WHERE `id` = ? AND `proposal_id` = ?;";
	private final static String GET_LINES_QRY = "SELECT `id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes` FROM proposal_lines WHERE `id` = ?;";
	private final static String DELETE_PROPOSAL_LINE_QRY = "DELETE FROM `proposal_lines` WHERE `id` = ?";

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
					proposalLine.setLast_updated_at(rset.getString("last_updated_at"));
					proposalLine.setLast_updated_by(rset.getString("last_updated_by"));
					proposalLine.setQuantity(rset.getDouble("quantity"));
					proposalLine.setPrice(rset.getDouble("price"));
					proposalLine.setNotes(rset.getString("notes"));
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
	public List<ProposalLine> batchSave(Connection connection, List<ProposalLine> proposalLines) {
		if (proposalLines.size() == 0) {
			return proposalLines;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<ProposalLine> ProposalLineItr = proposalLines.iterator();
				while (ProposalLineItr.hasNext()) {
					ProposalLine proposalLine = ProposalLineItr.next();
					pstmt.setString(1, proposalLine.getId());
					pstmt.setString(2, proposalLine.getProposal_id());
					pstmt.setString(3, proposalLine.getDescription());
					pstmt.setString(4, proposalLine.getObjectives());
					pstmt.setDouble(5, proposalLine.getAmount());
					pstmt.setString(6, proposalLine.getCurrency());
					pstmt.setString(7, proposalLine.getLast_updated_by());
					pstmt.setString(8, proposalLine.getLast_updated_at());
					pstmt.setDouble(9, proposalLine.getQuantity());
					pstmt.setDouble(10, proposalLine.getPrice());
					pstmt.setString(11, proposalLine.getNotes());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount == null) {
					throw new WebApplicationException("unable to create proposal lines", 500);
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error creating proposal lines:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLines;
	}

	@Override
	public ProposalLine update(Connection connection, ProposalLine proposalLine) {
		if (proposalLine == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(UPADTE_QRY);
				pstmt.setString(1, proposalLine.getDescription());
				pstmt.setString(2, proposalLine.getObjectives());
				pstmt.setDouble(3, proposalLine.getAmount());
				pstmt.setString(4, proposalLine.getCurrency());
				pstmt.setString(5, proposalLine.getLast_updated_by());
				pstmt.setString(6, proposalLine.getLast_updated_at());
				pstmt.setDouble(7, proposalLine.getQuantity());
				pstmt.setDouble(8, proposalLine.getPrice());
				pstmt.setString(9, proposalLine.getNotes());
				pstmt.setString(10, proposalLine.getId());
				pstmt.setString(11, proposalLine.getProposal_id());
			}
			int rowCount = pstmt.executeUpdate();
			if (rowCount == 0) {
				throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" + proposalLine.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLine;
	}

	@Override
	public ProposalLine delete(ProposalLine proposalLine) {
		Connection connection = null;
		if (proposalLine == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_PROPOSAL_LINE_QRY);
				pstmt.setString(1, proposalLine.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal lines deleted:" + rowCount);
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" + proposalLine.getId() + ",  ", e);
			throw e;
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
