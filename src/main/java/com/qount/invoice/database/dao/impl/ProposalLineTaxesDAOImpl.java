package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.ProposalLineTaxesDAO;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.utils.DatabaseUtilities;

public class ProposalLineTaxesDAOImpl implements ProposalLineTaxesDAO {
	private static Logger LOGGER = Logger.getLogger(ProposalDAOImpl.class);

	private ProposalLineTaxesDAOImpl() {
	}

	private static ProposalLineTaxesDAOImpl proposalLineTaxesDAOImpl = new ProposalLineTaxesDAOImpl();

	public static ProposalLineTaxesDAOImpl getProposalLineTaxesDAOImpl() {
		return proposalLineTaxesDAOImpl;
	}
	
	private final static String INSERT_QRY = "INSERT INTO proposal_line_taxes (`proposal_line_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";

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
					pstmt.setString(3, proposalLineTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return proposalLinesTaxes;
				} else {
					throw new WebApplicationException("unable to create proposal line taxes", 500);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return proposalLinesTaxes;
	}

	@Override
	public ProposalLineTaxes batchDelete(Connection connection, List<ProposalLineTaxes> proposalLinesTaxes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProposalLineTaxes deleteProposalLine(ProposalLineTaxes proposalLineTaxes) {
		// TODO Auto-generated method stub
		return null;
	}
}
