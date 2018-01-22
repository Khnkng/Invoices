package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceDiscountsDAO;
import com.qount.invoice.model.InvoiceDiscounts;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

import java.util.ArrayList;

public class InvoiceDiscountsDAOImpl implements InvoiceDiscountsDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceDiscountsDAOImpl.class);

	private InvoiceDiscountsDAOImpl() {
	}

	private static final InvoiceDiscountsDAOImpl Invoice_discountsdaoimpl = new InvoiceDiscountsDAOImpl();

	public static InvoiceDiscountsDAOImpl getInvoice_discountsDAOImpl() {
		return Invoice_discountsdaoimpl;
	}

	@Override
	public InvoiceDiscounts get(Connection conn, InvoiceDiscounts invoice_discounts) {
		LOGGER.debug("entered get:" + invoice_discounts);
		if (invoice_discounts == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		InvoiceDiscounts invoice_discounts_result = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.GET_QRY);
				pstmt.setString(1, invoice_discounts.getId());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					invoice_discounts_result = new InvoiceDiscounts();
					invoice_discounts_result.setId(rset.getString("id"));
					invoice_discounts_result.setName(rset.getString("name"));
					invoice_discounts_result.setDescription(rset.getString("description"));
					invoice_discounts_result.setType(rset.getString("type"));
					invoice_discounts_result.setValue(rset.getLong("value"));
					invoice_discounts_result.setCompany_id(rset.getString("company_id"));
					invoice_discounts_result.setCreated_by(rset.getString("created_by"));
					invoice_discounts_result.setCreated_at(rset.getString("created_at"));
					invoice_discounts_result.setLast_updated_by(rset.getString("last_updated_by"));
					invoice_discounts_result.setLast_updated_at(rset.getString("last_updated_at"));
					invoice_discounts_result.setDays(rset.getLong("days"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving invoice_discounts:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll:" + invoice_discounts);
		return invoice_discounts_result;
	}

	@Override
	public List<InvoiceDiscounts> getAll(Connection conn, InvoiceDiscounts input) {
		LOGGER.debug("entered getAll");
		List<InvoiceDiscounts> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<InvoiceDiscounts>();
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.GET_ALL_QRY);
				pstmt.setString(1, input.getCreated_by());
				pstmt.setString(2, input.getCompany_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceDiscounts invoiceDiscounts = new InvoiceDiscounts();
					invoiceDiscounts.setId(rset.getString("id"));
					invoiceDiscounts.setName(rset.getString("name"));
					invoiceDiscounts.setDescription(rset.getString("description"));
					invoiceDiscounts.setType(rset.getString("type"));
					invoiceDiscounts.setValue(rset.getLong("value"));
					invoiceDiscounts.setCompany_id(rset.getString("company_id"));
					invoiceDiscounts.setCreated_by(rset.getString("created_by"));
					invoiceDiscounts.setCreated_at(rset.getString("created_at"));
					invoiceDiscounts.setLast_updated_by(rset.getString("last_updated_by"));
					invoiceDiscounts.setLast_updated_at(rset.getString("last_updated_at"));
					invoiceDiscounts.setDays(rset.getLong("days"));
					result.add(invoiceDiscounts);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving all invoice_discounts", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll");
		return result;
	}

	@Override
	public InvoiceDiscounts delete(Connection conn, InvoiceDiscounts invoiceDiscounts) {
		LOGGER.debug("entered delete:" + invoiceDiscounts);
		if (invoiceDiscounts == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.DELETE_QRY);
				pstmt.setString(1, invoiceDiscounts.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice_discounts:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + invoiceDiscounts);
		return invoiceDiscounts;
	}

	@Override
	public boolean deleteAllDiscounts(Connection conn, String companyID) {
		LOGGER.debug("entered delete:" + companyID);
		if (StringUtils.isBlank(companyID)) {
			throw new WebApplicationException("Invalid input", Constants.INVALID_INPUT);
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.DELETE_BY_COMPANY_ID_QRY);
				pstmt.setString(1, companyID);
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return true;
				}
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice_discounts:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + companyID);
		return false;
	}

	@Override
	public InvoiceDiscounts create(Connection conn, InvoiceDiscounts invoiceDiscounts) {
		LOGGER.debug("entered create:" + invoiceDiscounts);
		if (invoiceDiscounts == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.INSERT_QRY);
				pstmt.setString(ctr++, invoiceDiscounts.getId());
				pstmt.setString(ctr++, invoiceDiscounts.getName());
				pstmt.setString(ctr++, invoiceDiscounts.getDescription());
				pstmt.setString(ctr++, invoiceDiscounts.getType());
				pstmt.setLong(ctr++, invoiceDiscounts.getValue());
				pstmt.setString(ctr++, invoiceDiscounts.getCompany_id());
				pstmt.setString(ctr++, invoiceDiscounts.getCreated_by());
				pstmt.setString(ctr++, invoiceDiscounts.getCreated_at());
				pstmt.setString(ctr++, invoiceDiscounts.getLast_updated_by());
				pstmt.setString(ctr++, invoiceDiscounts.getLast_updated_at());
				pstmt.setLong(ctr++, invoiceDiscounts.getDays());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record inserted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error inserting invoice_discounts:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited create:" + invoiceDiscounts);
		return invoiceDiscounts;
	}

	@Override
	public InvoiceDiscounts update(Connection conn, InvoiceDiscounts invoiceDiscounts) {
		LOGGER.debug("entered update:" + invoiceDiscounts);
		if (invoiceDiscounts == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.InvoiceDiscounts.UPDATE_QRY);
				pstmt.setString(ctr++, invoiceDiscounts.getName());
				pstmt.setString(ctr++, invoiceDiscounts.getDescription());
				pstmt.setString(ctr++, invoiceDiscounts.getType());
				pstmt.setLong(ctr++, invoiceDiscounts.getValue());
				pstmt.setString(ctr++, invoiceDiscounts.getCompany_id());
				pstmt.setString(ctr++, invoiceDiscounts.getLast_updated_by());
				pstmt.setString(ctr++, invoiceDiscounts.getLast_updated_at());
				pstmt.setLong(ctr++, invoiceDiscounts.getDays());
				pstmt.setString(ctr++, invoiceDiscounts.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record updated", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error updating invoice_discounts:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited update:" + invoiceDiscounts);
		return invoiceDiscounts;
	}

}
