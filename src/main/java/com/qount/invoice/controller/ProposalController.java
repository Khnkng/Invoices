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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
@Api(value = "proposal")
@Path("/user/{userID}/proposal")
public class ProposalController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"company_id\":\"qount\",\"company_name\":\"qount\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"desc\",\"objectives\":\"obj\",\"proposalLines\":[{\"description\":\"desc-1\",\"objectives\":\"obj-1\",\"amount\":2000,\"currency\":\"INR\"},{\"description\":\"desc-2\",\"objectives\":\"obj-2\",\"amount\":3000,\"currency\":\"INR\"}]}"
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
			+ "json ={\"company_id\":\"qount\",\"company_name\":\"qount\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"desc\",\"objectives\":\"obj\",\"proposalLines\":[{\"description\":\"desc-1\",\"objectives\":\"obj-1\",\"amount\":2000,\"currency\":\"INR\"},{\"description\":\"desc-2\",\"objectives\":\"obj-2\",\"amount\":3000,\"currency\":\"INR\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal update(@PathParam("userID") String userID, @PathParam("proposalID") String proposalID,
			@Valid Proposal proposal) {
		return ProposalControllerImpl.updateProposal(userID, proposalID, proposal);
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Proposal> getProposals(@PathParam("userID") String userID) {
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
	@ApiOperation(value = "Delete proposal", notes = "Used to delete a proposal.<br>", responseContainer = "java.lang.String")
	public Proposal deleteProposalById(@PathParam("userID") String userID,
			@PathParam("proposalID") @NotNull String proposalID) {
		return ProposalControllerImpl.deleteProposalById(userID, proposalID);
	}

}
