package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.CustomerDAO;
import com.qount.invoice.model.Customer;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

/**
 * dao impl for customer
 * 
 * @author Mateen, Qount.
 * @version 1.0, 06 Jun 2017
 *
 */
public class CustomerDAOImpl implements CustomerDAO {
	private static Logger LOGGER = Logger.getLogger(CustomerDAOImpl.class);

	private CustomerDAOImpl() {
	}

	private static CustomerDAOImpl customerDAOImpl = new CustomerDAOImpl();

	public static CustomerDAOImpl getCustomerDAOImpl() {
		return customerDAOImpl;
	}

	// private final static String INSERT_QRY = "INSERT INTO company_customers
	// (`user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`)VALUES
	// (?,?,?,?,?,?,?,?,?,?);";
	// private final static String UPDATE_QRY = "UPDATE company_customers SET
	// `customer_address` = ?,`customer_city` = ?,`customer_country` =
	// ?,`customer_ein` = ?,`customer_state` = ?,`customer_name` =
	// ?,`customer_zipcode` = ? WHERE `user_id`= ? AND `customer_id`= ? AND
	// `company_id` = ?;";
	// private final static String DELETE_QRY = "DELETE FROM company_customers
	// WHERE `user_id`=? AND company_id = ? AND customer_id=?;";
	// private final static String RETRIEVE_BY_ID_QRY = "SELECT
	// `user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`
	// FROM company_customers WHERE `user_id`= ? AND `customer_id`= ? AND
	// `company_id` = ?";
	// private final static String RETRIEVE_LIST_BY_ID_QRY = "SELECT
	// `user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`
	// FROM company_customers WHERE `user_id`= ? AND `company_id` = ?;";

	@Override
	public Customer create(Customer customer) {
		if (customer == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Customer.INSERT_QRY);
				pstmt.setString(1, customer.getUser_id());
				pstmt.setString(2, customer.getCompany_id());
				pstmt.setString(3, customer.getCustomer_id());
				pstmt.setString(4, customer.getCustomer_address());
				pstmt.setString(5, customer.getCustomer_city());
				pstmt.setString(6, customer.getCustomer_country());
				pstmt.setString(7, customer.getCustomer_ein());
				pstmt.setString(8, customer.getCustomer_state());
				pstmt.setString(9, customer.getCustomer_name());
				pstmt.setString(10, customer.getCustomer_zipcode());
				pstmt.setString(11, customer.getEmail_ids().toString());
				pstmt.setString(12, customer.getPhone_number());
				pstmt.setString(13, customer.getCoa());
				pstmt.setString(14, customer.getPayment_spring_id());
				pstmt.setString(15, customer.getTerm());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException();
				}
				LOGGER.debug("no of item code created:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error creating customer", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(conn);
			LOGGER.debug("execution time of CustomerDAOImpl.create = " + (System.currentTimeMillis() - startTime) + " in mili seconds  Customer:" + customer);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return customer;
	}

	@Override
	public Customer update(Customer customer) {
		if (customer == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Customer.UPDATE_QRY);
				pstmt.setString(1, customer.getCustomer_address());
				pstmt.setString(2, customer.getCustomer_city());
				pstmt.setString(3, customer.getCustomer_country());
				pstmt.setString(4, customer.getCustomer_ein());
				pstmt.setString(5, customer.getCustomer_state());
				pstmt.setString(6, customer.getCustomer_name());
				pstmt.setString(7, customer.getCustomer_zipcode());
				pstmt.setString(8, customer.getEmail_ids().toString());
				pstmt.setString(9, customer.getPhone_number());
				pstmt.setString(10, customer.getUser_id());
				pstmt.setString(11, customer.getCompany_id());
				pstmt.setString(12, customer.getCoa());
				pstmt.setString(13, customer.getPayment_spring_id());
				pstmt.setString(14, customer.getTerm());
				pstmt.setString(15, customer.getCustomer_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException();
				}
				LOGGER.debug("no of item code updated:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error updating customer", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(conn);
			LOGGER.debug("execution time of CustomerDAOImpl.update = " + (System.currentTimeMillis() - startTime) + " in mili seconds  Customer:" + customer);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return customer;
	}

	@Override
	public Customer delete(Customer customer) {
		if (customer == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Customer.DELETE_QRY);
				pstmt.setString(1, customer.getUser_id());
				pstmt.setString(2, customer.getCompany_id());
				pstmt.setString(3, customer.getCustomer_id());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException();
				}
				LOGGER.debug("no of item code deleted:" + rowCount);
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting customer", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(conn);
			LOGGER.debug("execution time of CustomerDAOImpl.delete = " + (System.currentTimeMillis() - startTime) + " in mili seconds  Customer:" + customer);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return customer;
	}

	@Override
	public Customer retrieveById(Connection conn, Customer customer) {
		if (customer == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		long startTime = System.currentTimeMillis();
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Customer.RETRIEVE_BY_ID_QRY);
				pstmt.setString(1, customer.getCustomer_id());
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					customer.setUser_id(rset.getString("user_id"));
					customer.setCompany_id(rset.getString("company_id"));
					customer.setCustomer_id(rset.getString("customer_id"));
					customer.setCustomer_address(rset.getString("customer_address"));
					customer.setCustomer_city(rset.getString("customer_city"));
					customer.setCustomer_country(rset.getString("customer_country"));
					customer.setCustomer_ein(rset.getString("customer_ein"));
					customer.setCustomer_state(rset.getString("customer_state"));
					customer.setCustomer_name(rset.getString("customer_name"));
					customer.setCustomer_zipcode(rset.getString("customer_zipcode"));
					customer.setEmail_ids(CommonUtils.getJsonArrayFromString(rset.getString("email_ids")));
					customer.setPhone_number(rset.getString("phone_number"));
					customer.setCoa(rset.getString("coa"));
					customer.setTerm(rset.getString("term"));
					return customer;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving customer", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("execution time of CustomerDAOImpl.retrieveByID = " + (System.currentTimeMillis() - startTime) + " in mili seconds  Customer:" + customer);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return null;
	}

	@Override
	public List<Customer> retrieveAll(Customer customer) {
		if (customer == null) {
			return null;
		}
		List<Customer> customerList = new ArrayList<Customer>();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Customer.RETRIEVE_LIST_BY_ID_QRY);
				pstmt.setString(1, customer.getUser_id());
				pstmt.setString(2, customer.getCompany_id());
				rset = pstmt.executeQuery();
				if (rset != null) {
					while (rset.next()) {
						Customer customer1 = new Customer();
						customer1.setUser_id(rset.getString("user_id"));
						customer1.setCompany_id(rset.getString("company_id"));
						customer1.setCustomer_id(rset.getString("customer_id"));
						customer1.setCustomer_address(rset.getString("customer_address"));
						customer1.setCustomer_city(rset.getString("customer_city"));
						customer1.setCustomer_country(rset.getString("customer_country"));
						customer1.setCustomer_ein(rset.getString("customer_ein"));
						customer1.setCustomer_state(rset.getString("customer_state"));
						customer1.setCustomer_name(rset.getString("customer_name"));
						customer1.setCustomer_zipcode(rset.getString("customer_zipcode"));
						customer.setEmail_ids(CommonUtils.getJsonArrayFromString(rset.getString("email_ids")));
						customer1.setPhone_number(rset.getString("phone_number"));
						customer1.setCoa(rset.getString("coa"));
						customer1.setTerm(rset.getString("term"));
						customerList.add(customer1);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving list", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(conn);
			LOGGER.debug("execution time of CustomerDAOImpl.retrieveAll = " + (System.currentTimeMillis() - startTime) + " in mili seconds  Customer:" + customer);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return customerList;
	}
}
