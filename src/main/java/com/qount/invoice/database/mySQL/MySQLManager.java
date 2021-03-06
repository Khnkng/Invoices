package com.qount.invoice.database.mySQL;

import java.io.File;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.database.dao.CompanyDAO;
import com.qount.invoice.database.dao.CurrencyDAO;
import com.qount.invoice.database.dao.CustomerDAO;
import com.qount.invoice.database.dao.DiscountsRangesDAO;
import com.qount.invoice.database.dao.InvoiceDAO;
import com.qount.invoice.database.dao.InvoiceDiscountsDAO;
import com.qount.invoice.database.dao.InvoiceLineDAO;
import com.qount.invoice.database.dao.InvoicePlanDAO;
import com.qount.invoice.database.dao.InvoicePreferenceDAO;
import com.qount.invoice.database.dao.Invoice_historyDAO;
import com.qount.invoice.database.dao.ProposalDAO;
import com.qount.invoice.database.dao.ProposalLineDAO;
import com.qount.invoice.database.dao.paymentDAO;
import com.qount.invoice.database.dao.impl.CompanyDAOImpl;
import com.qount.invoice.database.dao.impl.CurrencyDAOImpl;
import com.qount.invoice.database.dao.impl.CustomerDAOImpl;
import com.qount.invoice.database.dao.impl.DiscountsRangesDAOImpl;
import com.qount.invoice.database.dao.impl.InvoiceDAOImpl;
import com.qount.invoice.database.dao.impl.InvoiceDiscountsDAOImpl;
import com.qount.invoice.database.dao.impl.InvoiceLineDAOImpl;
import com.qount.invoice.database.dao.impl.InvoicePlanDAOImpl;
import com.qount.invoice.database.dao.impl.InvoicePreferenceDAOImpl;
import com.qount.invoice.database.dao.impl.Invoice_historyDAOImpl;
import com.qount.invoice.database.dao.impl.PaymentDAOImpl;
import com.qount.invoice.database.dao.impl.ProposalDAOImpl;
import com.qount.invoice.database.dao.impl.ProposalLineDAOImpl;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.DatabaseUtilities;

public class MySQLManager {

	private static final Logger LOGGER = Logger.getLogger(MySQLManager.class);

	private static final MySQLManager MY_SQL_MANAGER = new MySQLManager();

	private Session session = connectSSHSession();

	private DataSource dataSource = createDataSource();

	private static ProposalDAO proposalDAO = null;

	private static ProposalLineDAO proposalLineDAO = null;

	private static InvoiceDAO invoiceDAO = null;

	private static InvoiceLineDAO invoiceLineDAO = null;

	private static InvoicePreferenceDAO invoicePreferenceDAO = null;

	private static paymentDAO paymentDAO = null;

	private static CurrencyDAO currencyDAO = null;

	private static CustomerDAO customerDAO = null;

	private static CompanyDAO companyDAO = null;

	private static InvoicePlanDAO invoicePlanDAO = null;

	private static Invoice_historyDAO INVOICE_HISTORY_DAO = null;

	private static InvoiceDiscountsDAO invoiceDiscounts = null;

	private static DiscountsRangesDAO discountsRanges = null;

	private MySQLManager() {

	}

	public static MySQLManager getInstance() {
		return MY_SQL_MANAGER;
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
		dataSource.setMaxTotal(20);
		dataSource.setMaxIdle(15);
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			if (conn != null) {
				System.out.println("connection creation success");
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		} finally {
			DatabaseUtilities.closeConnection(conn);
		}
		return dataSource;
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

	public DataSource getDataSource() {
		return dataSource;
	}

	public Session getJCHSession() {
		return session;
	}

	public static ProposalDAO getProposalDAOInstance() {
		if (proposalDAO == null) {
			proposalDAO = ProposalDAOImpl.getProposalDAOImpl();
		}
		return proposalDAO;
	}

	public static ProposalLineDAO getProposalLineDAOInstance() {
		if (proposalLineDAO == null) {
			proposalLineDAO = ProposalLineDAOImpl.getProposalLineDAOImpl();
		}
		return proposalLineDAO;
	}

	public static InvoiceDAO getInvoiceDAOInstance() {
		if (invoiceDAO == null) {
			invoiceDAO = InvoiceDAOImpl.getInvoiceDAOImpl();
		}
		return invoiceDAO;
	}

	public static InvoiceLineDAO getInvoiceLineDAOInstance() {
		if (invoiceLineDAO == null) {
			invoiceLineDAO = InvoiceLineDAOImpl.getInvoiceLineDAOImpl();
		}
		return invoiceLineDAO;
	}

	public static InvoicePreferenceDAO getInvoicePreferenceDAOInstance() {
		if (invoicePreferenceDAO == null) {
			invoicePreferenceDAO = InvoicePreferenceDAOImpl.getInvoicePreferenceDAOImpl();
		}
		return invoicePreferenceDAO;
	}

	public static paymentDAO getPaymentDAOInstance() {
		if (paymentDAO == null) {
			paymentDAO = PaymentDAOImpl.getInstance();
		}
		return paymentDAO;
	}

	public static CurrencyDAO getCurrencyDAOInstance() {
		if (currencyDAO == null) {
			currencyDAO = CurrencyDAOImpl.getCurrencyDAOImpl();
		}
		return currencyDAO;
	}

	public static CustomerDAO getCustomerDAOInstance() {
		if (customerDAO == null) {
			customerDAO = CustomerDAOImpl.getCustomerDAOImpl();
		}
		return customerDAO;
	}

	public static CompanyDAO getCompanyDAOInstance() {
		if (companyDAO == null) {
			companyDAO = CompanyDAOImpl.getCompanyDAOImpl();
		}
		return companyDAO;
	}

	public static InvoicePlanDAO getInvoicePlanDAOInstance() {
		if (invoicePlanDAO == null) {
			invoicePlanDAO = InvoicePlanDAOImpl.getInvoicePlanDAOImpl();
		}
		return invoicePlanDAO;
	}

	public static Invoice_historyDAO getInvoice_historyDAO() {
		if (INVOICE_HISTORY_DAO == null) {
			INVOICE_HISTORY_DAO = Invoice_historyDAOImpl.getInvoice_historyDAOImpl();
		}
		return INVOICE_HISTORY_DAO;
	}

	public static InvoiceDiscountsDAO getInvoiceDiscountsDAO() {
		if (invoiceDiscounts == null) {
			invoiceDiscounts = InvoiceDiscountsDAOImpl.getInvoice_discountsDAOImpl();
		}
		return invoiceDiscounts;
	}

	public static DiscountsRangesDAO getDiscountsRangesDAO() {
		if (discountsRanges == null) {
			discountsRanges = DiscountsRangesDAOImpl.getDiscountsRangesDAOImpl();
		}
		return discountsRanges;
	}
}
