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
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.model.InvoiceMail;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.model.Item;
import com.qount.invoice.pdf.InvoiceReference;
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
	public Invoice save(Connection connection, Invoice invoice) {
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
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getAcceptance_date());
				pstmt.setString(ctr++, invoice.getAcceptance_final_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setDouble(ctr++, invoice.getDiscount());
				pstmt.setDouble(ctr++, invoice.getDeposit_amount());
				pstmt.setDouble(ctr++, invoice.getProcessing_fees());
				pstmt.setString(ctr++, invoice.getRemainder_json());
				pstmt.setString(ctr++, invoice.getRemainder_mail_json());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getRecurring_frequency());
				pstmt.setDouble(ctr++, invoice.getRecurring_frequency_value());
				pstmt.setString(ctr++, invoice.getRecurring_start_date());
				pstmt.setString(ctr++, invoice.getRecurring_end_date());
				pstmt.setBoolean(ctr++, invoice.is_mails_automated());
				pstmt.setBoolean(ctr++, invoice.is_cc_current_user());
				pstmt.setString(ctr++, invoice.getPayment_spring_customer_id());
				pstmt.setString(ctr++, invoice.getPo_number());
				pstmt.setString(ctr++, invoice.getDocument_id());
				pstmt.setDouble(ctr++, invoice.getAmount_due());
				pstmt.setString(ctr++, invoice.getPayment_date());
				pstmt.setString(ctr++, invoice.getCustomer_id());
				pstmt.setDouble(ctr++, invoice.getSub_totoal());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setString(ctr++, invoice.getCreated_at());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getNumber());
				pstmt.setLong(ctr++, new Date().getTime());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setString(ctr++, invoice.getRecepientsMails());
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
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited save(invoice):" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice update(Connection connection, Invoice invoice) {
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
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getAcceptance_date());
				pstmt.setString(ctr++, invoice.getAcceptance_final_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setDouble(ctr++, invoice.getDiscount());
				pstmt.setDouble(ctr++, invoice.getDeposit_amount());
				pstmt.setDouble(ctr++, invoice.getProcessing_fees());
				pstmt.setString(ctr++, invoice.getRemainder_json());
				pstmt.setString(ctr++, invoice.getRemainder_mail_json());
				pstmt.setBoolean(ctr++, invoice.is_recurring());
				pstmt.setString(ctr++, invoice.getRecurring_frequency());
				pstmt.setDouble(ctr++, invoice.getRecurring_frequency_value());
				pstmt.setString(ctr++, invoice.getRecurring_start_date());
				pstmt.setString(ctr++, invoice.getRecurring_end_date());
				pstmt.setBoolean(ctr++, invoice.is_mails_automated());
				pstmt.setBoolean(ctr++, invoice.is_cc_current_user());
				pstmt.setString(ctr++, invoice.getPayment_spring_customer_id());
				pstmt.setString(ctr++, invoice.getPo_number());
				pstmt.setString(ctr++, invoice.getDocument_id());
				pstmt.setDouble(ctr++, invoice.getAmount_due());
				pstmt.setString(ctr++, invoice.getPayment_date());
				pstmt.setDouble(ctr++, invoice.getSub_totoal());
				pstmt.setString(ctr++, invoice.getCustomer_id());
				pstmt.setDouble(ctr++, invoice.getAmount_by_date());
				pstmt.setDouble(ctr++, invoice.getAmount_paid());
				pstmt.setString(ctr++, invoice.getNumber());
				pstmt.setString(ctr++, invoice.getTerm());
				pstmt.setString(ctr++, invoice.getRecepientsMails());
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
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice update:" + invoice);
		}
		return invoice;
	}
	
	@Override
	public Invoice updateState(Connection connection, Invoice invoice) {
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
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoice updateState:" + invoice);
		}
		return invoice;
	}

	@Override
	public Invoice get(String invoiceID) {
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
					invoiceLine.setId(rset.getString("ilid"));
					int invoiceLineIndex = invoice.getInvoiceLines().indexOf(invoiceLine);
					if (invoiceLineIndex != -1) {
						invoiceLine = invoice.getInvoiceLines().get(invoiceLineIndex);
						InvoiceLineTaxes invoiceLineTax = new InvoiceLineTaxes();
						invoiceLineTax.setInvoice_line_id(rset.getString("ilt_invoice_line_id"));
						invoiceLineTax.setTax_id(rset.getString("ilt_tax_id"));
						invoiceLineTax.setName(rset.getString("ilt_name"));
						invoiceLineTax.setTax_rate(rset.getDouble("ilt_tax_rate"));
						invoiceLine.getInvoiceLineTaxes().add(invoiceLineTax);
					} else if (invoiceLineIndex == -1) {
						invoiceLine.setInvoice_id(rset.getString("invoice_id"));
						invoiceLine.setDescription(rset.getString("il_description"));
						Item item = new Item();
						item.setId(rset.getString("il_item_id"));
						item.setName(rset.getString("il_item_name"));
						invoiceLine.setItem(item);
						invoiceLine.setItem_id(item.getId());
						Coa coa = new Coa();
						coa.setId(rset.getString("il_coa_id"));
						coa.setName(rset.getString("il_coa_name"));
						invoiceLine.setCoa(coa);
						invoiceLine.setObjectives(rset.getString("il_objectives"));
						invoiceLine.setAmount(rset.getDouble("il_amount"));
						invoiceLine.setLast_updated_at(rset.getString("il_last_updated_at"));
						invoiceLine.setLast_updated_by(rset.getString("il_last_updated_by"));
						invoiceLine.setQuantity(rset.getLong("il_quantity"));
						invoiceLine.setPrice(rset.getDouble("il_price"));
						invoiceLine.setNotes(rset.getString("il_notes"));
						InvoiceLineTaxes invoiceLineTax = new InvoiceLineTaxes();
						invoiceLineTax.setInvoice_line_id(rset.getString("ilt_invoice_line_id"));
						invoiceLineTax.setTax_id(rset.getString("ilt_tax_id"));
						invoiceLineTax.setTax_rate(rset.getDouble("ilt_tax_rate"));
						invoiceLineTax.setName(rset.getString("ilt_name"));
						ArrayList<InvoiceLineTaxes> invoicesLineTaxes = new ArrayList<InvoiceLineTaxes>();
						invoicesLineTaxes.add(invoiceLineTax);
						invoiceLine.setInvoiceLineTaxes(invoicesLineTaxes);
						invoice.getInvoiceLines().add(invoiceLine);
						if (StringUtils.isBlank(invoice.getId())) {
							invoice.setId(rset.getString("id"));
							invoice.setNumber(rset.getString("i_number"));
							invoice.setTerm(rset.getString("i_term"));
							invoice.setUser_id(rset.getString("user_id"));
							invoice.setCompany_id(rset.getString("company_id"));
							invoice.setAmount(rset.getDouble("amount"));
							invoice.setCurrency(rset.getString("currency"));
							invoice.setDescription(rset.getString("description"));
							invoice.setObjectives(rset.getString("objectives"));
							invoice.setLast_updated_by(rset.getString("last_updated_by"));
							invoice.setLast_updated_at(rset.getString("last_updated_at"));
							invoice.setCustomer_id(rset.getString("customer_id"));
							customer.setCustomer_id(rset.getString("customer_id"));
							invoice.setState(rset.getString("state"));
							invoice.setInvoice_date(rset.getString("invoice_date"));
							invoice.setAcceptance_date(rset.getString("acceptance_date"));
							invoice.setAcceptance_final_date(rset.getString("acceptance_final_date"));
							invoice.setNotes(rset.getString("notes"));
							invoice.setDiscount(rset.getDouble("discount"));
							invoice.setDeposit_amount(rset.getDouble("deposit_amount"));
							invoice.setProcessing_fees(rset.getDouble("processing_fees"));
							invoice.setRemainder_json(rset.getString("remainder_json"));
							invoice.setRemainder_mail_json(rset.getString("remainder_mail_json"));
							invoice.setIs_recurring(rset.getBoolean("is_recurring"));
							invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
							invoice.setRecurring_frequency_value(rset.getDouble("recurring_frequency_value"));
							invoice.setRecurring_start_date(rset.getString("recurring_start_date"));
							invoice.setRecurring_end_date(rset.getString("recurring_end_date"));
							invoice.setIs_mails_automated(rset.getBoolean("is_mails_automated"));
							invoice.setIs_cc_current_user(rset.getBoolean("is_cc_current_user"));
							invoice.setPayment_spring_customer_id(rset.getString("payment_spring_id"));
							invoice.setPo_number(rset.getString("po_number"));
							invoice.setDocument_id(rset.getString("document_id"));
							invoice.setAmount_due(rset.getDouble("amount_due"));
							invoice.setPayment_date(rset.getString("payment_date"));
							invoice.setSub_totoal(rset.getDouble("sub_totoal"));
							invoice.setAmount_by_date(rset.getDouble("amount_by_date"));
							invoice.setCreated_at(rset.getString("i_created_at"));
							invoice.setCompanyName(rset.getString("company_name"));
							invoice.setRecepientsMails(rset.getString("recepients_mails"));
							customer.setPayment_spring_id(rset.getString("payment_spring_id"));
							customer.setCustomer_name(rset.getString("customer_name"));
							customer.setEmail_ids(CommonUtils.getJsonArrayFromString(rset.getString("email_ids")));
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
			throw new WebApplicationException(e.getMessage());
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get by invoice id:" + invoiceID);
		}
		return invoice;

	}

	@Override
	public InvoiceMail getInvoiceMailDetails(String invoiceID) {
		LOGGER.debug("entered get invoice mail details by invoice id:" + invoiceID);
		InvoiceMail invoiceMail = new InvoiceMail();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_INVOICES_MAIL_QRY);
				pstmt.setString(1, invoiceID);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					invoiceMail.setInvoiceNumber(rset.getString("i_number"));
					invoiceMail.setCustomerName(rset.getString("cust_name"));
					invoiceMail.setInvocieDate(rset.getString("invoice_date"));
					invoiceMail.setCompanyName(rset.getString("comp_name"));
					invoiceMail.setCurrencyCode(rset.getString("i_currency"));
					invoiceMail.setAmount(rset.getDouble("i_amount"));
					invoiceMail.setAmount_by_date(rset.getDouble("i_amount_by_date"));
					invoiceMail.setInvoiceDueDate(rset.getString("i_acceptance_final_date"));
					invoiceMail.setCustomerEmails(CommonUtils.getJsonArrayFromString(rset.getString("cust_email_ids")));
					invoiceMail.setInvoiceCreatedAt(rset.getString("i_created_at"));
					invoiceMail.setCurrencyHtml_symbol(rset.getString("html_symbol"));
					invoiceMail.setRecepients_mails(rset.getString("recepients_mails"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice for invoiceID [ " + invoiceID + " ]", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get invoice mail details by invoice id:" + invoiceID);
		}
		return invoiceMail;

	}
	
	@Override
	public Map<String, String> getCount(String userID,String companyID) {
		LOGGER.debug("entered get count of invoice: userID" + userID+" companyID"+companyID);
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
					result = new HashMap<String,String>();
					result.put("invoice_paid",rset.getString("invoice_paid"));
					result.put("invoice_unpaid",rset.getString("invoice_unpaid"));
					result.put("proposal_count",rset.getString("proposal_count"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching count for invoice: userID" + userID+" companyID"+companyID, e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get count of invoice: userID" + userID+" companyID"+companyID);
		}
		return result;

	}

	@Override
	public List<Invoice> getInvoiceList(String userID, String companyID, String state) {
		LOGGER.debug("entered getInvoiceList userID:" + userID + " companyID:" + companyID+ "state:"+state);
		if (StringUtils.isEmpty(userID) || StringUtils.isEmpty(companyID)|| StringUtils.isEmpty(state)) {
			throw new WebApplicationException("userID or companyID or state cannot be empty");
		}
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				String query = SqlQuerys.Invoice.GET_INVOICES_LIST_QRY;
				query +="`user_id`='"+userID + "' AND `company_id`= '"+companyID+"' ";
				if(!state.equals("paid")){
					query +="AND (state !='paid' OR state IS NULL );";					
				}else{
					query +="AND state='paid'";
				}
				pstmt = connection.prepareStatement(query);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setPo_number(rset.getString("po_number"));
					invoice.setInvoice_date(rset.getString("invoice_date"));
					invoice.setPayment_date(rset.getString("payment_date"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setLast_updated_by(rset.getString("last_updated_by"));
					invoice.setLast_updated_at(rset.getString("last_updated_at"));
					invoice.setCustomer_id(rset.getString("customer_id"));
					invoice.setState(rset.getString("state"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoice.setSub_totoal(rset.getDouble("sub_totoal"));
					invoice.setAmount_by_date(rset.getDouble("amount_by_date"));
					invoice.setId(rset.getString("id"));
					// invoice.setUser_id(rset.getString("user_id"));
					// invoice.setCompany_id(rset.getString("company_id"));
					// invoice.setDescription(rset.getString("description"));
					// invoice.setObjectives(rset.getString("objectives"));
					// invoice.setAcceptance_date(rset.getString("acceptance_date"));
					// invoice.setAcceptance_final_date(rset.getString("acceptance_final_date"));
					// invoice.setNotes(rset.getString("notes"));
					// invoice.setDiscount(rset.getDouble("discount"));
					// invoice.setDeposit_amount(rset.getDouble("deposit_amount"));
					// invoice.setProcessing_fees(rset.getDouble("processing_fees"));
					// invoice.setRemainder_json(rset.getString("remainder_json"));
					// invoice.setRemainder_mail_json(rset.getString("remainder_mail_json"));
					// invoice.setIs_recurring(rset.getBoolean("is_recurring"));
					// invoice.setRecurring_frequency(rset.getString("recurring_frequency"));
					// invoice.setRecurring_frequency_value(rset.getDouble("recurring_frequency_value"));
					// invoice.setRecurring_start_date(rset.getString("recurring_start_date"));
					// invoice.setRecurring_end_date(rset.getString("recurring_end_date"));
					// invoice.setIs_mails_automated(rset.getBoolean("is_mails_automated"));
					// invoice.setIs_cc_current_user(rset.getBoolean("is_cc_current_user"));
					// invoice.setPayment_spring_customer_id(rset.getString("payment_spring_customer_id"));
					// invoice.setDocument_id(rset.getString("document_id"));
					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for user_id [ " + userID + " ]", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited getInvoiceList userID:" + userID + " companyID:" + companyID+ "state:"+state);
		}
		return invoiceLst;

	}

	@Override
	public Invoice delete(Invoice invoice) {
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
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited invoice delete:" + invoice);
		}
		return invoice;
	}

	@Override
	public InvoiceReference getInvoiceRelatedDetails(Connection connection, InvoiceReference invoiceReference) {
		if (invoiceReference == null) {
			return null;
		}
		String companyId = invoiceReference.getCompany().getId();
		String customerId = invoiceReference.getCustomer().getCustomer_id();
		InvoicePreference invoicePreference = invoiceReference.getInvoicePreference();
		Company company = invoiceReference.getCompany();
		Customer customer = invoiceReference.getCustomer();
		if (StringUtils.isEmpty(companyId) || StringUtils.isEmpty(customerId) || invoicePreference == null || invoiceReference == null || company == null || customer == null) {
			return invoiceReference;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_INVOICES_PDF_QRY);
				pstmt.setString(1, companyId);
				pstmt.setString(2, customerId);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					invoicePreference.setItems(rset.getString("items"));
					invoicePreference.setUnits(rset.getString("units"));
					invoicePreference.setPrice(rset.getString("price"));
					invoicePreference.setAmount(rset.getString("amount"));
					invoicePreference.setDefaultTitle(rset.getString("default_title"));
					invoiceReference.setInvoiceType(rset.getString("template_type"));
					invoicePreference.setDefaultSubHeading(rset.getString("default_sub_heading"));
					company.setName(rset.getString("name"));
					company.setAddress(rset.getString("address"));
					company.setCity(rset.getString("city"));
					company.setState(rset.getString("state"));
					company.setCountry(rset.getString("country"));
					company.setPhone_number(rset.getString("phone_number"));
					customer.setCustomer_name(rset.getString("customer_name"));
					customer.setEmail_ids(CommonUtils.getJsonArrayFromString(rset.getString("email_ids")));
					invoicePreference.setStandardMemo(rset.getString("standard_memo"));
					invoicePreference.setDefaultFooter(rset.getString("default_footer"));
				}else{
					return null;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching InvoiceRelatedDetails", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoiceReference;
	}

}
