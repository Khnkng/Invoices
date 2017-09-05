package com.qount.invoice.database.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.paymentDAO;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.Payment;
import com.qount.invoice.model.PaymentLine;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.SqlQuerys;

public class PaymentDAOImpl implements paymentDAO{
	
	public static PaymentDAOImpl instance = new PaymentDAOImpl();
	private static final Logger LOGGER = Logger.getLogger(PaymentDAOImpl.class);
	
	public static PaymentDAOImpl getInstance() {
		return instance;
	}
	
	private PaymentDAOImpl() {
		
	}
	
	@Override
	public Payment save(Payment payment, Connection connection) {
		PreparedStatement pstmt = null;
			if (connection != null) {
				int ctr = 1;
				try {
					LOGGER.debug("entered invoice payment save:"+payment);
					List<PaymentLine> lines = getLines(payment.getId(), connection);
					pstmt = connection.prepareStatement(SqlQuerys.Payments.INSERT_QRY);
					pstmt.setString(ctr++, payment.getId());
					pstmt.setString(ctr++, payment.getReceivedFrom());
					double amt = 0;
					if(payment.getPaymentAmount() != null) {
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
					int affectedRows = pstmt.executeUpdate();
					if (affectedRows == 0) {
			            throw new SQLException("");
			        } 
					deletePaymentLines(payment.getId(), connection);
					for(PaymentLine paymentLine:payment.getPaymentLines()) {
						addPaymentLine(connection,paymentLine, payment.getId());
						if(paymentLine.getAmount() != null && paymentLine.getAmount().doubleValue() > 0) {							
							updateInvoicesState(connection, paymentLine, payment, lines);
						}
					}
				} catch (SQLException e) {
					System.out.println("exp"+e);
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(null, pstmt, null);
					LOGGER.debug("exited invoice payment save:"+payment);
				}
			}
		
		return payment;
	}
	
	private void updateInvoicesState(Connection connection, PaymentLine paymentLine, Payment payment, List<PaymentLine> lines) {
		InvoiceDAOImpl invoiceDAOImpl = InvoiceDAOImpl.getInvoiceDAOImpl();
		PaymentLine lineFromDb = null;
		if(payment.getId() != null) {			
			//List<PaymentLine> lines = getLines(payment.getId(), connection);
			for(PaymentLine line: lines) {
				if(line.getInvoiceId().equals(paymentLine.getInvoiceId())) {
					lineFromDb = line;
					break;
				}
			}
		}
		try {
			Invoice invoice = invoiceDAOImpl.get(paymentLine.getInvoiceId());
			double amountPaid = 0;
			if(invoice.getState() != null && invoice.getState().equals(Constants.INVOICE_STATE_PAID)) {
				return;
			}
			if (paymentLine.getAmount().doubleValue() > invoice.getAmount_due()) {
				throw new WebApplicationException(PropertyManager.getProperty("invoice.amount.greater.than.error"));
			}
			if (invoice.getAmount_due() == paymentLine.getAmount().doubleValue()) {
				invoice.setState(Constants.INVOICE_STATE_PAID);
				amountPaid = paymentLine.getAmount().doubleValue();
			} else {
				invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);	
				if(lineFromDb != null) {					
					amountPaid = paymentLine.getAmount().doubleValue() - lineFromDb.getAmount().doubleValue();
				} else {
					amountPaid = paymentLine.getAmount().doubleValue();
				}
			}
			invoice.setAmount_paid(invoice.getAmount_paid() + amountPaid);
			invoice.setAmount_due(invoice.getAmount_due() - amountPaid);
			invoiceDAOImpl.update(connection, invoice);
		} catch (Exception e) {
			throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
		} 
	}
	
	private void deletePaymentLines(String paymentId, Connection connection) {
		PreparedStatement pstmt = null;
			if (connection != null) {
				int ctr = 1;
				try {
					pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.DELETE_QRY);
					pstmt.setString(ctr++, paymentId);
					pstmt.executeUpdate();

				} catch (SQLException e) {
					throw new WebApplicationException(CommonUtils.constructResponse("unable to delete payment lines", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(null, pstmt, null);
				}
			}
	}
	
	private void addPaymentLine(Connection connection, PaymentLine paymentLine, String paymentId) {
		PreparedStatement pstmt = null;
			if (connection != null) {
				int ctr = 1;
				try {
					pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.INSERT_QRY);
					pstmt.setString(ctr++, UUID.randomUUID().toString());
					pstmt.setString(ctr++, paymentLine.getInvoiceId());
					double amt = 0;
					if(paymentLine.getAmount() != null) {
						amt = paymentLine.getAmount().doubleValue();
					}
					pstmt.setDouble(ctr++, amt);
					pstmt.setString(ctr++, paymentId);
					int affectedRows = pstmt.executeUpdate();
					if (affectedRows == 0) {
			            throw new SQLException("");
			        }

				} catch (SQLException e) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(null, pstmt, null);
				}
			}
	}
	
	private java.sql.Date getSQLDateFromString(String date, String format) {
		java.sql.Date parsedDate = null;
        try {
            if (StringUtils.isNoneBlank(date, format)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                Date utilDate = dateFormat.parse(date);
                parsedDate = new java.sql.Date(utilDate.getTime());
            }
        } catch (Exception e) {
        	throw new WebApplicationException(CommonUtils.constructResponse("cannot parse date", 400));
        }
        return parsedDate;
    }
	
	private String getDateStringFromSQLDate(java.sql.Date date, String format) {
		String dateStr = null;
		if(date==null){
			return null;
		}
		try {
            if (StringUtils.isNoneBlank(format)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                Date utilDate = new Date(date.getTime());
                dateStr = dateFormat.format(utilDate);
            }
        } catch (Exception e) {
        	throw new WebApplicationException(CommonUtils.constructResponse("cannot parse date", 400));
        }
        return dateStr;
	}
	
	private List<PaymentLine> getLines(String paymentId) {
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		List<PaymentLine> lines = new ArrayList<PaymentLine>();
		
			if (connection != null) {
				lines = getLines(paymentId, connection);
				DatabaseUtilities.closeResources(null, null, connection);
			}
			return lines;
	}
	
	private List<PaymentLine> getLines(String paymentId, Connection connection) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<PaymentLine> lines = new ArrayList<PaymentLine>();
		
			if (connection != null) {
				int ctr = 1;
				try {
					pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.GET_LIST_QRY);
					pstmt.setString(ctr++, paymentId);
					rset = pstmt.executeQuery();
					while(rset.next()) {
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
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(rset, pstmt, null);
				}
			}
			return lines;
	}

	@Override
	public List<Payment> list(String companyId) {
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
					while(rset.next()) {
						Payment payment = new Payment();
						payment.setId(rset.getString("id"));
						int index = payments.indexOf(payment);
						if(index == -1){
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
						payments.add(payment);
						}
						else{
							payment = payments.get(index);
						}
						String journalID =rset.getString("journal_id");
						if(StringUtils.isNotBlank(journalID)&& rset.getBoolean("isActive")){
							payment.setJournalID(journalID);
						}
					}
				} catch (SQLException e) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(rset, pstmt, connection);
				}
			}	
		return payments;	
	}
	
    public List<Payment> getUnmappedPayment(String companyID, String bankAccountID){
    	LOGGER.debug("retrieving unmapped payments with companyID" + companyID + " and bank accountID "+ bankAccountID);
	   ResultSet rset = null;
	   PreparedStatement pstmt = null;
	   Connection connection = null;
	   Payment invoicePayment = null;
	   List<Payment> payments = new ArrayList<Payment>();
	   List<String> ids = new ArrayList<String>();
	   try {
		connection=DatabaseUtilities.getReadWriteConnection();
		if(connection!=null){
			pstmt = connection.prepareStatement(SqlQuerys.Invoice.GET_UNMAPPED_PAYMENTS);
			pstmt.setString(1, companyID);
			pstmt.setString(2, bankAccountID);
			rset = pstmt.executeQuery();
			while(rset.next()){
				String paymentID = rset.getString("id");
			if(!ids.contains(paymentID)){
			    ids.add(paymentID);
				invoicePayment = new Payment();
				invoicePayment.setId(paymentID);
				invoicePayment.setCustomerName(rset.getString("customer_name"));
				invoicePayment.setDepositedTo(rset.getString("bank_account_id"));
				invoicePayment.setPaymentDate(rset.getString("payment_date"));
				invoicePayment.setAmountPaid(rset.getBigDecimal("amount"));
				payments.add(invoicePayment);
				}
			else{
				invoicePayment.setAmountPaid(rset.getBigDecimal("amount").add(invoicePayment.getAmountPaid()));
			}
			}
		}
	} catch (Exception e) {
		LOGGER.error("error while retrieving unmapped payments",e);
	}
	   finally {
			DatabaseUtilities.closeResources(rset, pstmt, connection);
	}
	return payments;
	
}

	@Override
	public Payment getById(String paymentId) {
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
					while(rset.next()) {
						payment.setId(rset.getString("id"));
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));		
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(rset.getString("type"));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setPaymentLines(getLines(payment.getId()));
					}
				} catch (SQLException e) {
					throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
				} finally {
					DatabaseUtilities.closeResources(rset, pstmt, connection);
				}
			}	
		return payment;		}

}
