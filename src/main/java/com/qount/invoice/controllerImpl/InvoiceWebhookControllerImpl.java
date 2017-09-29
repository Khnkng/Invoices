package com.qount.invoice.controllerImpl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.utils.Constants;
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
		LOGGER.debug("entered consumeWebHook : "+json);
		try {
			return Response.ok("[{\"under\":\"development\"}, "+json+"]").build();
		} catch (Exception e) {
			LOGGER.error("error consumeWebHook:",e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					PropertyManager.getProperty("invoice.webhook.error.msg"), Constants.EXPECTATION_FAILED));
		} finally {
			LOGGER.debug("exited consumeWebHook : "+json);
		}
	}
}
