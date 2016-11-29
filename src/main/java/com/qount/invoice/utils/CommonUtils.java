package com.qount.invoice.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

public class CommonUtils {
	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class);

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
	
	public static JSONObject getJsonFromString(String str){
		JSONObject result =null;
		try {
			if(!StringUtils.isBlank(str)){
				result = new JSONObject(str);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return result;
	}
	
	public static JSONArray getJsonArrayFromString(String str){
		JSONArray result =null;
		try {
			if(!StringUtils.isBlank(str)){
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
}
