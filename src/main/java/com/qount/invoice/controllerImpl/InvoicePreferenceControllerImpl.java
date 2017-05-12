package com.qount.invoice.controllerImpl;

import java.sql.Connection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 25 Jan 2017
 *
 */
public class InvoicePreferenceControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoicePreferenceControllerImpl.class);

	public static InvoicePreference createInvoicePreference(String userID, String companyId, InvoicePreference invoicePreference) {
		Connection connection = null;
		try {
			if (invoicePreference == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoicePreference.setCompanyId(companyId);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePreference result = MySQLManager.getInvoicePreferenceDAOInstance().save(connection, invoicePreference);
			if (result == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
			} else {
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static InvoicePreference updateInvoicePreference(String companyId, String invoiceID, InvoicePreference invoicePreference) {
		Connection connection = null;
		try {
			if (invoicePreference == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			invoicePreference.setCompanyId(companyId);
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePreference result = MySQLManager.getInvoicePreferenceDAOInstance().update(connection, invoicePreference);
			if (result == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
			} else {
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static InvoicePreference getInvoicePreference(String companyId) {
		Connection connection = null;
		try {
			if (StringUtils.isBlank(companyId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setCompanyId(companyId);
			return MySQLManager.getInvoicePreferenceDAOInstance().getInvoiceByCompanyId(connection, invoicePreference);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

	public static InvoicePreference deleteInvoicePreferenceById(String invoicePreferenceId) {
		Connection connection = null;
		try {
			if (StringUtils.isBlank(invoicePreferenceId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.PRECONDITION_FAILED, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setId(invoicePreferenceId);
			InvoicePreference result = MySQLManager.getInvoicePreferenceDAOInstance().delete(connection, invoicePreference);
			if (result == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS, Constants.UNEXPECTED_ERROR_STATUS, Status.INTERNAL_SERVER_ERROR));
			} else {
				return result;
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS,e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
	}

}
