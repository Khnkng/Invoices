package com.qount.invoice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.qount.invoice.controllerImpl.ProposalLineControllerImpl;
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
			+ "json = [{\"description\":\"line 1\",\"objectives\":\"line 1\",\"amount\":4000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":4,\"price\":4,\"notes\":\"line 1\",\"proposalLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":12.8},{\"tax_id\":\"207a247a-6bc0-42e3-b3e6-237a69c19b02\",\"tax_rate\":13.5}]},{\"description\":\"line 2\",\"objectives\":\"line 2\",\"amount\":4000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":4,\"price\":4,\"notes\":\"line 2\",\"proposalLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":78.3}]}]"
			+ "</div>", responseContainer = "java.lang.String")
	public List<ProposalLine> createProposal(@PathParam("userID") String userID,
			@PathParam("proposalID") String proposalID, @Valid List<ProposalLine> proposalLines) {
		return ProposalLineControllerImpl.createProposalLine(userID, proposalID, proposalLines);
	}

	@Path("/{proposalLineID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"description\":\"updated\",\"objectives\":\"updated\",\"amount\":70000,\"currency\":\"INR\",\"last_updated_by\":\"apurva.khune@qount.io\",\"last_updated_at\":\"2017-01-17 11:14:35\",\"quantity\":7,\"price\":7,\"notes\":\"new line\",\"proposalLineTaxes\":[{\"tax_id\":\"07b64e12-d4de-47c2-86ac-5f7245a16f7d\",\"tax_rate\":\"9.0000\"}]}"
			+ "</div>", responseContainer = "java.lang.String")
	public ProposalLine update(@PathParam("userID") String userID, @PathParam("proposalID") String proposalID,
			@PathParam("proposalLineID") String proposalLineID, @Valid ProposalLine proposalLine) {
		return ProposalLineControllerImpl.updateProposalLine(userID, proposalID,proposalLineID, proposalLine);
	}
	
	@DELETE
	@Path("/{proposalLineID}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Delete proposal line", notes = "Used to delete proposal line<br>", responseContainer = "java.lang.String")
	public ProposalLine deleteProposalById(@PathParam("userID") String userID,
			@PathParam("proposalLineID") @NotNull String proposalLineID) {
		return ProposalLineControllerImpl.deleteProposalLineById(userID, proposalLineID);
	}
}
