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
package com.imwot.distributed.schedule.framework.conf;

import java.util.List;

/**
 * 配置
 * 
 * @author jinhong zhou
 */
public class Config {

	/**
	 * 任务名称
	 */
	private String zookeeperAddress;

	/**
	 * 任务执行类(包名+名称)
	 */
	private String zookeeperPath;

	/**
	 * 任务执行类(包名+名称)
	 */
	private boolean distributed;

	/**
	 * 任务配置
	 */
	private List<JobConfig> jobList;

	/**
	 * @return 属性 zookeeperAddress
	 */
	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	/**
	 * 设置属性 zookeeperAddress 值
	 */
	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	/**
	 * @return 属性 zookeeperPath
	 */
	public String getZookeeperPath() {
		return zookeeperPath;
	}

	/**
	 * 设置属性 zookeeperPath 值
	 */
	public void setZookeeperPath(String zookeeperPath) {
		this.zookeeperPath = zookeeperPath;
	}

	/**
	 * @return 属性 distributed
	 */
	public boolean isDistributed() {
		return distributed;
	}

	/**
	 * 设置属性 distributed 值
	 */
	public void setDistributed(boolean distributed) {
		this.distributed = distributed;
	}

	/**
	 * @return 属性 jobList
	 */
	public List<JobConfig> getJobList() {
		return jobList;
	}

	/**
	 * 设置属性 jobList 值
	 */
	public void setJobList(List<JobConfig> jobList) {
		this.jobList = jobList;
	}
}
