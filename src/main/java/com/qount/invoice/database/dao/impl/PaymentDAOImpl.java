package com.qount.invoice.database.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.PayEventDAO;
import com.qount.invoice.database.dao.paymentDAO;
import com.qount.invoice.database.mySQL.MySQLManager;
import com.qount.invoice.helper.InvoiceHistoryHelper;
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
import com.qount.invoice.utils.Utilities;

public class PaymentDAOImpl implements paymentDAO {

	public static PaymentDAOImpl instance = new PaymentDAOImpl();
	private static final Logger LOGGER = Logger.getLogger(PaymentDAOImpl.class);

	public static PaymentDAOImpl getInstance() {
		return instance;
	}

	private PaymentDAOImpl() {

	}

	@Override
	public Payment save(Payment payment, Connection connection, boolean saveInvoiceHistory) {
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			Timestamp currentDate = new Timestamp(System.currentTimeMillis());
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
				pstmt.setTimestamp(ctr++, currentDate);

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
						updateInvoicesState(connection, paymentLine, payment, lines, saveInvoiceHistory);
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

	
	@Override
	public Payment update(Payment payment, Connection connection,String paymentID) {
		LOGGER.debug("entered invoice payment update:" + payment);
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.UPDATE_QRY);
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
				pstmt.setString(ctr++, payment.getPayment_status());
				pstmt.setString(ctr++, payment.getId());
				pstmt.executeUpdate();
			} catch (SQLException e) {
				LOGGER.error("error in invoice payment update",e);
				throw new WebApplicationException(CommonUtils.constructResponse("no record inserted", Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
				LOGGER.debug("exited invoice payment update:" + payment);
			}
		}

		return payment;
	}

    public List<Invoice> updateInvoiceForPaymentLines(Payment payment, List<PaymentLine> dblines,List<Invoice> dbInvoiceList){
    	LOGGER.debug("entered updateInvoiceForPaymentLines:"+ payment.getPaymentLines());
    	List<Invoice> invoiceList = new ArrayList<Invoice>();
    	PaymentLine dbPaymentLine = null;
    	double invoiceDueAmount = 0;
    	try {
    		for (PaymentLine paymentLine : payment.getPaymentLines()){
    			int index = dblines.indexOf(paymentLine);
    			if (index!= -1) {
    				dbPaymentLine = dblines.get(index);
    			if (paymentLine.getId().equals(dbPaymentLine.getId())) {
    				for (Invoice invoice : dbInvoiceList) {
    					if (invoice.getId().equals(paymentLine.getInvoiceId())) {
    						if (paymentLine.getAmount()!=dbPaymentLine.getAmount() && paymentLine.getAmount().doubleValue()!=0) {
    			    				invoiceDueAmount=(invoice.getAmount_due()+dbPaymentLine.getAmount().doubleValue())-paymentLine.getAmount().doubleValue();
    			    				invoice.setAmount_due(invoiceDueAmount); 
    			    				if (invoiceDueAmount== 0) {
    			    					invoice.setState(Constants.INVOICE_STATE_PAID);	
    			    				}else{
    			    					invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);	
    			    				}invoice.setAmount_paid(invoice.getAmount()-invoiceDueAmount);
    						}else {
//    							if(paymentLine.getAmount().doubleValue()==0 && StringUtils.isNotBlank(paymentLine.getId()))
			        			//update invoice for deleted lines
								invoiceDueAmount=(invoice.getAmount_due()+dbPaymentLine.getAmount().doubleValue());
		    					invoice.setAmount_due(invoiceDueAmount); 
		    					if (invoiceDueAmount== 0) {
			    					invoice.setState(Constants.INVOICE_STATE_PAID);	
			    				}else{
			    					invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);	
			    				}invoice.setAmount_paid(invoice.getAmount()-invoiceDueAmount);
							}
    						invoiceList.add(invoice);
						}
					}
    			}
    		} else{
    		        //newly added lines should be updated with due amount in invoice
    			Invoice newInvoice = null;
    			newInvoice = PaymentDAOImpl.getInstance().updateInvoiceForNewlyAddedPaymentLines(paymentLine,dbInvoiceList,dbInvoiceList);
    			invoiceList.add(newInvoice);
    		}
    		}
		} catch (Exception e) {
			LOGGER.error("error in updateInvoiceForPaymentLines ",e);
		}
		return invoiceList;
   }
    
//    public Invoice updateInvoiceForDeletedPaymentLines(PaymentLine paymentLine ,List<Invoice> dbInvoiceList, List<Invoice> invoices ){
//    	double invoiceDueAmount = 0;
//    	try {
//    			for (Invoice invoice : dbInvoiceList) {
//    				if (invoice.getId().equals(paymentLine.getInvoiceId())) {
//    					invoiceDueAmount=(invoice.getAmount_due()+paymentLine.getAmount().doubleValue());
//    					invoice.setAmount_due(invoiceDueAmount); 
//    					if (invoiceDueAmount== 0) {
//    						invoice.setState(Constants.INVOICE_STATE_PAID);	
//    					}else{
//    						invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);	
//    					}invoice.setAmount_paid(invoice.getAmount()-invoiceDueAmount);
//    				}return invoice;
//					}
//		} catch (Exception e) {
//		}
//		return null;
//   }
    public Invoice updateInvoiceForNewlyAddedPaymentLines(PaymentLine paymentLine,List<Invoice> dbInvoiceList,List<Invoice> invoices ){
    	double invoiceDueAmount = 0;
    	try {
    			for (Invoice invoice : dbInvoiceList) {
    				if (invoice.getId().equals(paymentLine.getInvoiceId())) {
    				invoiceDueAmount=invoice.getAmount_due()-paymentLine.getAmount().doubleValue();
                    invoice.setAmount_due(invoiceDueAmount); 
    				if (invoiceDueAmount== 0) {
    				invoice.setState(Constants.INVOICE_STATE_PAID);	
					}else{
	    				invoice.setState(Constants.INVOICE_STATE_PARTIALLY_PAID);	
					}invoice.setAmount_paid(invoice.getAmount()-invoiceDueAmount);
					}
    				return invoice;
    			}
		} catch (Exception e) {
		}
		return null;
   }
    
	private void updateInvoicesState(Connection connection, PaymentLine paymentLine, Payment payment, List<PaymentLine> lines, boolean saveInvoiceHistoy) {
		LOGGER.debug("entered updateInvoicesState(Connection connection, PaymentLine paymentLine:" + paymentLine + ", Payment payment:" + payment + ", List<PaymentLine> lines:"
				+ lines + ", boolean saveInvoiceHistoy:" + saveInvoiceHistoy);
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
			invoiceDAOImpl.update(connection, invoice);
			if(saveInvoiceHistoy) {
				//creating invoice history 
				String description = "Amount: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount())+
						",Amount Due: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount_due())+
						",Amount Paid: "+Utilities.getNumberAsCurrencyStr(invoice.getCurrency(), invoice.getAmount_paid())+
						",Ref Num: "+payment.getReferenceNo()+
						",State: "+InvoiceParser.getDisplayState(invoice.getState());
				InvoiceHistory history = InvoiceHistoryHelper.getInvoiceHistory(invoice,description,InvoiceParser.getDisplayState(invoice.getState()));
				MySQLManager.getInvoice_historyDAO().create(connection, history);
			}
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
					+ lines + ", boolean saveInvoiceHistoy:" + saveInvoiceHistoy);
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

	public void batchdeletePaymentLines(List<PaymentLine> paymentLines, Connection connection) {
		LOGGER.debug("enterd batchdeletePaymentLines");
		PreparedStatement pstmt = null;
		if (connection != null) {
			try {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.DELETE_BY_ID_QRY);
				for (PaymentLine paymentLine :paymentLines){
					if (paymentLine.getAmount().doubleValue()==0 && StringUtils.isNotBlank(paymentLine.getId())) {
						int ctr = 1;
						pstmt.setString(ctr++, paymentLine.getId());
						pstmt.addBatch();
					}
				}
				pstmt.executeBatch();
			} catch (SQLException e) {
				LOGGER.error("error in batchdeletePaymentLines" ,e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
			}
		}
	}
	
	public void batchaddPaymentLine(Connection connection, List<PaymentLine> paymentLines,String paymentId) {
		LOGGER.debug("enterd batchaddPaymentLine " + paymentLines);
		PreparedStatement pstmt = null;
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());

			try {
				if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.INSERT_AND_UPDATE_QRY);
				for (PaymentLine paymentLine :paymentLines){
				int ctr = 1;
				if (StringUtils.isBlank(paymentLine.getId())) {
					paymentLine.setId(UUID.randomUUID().toString());
				}
				pstmt.setString(ctr++, paymentLine.getId());
				pstmt.setString(ctr++, paymentLine.getInvoiceId());
				double amt = 0;
				if (paymentLine.getAmount() != null) {
					amt = paymentLine.getAmount().doubleValue();
				}
				pstmt.setDouble(ctr++, amt);
				pstmt.setString(ctr++, paymentId);
				pstmt.setDouble(ctr++, paymentLine.getDiscount());
				pstmt.setTimestamp(ctr++, currentDate);
				
				pstmt.setDouble(ctr++, amt);
				pstmt.setDouble(ctr++, paymentLine.getDiscount());
				pstmt.addBatch();
				}
				pstmt.executeBatch();
			}	}
			 catch (SQLException e) {
				 LOGGER.error("error in batchaddPaymentLine",e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(null, pstmt, null);
			}
	}
	

	private void addPaymentLine(Connection connection, PaymentLine paymentLine, String paymentId) {
		LOGGER.debug("enterd addPaymentLine(Connection connection, PaymentLine paymentLine:" + paymentLine + ", String paymentId:" + paymentId);
		PreparedStatement pstmt = null;
		if (connection != null) {
			int ctr = 1;
			Timestamp currentDate = new Timestamp(System.currentTimeMillis());
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
				pstmt.setTimestamp(ctr++, currentDate);
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

	public List<PaymentLine> getLines(String paymentId, Connection connection) {
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
					line.setId(rset.getString("id"));
					line.setInvoiceId(rset.getString("invoice_id"));
					line.setAmount(new BigDecimal(rset.getString("payment_amount")));
					line.setInvoiceDate(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setTerm(rset.getString("term"));
					line.setDisplayState(InvoiceParser.getDisplayState(rset.getString("state")));
					line.setState(rset.getString("state"));
					line.setInvoiceAmount(new BigDecimal(rset.getString("amount")));
					line.setInvoiceDueDate(getDateStringFromSQLDate(rset.getDate("invoice_due_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setAmountDue(rset.getDouble("amount_due"));
					line.setInvoiceNumber(rset.getString("invoice_number"));
					line.setCreatedDate(DateUtils.formatToString(rset.getTimestamp("created_date")));
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
	
	public List<PaymentLine> getunmappedLinesOfcustomer(String customerID,Payment payment) {
		LOGGER.debug("entered getunmappedLinesOfcustomer(String customerID:"+customerID+")");
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<PaymentLine> lines =null;
		if (payment==null) {
			lines = new ArrayList<>();
		}else {
		 lines = payment.getPaymentLines();
		}
		List<PaymentLine> unmappedLines = new ArrayList<PaymentLine>() ;
		Connection connection = null;
			int ctr = 1;
			try {
				connection = DatabaseUtilities.getReadWriteConnection();
				if (connection != null) {
					StringBuilder queryBuilder = new StringBuilder();
					if(lines.size()!=0){
						queryBuilder.append("AND id NOT IN(");
					for (PaymentLine mappedLine: lines) {
						queryBuilder.append("'").append(mappedLine.getInvoiceId()).append("'").append(" ,");
					}
					queryBuilder.deleteCharAt(queryBuilder.length() - 1).append(")");
					}
				pstmt = connection.prepareStatement(SqlQuerys.PaymentsLines.GET_UNMAPPED_LINES_INVOICE_LIST_QRY+queryBuilder);
				pstmt.setString(ctr++, customerID);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					PaymentLine line = new PaymentLine();
					line.setInvoiceId(rset.getString("invoice_id"));
					line.setInvoiceDate(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setTerm(rset.getString("term"));
					line.setDisplayState(InvoiceParser.getDisplayState(rset.getString("state")));
					line.setState(rset.getString("state"));
					line.setInvoiceAmount(new BigDecimal(rset.getString("amount")));
					line.setInvoiceDueDate(getDateStringFromSQLDate(rset.getDate("due_date"), Constants.INVOICE_UI_DATE_FORMAT));
					line.setAmountDue(rset.getDouble("amount_due"));
					line.setInvoiceNumber(rset.getString("number"));
					unmappedLines.add(line);
				}lines.addAll(unmappedLines);
				}
			} catch (Exception e) {
				LOGGER.error("error in getunmappedLinesOfcustomer(String customerID:"+customerID+")",e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("exited getunmappedLinesOfcustomer(String customerID:"+customerID+")");
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
					line.setCreatedDate(DateUtils.formatToString(rset.getTimestamp("created_date")));
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
						payment.setPayment_status(WordUtils.capitalize(rset.getString("payment_status")));
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(WordUtils.capitalize(rset.getString("type")));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setCustomerName(rset.getString("customer_name"));
						payment.setPaymentLines(getLines(connection, payment.getId()));
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
						payment.setPayment_status(WordUtils.capitalize(rset.getString("payment_status")));
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(WordUtils.capitalize(rset.getString("type")));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setCustomerName(rset.getString("customer_name"));
						payment.setPaymentLines(getLines(connection, payment.getId()));
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
					payment.setPayment_status(WordUtils.capitalize(rset.getString("payment_status")));
					payment.setId(rset.getString("id"));
					payment.setReceivedFrom(rset.getString("received_from"));
					payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
					payment.setCurrencyCode(rset.getString("currency_code"));
					payment.setReferenceNo(rset.getString("reference_no"));
					payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
					payment.setMemo(rset.getString("memo"));
					payment.setType(WordUtils.capitalize(rset.getString("type")));
					payment.setPaymentNote(rset.getString("payment_notes"));
//					payment.setMapping_id(rset.getString("mapping_id"));
					payment.setDepositedTo(rset.getString("bank_account_id"));
					payment.setCreatedDate(DateUtils.formatToString(rset.getTimestamp("created_date")));
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

	@Override
	public List<Invoice> getIvoicesByPaymentID(String paymentId) {
		LOGGER.debug("entered getIvoicesByPaymentID paymentId:"+paymentId);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		List<Invoice> invoices = new ArrayList<Invoice>();
		Invoice invoice = null;
		if (connection != null) {
			int ctr = 1;
			try {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.RETRIEVE_INVOICES_BY_PAYMENTID);
				pstmt.setString(ctr++, paymentId);
				rset = pstmt.executeQuery();
				while (rset.next()) {
					invoice = new Invoice();
					invoice.setId(rset.getString("invoice_id"));
					invoice.setNumber(rset.getString("number"));
					invoice.setAmount(rset.getDouble("amount"));
					invoice.setAmount_due(rset.getDouble("amount_due"));
					invoice.setInvoice_date(getDateStringFromSQLDate(rset.getDate("invoice_date"), Constants.INVOICE_UI_DATE_FORMAT));
					invoice.setDue_date(getDateStringFromSQLDate(rset.getDate("due_date"), Constants.INVOICE_UI_DATE_FORMAT));
					invoice.setState(rset.getString("state"));
					invoice.setCustomer_name(rset.getString("customer_name"));
					invoice.setCurrency(rset.getString("currency"));
					invoices.add(invoice);
				}
			} catch (SQLException e) {
				LOGGER.error("error in getIvoicesByPaymentID paymentId:"+paymentId, e);
				throw new WebApplicationException(CommonUtils.constructResponse(e.getLocalizedMessage(), Constants.DATABASE_ERROR_STATUS));
			} finally {
				DatabaseUtilities.closeResources(rset, pstmt, connection);
				LOGGER.debug("closed connection in getIvoicesByPaymentID: " +paymentId);
			}
		}
		return invoices;
	}
	
	@Override
	public List<Payment> getUnappliedPayments(String companyID) throws Exception {
		LOGGER.debug("entered get Unapplied Payments: companyID" + companyID);
		if (StringUtils.isEmpty(companyID)) {
			throw new WebApplicationException("companyID cannot be empty", Constants.INVALID_INPUT_STATUS);
		}
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = null;
		List<Payment> payments = new ArrayList<Payment>();
		Payment payment = null; 
		try {
			connection = DatabaseUtilities.getReadWriteConnection();
			if (connection != null) {
				pstmt = connection.prepareStatement(SqlQuerys.Payments.RETRIEVE_UNAPPLIED_AMOUNT);
				pstmt.setString(1, companyID);
				rset = pstmt.executeQuery();
				while(rset.next()) {
					payment = new Payment();
					payment.setId(rset.getString("id"));
					int index = payments.indexOf(payment);
					if (index == -1) {
						payment.setPayment_status(WordUtils.capitalize(rset.getString("payment_status")));
						payment.setReceivedFrom(rset.getString("received_from"));
						payment.setPaymentAmount(new BigDecimal(rset.getDouble("payment_amount")));
						payment.setCurrencyCode(rset.getString("currency_code"));
						payment.setReferenceNo(rset.getString("reference_no"));
						payment.setPaymentDate(getDateStringFromSQLDate(rset.getDate("payment_date"), Constants.INVOICE_UI_DATE_FORMAT));
						payment.setMemo(rset.getString("memo"));
						payment.setType(WordUtils.capitalize(rset.getString("type")));
						payment.setPaymentNote(rset.getString("payment_notes"));
						payment.setDepositedTo(rset.getString("bank_account_id"));
						payment.setCustomerName(rset.getString("customer_name"));
//						payment.setPaymentLines(getLines(connection, payment.getId()));
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
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching Unapplied Payments: companyID" + companyID, e);
			throw e;
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			DatabaseUtilities.closeConnection(connection);
			LOGGER.debug("exited get Unapplied Payments: companyID" + companyID);
		}
		return payments;
	}
}
