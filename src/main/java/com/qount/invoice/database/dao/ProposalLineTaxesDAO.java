package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.ProposalLineTaxes;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
public interface ProposalLineTaxesDAO {

	public ProposalLineTaxes save(Connection connection, ProposalLineTaxes proposalLineTaxes);

	public List<ProposalLineTaxes> batchSave(Connection connection, List<ProposalLineTaxes> proposalLinesTaxes);

	public ProposalLineTaxes deleteProposalLine(ProposalLineTaxes proposalLineTaxes);

	public List<ProposalLineTaxes> batchDeleteAndSave(Connection connection,String proposalId, String proposalLineId,
			List<ProposalLineTaxes> proposalLineTaxes);
}
