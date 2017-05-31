package com.qount.invoice.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoiceControllerImpl;
import com.qount.invoice.model.Invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
@Api(value = "Invoice")
@Path("/users/{userID}/companies/{companyID}/invoice")
public class InvoiceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"amount\":50000,\"currency\":\"INR\",\"description\":\"testing 1\",\"objectives\":\"testing 1\",\"customer_id\":\"1b860c54-e0aa-467c-800c-d29911b856f9\",\"invoice_date\":\"2017-01-01 11:05:47\",\"notes\":\"notes\",\"po_number\":\"po1234\",\"invoiceLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"objectives\":\"invoiecLine obj\",\"amount\":3000,\"quantity\":10,\"price\":300,\"notes\":\"InvoiceLine notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"InvoiceLine2 desc\",\"objectives\":\"InvoiceLine2 obj\",\"amount\":2000,\"quantity\":10,\"price\":300,\"notes\":\"InvoiceLine2 notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]}],\"invoiceTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"12.8\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice createInvoice(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.createInvoice(userID,companyID, invoice);
	}

	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"amount\":50000,\"currency\":\"INR\",\"description\":\"testing 1\",\"objectives\":\"testing 1\",\"customer_id\":\"1b860c54-e0aa-467c-800c-d29911b856f9\",\"invoice_date\":\"2017-01-01 11:05:47\",\"notes\":\"notes\",\"po_number\":\"po1234\",\"invoiceLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"objectives\":\"invoiecLine obj\",\"amount\":3000,\"quantity\":10,\"price\":300,\"notes\":\"InvoiceLine notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"InvoiceLine2 desc\",\"objectives\":\"InvoiceLine2 obj\",\"amount\":2000,\"quantity\":10,\"price\":300,\"notes\":\"InvoiceLine2 notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]}],\"invoiceTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"12.8\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice updateInvoices(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID,
			@Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID, companyID, invoiceID, invoice);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public Response getInvoices(@PathParam("userID") @NotNull String userID,@PathParam("companyID") @NotNull String companyID) {
		return InvoiceControllerImpl.getInvoices(userID,companyID);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Invoice getProposal(@PathParam("userID") @NotNull String userID,@PathParam("companyID") @NotNull String companyID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.getInvoice(invoiceID);
	}

	@DELETE
	@Path("/{invoiceID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Invoice deleteInvoiceById(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.deleteInvoiceById(userID, invoiceID);
	}

}
