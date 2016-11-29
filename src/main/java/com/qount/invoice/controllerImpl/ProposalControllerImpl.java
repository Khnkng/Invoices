package com.qount.invoice.controllerImpl;

import java.util.List;
import java.util.UUID;

import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.service.ProposalService;

public class ProposalControllerImpl {

	public static Proposal createProposal(String userID, String companyID, Proposal proposal) {
		proposal.setCompanyID(companyID);
		proposal.setProposalID(UUID.randomUUID().toString());
		List<ProposalLine> proposalLines = proposal.getProposalLines();

		for (ProposalLine proposalLine : proposalLines) {
			proposalLine.setLineID(UUID.randomUUID().toString());
			proposalLine.setProposalID(proposal.getProposalID());
		}
		return ProposalService.createProposal(companyID, proposal);
	}

	public static List<Proposal> getProposals(String userID, String companyID) {
		return ProposalService.getProposals(userID, companyID);
	}

	public static Proposal updateProposal(String userID, String companyID, String proposalID, Proposal proposal) {
		proposal.setProposalID(proposalID);
		proposal.setCompanyID(companyID);
		List<ProposalLine> proposalLines = proposal.getProposalLines();
		for (ProposalLine proposalLine : proposalLines) {
			if (proposalLine.getLineID() == null) {
				proposalLine.setLineID(UUID.randomUUID().toString());
			}
			proposalLine.setProposalID(proposal.getProposalID());
		}
		return ProposalService.update(companyID, proposal, proposalID);
	}

	public static Proposal getProposal(String userID, String companyID, String proposalID) {
		return ProposalService.getProposal(userID, companyID, proposalID);

	}
}
