package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Proposal;

/**
 * DAO interface for proposalDAOImpl
 * 
 * @author Apurva, Qount.
 * @version 1.0, 22 Nov 2016
 *
 */
public interface ProposalDAO {

	public Proposal save(Connection connection, Proposal proposal);

	public Proposal updateProposal(Proposal proposal);

	public Proposal get(String proposalID);

	public List<Proposal> getProposalList(String companyID);

	public Proposal delete(Proposal proposal);

}
