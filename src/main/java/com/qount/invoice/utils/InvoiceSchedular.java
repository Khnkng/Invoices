package com.qount.invoice.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.hibernate.validator.internal.util.privilegedactions.NewJaxbContext;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;

import com.qount.invoice.parser.InvoiceParser;


public class InvoiceSchedular {
	private static final Logger LOGGER = Logger.getLogger(InvoiceSchedular.class);

	public static void main(String[] args) {
		String uuid = UUID.randomUUID().toString();
		String jobName = "job_" + uuid;
		String triggerName = "trigger_" + uuid;
		Date startDate1 = new Date();
		startDate1.setHours(00);
		startDate1.setMinutes(00);
		startDate1.setSeconds(00);
		String jobStartDate = new java.sql.Date(System.currentTimeMillis()).toString()+" 00:00:00";
		Date startDate = convertStringToDate(startDate1.toString(), new SimpleDateFormat("MM/dd/yy"));
		return;
		try {
			JobDataMap jobDataMap = new JobDataMap();
			String str = InvoiceParser.convertTimeStampToString(startDate.toString(), Constants.TIME_STATMP_TO_BILLS_FORMAT, Constants.TIME_STATMP_TO_INVOICE_FORMAT);
			JobDetail job = newJob(InvoiceStateJob.class).withIdentity(jobName, "invoice_schedular").build();
			Trigger trigger = TriggerBuilder.newTrigger().startAt(startDate).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
					// SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(20).repeatForever())
					.usingJobData(jobDataMap).build();
		} catch (WebApplicationException e) {
			LOGGER.error("error unscheduling job", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
		}
	}

	private static Scheduler scheduler = null;

	public static Scheduler getScheduler() {
		return scheduler;
	}

	private static void startRunningJobs() {
		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Statring Db Scanner");
					for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals("job_group"))) {
						System.out.println("running jobs..");
						System.out.println();
						System.out.println("starting job with group name:" + jobKey.getGroup() + " job name" + jobKey.getName());
						// List<Trigger> triggers = (List<Trigger>)
						// scheduler.getTriggersOfJob(jobKey);
						// Trigger trigger = triggers.get(0);
						// System.out.println(trigger.getNextFireTime());
						scheduler.triggerJob(jobKey);
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("error in running jobs", e);
				}
			}
		}.start();

	}
	
	private static Date convertStringToDate(String dateStr, SimpleDateFormat sdf) {
		try {
			return sdf.parse(dateStr);
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}
}
