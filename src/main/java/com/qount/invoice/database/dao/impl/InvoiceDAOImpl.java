package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceDAOImpl implements InvoiceDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceDAOImpl.class);

	private InvoiceDAOImpl() {
	}

	private static InvoiceDAOImpl invoiceDAOImpl = new InvoiceDAOImpl();

	public static InvoiceDAOImpl getInvoiceDAOImpl() {
		return invoiceDAOImpl;
	}

	private static final String INSERT_QRY = "insert into `invoice` (`id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`invoice_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`is_recurring`,`recurring_frequency`,`recurring_frequency_value`,`recurring_start_date`,`recurring_end_date`,`is_mails_automated`,`is_cc_current_user`,`payment_spring_customer_id`,`po_number`,`document_id`,`amount_due`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private static final String UPDATE_QRY = "UPDATE `invoice` SET `user_id` = ?,`company_id` = ?,`company_name` = ?,`amount` = ?,`currency` = ?,`description` = ?,`objectives` = ?,`last_updated_by` = ?,`last_updated_at` = ?,`first_name` = ?,`last_name` = ?,`state` = ?,`invoice_date` = ?,`acceptance_date` = ?,`acceptance_final_date` = ?,`notes` = ?,`item_id` = ?,`item_name` = ?,`coa_id` = ?,`coa_name` = ?,`discount` = ?,`deposit_amount` = ?,`processing_fees` = ?,`remainder_json` = ?,`remainder_mail_json`= ?,`is_recurring`,`recurring_frequency` = ?,`recurring_frequency_value` = ?,`recurring_start_date` = ?,`recurring_end_date` = ?,`is_mails_automated` = ?,`is_cc_current_user` = ?,`payment_spring_customer_id` = ?,`po_number` = ?,`document_id` = ?,`amount_due` = ? WHERE `id` = ?";
	private final static String DELETE_QRY = "DELETE FROM invoice WHERE `id`=?;";
	private final static String GET_QRY = "SELECT i.`id`,i.`user_id`,i.`company_id`,i.`company_name`,i.`amount`,i.`currency`,i.`description`,i.`objectives`,i.`last_updated_by`,i.`last_updated_at`,i.`first_name`,i.`last_name`,i.`state`, i.`invoice_date`,i.`acceptance_date`,i.`acceptance_final_date`,i.`notes`,i.`item_id`,i.`item_name`,i.`coa_id`,i.`coa_name`,i.`discount`,i.`deposit_amount`,i.`processing_fees`,i.`remainder_json`, i.`remainder_mail_json`,i.`is_recurring`,i.`recurring_frequency`,i.`recurring_frequency_value`,i.`recurring_start_date`,i.`recurring_end_date`,i.`is_mails_automated`,i.`is_cc_current_user`,i.`payment_spring_customer_id`,i.`po_number`,i.`document_id`,i.`amount_due`, il.`id` AS `ilid`,il.`invoice_id`,il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`currency` `il_currency`,il.`last_updated_by` `il_last_updated_by`,il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`, ilt.`invoice_line_id` `ilt_invoice_line_id`,ilt.`tax_id` `ilt_tax_id`,ilt.`tax_rate` `ilt_tax_rate` FROM `invoice` i LEFT JOIN `invoice_lines` il ON i.id=il.invoice_id LEFT JOIN `invoice_line_taxes` ilt ON il.id =ilt.invoice_line_id WHERE i.id = ?;";
	private final static String GET_INVOICES_LIST_QRY = "SELECT `id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`invoice_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`is_recurring`,`recurring_frequency`,`recurring_frequency_value`,`recurring_start_date`,`recurring_end_date`,`is_mails_automated`,`is_cc_current_user`,`payment_spring_customer_id`,`number`,`po_number`,`document_id`,`amount_due` FROM invoice WHERE `user_id`=?;";

	@Override
	public Invoice save(Connection connection, Invoice invoice) {
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(INSERT_QRY);
				pstmt.setString(ctr++, invoice.getId());
				pstmt.setString(ctr++, invoice.getUser_id());
				pstmt.setString(ctr++, invoice.getCompany_id());
				pstmt.setString(ctr++, invoice.getCompany_name());
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getFirst_name());
				pstmt.setString(ctr++, invoice.getLast_name());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getAcceptance_date());
				pstmt.setString(ctr++, invoice.getAcceptance_final_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setString(ctr++, invoice.getItem_id());
				pstmt.setString(ctr++, invoice.getItem_name());
				pstmt.setString(ctr++, invoice.getCoa_id());
				pstmt.setString(ctr++, invoice.getCoa_name());
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
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting proposal:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoice;
	}

	@Override
	public Invoice update(Connection connection, Invoice invoice) {
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(UPDATE_QRY);
				pstmt.setString(ctr++, invoice.getUser_id());
				pstmt.setString(ctr++, invoice.getCompany_id());
				pstmt.setString(ctr++, invoice.getCompany_name());
				pstmt.setDouble(ctr++, invoice.getAmount());
				pstmt.setString(ctr++, invoice.getCurrency());
				pstmt.setString(ctr++, invoice.getDescription());
				pstmt.setString(ctr++, invoice.getObjectives());
				pstmt.setString(ctr++, invoice.getLast_updated_by());
				pstmt.setString(ctr++, invoice.getLast_updated_at());
				pstmt.setString(ctr++, invoice.getFirst_name());
				pstmt.setString(ctr++, invoice.getLast_name());
				pstmt.setString(ctr++, invoice.getState());
				pstmt.setString(ctr++, invoice.getInvoice_date());
				pstmt.setString(ctr++, invoice.getAcceptance_date());
				pstmt.setString(ctr++, invoice.getAcceptance_final_date());
				pstmt.setString(ctr++, invoice.getNotes());
				pstmt.setString(ctr++, invoice.getItem_id());
				pstmt.setString(ctr++, invoice.getItem_name());
				pstmt.setString(ctr++, invoice.getCoa_id());
				pstmt.setString(ctr++, invoice.getCoa_name());
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
				pstmt.setString(ctr++, invoice.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record updated", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating proposal:" + invoice.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return invoice;
	}

	@Override
	public Invoice get(String invoiceID) {
		Invoice invoice = new Invoice();
		List<InvoiceLine> invoiceLines = new ArrayList<InvoiceLine>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				invoice.setInvoiceLines(invoiceLines);
				pstmt = connection.prepareStatement(GET_QRY);
				pstmt.setString(1, invoiceID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setId(rset.getString("ilid"));
					int invoiceLineIndex = invoice.getInvoiceLines().indexOf(invoiceLine);
					if (invoiceLineIndex != -1) {
						invoiceLine = invoice.getInvoiceLines().get(invoiceLineIndex);
						InvoiceLineTaxes invoiceLineTax = new InvoiceLineTaxes();
						invoiceLineTax.setInvoice_line_id(rset.getString("ilt_invoice_line_id"));
						invoiceLineTax.setTax_id(rset.getString("ilt_tax_id"));
						invoiceLineTax.setTax_rate(rset.getDouble("ilt_tax_rate"));
						invoiceLine.getInvoiceLineTaxes().add(invoiceLineTax);
					} else if (invoiceLineIndex == -1) {
						invoiceLine.setInvoice_id(rset.getString("invoice_id"));
						invoiceLine.setDescription(rset.getString("il_description"));
						invoiceLine.setObjectives(rset.getString("il_objectives"));
						invoiceLine.setAmount(rset.getDouble("il_amount"));
						invoiceLine.setCurrency(rset.getString("il_currency"));
						invoiceLine.setLast_updated_at(rset.getString("il_last_updated_at"));
						invoiceLine.setLast_updated_by(rset.getString("il_last_updated_by"));
						invoiceLine.setQuantity(rset.getLong("il_quantity"));
						invoiceLine.setPrice(rset.getDouble("il_price"));
						invoiceLine.setNotes(rset.getString("il_notes"));
						InvoiceLineTaxes invoiceLineTax = new InvoiceLineTaxes();
						invoiceLineTax.setInvoice_line_id(rset.getString("ilt_invoice_line_id"));
						invoiceLineTax.setTax_id(rset.getString("ilt_tax_id"));
						invoiceLineTax.setTax_rate(rset.getDouble("ilt_tax_rate"));
						List<InvoiceLineTaxes> invoicesLineTaxes = new ArrayList<InvoiceLineTaxes>();
						invoicesLineTaxes.add(invoiceLineTax);
						invoiceLine.setInvoiceLineTaxes(invoicesLineTaxes);
						invoice.getInvoiceLines().add(invoiceLine);
						if (StringUtils.isBlank(invoice.getId())) {
							invoice.setId(rset.getString("id"));
							invoice.setUser_id(rset.getString("user_id"));
							invoice.setCompany_id(rset.getString("company_id"));
							invoice.setCompany_name(rset.getString("company_name"));
							invoice.setAmount(rset.getDouble("amount"));
							invoice.setCurrency(rset.getString("currency"));
							invoice.setDescription(rset.getString("description"));
							invoice.setObjectives(rset.getString("objectives"));
							invoice.setLast_updated_by(rset.getString("last_updated_by"));
							invoice.setLast_updated_at(rset.getString("last_updated_at"));
							invoice.setFirst_name(rset.getString("first_name"));
							invoice.setLast_name(rset.getString("last_name"));
							invoice.setState(rset.getString("state"));
							invoice.setInvoice_date(rset.getString("invoice_date"));
							invoice.setAcceptance_date(rset.getString("acceptance_date"));
							invoice.setAcceptance_final_date(rset.getString("acceptance_final_date"));
							invoice.setNotes(rset.getString("notes"));
							invoice.setItem_id(rset.getString("item_id"));
							invoice.setItem_name(rset.getString("item_name"));
							invoice.setCoa_id(rset.getString("coa_id"));
							invoice.setCoa_name(rset.getString("coa_name"));
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
							invoice.setPayment_spring_customer_id(rset.getString("payment_spring_customer_id"));
							invoice.setPo_number(rset.getString("po_number"));
							invoice.setDocument_id(rset.getString("document_id"));
							invoice.setAmount_due(rset.getDouble("amount_due"));
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice for invoiceID [ " + invoiceID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoice;

	}

	@Override
	public List<Invoice> getInvoiceList(String userID) {
		List<Invoice> invoiceLst = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(GET_INVOICES_LIST_QRY);
				pstmt.setString(1, userID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Invoice invoice = new Invoice();
					invoice.setId(rset.getString("id"));
					invoice.setUser_id(rset.getString("user_id"));
					invoice.setCompany_id(rset.getString("company_id"));
					invoice.setCompany_name(rset.getString("company_name"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setCurrency(rset.getString("currency"));
					invoice.setDescription(rset.getString("description"));
					invoice.setObjectives(rset.getString("objectives"));
					invoice.setLast_updated_by(rset.getString("last_updated_by"));
					invoice.setLast_updated_at(rset.getString("last_updated_at"));
					invoice.setFirst_name(rset.getString("first_name"));
					invoice.setLast_name(rset.getString("last_name"));
					invoice.setState(rset.getString("state"));
					invoice.setInvoice_date(rset.getString("invoice_date"));
					invoice.setAcceptance_date(rset.getString("acceptance_date"));
					invoice.setAcceptance_final_date(rset.getString("acceptance_final_date"));
					invoice.setNotes(rset.getString("notes"));
					invoice.setItem_id(rset.getString("item_id"));
					invoice.setItem_name(rset.getString("item_name"));
					invoice.setCoa_id(rset.getString("coa_id"));
					invoice.setCoa_name(rset.getString("coa_name"));
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
					invoice.setPayment_spring_customer_id(rset.getString("payment_spring_customer_id"));
					invoice.setPo_number(rset.getString("po_number"));
					invoice.setDocument_id(rset.getString("document_id"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoiceLst.add(invoice);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoices for user_id [ " + userID + " ]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		return invoiceLst;

	}

	@Override
	public Invoice delete(Invoice invoice) {
		Connection connection = null;
		if (invoice == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
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
		}
		return invoice;
	}

}
