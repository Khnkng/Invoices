package com.qount.invoice.utils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.common.PropertyManager;

public class Utilities {

	private static final Logger LOGGER = Logger.getLogger(Utilities.class);

	public static Response constructResponse(String message, int statusHeader) {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put("message", message);
		return Response.status(statusHeader).entity(responseJSON.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	public static String getLtmUrl(String hostName, String portName) {
		String path = null;
		try {
			String internalLinkingAddressStr = PropertyManager.getProperty(hostName, null);
			String internalLinkingPortStr = PropertyManager.getProperty(portName, null);
			String internalLinkingAddress = null, internalLinkingPort = null;
			internalLinkingAddress = System.getenv(internalLinkingAddressStr);
			internalLinkingPort = System.getenv(internalLinkingPortStr);
			if (!StringUtils.isBlank(internalLinkingAddress) && !StringUtils.isBlank(internalLinkingPort)) {
				path = "http://" + internalLinkingAddress + ":" + internalLinkingPort + "/";

			}
			return path;
		} catch (

		Exception e) {
			LOGGER.error(e);
			System.out.println(e);
		}
		return null;
	}
	
}
