package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalTaxesDAO;
import com.qount.invoice.model.ProposalTaxes;
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalTaxesDAOImpl implements ProposalTaxesDAO{
	
	private static Logger LOGGER = Logger.getLogger(ProposalTaxesDAOImpl.class);

	private ProposalTaxesDAOImpl() {
	}

	private static ProposalTaxesDAOImpl proposalTaxesDAOImpl = new ProposalTaxesDAOImpl();

	public static ProposalTaxesDAOImpl getProposalTaxesDAOImpl() {
		return proposalTaxesDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO proposal_taxes (`proposal_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
//	private final static String DELETE_QRY = "DELETE FROM `proposal_taxes` WHERE `tax_id`=?;";
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
					pstmt.setString(3, proposalTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalTaxes;
				} else {
					throw new WebApplicationException("unable to create proposal line taxes", 500);
				}
			}
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
	public ProposalTaxes batchDelete(Connection connection, List<ProposalTaxes> proposalTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProposalTaxes deleteProposalLine(ProposalTaxes proposalTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

}
