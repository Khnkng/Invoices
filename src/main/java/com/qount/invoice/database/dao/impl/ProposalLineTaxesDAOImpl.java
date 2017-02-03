package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalLineTaxesDAO;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalLineTaxesDAOImpl implements ProposalLineTaxesDAO {
	private static Logger LOGGER = Logger.getLogger(ProposalLineTaxesDAOImpl.class);

	private ProposalLineTaxesDAOImpl() {
	}

	private static ProposalLineTaxesDAOImpl proposalLineTaxesDAOImpl = new ProposalLineTaxesDAOImpl();

	public static ProposalLineTaxesDAOImpl getProposalLineTaxesDAOImpl() {
		return proposalLineTaxesDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO proposal_line_taxes (`proposal_line_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
	private final static String DELETE_QRY = "DELETE FROM proposal_line_taxes WHERE `proposal_line_id` = ?;";

	@Override
	public ProposalLineTaxes save(Connection connection, ProposalLineTaxes proposalLineTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProposalLineTaxes> batchSave(Connection connection,List<ProposalLineTaxes> proposalLinesTaxes) {
		if (proposalLinesTaxes.size() == 0) {
			return proposalLinesTaxes;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<ProposalLineTaxes> proposalLinesTaxesItr = proposalLinesTaxes.iterator();
				while (proposalLinesTaxesItr.hasNext()) {
					ProposalLineTaxes proposalLineTax = proposalLinesTaxesItr.next();
					pstmt.setString(1, proposalLineTax.getProposal_line_id());
					pstmt.setString(2, proposalLineTax.getTax_id());
					pstmt.setDouble(3, proposalLineTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalLinesTaxes;
				} else {
					throw new WebApplicationException("unable to inserting proposal line taxes", 500);
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal line taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLinesTaxes;
	}

	@Override
	public ProposalLineTaxes deleteProposalLine(ProposalLineTaxes proposalLineTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProposalLineTaxes> batchDeleteAndSave(Connection connection, String proposalId, String proposalLineId,
			List<ProposalLineTaxes> proposalLineTaxes) {
		if (proposalLineTaxes == null || StringUtils.isBlank(proposalId)) {
			return null;
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			int qryCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(qryCtr++, proposalLineId);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of proposal line taxes deleted:" + rowCount);
				if (rowCount > 0) {
					qryCtr = 1;
					pstmt2 = connection.prepareStatement(INSERT_QRY);
					Iterator<ProposalLineTaxes> proposalLineTaxesItr = proposalLineTaxes.iterator();
					while (proposalLineTaxesItr.hasNext()) {
						ProposalLineTaxes proposalLineTax = proposalLineTaxesItr.next();
						pstmt2.setString(1, proposalLineId);
						pstmt2.setString(2, proposalLineTax.getTax_id());
						pstmt2.setDouble(3, proposalLineTax.getTax_rate());
						pstmt2.addBatch();
					}
				}
				int[] rowCount2 = pstmt2.executeBatch();
				LOGGER.debug("no of proposal line taxes created:" + rowCount2);
				if (rowCount2 != null) {
					return proposalLineTaxes;
				} else {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal line taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error creating modules");
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeStatement(pstmt2);
		}
		return proposalLineTaxes;
	}
}
