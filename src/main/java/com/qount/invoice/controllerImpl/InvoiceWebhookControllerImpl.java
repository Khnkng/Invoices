package com.qount.invoice.controllerImpl;

import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
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

	public static Response consumeWebHook(String json) {
		LOGGER.debug("entered consumeWebHook : " + json);
		try {
			JSONObject obj = new JSONArray(json).optJSONObject(0);
			String type = obj.optString("type").trim();
			if(!type.equalsIgnoreCase(Constants.INVOICE)){
				LOGGER.fatal("webhooked invoked not for invoice from sendgrid with payload :"+json);
				return Response.ok().build();
			}
			String SERVER_INSTANCE_MODE = obj.optString("SERVER_INSTANCE_MODE").toUpperCase();
			if (SERVER_INSTANCE_MODE.equals("DEVELOPMENT")) {
				// forward to development
			} else if (SERVER_INSTANCE_MODE.equals("PRODUCTION")) {
				// logic should be here
			}
			updateInvoiceState(obj);
			return Response.ok("[{\"under\":\"development\"}, " + json + "]").build();
		} catch (Exception e) {
			LOGGER.error("error consumeWebHook:", e);
			throw new WebApplicationException(
					ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, PropertyManager.getProperty("invoice.webhook.error.msg"), Constants.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited consumeWebHook : " + json);
		}
	}

	private static void updateInvoiceState(JSONObject obj) throws Exception{
		LOGGER.debug("entered updateInvoiceState obj: " + obj);
		Connection connection = null;
		try {
			String invoiceId = obj.optString("id").trim();
			Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoiceId);
			String invoiceEmailState = getInvoiceMailState(obj.optString("event"), dbInvoice.getEmail_state());
			if(!invoiceEmailState.equalsIgnoreCase(dbInvoice.getEmail_state())){
				dbInvoice.setEmail_state(invoiceEmailState);
				connection = DatabaseUtilities.getReadWriteConnection();
				MySQLManager.getInvoiceDAOInstance().update(connection, dbInvoice);
			}
		} catch (Exception e) {
			LOGGER.error("error updateInvoiceState obj: " + obj, e);
			throw e;
		} finally {
			LOGGER.debug("exited updateInvoiceState obj: " + obj);
			connection.close();
		}
	}

	public static void main(String[] args) {
		System.out.println(getInvoiceMailState("delivered", null));
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
