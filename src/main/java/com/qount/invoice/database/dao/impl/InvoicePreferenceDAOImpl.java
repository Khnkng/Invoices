package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoicePreferenceDAO;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * DAOImpl  for InvoicePreferenceDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 25 Jan 2017
 *
 */

public class InvoicePreferenceDAOImpl implements InvoicePreferenceDAO {
	private static Logger LOGGER = Logger.getLogger(InvoicePreferenceDAOImpl.class);

	private InvoicePreferenceDAOImpl() {
	}

	private static InvoicePreferenceDAOImpl invoicePreferenceDAOImpl = new InvoicePreferenceDAOImpl();

	public static InvoicePreferenceDAOImpl getInvoicePreferenceDAOImpl() {
		return invoicePreferenceDAOImpl;
	}

	@Override
	public InvoicePreference save(Connection connection, InvoicePreference invoicePreference) {
		LOGGER.debug("entered save:" + invoicePreference);
		InvoicePreference result = null;
		if (invoicePreference == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		try {
			int rowCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePreference.INSERT_QRY);
				pstmt.setString(rowCtr++, invoicePreference.getId());
				pstmt.setString(rowCtr++, invoicePreference.getCompanyId());
				pstmt.setString(rowCtr++, invoicePreference.getTemplateType());
				pstmt.setString(rowCtr++, invoicePreference.getCompanyLogo());
				pstmt.setBoolean(rowCtr++, invoicePreference.isDisplayLogo());
				pstmt.setString(rowCtr++, invoicePreference.getAccentColor());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultPaymentTerms());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultTitle());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultSubHeading());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultFooter());
				pstmt.setString(rowCtr++, invoicePreference.getStandardMemo());
				pstmt.setString(rowCtr++, invoicePreference.getItems());
				pstmt.setString(rowCtr++, invoicePreference.getUnits());
				pstmt.setString(rowCtr++, invoicePreference.getPrice());
				pstmt.setString(rowCtr++, invoicePreference.getAmount());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideItemName());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideItemDescription());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideUnits());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHidePrice());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideAmount());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("invoice preferece created count:" + rowCount + " id:" + invoicePreference.getId());
				if(rowCount>0){
					return invoicePreference;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e);
		}
		LOGGER.debug("exited save:" + invoicePreference);
		return result;
	}

	@Override
	public InvoicePreference delete(Connection connection, InvoicePreference invoicePreference) {
		LOGGER.debug("entered delete:" + invoicePreference);
		InvoicePreference result = null;
		if (invoicePreference == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		try {
			int rowCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePreference.DELETE_QRY);
				pstmt.setString(rowCtr++, invoicePreference.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("invoice preferece deleted count:" + rowCount + " id:" + invoicePreference.getId());
				if(rowCount>0){
					return invoicePreference;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e);
		}
		LOGGER.debug("exited delete:" + invoicePreference);
		return result;
	}

	@Override
	public InvoicePreference getInvoiceByCompanyId(Connection connection, InvoicePreference invoicePreference) {
		LOGGER.debug("entered getInvoiceByCompanyId:" + invoicePreference);
		InvoicePreference result = null;
		if (invoicePreference == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			int rowCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePreference.GET_QRY);
				pstmt.setString(rowCtr++, invoicePreference.getCompanyId());
				rset = pstmt.executeQuery();
				// only 1 record because every company can have only one invoice setting at a time
				if (rset != null && rset.next()) {
					invoicePreference.setId(rset.getString("id"));
					invoicePreference.setCompanyId(rset.getString("company_id"));
					invoicePreference.setTemplateType(rset.getString("template_type"));
					invoicePreference.setCompanyLogo(rset.getString("company_logo"));
					invoicePreference.setDisplayLogo(rset.getBoolean("display_logo"));
					invoicePreference.setAccentColor(rset.getString("accent_color"));
					invoicePreference.setDefaultPaymentTerms(rset.getString("default_payment_terms"));
					invoicePreference.setDefaultTitle(rset.getString("default_title"));
					invoicePreference.setDefaultSubHeading(rset.getString("default_sub_heading"));
					invoicePreference.setDefaultFooter(rset.getString("default_footer"));
					invoicePreference.setStandardMemo(rset.getString("standard_memo"));
					invoicePreference.setItems(rset.getString("items"));
					invoicePreference.setUnits(rset.getString("units"));
					invoicePreference.setPrice(rset.getString("price"));
					invoicePreference.setAmount(rset.getString("amount"));
					invoicePreference.setHideItemName(rset.getBoolean("hide_item_name"));
					invoicePreference.setHideItemDescription(rset.getBoolean("hide_item_description"));
					invoicePreference.setHideUnits(rset.getBoolean("hide_units"));
					invoicePreference.setHidePrice(rset.getBoolean("hide_price"));
					invoicePreference.setHideAmount(rset.getBoolean("hide_amount"));
					return invoicePreference;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getInvoiceByCompanyId:" + invoicePreference);
		return result;
	}

	@Override
	public InvoicePreference update(Connection connection, InvoicePreference invoicePreference) {
		LOGGER.debug("entered update:" + invoicePreference);
		InvoicePreference result = null;
		if (invoicePreference == null) {
			return result;
		}
		PreparedStatement pstmt = null;
		try {
			int rowCtr = 1;
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.InvoicePreference.UPDATE_QRY);
				pstmt.setString(rowCtr++, invoicePreference.getTemplateType());
				pstmt.setString(rowCtr++, invoicePreference.getCompanyLogo());
				pstmt.setBoolean(rowCtr++, invoicePreference.isDisplayLogo());
				pstmt.setString(rowCtr++, invoicePreference.getAccentColor());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultPaymentTerms());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultTitle());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultSubHeading());
				pstmt.setString(rowCtr++, invoicePreference.getDefaultFooter());
				pstmt.setString(rowCtr++, invoicePreference.getStandardMemo());
				pstmt.setString(rowCtr++, invoicePreference.getItems());
				pstmt.setString(rowCtr++, invoicePreference.getUnits());
				pstmt.setString(rowCtr++, invoicePreference.getPrice());
				pstmt.setString(rowCtr++, invoicePreference.getAmount());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideItemName());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideItemDescription());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideUnits());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHidePrice());
				pstmt.setBoolean(rowCtr++, invoicePreference.isHideAmount());
				pstmt.setString(rowCtr++, invoicePreference.getCompanyId());
				pstmt.setString(rowCtr++, invoicePreference.getId());
				int rowCount = pstmt.executeUpdate();
				LOGGER.debug("invoice preferece update count:" + rowCount + " id:" + invoicePreference.getId());
				if(rowCount>0){
					return invoicePreference;
				}
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw new WebApplicationException(e);
		}
		LOGGER.debug("exited update:" + invoicePreference);
		return result;
	}

}
