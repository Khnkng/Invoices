package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.UserCompany;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.CurrencyConverter;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.ResponseUtil;

/**
 * @author Apurva
 * @version 1.0 Jan 13 2017
 */
public class ProposalParser {

	private static final Logger LOGGER = Logger.getLogger(ProposalParser.class);

	public static Proposal getProposalObj(String userId, Proposal proposal,String companyID) {
		try {
			if (proposal == null || StringUtils.isAnyBlank(userId,companyID,proposal.getCurrency())) {
				throw new WebApplicationException("userId, companyId, currency are mandatory");
			}
			UserCompany userCompany = null;
			proposal.setCompany_id(companyID);
			proposal.setIs_recurring(StringUtils.isNotEmpty(proposal.getPlan_id()));
			userCompany = CommonUtils.getCompany(userId, companyID);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Timestamp proposal_date = convertStringToTimeStamp(proposal.getProposal_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			Timestamp due_date = convertStringToTimeStamp(proposal.getDue_date(), Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			proposal.setUser_id(userId);
			if (StringUtils.isBlank(proposal.getId())) {
				proposal.setId(UUID.randomUUID().toString());
			}
			proposal.setProposal_date(proposal_date != null ? proposal_date.toString() : null);
			proposal.setDue_date(due_date != null ? due_date.toString() : null);
			proposal.setLast_updated_at(timestamp != null ? timestamp.toString() : null);
			proposal.setLast_updated_by(userId);
			setProposalAmountByDate(proposal, userCompany);
			List<ProposalLine> proposalLines = proposal.getProposalLines();
			if (proposalLines == null) {
				proposalLines = new ArrayList<>();
			}
			Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
			while (proposalLineItr.hasNext()) {
				ProposalLine line = proposalLineItr.next();
				if (StringUtils.isBlank(line.getId())) {
					line.setId(UUID.randomUUID().toString());
				}
				line.setProposal_id(proposal.getId());
				line.setLast_updated_at(timestamp.toString());
				line.setLast_updated_by(userId);
			}
			proposal.setCreated_at(timestamp.toString());
			proposal.setRecepientsMailsArr(CommonUtils.getJsonArrayFromList(proposal.getRecepientsMails()));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return proposal;
	}

	public static void setProposalAmountByDate(Proposal proposal, UserCompany userCompany) {
		try {
			Double amount = proposal.getAmount();
			String companyCurrency = userCompany.getDefaultCurrency();
			String proposalLineCurrency = proposal.getCurrency();
			Double proposalLineDateAmount = 0d;
			if (amount != null) {
				if (StringUtils.isAnyBlank(companyCurrency, proposalLineCurrency)) {
					return;
				}
				proposal.setAmount_by_date(amount);
				if (!proposalLineCurrency.equals(companyCurrency)) {
					CurrencyConverter converter = new CurrencyConverter();
					Date date = DateUtils.getDateFromString(proposal.getLast_updated_at(), Constants.DUE_DATE_FORMAT);
					String formatedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
					float conversion = converter.convert(proposalLineCurrency, companyCurrency, formatedDate);
					proposalLineDateAmount = amount * conversion;
					proposalLineDateAmount = Double.valueOf(new DecimalFormat("#.##").format(proposalLineDateAmount));
					proposal.setAmount_by_date(proposalLineDateAmount);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error converting currency", e);
		}

	}

	public static Proposal getProposalObjToDelete(String userId,String companyId,String proposal_id) {
		try {
			if (StringUtils.isEmpty(proposal_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Proposal proposal = new Proposal();
			proposal.setUser_id(userId);
			proposal.setCompany_id(companyId);
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
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
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
	
	public static String convertTimeStampToString(String dateStr, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	public static Proposal convertTimeStampToString(Proposal proposal) {
		try {
			if (proposal != null) {
				proposal.setProposal_date(convertTimeStampToString(proposal.getProposal_date(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return proposal;
	}
}
