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
import com.qount.invoice.utils.SqlQuerys;



public class ProposalLineDAOImpl implements ProposalLineDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalLineDAOImpl() {
	}

	private static ProposalLineDAOImpl proposalLineDAOImpl = new ProposalLineDAOImpl();

	public static ProposalLineDAOImpl getProposalLineDAOImpl() {
		return proposalLineDAOImpl;
	}
	
	@Override
	public List<ProposalLine> getByProposalId(Connection connection, ProposalLine proposalLine) {
		List<ProposalLine> proposalLines = new ArrayList<>();
		if (proposalLine == null || StringUtils.isBlank(proposalLine.getProposal_id())) {
			return proposalLines;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.ProposalLine.GET_LINES_QRY);
				pstmt.setString(1, proposalLine.getProposal_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					proposalLine.setId(rset.getString("id"));
					proposalLine.setProposal_id(rset.getString("proposal_id"));
					proposalLine.setDescription(rset.getString("description"));
					proposalLine.setObjectives(rset.getString("objectives"));
					proposalLine.setAmount(rset.getDouble("amount"));
					proposalLine.setLast_updated_at(rset.getString("last_updated_at"));
					proposalLine.setLast_updated_by(rset.getString("last_updated_by"));
					proposalLine.setQuantity(rset.getDouble("quantity"));
					proposalLine.setPrice(rset.getDouble("price"));
					proposalLine.setNotes(rset.getString("notes"));
					proposalLine.setItem_id(rset.getString("item_id"));
					proposalLine.setTax_id(rset.getString("tax_id"));
					proposalLines.add(proposalLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retreving proposal lines with ID = " + proposalLine.getProposal_id(), e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLines;
	}

	@Override
	public List<ProposalLine> save(Connection connection, List<ProposalLine> proposalLines) {
		LOGGER.debug("entered proposalLine save:" + proposalLines);
		if (proposalLines == null || proposalLines.size() == 0) {
			return proposalLines;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.ProposalLine.INSERT_QRY);
				Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
				int ctr = 1;
				while (proposalLineItr.hasNext()) {
					ProposalLine proposalLine = proposalLineItr.next();
					pstmt.setString(ctr++, proposalLine.getId());
					pstmt.setString(ctr++, proposalLine.getProposal_id());
					pstmt.setString(ctr++, proposalLine.getDescription());
					pstmt.setString(ctr++, proposalLine.getObjectives());
					pstmt.setDouble(ctr++, proposalLine.getAmount());
					pstmt.setString(ctr++, proposalLine.getLast_updated_by());
					pstmt.setString(ctr++, proposalLine.getLast_updated_at());
					pstmt.setDouble(ctr++, proposalLine.getQuantity());
					pstmt.setDouble(ctr++, proposalLine.getPrice());
					pstmt.setString(ctr++, proposalLine.getNotes());
					pstmt.setString(ctr++, proposalLine.getItem_id());
					pstmt.setString(ctr++, proposalLine.getType());
					pstmt.setString(ctr++, StringUtils.isBlank(proposalLine.getTax_id())?null:proposalLine.getTax_id());
					ctr = 1;
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalLines;
				} else {
					throw new WebApplicationException("unable to create proposal lines", 500);
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			LOGGER.debug("exited proposalLine save:" + proposalLines);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLines;
	}

	@Override
	public ProposalLine update(Connection connection, ProposalLine proposalLine) {
		if (proposalLine == null) {
			return proposalLine;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.ProposalLine.UPDATE_QRY);
				pstmt.setString(ctr++, proposalLine.getProposal_id());
				pstmt.setString(ctr++, proposalLine.getDescription());
				pstmt.setString(ctr++, proposalLine.getObjectives());
				pstmt.setDouble(ctr++, proposalLine.getAmount());
				pstmt.setString(ctr++, proposalLine.getLast_updated_by());
				pstmt.setString(ctr++, proposalLine.getLast_updated_at());
				pstmt.setDouble(ctr++, proposalLine.getQuantity());
				pstmt.setDouble(ctr++, proposalLine.getPrice());
				pstmt.setString(ctr++, proposalLine.getNotes());
				pstmt.setString(ctr++, proposalLine.getItem_id());
				pstmt.setString(ctr++, proposalLine.getTax_id());
				pstmt.setString(ctr++, proposalLine.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return proposalLine;
				} else {
					throw new WebApplicationException("unable to update proposal lines", 500);
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLine;
	}

	@Override
	public ProposalLine deleteByProposalId(Connection connection,ProposalLine proposalLine) {
		LOGGER.debug("entered delete Proposal line By Proposal Id:"+proposalLine);
		if (proposalLine == null || StringUtils.isBlank(proposalLine.getProposal_id())) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.ProposalLine.DELETE_PROPOSAL_BY_ID_QRY);
				pstmt.setString(1, proposalLine.getProposal_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return proposalLine;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited delete Proposal line By Proposal Id:"+proposalLine);
		}
		return proposalLine;
	}

	@Override
	public ProposalLine deleteProposalLine(ProposalLine proposalLines) {
		Connection connection = null;
		if (proposalLines == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.ProposalLine.DELETE_PROPOSAL_LINE_QRY);
				pstmt.setString(1, proposalLines.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal lines deleted:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal lines:" + proposalLines.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposalLines;
	}

}
