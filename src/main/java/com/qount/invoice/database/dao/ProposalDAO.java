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

	public boolean save(Connection connection, Proposal proposal);

	public boolean update(Connection connection, Proposal proposal);

	Proposal get(Connection connection, String companyID, String proposalID,String userID);

	List<Proposal> getList(Connection connection, String companyID);

}
