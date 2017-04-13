package com.qount.invoice.controller;

/**
 * 
 * @author apurva
 * @version 1.0
 * Jan 7th 2016
 */
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Project Metadata Controller")
@Path("/user/{userID}")

public class ProjectVersionController {
	/**
	 * this method is used to get the Current version of Project
	 */
	@GET
	@Path("/final")
	@ApiOperation(value = "Returns Project Current version ", notes = "Used to to get Project Current version", responseContainer = "java.lang.String")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjectCurrentVersion(@PathParam("userId") String userId) {
		return CommonUtils.constructResponse(PropertyManager.getProperty(Constants.PROJECT_CURRENT_VERSION, null),Constants.SUCCESS_RESPONSE_CODE);

	}

}