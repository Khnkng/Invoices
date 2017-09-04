package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.Dimension;
import com.qount.invoice.model.InvoiceLine;

public interface InvoiceLineDimensionDAO {
	
	public boolean save(Connection connection, String invoiceLineID, Dimension invoiceLineDimension);
	public boolean savelist(Connection connection, List<Dimension> invoiceLineDimensions);
	public boolean delete(Connection connection, List<InvoiceLine> lines);

}
