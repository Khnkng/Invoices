package com.qount.invoice.service;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;
import com.qount.invoice.utils.CommonUtils;

/**
 * @author Apurva
 * @version 1.0 Nov 15 2016
 */
public class ProposalParser {
	private static final Logger LOGGER = Logger.getLogger(ProposalParser.class);

	public static Proposal getProposalObject(String userID, String companyID, String json, boolean createFlag) {
		Proposal result = null;
		JSONObject bodyJson = CommonUtils.getJsonFromString(json);
		JSONArray lines = bodyJson.optJSONArray("proposalLines");
		if (!CommonUtils.isValidJSON(bodyJson)) {
			return result;
		}
		try {
			String customer_name = bodyJson.optString("customer_name") != null ? bodyJson.optString("customer_name")
					: null;
			double total_amount = bodyJson.optDouble("total_amount");
			String currency = bodyJson.optString("currency") != null ? bodyJson.optString("currency") : null;
			boolean bank_account = bodyJson.optBoolean("bank_account");
			boolean credit_card = bodyJson.optBoolean("credit_card");
			result = new Proposal();
			result.setUserID(userID);
			result.setCompanyID(companyID);
			result.setCustomer_name(customer_name);
			result.setTotal_amount((float) total_amount);
			result.setCurrency(currency);
			result.setBank_account(bank_account);
			result.setCredit_card(credit_card);
			if (CommonUtils.isValidJSONArray(lines)) {
				@SuppressWarnings("serial")
				List<ProposalLine> proposalLines = new Gson().fromJson(lines.toString(),
						new TypeToken<List<ProposalLine>>() {
						}.getType());
				result.setProposalLines(proposalLines);

				for (ProposalLine proposalLine : proposalLines) {
					proposalLine.setLineID(UUID.randomUUID().toString());
					proposalLine.setProposalID(result.getProposalID());
				}
			}
			if (!createFlag) {
				String proposalID = bodyJson.optString("proposalID");
				if (StringUtils.isBlank(proposalID)) {
					return null;
				}
				result.setProposalID(proposalID);
			} else if (createFlag) {
				result.setProposalID(UUID.randomUUID().toString());
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e);
		}
		return result;
	}

	public static JSONObject ProposalResponse(Proposal proposal) throws JSONException {
		JSONObject result = null;
		if (proposal == null) {
			return result;
		}
		try {
			result = new JSONObject(new ObjectMapper().writeValueAsString(proposal));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e);
		}
		return result;
	}

	public static JSONArray getAllproposalsResponse(List<Proposal> proposal) throws JSONException {
		JSONArray result = new JSONArray();
		if (proposal == null || proposal.size() == 0) {
			return result;
		}
		Iterator<Proposal> proposalItr = proposal.iterator();
		Proposal proposal1;
		while (proposalItr.hasNext()) {
			proposal1 = proposalItr.next();
			if (null != proposal1) {
				try {
					JSONObject resultObj = new JSONObject(new ObjectMapper().writeValueAsString(proposal1));
					result.put(resultObj);
				} catch (Exception e) {
					LOGGER.error(CommonUtils.getErrorStackTrace(e));
					throw new WebApplicationException(e);
				}
			}
		}
		return result;
	}
}
