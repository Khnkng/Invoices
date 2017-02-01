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
			List<ProposalLine> proposalLines) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(proposalID) && proposalLines == null) {
				return null;
			}

			Iterator<ProposalLine> proposalLineItr = proposalLines.iterator();
			while (proposalLineItr.hasNext()) {
				ProposalLine proposalLineObj = proposalLineItr.next();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				if (proposalLineObj.getId() == null) {
					proposalLineObj.setId(UUID.randomUUID().toString());
				}
				proposalLineObj.setLast_updated_at(timestamp.toString());
				proposalLineObj.setLast_updated_by(userID);

			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
		}
		return proposalLines;
	}

	public static ProposalLine getProposalLineObj(String userID, String proposalID, String lineID,
			ProposalLine proposalLine) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(proposalID) && proposalLine == null) {
				return null;
			}

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			if (proposalLine.getId() == null) {
				proposalLine.setId(UUID.randomUUID().toString());
			}
			proposalLine.setLast_updated_at(timestamp.toString());
			proposalLine.setLast_updated_by(userID);

		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			return null;
		}
		return proposalLine;
	}

	public static List<ProposalLineTaxes> getProposalLineTaxesList(List<ProposalLine> proposalLinesList) {
		List<ProposalLineTaxes> result = new ArrayList<ProposalLineTaxes>();
		Iterator<ProposalLine> ProposalLineItr = proposalLinesList.iterator();
		while (ProposalLineItr.hasNext()) {
			ProposalLine proposalLine = ProposalLineItr.next();
			List<ProposalLineTaxes> lineTaxesList = proposalLine.getProposalLineTaxes();
			Iterator<ProposalLineTaxes> ProposalLineTaxesItr = lineTaxesList.iterator();
			while (ProposalLineTaxesItr.hasNext()) {
				ProposalLineTaxes proposalLineTaxes = ProposalLineTaxesItr.next();
				result.add(proposalLineTaxes);
			}
		}
		return result;
	}

}
