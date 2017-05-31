package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoicePaymentDAO;
import com.qount.invoice.model.InvoicePayment;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * DAOImpl for InvoicePreferenceDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 22 May 2017
 *
 */
public class InvoicePaymentDAOImpl implements InvoicePaymentDAO {

	private static Logger LOGGER = Logger.getLogger(InvoicePaymentDAOImpl.class);

	private InvoicePaymentDAOImpl() {
	}

	private static InvoicePaymentDAOImpl invoicePaymentDAOImpl = new InvoicePaymentDAOImpl();

	public static InvoicePaymentDAOImpl getInvoicePaymentDAOImpl() {
		return invoicePaymentDAOImpl;
	}

	@Override
	public InvoicePayment save(Connection connection, InvoicePayment invoicePayment) {
		LOGGER.debug("entered invoicePayment save():" + invoicePayment);
		if (invoicePayment == null || StringUtils.isBlank(invoicePayment.getId()) || StringUtils.isBlank(invoicePayment.getInvoice_id())) {
			throw new WebApplicationException("invoice payment without invocie id cannot be inserted");
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				int ctr = 1;
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePayment.INSERT_QRY);
				pstmt.setString(ctr++, invoicePayment.getId());
				pstmt.setString(ctr++, invoicePayment.getInvoice_id());
				pstmt.setString(ctr++, invoicePayment.getTransaction_id());
				pstmt.setDouble(ctr++, invoicePayment.getAmount());
				pstmt.setString(ctr++, invoicePayment.getTransaction_date());
				pstmt.setString(ctr++, invoicePayment.getStatus());
				pstmt.setString(ctr++, invoicePayment.getPeriod_start());
				pstmt.setString(ctr++, invoicePayment.getPeriod_end());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice payment:" + invoicePayment.getId() + ", invoiceId :" + invoicePayment.getInvoice_id(), e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("exited invoicePayment save():" + invoicePayment);
		}
		return invoicePayment;
	}

	@Override
	public InvoicePayment getById(InvoicePayment invoicePayment) {
		LOGGER.debug("entered get invoice Payment by id:" + invoicePayment.getId());
		if (null == invoicePayment || StringUtils.isBlank(invoicePayment.getId())) {
			throw new WebApplicationException("invoice payment id is mandatory to retrieve invoice payments");
		}

		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePayment.GET_BY_ID_QRY);
				pstmt.setString(1, invoicePayment.getId());
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					invoicePayment.setAmount(rset.getLong("amount"));
					invoicePayment.setId(rset.getString("id"));
					invoicePayment.setInvoice_id(rset.getString("invoice_id"));
					invoicePayment.setPeriod_end(rset.getString("period_end"));
					invoicePayment.setPeriod_start(rset.getString("period_start"));
					invoicePayment.setStatus(rset.getString("status"));
					invoicePayment.setTransaction_date(rset.getString("transaction_date"));
					invoicePayment.setTransaction_id(rset.getString("transaction_id"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice payment for ID [ " + invoicePayment.getId() + " ]", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get invoice Payment by id:" + invoicePayment.getId());
		}
		return invoicePayment;
	}

	@Override
	public List<InvoicePayment> getByInvoiceId(InvoicePayment input) {
		LOGGER.debug("entered get invoice Payment by invoice id:" + input.getInvoice_id());
		if (null == input || StringUtils.isBlank(input.getInvoice_id())) {
			throw new WebApplicationException("invocie id is mandatory to retrieve invoice payments");
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		List<InvoicePayment> result = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePayment.GET_BY_INVOCIE_ID_QRY);
				pstmt.setString(1, input.getInvoice_id());
				rset = pstmt.executeQuery();
				if (rset != null) {
					result = new ArrayList<InvoicePayment>();
					while (rset.next()) {
						InvoicePayment invoicePayment = new InvoicePayment();
						invoicePayment.setAmount(rset.getLong("amount"));
						invoicePayment.setId(rset.getString("id"));
						invoicePayment.setInvoice_id(rset.getString("invoice_id"));
						invoicePayment.setPeriod_end(rset.getString("period_end"));
						invoicePayment.setPeriod_start(rset.getString("period_start"));
						invoicePayment.setStatus(rset.getString("status"));
						invoicePayment.setTransaction_date(rset.getString("transaction_date"));
						invoicePayment.setTransaction_id(rset.getString("transaction_id"));
						result.add(invoicePayment);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice payment for ID [ " + input.getInvoice_id() + " ]", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("entered get invoice Payment by invoice id:" + input.getInvoice_id());
		}
		return result;
	}

}
