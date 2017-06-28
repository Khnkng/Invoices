package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoicePlan;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 28 June 2017
 *
 */
public class InvoicePlanControllerImpl {
	private static final Logger LOGGER = Logger.getLogger(InvoicePlanControllerImpl.class);

	public static InvoicePlan createInvoicePlan(String userId, String companyId, InvoicePlan invoicePlan) {
		Connection connection = null;
		LOGGER.debug("entered createInvoicePlan() userID:" + userId + " companyId:" + companyId + " invoicePlan:" + invoicePlan);
		try {
			if(StringUtils.isAnyEmpty(invoicePlan.getName(),invoicePlan.getAmount(),invoicePlan.getEnds_after())){
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,"name, amount, ends after are mandatory for a plan to create", Status.PRECONDITION_FAILED));
			}
			invoicePlan.setId(UUID.randomUUID().toString());
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePlan resultInvoicePlan = MySQLManager.getInvoicePlanDAOInstance().create(connection, invoicePlan);
			if (resultInvoicePlan != null) {
					return resultInvoicePlan;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited createInvoicePlan() userID:" + userId + " companyId:" + companyId + " invoicePlan:" + invoicePlan);
		}
	}

	public static InvoicePlan updateInvoicePlan(String userId, String companyId, String proposalId, InvoicePlan invoicePlan) {
		Connection connection = null;
		LOGGER.debug("entered updateInvoicePlan() userID:" + userId + " companyId:" + companyId + " invoicePlan:" + invoicePlan);
		try {
			if(StringUtils.isEmpty(invoicePlan.getId())){
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,"Id is mandatory for a plan to update", Status.PRECONDITION_FAILED));
			}
			invoicePlan.setId(UUID.randomUUID().toString());
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection == null) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
			}
			InvoicePlan resultInvoicePlan = MySQLManager.getInvoicePlanDAOInstance().update(connection, invoicePlan);
			if (resultInvoicePlan != null) {
					return resultInvoicePlan;
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.UNEXPECTED_ERROR_STATUS_STR, Status.INTERNAL_SERVER_ERROR));
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateInvoicePlan() userID:" + userId + " companyId:" + companyId + " invoicePlan:" + invoicePlan);
		}
	}

	public static List<InvoicePlan> getInvoicePlans(String userId, String comapnyId) {
		Connection connection = null;
		try {
			LOGGER.debug("entered get getInvoicePlans() userID:" + userId + " companyId:" + comapnyId);
			if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(comapnyId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			connection = DatabaseUtilities.getReadConnection();
			return MySQLManager.getInvoicePlanDAOInstance().getAll(connection);
		} catch (Exception e) {
			LOGGER.error(e);
			DatabaseUtilities.closeConnection(connection);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			LOGGER.debug("exited get getInvoicePlans() userID:" + userId + " companyId:" + comapnyId);
		}
	}

	public static InvoicePlan getInvoicePlan(String invoicePlanId) {
		Connection connection = null;
		try {
			LOGGER.debug("entered getInvoicePlan() invoicePlanId:" + invoicePlanId);
			if (StringUtils.isEmpty(invoicePlanId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			InvoicePlan invoicePlan = new InvoicePlan();
			invoicePlan.setId(invoicePlanId);
			connection = DatabaseUtilities.getReadConnection();
			return MySQLManager.getInvoicePlanDAOInstance().get(connection,invoicePlan);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoicePlan() invoicePlanId:" + invoicePlanId);
		}

	}

	public static InvoicePlan deleteInvoicePlan(String invoicePlanId) {
		Connection connection = null;
		try {
			LOGGER.debug("entered deleteInvoicePlan() invoicePlanId:" + invoicePlanId);
			if (StringUtils.isEmpty(invoicePlanId)) {
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.PRECONDITION_FAILED_STR, Status.PRECONDITION_FAILED));
			}
			InvoicePlan invoicePlan = new InvoicePlan();
			invoicePlan.setId(invoicePlanId);
			connection = DatabaseUtilities.getReadWriteConnection();
			return MySQLManager.getInvoicePlanDAOInstance().delete(connection,invoicePlan);
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.INTERNAL_SERVER_ERROR));
		} finally {
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited deleteInvoicePlan() invoicePlanId:" + invoicePlanId);
		}
	}

}
