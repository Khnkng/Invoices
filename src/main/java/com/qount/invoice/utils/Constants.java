package com.qount.invoice.utils;

import java.text.SimpleDateFormat;

import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.BaseFont;

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
	public static final String DUE_DATE_FORMAT = "yyyy-MM-dd";
	// SWAGGER VARIABLES
	public static final String SWAGGER_API_SPEC_VERSION = "1.2.3";
	public static final String SWAGGER_API_HTTP = "https";
	public static final String SWAGGER_API_PACKAGE = "com.qount.invoice.controller";
	public static final String BILLS_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String INVOICE_UI_DATE_FORMAT = "MM/dd/yy";
	public static final SimpleDateFormat TIME_STATMP_TO_BILLS_FORMAT = new SimpleDateFormat(BILLS_DATE_FORMAT);
	public static final SimpleDateFormat TIME_STATMP_TO_INVOICE_FORMAT = new SimpleDateFormat(INVOICE_UI_DATE_FORMAT);
	public static final String FONT1 = "PlayfairDisplay-Regular.ttf";
	public static final String FONT2 = "arial.ttf";

	public static final Font F0 = new Font(FontFamily.HELVETICA, 13.0f, Font.NORMAL, BaseColor.WHITE);
	public static final Font F1 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
	public static final Font F2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
	public static final Font F3 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.WHITE);
	public static final Font F4 = new Font(FontFamily.HELVETICA, 24.0f, Font.NORMAL, BaseColor.BLACK);
	public static final Font F5 = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, BaseColor.GRAY);
	public static final Font F6 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.LIGHT_GRAY);
	public static final Font F7 = new Font(FontFamily.HELVETICA, 12.0f, Font.BOLD, BaseColor.LIGHT_GRAY);
	public static final Font F8 = new Font(FontFamily.HELVETICA, 18.0f, Font.NORMAL, BaseColor.WHITE);
	public static final Font F9 = new Font(FontFamily.HELVETICA, 11.0f, Font.NORMAL, BaseColor.WHITE);
	public static final Font TITLE_FONT = new Font(FontFamily.HELVETICA, 29.0f, Font.NORMAL, BaseColor.BLACK);
	public static final Font SUBHEADING_FONT = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.GRAY);
	public static final Font CURRENCY_FONT = FontFactory.getFont(Constants.FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12);
	public static final Font CURRENCY_FONT_2 = FontFactory.getFont(Constants.FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12.0f, Font.BOLD, BaseColor.BLACK);
	public static final Font CURRENCY_FONT_3 = FontFactory.getFont(Constants.FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12.0f, Font.NORMAL, BaseColor.BLACK);
	public static final Font CURRENCY_FONT_4 = FontFactory.getFont(Constants.FONT1, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18.0f, Font.NORMAL, BaseColor.WHITE);

}
