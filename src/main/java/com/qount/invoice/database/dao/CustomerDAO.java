package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Customer;

/**
 * DAO interface for CustomerDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Jun 2017
 *
 */

public interface CustomerDAO {
	
	Customer create(Customer Customer);

	Customer update(Customer Customer);

	Customer delete(Customer Customer);

	Customer retrieveById(Connection conn, Customer Customer);

	List<Customer> retrieveAll(Customer Customer);

}
