package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.ProposalLineTaxes;

public interface ProposalLineTaxesDAO {
	
	public ProposalLineTaxes save(Connection connection, ProposalLineTaxes proposalLineTaxes);

	public List<ProposalLineTaxes> batchSave(Connection connection, List<ProposalLineTaxes> proposalLinesTaxes);

	public ProposalLineTaxes batchDelete(Connection connection, List<ProposalLineTaxes> proposalLinesTaxes);

	public ProposalLineTaxes deleteProposalLine(ProposalLineTaxes proposalLineTaxes);
}
