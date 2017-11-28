package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.PayEventDAO;
import com.qount.invoice.model.PayEvent;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;



public class PayEventDAOImpl implements PayEventDAO{

	private static final Logger LOGGER = Logger.getLogger(PayEventDAOImpl.class);

	@Override
	public boolean update(Connection connection, PayEvent payEvent) {
		LOGGER.debug(" updates a payEvent [ " + payEvent + " ] ");

		boolean result = false;
		PreparedStatement pstmt = null;
		long startTime = System.currentTimeMillis();
		String query = null;
		try {
			int qryCtr = 1;
			pstmt = connection.prepareStatement(SqlQuerys.PayEvent.UPDATE);
			pstmt.setString(qryCtr++, payEvent.getInvoiceID());
			query = pstmt.toString();
			int rowCount = pstmt.executeUpdate();
			result = rowCount != 0;
		} catch (Exception e) {
			LOGGER.error("Error updating payEvent", e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("execution time of PayEventDAOImpl.update = " + (System.currentTimeMillis() - startTime)
					+ " in mili seconds with query : " + query);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return result;
	}
	
}
