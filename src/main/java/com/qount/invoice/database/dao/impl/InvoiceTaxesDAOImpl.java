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

import com.qount.invoice.database.dao.InvoiceTaxesDAO;
import com.qount.invoice.model.InvoiceTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Feb 2016
 *
 */
public class InvoiceTaxesDAOImpl implements InvoiceTaxesDAO {

	private static Logger LOGGER = Logger.getLogger(InvoiceTaxesDAOImpl.class);

	private InvoiceTaxesDAOImpl() {
	}

	private static InvoiceTaxesDAOImpl invoiceTaxesDAOImpl = new InvoiceTaxesDAOImpl();

	public static InvoiceTaxesDAOImpl getInvoiceTaxesDAOImpl() {
		return invoiceTaxesDAOImpl;
	}

	@Override
	public List<InvoiceTaxes> save(Connection connection, String invoiceID, List<InvoiceTaxes> invoiceTaxes) {
		LOGGER.debug("entered invoiceTaxes save:" + invoiceTaxes);
		if (invoiceTaxes == null || invoiceTaxes.size() == 0 || StringUtils.isBlank(invoiceID)) {
			return invoiceTaxes;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoiceTaxes.INSERT_QRY);
				Iterator<InvoiceTaxes> invoiceTaxesItr = invoiceTaxes.iterator();
				while (invoiceTaxesItr.hasNext()) {
					InvoiceTaxes invoiceTax = invoiceTaxesItr.next();
					pstmt.setString(1, invoiceID);
					pstmt.setString(2, invoiceTax.getTax_id());
					pstmt.setDouble(3, invoiceTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null && rowCount.length > 0) {
					return invoiceTaxes;
				} else {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited invoiceTaxes save:" + invoiceTaxes);
		return invoiceTaxes;
	}

	@Override
	public InvoiceTaxes deleteByInvoiceId(Connection connection, InvoiceTaxes invoiceTaxes) {
		LOGGER.debug("entered delete invoice taxes By Invoice Id:" + invoiceTaxes);
		if (invoiceTaxes == null || StringUtils.isBlank(invoiceTaxes.getInvoice_id())) {
			return invoiceTaxes;
		}
		PreparedStatement pstmt = null;
		try {
			int qryCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoiceTaxes.DELETE_QRY);
				pstmt.setString(qryCtr++, invoiceTaxes.getInvoice_id());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice taxes deleted:" + rowCount);
				if (rowCount > 0) {
					return invoiceTaxes;
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice taxes");
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete invoice taxes By Invoice Id:" + invoiceTaxes);
		return invoiceTaxes;
	}

	@Override
	public List<InvoiceTaxes> getByInvoiceID(InvoiceTaxes invoiceTaxes) {
		LOGGER.debug("entered invoiceTaxes getByInvoiceID:" + invoiceTaxes);
		Connection connection = null;
		if (invoiceTaxes == null || StringUtils.isBlank(invoiceTaxes.getInvoice_id())) {
			return null;
		}
		List<InvoiceTaxes> invoiceTaxesList = new ArrayList<InvoiceTaxes>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoiceTaxes.GET_QRY);
				pstmt.setString(1, invoiceTaxes.getInvoice_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoiceTaxes invoiceTax = new InvoiceTaxes();
					invoiceTax.setInvoice_id(rset.getString("invoice_id"));
					invoiceTax.setTax_id(rset.getString("tax_id"));
					invoiceTax.setTax_rate(rset.getDouble("tax_rate"));
					invoiceTaxesList.add(invoiceTax);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching invoice taxes for invoice_id [ " + invoiceTaxes.getInvoice_id() + " ]", e);
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
		}
		LOGGER.debug("exited invoiceTaxes getByInvoiceID:" + invoiceTaxes);
		return invoiceTaxesList;
	}
}
