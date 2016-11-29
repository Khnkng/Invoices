package com.qount.invoice.clients.httpClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

public class JerseyClient {

	private static final Logger LOGGER = Logger.getLogger(JerseyClient.class);

	private static final Client CLIENT = ClientBuilder.newClient();

	public static String get(String url) {
		String response = null;
		try {
			Response responseEntity = CLIENT.target(url).request().accept(MediaType.APPLICATION_JSON).get();
			response = responseEntity.readEntity(String.class);
		} catch (Exception e) {
			LOGGER.error("Error calling target URL [ " + url + " ]", e);
		}
		return response;
	}

	public static String post(String url, String payload) {
		String response = null;
		try {
			Response responseEntity = CLIENT.target(url).request().accept(MediaType.APPLICATION_JSON)
					.post(Entity.entity(payload, MediaType.APPLICATION_JSON));
			response = responseEntity.readEntity(String.class);
		} catch (Exception e) {
			LOGGER.error("Error calling target URL [ " + url + " ]", e);
		}
		return response;
	}

	public static String put(String url, String payload) {
		String response = null;
		try {
			Response responseEntity = CLIENT.target(url).request().accept(MediaType.APPLICATION_JSON)
					.put(Entity.entity(payload, MediaType.APPLICATION_JSON));
			response = responseEntity.readEntity(String.class);
		} catch (Exception e) {
			LOGGER.error("Error calling target URL [ " + url + " ]", e);
		}
		return response;
	}

}
