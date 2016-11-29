package com.qount.invoice.utils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;


public class ResponseUtil {
	
	public static final String STATUS = "status";
	public static final String MESSAGE = "message";
	

	/**
	 * 
	 * @param status
	 * @param message
	 * @param statusHeader
	 * @return
	 */
	public static Response constructResponse(String status, String message, Status statusHeader) {
		JSONObject responseJSON = new JSONObject();
		responseJSON.put(STATUS, status);
		responseJSON.put(MESSAGE, message);
		return Response.status(statusHeader).entity(responseJSON.toString()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getSuccessEntity(){
		JSONObject responseJSON = new JSONObject();
		responseJSON.put(STATUS, Constants.SUCCESS_STATUS);
		responseJSON.put(MESSAGE, Constants.SUCCESS_STATUS);
		return responseJSON.toString();
	}
	

	public static Response getResponse(int statusCode, JSONObject jsonObject) {
		String jsonStr = jsonObject != null ? jsonObject.toString() : null;
		return Response.status(statusCode).entity(jsonStr).header("status",statusCode).build();
	}
}