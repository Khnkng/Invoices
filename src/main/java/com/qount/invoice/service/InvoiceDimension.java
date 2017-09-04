package com.qount.invoice.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.qount.invoice.database.dao.InvoiceLineDimensionDAO;
import com.qount.invoice.database.dao.impl.InvoiceLineDimensionDAOImpl;
import com.qount.invoice.model.Dimension;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

public class InvoiceDimension {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceDimension.class);
	
	public void create(Connection connection, List<InvoiceLine> invoiceLines){
		LOGGER.debug("creating invoice Dimensions from invoice lines");
		if(connection != null){
			List<Dimension> dimensions = new ArrayList<>();
			for(InvoiceLine invoiceLine : invoiceLines){
				dimensions.addAll(invoiceLine.getDimensions());
			}
			if(dimensions.size() > 0){
				if(!InvoiceLineDimensionDAOImpl.getInstance().savelist(connection, dimensions)){
					LOGGER.debug("Error creating invoice dimensions");
					throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
				}
			}
		}
	}
	
	public void update(Connection connection, List<InvoiceLine> invoiceLines){
		LOGGER.debug("updating invoice Dimensions from invoice lines");
		if(connection != null){
			List<Dimension> dimensions = new ArrayList<>();
			for(InvoiceLine invoiceLine : invoiceLines){
				dimensions.addAll(invoiceLine.getDimensions());
			}
			if(dimensions.size() > 0){
			InvoiceLineDimensionDAO invoiceLineDimensionDAO	= InvoiceLineDimensionDAOImpl.getInstance();
			if(!invoiceLineDimensionDAO.delete(connection, invoiceLines)){
				LOGGER.debug("Error deleting dimensions for a invoice");
				throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
			}
				if(InvoiceLineDimensionDAOImpl.getInstance().savelist(connection, dimensions)){
					LOGGER.debug("Error creating invoice dimensions");
					throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, Constants.FAILURE_STATUS_STR, Status.EXPECTATION_FAILED));
				}
			}
		}
	}
	
	
	
	

}
