package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLines;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoiceControllerImpl.class);

	public static Invoice createInvoice(String userID, Invoice invoice) {
		Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice);
		if (invoiceObj == null) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
		}
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

	public static Invoice updateInvoice(String userID, String invoiceID, Invoice invoice) {
		Connection connection = null;
		try {
			Invoice invoiceObj = InvoiceParser.getInvoiceObj(userID, invoice);
			if (invoiceObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						"Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			if (MySQLManager.getInvoiceDAOInstance().deleteAndCreateInvoice(connection, invoiceID, invoiceObj)) {
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

	public static List<Invoice> getInvoices(String userID) {
		try {
			if (StringUtils.isEmpty(userID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().getInvoiceList(userID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static Invoice getInvoice(String userID, String invoiceID) {
		try {
			if (StringUtils.isEmpty(userID) && StringUtils.isEmpty(invoiceID)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().getInvoiceById(invoiceID, userID);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}

	}

	public static Invoice deleteInvoiceById(String userID, String invoiceID) {
		try {
			Invoice invoice = InvoiceParser.getInvoiceObjToDelete(userID, invoiceID);
			if (invoice == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceDAOInstance().delete(invoice);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}

	public static InvoiceLines deleteInvoiceLine(String userID, String companyID, String invoiceID, String lineID) {
		try {
			InvoiceLines invoiceLine = InvoiceParser.getInvoiceLineObjToDelete(invoiceID, lineID);
			if (invoiceLine == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
						Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			return MySQLManager.getInvoiceLineDAOInstance().deleteInvoiceLine(invoiceLine);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,
					Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
		}
	}

}
