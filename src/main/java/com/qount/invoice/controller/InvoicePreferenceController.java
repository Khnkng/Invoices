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

import com.qount.invoice.controllerImpl.InvoicePreferenceControllerImpl;
import com.qount.invoice.model.InvoicePreference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 25 Jan 2017
 *
 */
@Api(value = "Invoice Preference")
@Path("/users/{userId}/companies/{companyId}/invoice/preference")
public class InvoicePreferenceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create invoice preference", notes = "Used to add new invoice preference" + "<span class='bolder'>Sample Request:</span>"
			+ "<div class='sample_response'>"
			+ "json = {\"id\":\"uuid2\",\"templateType\":\"classic\",\"companyLogo\":\"dcoument_id_link\",\"displayLogo\":false,\"accentColor\":\"red\",\"defaultPaymentTerms\":\"net20\",\"defaultTitle\":\"t1\",\"defaultSubHeading\":\"sh\",\"defaultFooter\":\"f1\",\"standardMemo\":\"m1\",\"items\":\"Items\",\"units\":\"Units\",\"price\":\"Price\",\"amount\":\"Amount\",\"hideItemName\":false,\"hideItemDescription\":false,\"hideUnits\":false,\"hidePrice\":false,\"hideAmount\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public InvoicePreference createInvoicePreference(@PathParam("userID") String userID, @PathParam("companyId") @NotNull String companyId,
			@Valid InvoicePreference invoicePreference) {
		return InvoicePreferenceControllerImpl.createInvoicePreference(userID, companyId, invoicePreference);
	}

	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update invoice preference", notes = "Used to update invoice preference" + "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"id\":\"uuid2\",\"templateType\":\"classic\",\"companyLogo\":\"dcoument_id_link\",\"displayLogo\":false,\"accentColor\":\"red\",\"defaultPaymentTerms\":\"net20\",\"defaultTitle\":\"t1\",\"defaultSubHeading\":\"sh\",\"defaultFooter\":\"f1\",\"standardMemo\":\"m1\",\"items\":\"Items\",\"units\":\"Units\",\"price\":\"Price\",\"amount\":\"Amount\",\"hideItemName\":false,\"hideItemDescription\":false,\"hideUnits\":false,\"hidePrice\":false,\"hideAmount\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public InvoicePreference updateInvoicePreference(@PathParam("companyId") @NotNull String companyId,
			@PathParam("id") @NotNull String invoiceID, @Valid InvoicePreference invoicePreference) {
		return InvoicePreferenceControllerImpl.updateInvoicePreference(companyId, invoiceID, invoicePreference);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice preference of company", value = "retieves invoice preference", responseContainer = "java.lang.String")
	public InvoicePreference getInvoicePreference(@PathParam("companyId") @NotNull String companyId) {
		return InvoicePreferenceControllerImpl.getInvoicePreference(companyId);
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public InvoicePreference deleteInvoicePreferenceById(@PathParam("userID") String userID, @PathParam("id") @NotNull String invoicePreferenceId) {
		return InvoicePreferenceControllerImpl.deleteInvoicePreferenceById(invoicePreferenceId);
	}

}
