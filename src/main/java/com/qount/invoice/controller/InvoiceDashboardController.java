package com.qount.invoice.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.qount.invoice.controllerImpl.InvoiceDashboardControllerImpl;
import com.qount.invoice.model.Invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 12 Sept 2016
 *
 */

@Api(value = "invoice Dashboard Services")
@Path("user/{userID}/companies/{companyID}/invoicedashboard")
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceDashboardController {

	private static final Logger LOGGER = Logger.getLogger(InvoiceDashboardController.class);

	/**
	 * 
	 * @param userID
	 * @param companyID
	 * @return
	 */
	@Path("details")
	@GET
	@ApiOperation(value = "Used to  Get invoice Dashboard List", notes = "Used to Get invoice Dashboard List Based on CompanyID"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"query param\":\"receivables, past_due,opened,sent,recvdin30days\"}"
			+ "</div>", responseContainer = "java.lang.String")
	public List<Invoice> getInvoiceDashboardList(@PathParam("userID") String userID,
			@PathParam("companyID") String companyID, @QueryParam("filter") String filter) {
		LOGGER.debug("Entered into InvoiceDashboardController.getInvoiceDashboardList with  userID [ " + userID
				+ " ] and companyID [ " + companyID + " ]");
		return InvoiceDashboardControllerImpl.getInvoiceDashboardList(userID, companyID, filter);
	}

}
