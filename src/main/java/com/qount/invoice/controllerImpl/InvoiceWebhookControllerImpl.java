package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.UUID;

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
					if (!devUpdate && SERVER_INSTANCE_MODE.equals("DEVELOPMENT")) {
						String url = PropertyManager.getProperty("invoice.dev.webhook.url")+"?devUpdate=true";
						LOGGER.debug("invoking url:"+url + " json:"+json);
						HTTPClient.post(url,json);
					} else if (SERVER_INSTANCE_MODE.equals("PRODUCTION")) {
						updateInvoiceState(obj);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error consumeWebHook:", e);
		} finally {
			LOGGER.debug("exited consumeWebHook : " + json+" devUpdate:"+devUpdate);
		}
		return Response.ok(json).build();
	}
	
	private static void updateInvoiceState(JSONObject obj) throws Exception{
		LOGGER.debug("entered updateInvoiceState obj: " + obj);
		Connection connection = null;
		try {
			String invoiceId = obj.optString("id").trim();
			Invoice dbInvoice = MySQLManager.getInvoiceDAOInstance().get(invoiceId);
			String inputEmailState = obj.optString("event");
			String email = obj.optString("email");
			String webhook_event_id = obj.optString("sg_event_id");
			InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(dbInvoice, UUID.randomUUID().toString(), dbInvoice.getUser_id(), dbInvoice.getCompany_id(),inputEmailState,email);
			invoice_history.setWebhook_event_id(webhook_event_id);
			connection = DatabaseUtilities.getReadWriteConnection();
			MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
			String invoiceEmailState = getInvoiceMailState(inputEmailState, dbInvoice.getEmail_state());
			if(StringUtils.isNotEmpty(invoiceEmailState) && !invoiceEmailState.equalsIgnoreCase(dbInvoice.getEmail_state())){
				dbInvoice.setEmail_state(invoiceEmailState);
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
		consumeWebHook("[{\"ip\":\"64.233.173.17\",\"sg_event_id\":\"mRe3fWDVTn63KQ1ituDxew\",\"sg_message_id\":\"ZQ5YBfo3RO-ch1arVz6_7Q.filter0025p3mdw1-18878-59F3DC31-6.0\",\"useragent\":\"Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko Firefox/11.0 (via ggpht.com GoogleImageProxy)\",\"event\":\"open\",\"email\":\"mateen.khan@qount.io\",\"timestamp\":1509154036,\"SERVER_INSTANCE_MODE\":\"DEVELOPMENT\",\"type\":\"invoice\",\"id\":\"3f66430d-aed7-4b60-923e-fb208e6cbea4\"}]", false);
		
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
