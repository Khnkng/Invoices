package com.qount.invoice.controller;

import java.util.List;

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
import javax.ws.rs.QueryParam;
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
	@ApiOperation(value = "Create Invoice", notes = "Used to add new invoice" + "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"customer_id\":\"4ab0c48c-5006-4dbe-b0ce-7f0c24ef9bc8\",\"recepientsMails\":[\"mateen.khan@qount.io\"],\"number\":\"22222222222\",\"invoice_date\":\"01/25/2017\",\"currency\":\"INR\",\"invoiceLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"amount\":3000,\"quantity\":10,\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}],\"price\":300,\"type\":\"task\"},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"amount\":3000,\"quantity\":10,\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}],\"price\":300,\"type\":\"item\"}],\"notes\":\"notes\",\"term\":\"po1234\",\"payment_options\":\"how can your clients pay you ?\",\"plan_id\":\"b4c25144-bdfb-441a-b92f-392e230c4193\"}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice createInvoice(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.createInvoice(userID, companyID, invoice);
	}

	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update invoice", notes = "Used to update invoice" + "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"customer_id\":\"4ab0c48c-5006-4dbe-b0ce-7f0c24ef9bc8\",\"recepientsMails\":[\"mateen.khan@qount.io\"],\"number\":\"22222222222\",\"invoice_date\":\"01/25/2017\",\"currency\":\"INR\",\"invoiceLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"amount\":3000,\"quantity\":10,\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}],\"price\":300,\"type\":\"task\"},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"invoiceLine desc\",\"amount\":3000,\"quantity\":10,\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}],\"price\":300,\"type\":\"item\"}],\"notes\":\"notes\",\"term\":\"po1234\",\"payment_options\":\"how can your clients pay you ?\",\"plan_id\":\"b4c25144-bdfb-441a-b92f-392e230c4193\"}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice updateInvoices(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID,
			@Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID, companyID, invoiceID, invoice);
	}

	@Path("/{invoiceID}/state")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update invoice", notes = "Used to update invoice state" + "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"state\":\"sent\"}" + "</div>", responseContainer = "java.lang.String")
	public Invoice updateInvoiceState(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID,
			@Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID, companyID, invoiceID, invoice);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoices of company", value = "retieves invoices", responseContainer = "java.lang.String")
	public Response getInvoices(@PathParam("userID") @NotNull String userID, @PathParam("companyID") @NotNull String companyID, @QueryParam("state") String state) {
		return InvoiceControllerImpl.getInvoices(userID, companyID, state);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice of company", value = "retieves invoice", responseContainer = "java.lang.String")
	public Invoice getProposal(@PathParam("userID") @NotNull String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.getInvoice(invoiceID);
	}

	@DELETE
	@Path("/{invoiceID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete invoice", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Invoice deleteInvoiceById(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.deleteInvoiceById(userID, companyID, invoiceID);
	}

	@POST
	@Path("/delete")
	@ApiOperation(value = "Delete invoices", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public boolean deleteInvoicesById(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, List<String> ids) {
		return InvoiceControllerImpl.deleteInvoicesById(userID, companyID, ids);
	}

	@PUT
	@Path("/sent")
	@ApiOperation(value = "Delete invoices", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public boolean udpateInvoicesByState(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, List<String> ids) {
		return InvoiceControllerImpl.updateInvoicesAsSent(userID, companyID, ids);
	}

}
