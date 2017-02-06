package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceLineTaxesDAO;
import com.qount.invoice.model.InvoiceLineTaxes;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceLineTaxesDAOImpl implements InvoiceLineTaxesDAO {
	
	private static Logger LOGGER = Logger.getLogger(InvoiceLineTaxesDAOImpl.class);

	private InvoiceLineTaxesDAOImpl() {
	}

	private static InvoiceLineTaxesDAOImpl invoiceLineTaxesDAOImpl = new InvoiceLineTaxesDAOImpl();

	public static InvoiceLineTaxesDAOImpl getInvoiceLineTaxesDAOImpl() {
		return invoiceLineTaxesDAOImpl;
	}

	private final static String INSERT_QRY = "INSERT INTO invoice_line_taxes (`invoice_line_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
	private final static String DELETE_QRY = "DELETE FROM invoice_line_taxes WHERE `invoice_line_id` = ?;";


	@Override
	public List<InvoiceLineTaxes> save(Connection connection, List<InvoiceLineTaxes> InvoiceLinesTaxes) {
		LOGGER.debug("entered invoiceLineTaxes save:"+InvoiceLinesTaxes);
		if (InvoiceLinesTaxes == null || InvoiceLinesTaxes.size() == 0) {
			return InvoiceLinesTaxes;
		}
		PreparedStatement pstmt = null;
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement(INSERT_QRY);
				Iterator<InvoiceLineTaxes> invoiceLinesTaxesItr = InvoiceLinesTaxes.iterator();
				while (invoiceLinesTaxesItr.hasNext()) {
					InvoiceLineTaxes invoiceLineTax = invoiceLinesTaxesItr.next();
					pstmt.setString(1, invoiceLineTax.getInvoice_line_id());
					pstmt.setString(2, invoiceLineTax.getTax_id());
					pstmt.setDouble(3, invoiceLineTax.getTax_rate());
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null) {
					return InvoiceLinesTaxes;
				} else {
					throw new WebApplicationException("unable to insert invoice line taxes", 500);
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting invoice line taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("entered invoiceLineTaxes save:"+InvoiceLinesTaxes);
		return InvoiceLinesTaxes;
	}

	@Override
	public InvoiceLineTaxes deleteByInvoiceLineId(Connection connection, InvoiceLineTaxes invoiceLineTaxes){
		LOGGER.debug("entered invoiceLineTaxes deleteByInvoiceLineId:"+invoiceLineTaxes);
		if (invoiceLineTaxes == null || StringUtils.isBlank(invoiceLineTaxes.getInvoice_line_id())) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			int qryCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(DELETE_QRY);
				pstmt.setString(qryCtr++, invoiceLineTaxes.getInvoice_line_id());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("no of invoice line taxes deleted:" + rowCount);
				if (rowCount > 0) {
					return invoiceLineTaxes;
				} else {
					throw new WebApplicationException(CommonUtils.constructResponse("no record deleted", 500));
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error deleting invoice line taxes:" + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("Error deleting invoice line taxes");
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited invoiceLineTaxes deleteByInvoiceLineId:"+invoiceLineTaxes);
		return invoiceLineTaxes;
	}
}
