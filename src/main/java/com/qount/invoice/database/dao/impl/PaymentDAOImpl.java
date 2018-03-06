package com.qount.invoice.database.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.PayEventDAO;
import com.qount.invoice.database.dao.paymentDAO;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceHistory;
import com.qount.invoice.model.PayEvent;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.parser.InvoiceParser;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.DateUtils;
import com.qount.invoice.utils.SqlQuerys;

public class PaymentDAOImpl implements paymentDAO {

	public static PaymentDAOImpl instance = new PaymentDAOImpl();
	private static final Logger LOGGER = Logger.getLogger(PaymentDAOImpl.class);

	public static PaymentDAOImpl getInstance() {
		return instance;
	}

	private PaymentDAOImpl() {

	}

	@Override
	public Payment save(Payment payment, Connection connection, boolean checkInvoiceAmountFlag) {
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			try {
				LOGGER.debug("entered invoice payment save:" + payment);
				List<PaymentLine> lines = getLines(payment.getId(), connection);
				pstmt = connection.prepareStatement(SqlQuerys.Payments.INSERT_QRY);
				pstmt.setString(ctr++, payment.getPayment_status());
				pstmt.setString(ctr++, payment.getId());
				pstmt.setString(ctr++, payment.getReceivedFrom());
				double amt = 0;
				if (payment.getPaymentAmount() != null) {
					amt = payment.getPaymentAmount().doubleValue();
				}
				pstmt.setDouble(ctr++, amt);
				pstmt.setString(ctr++, payment.getCurrencyCode());
				pstmt.setString(ctr++, payment.getReferenceNo());
				pstmt.setDate(ctr++, getSQLDateFromString(payment.getPaymentDate(), Constants.INVOICE_UI_DATE_FORMAT));
				pstmt.setString(ctr++, payment.getMemo());
				pstmt.setString(ctr++, payment.getCompanyId());
				pstmt.setString(ctr++, payment.getType());
				pstmt.setString(ctr++, payment.getPaymentNote());
				pstmt.setString(ctr++, payment.getDepositedTo());

				pstmt.setString(ctr++, payment.getReceivedFrom());
				pstmt.setDouble(ctr++, amt);
				pstmt.setString(ctr++, payment.getCurrencyCode());
				pstmt.setString(ctr++, payment.getReferenceNo());
				pstmt.setDate(ctr++, getSQLDateFromString(payment.getPaymentDate(), Constants.INVOICE_UI_DATE_FORMAT));
				pstmt.setString(ctr++, payment.getMemo());
				pstmt.setString(ctr++, payment.getCompanyId());
				pstmt.setString(ctr++, payment.getType());
				pstmt.setString(ctr++, payment.getPaymentNote());
				pstmt.setString(ctr++, payment.getDepositedTo());
				pstmt.setString(ctr++, payment.getPayment_status());
				int affectedRows = pstmt.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("");
				}
				deletePaymentLines(payment.getId(), connection);
				for (PaymentLine paymentLine : payment.getPaymentLines()) {
					addPaymentLine(connection, paymentLine, payment.getId());
					if (paymentLine.getAmount() != null && paymentLine.getAmount().doubleValue() > 0) {
						updateInvoicesState(connection, paymentLine, payment, lines, checkInvoiceAmountFlag);
					}
				}
			} catch (SQLException e) {
				System.out.println("exp" + e);
				throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
				LOGGER.debug("exited invoice payment save:" + payment);
			}
		}

		return payment;
	}

	private void updateInvoicesState(Connection connection, PaymentLine paymentLine, Payment payment, List<PaymentLine> lines, boolean checkInvoiceAmountFlag) {
		LOGGER.debug("entered updateInvoicesState(Connection connection, PaymentLine paymentLine:" + paymentLine + ", Payment payment:" + payment + ", List<PaymentLine> lines:"
				+ lines + ", boolean checkInvoiceAmountFlag:" + checkInvoiceAmountFlag);
		InvoiceDAOImpl invoiceDAOImpl = InvoiceDAOImpl.getInvoiceDAOImpl();
		PaymentLine lineFromDb = null;
		if (payment.getId() != null) {
			// List<PaymentLine> lines = getLines(payment.getId(), connection);
			for (PaymentLine line : lines) {
				if (line.getInvoiceId().equals(paymentLine.getInvoiceId())) {
					lineFromDb = line;
					break;
				}
			}
		}
		try {
			Invoice invoice = invoiceDAOImpl.get(paymentLine.getInvoiceId());
			double amountPaid = 0;
			if (invoice.getState() != null && invoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				return;
			}
//			if (!checkInvoiceAmountFlag) {
				if ((paymentLine.getAmount().doubleValue() + paymentLine.getDiscount()) > invoice.getAmount_due()) {
					throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
				}
				//		100								90										10
				if (invoice.getAmount_due() == ( paymentLine.getAmount().doubleValue() + paymentLine.getDiscount())) {
					invoice.setState(Constants.INVOICE_STATE_PAID);
					amountPaid = paymentLine.getAmount().doubleValue();
				} else {
					invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);
					if (lineFromDb != null) {
						amountPaid = paymentLine.getAmount().doubleValue() - lineFromDb.getAmount().doubleValue();
					} else {
						amountPaid = paymentLine.getAmount().doubleValue();
					}
				}
				invoice.setAmount_paid(invoice.getAmount_paid() + amountPaid );
				invoice.setAmount_due(invoice.getAmount_due() - amountPaid - paymentLine.getDiscount() );
				invoice.setDiscount(paymentLine.getDiscount());
//			} else if (checkInvoiceAmountFlag) {
//				// 			100									10								100
//				if ((paymentLine.getAmount().doubleValue() + paymentLine.getDiscount()) > invoice.getAmount()) {
//					throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
//				}
//				//	100							90										10
//				if (invoice.getAmount() == (paymentLine.getAmount().doubleValue() + paymentLine.getDiscount())) {
//					invoice.setState(Constants.INVOICE_STATE_PAID);
//					amountPaid = paymentLine.getAmount().doubleValue();
//				} else {
//					invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);
//					// if(lineFromDb != null) {
//					// amountPaid = paymentLine.getAmount().doubleValue() -
//					// lineFromDb.getAmount().doubleValue();
//					// } else {
//					// }
//					amountPaid = paymentLine.getAmount().doubleValue();
//				}
//				invoice.setAmount_paid(amountPaid);
//				invoice.setAmount_due(invoice.getAmount() - amountPaid);
//				invoice.setDiscount(paymentLine.getDiscount());
//			} else {
//				throw new WebApplicationException("unable to perform invoice amount validation", Constants.EXPECTATION_FAILED);
//			}
			invoiceDAOImpl.update(connection, invoice);
			InvoiceHistory invoice_history = InvoiceParser.getInvoice_history(invoice, UUID.randomUUID().toString(), invoice.getUser_id(), invoice.getCompany_id());
			MySQLManager.getInvoice_historyDAO().create(connection, invoice_history);
			if (invoice.getState().equalsIgnoreCase("paid")) {
				PayEventDAO payEventDao = new PayEventDAOImpl();
				PayEvent payEvent = new PayEvent();
				payEvent.setInvoiceID(invoice.getId());
				payEventDao.update(connection, payEvent);
			}
		} catch (Exception e) {
			throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
		} finally {
			LOGGER.debug("exited updateInvoicesState(Connection connection, PaymentLine paymentLine:" + paymentLine + ", Payment payment:" + payment + ", List<PaymentLine> lines:"
					+ lines + ", boolean checkInvoiceAmountFlag:" + checkInvoiceAmountFlag);
		}
	}

	private void deletePaymentLines(String paymentId, Connection connection) {
		LOGGER.debug("enterd deletePaymentLines(String paymentId:" + paymentId + ", Connection connection) ");
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.DELETE_QRY);
				pstmt.setString(ctr++, paymentId);
				pstmt.executeUpdate();

			} catch (SQLException e) {
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
			}
		}
		LOGGER.debug("exited deletePaymentLines(String paymentId:" + paymentId + ", Connection connection) ");
	}

	private void addPaymentLine(Connection connection, PaymentLine paymentLine, String paymentId) {
		LOGGER.debug("enterd addPaymentLine(Connection connection, PaymentLine paymentLine:" + paymentLine + ", String paymentId:" + paymentId);
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.INSERT_QRY);
				pstmt.setString(ctr++, UUID.randomUUID().toString());
				pstmt.setString(ctr++, paymentLine.getInvoiceId());
				double amt = 0;
				if (paymentLine.getAmount() != null) {
					amt = paymentLine.getAmount().doubleValue();
				}
				pstmt.setDouble(ctr++, amt);
				pstmt.setString(ctr++, paymentId);
				pstmt.setDouble(ctr++, paymentLine.getDiscount());
				int affectedRows = pstmt.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("");
				}

			} catch (SQLException e) {
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
				LOGGER.debug("exited addPaymentLine(Connection connection, PaymentLine paymentLine:" + paymentLine + ", String paymentId:" + paymentId);
			}
		}
	}

	private java.sql.Date getSQLDateFromString(String date, String format) {
		java.sql.Date parsedDate = null;
		LOGGER.debug("entered getSQLDateFromString(String date:" + date + ", String format:" + format);
		try {
			if (StringUtils.isNoneBlank(date, format)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				Date utilDate = dateFormat.parse(date);
				parsedDate = new java.sql.Date(utilDate.getTime());
			}
		} catch (Exception e) {
			throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), 400));
		} finally {
			LOGGER.debug("exited getSQLDateFromString(String date:" + date + ", String format:" + format);
		}
		return parsedDate;
	}

	private String getDateStringFromSQLDate(java.sql.Date date, String format) {
		LOGGER.debug("entered getDateStringFromSQLDate(java.sql.Date date:" + date + ", String format:" + format);
		String dateStr = null;
		if (date == null) {
			return null;
		}
		try {
			if (StringUtils.isNoneBlank(format)) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				Date utilDate = new Date(date.getTime());
				dateStr = dateFormat.format(utilDate);
			}
		} catch (Exception e) {
			throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), 400));
		} finally {
			LOGGER.debug("exited getDateStringFromSQLDate(java.sql.Date date:" + date + ", String format:" + format);
		}
		return dateStr;
	}

	private List<PaymentLine> getLines(String paymentId) {
		LOGGER.debug("entered getLines(String paymentId:" + paymentId);
		Connection connection = null;
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			List<PaymentLine> lines = new ArrayList<PaymentLine>();

			if (connection != null) {
				lines = getLines(paymentId, connection);
			}
			return lines;
		} catch (Exception e) {
			LOGGER.error("error in getLines(String paymentId:" + paymentId, e);
		} finally{
			DatabaseUtilities.closeResources(null, null, connection);
		}
		LOGGER.debug("entered getLines(String paymentId:" + paymentId);
		return null;
	}
	
	private List<PaymentLine> getLines(Connection connection, String paymentId) {
		LOGGER.debug("entered getLines(String paymentId:" + paymentId);
		try {
			List<PaymentLine> lines = new ArrayList<PaymentLine>();
			if (connection != null) {
				lines = getLines(paymentId, connection);
			}
			return lines;
		} catch (Exception e) {
			LOGGER.error("error in getLines(String paymentId:" + paymentId, e);
		} finally{
			LOGGER.debug("exited getLines(String paymentId:" + paymentId);
		}
		return null;
	}

	private List<PaymentLine> getLines(String paymentId, Connection connection) {
		LOGGER.debug("entered getLines(String paymentId:"+paymentId+", Connection connection)");
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<PaymentLine> lines = new ArrayList<PaymentLine>();

		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.GET_LIST_QRY);
				pstmt.setString(ctr++, paymentId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					PaymentLine line = new PaymentLine();
					line.setInvoiceId(rset.getString("invoice_id"));
					line.setAmount(new BigDecimal(rset.getString("payment_amount")));
					line.setInvoiceDate(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setTerm(rset.getString("term"));
					line.setState(rset.getString("state"));
					line.setInvoiceAmount(new BigDecimal(rset.getString("amount")));
					lines.add(line);
				}
			} catch (SQLException e) {
				LOGGER.error("error in getLines(String paymentId:"+paymentId+", Connection connection)",e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, null);
				LOGGER.debug("exited getLines(String paymentId:"+paymentId+", Connection connection)");
			}
		}
		return lines;
	}
	
	public List<PaymentLine> getLinesByInvoiceId(String invoiceId) {
		LOGGER.debug("entered getLines(String invoiceId:"+invoiceId);
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<PaymentLine> lines = new ArrayList<PaymentLine>();
		Connection connection = DatabaseUtilities.getReadConnection();
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.GET_LIST_QRY);
				pstmt.setString(ctr++, invoiceId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					PaymentLine line = new PaymentLine();
					line.setInvoiceId(rset.getString("invoice_id"));
					line.setAmount(new BigDecimal(rset.getString("payment_amount")));
					line.setInvoiceDate(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setTerm(rset.getString("term"));
					line.setState(rset.getString("state"));
					line.setInvoiceAmount(new BigDecimal(rset.getString("amount")));
					lines.add(line);
				}
			} catch (SQLException e) {
				LOGGER.error("error in getLines(String invoiceId:"+invoiceId,e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, null);
				LOGGER.debug("exited getLines(String invoiceId:"+invoiceId);
			}
		}
		return lines;
	}
	
	public Map<String, Double> getPaidAmountMap(String paymentIds) {
		LOGGER.debug("entered getPaidAmountMap(String paymentId:"+paymentIds);
		if(StringUtils.isBlank(paymentIds)){
			return null;
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = DatabaseUtilities.getReadConnection();
		if (connection != null) {
			try {
				Map<String, Double> paidAmountMap = new HashMap<>();
				String query = SqlQuerys.PaymentsLines.GET_PAID_AMOUNT_LIST_QRY;
				query += paymentIds+SqlQuerys.PaymentsLines.GET_PAID_AMOUNT_LIST_QRY_2;
				pstmt = connection.prepareStatement(query);
				LOGGER.debug("amount paid query: "+pstmt.toString());
				rset = pstmt.executeQuery();
				while (rset.next()) {
					paidAmountMap.put(rset.getString("payment_id"),rset.getDouble("applied_amount"));
				}
				return paidAmountMap;
			} catch (SQLException e) {
				LOGGER.error("error in getPaidAmountMap(String paymentIds:"+paymentIds,e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("exited getPaidAmountMap(String paymentIds:"+paymentIds);
			}
		}
		return null;
	}

	@Override
	public List<Payment> list(String companyId) {
		LOGGER.debug("entered list(String companyId:"+companyId);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Payment> payments = new ArrayList<Payment>();

		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.RETRIEVE_BY_COMPANYID_QRY);
				pstmt.setString(ctr++, companyId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Payment payment = new Payment();
					payment.setId(rset.getString("id"));
					int index = payments.indexOf(payment);
					if (index == -1) {
						payment.setPayment_status(rset.getString("payment_status"));
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(rset.getString("type"));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setCustomerName(rset.getString("customer_name"));
						payment.setPaymentLines(getLines(payment.getId()));
						String depositID = rset.getString("deposit_id");
						payment.setDepositID(depositID);
						if (depositID != null) {
							payment.setStatus("mapped");
						}
						payments.add(payment);
					} else {
						payment = payments.get(index);
					}
					String journalID = rset.getString("journal_id");
					if (StringUtils.isNotBlank(journalID) && rset.getBoolean("isActive")) {
						payment.setJournalID(journalID);
					}
				}
			} catch (SQLException e) {
				LOGGER.error("error list(String companyId:"+companyId,e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("exited list(String companyId:"+companyId);
			}
		}
		return payments;
	}
	
	@Override
	public List<Payment> listByInvoiceId(String invoiceId) {
		LOGGER.debug("entered listByInvoiceId(String invoiceId:"+invoiceId);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Payment> payments = new ArrayList<Payment>();

		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.RETRIEVE_BY_INVOICE_QRY);
				pstmt.setString(ctr++, invoiceId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					Payment payment = new Payment();
					payment.setId(rset.getString("id"));
					int index = payments.indexOf(payment);
					if (index == -1) {
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(rset.getString("type"));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setCustomerName(rset.getString("customer_name"));
						payment.setPaymentLines(getLines(payment.getId()));
						String depositID = rset.getString("deposit_id");
						payment.setDepositID(depositID);
						if (depositID != null) {
							payment.setStatus("mapped");
						}
						payments.add(payment);
					} else {
						payment = payments.get(index);
					}
					String journalID = rset.getString("journal_id");
					if (StringUtils.isNotBlank(journalID) && rset.getBoolean("isActive")) {
						payment.setJournalID(journalID);
					}
				}
			} catch (SQLException e) {
				LOGGER.error("error listByInvoiceId(String invoiceId:"+invoiceId,e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("exited listByInvoiceId(String invoiceId:"+invoiceId);
			}
		}
		return payments;
	}

	public List<Payment> getUnmappedPayment(String companyID, String bankAccountID, String depositId) {
		LOGGER.debug("retrieving unmapped payments with companyID" + companyID + " and bank accountID " + bankAccountID);
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		Payment invoicePayment = null;
		List<Payment> payments = new ArrayList<Payment>();
		List<String> ids = new ArrayList<String>();
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_UNMAPPED_PAYMENTS);
				pstmt.setString(1, companyID);
				pstmt.setString(2, bankAccountID);
				pstmt.setString(3, depositId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String paymentID = rset.getString("id");
					if (!ids.contains(paymentID)) {
						ids.add(paymentID);
						invoicePayment = new Payment();
						invoicePayment.setId(paymentID);
						invoicePayment.setCustomerName(rset.getString("customer_name"));
						invoicePayment.setDepositedTo(rset.getString("bank_account_id"));
						invoicePayment.setPaymentDate(rset.getString("payment_date"));
						invoicePayment.setAmountPaid(rset.getBigDecimal("payment_amount"));
						invoicePayment.setMappingID(rset.getString("mapping_id"));
						invoicePayment.setInvoiceDate(DateUtils.formatToString(rset.getTimestamp("invoice_date")));
						invoicePayment.setInvoiceNumber(rset.getString("number"));
						if(invoicePayment.getMappingID() == null ){
							invoicePayment.setMapping(false);
						}else{
							invoicePayment.setMapping(true);}
						payments.add(invoicePayment);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error while retrieving unmapped payments", e);
			throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, connection);
			LOGGER.debug("exited getUnmappedPayment(String companyID:"+companyID+", String bankAccountID:"+bankAccountID);
		}
		return payments;
	}

	public List<Payment> getUnmappedPaymentWithEntityId(String companyID, String bankAccountID, String entityID, String depositId) {
		LOGGER.debug("retrieving unmapped payments with companyID" + companyID + " and bank accountID " + bankAccountID + " and  entityID " + entityID);
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		Connection connection = null;
		Payment invoicePayment = null;
		List<Payment> payments = new ArrayList<Payment>();
		List<String> ids = new ArrayList<String>();
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_UNMAPPED_PAYMENTS_WITH_ENTITYID);
				pstmt.setString(1, companyID);
				pstmt.setString(2, bankAccountID);
				pstmt.setString(3, entityID);
				pstmt.setString(4, depositId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String paymentID = rset.getString("id");
					if (!ids.contains(paymentID)) {
						ids.add(paymentID);
						invoicePayment = new Payment();
						invoicePayment.setId(paymentID);
						invoicePayment.setCustomerName(rset.getString("customer_name"));
						invoicePayment.setDepositedTo(rset.getString("bank_account_id"));
						invoicePayment.setPaymentDate(rset.getString("payment_date"));
						invoicePayment.setAmountPaid(rset.getBigDecimal("payment_amount"));
						invoicePayment.setInvoiceDate(DateUtils.formatToString(rset.getTimestamp("invoice_date")));
						invoicePayment.setInvoiceNumber(rset.getString("number"));
						invoicePayment.setMappingID(rset.getString("mapping_id"));
						if(invoicePayment.getMappingID() == null ){
							invoicePayment.setMapping(false);
						}else{
							invoicePayment.setMapping(true);}
						payments.add(invoicePayment);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("error while retrieving unmapped payments with entityID", e);
			throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, connection);
			LOGGER.debug("exited getUnmappedPaymentWithEntityId(String companyID:"+companyID+", String bankAccountID:"+bankAccountID+", String entityID:"+entityID);
		}
		return payments;

	}

	@Override
	public Payment getById(String paymentId) {
		LOGGER.debug("entered getById(String paymentId:"+paymentId);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Payment payment = new Payment();
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.RETRIEVE_BY_PAYMENTID_QRY);
				pstmt.setString(ctr++, paymentId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
//					payment.setPayment_status(rset.getString("payment_status"));
					payment.setId(rset.getString("id"));
					payment.setReceivedFrom(rset.getString("received_from"));
					payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
					payment.setCurrencyCode(rset.getString("currency_code"));
					payment.setReferenceNo(rset.getString("reference_no"));
					payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
					payment.setMemo(rset.getString("memo"));
					payment.setType(rset.getString("type"));
					payment.setPaymentNote(rset.getString("payment_notes"));
					payment.setMapping_id(rset.getString("mapping_id"));
					payment.setDepositedTo(rset.getString("bank_account_id"));
					payment.setPaymentLines(getLines(connection ,payment.getId()));
				}
			} catch (SQLException e) {
				LOGGER.error("error in getById(String paymentId:"+paymentId, e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("entered getById(String paymentId:"+paymentId);
			}
		}
		return payment;
	}

}
