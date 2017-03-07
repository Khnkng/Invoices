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
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.INSERT_QRY);
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
				pstmt.setDouble(27, proposal.getAmount_by_date());
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
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.UPDATE_QRY);
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
				pstmt.setDouble(26, proposal.getAmount_by_date());
				pstmt.setString(27, proposal.getId());
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
					if (proposalLineIndex != -1) {
						proposalLine = proposal.getProposalLines().get(proposalLineIndex);
						ProposalLineTaxes proposalLineTax = new ProposalLineTaxes();
						proposalLineTax.setProposal_line_id(rset.getString("plt_proposal_line_id"));
						proposalLineTax.setTax_id(rset.getString("plt_tax_id"));
						proposalLineTax.setTax_rate(rset.getDouble("plt_tax_rate"));
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
						proposalLineTax.setTax_rate(rset.getDouble("plt_tax_rate"));
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
	public List<Proposal> getProposalList(String user_id) {
		List<Proposal> proposals = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Proposal.GET_PROPOSAL_LIST_QRY);
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
