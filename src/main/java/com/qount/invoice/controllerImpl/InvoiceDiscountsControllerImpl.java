
package com.qount.invoice.controllerImpl;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.DiscountsRanges;
import com.qount.invoice.model.InvoiceDiscounts;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.ResponseUtil;
import com.qount.invoice.utils.Utilities;

public class InvoiceDiscountsControllerImpl {

	private static Logger LOGGER = Logger.getLogger(InvoiceDiscountsControllerImpl.class);

	public static InvoiceDiscounts getInvoice_discounts(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setId(id);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().get(conn, invoice_discounts);
			if (invoice_discounts == null) {
				throw new WebApplicationException(Utilities.constructResponse(Constants.FAILURE_STATUS_STR,
						"no records found", Status.EXPECTATION_FAILED));
			}
			invoice_discounts = ChangeDateFormat(invoice_discounts);
			return invoice_discounts;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_discounts", e);
			throw e;
		} catch (Exception e) {
			LOGGER.error("getInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	private static InvoiceDiscounts ChangeDateFormat(InvoiceDiscounts invoice_discounts) {
		try {
			if (invoice_discounts != null) {
				invoice_discounts.setCreated_at(convertTimeStampToString(invoice_discounts.getCreated_at(),
						Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
				invoice_discounts.setLast_updated_at(convertTimeStampToString(invoice_discounts.getLast_updated_at(),
						Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT));
			}
		} catch (Exception e) {
			LOGGER.error(Utilities.getErrorStackTrace(e));
		}
		return invoice_discounts;
	}

	public static List<InvoiceDiscounts> getInvoice_discountss(String userId, String companyId) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setCreated_by(userId);
			invoice_discounts.setCompany_id(companyId);
			List<InvoiceDiscounts> invoice_discountss = MySQLManager.getInvoiceDiscountsDAO().getAll(conn,
					invoice_discounts);
			return invoice_discountss;
		} catch (WebApplicationException e) {
			LOGGER.error("getInvoice_discountss", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("getInvoice_discountss", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response deleteInvoice_discounts(String userId, String companyId, String id) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
			invoice_discounts.setId(id);
			invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().delete(conn, invoice_discounts);
			return Response.ok(invoice_discounts).build();
		} catch (WebApplicationException e) {
			LOGGER.error("deleteInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("deleteInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response createInvoice_discount(String userId, String companyId, InvoiceDiscounts invoice_discount) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				invoice_discount.setId(UUID.randomUUID().toString());
				invoice_discount.setCreated_by(userId);
				invoice_discount.setCompany_id(companyId);
				String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
				invoice_discount.setCreated_at(currentUtcDateStr);
				invoice_discount.setLast_updated_by(userId);
				invoice_discount.setLast_updated_at(currentUtcDateStr);
				conn.setAutoCommit(false);
				invoice_discount = MySQLManager.getInvoiceDiscountsDAO().create(conn, invoice_discount);
				if (invoice_discount != null && StringUtils.isNotBlank(invoice_discount.getId())) {
					List<DiscountsRanges> discountsRanges = invoice_discount.getDiscountsRanges();
					if (discountsRanges != null) {
						discountsRanges = MySQLManager.getDiscountsRangesDAO().create(conn, discountsRanges,
								invoice_discount.getId());
						if (discountsRanges != null && !discountsRanges.isEmpty()) {
							conn.commit();
							return Response.ok(invoice_discount).build();
						}

					}

				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error("createInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("createInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static Response updateInvoice_discount(String userId, String companyId, String id,
			InvoiceDiscounts invoice_discount) {
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				invoice_discount.setId(id);
				invoice_discount.setCompany_id(companyId);
				String currentUtcDateStr = new Date(System.currentTimeMillis()).toString();
				invoice_discount.setLast_updated_by(userId);
				invoice_discount.setLast_updated_at(currentUtcDateStr);
				conn.setAutoCommit(false);
				InvoiceDiscounts invoice_discount_result = MySQLManager.getInvoiceDiscountsDAO().update(conn,
						invoice_discount);
				if (invoice_discount_result != null && StringUtils.isNotBlank(invoice_discount_result.getId())) {
					String discountId = MySQLManager.getDiscountsRangesDAO().delete(conn,
							invoice_discount_result.getId());
					if (StringUtils.isNotBlank(discountId)) {
						List<DiscountsRanges> result = MySQLManager.getDiscountsRangesDAO().create(conn,
								invoice_discount_result.getDiscountsRanges(), discountId);
						if (result != null && !result.isEmpty()) {
							conn.commit();
							return Response.ok(invoice_discount).build();
						}
					}

				}
			}
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR,
					Constants.UNEXPECTED_ERROR_STATUS_STR, Status.EXPECTATION_FAILED));
		} catch (WebApplicationException e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
	}

	public static String convertTimeStampToString(String dateStr, SimpleDateFormat from, SimpleDateFormat to) {
		try {
			if (StringUtils.isNotBlank(dateStr))
				return to.format(from.parse(dateStr)).toString();
		} catch (Exception e) {
			LOGGER.error(Utilities.getErrorStackTrace(e));
		}
		return null;
	}

	public static Response get_discount_amount(String company_id, String discount_id, String payload) {
		Connection conn = null;
		JSONObject json = CommonUtils.getJsonFromString(payload);
		double discount_amount = 0.0d;
		JSONObject result = new JSONObject();
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
				InvoiceDiscounts invoice_discounts = new InvoiceDiscounts();
				invoice_discounts.setId(discount_id);
				invoice_discounts = MySQLManager.getInvoiceDiscountsDAO().get(conn, invoice_discounts);
				if (invoice_discounts != null && CommonUtils.isValidJSON(json)) {
					String type = invoice_discounts.getType();
					List<DiscountsRanges> discountsRanges = invoice_discounts.getDiscountsRanges();
					if (discountsRanges != null && !discountsRanges.isEmpty()) {
						java.util.Date currentDate = new java.util.Date();
						currentDate = getDate(currentDate);
						LOGGER.debug("current date" + currentDate);
						java.util.Date endDate = DateUtils.getDateFromString(json.optString("due_date"),
								Constants.TIME_STATMP_TO_INVOICE_FORMAT);
						endDate = getDate(endDate);
						LOGGER.debug("endDate" + endDate);
						long daysDifference = InvoiceParser.getDateDifference(currentDate, endDate);
						LOGGER.debug("daysDifference" + daysDifference);
						Iterator<DiscountsRanges> discountsRangesItr = discountsRanges.iterator();
						while (discountsRangesItr.hasNext()) {
							DiscountsRanges discountsRange = discountsRangesItr.next();
							long fromDay = discountsRange.getFromDay();
							long toDay = discountsRange.getToDay();
							if (fromDay <= daysDifference && toDay >= daysDifference) {
								if (type.equals(Constants.FLAT_DISCOUNT)) {
									discount_amount = discountsRange.getValue();
									result.put("discount_amount", discount_amount);
								} else if (type.equals(Constants.PERCENTAGE)) {
									discount_amount = json.optDouble("amount") * (discountsRange.getValue() / 100);
									result.put("discount_amount", discount_amount);
								} else {
									throw new WebApplicationException(
											"Discount range type is neither flat_discount not percentage",
											Constants.EXPECTATION_FAILED);
								}
							}
							if (!result.isNull("discount_amount")) {
								return Response.ok(result.toString()).build();
							}
						}
					}
				}
			}
		} catch (WebApplicationException e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(Utilities.constructResponse(e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error("updateInvoice_discounts", e);
			throw new WebApplicationException(
					Utilities.constructResponse(e.getMessage(), Constants.EXPECTATION_FAILED));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
		return Response.ok(new JSONObject().put("discount_amount", 0).toString()).build();
	}

	private static java.util.Date getDate(java.util.Date d) {

		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.MONTH, d.getMonth());
		cl.set(Calendar.DATE, d.getDate());
		cl.set(Calendar.MILLISECOND, 0);
		cl.set(Calendar.SECOND, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.HOUR, 0);
		d.setTime(cl.getTimeInMillis());
		return d;

	};

	public static void main(String[] args) {
		java.util.Date d1 = new java.util.Date();
		// d1.setDate(31);
		System.out.println(d1);
		System.out.println("==============");
		System.out.println(getDate(d1));
	}
}
