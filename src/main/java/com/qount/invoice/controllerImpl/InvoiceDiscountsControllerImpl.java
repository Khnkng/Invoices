
package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoiceDiscounts;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.Utilities;

public class InvoiceDiscountsControllerImpl {

	private static Logger LOGGER = Logger.getLogger(InvoiceDiscountsControllerImpl.class);

	public static InvoiceDiscounts getInvoice_discounts(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setId(id);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().get(conn, invoice_discounts);
			if (invoice_discounts == null) {
				throw new WebApplicationException(Utilities.constructResponse(Constants.FAILURE_STATUS_STR,
						"no records found", Status.EXPECTATION_FAILED));
			}
			invoice_discounts = ChangeDateFormat(invoice_discounts);
			return invoice_discounts;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_discounts", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("getInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	private static InvoiceDiscounts ChangeDateFormat(InvoiceDiscounts invoice_discounts) {
		try {
			if (invoice_discounts != null) {
				invoice_discounts.setCreated_at(convertTimeStampToString(invoice_discounts.getCreated_at(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
						Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice_discounts.setLast_updated_at(convertTimeStampToString(invoice_discounts.getLast_updated_at(), Constants.TIME_STATMP_TO_BILLS_FORMAT,
						Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(Utilities.getErrorStackTrace(e));
		}
		return invoice_discounts;
	}

	public static List<InvoiceDiscounts> getInvoice_discountss(String userId, String companyId) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setCreated_by(userId);
			invoice_discounts.setCompany_id(companyId);
			List<InvoiceDiscounts> invoice_discountss = MySQLManager.getInvoiceDiscountsDAO().getAll(conn,
					invoice_discounts);
			return invoice_discountss;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_discountss", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("getInvoice_discountss", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response deleteInvoice_discounts(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setId(id);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().delete(conn, invoice_discounts);
			return Response.ok(invoice_discounts).build();
		} catch (WebApplicationException e) {
			LOGGER.error("deleteInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("deleteInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response deleteAllDiscounts(String userId, String companyId) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (MySQLManager.getInvoiceDiscountsDAO().deleteAllDiscounts(conn, companyId)) {
				return Response.ok("all discounts are deleted for company").build();
			}
		} catch (WebApplicationException e) {
			LOGGER.error("deleteInvoice_discountss", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("deleteInvoice_discountss", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
		return Response.ok(Constants.FAILURE_STATUS_STR).status(Constants.EXPECTATION_FAILED).build();
	}

	public static Response createInvoice_discounts(String userId, String companyId,
			InvoiceDiscounts invoice_discounts) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			invoice_discounts.setId(UUID.randomUUID().toString());
			invoice_discounts.setCreated_by(userId);
			invoice_discounts.setCompany_id(companyId);
			String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
			invoice_discounts.setCreated_at(currentUtcDateStr);
			invoice_discounts.setLast_updated_by(userId);
			invoice_discounts.setLast_updated_at(currentUtcDateStr);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().create(conn, invoice_discounts);
			return Response.ok(invoice_discounts).build();
		} catch (WebApplicationException e) {
			LOGGER.error("createInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("createInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response updateInvoice_discounts(String userId, String companyId, String id,
			InvoiceDiscounts invoice_discounts) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			invoice_discounts.setId(id);
			invoice_discounts.setCompany_id(companyId);
			String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
			invoice_discounts.setLast_updated_by(userId);
			invoice_discounts.setLast_updated_at(currentUtcDateStr);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().update(conn, invoice_discounts);
			return Response.ok(invoice_discounts).build();
		} catch (WebApplicationException e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static String convertTimeStampToString(String dateStr, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			if (StringUtils.isNotBlank(dateStr))
				return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(Utilities.getErrorStackTrace(e));
		}
		return null;
	}

}