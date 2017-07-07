package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalDAO;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
public class ProposalDAOImpl implements ProposalDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalDAOImpl() {
	}

	private static ProposalDAOImpl proposalDAOImpl = new ProposalDAOImpl();

	public static ProposalDAOImpl getProposalDAOImpl() {
		return proposalDAOImpl;
	}

	@Override
	public Proposal save(Connection connection, Proposal proposal) {
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.INSERT_QRY);
				pstmt.setString(ctr++, proposal.getId());
				pstmt.setString(ctr++, proposal.getUser_id());
				pstmt.setString(ctr++, proposal.getCompany_id());
				pstmt.setDouble(ctr++, proposal.getAmount());
				pstmt.setString(ctr++, proposal.getCurrency());
				pstmt.setString(ctr++, proposal.getDescription());
				pstmt.setString(ctr++, proposal.getObjectives());
				pstmt.setString(ctr++, proposal.getLast_updated_by());
				pstmt.setString(ctr++, proposal.getLast_updated_at());
				pstmt.setString(ctr++, proposal.getState());
				pstmt.setString(ctr++, proposal.getProposal_date());
				pstmt.setString(ctr++, proposal.getAcceptance_date());
				pstmt.setString(ctr++, proposal.getAcceptance_final_date());
				pstmt.setString(ctr++, proposal.getNotes());
				pstmt.setDouble(ctr++, proposal.getDiscount());
				pstmt.setDouble(ctr++, proposal.getDeposit_amount());
				pstmt.setDouble(ctr++, proposal.getProcessing_fees());
				pstmt.setString(ctr++, proposal.getRemainder_json());
				pstmt.setString(ctr++, proposal.getRemainder_mail_json());
				pstmt.setDouble(ctr++, proposal.getAmount_by_date());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposal;
	}

	@Override
	public Proposal updateProposal(Connection connection, Proposal proposal) {
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.UPDATE_QRY);
				pstmt.setString(ctr++, proposal.getUser_id());
				pstmt.setString(ctr++, proposal.getCompany_id());
				pstmt.setDouble(ctr++, proposal.getAmount());
				pstmt.setString(ctr++, proposal.getCurrency());
				pstmt.setString(ctr++, proposal.getDescription());
				pstmt.setString(ctr++, proposal.getObjectives());
				pstmt.setString(ctr++, proposal.getLast_updated_by());
				pstmt.setString(ctr++, proposal.getLast_updated_at());
				pstmt.setString(ctr++, proposal.getState());
				pstmt.setString(ctr++, proposal.getProposal_date());
				pstmt.setString(ctr++, proposal.getAcceptance_date());
				pstmt.setString(ctr++, proposal.getAcceptance_final_date());
				pstmt.setString(ctr++, proposal.getNotes());
				pstmt.setDouble(ctr++, proposal.getDiscount());
				pstmt.setDouble(ctr++, proposal.getDeposit_amount());
				pstmt.setDouble(ctr++, proposal.getProcessing_fees());
				pstmt.setString(ctr++, proposal.getRemainder_json());
				pstmt.setString(ctr++, proposal.getRemainder_mail_json());
				pstmt.setDouble(ctr++, proposal.getAmount_by_date());
				pstmt.setString(ctr++, proposal.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposal;
	}

	@Override
	public Proposal get(String proposalID) {
		Proposal proposal = new Proposal();
		List<ProposalLine> proposalLines = new ArrayList<ProposalLine>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				proposal.setProposalLines(proposalLines);
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.GET_QRY);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setId(rset.getString("plid"));
					int proposalLineIndex = proposal.getProposalLines().indexOf(proposalLine);
					if (proposalLineIndex == -1) {
						proposalLine.setProposal_id(rset.getString("proposal_id"));
						proposalLine.setDescription(rset.getString("pl_description"));
						proposalLine.setObjectives(rset.getString("pl_objectives"));
						proposalLine.setAmount(rset.getDouble("pl_amount"));
						proposalLine.setCurrency(rset.getString("pl_currency"));
						proposalLine.setLast_updated_at(rset.getString("pl_last_updated_at"));
						proposalLine.setLast_updated_by(rset.getString("pl_last_updated_by"));
						proposalLine.setQuantity(rset.getLong("pl_quantity"));
						proposalLine.setPrice(rset.getDouble("pl_price"));
						proposalLine.setNotes(rset.getString("pl_notes"));
						proposal.getProposalLines().add(proposalLine);
						if (StringUtils.isBlank(proposal.getId())) {
							proposal.setId(rset.getString("id"));
							proposal.setUser_id(rset.getString("user_id"));
							proposal.setCompany_id(rset.getString("company_id"));
							proposal.setAmount(rset.getDouble("amount"));
							proposal.setCurrency(rset.getString("currency"));
							proposal.setDescription(rset.getString("description"));
							proposal.setObjectives(rset.getString("objectives"));
							proposal.setLast_updated_by(rset.getString("last_updated_by"));
							proposal.setLast_updated_at(rset.getString("last_updated_at"));
							proposal.setState(rset.getString("state"));
							proposal.setProposal_date(rset.getString("proposal_date"));
							proposal.setAcceptance_date(rset.getString("acceptance_date"));
							proposal.setNotes(rset.getString("notes"));
							proposal.setDiscount(rset.getDouble("discount"));
							proposal.setDeposit_amount(rset.getDouble("deposit_amount"));
							proposal.setProcessing_fees(rset.getDouble("processing_fees"));
							proposal.setRemainder_json(rset.getString("remainder_json"));
							proposal.setRemainder_mail_json(rset.getString("remainder_mail_json"));
							proposal.setAmount_by_date(rset.getDouble("amount_by_date"));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal for id [ " + proposalID + " ]", e);
			return null;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		if (StringUtils.isBlank(proposal.getId())) {
			return null;
		}
		return proposal;
	}

	@Override
	public List<Proposal> getProposalList(String user_id,String comapnyId) {
		List<Proposal> proposals = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.GET_PROPOSAL_LIST_QRY);
				pstmt.setString(1, user_id);
				pstmt.setString(2, comapnyId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Proposal proposal = new Proposal();
					proposal.setId(rset.getString("id"));
					proposal.setUser_id(rset.getString("user_id"));
					proposal.setCompany_id(rset.getString("company_id"));
					proposal.setAmount(rset.getDouble("amount"));
					proposal.setCurrency(rset.getString("currency"));
					proposal.setDescription(rset.getString("description"));
					proposal.setObjectives(rset.getString("objectives"));
					proposal.setLast_updated_at(rset.getString("last_updated_at"));
					proposal.setLast_updated_by(rset.getString("last_updated_by"));
					proposal.setState(rset.getString("state"));
					proposal.setProposal_date(rset.getString("proposal_date"));
					proposal.setAcceptance_date(rset.getString("acceptance_date"));
					proposal.setAcceptance_final_date(rset.getString("acceptance_final_date"));
					proposal.setNotes(rset.getString("notes"));
					proposal.setDiscount(rset.getDouble("discount"));
					proposal.setDeposit_amount(rset.getDouble("deposit_amount"));
					proposal.setProcessing_fees(rset.getDouble("processing_fees"));
					proposal.setRemainder_json(rset.getString("remainder_json"));
					proposal.setRemainder_mail_json(rset.getString("remainder_mail_json"));
					proposal.setAmount_by_date(rset.getDouble("amount_by_date"));
					proposals.add(proposal);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposals for user_id [ " + user_id + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposals;
	}

	@Override
	public Proposal delete(Proposal proposal) {
		Connection connection = null;
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.DELETE_QRY);
				pstmt.setString(1, proposal.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
				LOGGER.debug("no of proposal deleted:" + rowCount);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record deleted:" + proposal.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal:" + proposal.getId() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposal;
	}
}
