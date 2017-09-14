package com.qount.invoice.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;
import org.quartz.utils.ConnectionProvider;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mchange.v2.c3p0.DataSources;
import com.qount.invoice.common.PropertyManager;

/**
 * 
 * @author MateenAhmed
 * @version 1.0 25th Aug 2017
 */
public class ConnectionProviderImpl implements ConnectionProvider {

	private final static Logger LOGGER = Logger.getLogger(ConnectionProviderImpl.class);

	private Session session = null;
	private DataSource dataSource = null;

	public ConnectionProviderImpl() throws Exception {
		super(); // up to now, do nothing...
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void shutdown() throws SQLException {
		DataSources.destroy(dataSource);
	}

	@Override
	public void initialize() throws SQLException {
		setSession(connectSSHSession());
		dataSource = createDataSource();

	}

	private Session connectSSHSession() {
		Session session = null;
		String sshHost = PropertyManager.getProperty("mysql.remoteHost");// "ec2-54-175-105-44.compute-1.amazonaws.com";
		String sshuser = PropertyManager.getProperty("mysql.sshuser");// "ubuntu";
		String fileName = PropertyManager.getProperty("mysql.pem_file_name");// "qount-mysql-server.pem";
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		// File file = new File("src/main/resources/"+fileName);
		String SshKeyFilepath = file.getPath();
		int localPort = Integer.parseInt(PropertyManager.getProperty("mysql.localPort"));
		String remoteHost = PropertyManager.getProperty("mysql.localSSHUrl");// "127.0.0.1";
		int remotePort = Integer.parseInt(PropertyManager.getProperty("mysql.remotePort"));// 3306;
		String driverName = PropertyManager.getProperty("mysql.driverName");// "com.mysql.jdbc.Driver";
		try {
			java.util.Properties config = new java.util.Properties();
			JSch jsch = new JSch();
			session = jsch.getSession(sshuser, sshHost, 22);
			jsch.addIdentity(SshKeyFilepath);
			config.put("StrictHostKeyChecking", "no");
			config.put("ConnectionAttempts", "3");
			session.setConfig(config);
			session.connect();
			System.out.println("SSH Connected");
			Class.forName(driverName).newInstance();
			int assinged_port = session.setPortForwardingL(localPort, remoteHost, remotePort);
			System.out.println("Port Forwarded  localhost:" + assinged_port + " -> " + remoteHost + ":" + remotePort);
		} catch (Exception e) {
			LOGGER.error("Error", e);
			e.printStackTrace();
		}
		return session;
	}

	private DataSource createDataSource() {
		String dbuserName = PropertyManager.getProperty("mysql.dbuserName");
		String dbpassword = PropertyManager.getProperty("mysql.dbpassword");
		int localPort = Integer.parseInt(PropertyManager.getProperty("mysql.localPort"));
		String localSSHUrl = PropertyManager.getProperty("mysql.localSSHUrl");
		BasicDataSource dataSource = new BasicDataSource();
		String database = PropertyManager.getProperty("mysql.dataBaseName");
		dataSource.setUrl("jdbc:mysql://" + localSSHUrl + ":" + localPort + "/" + database);
		dataSource.setUsername(dbuserName);
		dataSource.setPassword(dbpassword);
		dataSource.setMaxOpenPreparedStatements(100);
		dataSource.setInitialSize(5);
		dataSource.setMaxTotal(30);
		dataSource.setMaxIdle(15);
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			if (conn != null) {
				System.out.println("connection creation success");
			}
		} catch (Exception e) {
			LOGGER.error("error createDataSource", e);
		} finally {
			closeConnection(conn);
		}
		return dataSource;
	}

	private static void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception ignore) {

		}
	}
}
