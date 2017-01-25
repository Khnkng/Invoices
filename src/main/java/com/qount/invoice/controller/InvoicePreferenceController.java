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
@Path("/user/{userId}/company/{companyId}/invoice/preference")
public class InvoicePreferenceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create invoice preference", notes = "Used to add new invoice preference" + "<span class='bolder'>Sample Request:</span>"
			+ "<div class='sample_response'>"
			+ "json = {\"customer_name\":\"apurva\",\"invoice_date\":\"2016/11/3\",\"due_date\":\"2016/11/3\",\"invoice_amount\":100000,\"invoice_status\":\"paid\",\"bank_account\":true,\"credit_card\":false,\"terms\":\"terms\",\"currencyID\":101,\"recurring\":false,\"start_date\":\"2016/12/5\",\"end_date\":\"2016/12/31\",\"recurring_frequency\":\"daily\",\"number_of_invoices\":30,\"invoiceLines\":[{\"line_number\":1,\"description\":\"desc1\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100},{\"line_number\":2,\"description\":\"desc2\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100}]}"
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
			+ "json ={\"customer_name\":\"apurva123\",\"invoice_date\":\"2016/11/3\",\"due_date\":\"2016/12/15\",\"invoice_amount\":2000,\"invoice_status\":\"due\",\"bank_account\":true,\"credit_card\":false,\"terms\":\"terms\",\"currencyID\":201,\"recurring\":true,\"start_date\":\"2016/12/3\",\"end_date\":\"2016/12/30\",\"recurring_frequency\":\"daily\",\"number_of_invoices\":\"10\",\"invoiceLines\":[{\"lineID\":\"05f13774-4cd6-41c1-affa-ab363c0a0a6e\",\"line_number\":1,\"description\":\"updated\",\"quantity\":20,\"unit_cost\":20,\"total_amount\":200},{\"line_number\":5,\"description\":\"new line added\",\"quantity\":20,\"unit_cost\":20,\"total_amount\":200}]}"
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
