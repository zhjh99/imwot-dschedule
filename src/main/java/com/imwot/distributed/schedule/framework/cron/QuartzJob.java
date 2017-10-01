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

import java.lang.reflect.Constructor;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.imwot.distributed.schedule.framework.AbstractLog;
import com.imwot.distributed.schedule.framework.interfaces.AbstractJob;

/**
 * QuartzJob
 * 
 * @author jinhong zhou
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class QuartzJob extends AbstractLog implements Job {

	/**
	 * 此方法覆盖父类的方法
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
			String className = jobDataMap.getString("className");
			String name = jobDataMap.getString("name");
			AbstractJob job = null;
			if (Environment.containsJob(className)) {
				job = Environment.getJob(className);
			} else {
				Class<?> clazz = Class.forName(className);
				Constructor<?> constructor = clazz.getConstructor();
				constructor.setAccessible(true);

				job = (AbstractJob) constructor.newInstance();
				job.addInstanceToJobTable(className, job);
			}

			log.info("Job starting:{},{}", name, "......");
			job.exec();
			log.info("Job success:{}", name);
		} catch (Exception e) {
			log.error(null, e);
		}
	}
}