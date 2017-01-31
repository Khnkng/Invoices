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
import com.qount.invoice.model.ProposalLineTaxes;
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
	private final static String GET_QRY = "SELECT p.`id`,p.`user_id`,p.`company_id`,p.`company_name`,p.`amount`,p.`currency`,p.`description`,p.`objectives`,p.`last_updated_by`,p.`last_updated_at`,p.`first_name`,p.`last_name`,p.`state`,p.`proposal_date`,p.`acceptance_date`,p.`acceptance_final_date`,p.`notes`,p.`item_id`,p.`item_name`,p.`coa_id`,p.`coa_name`,p.`discount`,p.`deposit_amount`,p.`processing_fees`,p.`remainder_json`,p.`remainder_mail_json`, pl.`id` AS `plid`,pl.`proposal_id`,pl.`description` `pl_description`,pl.`objectives` `pl_objectives`,pl.`amount` `pl_amount`,pl.`currency` `pl_currency`,pl.`last_updated_by` `pl_last_updated_by`,pl.`last_updated_at` `pl_last_updated_at`,pl.`quantity` `pl_quantity`,pl.`price` `pl_price`,pl.`notes` `pl_notes`, plt.`proposal_line_id` `plt_proposal_line_id`,plt.`tax_id` `plt_tax_id`,plt.`tax_rate` `plt_tax_rate` FROM `proposal` p LEFT JOIN `proposal_lines` pl ON p.id=pl.proposal_id LEFT JOIN `proposal_line_taxes` plt ON pl.id =plt.proposal_line_id WHERE p.id = ?;";
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
				pstmt = connection.prepareStatement(GET_QRY);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setId(rset.getString("plid"));
					int proposalLineIndex = proposal.getProposalLines().indexOf(proposalLine);
					if (proposalLineIndex != -1) {
						proposalLine = proposal.getProposalLines().get(proposalLineIndex);
						ProposalLineTaxes proposalLineTax = new ProposalLineTaxes();
						proposalLineTax.setProposal_line_id(rset.getString("plt_proposal_line_id"));
						proposalLineTax.setTax_id(rset.getString("plt_tax_id"));
						proposalLineTax.setTax_rate(rset.getString("plt_tax_rate"));
						proposalLine.getProposalLineTaxes().add(proposalLineTax);
					} else if (proposalLineIndex == -1) {
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
						ProposalLineTaxes proposalLineTax = new ProposalLineTaxes();
						proposalLineTax.setProposal_line_id(rset.getString("plt_proposal_line_id"));
						proposalLineTax.setTax_id(rset.getString("plt_tax_id"));
						proposalLineTax.setTax_rate(rset.getString("plt_tax_rate"));
						List<ProposalLineTaxes> proposalLineTaxes = new ArrayList<ProposalLineTaxes>();
						proposalLineTaxes.add(proposalLineTax);
						proposalLine.setProposalLineTaxes(proposalLineTaxes);
						proposal.getProposalLines().add(proposalLine);
						if (StringUtils.isBlank(proposal.getId())) {
							proposal.setId(rset.getString("id"));
							proposal.setUser_id(rset.getString("user_id"));
							proposal.setCompany_id(rset.getString("company_id"));
							proposal.setCompany_name(rset.getString("company_name"));
							proposal.setAmount(rset.getDouble("amount"));
							proposal.setCurrency(rset.getString("currency"));
							proposal.setDescription(rset.getString("description"));
							proposal.setObjectives(rset.getString("objectives"));
							proposal.setLast_updated_by(rset.getString("last_updated_by"));
							proposal.setLast_updated_at(rset.getString("last_updated_at"));
							proposal.setFirst_name(rset.getString("first_name"));
							proposal.setLast_name(rset.getString("last_name"));
							proposal.setState(rset.getString("state"));
							proposal.setProposal_date(rset.getString("proposal_date"));
							proposal.setAcceptance_date(rset.getString("acceptance_date"));
							proposal.setNotes(rset.getString("notes"));
							proposal.setItem_id(rset.getString("item_id"));
							proposal.setItem_name(rset.getString("item_name"));
							proposal.setCoa_id(rset.getString("coa_id"));
							proposal.setCoa_name(rset.getString("coa_name"));
							proposal.setDiscount(rset.getDouble("discount"));
							proposal.setDeposit_amount(rset.getDouble("deposit_amount"));
							proposal.setProcessing_fees(rset.getDouble("processing_fees"));
							proposal.setRemainder_json(rset.getString("remainder_json"));
							proposal.setRemainder_mail_json(rset.getString("remainder_mail_json"));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal for id [ " + proposalID + " ]", e);
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
