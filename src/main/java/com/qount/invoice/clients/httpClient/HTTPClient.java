package com.qount.invoice.clients.httpClient;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.utils.CommonUtils;

public class HTTPClient {

	private static final Logger LOGGER = Logger.getLogger(HTTPClient.class);

	private static final CloseableHttpClient HTTPCLIENT = HttpClients.createDefault();

	/**
	 * 
	 * @param url
	 * @param payload
	 * @return
	 */
	public static JSONObject post(String url, String payload) throws Exception{
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(payload));
			responseEntity = HTTPCLIENT.execute(post);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = new JSONObject(response);
			}
		} catch (Exception e) {
			LOGGER.error("Error calling service", e);
			throw e;
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}
	
	/**
	 * 
	 * @param url
	 * @param payload
	 * @return
	 */
	public static Object postObject(String url, String payload) throws Exception{
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(payload));
			responseEntity = HTTPCLIENT.execute(post);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = CommonUtils.getJsonFromString(response);
				if(!CommonUtils.isValidJSON(responseJSON)){
					return response;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error calling service", e);
			throw e;
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}

	public static JSONObject post(HttpPost post) {
		JSONObject responseJSON = new JSONObject();
		CloseableHttpResponse responseEntity = null;
		try {
			responseEntity = HTTPCLIENT.execute(post);
			HttpEntity entity = responseEntity.getEntity();
			int statusCode = responseEntity.getStatusLine().getStatusCode();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			System.out.println(response);
			responseJSON = new JSONObject(response);
			if (StringUtils.isNotBlank(response) || statusCode == 201) {
				responseJSON.put("statusCode", responseEntity.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error calling service", e);
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}
	
	public static JSONObject put(String url, String payload) {
		CloseableHttpResponse responseEntity = null;
		JSONObject responseJSON = null;
		try {
			HttpPut put = new HttpPut(url.replaceAll(" ", "%20"));
			put.addHeader("Content-Type", "application/json");
			put.setEntity(new StringEntity(payload));
			responseEntity = HTTPCLIENT.execute(put);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = new JSONObject(response);
				responseJSON.put("statusCode", responseEntity.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}
	
	public static void main(String[] args) {
		String url = "http://bigpayservices.56f8b68d.svc.dockerapp.io:83/BigPayServices/user/uday.koorella@bighalf.io/companies/big half/bills/576fb55d-0a0a-4003-aa39-a8e719fa9d81";
		String payload = "{\"id\":\"576fb55d-0a0a-4003-aa39-a8e719fa9d81\",\"name\":\"Bill5.pdf\",\"action\":\"pay\",\"payAmount\":222}";
		System.out.println(put(url, payload));
	}

	/**
	 * 
	 * @param get
	 * @return
	 */
	public static JSONObject get(HttpGet get) {
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			responseEntity = HTTPCLIENT.execute(get);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = new JSONObject(response);
				responseJSON.put("statusCode", responseEntity.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error calling service", e);
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}
	
	/**
	 * 
	 * @param get
	 * @return
	 */
	public static JSONObject get(String url) {
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpGet get = new HttpGet(url);
			responseEntity = HTTPCLIENT.execute(get);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = new JSONObject(response);
				responseJSON.put("statusCode", responseEntity.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error calling service", e);
		} finally {
			if (responseEntity != null) {
				try {
					responseEntity.close();
				} catch (IOException e) {
				}
			}
		}
		return responseJSON;
	}

}
