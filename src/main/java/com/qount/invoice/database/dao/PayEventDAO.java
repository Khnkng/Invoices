package com.qount.invoice.database.dao;

import java.sql.Connection;

import com.qount.invoice.model.PayEvent;


public interface PayEventDAO {


	boolean update(Connection connection, PayEvent payEvent);

}
