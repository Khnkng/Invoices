package com.qount.invoice.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;

import com.qount.invoice.model.Config;
import com.qount.invoice.utils.DatabaseUtilities;

public class ConfigDAO {

	private static Map<String, Config> configParams = new HashMap<>();

	private static ConfigDAO configDAO = null;


	private static final Logger LOGGER = Logger.getLogger(ConfigDAO.class);
  
	public static ConfigDAO getInstance() {
		if (configDAO == null) {
			configDAO = new ConfigDAO();
		}
		return configDAO;
	}

	public Config getRow(String key) {
		Config config = null;
		config = configParams.get(key);
		if (config != null) {
			return config;
		}
		
		Connection conn = DatabaseUtilities.getReadConnection();  
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		long startTime = System.currentTimeMillis();

		try {
			int qryCtr = 1;
			if (conn != null) {
				pstmt = conn.prepareStatement("select user_name, password, source_type, info from  config WHERE source_type = ?");
				pstmt.setString(qryCtr++, key);
				rset = pstmt.executeQuery();
				if (rset != null && rset.next()) {
					config = new Config();
					config.setSource_type(key);
					config.setInfo(rset.getString("info"));
					config.setPassword(rset.getString("password"));
					config.setUser_name(rset.getString("user_name")); 
					return config;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error retrieving user", e);
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, conn);
			LOGGER.debug("execution time of "+ConfigDAO.class+".get = " + (System.currentTimeMillis() - startTime)
					+ " in mili seconds , User ID: " + key);
			System.out.println((System.currentTimeMillis() - startTime));
		}
		return null;
	}
	
	public Map<String, String> get() {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection connection = DatabaseUtilities.getReadConnection();
		Map<String, String> configParams = new HashMap<>();
		try {
			if (connection != null) {
				pstmt = connection.prepareStatement("SELECT * from config");
				rset = pstmt.executeQuery();
				while (rset.next()) {
					String key = rset.getString("name");
					String value = rset.getString("value");
					configParams.put(key, value);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error fetching configuration parameters", e);
		} finally {
			DatabaseUtilities.closeResources(rset, pstmt, connection);
		}
		return configParams;
	}
	

}
