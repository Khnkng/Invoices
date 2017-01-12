package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.service.InvoiceService;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoiceControllerImpl.class);

	public static Invoice createInvoice(Invoice invoice) {
		Invoice invoiceObj = InvoiceParser.getInvoiceObj(invoice);
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			if (MySQLManager.getInvoiceDAOInstance().save(connection, invoiceObj)) {
				if (MySQLManager.getInvoiceLineDAOInstance().batchSave(connection, invoiceObj.getInvoiceLines())) {
					connection.commit();
					return invoiceObj;
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}

	}

	public static List<Invoice> getInvoices(String userID, String companyID) {
		return InvoiceService.getInvoices(userID, companyID);
	}

	public static Invoice updateInvoice(String userID, String companyID, String invoiceID, Invoice invoice) {
		invoice.setId(invoiceID);
		invoice.setUser_id(userID);
		invoice.setCompany_id(companyID);
		List<InvoiceLines> invoiceLines = invoice.getInvoiceLines();
		for (InvoiceLines invoiceLine : invoiceLines) {
			if (invoiceLine.getLineID() == null) {
				invoiceLine.setLineID(UUID.randomUUID().toString());
			}
			invoiceLine.setInvoiceID(invoice.getId());
		}
		return InvoiceService.updateInvoice(companyID, invoice, invoiceID);
	}

	public static Invoice getInvoice(String userID, String companyID, String invoiceID) {
		return InvoiceService.getInvoice(userID, companyID, invoiceID);

	}

	public static Invoice deleteInvoiceById(String userID, String companyID, String invoiceID) {
		Invoice invoice = new Invoice();
		invoice.setCompany_id(companyID);
		invoice.setId(invoiceID);
		return InvoiceService.deleteInvoiceById(invoice);
	}

	public static InvoiceLines deleteInvoiceLine(String userID, String companyID, String invoiceID, String lineID) {
		InvoiceLines invoiceLine = new InvoiceLines();
		invoiceLine.setLineID(lineID);
		invoiceLine.setInvoiceID(invoiceID);
		return InvoiceService.deleteInvoiceLine(invoiceLine);

	}
}
