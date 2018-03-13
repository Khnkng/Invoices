package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.Invoice_historyDAO;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

public class Invoice_historyDAOImpl implements Invoice_historyDAO {

	private static Logger LOGGER = Logger.getLogger(Invoice_historyDAOImpl.class);

	private Invoice_historyDAOImpl() {
	}

	private static final Invoice_historyDAOImpl Invoice_historydaoimpl = new Invoice_historyDAOImpl();

	public static Invoice_historyDAOImpl getInvoice_historyDAOImpl() {
		return Invoice_historydaoimpl;
	}

	@Override
	public InvoiceHistory get(Connection conn, InvoiceHistory invoice_history) {
		LOGGER.debug("entered get:" + invoice_history);
		if (invoice_history == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.GET_QRY);
				pstmt.setString(1, invoice_history.getId());
				rset = pstmt.executeQuery();
				while (rset.next()) { 
					invoice_history.setCurrency(rset.getString("currency"));
					invoice_history.setSub_totoal(rset.getDouble("sub_totoal"));
					invoice_history.setAmount_by_date(rset.getDouble("amount_by_date"));
					invoice_history.setAmount_paid(rset.getDouble("amount_paid"));
					invoice_history.setTax_amount(rset.getDouble("tax_amount"));
					invoice_history.setAmount_due(rset.getDouble("amount_due"));
					invoice_history.setAmount(rset.getDouble("amount"));
					invoice_history.setWebhook_event_id(rset.getString("webhook_event_id"));
					invoice_history.setDescription(rset.getString("description"));
					invoice_history.setId(rset.getString("id"));
					invoice_history.setInvoice_id(rset.getString("invoice_id"));
					invoice_history.setUser_id(rset.getString("user_id"));
					invoice_history.setAction(rset.getString("action"));
					invoice_history.setAction_at(rset.getString("action_at"));
					invoice_history.setCompany_id(rset.getString("company_id"));
					invoice_history.setEmail_to(rset.getString("email_to"));
					invoice_history.setEmail_subject(rset.getString("email_subject"));
					invoice_history.setEmail_from(rset.getString("email_from"));
					invoice_history.setCreated_by(rset.getString("created_by"));
					invoice_history.setCreated_at(rset.getString("created_at"));
					invoice_history.setLast_updated_by(rset.getString("last_updated_by"));
					invoice_history.setLast_updated_at(rset.getString("last_updated_at"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll:" + invoice_history);
		return invoice_history;
	}
	
	@Override
	public String getByWebhookId(Connection conn, String webhookId) {
		LOGGER.debug("entered getByWebhookId:" + webhookId);
		if (StringUtils.isBlank(webhookId)) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.GET_BY_WEBHOOK_ID_QRY);
				pstmt.setString(1, webhookId);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					String id = rset.getString("id");
					LOGGER.debug("result id:"+id);
					return id;
				}else{
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving getByWebhookId:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getByWebhookId:" + webhookId);
		return null;
	}
	
	
	@Override
	public String getByInvoiceidAndAction(Connection conn, String invoiceId, String action) {
		LOGGER.debug("entered getByInvoiceidAndState:" + invoiceId +" state:"+action);
		if (StringUtils.isAnyBlank(invoiceId,action)) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.GET_BY_INVOICE_AND_ACTION_ID_QRY);
				pstmt.setString(1, invoiceId);
				pstmt.setString(2, action);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					String id = rset.getString("id");
					LOGGER.debug("result id:"+id);
					return id;
				}else{
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving getByInvoiceidAndState:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getByInvoiceidAndState:" + invoiceId +" state:"+action);
		return null;
	}

	@Override
	public List<InvoiceHistory> getAll(Connection conn, InvoiceHistory input) {
		LOGGER.debug("entered getAll");
		List<InvoiceHistory> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<InvoiceHistory>();
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.GET_ALL_QRY);
				pstmt.setString(1, input.getCreated_by());
				pstmt.setString(2, input.getCompany_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceHistory invoice_history = new InvoiceHistory();
					invoice_history.setAmount(rset.getDouble("amount"));
					invoice_history.setId(rset.getString("id"));
					invoice_history.setInvoice_id(rset.getString("invoice_id"));
					invoice_history.setUser_id(rset.getString("user_id"));
					invoice_history.setAction(rset.getString("action"));
					invoice_history.setAction_at(rset.getString("action_at"));
					invoice_history.setCompany_id(rset.getString("company_id"));
					invoice_history.setEmail_to(rset.getString("email_to"));
					invoice_history.setEmail_subject(rset.getString("email_subject"));
					invoice_history.setEmail_from(rset.getString("email_from"));
					invoice_history.setCreated_by(rset.getString("created_by"));
					invoice_history.setCreated_at(rset.getString("created_at"));
					invoice_history.setLast_updated_by(rset.getString("last_updated_by"));
					invoice_history.setLast_updated_at(rset.getString("last_updated_at"));
					result.add(invoice_history);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving all invoice_history", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll");
		return result;
	}
	
	@Override
	public List<InvoiceHistory> getAllByInvoiceId(Connection conn, InvoiceHistory input) {
		LOGGER.debug("entered getAllByInvoiceId:"+input);
		List<InvoiceHistory> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<InvoiceHistory>();
				String query = SqlQuerys.Invoice_history.GET_ALL_BY_INVOICE_ID_WTIH_LIMITED_ACTION_QRY.replace("?", "'"+input.getInvoice_id()+"'");
				query+=SqlQuerys.Invoice_history.LIMITED_ACTIONS+") OR `action` LIKE '%Late fee%' ) ORDER BY `action_at_mills` ASC";
				pstmt = conn.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceHistory invoice_history = new InvoiceHistory();
					invoice_history.setAction_at_mills(rset.getLong("action_at_mills"));
					invoice_history.setCurrency(rset.getString("currency"));
					invoice_history.setSub_totoal(rset.getDouble("sub_totoal"));
					invoice_history.setAmount_by_date(rset.getDouble("amount_by_date"));
					invoice_history.setAmount_paid(rset.getDouble("amount_paid"));
					invoice_history.setTax_amount(rset.getDouble("tax_amount"));
					invoice_history.setAmount_due(rset.getDouble("amount_due"));
					invoice_history.setAmount(rset.getDouble("amount"));
					invoice_history.setWebhook_event_id(rset.getString("webhook_event_id"));
					invoice_history.setDescription(rset.getString("description"));
					invoice_history.setId(rset.getString("id"));
					invoice_history.setInvoice_id(rset.getString("invoice_id"));
					invoice_history.setUser_id(rset.getString("user_id"));
					invoice_history.setAction(rset.getString("action"));
					invoice_history.setAction_at(rset.getString("action_at"));
					invoice_history.setCompany_id(rset.getString("company_id"));
					invoice_history.setEmail_to(rset.getString("email_to"));
					invoice_history.setEmail_subject(rset.getString("email_subject"));
					invoice_history.setEmail_from(rset.getString("email_from"));
					invoice_history.setCreated_by(rset.getString("created_by"));
					invoice_history.setCreated_at(rset.getString("created_at"));
					invoice_history.setLast_updated_by(rset.getString("last_updated_by"));
					invoice_history.setLast_updated_at(rset.getString("last_updated_at"));
					result.add(invoice_history);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving all invoice_history", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAllByInvoiceId input:"+input);
		return result;
	}

	@Override
	public InvoiceHistory delete(Connection conn, InvoiceHistory invoice_history) {
		LOGGER.debug("entered delete:" + invoice_history);
		if (invoice_history == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.DELETE_QRY);
				pstmt.setString(1, invoice_history.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + invoice_history);
		return invoice_history;
	}

	@Override
	public boolean deleteByIds(Connection conn, String commaSeparatedIds) {
		LOGGER.debug("entered delete:" + commaSeparatedIds);
		if (StringUtils.isBlank(commaSeparatedIds)) {
			throw new WebApplicationException("Invalid input", Constants.INVALID_INPUT);
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				String query = SqlQuerys.Invoice_history.DELETE_BY_IDS_QRY;
				query += commaSeparatedIds + ");";
				pstmt = conn.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return true;
				}
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + commaSeparatedIds);
		return false;
	}

	@Override
	public InvoiceHistory create(Connection conn, InvoiceHistory invoice_history) {
		LOGGER.debug("entered create:" + invoice_history);
		if (invoice_history == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				if(StringUtils.isNotBlank(getByWebhookId(conn, invoice_history.getWebhook_event_id()))){
					throw new WebApplicationException(PropertyManager.getProperty("invoice.history.webhook.event.already.stored")+":"+invoice_history.getWebhook_event_id(), Constants.INVALID_INPUT_STATUS);
				}
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.INSERT_QRY);
				pstmt.setLong(ctr++, invoice_history.getAction_at_mills());
				pstmt.setString(ctr++, invoice_history.getCurrency());
				pstmt.setDouble(ctr++, invoice_history.getSub_totoal());
				pstmt.setDouble(ctr++, invoice_history.getAmount_by_date());
				pstmt.setDouble(ctr++, invoice_history.getAmount_paid());
				pstmt.setDouble(ctr++, invoice_history.getTax_amount());
				pstmt.setDouble(ctr++, invoice_history.getAmount_due());
				pstmt.setDouble(ctr++, invoice_history.getAmount());
				pstmt.setString(ctr++, invoice_history.getWebhook_event_id());
				pstmt.setString(ctr++, invoice_history.getDescription());
				pstmt.setString(ctr++, invoice_history.getId());
				pstmt.setString(ctr++, invoice_history.getInvoice_id());
				pstmt.setString(ctr++, invoice_history.getUser_id());
				pstmt.setString(ctr++, invoice_history.getAction());
				pstmt.setString(ctr++, invoice_history.getAction_at());
				pstmt.setString(ctr++, invoice_history.getCompany_id());
				pstmt.setString(ctr++, invoice_history.getEmail_to());
				pstmt.setString(ctr++, invoice_history.getEmail_subject());
				pstmt.setString(ctr++, invoice_history.getEmail_from());
				pstmt.setString(ctr++, invoice_history.getCreated_by());
				pstmt.setString(ctr++, invoice_history.getCreated_at());
				pstmt.setString(ctr++, invoice_history.getLast_updated_by());
				pstmt.setString(ctr++, invoice_history.getLast_updated_at());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record inserted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error inserting invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited create:" + invoice_history);
		return invoice_history;
	}
	

	@Override
	public List<InvoiceHistory> create(Connection conn, List<InvoiceHistory> invoice_histories) {
		LOGGER.debug("entered create:" + invoice_histories);
		if (invoice_histories == null || invoice_histories.isEmpty()) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				Iterator<InvoiceHistory> invoice_historyItr = invoice_histories.iterator();
				while(invoice_historyItr.hasNext()){
					InvoiceHistory invoice_history = invoice_historyItr.next();
					if(StringUtils.isNotBlank(getByWebhookId(conn, invoice_history.getWebhook_event_id()))){
						throw new WebApplicationException(PropertyManager.getProperty("invoice.history.webhook.event.already.stored")+":"+invoice_history.getWebhook_event_id(), Constants.INVALID_INPUT_STATUS);
					}
					int ctr = 1;
					pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.INSERT_QRY);
					pstmt.setLong(ctr++, invoice_history.getAction_at_mills());
					pstmt.setString(ctr++, invoice_history.getCurrency());
					pstmt.setDouble(ctr++, invoice_history.getSub_totoal());
					pstmt.setDouble(ctr++, invoice_history.getAmount_by_date());
					pstmt.setDouble(ctr++, invoice_history.getAmount_paid());
					pstmt.setDouble(ctr++, invoice_history.getTax_amount());
					pstmt.setDouble(ctr++, invoice_history.getAmount_due());
					pstmt.setDouble(ctr++, invoice_history.getAmount());
					pstmt.setString(ctr++, invoice_history.getWebhook_event_id());
					pstmt.setString(ctr++, invoice_history.getDescription());
					pstmt.setString(ctr++, invoice_history.getId());
					pstmt.setString(ctr++, invoice_history.getInvoice_id());
					pstmt.setString(ctr++, invoice_history.getUser_id());
					pstmt.setString(ctr++, invoice_history.getAction());
					pstmt.setString(ctr++, invoice_history.getAction_at());
					pstmt.setString(ctr++, invoice_history.getCompany_id());
					pstmt.setString(ctr++, invoice_history.getEmail_to());
					pstmt.setString(ctr++, invoice_history.getEmail_subject());
					pstmt.setString(ctr++, invoice_history.getEmail_from());
					pstmt.setString(ctr++, invoice_history.getCreated_by());
					pstmt.setString(ctr++, invoice_history.getCreated_at());
					pstmt.setString(ctr++, invoice_history.getLast_updated_by());
					pstmt.setString(ctr++, invoice_history.getLast_updated_at());
					pstmt.addBatch();
				}
				LOGGER.debug("query:"+pstmt.toString());
				int[] rowCount = pstmt.executeBatch();
				if (rowCount == null || rowCount.length==0) {
					throw new WebApplicationException("No invoice history inserted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error inserting invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited create:" + invoice_histories);
		return invoice_histories;
	}
	
	@Override
	public InvoiceHistory update(Connection conn, InvoiceHistory invoice_history) {
		LOGGER.debug("entered update:" + invoice_history);
		if (invoice_history == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.Invoice_history.UPDATE_QRY);
				pstmt.setDouble(ctr++, invoice_history.getAmount());
				pstmt.setString(ctr++, invoice_history.getWebhook_event_id());
				pstmt.setString(ctr++, invoice_history.getDescription());
				pstmt.setString(ctr++, invoice_history.getInvoice_id());
				pstmt.setString(ctr++, invoice_history.getUser_id());
				pstmt.setString(ctr++, invoice_history.getAction());
				pstmt.setString(ctr++, invoice_history.getAction_at());
				pstmt.setString(ctr++, invoice_history.getCompany_id());
				pstmt.setString(ctr++, invoice_history.getEmail_to());
				pstmt.setString(ctr++, invoice_history.getEmail_subject());
				pstmt.setString(ctr++, invoice_history.getEmail_from());
				pstmt.setString(ctr++, invoice_history.getLast_updated_by());
				pstmt.setString(ctr++, invoice_history.getLast_updated_at());
				pstmt.setString(ctr++, invoice_history.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record updated", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error updating invoice_history:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited update:" + invoice_history);
		return invoice_history;
	}
	
	public static void main(String[] args) {
		InvoiceHistory input = new InvoiceHistory();
		input.setInvoice_id("57d3e3e3-fdd1-4ddc-90fe-1e059a11b0de");
		String query = SqlQuerys.Invoice_history.GET_ALL_BY_INVOICE_ID_WTIH_LIMITED_ACTION_QRY.replace("?", "'"+input.getInvoice_id()+"'");
		query+=SqlQuerys.Invoice_history.LIMITED_ACTIONS+") ORDER BY `action_at` ASC";
		System.out.println(query);
	}
	
}