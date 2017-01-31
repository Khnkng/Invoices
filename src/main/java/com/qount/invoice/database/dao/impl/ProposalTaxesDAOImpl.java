package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

	@Override
	public List<ProposalTaxes> saveProposalTaxes(Connection connection, List<ProposalTaxes> proposalTaxes) {
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
					pstmt.setString(1, proposalTax.getProposal_id());
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
	public List<ProposalTaxes> batchDeleteAndSave(String proposalId, List<ProposalTaxes> proposalTaxes) {
		if (proposalTaxes == null || StringUtils.isBlank(proposalId)) {
			return null;
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			int qryCtr = 1;
			if (conn != null) {
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(DELETE_QRY);
				pstmt.setString(qryCtr++, proposalId);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of taxes deleted:" + rowCount);
				if (rowCount > 0) {
					qryCtr = 1;
					pstmt2 = conn.prepareStatement(INSERT_QRY);
					Iterator<ProposalTaxes> proposalTaxesItr = proposalTaxes.iterator();
					while (proposalTaxesItr.hasNext()) {
						ProposalTaxes proposalTax = proposalTaxesItr.next();
						pstmt.setString(1, proposalTax.getProposal_id());
						pstmt.setString(2, proposalTax.getTax_id());
						pstmt.setDouble(3, proposalTax.getTax_rate());
						pstmt.addBatch();
					}
				}
				int[] rowCount2 = pstmt2.executeBatch();
				LOGGER.debug("no of taxes created:" + rowCount2);
				if (rowCount2 != null) {
					conn.commit();
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
			DatabaseUtilities.closeConnection(conn);
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
}