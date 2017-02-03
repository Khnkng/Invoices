package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

/**
 * @author Apurva
 * @version 1.0 Jan 13 2017
 */
public class ProposalParser {

	private static final Logger LOGGER = Logger.getLogger(ProposalParser.class);

	public static Proposal getProposalObj(String userId, Proposal proposal) {
		try {
			if (StringUtils.isEmpty(userId) && proposal == null) {
				return null;
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp proposal_date = convertStringToTimeStamp(proposal.getProposal_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp acceptance_date = convertStringToTimeStamp(proposal.getAcceptance_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			Timestamp acceptance_final_date = convertStringToTimeStamp(proposal.getAcceptance_final_date(),
					Constants.TIME_STATMP_TO_BILLS_FORMAT);
			proposal.setUser_id(userId);
			if (proposal.getId() == null) {
				proposal.setId(UUID.randomUUID().toString());
			}
			proposal.setLast_updated_at(timestamp.toString());
			proposal.setLast_updated_by(userId);
			proposal.setProposal_date(proposal_date.toString());
			proposal.setAcceptance_date(acceptance_date.toString());
			proposal.setAcceptance_final_date(acceptance_final_date.toString());

			List<ProposalLine> proposalLines = proposal.getProposalLines();
			if (!proposalLines.isEmpty()) {
				Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
				while (proposalLineItr.hasNext()) {
					ProposalLine line = proposalLineItr.next();
					line.setId(UUID.randomUUID().toString());
					line.setProposal_id(proposal.getId());
					line.setLast_updated_at(timestamp.toString());
					line.setLast_updated_by(userId);
				}
			}

		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
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

	public static Timestamp convertStringToTimeStamp(String dateStr, SimpleDateFormat sdf) {
		try {
			return new Timestamp(sdf.parse(dateStr).getTime());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

	public static List<ProposalLineTaxes> getProposalLineTaxesList(List<ProposalLine> proposalLinesList) {
		List<ProposalLineTaxes> restlt = new ArrayList<ProposalLineTaxes>();
		Iterator<ProposalLine> ProposalLineItr = proposalLinesList.iterator();
		while (ProposalLineItr.hasNext()) {
			ProposalLine proposalLine = ProposalLineItr.next();
			List<ProposalLineTaxes> lineTaxesList = proposalLine.getProposalLineTaxes();
			Iterator<ProposalLineTaxes> ProposalLineTaxesItr = lineTaxesList.iterator();
			while (ProposalLineTaxesItr.hasNext()) {
				ProposalLineTaxes proposalLineTaxes = ProposalLineTaxesItr.next();
				proposalLineTaxes.setProposal_line_id(proposalLine.getId());
				restlt.add(proposalLineTaxes);
			}
		}
		return restlt;
	}
}
