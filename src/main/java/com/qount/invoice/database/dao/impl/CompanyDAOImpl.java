package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.CompanyDAO;
import com.qount.invoice.model.Company;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;
import com.qount.invoice.utils.Utilities;

public class CompanyDAOImpl implements CompanyDAO {

	private static Logger LOGGER = Logger.getLogger(CompanyDAOImpl.class);

	private CompanyDAOImpl() {
	}

	private static final CompanyDAOImpl Companydaoimpl = new CompanyDAOImpl();

	public static CompanyDAOImpl getCompanyDAOImpl() {
		 return Companydaoimpl;
	}

	@Override
	public	Company get(Connection conn, Company company){
		LOGGER.debug("entered get:"+company);
		if(company == null){
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Company.GET_QRY);
				pstmt.setString(1, company.getId());
				rset = pstmt.executeQuery();
				if (rset.next()) {
					company.setId(rset.getString("id"));
					company.setName(rset.getString("name"));
					company.setEin(rset.getString("ein"));
					company.setType(rset.getString("type"));
					company.setPhone_number(rset.getString("phone_number"));
					company.setAddress(rset.getString("address"));
					company.setCity(rset.getString("city"));
					company.setState(rset.getString("state"));
					company.setCountry(rset.getString("country"));
					company.setZipcode(rset.getString("zipcode"));
					company.setCurrency(rset.getString("currency"));
					company.setEmail(rset.getString("email"));
					company.setPayment_info(rset.getString("payment_info"));
					company.setCreatedby(rset.getString("createdBy"));
					company.setModifiedby(rset.getString("modifiedBy"));
					company.setCreateddate(rset.getString("createdDate"));
					company.setModifieddate(rset.getString("modifiedDate"));
					company.setOwner(rset.getString("owner"));
					company.setActive(rset.getBoolean("active"));
				}
			}
		}catch(WebApplicationException e) {
			LOGGER.error("Error retrieving company:" + company.getId() + ",  ", e);
			throw e;
		}catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll:"+company);
		return company;
	}

	@Override
	public	List<Company> getAll(Connection conn){
		LOGGER.debug("entered getAll");
		List<Company> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<Company>();
				pstmt = conn.prepareStatement(SqlQuerys.Company.GET_ALL_QRY);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Company company= new Company();
					company.setId(rset.getString("id"));
					company.setName(rset.getString("name"));
					company.setEin(rset.getString("ein"));
					company.setType(rset.getString("type"));
					company.setPhone_number(rset.getString("phone_number"));
					company.setAddress(rset.getString("address"));
					company.setCity(rset.getString("city"));
					company.setState(rset.getString("state"));
					company.setCountry(rset.getString("country"));
					company.setZipcode(rset.getString("zipcode"));
					company.setCurrency(rset.getString("currency"));
					company.setEmail(rset.getString("email"));
					company.setPayment_info(rset.getString("payment_info"));
					company.setCreatedby(rset.getString("createdBy"));
					company.setModifiedby(rset.getString("modifiedBy"));
					company.setCreateddate(rset.getString("createdDate"));
					company.setModifieddate(rset.getString("modifiedDate"));
					company.setOwner(rset.getString("owner"));
					company.setActive(rset.getBoolean("active"));
					result.add(company);
				}
			}
		}catch(WebApplicationException e) {
			LOGGER.error("Error retrieving all company"+  e);
			throw e;
		}catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll");
		return result;
	}

	@Override
	public	Company delete(Connection conn, Company company){
		LOGGER.debug("entered delete:"+company);
		if(company == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.Company.DELETE_QRY);
				pstmt.setString(1, company.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record deleted", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error deleting company:" + company.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}		LOGGER.debug("exited delete:"+company);
		return company;
	}

	@Override
	public	Company create(Connection conn, Company company){
		LOGGER.debug("entered create:"+company);
		if(company == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.Company.INSERT_QRY);
				pstmt.setString(ctr++, company.getId());
				pstmt.setString(ctr++, company.getName());
				pstmt.setString(ctr++, company.getEin());
				pstmt.setString(ctr++, company.getType());
				pstmt.setString(ctr++, company.getPhone_number());
				pstmt.setString(ctr++, company.getAddress());
				pstmt.setString(ctr++, company.getCity());
				pstmt.setString(ctr++, company.getState());
				pstmt.setString(ctr++, company.getCountry());
				pstmt.setString(ctr++, company.getZipcode());
				pstmt.setString(ctr++, company.getCurrency());
				pstmt.setString(ctr++, company.getEmail());
				pstmt.setString(ctr++, company.getPayment_info());
				pstmt.setString(ctr++, company.getCreatedby());
				pstmt.setString(ctr++, company.getModifiedby());
				pstmt.setString(ctr++, company.getCreateddate());
				pstmt.setString(ctr++, company.getModifieddate());
				pstmt.setString(ctr++, company.getOwner());
				pstmt.setBoolean(ctr++, company.getActive());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record inserted", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting company:" + company.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}		LOGGER.debug("exited create:"+company);
		return company;
	}
	@Override
	public	Company update(Connection conn, Company company){
		LOGGER.debug("entered update:"+company);
		if(company == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.Company.UPDATE_QRY);
				pstmt.setString(ctr++, company.getName());
				pstmt.setString(ctr++, company.getEin());
				pstmt.setString(ctr++, company.getType());
				pstmt.setString(ctr++, company.getPhone_number());
				pstmt.setString(ctr++, company.getAddress());
				pstmt.setString(ctr++, company.getCity());
				pstmt.setString(ctr++, company.getState());
				pstmt.setString(ctr++, company.getCountry());
				pstmt.setString(ctr++, company.getZipcode());
				pstmt.setString(ctr++, company.getCurrency());
				pstmt.setString(ctr++, company.getEmail());
				pstmt.setString(ctr++, company.getPayment_info());
				pstmt.setString(ctr++, company.getCreatedby());
				pstmt.setString(ctr++, company.getModifiedby());
				pstmt.setString(ctr++, company.getCreateddate());
				pstmt.setString(ctr++, company.getModifieddate());
				pstmt.setString(ctr++, company.getOwner());
				pstmt.setBoolean(ctr++, company.getActive());
				pstmt.setString(ctr++, company.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record updated", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating company:" + company.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited update:"+company);
		return company;
	}

	@Override
	public boolean isCompanyRegisteredWithPaymentSpring(Connection connection, String companyId) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		long startTime = System.currentTimeMillis();
		try {
			if (connection != null && StringUtils.isNotBlank(companyId)) {
				pstmt = connection.prepareStatement(SqlQuerys.Company.GET_PAYMMENT_SPRING_COMPANY_DETAILS);
				pstmt.setString(1, companyId);
				rset = pstmt.executeQuery();
				if (rset.next()) {
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching payment spirng company details [ " + companyId + "]", e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("execution time of CompaniesDAOImpl.isCompanyRegisteredWithPaymentSpring = " + (System.currentTimeMillis() - startTime) + " in mili seconds companyId: "
					+ companyId);
		}
		return false;
	}

}