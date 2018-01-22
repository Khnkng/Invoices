package com.qount.invoice.controller;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoiceDiscountsControllerImpl;
import com.qount.invoice.model.InvoiceDiscounts;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 19 Jan 2018
 *
 */
@Api(value = "InvoiceDiscounts Controller")
@Path("/users/{userId}/companies/{companyId}/discounts")
public class InvoiceDiscountsController {

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Returns Invoice_discounts", notes = "Used to get of Invoice_discounts by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public InvoiceDiscounts getInvoice_discounts(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id) {
		return InvoiceDiscountsControllerImpl.getInvoice_discounts(userId, companyId, id);
	}

	@GET
	@ApiOperation(value = "Returns list of Invoice_discountss", notes = "Used to get list of Invoice_discountss", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InvoiceDiscounts> getInvoice_discountss(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId) {
		return InvoiceDiscountsControllerImpl.getInvoice_discountss(userId, companyId);
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete Invoice_discounts", notes = "Deletes Invoice_discounts by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInvoice_discounts(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id) {
		return InvoiceDiscountsControllerImpl.deleteInvoice_discounts(userId, companyId, id);
	}

	@POST
	@Path("/delete")
	@ApiOperation(value = "Delete Invoice_discounts", notes = "Deletes Invoice_discounts by ids", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInvoice_discountss(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId) {
		return InvoiceDiscountsControllerImpl.deleteAllDiscounts(userId, companyId);
	}

	@POST
	@ApiOperation(value = "Create Invoice_discounts", notes = "Used to create Invoice_discounts", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInvoice_discounts(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId, InvoiceDiscounts invoiceDiscounts) {
		return InvoiceDiscountsControllerImpl.createInvoice_discounts(userId, companyId, invoiceDiscounts);
	}

	@PUT
	@Path("/{id}")
	@ApiOperation(value = "Update Invoice_discounts", notes = "Update Invoice_discounts by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInvoice_discounts(@NotNull @PathParam("userId") String userId,
			@NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id,
			InvoiceDiscounts invoiceDiscounts) {
		return InvoiceDiscountsControllerImpl.updateInvoice_discounts(userId, companyId, id, invoiceDiscounts);
	}

}