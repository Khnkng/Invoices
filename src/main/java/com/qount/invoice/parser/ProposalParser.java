package com.qount.invoice.parser;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

	public static Proposal getProposalObj(String userId,Proposal proposal) {
		try {
			if (StringUtils.isEmpty(userId) && proposal == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			DateTime dateTime = new DateTime(DateTimeZone.UTC);
//			DateTimeFormatter dtf = DateTimeFormat.forPattern("hh:mm a, MMM d, yyyy");
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:SS");
			String time = dtf.print(dateTime);
			
			proposal.setUser_id(userId);
			proposal.setId(UUID.randomUUID().toString());
			proposal.setLast_updated_at(time);
			proposal.setLast_updated_by(userId);
			List<ProposalLine> proposalLines = proposal.getProposalLines();
			
			Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
			while(proposalLineItr.hasNext()){
				ProposalLine line = proposalLineItr.next();	
				line.setId(UUID.randomUUID().toString());
				line.setProposal_id(proposal.getId());
				line.setLast_updated_at(time);
				line.setLast_updated_by(userId);
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
