package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceHistory;

public interface Invoice_historyDAO {

	InvoiceHistory get(Connection conn, InvoiceHistory invoice_history);

	List<InvoiceHistory> getAll(Connection conn, InvoiceHistory input);

	InvoiceHistory delete(Connection conn, InvoiceHistory invoice_history);

	boolean deleteByIds(Connection conn, String commaSeparatedIds);

	InvoiceHistory create(Connection conn, InvoiceHistory invoice_history);
	
	List<InvoiceHistory> create(Connection conn, List<InvoiceHistory> invoice_history);
	
	InvoiceHistory update(Connection conn, InvoiceHistory invoice_history);

	String getByWebhookId(Connection conn, String webhookId);
	
	List<InvoiceHistory> getAllByInvoiceId(Connection conn, InvoiceHistory input);
	
	String getByInvoiceidAndAction(Connection conn, String invoiceId, String action);
	
	boolean isDuplicateEvent(Connection conn,InvoiceHistory invoice_history);
}