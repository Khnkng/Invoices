package com.qount.invoice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.InvoiceControllerImpl;
import com.qount.invoice.model.Invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoice")
@Path("/user/{userID}/company/{companyID}/invoices")
public class InvoiceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"companyID\":\"1212\",\"customer_name\":\"Apurva\",\"total_amount\":\"1000\",\"currency\":\"$\",\"bank_account\":true,\"credit_card\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice createInvoice(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@Valid Invoice invoice) {
		return InvoiceControllerImpl.createInvoice(userID, companyID, invoice);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Invoice> getInvoices(@PathParam("userID") String userID, @PathParam("companyID") String companyID) {
		return InvoiceControllerImpl.getInvoices(userID, companyID);
	}

	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"proposalID\":\"12\",\"companyID\":\"1212\",\"customer_name\":\"Apurva\",\"total_amount\":\"1000\",\"currency\":\"$\",\"bank_account\":true,\"credit_card\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice updateInvoices(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@PathParam("invoiceID") String invoiceID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID, companyID,invoiceID, invoice);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Invoice getProposal(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@PathParam("invoiceID") String invoiceID) {
		return InvoiceControllerImpl.getInvoice(userID, companyID, invoiceID);
	}
}
