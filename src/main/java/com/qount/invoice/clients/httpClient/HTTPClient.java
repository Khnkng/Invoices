package com.qount.invoice.clients.httpClient;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
//			post.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
		LOGGER.debug("entered postObject url:"+url+" paload:"+payload);
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json");
//			post.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
			LOGGER.error("Error calling service  postObject url:"+url+" paload:"+payload, e);
			throw e;
		} finally {
			LOGGER.debug("exited postObject url:"+url+" paload:"+payload);
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
	public static Object postUrlAndGetStatus(String url, String payload) throws Exception{
		LOGGER.debug("entered postUrlAndGetStatus url:"+url+" payload:"+payload);
		JSONObject responseJSON = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(payload));
			responseEntity = HTTPCLIENT.execute(post);
			HttpEntity entity = responseEntity.getEntity();
			int status = responseEntity.getStatusLine().getStatusCode();
			if(status==202){
				responseJSON = new JSONObject();
				responseJSON.put("status", status);
				return responseJSON; 
			}
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			String response = writer.toString();
			if (StringUtils.isNotBlank(response)) {
				responseJSON = CommonUtils.getJsonFromString(response);
				responseJSON.put("status", status);
				if(!CommonUtils.isValidJSON(responseJSON)){
					return response;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error postUrlAndGetStatus url:"+url+" payload:"+payload, e);
			throw e;
		} finally {
			LOGGER.debug("exited postUrlAndGetStatus url:"+url+" payload:"+payload);
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
			post.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
//			put.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
//			get.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
//			get.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
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
	public static String delete(String url) {
		String response = null;
		CloseableHttpResponse responseEntity = null;
		try {
			HttpDelete delete = new HttpDelete(url);
//			delete.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJraWQiOiI5MGUwZDU4Ny1kZjE2LTQ1YzgtOTExZC1jYjFlNDhmMDA4ZTMiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJxb3VudC5pbyIsImV4cCI6MTUyNjk5MTcwMiwianRpIjoiNDdlYzgxNTAtMDJlNy00MDBjLWE3YTctNDNkNmJmMTQ5OTg3IiwiaWF0IjoxNDk1NDU1NzAzLCJuYmYiOjE0OTU0NTU1ODMsInN1YiI6IntcImlkXCI6XCJyYXZpa2lyYW43MzhAZ21haWwuY29tXCIsXCJwaG9uZU51bWJlclwiOm51bGwsXCJwcm9maWxlUGljXCI6bnVsbCxcImNyZWF0ZWREYXRlXCI6bnVsbCxcIm1vZGlmaWVkRGF0ZVwiOm51bGwsXCJkZWZhdWx0Q29tcGFueVwiOm51bGwsXCJ0ZW1wUGFzc3dvcmRcIjpmYWxzZSxcInZlbmRvcklEXCI6bnVsbCxcImFjdGl2ZVwiOmZhbHNlLFwicGFzc3dvcmRcIjpudWxsLFwiYWRtaW5cIjpmYWxzZSxcImZpcnN0X25hbWVcIjpcIlJhdmlraXJhblwiLFwibGFzdF9uYW1lXCI6XCJEZXZpbmVuaVwifSJ9.xAWpRbt8ARab_YqgB70Y4KEk2K1NQ9HeYiffMeEs8xo");
			responseEntity = HTTPCLIENT.execute(delete);
			HttpEntity entity = responseEntity.getEntity();
			StringWriter writer = new StringWriter();
			IOUtils.copy(entity.getContent(), writer);
			EntityUtils.consume(entity);
			response = writer.toString();
			return response;
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
		return response;
	}

}
