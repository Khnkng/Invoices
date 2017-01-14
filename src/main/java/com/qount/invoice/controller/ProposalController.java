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

import com.qount.invoice.controllerImpl.ProposalControllerImpl;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "proposal")
@Path("/user/{userID}/proposal")
public class ProposalController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"customer_name\":\"apurva\",\"total_amount\":100000,\"currencyID\":101,\"bank_account\":true,\"credit_card\":false,\"proposalLines\":[{\"line_number\":1,\"description\":\"part2-1\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100},{\"line_number\":2,\"description\":\"part2-2\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100},{\"line_number\":3,\"description\":\"part2-3\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal createProposal(@PathParam("userID") String userID, @Valid Proposal proposal) {
		return ProposalControllerImpl.createProposal(userID, proposal);
	}

	@Path("/{proposalID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"customer_name\":\"apurva\",\"total_amount\":100000,\"currencyID\":101,\"bank_account\":true,\"credit_card\":false,\"proposalLines\":[{\"lineID\":\"1612ad32-913d-461b-bcee-c41af2754dd7\",\"line_number\":1,\"description\":\"updateddd\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100},{\"line_number\":2,\"description\":\"new line added\",\"quantity\":10,\"unit_cost\":10,\"total_amount\":100}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal update(@PathParam("userID") String userID, @PathParam("proposalID") String proposalID,
			@Valid Proposal proposal) {
		return ProposalControllerImpl.updateProposal(userID, proposalID, proposal);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Proposal> getProposals(@PathParam("userID") String userID, @PathParam("companyID") String companyID) {
		return ProposalControllerImpl.getProposals(userID);
	}

	@Path("/{proposalID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Proposal getProposal(@PathParam("userID") String userID, @PathParam("proposalID") String proposalID) {
		return ProposalControllerImpl.getProposal(userID, proposalID);
	}

	@DELETE
	@Path("/{proposalID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public Proposal deleteProposalById(@PathParam("userID") String userID,
			@PathParam("proposalID") @NotNull String proposalID) {
		return ProposalControllerImpl.deleteProposalById(userID, proposalID);
	}

	@DELETE
	@Path("/{proposalID}/proposalline/{proposalLineID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete expense", notes = "Used to delete a expense code.<br>", responseContainer = "java.lang.String")
	public ProposalLine deleteProposalLine(@PathParam("userID") String userID,
			@PathParam("proposalID") @NotNull String proposalID,
			@PathParam("proposalLineID") @NotNull String proposalLineID) {
		return ProposalControllerImpl.deleteProposalLine(userID, proposalID, proposalLineID);
	}
}
