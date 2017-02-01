package com.qount.invoice.parser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.model.ProposalLineTaxes;
import com.qount.invoice.utils.CommonUtils;

public class ProposalLineParser {
	private static final Logger LOGGER = Logger.getLogger(ProposalLineParser.class);

	public static List<ProposalLine> getProposalLineList(String userID, String proposalID,
			List<ProposalLine> proposalLine) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(proposalID) && proposalLine == null) {
				return null;
			}

			Iterator<ProposalLine> proposalLineItr = proposalLine.iterator();
			while (proposalLineItr.hasNext()) {
				ProposalLine proposalLineObj = proposalLineItr.next();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				if (proposalLineObj.getId() == null) {
					proposalLineObj.setId(UUID.randomUUID().toString());
				}
				proposalLineObj.setLast_updated_at(timestamp.toString());
				proposalLineObj.setLast_updated_by(userID);

				List<ProposalLineTaxes> proposalLineTaxes = proposalLineObj.getProposalLineTaxes();
				if (!proposalLineTaxes.isEmpty()) {
					Iterator<ProposalLineTaxes> proposalLineTaxesItr = proposalLineTaxes.iterator();
					while (proposalLineTaxesItr.hasNext()) {
						ProposalLineTaxes ProposalLineTaxesObj = proposalLineTaxesItr.next();
						ProposalLineTaxesObj.setProposal_line_id(ProposalLineTaxesObj.getProposal_line_id());
						ProposalLineTaxesObj.setTax_id(ProposalLineTaxesObj.getTax_id());
						ProposalLineTaxesObj.setTax_rate(ProposalLineTaxesObj.getTax_rate());
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
		}
		return proposalLine;
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
				restlt.add(proposalLineTaxes);
			}
		}
		return restlt;
	}
}
