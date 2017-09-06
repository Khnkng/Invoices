package com.qount.invoice.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.qount.invoice.database.dao.impl.PaymentDAOImpl;
import com.qount.invoice.model.Payment;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class PaymentService {
	
	private static PaymentService instance = new PaymentService();
	
	private PaymentService() {
		
	}
	
	public static PaymentService getInstance() {
		return instance;
	}

	public Payment createOrUpdatePayment(Payment payment, String companyId, String userID) {
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		if (connection == null) {
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, "Database Error", Status.INTERNAL_SERVER_ERROR));
		}
		Payment pymt = null;
		try {
			connection.setAutoCommit(false);
			payment.setCompanyId(companyId);
			if(StringUtils.isBlank(payment.getId())) {
				payment.setId(UUID.randomUUID().toString());
			}
			pymt = PaymentDAOImpl.getInstance().save(payment, connection, true);
			connection.commit();
			CommonUtils.createJournal(new JSONObject().put("source", "invoicePayment").put("sourceID", payment.getId()).toString(), userID, companyId);
		} catch (SQLException e) {
				//connection.rollback();
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getLocalizedMessage(), Status.BAD_REQUEST));
			
		} finally {
			DatabaseUtilities.closeConnection(connection);
		}
		return pymt;
	}
	
	public List<Payment> getList(String companyId) {
		return PaymentDAOImpl.getInstance().list(companyId);
	}
	
	public List<Payment> getunmappedPayments(String companyID, String bankAccountID){
		return PaymentDAOImpl.getInstance().getUnmappedPayment(companyID, bankAccountID);
	}
	
	public Payment getById(String companyId, String paymentId) {
		return PaymentDAOImpl.getInstance().getById(paymentId);
	}
}
