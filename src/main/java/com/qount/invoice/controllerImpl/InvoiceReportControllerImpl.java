package com.qount.invoice.controllerImpl;

import java.io.File;
import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.email.EmailHandler;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceMail;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.pdf.InvoiceReference;
import com.qount.invoice.pdf.PdfGenerator;
import com.qount.invoice.pdf.PdfUtil;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * @author Mateen
 * @version 1.0 Feb 28 2017
 */
public class InvoiceReportControllerImpl {

	private static final Logger LOGGER = Logger.getLogger(InvoiceReportControllerImpl.class);

	/**
	 * method used to create pdf for invoices
	 * 
	 * @param userID
	 * @param companyID
	 * @param customerID
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static Response createPdfAndSendEmail(Invoice invoice) throws Exception {
		File pdfFile = null;
		Connection conn = null;
		try {
			InvoiceReference invoiceReference = InvoiceParser.getInvoiceReference(invoice);
			if (invoiceReference == null || invoice == null || StringUtils.isEmpty(invoice.getCurrency())) {
				return Response.status(412).entity("invalid input").build();
			}
			conn = DatabaseUtilities.getReadConnection();
			invoiceReference = MySQLManager.getInvoiceDAOInstance().getInvoiceRelatedDetails(conn, invoiceReference);
			if (invoiceReference == null) {
				throw new WebApplicationException("please create invoice settings");
			}
			invoiceReference.setInvoice(invoice);
			pdfFile = PdfGenerator.createPdf(invoiceReference);
			if (pdfFile != null) {
				JSONObject jsonObj = new JSONObject();
				JSONObject emailJson = new JSONObject();
				emailJson.put("subject", PropertyManager.getProperty("invoice.subject"));
				emailJson.put("mailBodyContentType", PropertyManager.getProperty("mail.body.content.type"));
				jsonObj.put("emailJson", emailJson);
				jsonObj.put("fileName", PropertyManager.getProperty("invoice.email.attachment.name"));
				if (jsonObj != null && jsonObj.length() > 0) {
					if (jsonObj.optJSONObject("emailJson").optJSONArray("recipients") == null || jsonObj.optJSONObject("emailJson").optJSONArray("recipients").length() == 0) {
						JSONArray recipients = new JSONArray();
						recipients.put(invoiceReference.getCustomer().getEmail_id());
						jsonObj.optJSONObject("emailJson").remove("recipients");
						jsonObj.optJSONObject("emailJson").put("recipients", recipients);
					}
					Currencies currencies = MySQLManager.getCurrencyDAOInstance().get(conn, invoice.getCurrency());
					Customer tempCustomer = new Customer();
					tempCustomer.setCustomer_id(invoice.getCustomer_id());
					Customer customer = MySQLManager.getCustomerDAOInstance().retrieveById(conn, tempCustomer);
					Company tempCompany = new Company();
					tempCompany.setId(invoice.getCompany_id());
					Company company = MySQLManager.getCompanyDAOInstance().get(conn, tempCompany);
					invoice.setCustomer(customer);
					invoice.setCurrencies(currencies);
					invoice.setCompanyName(company.getName());
					InvoiceMail invoiceMail = InvoiceParser.getInvoiceMailFromInvoice(invoice);
					boolean isMailSent = EmailHandler.sendEmail(pdfFile, jsonObj, invoiceMail);
					if (isMailSent) {
						return Response.ok("Email sent successfully!").build();
					}
				}
				ResponseBuilder responseBuilder = Response.ok(pdfFile);
				responseBuilder.header("Content-Type", "application/pdf");
				responseBuilder.header("Content-Disposition", "attachment; filename=" + pdfFile.getName());
				return responseBuilder.build();
			}
		} catch (Exception e) {
			LOGGER.error(e);
			if (e instanceof WebApplicationException) {
				throw e;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			PdfUtil.deleteFile(pdfFile);
			DatabaseUtilities.closeConnection(conn);
		}
		return Response.status(500).entity("server error while building pdf").build();
	}

}
