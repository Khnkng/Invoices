package com.qount.invoice.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.controllerImpl.InvoiceReportControllerImpl;

/**
 * @author Mateen
 * @version 1.0 Feb 28 2017
 */
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Invoice")
@Path("/companies/{companyID}/customers/{customerID}/invoices/{invoiceID}/report")
public class InvoiceReportController {

	@POST
	@Path("/pdf")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Returns Report PDF File", notes = "Genrates & Sends a PDF file", responseContainer = "java.lang.String")
	public Response createPdf(@PathParam("invoiceID") String invoiceID, @PathParam("companyID") String companyID, @PathParam("customerID") String customerID, String json)
			throws Exception {
		return InvoiceReportControllerImpl.createPdf(companyID, customerID, invoiceID, json);
	}

	@GET
	@Path("/pdf")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Returns Report PDF File", notes = "Genrates & Sends a PDF file", responseContainer = "java.lang.String")
	public Response createPdfGet(@PathParam("invoiceID") String invoiceID, @PathParam("companyID") String companyID, @PathParam("customerID") String customerID) throws Exception {
		return InvoiceReportControllerImpl.createPdf(companyID, customerID, invoiceID, null);
	}
}
