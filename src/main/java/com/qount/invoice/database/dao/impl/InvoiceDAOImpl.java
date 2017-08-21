package com.qount.invoice.database.dao.impl;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.model.Coa;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.CustomerContactDetails;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceMetrics;
import com.qount.invoice.model.Item;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
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
				pstmt.setString(ctr++, invoice.getDue_date());
				pstmt.setDouble(ctr++, invoice.getSub_total());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setString(ctr++, invoice.getCreated_at());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setLong(ctr++, new Date().getTime());
				pstmt.setString(ctr++, invoice.getRecepientsMailsArr() == null ? null : invoice.getRecepientsMailsArr().toString());
				pstmt.setString(ctr++, invoice.getPlan_id());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getPayment_options());
				pstmt.setString(ctr++, invoice.getEmail_state());
				pstmt.setString(ctr++, invoice.getSend_to());
				pstmt.setString(ctr++, invoice.getPayment_method());
				pstmt.setDouble(ctr++, invoice.getTax_amount());
				// below value only comes when proposal is accepted to invoice
				pstmt.setString(ctr++, invoice.getProposal_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted",Constants.DATABASE_ERROR_STATUS));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
				pstmt.setString(ctr++, invoice.getDue_date());
				pstmt.setDouble(ctr++, invoice.getSub_total());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setString(ctr++, invoice.getRecepientsMailsArr() == null ? null : invoice.getRecepientsMailsArr().toString());
				pstmt.setString(ctr++, invoice.getPlan_id());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getPayment_options());
				pstmt.setString(ctr++, invoice.getEmail_state());
				pstmt.setString(ctr++, invoice.getSend_to());
				pstmt.setString(ctr++, invoice.getPayment_method());
				pstmt.setDouble(ctr++, invoice.getTax_amount());
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", Constants.DATABASE_ERROR_STATUS));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", Constants.DATABASE_ERROR_STATUS));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice state:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice updateState:" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice markAsPaid(Connection connection, Invoice invoice) throws Exception {
		LOGGER.debug("entered invoice markAsPaid:" + invoice);
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.MARK_AS_PAID_QRY);
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setDouble(ctr++, invoice.getAmount_due());
				pstmt.setString(ctr++, invoice.getReference_number());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", Constants.DATABASE_ERROR_STATUS));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice markAsPaid:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice markAsPaid:" + invoice);
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
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", Constants.DATABASE_ERROR_STATUS));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating invoice state:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
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
		CustomerContactDetails customerContactDetails = null;
		Company company = null;
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
						customerContactDetails = new CustomerContactDetails();
						company = new Company();
						invoice.setCustomer(customer);
						invoice.setInvoiceLines(invoiceLines);
						invoice.setCustomerContactDetails(customerContactDetails);
						invoice.setCompany(company);
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
						invoiceLine.setQuantity(rset.getDouble("il_quantity"));
						invoiceLine.setPrice(rset.getDouble("il_price"));
						invoiceLine.setNotes(rset.getString("il_notes"));
						invoiceLine.setType(rset.getString("il_type"));
						invoice.getInvoiceLines().add(invoiceLine);
						if (StringUtils.isBlank(invoice.getId())) {
							invoice.setId(rset.getString("id"));
							invoice.setTax_amount(rset.getDouble("tax_amount"));
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
							invoice.setDue_date(rset.getString("due_date"));
							invoice.setSub_total(rset.getDouble("sub_totoal"));
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
							customer.setCustomer_address(rset.getString("customer_address"));
							customer.setCustomer_city(rset.getString("customer_city"));
							customer.setCustomer_country(rset.getString("customer_country"));
							customer.setCustomer_state(rset.getString("customer_state"));
							customer.setCustomer_ein(rset.getString("customer_ein"));
							customer.setCustomer_zipcode(rset.getString("customer_zipcode"));
							customer.setPhone_number(rset.getString("phone_number"));
							customer.setCoa(rset.getString("coa"));
							customer.setTerm(rset.getString("term"));
							customer.setFax(rset.getString("fax"));
							customer.setStreet_1(rset.getString("street_1"));
							customer.setStreet_2(rset.getString("street_2"));
							Currencies currencies_2 = new Currencies();
							currencies_2.setCode(rset.getString("code"));
							currencies_2.setName(rset.getString("name"));
							currencies_2.setHtml_symbol(rset.getString("html_symbol"));
							currencies_2.setJava_symbol(rset.getString("java_symbol"));
							customerContactDetails.setId(rset.getString("ccd_id"));
							customerContactDetails.setCustomer_id(rset.getString("ccd_customer_id"));
							customerContactDetails.setFirst_name(rset.getString("ccd_first_name"));
							customerContactDetails.setLast_name(rset.getString("ccd_last_name"));
							customerContactDetails.setMobile(rset.getString("ccd_mobile"));
							customerContactDetails.setEmail(rset.getString("ccd_email"));
							customerContactDetails.setOther(rset.getString("ccd_other"));
							company.setActive(rset.getBoolean("com_active"));
							company.setId(rset.getString("com_id"));
							company.setName(rset.getString("com_name"));
							company.setAddress(rset.getString("com_address"));
							company.setCity(rset.getString("com_city"));
							company.setContact_first_name(rset.getString("com_contact_first_name"));
							company.setContact_last_name(rset.getString("com_contact_last_name"));
							company.setCurrency(rset.getString("com_currency"));
							company.setEin(rset.getString("com_ein"));
							company.setEmail(rset.getString("com_email"));
							company.setCountry(rset.getString("com_country"));
							company.setPhone_number(rset.getString("com_phone_number"));
							company.setState(rset.getString("com_state"));
							company.setZipcode(rset.getString("com_zipcode"));
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
			throw new WebApplicationException("userID or companyID cannot be empty",Constants.INVALID_INPUT_STATUS);
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
				pstmt.setString(2, companyID);
				pstmt.setString(3, companyID);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					result = new HashMap<String, String>();
					result.put("invoice_count", rset.getString("invoice_count"));
					result.put("proposal_count", rset.getString("proposal_count"));
					result.put("payment_count", rset.getString("payment_count"));
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
	public boolean invoiceExists(Connection connection, String invoiceNumber, String companyId) throws Exception {
		LOGGER.debug("entered invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId);
		if (StringUtils.isAnyBlank(invoiceNumber, companyId)) {
			throw new WebApplicationException("invoiceNumber or companyID cannot be empty", Constants.INVALID_INPUT_STATUS);
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_INVOICE_BY_NUMBER);
				pstmt.setString(1, invoiceNumber);
				pstmt.setString(2, companyId);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					int count = rset.getInt("count");
					if (count > 0) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error executing invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId, e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("entered invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId);
		}
		return false;
	}

	@Override
	public boolean invoiceExists(Connection connection, String invoiceNumber, String companyId, String id) throws Exception {
		LOGGER.debug("entered invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId + " id:" + id);
		if (StringUtils.isAnyBlank(invoiceNumber, companyId, id)) {
			throw new WebApplicationException("invoiceNumber or companyID od invoiceId cannot be empty", Constants.INVALID_INPUT_STATUS);
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_INVOICE_BY_NUMBER_AND_ID);
				pstmt.setString(1, invoiceNumber);
				pstmt.setString(2, companyId);
				pstmt.setString(3, id);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					int count = rset.getInt("count");
					if (count > 0) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error executing invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId + " id:" + id, e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("entered invoiceExists: invoiceNumber" + invoiceNumber + " companyID" + companyId + " id:" + id);
		}
		return false;
	}

	@Override
	public List<Invoice> getInvoiceList(String userID, String companyID, String state) throws Exception {
		LOGGER.debug("entered getInvoiceList userID:" + userID + " companyID:" + companyID + "state:" + state);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("userID or companyID cannot be empty",Constants.INVALID_INPUT_STATUS);
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_QRY;
				query += "  invoice.`company_id`= '" + companyID + "' ";
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
					invoice.setId(rset.getString("id"));
					int index = invoiceLst.indexOf(invoice);
					if (index == -1) {
						invoice.setNumber(rset.getString("number"));
						invoice.setCustomer_id(rset.getString("customer_id"));
						invoice.setId(rset.getString("id"));
						invoice.setInvoice_date(rset.getString("invoice_date"));
						invoice.setDue_date(rset.getString("due_date"));
						invoice.setAmount(rset.getDouble("amount"));
						invoice.setCurrency(rset.getString("currency"));
						invoice.setState(rset.getString("state"));
						invoice.setAmount_due(rset.getDouble("amount_due"));
						invoice.setCustomer_name(rset.getString("customer_name"));
						invoiceLst.add(invoice);
					} else {
						invoice = invoiceLst.get(index);
					}
					String journalID = rset.getString("journal_id");
					if (StringUtils.isNoneBlank(journalID) && rset.getBoolean("isActive")) {
						invoice.setJournalID(journalID);
					}
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
				pstmt.setString(2, invoice.getCompany_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", Constants.DATABASE_ERROR_STATUS));
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
				query += lst + ")   AND `company_id` ='" + companyId + "';";
				pstmt = connection.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice deleted:" + rowCount);
				if (rowCount > 0) {
					return true;
				} else {
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
		if (StringUtils.isEmpty(lst) || StringUtils.isAnyBlank(companyId, userId)) {
			return false;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.UPDATE_AS_SENT_QRY;
				query += lst + ")   AND `company_id` ='" + companyId + "';";
				pstmt = connection.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice updated:" + rowCount);
				if (rowCount > 0) {
					return true;
				} else {
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
			throw new WebApplicationException("userID or companyID cannot be empty",Constants.INVALID_INPUT_STATUS);
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_QRY;
				query += " invoice.`company_id`= '" + companyID + "' ";
				query += "AND invoice.`customer_id`= '" + clientID + "' ";

				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setNumber(rset.getString("number"));
					invoice.setId(rset.getString("id"));

					invoice.setInvoice_date(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					invoice.setDue_date(getDateStringFromSQLDate(rset.getDate("due_date"), Constants.INVOICE_UI_DATE_FORMAT));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setState(rset.getString("state"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoice.setAmount_paid(rset.getDouble("amount_paid"));
					invoice.setDue_date(getDateStringFromSQLDate(rset.getDate("due_date"), Constants.INVOICE_UI_DATE_FORMAT));

					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			System.out.println("exception" + e);
			LOGGER.error("Error fetching invoices for user_id [ " + userID + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoiceList userID:" + userID + " companyID:" + companyID + "cientID:" + clientID);
		}
		return invoiceLst;
	}

	private String getDateStringFromSQLDate(java.sql.Date date, String format) {
		String dateStr = null;
		if (date != null && format != null) {
			try {
				if (StringUtils.isNoneBlank(format)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(format);
					Date utilDate = new Date(date.getTime());
					dateStr = dateFormat.format(utilDate);
				}
			} catch (Exception e) {
				// throw new
				// WebApplicationException(CommonUtils.constructResponse("cannot
				// parse date", 400));
			}
		}
		return dateStr;
	}

	public InvoiceMetrics getInvoiceMetrics(String companyID) throws Exception {
		LOGGER.debug("Fetching Box for company [ " + companyID + " ]");
		InvoiceMetrics invoiceMetrics = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		DecimalFormat df = new DecimalFormat("#.00");
		df.setRoundingMode(RoundingMode.CEILING);
		try {
			if (StringUtils.isNotBlank(companyID)) {
				connection = DatabaseUtilities.getReadConnection();
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_BOX_VALUES);
				pstmt.setString(1, companyID);
				pstmt.setString(2, companyID);
				pstmt.setString(3, companyID);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					invoiceMetrics = new InvoiceMetrics();
					invoiceMetrics.setAvgOutstandingAmount(df.format(rset.getDouble("avg_outstanding")));
					invoiceMetrics.setAvgReceivableDays(df.format(rset.getDouble("avg_rec_date")));
					invoiceMetrics.setInvoiceCount(df.format(rset.getDouble("invoice_count")));
					invoiceMetrics.setTotalReceivableAmount(df.format(rset.getDouble("total_due")));
					invoiceMetrics.setTotalPastDueAmount(df.format(rset.getDouble("total_past_due")));
					invoiceMetrics.setSentInvoices("0.00");
					invoiceMetrics.setOpenedInvoices("0.00");
					invoiceMetrics.setTotalReceivedLast30Days(df.format(rset.getDouble("received_amount")));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching box values", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, connection);
		}
		return invoiceMetrics;
	}

	@Override
	public List<Invoice> saveInvoice(Connection connection, List<Invoice> invoiceList) throws Exception {
		LOGGER.debug("entered saveInvoice:" + invoiceList);
		if (invoiceList == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.INSERT_QRY);
				Iterator<Invoice> invoiceItr = invoiceList.iterator();
				int ctr = 1;
				while (invoiceItr.hasNext()) {
					Invoice invoice = invoiceItr.next();
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
					pstmt.setString(ctr++, invoice.getDue_date());
					pstmt.setDouble(ctr++, invoice.getSub_total());
					pstmt.setDouble(ctr++, invoice.getAmount_by_date());
					pstmt.setString(ctr++, invoice.getCreated_at());
					pstmt.setDouble(ctr++, invoice.getAmount_paid());
					pstmt.setString(ctr++, invoice.getTerm());
					pstmt.setLong(ctr++, new Date().getTime());
					pstmt.setString(ctr++, invoice.getRecepientsMailsArr() == null ? null : invoice.getRecepientsMailsArr().toString());
					pstmt.setString(ctr++, invoice.getPlan_id());
					pstmt.setBoolean(ctr++, invoice.is_recurring());
					pstmt.setString(ctr++, invoice.getPayment_options());
					pstmt.setString(ctr++, invoice.getEmail_state());
					pstmt.setString(ctr++, invoice.getSend_to());
					pstmt.setString(ctr++, invoice.getPayment_method());
					pstmt.setDouble(ctr++, invoice.getTax_amount());
					pstmt.setString(ctr++, invoice.getProposal_id());
					ctr = 1;
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return invoiceList;
				} else {
					throw new WebApplicationException("unable to save invoice", Constants.DATABASE_ERROR_STATUS);
				}

			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice:" + e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited saveInvoice:" + invoiceList);
		}
		return invoiceList;
	}
	
	@Override
	public List<Invoice> getInvoices(String invoiceIds) throws Exception {
		LOGGER.debug("entered getInvoices invoiceIds:" + invoiceIds );
		if (StringUtils.isBlank(invoiceIds)) {
			throw new WebApplicationException("invoiceIds cannot be empty", Constants.INVALID_INPUT_STATUS);
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_BY_ID_QRY;
				query+=invoiceIds+")";
				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setId(rset.getString("id"));
					int index = invoiceLst.indexOf(invoice);
					if (index == -1) {
						invoice.setNumber(rset.getString("number"));
						invoice.setCustomer_id(rset.getString("customer_id"));
						invoice.setId(rset.getString("id"));
						invoice.setInvoice_date(rset.getString("invoice_date"));
						invoice.setDue_date(rset.getString("due_date"));
						invoice.setAmount(rset.getDouble("amount"));
						invoice.setCurrency(rset.getString("currency"));
						invoice.setState(rset.getString("state"));
						invoice.setAmount_due(rset.getDouble("amount_due"));
						invoiceLst.add(invoice);
					} else {
						invoice = invoiceLst.get(index);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for invoiceIds [ " + invoiceIds + " ]", e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoiceList invoiceIds:" + invoiceIds);
		}
		return invoiceLst;

	}

	public static void main(String[] args) throws Exception {
		Connection connection = DatabaseUtilities.getReadConnection();
		ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM `invoice_payments` WHERE `company_id` = 'a525bf7f-3017-40dd-9f1a-86e4386dfcb4'");
		while (resultSet.next()) {
			String paymentID = resultSet.getString("id");
			System.out.println("id = " + paymentID);
			JSONObject res = CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", paymentID).toString(), "yoda@qount.io", "a525bf7f-3017-40dd-9f1a-86e4386dfcb4");
			System.out.println(res);
		}
	}

}
