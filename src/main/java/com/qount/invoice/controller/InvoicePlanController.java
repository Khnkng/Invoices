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
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.InvoicePlanControllerImpl;
import com.qount.invoice.model.InvoicePlan;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 28 June 2017
 *
 */
@Api(value = "InvoicePlan")
@Path("/users/{userID}/companies/{companyID}/invoicePlan")
public class InvoicePlanController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"frequency\":\"daily\",\"name\":\"test_daily_092\",\"amount\":\"100\",\"ends_after\":\"2\"}"
			+ "</div>", responseContainer = "java.lang.String")
	public InvoicePlan createInvoicePlan(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID, @Valid InvoicePlan invoicePlan) {
		return InvoicePlanControllerImpl.createInvoicePlan(userID, companyID, invoicePlan);
	}

	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"frequency\":\"daily\",\"name\":\"test_daily_092\",\"amount\":\"100\",\"ends_after\":\"2\"}"
			+ "</div>", responseContainer = "java.lang.String")
	public InvoicePlan updateInvoices(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID, @PathParam("invoiceID") @NotNull String invoiceID,
			@Valid InvoicePlan invoicePlan) {
		return InvoicePlanControllerImpl.updateInvoicePlan(userID, companyID, invoiceID, invoicePlan);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<InvoicePlan> getInvoicePlans(@PathParam("userID") @NotNull String userID,@PathParam("companyID") @NotNull String companyID) {
		return InvoicePlanControllerImpl.getInvoicePlans(userID,companyID);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public InvoicePlan getProposal(@PathParam("userID") @NotNull String userID,@PathParam("companyID") @NotNull String companyID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoicePlanControllerImpl.getInvoicePlan(invoiceID);
	}

	@DELETE
	@Path("/{invoiceID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public InvoicePlan deleteInvoiceById(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoicePlanControllerImpl.deleteInvoicePlan(invoiceID);
	}

}
