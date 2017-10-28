package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.Utilities;

public class Invoice_historyControllerImpl {

	private static Logger LOGGER = Logger.getLogger(Invoice_historyControllerImpl.class);

	public static InvoiceHistory getInvoice_history(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceHistory invoice_history = new InvoiceHistory();
			invoice_history.setId(id);
			invoice_history = MySQLManager.getInvoice_historyDAO().get(conn, invoice_history);
			return invoice_history;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("getInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}
	
	public static List<InvoiceHistory> getInvoice_historysByInvoiceid(String userId, String companyId, String invoiceId) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceHistory invoice_history = new InvoiceHistory();
			invoice_history.setInvoice_id(invoiceId);
			return MySQLManager.getInvoice_historyDAO().getAllByInvoiceId(conn, invoice_history);
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("getInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static List<InvoiceHistory> getInvoice_historys(String userId, String companyId) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceHistory invoice_history = new InvoiceHistory();
			invoice_history.setCreated_by(userId);
			invoice_history.setCompany_id(companyId);
			List<InvoiceHistory> invoice_historys = MySQLManager.getInvoice_historyDAO().getAll(conn, invoice_history);
			return invoice_historys;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_historys", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("getInvoice_historys", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response deleteInvoice_history(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			InvoiceHistory invoice_history = new InvoiceHistory();
			invoice_history.setId(id);
			invoice_history = MySQLManager.getInvoice_historyDAO().delete(conn, invoice_history);
			return Response.ok(invoice_history).build();
		} catch (WebApplicationException e) {
			LOGGER.error("deleteInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("deleteInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response deleteInvoice_historys(String userId, String companyId, List<String> ids) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			String commaSeparatedIds = Utilities.toQoutedCommaSeparatedString(ids);
			if (MySQLManager.getInvoice_historyDAO().deleteByIds(conn, commaSeparatedIds)) {
				JSONObject obj = new JSONObject();
				obj.put("deleted_ids", commaSeparatedIds);
				return Response.ok(obj.toString()).build();
			}
		} catch (WebApplicationException e) {
			LOGGER.error("deleteInvoice_historys", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("deleteInvoice_historys", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
		return Response.ok(Constants.FAILURE_STATUS_STR).status(Constants.EXPECTATION_FAILED).build();
	}

	public static Response createInvoice_history(String userId, String companyId, InvoiceHistory invoice_history) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			invoice_history.setId(UUID.randomUUID().toString());
			invoice_history.setCreated_by(userId);
			invoice_history.setCompany_id(companyId);
			String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
			invoice_history.setCreated_at(currentUtcDateStr);
			invoice_history.setLast_updated_by(userId);
			invoice_history.setLast_updated_at(currentUtcDateStr);
			invoice_history = MySQLManager.getInvoice_historyDAO().create(conn, invoice_history);
			return Response.ok(invoice_history).build();
		} catch (WebApplicationException e) {
			LOGGER.error("createInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("createInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response updateInvoice_history(String userId, String companyId, String id, InvoiceHistory invoice_history) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			invoice_history.setId(id);
			invoice_history.setCompany_id(companyId);
			String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
			invoice_history.setLast_updated_by(userId);
			invoice_history.setLast_updated_at(currentUtcDateStr);
			invoice_history = MySQLManager.getInvoice_historyDAO().update(conn, invoice_history);
			return Response.ok(invoice_history).build();
		} catch (WebApplicationException e) {
			LOGGER.error("updateInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("updateInvoice_history", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

}