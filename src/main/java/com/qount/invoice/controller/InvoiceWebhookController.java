package com.qount.invoice.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoiceWebhookControllerImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 29 Sep 2017
 *
 */
@Api(value = "Invoice")
@Path("/webhook")
public class InvoiceWebhookController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Invoice WebHook", notes = "Used for invoice webhooks", responseContainer = "java.lang.String")
	public Response consumeWebHook(@QueryParam("devUpdate") boolean devUpdate,String json) {
		return InvoiceWebhookControllerImpl.consumeWebHook(json,devUpdate);
	}
}
