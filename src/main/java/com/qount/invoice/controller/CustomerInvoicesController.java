package com.qount.invoice.controller;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.service.PaymentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoice")
@Path("/users/{userID}/companies/{companyID}")
public class CustomerInvoicesController {

	private static final Logger LOGGER = Logger.getLogger(CustomerInvoicesController.class);

	
	@Path("/customers/{customerID}/invoices")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoices of a customer", value = "retieves invoices", responseContainer = "java.lang.String")
	public Response getunmappedInvoicesbyClientID(@PathParam("userID") @NotNull String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("customerID") @NotNull String customerID,  @QueryParam("payment") boolean payment, @QueryParam("paymentId") String paymentID) {
		LOGGER.debug("entered CustomerInvoicesController.getunmappedInvoicesbyClientID" + customerID);
		List<PaymentLine> paymentLines = null;
		if (payment) {
			paymentLines = PaymentService.getInstance().getLinesByCustomerIdOrPaymentId(companyID,customerID, paymentID);
		}
		return Response.status(200).entity(paymentLines.toString()).build();
	}
}
