package com.qount.invoice.controller;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoiceDiscountsControllerImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 16 Feb 2018
 *
 */
@Api(value = "InvoiceDiscounts Controller")
@Path("/discounts")
public class InvoiceDiscountsDetailsController {


	@POST
	@Path("/{id}/discountedAmount")
	@ApiOperation(value = "get Invoice discount amount", notes = "get invoice amount after applying discount", responseContainer = "java.lang.String")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_discount_amount(@NotNull @PathParam("id") String id, String payload) {
		return InvoiceDiscountsControllerImpl.get_discount_amount(id, payload);
	}


}