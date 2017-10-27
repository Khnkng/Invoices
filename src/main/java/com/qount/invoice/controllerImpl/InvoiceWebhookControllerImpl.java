package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.clients.httpClient.HTTPClient;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 29 Sep 2017
 *
 */
public class InvoiceWebhookControllerImpl {

	private static final Logger LOGGER = Logger.getLogger(InvoiceWebhookControllerImpl.class);

	public static Response consumeWebHook(String json,boolean devUpdate) {
		LOGGER.debug("entered consumeWebHook : " + json +" devUpdate:"+devUpdate);
		try {
			JSONArray arr = new JSONArray(json);
			for(int i=0;i<arr.length();i++){
				JSONObject obj = arr.optJSONObject(i);
				String type = obj.optString("type").trim();
				if(!type.equalsIgnoreCase(Constants.INVOICE)){
					LOGGER.fatal("webhooked invoked not for invoice from sendgrid with payload :"+json);
					return Response.ok().build();
				}
				String invoiceId = obj.optString("id").trim();
				if(StringUtils.isBlank(invoiceId)){
					LOGGER.fatal("webhooked invoked withouth invoiceId json:"+json);
					return Response.ok().build();
				}
				if(devUpdate){
					updateInvoiceState(obj);
				}else{
					String SERVER_INSTANCE_MODE = obj.optString("SERVER_INSTANCE_MODE").toUpperCase();
					if (devUpdate && SERVER_INSTANCE_MODE.equals("DEVELOPMENT")) {
						String url = PropertyManager.getProperty("invoice.dev.webhook.url")+"?devUpdate=true";
						LOGGER.debug("invoking url:"+url + " json:"+json);
						HTTPClient.post(url,json);
					} else if (SERVER_INSTANCE_MODE.equals("PRODUCTION")) {
						updateInvoiceState(obj);
					}
				}
			}
			
			return Response.ok(json).build();
		} catch (Exception e) {
			LOGGER.error("error consumeWebHook:", e);
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, PropertyManager.getProperty("invoice.webhook.error.msg"), Constants.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited consumeWebHook : " + json+" devUpdate:"+devUpdate);
		}
	}
	
	private static void updateInvoiceState(JSONObject obj) throws Exception{
		LOGGER.debug("entered updateInvoiceState obj: " + obj);
		Connection connection = null;
		try {
			String invoiceId = obj.optString("id").trim();
			Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoiceId);
			String inputEmailState = obj.optString("event");
			String email = obj.optString("email");
			InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(dbInvoice, UUID.randomUUID().toString(), dbInvoice.getUser_id(), dbInvoice.getCompany_id(),inputEmailState,email);
			MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
			String invoiceEmailState = getInvoiceMailState(inputEmailState, dbInvoice.getEmail_state());
			if(StringUtils.isNotEmpty(invoiceEmailState) && !invoiceEmailState.equalsIgnoreCase(dbInvoice.getEmail_state())){
				dbInvoice.setEmail_state(invoiceEmailState);
				connection = DatabaseUtilities.getReadWriteConnection();
				MySQLManager.getInvoiceDAOInstance().updateEmailState(connection, dbInvoice);
			}
		} catch (Exception e) {
			LOGGER.error("error updateInvoiceState obj: " + obj, e);
			throw e;
		} finally {
			LOGGER.debug("exited updateInvoiceState obj: " + obj);
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static void main(String[] args) {

		JSONObject obj = new JSONObject();
		obj.put("server_INSTANCE_MODE", "asdf");
		obj.put("SERVER_INSTANCE_MODE", "aaaa");
		System.out.println(obj.optString("SERVER_INSTANCE_MODE"));
		
	}

	private static String getInvoiceMailState(String receivedState, String storedState) {
		LOGGER.debug("entered getInvoiceMailState receivedState: " + receivedState + " storedState:" + storedState);
		try {
			if (StringUtils.isBlank(receivedState)) {
				return null;
			} 
			if(StringUtils.isBlank(storedState)){
				return receivedState;
			}else{
				if (Constants.INVOICE_MAIL_STATE_MAP.get(receivedState.trim().toLowerCase()) > Constants.INVOICE_MAIL_STATE_MAP.get(storedState.trim().toLowerCase())) {
					return receivedState;
				} else {
					return storedState;
				}
			}
		} catch (Exception e) {
			LOGGER.error("error getInvoiceMailState receivedState: " + receivedState + " storedState:" + storedState, e);
			throw e;
		} finally {
			LOGGER.debug("exited getInvoiceMailState receivedState: " + receivedState + " storedState:" + storedState);
		}
	}
}
