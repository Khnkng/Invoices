package com.qount.invoice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.InvoiceLineControllerImpl;
import com.qount.invoice.model.InvoiceLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
@Api(value = "invoice Line")
@Path("/users/{userID}/companies/{companyId}/invoice/{invoiceID}/line")
public class InvoiceLineController {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Invoice line", notes = "Used to add new invoice line"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = [{\"description\":\"line 1\",\"objectives\":\"line 1\",\"amount\":4000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":4,\"price\":4,\"notes\":\"line 1\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":12.8},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":13.5}]},{\"description\":\"line 2\",\"objectives\":\"line 2\",\"amount\":4000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":4,\"price\":4,\"notes\":\"line 2\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":78.3}]}]"
			+ "</div>", responseContainer = "java.lang.String")
	public List<InvoiceLine> createInvoice(@PathParam("userID") String userID, @PathParam("invoiceID") String invoiceID,
			@Valid List<InvoiceLine> invoiceLines) {
		return InvoiceLineControllerImpl.createInvoiceLine(userID, invoiceID, invoiceLines);
	}

	@Path("/{invoiceLineID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update invoice", notes = "Used to update invoice"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"description\":\"updated line 1\",\"objectives\":\"updated line 1\",\"amount\":5000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-02-05 11:14:35\",\"quantity\":8,\"price\":10,\"notes\":\"updated line 1\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":1.111},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":2.222}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public InvoiceLine update(@PathParam("userID") String userID, @PathParam("invoiceID") String invoiceID,
			@PathParam("invoiceLineID") String invoiceLineID, @Valid InvoiceLine invoiceLine) {
		return InvoiceLineControllerImpl.updateInvoiceLine(userID, invoiceID, invoiceLineID, invoiceLine);
	}

	@DELETE
	@Path("/{invoiceLineID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete invoice line", notes = "Used to delete invoice line<br>", responseContainer = "java.lang.String")
	public InvoiceLine deleteInvoiceById(@PathParam("userID") String userID,
			@PathParam("invoiceLineID") @NotNull String invoiceLineID) {
		return InvoiceLineControllerImpl.deleteInvoiceLineById(userID, invoiceLineID);
	}
}
