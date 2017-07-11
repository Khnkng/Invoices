package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Currencies;

/**
 * DAO interface for CurrencyDAOmpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Jun 2017
 *
 */
public interface CurrencyDAO {

	List<Currencies> retrieveCurrencies();
	public Currencies get(Connection conn, String id);

}
