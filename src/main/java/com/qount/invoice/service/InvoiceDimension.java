package com.qount.invoice.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qount.invoice.database.dao.InvoiceLineDimensionDAO;
import com.qount.invoice.database.dao.impl.InvoiceLineDimensionDAOImpl;
import com.qount.invoice.model.Dimension;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.DatabaseUtilities;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceDimension {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceDimension.class);
	
	public void create(Connection connection,String companyID, List<InvoiceLine> invoiceLines){
		LOGGER.debug("creating invoice Dimensions from invoice lines");
		if(connection != null){
			List<Dimension> dimensions = new ArrayList<>();
			for(InvoiceLine invoiceLine : invoiceLines){
				for(Dimension dimension : invoiceLine.getDimensions()){
					dimension.setCompanyID(companyID);
					dimension.setInvoiceLineID(invoiceLine.getId());
					dimensions.add(dimension);
				}
			}
			if(dimensions.size() > 0){
				if(!InvoiceLineDimensionDAOImpl.getInstance().savelist(connection, dimensions)){
					LOGGER.debug("Error creating invoice dimensions");
					throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		String lines = "{\"id\":\"0bd66bb6-b45b-49c3-97e6-edaa04b48d0a\",\"customer_id\":\"78864f91-cbb8-489c-a870-407c6d39f877\",\"company_id\":\"fa0b8c60-4347-4fb1-9982-4ebba798b108\",\"invoice_date\":\"07/15/17\",\"due_date\":\"09/13/17\",\"customer_name\":\"\",\"amount\":18000,\"term\":\"net60\",\"notes\":\"\",\"currency\":\"USD\",\"number\":\"Tst-101\",\"discount\":\"\",\"amount_paid\":\"\",\"sub_total\":18000,\"send_to\":\"9d5e52b1-675f-42e0-85e7-2923528065d3\",\"tax_amount\":0,\"payment_options\":\"\",\"amount_due\":18000,\"invoiceLines\":[{\"item_id\":\"2fb4254b-00f1-48ad-81b8-77cca34ebf1e\",\"description\":\"1 liter\",\"quantity\":\"900.0000\",\"price\":\"20.00\",\"name\":null,\"amount\":18000,\"destroy\":null,\"type\":\"item\",\"tax_id\":null,\"id\":\"3d9c7f0b-8c38-43ec-9cd0-5163754da862\",\"item\":{\"name\":\"bottle\"},\"dimensions\":[{\"createdDate\":0,\"modifiedDate\":0,\"name\":\"dimen_test\",\"values\":[\"123\"]}]}],\"recepientsMails\":[\"rama.raju@qount.io\"],\"sendMail\":false,\"user_id\":\"uday.koorella@qount.io\"}";
		ObjectMapper objectMapper = new ObjectMapper();
		Invoice invoice = objectMapper.readValue(lines, Invoice.class);
		Connection connection = DatabaseUtilities.getReadWriteConnection();
		InvoiceDimension dimension = new InvoiceDimension();
		dimension.update(connection, "fa0b8c60-4347-4fb1-9982-4ebba798b108", invoice.getInvoiceLines());
	}
	
	public void update(Connection connection, String companyID, List<InvoiceLine> invoiceLines){
		LOGGER.debug("updating invoice Dimensions from invoice lines");
		if(connection != null){
			List<Dimension> dimensions = new ArrayList<>();
			for(InvoiceLine invoiceLine : invoiceLines){
				for(Dimension dimension : invoiceLine.getDimensions()){
					dimension.setCompanyID(companyID);
					dimension.setInvoiceLineID(invoiceLine.getId());
					dimensions.add(dimension);
				}
			}
			if(dimensions.size() > 0){
			InvoiceLineDimensionDAO invoiceLineDimensionDAO	= InvoiceLineDimensionDAOImpl.getInstance();
			if(!invoiceLineDimensionDAO.delete(connection, invoiceLines)){
				LOGGER.debug("Error deleting dimensions for a invoice");
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
			}
				if(!InvoiceLineDimensionDAOImpl.getInstance().savelist(connection, dimensions)){
					LOGGER.debug("Error creating invoice dimensions");
					throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
				}
			}
		}
	}
	
	
	
	

}
