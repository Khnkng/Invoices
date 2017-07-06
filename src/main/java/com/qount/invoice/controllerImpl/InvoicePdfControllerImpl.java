package com.qount.invoice.controllerImpl;

import java.io.File;
import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.pdf.InvoiceReference;
import com.qount.invoice.pdf.PdfGenerator;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * controller class for invoice pdf operations
 * 
 * @author Mateen
 * @version 1.0 Jul 06 2017
 */
public class InvoicePdfControllerImpl {

	private static Logger LOGGER = Logger.getLogger(InvoicePdfControllerImpl.class);

	private static File createPdf(String invoiceId) throws Exception {
		LOGGER.debug("entered createPdf invoiceId:" + invoiceId);
		File pdfFile = null;
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			Invoice invoice = MySQLManager.getInvoiceDAOInstance().get(invoiceId);
			InvoiceReference invoiceReference = InvoiceParser.getInvoiceReference(invoice);
			if (invoiceReference == null || invoice == null || StringUtils.isEmpty(invoice.getCurrency())) {
				throw new WebApplicationException("invalid input");
			}
			invoiceReference = MySQLManager.getInvoiceDAOInstance().getInvoiceRelatedDetails(conn, invoiceReference);
			if (invoiceReference == null) {
				throw new WebApplicationException("please create invoice settings");
			}
			invoiceReference.setInvoice(invoice);
			pdfFile = PdfGenerator.createPdf(invoiceReference);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug(e);
			throw e;
		} finally {
			LOGGER.debug("exited createPdf invoiceId:" + invoiceId);
			DatabaseUtilities.closeConnection(conn);
		}
		return pdfFile;
	}

	public static Response getInvoicePdfPrevew(String invoiceId) {
		LOGGER.debug("exited getInvoicePdfPrevew invoiceId:" + invoiceId);
		try {
			File pdfFile = createPdf(invoiceId);
			ResponseBuilder responseBuilder = Response.ok(pdfFile);
			responseBuilder.header("Content-Type", "application/pdf");
			responseBuilder.header("Content-Disposition", "attachment; filename=" + pdfFile.getName());
			return responseBuilder.build();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.debug(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited getInvoicePdfPrevew invoiceId:" + invoiceId);
		}
	}

	public static void main(String[] args) throws Exception {
		createPdf("64402eb8-220e-4925-be7c-a7d7f06931bb");
	}
}
