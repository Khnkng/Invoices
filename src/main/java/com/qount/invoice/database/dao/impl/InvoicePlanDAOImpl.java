package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoicePlanDAO;
import com.qount.invoice.model.InvoicePlan;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;
import com.qount.invoice.utils.Utilities;

public class InvoicePlanDAOImpl implements InvoicePlanDAO {

	private static Logger LOGGER = Logger.getLogger(InvoicePlanDAOImpl.class);

	private InvoicePlanDAOImpl() {
	}

	private static final InvoicePlanDAOImpl InvoicePlandaoimpl = new InvoicePlanDAOImpl();

	public static InvoicePlanDAOImpl getInvoicePlanDAOImpl() {
		 return InvoicePlandaoimpl;
	}

	@Override
	public	InvoicePlan get(Connection conn, InvoicePlan InvoicePlan){
		LOGGER.debug("entered get:"+InvoicePlan);
		if(InvoicePlan == null){
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.InvoicePlan.GET_QRY);
				pstmt.setString(1, InvoicePlan.getId());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoicePlan.setId(rset.getString("id"));
					InvoicePlan.setName(rset.getString("name"));
					InvoicePlan.setAmount(rset.getString("amount"));
					InvoicePlan.setFrequency(rset.getString("frequency"));
					InvoicePlan.setEnds_after(rset.getString("ends_after"));
					InvoicePlan.setBill_immediately(rset.getString("bill_immediately"));
					InvoicePlan.setDay_map(rset.getString("day_map"));
				}
			}
		}catch(WebApplicationException e) {
			LOGGER.error("Error retrieving InvoicePlan:" + InvoicePlan.getId() + ",  ", e);
			throw e;
		}catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll:"+InvoicePlan);
		return InvoicePlan;
	}

	@Override
	public	List<InvoicePlan> getAll(Connection conn){
		LOGGER.debug("entered getAll");
		List<InvoicePlan> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<InvoicePlan>();
				pstmt = conn.prepareStatement(SqlQuerys.InvoicePlan.GET_ALL_QRY);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					InvoicePlan InvoicePlan= new InvoicePlan();
					InvoicePlan.setId(rset.getString("id"));
					InvoicePlan.setName(rset.getString("name"));
					InvoicePlan.setAmount(rset.getString("amount"));
					InvoicePlan.setFrequency(rset.getString("frequency"));
					InvoicePlan.setEnds_after(rset.getString("ends_after"));
					InvoicePlan.setBill_immediately(rset.getString("bill_immediately"));
					result.add(InvoicePlan);
				}
			}
		}catch(WebApplicationException e) {
			LOGGER.error("Error retrieving all InvoicePlan"+  e);
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
	public	InvoicePlan delete(Connection conn, InvoicePlan InvoicePlan){
		LOGGER.debug("entered delete:"+InvoicePlan);
		if(InvoicePlan == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.InvoicePlan.DELETE_QRY);
				pstmt.setString(1, InvoicePlan.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record deleted", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error deleting InvoicePlan:" + InvoicePlan.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}		LOGGER.debug("exited delete:"+InvoicePlan);
		return InvoicePlan;
	}

	@Override
	public	InvoicePlan create(Connection conn, InvoicePlan InvoicePlan){
		LOGGER.debug("entered create:"+InvoicePlan);
		if(InvoicePlan == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.InvoicePlan.INSERT_QRY);
				pstmt.setString(ctr++, InvoicePlan.getId());
				pstmt.setString(ctr++, InvoicePlan.getName());
				pstmt.setString(ctr++, InvoicePlan.getAmount());
				pstmt.setString(ctr++, InvoicePlan.getFrequency());
				pstmt.setString(ctr++, InvoicePlan.getEnds_after());
				pstmt.setString(ctr++, InvoicePlan.getBill_immediately());
				pstmt.setString(ctr++, InvoicePlan.getDay_map());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record inserted", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error inserting InvoicePlan:" + InvoicePlan.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}		LOGGER.debug("exited create:"+InvoicePlan);
		return InvoicePlan;
	}
	
	@Override
	public	InvoicePlan update(Connection conn, InvoicePlan InvoicePlan){
		LOGGER.debug("entered update:"+InvoicePlan);
		if(InvoicePlan == null){
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.InvoicePlan.UPDATE_QRY);
				pstmt.setString(ctr++, InvoicePlan.getName());
				pstmt.setString(ctr++, InvoicePlan.getAmount());
				pstmt.setString(ctr++, InvoicePlan.getFrequency());
				pstmt.setString(ctr++, InvoicePlan.getEnds_after());
				pstmt.setString(ctr++, InvoicePlan.getBill_immediately());
				pstmt.setString(ctr++, InvoicePlan.getDay_map());
				pstmt.setString(ctr++, InvoicePlan.getId());
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException(Utilities.constructResponse("no record updated", 500));
				}			}
		} catch (WebApplicationException e) {
			LOGGER.error("Error updating InvoicePlan:" + InvoicePlan.getId() + ",  ", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error(e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited update:"+InvoicePlan);
		return InvoicePlan;
	}

}
