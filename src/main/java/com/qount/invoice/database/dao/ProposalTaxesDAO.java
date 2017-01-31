package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.ProposalTaxes;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 25 Jan 2016
 *
 */
public interface ProposalTaxesDAO {


	public List<ProposalTaxes> saveProposalTaxes(Connection connection, List<ProposalTaxes> proposalTaxes);
	
	public List<ProposalTaxes> update(Connection connection, List<ProposalTaxes> proposalTaxes);

	public List<ProposalTaxes> batchDeleteAndSave(String proposalId,List<ProposalTaxes> proposalTaxes);

	public ProposalTaxes deleteProposalTax(ProposalTaxes proposalTax);
}
