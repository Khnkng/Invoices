package com.qount.invoice.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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

@Api(value = "proposal")
@Path("/user/{userID}/company/{companyID}/proposal")
public class ProposalController {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "Create Proposal", notes = "Used to add new proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json = {\"companyID\":\"1212\",\"customer_name\":\"Apurva\",\"total_amount\":\"1000\",\"currency\":\"$\",\"bank_account\":true,\"credit_card\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal createProposal(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@Valid Proposal proposal) {
		return ProposalControllerImpl.createProposal(userID, companyID, proposal);
	}

	// public static void main(String[] args) throws Exception {
	// InputStream inputStream=new
	// FileInputStream("src/main/resources/project_development.properties");
	// PropertyManager.getPropertyManager().getProperties().load(inputStream);
	// ProposalController pc = new ProposalController();
	// String bodyJson =
	// "{\"companyID\":\"abc\",\"customer_name\":\"Apurva\",\"total_amount\":\"1000\",\"currency\":\"$\",\"bank_account\":true,\"credit_card\":false}";
	// Response response = pc.createProposal("apurva.khune", "abc", bodyJson);
	// System.out.println(response.getEntity());
	// }

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposals of company", value = "retieves proposals", responseContainer = "java.lang.String")
	public List<Proposal> getProposals(@PathParam("userID") String userID, @PathParam("companyID") String companyID) {
		return ProposalControllerImpl.getProposals(userID, companyID);
	}

	@Path("/{proposalID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@NotNull(message = "Invalid Request")
	@ApiOperation(value = "update proposal", notes = "Used to update proposal"
			+ "<span class='bolder'>Sample Request:</span>" + "<div class='sample_response'>"
			+ "json ={\"proposalID\":\"12\",\"companyID\":\"1212\",\"customer_name\":\"Apurva\",\"total_amount\":\"1000\",\"currency\":\"$\",\"bank_account\":true,\"credit_card\":false}"
			+ "</div>", responseContainer = "java.lang.String")
	public Proposal update(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@PathParam("proposalID") String proposalID, @Valid Proposal proposal) {
		return ProposalControllerImpl.updateProposal(userID, companyID,proposalID, proposal);
	}

	@Path("/{proposalID}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(notes = "Used to retieve proposal of company", value = "retieves proposal", responseContainer = "java.lang.String")
	public Proposal getProposal(@PathParam("userID") String userID, @PathParam("companyID") String companyID,
			@PathParam("proposalID") String proposalID) {
		return ProposalControllerImpl.getProposal(userID, companyID, proposalID);
	}
}
