package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Company;
import com.qount.invoice.model.Company2;

public interface CompanyDAO {

	Company get(Connection conn, Company company);

	List<Company> getAll(Connection conn);

	Company delete(Connection conn, Company company);

	Company create(Connection conn, Company company);

	Company update(Connection conn, Company company);

	/**
	 * 
	 * @param conn
	 * @return
	 */
	boolean isCompanyRegisteredWithPaymentSpring(Connection conn,String companyId);
	
	public Company2 retrieveCompany(String companyID);
}