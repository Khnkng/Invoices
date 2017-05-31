package com.qount.invoice.controller;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.InvoiceControllerImpl;
import com.qount.invoice.model.Invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 30 May 2017
 *
 */
@Api(value = "Invoice Detail")
@Path("/invoices")
public class InvoiceDetailController {

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice for given id", value = "retieves invoice", responseContainer = "java.lang.String")
	public Invoice getProposal(@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.getInvoice(invoiceID);
	}
}
