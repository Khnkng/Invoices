package com.qount.invoice.utils;

import com.google.gson.Gson;

public class Constants {

	public static final String FAILURE_STATUS = "Failure";
	public static final String PRECONDITION_FAILED = "invalid input";
	public static final String SUCCESS_STATUS = "Success";
	public static final String UNEXPECTED_ERROR_STATUS = "Un-expected Error";
	public static final String INVALID_REQUEST_ERROR_STATUS = "Invalid Request";
	public static final String DATABASE_ERROR_STATUS = "Database Error";
	public static final String CASSANDRA_KEYSPACE_BIGPAY = "bigpay";
	public static final String ADMIN_ROLE = "admin";
	public static final String URL_DOMAIN = "signup.activation.link.domain";
	public static final Gson GSON = new Gson();
	// SWAGGER VARIABLES
	public static final String SWAGGER_API_SPEC_VERSION = "1.2.3";
	public static final String SWAGGER_API_HTTP = "https";
	public static final String SWAGGER_API_PACKAGE = "com.qount.invoice.controller";
}