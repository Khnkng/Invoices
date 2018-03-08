package com.qount.invoice.controller;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.service.PaymentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoices Mapped To Payments")
@Path("/users/{userID}/companies/{companyID}")

public class InvoiceMappedToPaymentsController {
	
	@Path("/payment/{paymentID}/invoices")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Returns list of invoice in a payment", notes = "Used to retreive list  of invoice in a payments", responseContainer = "java.lang.String")
	public List<Invoice> getIvoicesByPaymentID(@PathParam("userID") String userID, @PathParam("companyID") String companyID, @PathParam("paymentID") String paymentID) {
		return PaymentService.getInstance().getIvoicesByPaymentID(companyID, paymentID);
	}
}
