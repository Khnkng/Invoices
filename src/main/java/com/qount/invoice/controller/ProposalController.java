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
import javax.ws.rs.core.Response;

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
			+ "json = {\"company_id\":\"ca51c550-e8cb-4582-806f-5edf709e4c4d\",\"company_name\":\"company1\",\"amount\":555,\"currency\":\"INR\",\"description\":\" proposal 3\",\"objectives\":\"proposal 3\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"state\":\"paid\",\"proposal_date\":\"2017-01-01 11:05:47\",\"notes\":\"notes\",\"discount\":2.3,\"deposit_amount\":1000,\"processing_fees\":10,\"is_recurring\":\"true\",\"number\":\"10101\",\"proposalLines\":[{\"description\":\"proposalLine 1\",\"objectives\":\"proposalLine objective 1\",\"amount\":10,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":12,\"notes\":\"proposalLine notes\",\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\"},{\"description\":\"proposalLine 2\",\"objectives\":\"proposalLine objective 2\",\"amount\":20,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":2,\"notes\":\"proposalLine notes\",\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\"}],\"recepientsMails\":[\"apurvakh.92@gmail.com\"],\"sendMail\":false}"
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
			+ "json ={\"company_id\":\"ca51c550-e8cb-4582-806f-5edf709e4c4d\",\"company_name\":\"company1\",\"amount\":12356,\"currency\":\"INR\",\"description\":\"updated proposal 3\",\"objectives\":\"updated proposal 3\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"last_updated_by\":\"apurva.khune@qount.io\",\"state\":\"paid\",\"proposal_date\":\"2017-01-01 11:05:47\",\"notes\":\"notes\",\"discount\":2.3,\"deposit_amount\":1000,\"processing_fees\":10,\"is_recurring\":\"true\",\"number\":\"10101\",\"proposalLines\":[{\"description\":\"proposalLine 1\",\"objectives\":\"proposalLine objective 1\",\"amount\":10,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":12,\"notes\":\"proposalLine notes\",\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\"},{\"description\":\"proposalLine 2\",\"objectives\":\"proposalLine objective 2\",\"amount\":20,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":45,\"price\":2,\"notes\":\"proposalLine notes\",\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"coa_id\":\"04ec2ea5-8f4b-4112-9a37-89a52a6269de\"}],\"recepientsMails\":[\"apurvakh.92@gmail.com\"],\"sendMail\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal update(@PathParam("userID") String userID, @PathParam("companyID") @NotNull String companyID, @PathParam("proposalID") String proposalID,
			@Valid Proposal proposal) {
		return ProposalControllerImpl.updateProposal(userID, companyID, proposalID, proposal);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public Response getProposals(@PathParam("userID") String userID,@PathParam("companyID") @NotNull String companyID,@QueryParam("state") String state) {
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
	@Path("/state/{state}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "update proposal state", notes = "Used to update proposal state.<br>", responseContainer = "java.lang.String")
	public boolean acceptProposal(@PathParam("userID") String userID,
			@PathParam("companyID") @NotNull String companyID, @PathParam("state") @NotNull String state,
			List<String> proposalIdList) {
		return ProposalControllerImpl.updateProposalState1(userID, companyID, state,proposalIdList);
	}
}
