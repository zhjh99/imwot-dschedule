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

import java.util.HashMap;
import java.util.Map;

/**
 * 全局变量
 * 
 * @author jinhong zhou
 */
public class Environment {
	/**
	 * 任务表，记载执行的任务
	 */
	public static Map<String, AbstractJob> jobMap = new HashMap<String, AbstractJob>();

	/**
	 * 是否获取锁
	 */
	public static Boolean isAcquire = false;
	

	/**
	 * @return 属性 jobMap
	 */
	public static AbstractJob getJob(String jobName) {
		return jobMap.get(jobName);
	}

	/**
	 * 设置属性 jobMap 值
	 */
	public static void addJob(String jobName, AbstractJob job) {
		Environment.jobMap.put(jobName, job);
	}

	/**
	 * 设置属性 jobMap 值
	 */
	public static boolean containsJob(String jobName) {
		return Environment.jobMap.containsKey(jobName);
	}
}
