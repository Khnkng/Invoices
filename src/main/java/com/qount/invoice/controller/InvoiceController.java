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

@Api(value = "Invoice")
@Path("/user/{userID}/invoice")
public class InvoiceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"customer_name\":\"apurva\",\"invoice_date\":\"2016/11/3\",\"due_date\":\"2016/11/3\",\"invoice_amount\":100000,\"invoice_status\":\"paid\",\"bank_account\":true,\"credit_card\":false,\"terms\":\"terms\",\"currencyID\":101,\"recurring\":false,\"start_date\":\"2016/12/5\",\"end_date\":\"2016/12/31\",\"recurring_frequency\":\"daily\",\"number_of_invoices\":30,\"invoiceLines\":[{\"line_number\":1,\"description\":\"desc1\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100},{\"line_number\":2,\"description\":\"desc2\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Response createInvoice(@PathParam("userID") String userID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.createInvoice(userID,invoice);
	}
	
	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"customer_name\":\"apurva123\",\"invoice_date\":\"2016/11/3\",\"due_date\":\"2016/12/15\",\"invoice_amount\":2000,\"invoice_status\":\"due\",\"bank_account\":true,\"credit_card\":false,\"terms\":\"terms\",\"currencyID\":201,\"recurring\":true,\"start_date\":\"2016/12/3\",\"end_date\":\"2016/12/30\",\"recurring_frequency\":\"daily\",\"number_of_invoices\":\"10\",\"invoiceLines\":[{\"lineID\":\"05f13774-4cd6-41c1-affa-ab363c0a0a6e\",\"line_number\":1,\"description\":\"updated\",\"quantity\":20,\"unit_cost\":20,\"total_amount\":200},{\"line_number\":5,\"description\":\"new line added\",\"quantity\":20,\"unit_cost\":20,\"total_amount\":200}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Response updateInvoices(@PathParam("userID") String userID,
			@PathParam("invoiceID") @NotNull String invoiceID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID,invoiceID, invoice);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public Response getInvoices(@PathParam("userID") @NotNull String userID) {
		return InvoiceControllerImpl.getInvoices(userID);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Response getProposal(@PathParam("userID") @NotNull String userID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.getInvoice(userID,invoiceID);
	}

	@DELETE
	@Path("/{invoiceID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Response deleteInvoiceById(@PathParam("userID") String userID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.deleteInvoiceById(userID,invoiceID);
	}
	
	@DELETE
	@Path("/{invoiceID}/lineID/{lineID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Response deleteInvoiceLine(@PathParam("userID") String userID,
			@PathParam("invoiceID") @NotNull String invoiceID,
			@PathParam("lineID") @NotNull String lineID) {
		return InvoiceControllerImpl.deleteInvoiceLine(userID,invoiceID, lineID);
	}
}
