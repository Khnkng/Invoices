package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.CurrencyDAO;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * dao impl for currency
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Jun 2017
 *
 */
public class CurrencyDAOImpl implements CurrencyDAO {

	private Logger LOGGER = Logger.getLogger(CurrencyDAOImpl.class);

	private CurrencyDAOImpl() {
	}

	private static CurrencyDAOImpl currencyDAOImpl = new CurrencyDAOImpl();

	public static CurrencyDAOImpl getCurrencyDAOImpl() {
		return currencyDAOImpl;
	}

	// private final static String RETRIEVE_LIST = "select
	// `code`,`name`,`html_symbol` from currencies;";
	// private final static String GET = "select `code`,`name`,`html_symbol`
	// from currencies where `code` = ?";

	@Override
	public List<Currencies> retrieveCurrencies() {
		List<Currencies> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Currencies.RETRIEVE_LIST);
				rset = pstmt.executeQuery();
				if (rset != null) {
					result = new ArrayList<Currencies>();
					while (rset.next()) {
						Currencies dbCurrency = new Currencies();
						dbCurrency.setCode(rset.getString("code"));
						dbCurrency.setName(rset.getString("name"));
						dbCurrency.setHtml_symbol(rset.getString("html_symbol"));
						dbCurrency.setJava_symbol(rset.getString("java_symbol"));
						result.add(dbCurrency);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving Currencies", e);
			throw new WebApplicationException(e.getLocalizedMessage(), 500);
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, conn);
			LOGGER.debug("execution time of CurrencyDAOImpl.retrieveCurrencies = " + (System.currentTimeMillis() - startTime) + " in mili seconds ");
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return result;
	}

	@Override
	public Currencies get(Connection conn, String id) {
		Currencies currency = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		long startTime = System.currentTimeMillis();
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Currencies.GET);
				pstmt.setString(1, id);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					currency = new Currencies();
					currency.setCode(rset.getString("code"));
					currency.setName(rset.getString("name"));
					currency.setHtml_symbol(rset.getString("html_symbol"));
					currency.setJava_symbol(rset.getString("java_symbol"));
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error fetching currency", e);
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, null);
			LOGGER.debug("execution time of CurrencyDAOImpl.get = " + (System.currentTimeMillis() - startTime) + " in mili seconds CurrencyID: " + id);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return currency;
	}

}
