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
			+ "json = {\"customer_id\":\"1e4ca87f-7294-4c5b-8e88-3013a92c39d0\",\"send_to\":\"8650745b-c5a7-49cd-a0ad-bc5e69f948d4\",\"number\":\"10101\",\"proposal_date\":\"01/25/17\",\"currency\":\"USD\",\"recepientsMails\":[],\"sendMail\":false,\"amount\":1000,\"estimate_date\":\"01/25/17\",\"notes\":\"notes\",\"discount\":0,\"deposit_amount\":0,\"processing_fees\":10,\"proposalLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine 1\",\"amount\":500,\"quantity\":5,\"price\":10,\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"type\":\"task\"},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine 1\",\"amount\":500,\"quantity\":5,\"price\":10,\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"type\":\"task\"}],\"term\":\"net30\"}"
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
			+ "json = {\"customer_id\":\"1e4ca87f-7294-4c5b-8e88-3013a92c39d0\",\"send_to\":\"8650745b-c5a7-49cd-a0ad-bc5e69f948d4\",\"number\":\"10101\",\"proposal_date\":\"01/25/17\",\"currency\":\"USD\",\"recepientsMails\":[],\"sendMail\":false,\"amount\":1000,\"estimate_date\":\"01/25/17\",\"notes\":\"notes\",\"discount\":0,\"deposit_amount\":0,\"processing_fees\":10,\"proposalLines\":[{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine 1\",\"amount\":500,\"quantity\":5,\"price\":10,\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"type\":\"task\"},{\"item_id\":\"07f9ec5e-a808-4962-bd17-9b5957b128c8\",\"description\":\"proposalLine 1\",\"amount\":500,\"quantity\":5,\"price\":10,\"tax_id\":\"13834ec1-6099-4882-8ded-ab07f9eca43a\",\"type\":\"task\"}],\"term\":\"net30\"}"
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
	@ApiOperation(value = "update proposal state", notes = "Used to update proposal state(convert_to_invoice|deny|delete)."
			+ "json:[\"a71aee47-b8bd-4e3e-a40c-142cb9f4791d\"]<br>", responseContainer = "java.lang.String")
	public boolean acceptProposal(@PathParam("userID") String userID,
			@PathParam("companyID") @NotNull String companyID, @PathParam("state") @NotNull String state,
			List<String> proposalIdList) {
		return ProposalControllerImpl.updateProposalState(userID, companyID, state,proposalIdList);
	}
}
