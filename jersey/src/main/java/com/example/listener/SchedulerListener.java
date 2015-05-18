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


public class SchedulerListener extends HttpServlet implements ServletContextListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String expr ="0 0/5 8-20 * * ? *";// "0 1/5 15 * * ? *";//"1/5 * * * * ? *";
	
	private Scheduler s =null;
	
	public void contextInitialized(ServletContextEvent sce) {
		try {
			s =startScheduler();
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
	
	private Scheduler startScheduler() throws SchedulerException{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail jobDetail = newJob(WeiJob.class).withIdentity("weibo", "robots").build();
	    Trigger trigger = newTrigger().withIdentity("weibo", "robots").withSchedule(cronSchedule(expr).withMisfireHandlingInstructionFireAndProceed()).forJob("weibo", "robots").build();
	    scheduler.scheduleJob(jobDetail, trigger);
	    scheduler.start();
	    return scheduler;
	}

}
