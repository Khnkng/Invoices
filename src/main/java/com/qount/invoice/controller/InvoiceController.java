package com.qount.invoice.controller;

import java.util.List;

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

import com.qount.invoice.controllerImpl.InvoiceControllerImpl;
import com.qount.invoice.model.Invoice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Nov 2016
 *
 */
@Api(value = "Invoice")
@Path("/user/{userID}/invoice")
public class InvoiceController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"company_id\":\"7e9dc88f-660d-4b6f-88a9-3eaf9006153b\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"testing 1\",\"objectives\":\"testing 1\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"customer_id\":\"4ab0c48c-5006-4dbe-b0ce-7f0c24ef9bc8\",\"state\":\"unpaid\",\"invoice_date\":\"2017-01-01 11:05:47\",\"acceptance_date\":\"2017-01-03 11:05:47\",\"acceptance_final_date\":\"2017-01-30 11:05:47\",\"notes\":\"notes\",\"discount\":2.3,\"deposit_amount\":1000,\"processing_fees\":10,\"remainder_json\":\"jsno 1\",\"remainder_mail_json\":\"json 2\",\"po_number\":\"po1234\",\"amount_due\":565656,\"recurring_frequency\":\"daily\",\"recurring_frequency_value\":10,\"recurring_start_date\":\"2017-01-02 11:05:47\",\"recurring_end_date\":\"2017-20-02 11:05:47\",\"payment_spring_customer_id\":\"123456\",\"document_id\":\"di1234\",\"is_recurring\":true,\"is_mails_automated\":true,\"is_cc_current_user\":true,\"invoiceLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine desc\",\"objectives\":\"proposalLine obj\",\"amount\":3000,\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":0,\"notes\":\"proposalLine notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine desc\",\"objectives\":\"proposalLine obj\",\"amount\":3000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":0,\"notes\":\"proposalLine notes\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"invoiceLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]}],\"invoiceTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"12.8\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice createInvoice(@PathParam("userID") String userID, @Valid Invoice invoice) {
		return InvoiceControllerImpl.createInvoice(userID, invoice);
	}

	@Path("/{invoiceID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"company_id\":\"82e6b951-25d9-490c-a8d0-0952a6c53a78\",\"company_name\":\"TestTaxes\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"updated\",\"objectives\":\"updated\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"customer_id\":\"\",\"customer_name\":\"\",\"customer_email_id\":\"\",\"state\":\"unpaid\",\"invoice_date\":\"2017-02-01 11:05:47\",\"acceptance_date\":\"2017-02-06 11:00:00\",\"acceptance_final_date\":\"2017-02-28 12:00:00\",\"notes\":\"updated notes\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"item_name\":\"White Cards\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"coa_name\":\"Office Expenses\",\"discount\":2.3,\"deposit_amount\":1000,\"processing_fees\":10,\"remainder_json\":\"sample json 1\",\"remainder_mail_json\":\"sample json 2\",\"recurring_frequency\":\"weekly\",\"recurring_frequency_value\":10,\"recurring_start_date\":\"2017-02-07 11:00:00\",\"recurring_end_date\":\"2017-02-28 11:00:00\",\"payment_spring_customer_id\":\"123456\",\"po_number\":\"po1234\",\"document_id\":\"di1234\",\"amount_due\":90000,\"is_recurring\":true,\"is_mails_automated\":true,\"is_cc_current_user\":true,\"invoiceTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"89\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Invoice updateInvoices(@PathParam("userID") String userID, @PathParam("invoiceID") @NotNull String invoiceID,
			@Valid Invoice invoice) {
		return InvoiceControllerImpl.updateInvoice(userID, invoiceID, invoice);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Invoice> getInvoices(@PathParam("userID") @NotNull String userID) {
		return InvoiceControllerImpl.getInvoices(userID);
	}

	@Path("/{invoiceID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Invoice getProposal(@PathParam("userID") @NotNull String userID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.getInvoice(userID, invoiceID);
	}

	@DELETE
	@Path("/{invoiceID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Invoice deleteInvoiceById(@PathParam("userID") String userID,
			@PathParam("invoiceID") @NotNull String invoiceID) {
		return InvoiceControllerImpl.deleteInvoiceById(userID, invoiceID);
	}

}
