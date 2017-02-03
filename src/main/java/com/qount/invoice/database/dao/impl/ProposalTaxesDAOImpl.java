package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalTaxesDAO;
import com.qount.invoice.model.ProposalTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
public class ProposalTaxesDAOImpl implements ProposalTaxesDAO {

	private static Logger LOGGER = Logger.getLogger(ProposalTaxesDAOImpl.class);

	private ProposalTaxesDAOImpl() {
	}

	private static ProposalTaxesDAOImpl proposalTaxesDAOImpl = new ProposalTaxesDAOImpl();

	public static ProposalTaxesDAOImpl getProposalTaxesDAOImpl() {
		return proposalTaxesDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO proposal_taxes (`proposal_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
	private final static String DELETE_QRY = "DELETE FROM `proposal_taxes` WHERE `proposal_id`=?;";
	private final static String GET_QRY = "SELECT * FROM proposal_taxes WHERE `proposal_id` = ?;";

	@Override
	public List<ProposalTaxes> saveProposalTaxes(Connection connection, String proposalID,
			List<ProposalTaxes> proposalTaxes) {
		if (proposalTaxes.size() == 0) {
			return proposalTaxes;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<ProposalTaxes> proposalTaxesItr = proposalTaxes.iterator();
				while (proposalTaxesItr.hasNext()) {
					ProposalTaxes proposalTax = proposalTaxesItr.next();
					pstmt.setString(1, proposalID);
					pstmt.setString(2, proposalTax.getTax_id());
					pstmt.setDouble(3, proposalTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalTaxes;
				} else {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalTaxes;
	}

	@Override
	public List<ProposalTaxes> update(Connection connection, List<ProposalTaxes> proposalTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProposalTaxes> batchDeleteAndSave(Connection connection,String proposalId, List<ProposalTaxes> proposalTaxes) {
		if (proposalTaxes == null || StringUtils.isBlank(proposalId)) {
			return null;
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			int qryCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(qryCtr++, proposalId);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of taxes deleted:" + rowCount);
				if (rowCount > 0) {
					qryCtr = 1;
					pstmt2 = connection.prepareStatement(INSERT_QRY);
					Iterator<ProposalTaxes> proposalTaxesItr = proposalTaxes.iterator();
					while (proposalTaxesItr.hasNext()) {
						ProposalTaxes proposalTax = proposalTaxesItr.next();
						pstmt2.setString(1, proposalId);
						pstmt2.setString(2, proposalTax.getTax_id());
						pstmt2.setDouble(3, proposalTax.getTax_rate());
						pstmt2.addBatch();
					}
				}
				int[] rowCount2 = pstmt2.executeBatch();
				LOGGER.debug("no of taxes created:" + rowCount2);
				if (rowCount2 != null) {
					return proposalTaxes;
				} else {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error creating modules");
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeStatement(pstmt2);
		}
		return proposalTaxes;
	}

	@Override
	public ProposalTaxes deleteProposalTax(ProposalTaxes proposalTax) {
		Connection connection = null;
		if (proposalTax == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(1, proposalTax.getProposal_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
				LOGGER.debug("no of proposal tax deleted:" + rowCount);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error deleting proposal tax:" + proposalTax.getProposal_id() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting proposal:" + proposalTax.getProposal_id() + ",  ", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposalTax;
	}

	@Override
	public List<ProposalTaxes> getByProposalID(String proposalID) {
		List<ProposalTaxes> proposalTaxesList = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_QRY);
				pstmt.setString(1, proposalID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					ProposalTaxes proposalTax = new ProposalTaxes();
					proposalTax.setProposal_id(rset.getString("proposal_id"));
					proposalTax.setTax_id(rset.getString("tax_id"));
					proposalTax.setTax_rate(rset.getDouble("tax_rate"));
					proposalTaxesList.add(proposalTax);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching proposal taxes for proposal_id [ " + proposalID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return proposalTaxesList;
	}
}
