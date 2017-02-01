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


	public List<ProposalLine> getLines(Connection connection, String proposalID);

	public List<ProposalLine> batchSave(Connection connection, List<ProposalLine> proposalLines);
	
	public ProposalLine update(Connection connection, ProposalLine proposalLine);

	public List<ProposalLine> batchDelete(Connection connection, List<ProposalLine> proposalLines);

	public ProposalLine deleteProposalLine(ProposalLine proposalLine);
}
