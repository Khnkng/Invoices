package com.qount.invoice.schedular;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.qount.invoice.utils.DatabaseUtilities;

public class InvoiceStateJob implements Job {
	
	private static Logger LOGGER = Logger.getLogger(InvoiceStateJob.class);
	
	public InvoiceStateJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("starting to execute invoice update");
		ResultSet rset = null;
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = DatabaseUtilities.getReadWriteConnection();
			if (conn != null) {
//				String query = "UPDATE invoice_test SET state = 'past_due' WHERE id IN ( SELECT id  FROM (SELECT id FROM invoice_test WHERE state !='paid' AND due_date < ?) AS ids);";
				String query = "UPDATE invoice_test SET state = 'past_due' WHERE id IN ( SELECT id  FROM (SELECT id FROM invoice_test WHERE state !='paid' AND state ='sent' AND due_date < ?) AS ids);";
				pstmt = conn.prepareStatement(query);
				Date date = new Date(System.currentTimeMillis());
				String dateStr = date.toString()+" 00:00:00";
				pstmt.setString(1,dateStr);
				LOGGER.debug("Invoice State Job update query:"+query);
				LOGGER.debug("Invoice State Job update query param:"+dateStr);
				int result = pstmt.executeUpdate();
				LOGGER.debug("invoice update result:"+result);
			}
		}catch(WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		} finally {
			DatabaseUtilities.closeResultSet(rset);
			DatabaseUtilities.closeStatement(pstmt);
			LOGGER.debug("completed to execute invoice update");
		}
	}

	public static void main(String[] args) {
		Date date = new Date(System.currentTimeMillis());
		String dateStr = date.toString()+" 00:00:00";
		System.out.println(dateStr);
	}
}
