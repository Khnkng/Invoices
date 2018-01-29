package com.qount.invoice.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.DiscountsRangesDAO;
import com.qount.invoice.model.DiscountsRanges;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

public class DiscountsRangesDAOImpl implements DiscountsRangesDAO {
	private static Logger LOGGER = Logger.getLogger(DiscountsRangesDAOImpl.class);

	private DiscountsRangesDAOImpl() {
	}

	private static final DiscountsRangesDAOImpl discountsRangesDAOImpl = new DiscountsRangesDAOImpl();

	public static DiscountsRangesDAOImpl getDiscountsRangesDAOImpl() {
		return discountsRangesDAOImpl;
	}

	@Override
	public DiscountsRanges get(Connection conn, DiscountsRanges discounts_ranges) {
		LOGGER.debug("entered get:" + discounts_ranges);
		if (discounts_ranges == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.DiscountsRanges.GET_QRY);
				pstmt.setString(1, discounts_ranges.getId());
				pstmt.setString(2, discounts_ranges.getDiscount_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					discounts_ranges.setId(rset.getString("id"));
					discounts_ranges.setDiscount_id(rset.getString("discount_id"));
					discounts_ranges.setFromDay(rset.getInt("fromDay"));
					discounts_ranges.setToDay(rset.getInt("toDay"));
					discounts_ranges.setValue(rset.getDouble("value"));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving discounts_ranges:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll:" + discounts_ranges);
		return discounts_ranges;
	}

	@Override
	public List<DiscountsRanges> getAll(Connection conn, DiscountsRanges input) {
		LOGGER.debug("entered getAll");
		List<DiscountsRanges> result = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		try {
			if (conn != null) {
				result = new ArrayList<DiscountsRanges>();
				pstmt = conn.prepareStatement(SqlQuerys.DiscountsRanges.GET_ALL_QRY);
				pstmt.setString(1, input.getDiscount_id());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					DiscountsRanges discounts_ranges = new DiscountsRanges();
					discounts_ranges.setId(rset.getString("id"));
					discounts_ranges.setDiscount_id(rset.getString("discount_id"));
					discounts_ranges.setFromDay(rset.getInt("fromDay"));
					discounts_ranges.setToDay(rset.getInt("toDay"));
					discounts_ranges.setValue(rset.getDouble("value"));
					result.add(discounts_ranges);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving all discounts_ranges", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited getAll");
		return result;
	}

	@Override
	public String delete(Connection conn, String discountId) {
		LOGGER.debug("entered discountId:" + discountId);
		if (StringUtils.isBlank(discountId)) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				pstmt = conn.prepareStatement(SqlQuerys.DiscountsRanges.DELETE_QRY);
				pstmt.setString(1, discountId);
				int rowCount = pstmt.executeUpdate();
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting discounts_ranges:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + discountId);
		return discountId;
	}

	@Override
	public boolean deleteByIds(Connection conn, String commaSeparatedIds) {
		LOGGER.debug("entered delete:" + commaSeparatedIds);
		if (StringUtils.isBlank(commaSeparatedIds)) {
			throw new WebApplicationException("Invalid input", Constants.INVALID_INPUT);
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				String query = SqlQuerys.DiscountsRanges.DELETE_BY_IDS_QRY;
				query += commaSeparatedIds + ");";
				pstmt = conn.prepareStatement(query);
				int rowCount = pstmt.executeUpdate();
				if (rowCount > 0) {
					return true;
				}
				if (rowCount == 0) {
					throw new WebApplicationException("no record deleted", Constants.EXPECTATION_FAILED);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error deleting discounts_ranges:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited delete:" + commaSeparatedIds);
		return false;
	}

	@Override
	public List<DiscountsRanges> create(Connection conn, List<DiscountsRanges> discounts_ranges, String discount_id) {
		LOGGER.debug("entered create:" + discounts_ranges);
		if (discounts_ranges == null) {
			return null;
		}
		PreparedStatement pstmt = null;
		try {
			if (conn != null) {
				int ctr = 1;
				pstmt = conn.prepareStatement(SqlQuerys.DiscountsRanges.INSERT_QRY);

				Iterator<DiscountsRanges> discounts_rangesItr = discounts_ranges.iterator();
				while (discounts_rangesItr.hasNext()) {
					DiscountsRanges discountsRange = discounts_rangesItr.next();
					pstmt.setString(ctr++, UUID.randomUUID().toString());
					pstmt.setString(ctr++, discount_id);
					pstmt.setInt(ctr++, discountsRange.getFromDay());
					pstmt.setInt(ctr++, discountsRange.getToDay());
					pstmt.setDouble(ctr++, discountsRange.getValue());
					ctr = 1;
					pstmt.addBatch();
				}
				int[] rowCount = pstmt.executeBatch();
				if (rowCount != null && rowCount.length != 0) {
					return discounts_ranges;
				} else {
					throw new WebApplicationException("unable to create discount ranges",
							Constants.DATABASE_ERROR_STATUS);
				}

			}
		} catch (Exception e) {
			LOGGER.error("Error inserting discounts_ranges:", e);
			throw new WebApplicationException(e.getMessage(), Constants.EXPECTATION_FAILED);
		} finally {
			DatabaseUtilities.closeStatement(pstmt);
		}
		LOGGER.debug("exited create:" + discounts_ranges);
		return discounts_ranges;
	}

}