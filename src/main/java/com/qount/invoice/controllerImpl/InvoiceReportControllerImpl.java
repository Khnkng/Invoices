package com.qount.invoice.controllerImpl;

import java.io.File;
import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.email.EmailHandler;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.pdf.InvoiceReference;
import com.qount.invoice.pdf.PdfGenerator;
import com.qount.invoice.pdf.PdfUtil;
import com.qount.invoice.utils.CommonUtils;
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
	public static Response createPdf(String companyID, String customerID, String invoiceID, String json) throws Exception {
		File pdfFile = null;
		Connection conn = null;
		try {
			InvoiceReference invoiceReference = InvoiceParser.getInvoiceReference(companyID, customerID, invoiceID);
			if (invoiceReference == null) {
				return Response.status(412).entity("invalid input").build();
			}
			conn = DatabaseUtilities.getReadConnection();
			invoiceReference = MySQLManager.getInvoiceDAOInstance().getInvoiceRelatedDetails(conn, invoiceReference);
			Invoice invoice = MySQLManager.getInvoiceDAOInstance().get(invoiceID);
			invoiceReference.setInvoice(invoice);
			pdfFile = PdfGenerator.createPdf(invoiceReference);
			if (pdfFile != null) {
				JSONObject jsonObj = CommonUtils.getJsonFromString(json);
				if (jsonObj != null && jsonObj.length() > 0) {
					if(jsonObj.optJSONObject("emailJson").optJSONArray("recipients") == null || jsonObj.optJSONObject("emailJson").optJSONArray("recipients").length() ==0){
						JSONArray recipients = new JSONArray();
						recipients.put(invoiceReference.getCustomer().getEmail_id());
						jsonObj.optJSONObject("emailJson").remove("recipients");
						jsonObj.optJSONObject("emailJson").put("recipients", recipients);
					}
					boolean isMailSent = EmailHandler.sendEmail(pdfFile, jsonObj, invoiceID);
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
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			PdfUtil.deleteFile(pdfFile);
			DatabaseUtilities.closeConnection(conn);
		}
		return Response.status(500).entity("server error while building pdf").build();
	}

}
