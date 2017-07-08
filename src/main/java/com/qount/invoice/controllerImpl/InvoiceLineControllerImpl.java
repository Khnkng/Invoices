package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.impl.InvoiceLineDAOImpl;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.parser.InvoiceLineParser;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Feb 2016
 *
 */
public class InvoiceLineControllerImpl {

	private static final Logger LOGGER = Logger.getLogger(InvoiceLineControllerImpl.class);

	public static List<InvoiceLine> createInvoiceLine(String userID, String invoiceID, List<InvoiceLine> invoiceLines) {
		Connection connection = null;
		try {
			List<InvoiceLine> invoiceLineObjLst = InvoiceLineParser.getInvoiceLineList(userID, invoiceID, invoiceLines);
			if (invoiceLineObjLst == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			List<InvoiceLine> invoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().save(connection, invoiceLineObjLst);
			if (invoiceLineResult != null) {
				connection.commit();
				return invoiceLineObjLst;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static InvoiceLine updateInvoiceLine(String userID, String invoiceID, String invoiceLineId, InvoiceLine invoiceLine) {
		Connection connection = null;
		try {
			InvoiceLine invoiceLineObj = InvoiceLineParser.getInvoiceLineObj(userID, invoiceID, invoiceLineId, invoiceLine);
			if (invoiceLineObj == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			connection.setAutoCommit(false);
			InvoiceLine InvoiceLineResult = MySQLManager.getInvoiceLineDAOInstance().update(connection, invoiceLineObj);
			if (InvoiceLineResult != null) {
				connection.commit();
				return invoiceLineObj;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}

	}

	public static InvoiceLine deleteInvoiceLineById(String userID, String invoiceLineID) {
		try {
			InvoiceLine InvoiceLine = InvoiceLineParser.getInvoiceLineObjToDelete(invoiceLineID);
			if (InvoiceLine == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			return InvoiceLineDAOImpl.getInvoiceLineDAOImpl().deleteInvoiceLine(InvoiceLine);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		}
	}
}
