package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceLineDimensionDAO;
import com.qount.invoice.model.Dimension;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

public class InvoiceLineDimensionDAOImpl implements InvoiceLineDimensionDAO {

	private static final InvoiceLineDimensionDAO INVOICELINE_DIMENSIONS_DAO = new InvoiceLineDimensionDAOImpl();

	private static final Logger LOGGER = Logger.getLogger(InvoiceLineDimensionDAOImpl.class);
	
	private InvoiceLineDimensionDAOImpl() {
	}

	@Override
	public boolean save(Connection connection, String invoiceLineID, Dimension invoiceLineDimension) {
		LOGGER.debug("Saving dimension for company [ " + invoiceLineID + " ] and [ " + invoiceLineDimension + " ]");
		boolean result = false;
		PreparedStatement pstmt = null;
		long startTime = System.currentTimeMillis();
		String query = null;
		try {
			pstmt = connection.prepareStatement(SqlQuerys.InvoiceLineDimension.INSERT);
			for (String dimensionValue : invoiceLineDimension.getValues()) {
				pstmt.setString(1, invoiceLineID);
				pstmt.setString(2, invoiceLineDimension.getName());
				pstmt.setString(3, dimensionValue);
				pstmt.setString(4, invoiceLineDimension.getCompanyID());
				pstmt.addBatch();
			}
			query = pstmt.toString();
			pstmt.executeBatch();
			result = true;

		} catch (Exception e) {
			LOGGER.error("Error creating dimesion [" + invoiceLineDimension.getCompanyID() + " : "
					+ invoiceLineDimension.getName() + "]", e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("execution time of InvoiceLineDimensionsDAO.save = " + (System.currentTimeMillis() - startTime)
					+ " in mili seconds with query: " + query);
		}
		return result;
	}

	public boolean savelist(Connection connection, List<Dimension> invoiceLineDimensions) {
		LOGGER.debug("Saving dimensions of invoice lines [ " + invoiceLineDimensions + " ]");
		boolean result = false;
		PreparedStatement pstmt = null;
		long startTime = System.currentTimeMillis();
		String query = null;
		try {
			pstmt = connection.prepareStatement(SqlQuerys.InvoiceLineDimension.INSERT);
			for (Dimension invoiceLineDimension : invoiceLineDimensions) {
				for (String dimensionValue : invoiceLineDimension.getValues()) {
					pstmt.setString(1, invoiceLineDimension.getInvoiceLineID());
					pstmt.setString(2, invoiceLineDimension.getName());
					pstmt.setString(3, dimensionValue);
					pstmt.setString(4, invoiceLineDimension.getCompanyID());
					pstmt.setString(5, invoiceLineDimension.getName());
					pstmt.setString(6, dimensionValue);
					pstmt.addBatch();
				}
			}
			query = pstmt.toString();
			pstmt.executeBatch();
			result = true;

		} catch (Exception e) {
			LOGGER.error("Error creating dimesion", e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("execution time of InvoiceLineDimensionsDAO.savelist = "
					+ (System.currentTimeMillis() - startTime) + " in mili seconds with query: " + query);
		}
		return result;
	}

	@Override
	public boolean delete(Connection connection, List<InvoiceLine> lines) {
		LOGGER.debug("deletes dimensions");
		boolean result = false;
		PreparedStatement pstmt = null;
		try {
			if (lines != null && lines.size() > 0) {
				StringBuilder query = new StringBuilder(SqlQuerys.InvoiceLineDimension.DELETE_LIST).append("(");
				for(InvoiceLine line : lines){
					query.append("'" + line.getId() + "',");
				}
				query.deleteCharAt(query.length() - 1).append(")");
				LOGGER.debug("invoice dimension deletion query [ " + query + " ]");
				pstmt = connection.prepareStatement(query.toString());
				int deletedRows = pstmt.executeUpdate();
				LOGGER.debug("deletedRows [ " + deletedRows + " ]");
				result = true;
			}
		} catch (Exception e) {
			LOGGER.error("Error invoice deleting dimensions", e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		return result;
	}

	public static InvoiceLineDimensionDAO getInstance() {
		return INVOICELINE_DIMENSIONS_DAO;
	}

}
