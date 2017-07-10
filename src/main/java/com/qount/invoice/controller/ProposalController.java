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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.ProposalControllerImpl;
import com.qount.invoice.model.Proposal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
@Api(value = "proposal")
@Path("/users/{userID}/companies/{companyID}/proposal")
public class ProposalController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"company_id\":\"82e6b951-25d9-490c-a8d0-0952a6c53a78\",\"company_name\":\"TestTaxes\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"testing 1\",\"objectives\":\"testing 1\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"first_name\":\"Apurva\",\"last_name\":\"Khune\",\"state\":\"unpaid\",\"proposal_date\":\"2017-01-01 11:05:47\",\"acceptance_date\":\"2017-01-03 11:05:47\",\"acceptance_final_date\":\"2017-01-30 11:05:47\",\"notes\":\"notes\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"item_name\":\"White Cards\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"coa_name\":\"Office Expenses\",\"discount\":2.3,\"deposit_amount\":1000,\"processing_fees\":10,\"remainder_json\":{\"abc\":\"def\"},\"remainder_mail_json\":{\"mail\":\"json\"},\"proposalLines\":[{\"description\":\"proposalLine desc\",\"objectives\":\"proposalLine obj\",\"amount\":3000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":0,\"notes\":\"proposalLine notes\",\"proposalLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]},{\"description\":\"proposalLine desc\",\"objectives\":\"proposalLine obj\",\"amount\":3000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":0,\"notes\":\"proposalLine notes\",\"proposalLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":\"9.0000\"}]}],\"proposalTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"12.8\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal createProposal(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @Valid Proposal proposal) {
		return ProposalControllerImpl.createProposal(userID, companyID, proposal);
	}

	@Path("/{proposalID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"company_id\":\"82e6b951-25d9-490c-a8d0-0952a6c53a78\",\"company_name\":\"TestTaxes\",\"amount\":23000,\"currency\":\"INR\",\"description\":\"updated\",\"objectives\":\"updated\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"first_name\":\"Apurva\",\"last_name\":\"Khune\",\"state\":\"paid\",\"proposal_date\":\"2017-01-01 11:05:47\",\"acceptance_date\":\"2017-01-03 11:05:47\",\"acceptance_final_date\":\"2017-01-30 11:05:47\",\"notes\":\"notes\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"item_name\":\"White Cards\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\",\"coa_name\":\"Office Expenses\",\"discount\":25,\"deposit_amount\":5000,\"processing_fees\":20,\"remainder_json\":\"updated sample json\",\"remainder_mail_json\":\"updated sample json\",\"proposalTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"78.96\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal update(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("proposalID") String proposalID,
			@Valid Proposal proposal) {
		return ProposalControllerImpl.updateProposal(userID, companyID, proposalID, proposal);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Proposal> getProposals(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID,@QueryParam("state") String state) {
		return ProposalControllerImpl.getProposals(userID,companyID,state);
	}

	@Path("/{proposalID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Proposal getProposal(@PathParam("userID") String userID, @PathParam("proposalID") String proposalID) {
		return ProposalControllerImpl.getProposal(proposalID);
	}

	@DELETE
	@Path("/{proposalID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete proposal", notes = "Used to delete a proposal.<br>", responseContainer = "java.lang.String")
	public Proposal deleteProposalById(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID,@PathParam("proposalID") @NotNull String proposalID) {
		return ProposalControllerImpl.deleteProposalById(userID,companyID,proposalID);
	}

	@PUT
	@Path("/states/{state}")
	@ApiOperation(value = "update proposal state", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public boolean udpateInvoicesByState(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID,@PathParam("state") @NotNull String state, List<String> ids) {
		return ProposalControllerImpl.updateProposalsState(userID, companyID, ids, state);
	}
}
