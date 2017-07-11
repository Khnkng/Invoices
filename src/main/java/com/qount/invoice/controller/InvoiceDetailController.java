package com.qount.invoice.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.qount.invoice.controllerImpl.InvoiceControllerImpl;
import com.qount.invoice.controllerImpl.InvoiceDetailControllerImpl;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 30 May 2017
 *
 */
@Api(value = "Invoice Detail")
@Path("/invoices")
public class InvoiceDetailController {

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice for given id", value = "retieves invoice", responseContainer = "java.lang.String")
	public Invoice openInvoice(@PathParam("invoiceID") @NotNull String invoiceID,@QueryParam("action") String action) {
		return InvoiceControllerImpl.getInvoice(invoiceID);
	}
	
	@Path("/{invoiceID}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve invoice for given id", value = "retieves invoice", responseContainer = "java.lang.String")
	public Invoice payInvoice(@PathParam("invoiceID") @NotNull String invoiceID,@NotNull @QueryParam("action") String action,@Valid Invoice inputInvoice) {
		if(StringUtils.isEmpty(action)){
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
		}else if(action.equals("pay")){
			Invoice invoice = InvoiceControllerImpl.getInvoice(invoiceID);
			if(InvoiceDetailControllerImpl.makeInvoicePayment(invoice, invoiceID,inputInvoice)){;
				Invoice updateInvoice = InvoiceControllerImpl.updateInvoice(invoice.getUser_id(), invoice.getCompany_id(), invoiceID, invoice);
				if(updateInvoice!=null){
					return updateInvoice;
				}
			}
		}
		return null;
	}
}
