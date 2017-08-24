package com.qount.invoice.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

public class Constants {
	// SWAGGER VARIABLES

	public static final String SWAGGER_API_SPEC_VERSION = "1.2.3";
	public static final String SWAGGER_API_HTTP = "https";
	public static final String SWAGGER_API_PACKAGE = "com.qount.invoice.controller";
	public static final String FAILURE_STATUS_STR = "Failure";
	public static final String PRECONDITION_FAILED_STR = "invalid input";
	public static final String SUCCESS_STATUS_STR = "Success";
	public static final int SUCCESS_RESPONSE_CODE = 200;
	public static final String UNEXPECTED_ERROR_STATUS_STR = "Un-expected Error";
	public static final String PARTIAL_SUCCESS = "proposal not inserted into invoices";
	public static final String INVALID_REQUEST_ERROR_STATUS = "Invalid Request";
	public static final String DATABASE_ERROR_STATUS_STR = "Database Error";
	public static final String CASSANDRA_KEYSPACE_BIGPAY = "bigpay";
	public static final String ADMIN_ROLE = "admin";
	public static final String URL_DOMAIN = "signup.activation.link.domain";
	public static final Gson GSON = new Gson();
	public static final String DUE_DATE_FORMAT = "yyyy-MM-dd";

	public static final String BILLS_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String INVOICE_UI_DATE_FORMAT = "MM/dd/yy";
	public static final String PROPOSAL_UI_DATE_FORMAT = "MM/dd/yy";
	public static final String INVOICE_MAIL_DATE_FORMAT = "MMM dd, yyyy";
	public static final SimpleDateFormat TIME_STATMP_TO_BILLS_FORMAT = new SimpleDateFormat(BILLS_DATE_FORMAT);
	public static final SimpleDateFormat TIME_STATMP_TO_INVOICE_FORMAT = new SimpleDateFormat(INVOICE_UI_DATE_FORMAT);
	public static final SimpleDateFormat TIME_STATMP_TO_PROPOSAL_FORMAT = new SimpleDateFormat(PROPOSAL_UI_DATE_FORMAT);
	public static final SimpleDateFormat DATE_TO_INVOICE_FORMAT = new SimpleDateFormat(Constants.INVOICE_UI_DATE_FORMAT);
	public static final SimpleDateFormat TIME_STATMP_TO_INVOICE_MAIL_FORMAT = new SimpleDateFormat(INVOICE_MAIL_DATE_FORMAT);
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

	public static final String PROJECT_CURRENT_VERSION = "project.current.version";
	// public static final String PAYMENT_SPRING_API_DATE_FORMAT_STR =
	// "yyyy-MM-ddThh:mm:ss.SSSZ";
	public static final String INVOICE_DATE_FORMAT_STR = "yyyy-MM-dd hh:mm:ss";
	// public static final SimpleDateFormat PAYMENT_SPRING_API_DATE_FORMAT = new
	// SimpleDateFormat(PAYMENT_SPRING_API_DATE_FORMAT_STR);
	public static final SimpleDateFormat INVOICE_DATE_FORMAT = new SimpleDateFormat(INVOICE_DATE_FORMAT_STR);
	public static final SimpleDateFormat DATE_FORMAT_GMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		DATE_FORMAT_GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	public static final String DEFAULT_INVOICE_CURRENCY = "USD";
	public static final CurrencyConverter CURRENCY_CONVERTER = new CurrencyConverter();
	public static final String INVOICE_CONVERSION_DATE_FORMAT_STR = "yyyy-MM-dd";
	public static final SimpleDateFormat INVOICE_CONVERSION_DATE_FORMAT = new SimpleDateFormat(INVOICE_CONVERSION_DATE_FORMAT_STR);
	public static final DecimalFormat INVOICE_CONVERSION_DECIMALFORMAT = new DecimalFormat("#.##");
	public static final String POST = "post";
	public static final String SUBSCRIPTION_CUSTOMER_CHARGE = "subscription_customer_charge";
	public static final String INVOICE_STATE_SENT = "sent";
	public static final String INVOICE_STATE_DRAFT = "draft";
	public static final String INVOICE_STATE_PAID = "paid";
	public static final String INVOICE_STATE_PARTIALLY_PAID = "partially_paid";
	public static final String INVOICE_CREDIT_CARD = "Credit Card";
	public static final String INVOICE_BANK_ACCOUNT = "Bank Account";
	public static final String PROPOSAL_STATE_DENY = "deny";
	public static final String PROPOSAL_STATE_ACCEPT = "accept";
	public static final String PROPOSAL_STATE_DELETE = "delete";
	public static final String PROPOSAL_STATE_CONVERT_TO_INVOICE = "convert_to_invoice";
	
	public static final int DATABASE_ERROR_STATUS = 422;
	public static final int INVALID_INPUT_STATUS = 412;
}
