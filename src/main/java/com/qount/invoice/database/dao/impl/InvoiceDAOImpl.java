package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.model.Coa;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.Item;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

public class InvoiceDAOImpl implements InvoiceDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceDAOImpl.class);

	private InvoiceDAOImpl() {
	}

	private static InvoiceDAOImpl invoiceDAOImpl = new InvoiceDAOImpl();

	public static InvoiceDAOImpl getInvoiceDAOImpl() {
		return invoiceDAOImpl;
	}

	@Override
	public Invoice save(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered save(invoice):" + invoice);
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.INSERT_QRY);
				pstmt.setString(ctr++, invoice.getId());
				pstmt.setString(ctr++, invoice.getUser_id());
				pstmt.setString(ctr++, invoice.getCompany_id());
				pstmt.setString(ctr++, invoice.getCustomer_id());
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setDouble(ctr++, invoice.getDiscount());
				pstmt.setDouble(ctr++, invoice.getDeposit_amount());
				pstmt.setDouble(ctr++, invoice.getProcessing_fees());
				pstmt.setString(ctr++, invoice.getNumber());
				pstmt.setString(ctr++, invoice.getDocument_id());
				pstmt.setDouble(ctr++, invoice.getAmount_due());
				pstmt.setString(ctr++, invoice.getPayment_date());
				pstmt.setDouble(ctr++, invoice.getSub_totoal());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setString(ctr++, invoice.getCreated_at());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setLong(ctr++, new Date().getTime());
				pstmt.setString(ctr++, invoice.getRecepientsMailsArr().toString());
				pstmt.setString(ctr++, invoice.getPlan_id());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getPayment_options());
				pstmt.setString(ctr++, invoice.getEmail_state());
				pstmt.setString(ctr++, invoice.getSend_to());
				pstmt.setString(ctr++, invoice.getRefrence_number());
				pstmt.setString(ctr++, invoice.getPayment_method());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited save(invoice):" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice update(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered invoice update:" + invoice);
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.UPDATE_QRY);
				pstmt.setString(ctr++, invoice.getUser_id());
				pstmt.setString(ctr++, invoice.getCompany_id());
				pstmt.setString(ctr++, invoice.getCustomer_id());
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setDouble(ctr++, invoice.getDiscount());
				pstmt.setDouble(ctr++, invoice.getDeposit_amount());
				pstmt.setDouble(ctr++, invoice.getProcessing_fees());
				pstmt.setString(ctr++, invoice.getNumber());
				pstmt.setString(ctr++, invoice.getDocument_id());
				pstmt.setDouble(ctr++, invoice.getAmount_due());
				pstmt.setString(ctr++, invoice.getPayment_date());
				pstmt.setDouble(ctr++, invoice.getSub_totoal());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setString(ctr++, invoice.getRecepientsMailsArr()==null?null:invoice.getRecepientsMailsArr().toString());
				pstmt.setString(ctr++, invoice.getPlan_id());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getPayment_options());
				pstmt.setString(ctr++, invoice.getEmail_state());
				pstmt.setString(ctr++, invoice.getSend_to());
				pstmt.setString(ctr++, invoice.getRefrence_number());
				pstmt.setString(ctr++, invoice.getPayment_method());
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice update:" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice updateState(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered invoice updateSate:" + invoice);
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.UPDATE_STATE_QRY);
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice state:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice updateState:" + invoice);
		}
		return invoice;
	}
	
	@Override
	public Invoice updateInvoiceAsPaid(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered invoice updateSate:" + invoice);
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.UPDATE_INVOICE_AS_PAID_STATE_QRY);
				pstmt.setString(ctr++, invoice.getRefrence_number());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getPayment_method());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice state:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice updateState:" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice get(String invoiceID) throws Exception {
		LOGGER.debug("entered get by invoice id:" + invoiceID);
		Invoice invoice = null;
		Customer customer = null;
		List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_QRY);
				pstmt.setString(1, invoiceID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					if (invoice == null) {
						invoice = new Invoice();
						customer = new Customer();
						invoice.setCustomer(customer);
						invoice.setInvoiceLines(invoiceLines);
					}
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setId(rset.getString("il_id"));
					int invoiceLineIndex = invoice.getInvoiceLines().indexOf(invoiceLine);
					if (invoiceLineIndex == -1) {
						invoiceLine.setInvoice_id(rset.getString("il_invoice_id"));
						invoiceLine.setDescription(rset.getString("il_description"));
						Item item = new Item();
						item.setId(rset.getString("il_item_id"));
						item.setName(rset.getString("il_item_name"));
						invoiceLine.setItem(item);
						invoiceLine.setItem_id(item.getId());
						Coa coa = new Coa();
						coa.setId(rset.getString("il_item_id"));
						coa.setName(rset.getString("il_coa_name"));
						invoiceLine.setCoa(coa);
						invoiceLine.setObjectives(rset.getString("il_objectives"));
						invoiceLine.setTax_id(rset.getString("il_tax_id"));
						invoiceLine.setAmount(rset.getDouble("il_amount"));
						invoiceLine.setLast_updated_at(rset.getString("il_last_updated_at"));
						invoiceLine.setLast_updated_by(rset.getString("il_last_updated_by"));
						invoiceLine.setQuantity(rset.getLong("il_quantity"));
						invoiceLine.setPrice(rset.getDouble("il_price"));
						invoiceLine.setNotes(rset.getString("il_notes"));
						invoiceLine.setType(rset.getString("il_type"));
						invoice.getInvoiceLines().add(invoiceLine);
						if (StringUtils.isBlank(invoice.getId())) {
							invoice.setId(rset.getString("id"));
							invoice.setRefrence_number(rset.getString("refrence_number"));
							invoice.setPayment_method(rset.getString("payment_method"));
							invoice.setIs_recurring(rset.getBoolean("is_recurring"));
							invoice.setUser_id(rset.getString("user_id"));
							invoice.setCompany_id(rset.getString("company_id"));
							invoice.setCustomer_id(rset.getString("customer_id"));
							invoice.setAmount(rset.getDouble("amount"));
							invoice.setCurrency(rset.getString("currency"));
							invoice.setDescription(rset.getString("description"));
							invoice.setObjectives(rset.getString("objectives"));
							invoice.setLast_updated_by(rset.getString("last_updated_by"));
							invoice.setLast_updated_at(rset.getString("last_updated_at"));
							invoice.setState(rset.getString("state"));
							invoice.setInvoice_date(rset.getString("invoice_date"));
							invoice.setNotes(rset.getString("notes"));
							invoice.setDiscount(rset.getLong("discount"));
							invoice.setDeposit_amount(rset.getDouble("deposit_amount"));
							invoice.setProcessing_fees(rset.getDouble("processing_fees"));
							invoice.setNumber(rset.getString("number"));
							invoice.setDocument_id(rset.getString("document_id"));
							invoice.setAmount_due(rset.getDouble("amount_due"));
							invoice.setPayment_date(rset.getString("payment_date"));
							invoice.setSub_totoal(rset.getDouble("sub_totoal"));
							invoice.setAmount_by_date(rset.getDouble("amount_by_date"));
							invoice.setCreated_at(rset.getString("created_at"));
							invoice.setAmount_paid(rset.getDouble("amount_paid"));
							invoice.setTerm(rset.getString("term"));
							invoice.setRecepientsMails(CommonUtils.getListString(rset.getString("recepients_mails")));
							invoice.setPlan_id(rset.getString("plan_id"));
							invoice.setIs_recurring(rset.getBoolean("is_recurring"));
							invoice.setPayment_options(rset.getString("payment_options"));
							invoice.setEmail_state(rset.getString("email_state"));
							invoice.setSend_to(rset.getString("send_to"));
							customer.setCustomer_id(rset.getString("customer_id"));
							customer.setPayment_spring_id(rset.getString("payment_spring_id"));
							customer.setCustomer_name(rset.getString("customer_name"));
							customer.setCard_name(rset.getString("card_name"));
							Currencies currencies_2 = new Currencies();
							currencies_2.setCode(rset.getString("code"));
							currencies_2.setName(rset.getString("name"));
							currencies_2.setHtml_symbol(rset.getString("html_symbol"));
							currencies_2.setJava_symbol(rset.getString("java_symbol"));
							invoice.setCurrencies(currencies_2);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice for invoiceID [ " + invoiceID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get by invoice id:" + invoiceID);
		}
		return invoice;

	}

	@Override
	public Map<String, String> getCount(String userID, String companyID) throws Exception {
		LOGGER.debug("entered get count of invoice: userID" + userID + " companyID" + companyID);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("userID or companyID cannot be empty");
		}
		Map<String, String> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.QOUNT_QRY);
				pstmt.setString(1, companyID);
				pstmt.setString(2, userID);
				pstmt.setString(3, companyID);
				pstmt.setString(4, userID);
				pstmt.setString(5, companyID);
				pstmt.setString(6, userID);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					result = new HashMap<String, String>();
					result.put("invoice_paid", rset.getString("invoice_paid"));
					result.put("invoice_unpaid", rset.getString("invoice_unpaid"));
					result.put("proposal_count", rset.getString("proposal_count"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching count for invoice: userID" + userID + " companyID" + companyID, e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get count of invoice: userID" + userID + " companyID" + companyID);
		}
		return result;

	}

	@Override
	public List<Invoice> getInvoiceList(String userID, String companyID, String state) throws Exception {
		LOGGER.debug("entered getInvoiceList userID:" + userID + " companyID:" + companyID + "state:" + state);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("userID or companyID cannot be empty");
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_QRY;
				query += "`user_id`='" + userID + "' AND `company_id`= '" + companyID + "' ";
				if (!StringUtils.isEmpty(state)) {
					if (!state.equals("paid")) {
						query += "AND (state !='paid' OR state IS NULL );";
					} else {
						query += "AND state='paid'";
					}
				}
				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setNumber(rset.getString("number"));
					invoice.setCustomer_id(rset.getString("customer_id"));
					invoice.setId(rset.getString("id"));
					invoice.setInvoice_date(rset.getString("invoice_date"));
					invoice.setPayment_date(rset.getString("payment_date"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setState(rset.getString("state"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for user_id [ " + userID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoiceList userID:" + userID + " companyID:" + companyID + "state:" + state);
		}
		return invoiceLst;

	}

	@Override
	public Invoice delete(Invoice invoice) throws Exception {
		LOGGER.debug("entered invoice delete:" + invoice);
		Connection connection = null;
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.DELETE_QRY);
				pstmt.setString(1, invoice.getId());
				pstmt.setString(2, invoice.getUser_id());
				pstmt.setString(3, invoice.getCompany_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
				LOGGER.debug("no of invoice deleted:" + rowCount);
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record deleted:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice:" + invoice.getId() + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited invoice delete:" + invoice);
		}
		return invoice;
	}

	@Override
	public boolean deleteLst(String userId, String companyId, String lst) throws Exception {
		LOGGER.debug("entered invoice delete lst:" + lst);
		Connection connection = null;
		if (StringUtils.isEmpty(lst)) {
			return false;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.DELETE_LST_QRY;
				query +=lst+") AND `user_id` = '"+userId+"' AND `company_id` ='"+companyId+"';";
				pstmt = connection.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice deleted:" + rowCount);
				if (rowCount > 0) {
					return true;
				}else{
					return false;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record deleted:" + lst + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice lst:" + lst + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited invoice delete lst:" + lst);
		}
		return false;
	}

	@Override
	public boolean updateStateAsSent(String userId, String companyId, String lst) throws Exception {
		LOGGER.debug("entered updateStateAsSent lst:" + lst);
		Connection connection = null;
		if (StringUtils.isEmpty(lst) || StringUtils.isAnyBlank(companyId,userId)) {
			return false;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.UPDATE_AS_SENT_QRY;
				query +=lst+") AND `user_id` = '"+userId+"' AND `company_id` ='"+companyId+"';";
				pstmt = connection.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice updated:" + rowCount);
				if (rowCount > 0) {
					return true;
				}else{
					return false;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("no record updated:" + lst + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error updateStateAsSent lst:" + lst + ",  ", e);
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited updateStateAsSent lst:" + lst);
		}
		return false;
	}

	@Override
	public List<Invoice> getInvoiceListByClientId(String userID, String companyID, String clientID) throws Exception {
		LOGGER.debug("entered getInvoiceList userID:" + userID + " companyID:" + companyID + "clientID:" + clientID);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("userID or companyID cannot be empty");
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_QRY;
				query += "`user_id`='" + userID + "' AND `company_id`= '" + companyID + "' ";
				query += "AND `customer_id`= '" + clientID + "' ";
				
				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setNumber(rset.getString("number"));
					invoice.setId(rset.getString("id"));
					invoice.setInvoice_date(rset.getString("invoice_date"));
					invoice.setPayment_date(rset.getString("payment_date"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setState(rset.getString("state"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for user_id [ " + userID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoiceList userID:" + userID + " companyID:" + companyID + "cientID:" + clientID);
		}
		return invoiceLst;	}

}
