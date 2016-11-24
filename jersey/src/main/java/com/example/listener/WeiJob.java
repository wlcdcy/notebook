package com.example.listener;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WeiJob implements Job {
    static long since_id = 0;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("触发定时器....");
        // context.getJobDetail().getJobDataMap();
        // since_id
        // =WeiboProvide.friendsTimeLine("2.00HOqPrC_2YvNBf94a6f6760SsXtVB",since_id);
    }

}
