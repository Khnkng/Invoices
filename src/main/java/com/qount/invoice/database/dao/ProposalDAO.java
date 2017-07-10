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

	public Proposal save(Connection connection, Proposal proposal) throws Exception;;

	public Proposal update(Connection connection, Proposal proposal) throws Exception;;

	public Proposal get(String ProposalID) throws Exception;;

	public Proposal delete(Proposal proposal) throws Exception;

	public boolean deleteLst(String userId, String companyId, String lst) throws Exception;

	public boolean updateState(String userId, String companyId, String lst, String state) throws Exception;
	
	public List<Proposal> getProposalList(String userID, String companyID, String state) throws Exception;;

}
