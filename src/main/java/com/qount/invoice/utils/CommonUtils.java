package com.qount.invoice.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.qount.invoice.clients.httpClient.JerseyClient;
import com.qount.invoice.model.UserCompany;

public class CommonUtils {
	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";

	public static String toCommaSeparatedString(List<String> strings) {
		Joiner joiner = Joiner.on(",").skipNulls();
		return joiner.join(strings);
	}

	public static List<String> fromCommaSeparatedString(String string) {
		if (StringUtils.isBlank(string)) {
			return new ArrayList<>();
		}
		return Splitter.on("-").trimResults().splitToList(string);
	}

	public static JSONObject getJsonFromString(String str) {
		JSONObject result = null;
		try {
			if (!StringUtils.isBlank(str)) {
				result = new JSONObject(str);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return result;
	}

	public static JSONArray getJsonArrayFromString(String str) {
		JSONArray result = null;
		try {
			if (!StringUtils.isBlank(str)) {
				result = new JSONArray(str);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return result;
	}

	public static boolean isValidJSON(JSONObject jsonObject) {
		boolean result = false;
		try {
			if (null != jsonObject && jsonObject.length() != 0) {
				result = true;
			}
		} catch (Exception e) {
			LOGGER.error(getErrorStackTrace(e));
		}
		return result;
	}

	public static boolean isValidJSONArray(JSONArray jsonArray) {
		boolean result = false;
		try {
			if (null != jsonArray && jsonArray.length() != 0) {
				result = true;
			}
		} catch (Exception e) {
			LOGGER.error(getErrorStackTrace(e));
		}
		return result;
	}

	public static String getErrorStackTrace(Throwable th) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString();
	}

	public static Response constructResponse(String message, int statusHeader) {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put(MESSAGE, message);
		return Response.status(statusHeader).entity(responseJSON.toString()).type(MediaType.APPLICATION_JSON_TYPE)
				.build();
	}

	/**
	 * 
	 * @param userID
	 * @param companyID
	 * @return
	 */
	public static UserCompany getCompany(String userID, String companyID) {
		String path = LTMUtils.getHostAddress("half.service.docker.hostname", "half.service.docker.port",
				"half.service.url");
		path = path + "user/" + userID + "/companies2/" + companyID;
		System.out.println("path = " + path);
		LOGGER.debug("path = " + path);
		String response = JerseyClient.get(path);
		if (StringUtils.isBlank(response)) {
			LOGGER.error("invalid company userID [ " + userID + "] companyID [ " + companyID + " ]");
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					"Invalid Company", Status.PRECONDITION_FAILED));
		}
		return Constants.GSON.fromJson(response, UserCompany.class);
	}
}
