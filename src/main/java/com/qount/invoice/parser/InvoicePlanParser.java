package com.qount.invoice.parser;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.model.DaysMap;
import com.qount.invoice.model.InvoicePlan;
import com.qount.invoice.utils.CommonUtils;

/**
 * @author Apurva
 * @version 1.0 Jan 11 2017
 */
public class InvoicePlanParser {
	private static final Logger LOGGER = Logger.getLogger(InvoicePlanParser.class);

	public static InvoicePlan getInvoicePlanObj(String userId, InvoicePlan invoicePlan, String companyID) {
		try {
			if (StringUtils.isEmpty(userId) || invoicePlan == null || StringUtils.isEmpty(companyID) ) {
				return null;
			}
			invoicePlan.setCompany_id(companyID);
			invoicePlan.setUser_id(userId);
			invoicePlan.setLast_updated_by(userId);
			if(invoicePlan.getDay_map()==null){
				invoicePlan.setDay_map(new DaysMap());
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		}
		return invoicePlan;
	}
	
	public static JSONObject getJsonForPaymentSpringPlan(InvoicePlan invoicePlan) throws Exception {
		try {
			LOGGER.debug("entered getJsonForPaymentSpringPlan :" + invoicePlan);
			JSONObject result = new JSONObject(invoicePlan.toString());
			if(StringUtils.isEmpty(result.optString("day"))){
				result.put("day", result.optJSONObject("day_map"));
				result.remove("day_map");
			}
			CommonUtils.removeKeysIfNull(result, "bill_immediately", "ends_after", "day");
			return result;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			LOGGER.debug("exited getJsonForPaymentSpringPlan :" + invoicePlan);
		}
	}
	

}
