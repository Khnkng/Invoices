package com.qount.invoice.service;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.dao.impl.InvoiceLineDAOImpl;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceService {

	private static final Logger LOGGER = Logger.getLogger(InvoiceService.class);

	public static Invoice createInvoice(String companyID, Invoice invoice) {
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			InvoiceDAOImpl invoiceDAO = InvoiceDAOImpl.getInvoiceDAOImpl();
			if (invoiceDAO.save(connection, invoice)) {
				if (InvoiceLineDAOImpl.getInvoiceLineDAOImpl().batchSave(connection, invoice.getInvoiceLines())) {
					connection.commit();
					return invoice;
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static List<Invoice> getInvoices(String userID, String companyID) {
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			return InvoiceDAOImpl.getInvoiceDAOImpl().getList(connection, companyID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static Invoice getInvoice(String userID, String companyID, String invoiceID) {
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			return InvoiceDAOImpl.getInvoiceDAOImpl().get(connection, companyID, invoiceID,userID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}
	
	public static Invoice deleteInvoiceById(Invoice invoice){
		if(invoice == null){
			throw new WebApplicationException();
		}
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			return InvoiceDAOImpl.getInvoiceDAOImpl().delete(connection,invoice);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
		
	}
	
	public static InvoiceLines deleteInvoiceLine(InvoiceLines invoiceLines){
		if(invoiceLines == null){
			throw new WebApplicationException();
		}
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			return InvoiceLineDAOImpl.getInvoiceLineDAOImpl().deleteInvoiceLine(connection,invoiceLines);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
		
	}

	public static Invoice updateInvoice(String companyID, Invoice invoice, String invoiceID) {
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			InvoiceDAOImpl invoiceDAOImpl = InvoiceDAOImpl.getInvoiceDAOImpl();
			InvoiceLineDAOImpl invoiceLineDAOImpl = InvoiceLineDAOImpl.getInvoiceLineDAOImpl();
			List<InvoiceLines> lines = invoiceLineDAOImpl.getLines(connection, invoiceID);
			List<InvoiceLines> requestLines = invoice.getInvoiceLines();
			// lines to be deleted
			lines.removeAll(requestLines);

			if (invoiceDAOImpl.update(connection, invoice)) {
				if (invoiceLineDAOImpl.batchSaveAndDelete(connection, requestLines, lines)) {
					connection.commit();
					return invoice;
				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

}
