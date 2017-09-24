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
package com.imwot;

import com.imwot.distributed.schedule.framework.AbstractLog;
import com.imwot.distributed.schedule.framework.conf.Config;
import com.imwot.distributed.schedule.framework.conf.XmlConfiguration;
import com.imwot.distributed.schedule.framework.cron.CronService;
import com.imwot.distributed.schedule.framework.interfaces.CallBack;
import com.imwot.distributed.schedule.framework.lock.zookeeper.DistributedLock;

/**
 * 分布式调度
 * 
 * @author jinhong zhou
 */
public class DScheduler extends AbstractLog {

	/**
	 * 配置
	 */
	private Config config;

	public static void main(String[] args) {
		DScheduler ds = new DScheduler();
		ds.init();

		DistributedLock lock = null;
		try {
			lock = new DistributedLock(ds.config.getZookeeperAddress(), ds.config.getZookeeperPath(), ds.config.isDistributed(), ds.callBack);
			lock.lock();
			ds.log.info(Thread.currentThread().getName() + "正在运行");
		} catch (Exception e) {
			ds.log.error(null, e);
		}
	}

	/**
	 * 
	 * 初始化
	 * 
	 * @return Config
	 * @exception/throws
	 */
	public Config init() {
		try {
			config = new XmlConfiguration().parse("/job.xml");
		} catch (Exception e) {
			log.error(null, e);
			System.exit(0);
		}
		return config;
	}

	private CallBack callBack = new CallBack() {
		@Override
		public void callBack() {
			CronService cs = new CronService(config.getJobList());
			Thread t = new Thread(cs);
			t.start();
		}
	};
}
