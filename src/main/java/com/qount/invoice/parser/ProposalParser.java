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
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.model.ProposalTaxes;
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

	public static Proposal getProposalObj(String userId, Proposal proposal,String companyId) {
		try {
			if (StringUtils.isEmpty(userId) && proposal == null) {
				return null;
			}
			UserCompany userCompany = null;
			userCompany = CommonUtils.getCompany(userId, companyId);
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
			proposal.setLast_updated_at(null==timestamp?null:timestamp.toString());
			proposal.setLast_updated_by(userId);
			proposal.setProposal_date(null==proposal_date?null:proposal_date.toString());
			proposal.setAcceptance_date(null==acceptance_date?null:acceptance_date.toString());
			proposal.setAcceptance_final_date(null==acceptance_final_date?null:acceptance_final_date.toString());
			setProposalAmountByDate(proposal, userCompany);

			List<ProposalLine> proposalLines = proposal.getProposalLines();
			if (proposalLines == null) {
				proposalLines = new ArrayList<>();
			}
			Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
			while (proposalLineItr.hasNext()) {
				ProposalLine line = proposalLineItr.next();
				line.setId(UUID.randomUUID().toString());
				line.setProposal_id(proposal.getId());
				line.setLast_updated_at(null==timestamp?null:timestamp.toString());
				line.setLast_updated_by(userId);
			}

			List<ProposalTaxes> proposalTaxesList = proposal.getProposalTaxes();
			if (proposalTaxesList == null) {
				proposalTaxesList = new ArrayList<>();
				proposal.setProposalTaxes(proposalTaxesList);
			}
			Iterator<ProposalTaxes> proposalTaxesListItr = proposalTaxesList.iterator();
			while (proposalTaxesListItr.hasNext()) {
				ProposalTaxes proposalTaxes = proposalTaxesListItr.next();
				proposalTaxes.setProposal_id(proposal.getId());
			}

		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
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

	public static Proposal getProposalObjToDelete(String proposal_id) {
		try {
			if (StringUtils.isEmpty(proposal_id)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
						Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			Proposal proposal = new Proposal();
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
