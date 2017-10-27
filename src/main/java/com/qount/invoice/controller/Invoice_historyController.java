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

import com.qount.invoice.controllerImpl.Invoice_historyControllerImpl;
import com.qount.invoice.model.InvoiceHistory;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoice_history Controller")
@Path("/users/{userId}/companies/{companyId}/invoice_history")
public class Invoice_historyController {

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Returns Invoice_history", notes = "Used to get of Invoice_history by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public InvoiceHistory getInvoice_history(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id) {
		return Invoice_historyControllerImpl.getInvoice_history(userId, companyId, id);
	}

	@GET
	@ApiOperation(value = "Returns list of Invoice_historys", notes = "Used to get list of Invoice_historys", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public List<InvoiceHistory> getInvoice_historys(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId) {
		return Invoice_historyControllerImpl.getInvoice_historys(userId, companyId);
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete Invoice_history", notes = "Deletes Invoice_history by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInvoice_history(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id) {
		return Invoice_historyControllerImpl.deleteInvoice_history(userId, companyId, id);
	}

	@POST
	@Path("/delete")
	@ApiOperation(value = "Delete Invoice_history", notes = "Deletes Invoice_history by ids", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInvoice_historys(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId, List<String> ids) {
		return Invoice_historyControllerImpl.deleteInvoice_historys(userId, companyId, ids);
	}

	@POST
	@ApiOperation(value = "Create Invoice_history", notes = "Used to create Invoice_history", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createInvoice_history(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId, InvoiceHistory invoice_history) {
		return Invoice_historyControllerImpl.createInvoice_history(userId, companyId, invoice_history);
	}

	@PUT
	@Path("/{id}")
	@ApiOperation(value = "Update Invoice_history", notes = "Update Invoice_history by id", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInvoice_history(@NotNull @PathParam("userId") String userId, @NotNull @PathParam("companyId") String companyId, @NotNull @PathParam("id") String id,
			InvoiceHistory Invoice_history) {
		return Invoice_historyControllerImpl.updateInvoice_history(userId, companyId, id, Invoice_history);
	}

}