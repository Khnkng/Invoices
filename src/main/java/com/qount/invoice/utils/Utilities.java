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
			System.out.println("hostName:"+hostName);
			System.out.println("portName:"+portName);
			String internalLinkingAddressStr = PropertyManager.getProperty(hostName, null);
			System.out.println("internalLinkingAddressStr:"+internalLinkingAddressStr);
			String internalLinkingPortStr = PropertyManager.getProperty(portName, null);
			System.out.println("internalLinkingPortStr:"+internalLinkingPortStr);
			String internalLinkingAddress = null, internalLinkingPort = null;
			internalLinkingAddress = System.getenv(internalLinkingAddressStr);
			System.out.println("internalLinkingAddress:"+internalLinkingAddress);
			internalLinkingPort = System.getenv(internalLinkingPortStr);
			System.out.println("internalLinkingPort:"+internalLinkingPort);
			if (!StringUtils.isBlank(internalLinkingAddress) && !StringUtils.isBlank(internalLinkingPort)) {
				path = "http://" + internalLinkingAddress + ":" + internalLinkingPort + "/";
			}
			return path;
		} catch (Exception e) {
			LOGGER.error(e);
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

}
