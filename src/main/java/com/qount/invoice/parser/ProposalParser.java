package com.qount.invoice.parser;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

/**
 * @author Apurva
 * @version 1.0 Jan 13 2017
 */
public class ProposalParser {

	private static final Logger LOGGER = Logger.getLogger(InvoiceParser.class);

	public static Proposal getProposalObj(String user_id,Proposal proposal) {
		try {
			if (StringUtils.isEmpty(user_id) && proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			proposal.setUser_id(user_id);
			proposal.setId(UUID.randomUUID().toString());
			proposal.setCreated_at(new Date().getTime());
			proposal.setCreated_by(user_id);
			proposal.setLast_updated_at(new Date().getTime());
			proposal.setLast_updated_by(user_id);
			List<ProposalLine> proposalLines = proposal.getProposalLines();

			for (ProposalLine proposalLine : proposalLines) {
				proposalLine.setId(UUID.randomUUID().toString());
				proposalLine.setProposal_id(proposal.getId());
				proposalLine.setCreated_at(new Date().getTime());
				proposalLine.setCreated_by(user_id);
				proposalLine.setLast_updated_at(new Date().getTime());
				proposalLine.setLast_updated_by(user_id);
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return proposal;
	}

	public static Proposal getProposalObjToUpdate(String user_id, String proposal_id, Proposal proposal) {
		try {
			if (StringUtils.isEmpty(user_id) && StringUtils.isEmpty(proposal_id) && proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			proposal.setUser_id(user_id);
			proposal.setId(proposal_id);
			proposal.setLast_updated_at(new Date().getTime());
			proposal.setLast_updated_by(user_id);
			List<ProposalLine> proposalLines = proposal.getProposalLines();

			for (ProposalLine proposalLine : proposalLines) {
				proposalLine.setId(UUID.randomUUID().toString());
				proposalLine.setProposal_id(proposal.getId());
				proposalLine.setLast_updated_at(new Date().getTime());
				proposalLine.setLast_updated_by(user_id);
				
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return proposal;
	}

	public static Proposal getProposalObjToDelete(String user_id, String proposal_id) {
		try {
			if (StringUtils.isEmpty(user_id) && StringUtils.isEmpty(proposal_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			Proposal proposal = new Proposal();
			proposal.setUser_id(user_id);
			proposal.setId(proposal_id);
			return proposal;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
	}

	public static ProposalLine getProposalLineObjToDeleteProposalLine(String proposal_id, String proposalLine_id) {
		try {
			if (StringUtils.isEmpty(proposal_id) && StringUtils.isEmpty(proposalLine_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			ProposalLine proposalLine = new ProposalLine();
			proposalLine.setId(proposalLine_id);
			proposalLine.setProposal_id(proposal_id);
			return proposalLine;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
	}

}
