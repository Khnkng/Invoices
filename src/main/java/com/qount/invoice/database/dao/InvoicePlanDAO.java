package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.util.List;

import com.qount.invoice.model.InvoicePlan;
/**
 * DAO interface for InvoicePlanDAOImpl
 * 
 * @author Mateen, Qount.
 * @version 1.0, 25 Jan 2017
 *
 */
public interface InvoicePlanDAO {

	InvoicePlan get(Connection conn, InvoicePlan invoicePlan);

	List<InvoicePlan> getAll(Connection conn);

	InvoicePlan delete(Connection conn, InvoicePlan invoicePlan);

	InvoicePlan create(Connection conn, InvoicePlan invoicePlan);

	InvoicePlan update(Connection conn, InvoicePlan invoicePlan);
}
