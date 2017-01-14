package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.ProposalLine;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 22 Nov 2016
 *
 */
public interface ProposalLineDAO {

	public boolean save(Connection connection, ProposalLine proposalLine);

	public List<ProposalLine> getLines(Connection connection, String proposalID);

	public boolean batchSave(Connection connection, List<ProposalLine> proposalLines);

	public boolean batchDelete(Connection connection, List<ProposalLine> proposalLines);

	boolean batchSaveAndDelete(Connection connection, List<ProposalLine> proposalLines,
			List<ProposalLine> deletionLines);
	
	public ProposalLine deleteProposalLine(ProposalLine proposalLine);
}
