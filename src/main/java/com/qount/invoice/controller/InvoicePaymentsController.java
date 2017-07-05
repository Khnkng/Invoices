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

import com.qount.invoice.model.Payment;
import com.qount.invoice.service.PaymentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoice")
@Path("/users/{userID}/companies/{companyID}/invoice/payment")
public class InvoicePaymentsController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create a payment", notes = "Used to create new payment", responseContainer = "java.lang.String")
	public Payment createPayment(@PathParam("userID") String userID, @PathParam("companyID") String companyID, @Valid Payment payment) {
		return PaymentService.getInstance().createOrUpdatePayment(payment, companyID);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create a payment", notes = "Used to create new payment", responseContainer = "java.lang.String")
	public Payment updatePayment(@PathParam("userID") String userID, @PathParam("companyID") String companyID, @Valid Payment payment) {
		return PaymentService.getInstance().createOrUpdatePayment(payment, companyID);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Returns list of payments", notes = "Used to retreive list of payments against company", responseContainer = "java.lang.String")
	public List<Payment> list(@PathParam("userID") String userID, @PathParam("companyID") String companyID) {
		return PaymentService.getInstance().getList(companyID);
	}
}
 