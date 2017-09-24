/**
 [The "BSD license"]
 Copyright (c) 2013-2017 jinhong zhou (周金红)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imwot.distributed.schedule.framework.cron;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.imwot.distributed.schedule.framework.AbstractLog;
import com.imwot.distributed.schedule.framework.conf.JobConfig;

/**
 * 调度服务
 * 
 * @author jinhong zhou
 */
public class CronService extends AbstractLog implements Runnable {

	private List<JobConfig> jobList;

	public CronService(List<JobConfig> list) {
		this.jobList = list;
	}

	public CronService(JobConfig config) {
		this.jobList = new ArrayList<JobConfig>();
		jobList.add(config);
	}

	public void run() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();

			for (int x = 0; x < jobList.size(); x++) {
				JobConfig config = jobList.get(x);
				String name = config.getName();
				String className = config.getClazz();
				String execTime = config.getTime();

				JobDetail job = JobBuilder.newJob(QuartzJob.class).withIdentity("job" + x, "group1").build();
				JobDataMap jobDataMap = job.getJobDataMap();
				jobDataMap.put("className", className);
				jobDataMap.put("name", name);

				CronTrigger trigger = newTrigger().withIdentity("trigger" + x, "group1").withSchedule(cronSchedule(execTime)).build();

				Date ft = sched.scheduleJob(job, trigger);
				log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
			}

			sched.start();
			log.info("------- Started Scheduler -----------------");

			log.info("------- Waiting five minutes... ------------");
			while (true) {
				try {
					List<JobExecutionContext> jobLists = sched.getCurrentlyExecutingJobs();
					StringBuffer sb = new StringBuffer();
					sb.append("(");
					for (int x = 0; x < jobLists.size(); x++) {
						sb.append(jobLists.get(x).getJobDetail().getJobDataMap().get("className")).append(",");
					}
					sb.append(")");
					log.info("job num is:" + jobLists.size() + ",jobs:--->" + sb.toString());
					Thread.sleep(5 * 60 * 1000L);
				} catch (Exception e) {
					log.warn(e.getMessage());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}