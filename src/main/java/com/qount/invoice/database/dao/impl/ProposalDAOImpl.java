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
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalDAOImpl implements ProposalDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalDAOImpl() {
	}

	private static ProposalDAOImpl proposalDAOImpl = new ProposalDAOImpl();

	public static ProposalDAOImpl getProposalDAOImpl() {
		return proposalDAOImpl;
	}

	@Override
	public boolean save(Connection connection, Proposal proposal) {
		boolean result = false;
		if (proposal == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO `proposal` (`companyID`, `proposalID`, `customer_name`, `total_amount`, `currency`, `bank_account`, `credit_card`, `userID`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, proposal.getCompanyID());
				pstmt.setString(2, proposal.getProposalID());
				pstmt.setString(3, proposal.getCustomer_name());
				pstmt.setFloat(4, proposal.getTotal_amount());
				pstmt.setInt(5, proposal.getCurrency());
				pstmt.setBoolean(6, proposal.isBank_account());
				pstmt.setBoolean(7, proposal.isCredit_card());
				pstmt.setString(8, proposal.getUserID());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug(
						"proposal [" + proposal.getCompanyID() + " : " + proposal.getProposalID() + "]" + " created");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean update(Connection connection, Proposal proposal) {
		boolean result = false;
		if (proposal == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		String sql = "UPDATE proposal SET `customer_name` = ?, `total_amount` = ?, `currency` = ?, bank_account = ?, credit_card = ?, userID = ? WHERE `companyID` = ? AND `proposalID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, proposal.getCustomer_name());
				pstmt.setFloat(2, proposal.getTotal_amount());
				pstmt.setInt(3, proposal.getCurrency());
				pstmt.setBoolean(4, proposal.isBank_account());
				pstmt.setBoolean(5, proposal.isCredit_card());
				pstmt.setString(6, proposal.getUserID());
				pstmt.setString(7, proposal.getCompanyID());
				pstmt.setString(8, proposal.getProposalID());
				int rowCount = pstmt.executeUpdate();
				result = rowCount != 0;
				LOGGER.debug(
						"proposal [" + proposal.getCompanyID() + " : " + proposal.getProposalID() + "]" + " updated");
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
	public Proposal get(Connection connection, String companyID, String proposalID,String userID) {
		Proposal proposal = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT proposal.*, proposal_lines.lineID, proposal_lines.line_number, proposal_lines.description, proposal_lines.quantity, proposal_lines.unit_cost, proposal_lines.total_amount FROM proposal INNER JOIN proposal_lines ON proposal.proposalID=proposal_lines.proposalID  WHERE proposal.`companyID` = ? AND proposal.`proposalID` = ? AND proposal.`userID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, companyID);
				pstmt.setString(2, proposalID);
				pstmt.setString(3, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					if (proposal == null) {
						proposal = new Proposal();
						proposal.setProposalID(proposalID);
						proposal.setCompanyID(companyID);
						proposal.setCustomer_name(rset.getString("customer_name"));
						proposal.setTotal_amount(rset.getInt("total_amount"));
						proposal.setCurrency(rset.getInt("currency"));
						proposal.setBank_account(rset.getBoolean("bank_account"));
						proposal.setCredit_card(rset.getBoolean("credit_card"));
					}
					ProposalLine proposalLine = new ProposalLine();
					proposalLine.setProposalID(rset.getString("proposalID"));
					proposalLine.setLineID(rset.getString("lineID"));
					proposalLine.setLine_number(rset.getInt("line_number"));
					proposalLine.setDescription(rset.getString("description"));
					proposalLine.setQuantity(rset.getInt("quantity"));
					proposalLine.setUnit_cost(rset.getFloat("unit_cost"));
					proposalLine.setTotal_amount(rset.getFloat("total_amount"));
					proposal.getProposalLines().add(proposalLine);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal for CompanyID [ " + companyID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposal;

	}

	@Override
	public List<Proposal> getList(Connection connection, String companyID) {
		List<Proposal> proposals = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "SELECT `companyID`,`proposalID`,`customer_name`,`total_amount`,`currency`,`bank_account`,`credit_card`, `userID` FROM proposal WHERE `companyID` = ?;";
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(sql);
				pstmt.setString(1, companyID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Proposal proposal = new Proposal();
					proposal.setProposalID(rset.getString("proposalID"));
					proposal.setCompanyID(companyID);
					proposal.setCustomer_name(rset.getString("customer_name"));
					proposal.setTotal_amount(rset.getInt("total_amount"));
					proposal.setCurrency(rset.getInt("currency"));
					proposal.setBank_account(rset.getBoolean("bank_account"));
					proposal.setCredit_card(rset.getBoolean("credit_card"));
					proposal.setUserID(rset.getString("userID"));
					proposals.add(proposal);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposals for CompanyID [ " + companyID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}

		return proposals;

	}
}
