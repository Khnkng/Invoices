package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoiceDiscounts;

public interface InvoiceDiscountsDAO {

	InvoiceDiscounts get(Connection conn, InvoiceDiscounts invoice_discounts);

	List<InvoiceDiscounts> getAll(Connection conn, InvoiceDiscounts input);

	InvoiceDiscounts delete(Connection conn, InvoiceDiscounts invoice_discounts);

	boolean deleteAllDiscounts(Connection conn, String companyID);

	InvoiceDiscounts create(Connection conn, InvoiceDiscounts invoice_discounts);

	InvoiceDiscounts update(Connection conn, InvoiceDiscounts invoice_discounts);

}