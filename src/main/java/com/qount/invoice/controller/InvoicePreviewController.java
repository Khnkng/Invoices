package com.qount.invoice.controller;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoicePreviewControllerImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0 Jul 06 2017
 *
 */
@Api(value = "Invoice pdf")
@Path("/invoice/{invoiceID}/preview")
public class InvoicePreviewController {



	@Path("/pdf")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice of company", value = "retieves invoice", responseContainer = "java.lang.String")
	public Response getInvoicePreview(@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoicePreviewControllerImpl.getInvoicePdfPrevew(invoiceID);
	}


}
