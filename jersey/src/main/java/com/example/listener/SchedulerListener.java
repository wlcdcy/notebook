package com.example.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

public class SchedulerListener extends HttpServlet implements
		ServletContextListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String expr = "0 0/5 8-20 * * ? *";// "0 1/5 15 * * ? *";//"1/5 * * * * ? *";
	private static String osc_expr = "0 0 10 * * ? *";

	private Scheduler s = null;

	public void contextInitialized(ServletContextEvent sce) {
		try {
			s = startScheduler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			s.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private Scheduler startScheduler() throws SchedulerException {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail jobDetail = newJob(WeiJob.class).withIdentity("weibo",
				"robots").build();
		Trigger trigger = newTrigger()
				.withIdentity("weibo", "robots")
				.withSchedule(
						cronSchedule(expr)
								.withMisfireHandlingInstructionFireAndProceed())
				.forJob("weibo", "robots").build();
		scheduler.scheduleJob(jobDetail, trigger);

		JobDetail osc_job_detail = newJob(OSCJob.class)
				.withIdentity("osc", "robots")
				.usingJobData("token", "bb140b22-c8f6-497e-b89c-3defff3293be")
				.usingJobData("url", "https://api.hiwork.cc/api/sendmsg")
				.build();
		Trigger osc_trigger = newTrigger()
				.withIdentity("osc", "robots")
				.withSchedule(
						cronSchedule(osc_expr)
								.withMisfireHandlingInstructionFireAndProceed())
				.forJob("osc", "robots").build();
		scheduler.scheduleJob(osc_job_detail, osc_trigger);
		scheduler.start();
		return scheduler;
	}

}
