package com.qount.invoice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.ProposalControllerImpl;
import com.qount.invoice.controllerImpl.ProposalLineControllerImpl;
import com.qount.invoice.model.Proposal;
import com.qount.invoice.model.ProposalLine;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Apurva, Qount.
 * @version 1.0, 30 Jan 2016
 *
 */
@Api(value = "proposal Line")
@Path("/user/{userID}/proposal/{proposalID}/line")
public class ProposalLineController {
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal line", notes = "Used to add new proposal line"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"company_id\":\"qount\",\"company_name\":\"qount\",\"amount\":50000,\"currency\":\"INR\",\"description\":\"desc\",\"objectives\":\"obj\",\"proposalLines\":[{\"description\":\"desc-1\",\"objectives\":\"obj-1\",\"amount\":2000,\"currency\":\"INR\"},{\"description\":\"desc-2\",\"objectives\":\"obj-2\",\"amount\":3000,\"currency\":\"INR\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public List<ProposalLine> createProposal(@PathParam("userID") String userID,@PathParam("proposalID") String proposalID, @Valid List<ProposalLine> proposalLine) {
		return ProposalLineControllerImpl.createProposalLine(userID, proposalID,proposalLine);
	}

	@Path("/{lineID}")
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
}
