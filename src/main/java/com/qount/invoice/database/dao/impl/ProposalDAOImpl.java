package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalDAO;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
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

	private final static String INSERT_QRY = "INSERT INTO `proposal` (`id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private final static String UPDATE_QRY = "UPDATE `proposal` SET `user_id` = ?,`company_id` = ?,`company_name` = ?,`amount` = ?,`currency` = ?,`description` = ?,`objectives` = ?,`last_updated_by` = ?,`last_updated_at` = ?,`first_name` = ?,`last_name` = ?,`state` = ?,`proposal_date` = ?,`acceptance_date` = ?,`acceptance_final_date` = ?,`notes` = ?,`item_id` = ?,`item_name` = ?,`coa_id` = ?,`coa_name` = ?,`discount` = ?,`deposit_amount` = ?,`processing_fees` = ?,`remainder_json` = ?,`remainder_mail_json`= ? WHERE `id` = ?";
	private final static String DELETE_QRY = "DELETE FROM proposal WHERE `id`=?;";
	private final static String GET_QRY = "SELECT proposal.*, proposal_lines.id proposal_line_id, proposal_lines.proposal_id, proposal_lines.description, proposal_lines.objectives, proposal_lines.amount, proposal_lines.currency,proposal_lines.last_updated_by,proposal_lines.last_updated_at FROM proposal INNER JOIN proposal_lines ON proposal.id=proposal_lines.proposal_id WHERE proposal.`id` = ? AND proposal.`user_id` = ?;";
	private final static String GET_PROPOSAL_LIST_QRY = "SELECT `id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`, `objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json` FROM proposal WHERE `user_id` = ?;";

	@Override
	public Proposal save(Connection connection, Proposal proposal) {
		if (proposal == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				pstmt.setString(1, proposal.getId());
				pstmt.setString(2, proposal.getUser_id());
				pstmt.setString(3, proposal.getCompany_id());
				pstmt.setString(4, proposal.getCompany_name());
				pstmt.setDouble(5, proposal.getAmount());
				pstmt.setString(6, proposal.getCurrency());
				pstmt.setString(7, proposal.getDescription());
				pstmt.setString(8, proposal.getObjectives());
				pstmt.setString(9, proposal.getLast_updated_by());
				pstmt.setString(10, proposal.getLast_updated_at());
				pstmt.setString(11, proposal.getFirst_name());
				pstmt.setString(12, proposal.getLast_name());
				pstmt.setString(13, proposal.getState());
				pstmt.setString(14, proposal.getProposal_date());
				pstmt.setString(15, proposal.getAcceptance_date());
				pstmt.setString(16, proposal.getAcceptance_final_date());
				pstmt.setString(17, proposal.getNotes());
				pstmt.setString(18, proposal.getItem_id());
				pstmt.setString(19, proposal.getItem_name());
				pstmt.setString(20, proposal.getCoa_id());
				pstmt.setString(21, proposal.getCoa_name());
				pstmt.setDouble(22, proposal.getDiscount());
				pstmt.setDouble(23, proposal.getDeposit_amount());
				pstmt.setDouble(24, proposal.getProcessing_fees());
				pstmt.setString(25, proposal.getRemainder_json());
				pstmt.setString(26, proposal.getRemainder_mail_json());
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
	public Proposal updateProposal(Proposal proposal) {
		if (proposal == null) {
			return null;
		}
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(UPDATE_QRY);
				pstmt.setString(1, proposal.getUser_id());
				pstmt.setString(2, proposal.getCompany_id());
				pstmt.setString(3, proposal.getCompany_name());
				pstmt.setDouble(4, proposal.getAmount());
				pstmt.setString(5, proposal.getCurrency());
				pstmt.setString(6, proposal.getDescription());
				pstmt.setString(7, proposal.getObjectives());
				pstmt.setString(8, proposal.getLast_updated_by());
				pstmt.setString(9, proposal.getLast_updated_at());
				pstmt.setString(10, proposal.getFirst_name());
				pstmt.setString(11, proposal.getLast_name());
				pstmt.setString(12, proposal.getState());
				pstmt.setString(13, proposal.getProposal_date());
				pstmt.setString(14, proposal.getAcceptance_date());
				pstmt.setString(15, proposal.getAcceptance_final_date());
				pstmt.setString(16, proposal.getNotes());
				pstmt.setString(17, proposal.getItem_id());
				pstmt.setString(18, proposal.getItem_name());
				pstmt.setString(19, proposal.getCoa_id());
				pstmt.setString(20, proposal.getCoa_name());
				pstmt.setDouble(21, proposal.getDiscount());
				pstmt.setDouble(22, proposal.getDeposit_amount());
				pstmt.setDouble(23, proposal.getProcessing_fees());
				pstmt.setString(24, proposal.getRemainder_json());
				pstmt.setString(25, proposal.getRemainder_mail_json());
				pstmt.setString(26, proposal.getId());
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
			DatabaseUtilities.closeConnection(connection);
		}
		return proposal;
	}

	@Override
	public Proposal get(String proposalID, String userID) {
		Proposal proposal = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_QRY);
				pstmt.setString(1, proposalID);
				pstmt.setString(2, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					if (proposal == null) {
						proposal = new Proposal();
						proposal.setId(proposalID);
						proposal.setUser_id(rset.getString("user_id"));
						proposal.setCompany_id(rset.getString("company_id"));
						proposal.setCompany_name("company_name");
						proposal.setAmount(rset.getDouble("amount"));
						proposal.setCurrency(rset.getString("currency"));
						proposal.setDescription(rset.getString("description"));
						proposal.setObjectives(rset.getString("objectives"));
						proposal.setLast_updated_at(rset.getString("last_updated_at"));
						proposal.setLast_updated_by(rset.getString("last_updated_by"));
					}
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setId(rset.getString("proposal_line_id"));
					proposalLine.setProposal_id(rset.getString("proposal_id"));
					proposalLine.setDescription(rset.getString("description"));
					proposalLine.setObjectives(rset.getString("objectives"));
					proposalLine.setAmount(rset.getDouble("amount"));
					proposalLine.setCurrency(rset.getString("currency"));
					proposalLine.setLast_updated_at(rset.getString("last_updated_at"));
					proposalLine.setLast_updated_by(rset.getString("last_updated_by"));
					proposal.getProposalLines().add(proposalLine);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal for user [ " + userID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposal;
	}

	@Override
	public List<Proposal> getProposalList(String user_id) {
		List<Proposal> proposals = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_PROPOSAL_LIST_QRY);
				pstmt.setString(1, user_id);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Proposal proposal = new Proposal();
					proposal.setId(rset.getString("id"));
					proposal.setUser_id(rset.getString("user_id"));
					proposal.setCompany_id(rset.getString("company_id"));
					proposal.setCompany_name(rset.getString("company_name"));
					proposal.setAmount(rset.getDouble("amount"));
					proposal.setCurrency(rset.getString("currency"));
					proposal.setDescription(rset.getString("description"));
					proposal.setObjectives(rset.getString("objectives"));
					proposal.setLast_updated_at(rset.getString("last_updated_at"));
					proposal.setLast_updated_by(rset.getString("last_updated_by"));
					proposal.setFirst_name(rset.getString("first_name"));
					proposal.setLast_name(rset.getString("last_name"));
					proposal.setState(rset.getString("state"));	
					proposal.setProposal_date(rset.getString("proposal_date"));
					proposal.setAcceptance_date(rset.getString("acceptance_date"));
					proposal.setAcceptance_final_date(rset.getString("acceptance_final_date"));
					proposal.setNotes(rset.getString("notes"));
					proposal.setItem_id(rset.getString("item_id"));
					proposal.setItem_name(rset.getString("item_name"));
					proposal.setCoa_id(rset.getString("coa_id"));
					proposal.setCoa_name(rset.getString("coa_name"));
					proposal.setDiscount(rset.getDouble("discount"));
					proposal.setDeposit_amount(rset.getDouble("deposite_amount"));
					proposal.setProcessing_fees(rset.getDouble("processing_fees"));
					proposal.setRemainder_json(rset.getString("remainder_json"));
					proposal.setRemainder_mail_json(rset.getString("remainder_mail_json"));
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
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(1, proposal.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
				LOGGER.debug("no of proposal deleted:" + rowCount);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error deleting proposal:" + proposal.getId() + ",  ", e);
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
