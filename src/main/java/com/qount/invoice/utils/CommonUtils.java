package com.qount.invoice.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.clients.httpClient.JerseyClient;
import com.qount.invoice.model.Company2;
import com.qount.invoice.model.UserCompany;
import com.qount.jwt.JWTTokenService;

public class CommonUtils {
	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";

	public static String toCommaSeparatedString(List<String> strings) {
		Joiner joiner = Joiner.on(",").skipNulls();
		return joiner.join(strings);
	}

	public static String toQoutedCommaSeparatedString(List<String> strings) {
		String result = null;
		if (strings != null && !strings.isEmpty()) {
			result = "";
			for (int i = 0; i < strings.size(); i++) {
				result += "'" + strings.get(i) + "',";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return result;
	}

	public static List<String> getListString(String str) {
		List<String> result = null;
		try {
			if (!StringUtils.isBlank(str)) {
				JSONArray emailArr = getJsonArrayFromString(str);
				if (isValidJSONArray(emailArr)) {
					result = new ArrayList<String>();
					for (int i = 0; i < emailArr.length(); i++) {
						result.add(emailArr.optString(i));
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return result;
	}

	public static JSONArray getJsonArrayFromList(List<String> lst) {
		JSONArray result = null;
		try {
			if (lst != null && !lst.isEmpty()) {
				result = new JSONArray();
				for (int i = 0; i < lst.size(); i++) {
					result.put(lst.get(i));
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
		return Response.status(statusHeader).entity(responseJSON.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	/**
	 * 
	 * @param userID
	 * @param companyID
	 * @return
	 */
	public static UserCompany getCompany(String userID, String companyID) {
		String path = LTMUtils.getHostAddress("half.service.docker.hostname", "half.service.docker.port", "oneapp.base.url");
		path = path + "HalfService/user/" + userID + "/companies2/" + companyID;
		System.out.println("path = " + path);
		LOGGER.debug("path = " + path);
		String response = JerseyClient.get(path);
		if (StringUtils.isBlank(response)) {
			LOGGER.error("invalid company userID [ " + userID + "] companyID [ " + companyID + " ]");
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Invalid Company", Status.PRECONDITION_FAILED));
		}
		return Constants.GSON.fromJson(response, UserCompany.class);
	}

	public static UserCompany getCompany(String companyID) {
		String path = LTMUtils.getHostAddress("half.service.docker.hostname", "half.service.docker.port", "oneapp.base.url");
		path = path + "HalfService/companies/" + companyID;
		System.out.println("path = " + path);
		LOGGER.debug("path = " + path);
		String token = new JWTTokenService().generate(companyID);
		Response responseEntity = ClientBuilder.newClient().target(path).request().header("token", token).accept(MediaType.APPLICATION_JSON).get();
		String response = responseEntity.readEntity(String.class);
		if (StringUtils.isBlank(response)) {
			LOGGER.error("invalid company companyID [ " + companyID + " ]");
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Invalid Company", Status.PRECONDITION_FAILED));
		}
		return Constants.GSON.fromJson(response, UserCompany.class);
	}

	public static Company2 retrieveCompany(String userId, String companyID) {
		try {
			String hostVaribale = "half.service.docker.hostname";
			String portVariable = "half.service.docker.port";
			String GTMVariable = "oneapp.base.url";
			String url = LTMUtils.getHostAddress(hostVaribale, portVariable, GTMVariable);
			url += "HalfService/user/{userId}/companies2/{companyId}";
			url = url.replace("{userId}", userId).replace("{companyId}", companyID);
			JSONObject responseJSON = HTTPClient.get(url);
			return Constants.GSON.fromJson(responseJSON.toString(), Company2.class);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static String convertDate(String sourceDate, SimpleDateFormat sourceDateFormat, SimpleDateFormat resultDateFormat) {
		try {
			return resultDateFormat.format(sourceDateFormat.parse(sourceDate));
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static Date getDate(String sourceDate, SimpleDateFormat sourceDateFormat) {
		try {
			return sourceDateFormat.parse(sourceDate);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static String getGMTDateTime(Date sourceDate) {
		try {
			return Constants.DATE_FORMAT_GMT.format(sourceDate);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return null;
	}

	public static boolean isValidStrings(String... strings) throws Exception {
		try {
			if (strings == null || strings.length == 0) {
				throw new Exception("empty input");
			}
			for (String str : strings) {
				if (StringUtils.isEmpty(str)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return false;
	}

	public static boolean isAnyStringValid(String... strings) throws Exception {
		try {
			if (strings == null || strings.length == 0) {
				throw new Exception("empty input");
			}
			for (String str : strings) {
				if (StringUtils.isNotBlank(str)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
		return false;
	}

	public static void removeKeysIfNull(JSONObject input, String... keys) {
		try {
			for (String key : keys) {
				if (StringUtils.isEmpty(input.optString(key))) {
					input.remove(key);
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	public static JSONObject createJournal(String payload, String userID, String companyID) {
		LOGGER.debug("entered createJournal(String payload:"+payload+", String userID:"+userID+", String companyID:"+companyID);
		JSONObject responseJSON = null;
		JSONObject queJSON = new JSONObject(payload).put("companyID", companyID).put("userID", userID);
		try {
			String path = LTMUtils.getHostAddress("qounting.service.docker.hostname", "qounting.service.docker.port", "oneapp.base.url");
			path = path + "Qounting/users/" + userID + "/companies/" + companyID + "/journals";
			LOGGER.debug("path = " + path);
			LOGGER.debug("payload = " + payload);
			String responseString = JerseyClient.post(path, payload);
			LOGGER.debug("responseString = " + responseString);
			if (StringUtils.isBlank(responseString)) {
				throw new Exception(queJSON.toString());
			}
			responseJSON = new JSONObject(responseString);
			if (Constants.FAILURE_STATUS_STR.equalsIgnoreCase(responseJSON.optString("status"))) {
				throw new Exception(queJSON.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Error creating invoice journal", e);
			RedisUtils.writeToQue(queJSON.toString());
		} finally{
			LOGGER.debug("exited createJournal(String payload:"+payload+", String userID:"+userID+", String companyID:"+companyID);
		}
		return responseJSON;
	}

	public static JSONObject createJournal(String payload, String companyID) {
		JSONObject responseJSON = null;
		JSONObject queJSON = new JSONObject(payload).put("companyID", companyID);
		try {
			String path = LTMUtils.getHostAddress("qounting.service.docker.hostname", "qounting.service.docker.port", "oneapp.base.url");
			path = path + "Qounting/companies/" + companyID + "/journals";
			LOGGER.debug("path = " + path);
			LOGGER.debug("payload = " + payload);
			String token = new JWTTokenService().generate(companyID);
			Response responseEntity = ClientBuilder.newClient().target(path).request().header("token", token).accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(payload, MediaType.APPLICATION_JSON));
			String responseString = responseEntity.readEntity(String.class);
			LOGGER.debug("responseString = " + responseString);
			if (StringUtils.isBlank(responseString)) {
				throw new Exception(queJSON.toString());
			}
			responseJSON = new JSONObject(responseString);
			if (Constants.FAILURE_STATUS_STR.equalsIgnoreCase(responseJSON.optString("status"))) {
				throw new Exception(queJSON.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Error creating invoice journal", e);
			RedisUtils.writeToQue(queJSON.toString());
		}
		return responseJSON;
	}

	public static void createJournalAsync(String payload, String userID, String companyID) {
		Runnable task2 = () -> {
			JSONObject responseJSON = null;
			JSONObject queJSON = new JSONObject(payload).put("companyID", companyID).put("userID", userID);
			try {
				String path = LTMUtils.getHostAddress("qounting.service.docker.hostname", "qounting.service.docker.port", "oneapp.base.url");
				path = path + "Qounting/users/" + userID + "/companies/" + companyID + "/journals";
				LOGGER.debug("path = " + path);
				LOGGER.debug("payload = " + payload);
				String responseString = JerseyClient.post(path, payload);
				LOGGER.debug("responseString = " + responseString);
				if (StringUtils.isBlank(responseString)) {
					throw new Exception(queJSON.toString());
				}
				responseJSON = new JSONObject(responseString);
				if (Constants.FAILURE_STATUS_STR.equalsIgnoreCase(responseJSON.optString("status"))) {
					throw new Exception(queJSON.toString());
				}
			} catch (Exception e) {
				LOGGER.error("Error creating invoice journal", e);
				RedisUtils.writeToQue(queJSON.toString());
			}
		};
		new Thread(task2).start();
	}
	
	public static enum SubStates {
		draft("Draft"),  paid("Paid"), sent("Sent"), partially_paid("Partially Paid"), open("Open"), click("Click"), delivered("Delivered"), past_due("Past Due");

		private String value;

		private SubStates(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
	
	public static String getSubState(String name){
		if(StringUtils.isBlank(name)){
			return name;
		}
		String value = null;
		try {
			value = SubStates.valueOf(name.toLowerCase()).getValue();
		} catch (Exception e) {
			LOGGER.error("Invalid name [ " + name + " ]",e);
			value = name;
		}
		return value;
	}

	public static void deleteJournal(String userID, String companyID, String source) {
		try {
			String path = LTMUtils.getHostAddress("journal.service.docker.hostname", "journal.service.docker.port", "oneapp.base.url");
			path = path + "Journal/users/" + userID + "/companies/" + companyID + "/sources/" + source;
			LOGGER.debug("journal deletion path = [ " + path + " ]");
			String responseString = JerseyClient.delete(path);
			LOGGER.debug("Journal deletion status [ " + responseString + " ]");
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static void deleteJournalsAsync(String userID, String companyID, List<String> invoiceIds) {
		try {
			Runnable task2 = () -> {
				String path = LTMUtils.getHostAddress("journal.service.docker.hostname", "journal.service.docker.port", "oneapp.base.url");
				for (String invoiceID : invoiceIds) {
					path = path + "Journal/users/" + userID + "/companies/" + companyID + "/sources/" + invoiceID + "@" + "invoice";
					LOGGER.debug("journal deletion path = [ " + path + " ]");
					String responseString = JerseyClient.delete(path);
					LOGGER.debug("Journal deletion status [ " + responseString + " ]");
				}
			};
			new Thread(task2).start();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static void main(String[] args) {
		String token = new JWTTokenService().generate("4b5195e9-e4b5-476b-89f7-296fbecd5afa");
		System.out.println(token);
	}
}
