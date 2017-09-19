package com.qount.invoice.schedular;

import static org.quartz.JobBuilder.newJob;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.ResponseUtil;

/**
 * 
 * @author MateenAhmed
 * @version 1.0 25th Aug 2017
 */
public class SchedularService {

	private static final Logger LOGGER = Logger.getLogger(SchedularService.class);

	private SchedularService() {
	}

	private static Scheduler scheduler = null;

	static {
		intializeScheduler();
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	private static void intializeScheduler() {
		try {
			if (scheduler == null) {
				System.out.println("Statring Scheduler intialization");
				scheduler = new StdSchedulerFactory().getScheduler();
				System.out.println("Statring Scheduler");
				scheduler.start();
				startRunningJobs();
			}
		} catch (Throwable e) {
			LOGGER.error(e);
			e.printStackTrace();
			System.out.println("exiting system error creating cron scheduler");
			System.exit(0);
		}
	}

	private static void startRunningJobs() {
		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Statring Db Scanner");
					for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Constants.INVOICE_SCHEDULAR))) {
						System.out.println("running jobs..");
						System.out.println();
						System.out.println("starting job with group name:" + jobKey.getGroup() + " job name" + jobKey.getName());
						scheduler.triggerJob(jobKey);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("error in running jobs", e);
				}
			}
		}.start();

	}

	private static void startJob() {
		try {
			String uuid = UUID.randomUUID().toString();
			String jobName = "job_" + uuid;
			System.out.println(jobName);
			String triggerName = "trigger_" + uuid;
			System.out.println(triggerName);
			Date startDate = new Date();
			startDate.setHours(00);
			startDate.setMinutes(00);
			startDate.setSeconds(00);
			JobDetail job = newJob(InvoiceStateJob.class).withIdentity(jobName, Constants.INVOICE_SCHEDULAR).build();
			Trigger trigger = TriggerBuilder.newTrigger().startAt(startDate).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
					.build();
			SchedularService.getScheduler().scheduleJob(job, trigger);
		} catch (WebApplicationException e) {
			LOGGER.error("error unscheduling job", e);
			throw new WebApplicationException(ResponseUtil.constructResponse(Constants.FAILURE_STATUS_STR, e.getMessage(), e.getResponse().getStatus()));
		} catch (Exception e) {
			LOGGER.error(e);
		} finally {
		}
	}

//	public static void main(String[] args) {
//		startRunningJobs();
//	}
}
