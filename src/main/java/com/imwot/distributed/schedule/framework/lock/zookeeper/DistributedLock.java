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
package com.imwot.distributed.schedule.framework.lock.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.imwot.distributed.schedule.framework.AbstractLog;
import com.imwot.distributed.schedule.framework.interfaces.CallBack;

/**
 * 锁
 * 
 * @author jinhong zhou
 */
public class DistributedLock extends AbstractLog implements Watcher {
	private ZooKeeper zk = null;
	// 根节点
	private String rootPath = "/jstar";
	// 竞争的资源
	private String lockName = "schedule";
	// 等待的前一个锁
	private String WAIT_LOCK;
	// 当前锁
	private String CURRENT_LOCK;
	// 计数器
	private CountDownLatch countDownLatch;
	private int sessionTimeout = 60000;
	private List<Exception> exceptionList = new ArrayList<Exception>();

	private boolean distributed;
	private CallBack callBack;

	/**
	 * 配置分布式锁
	 * 
	 * @param config
	 *            连接的url
	 * @param lockName
	 *            竞争资源
	 */
	public DistributedLock(String config, String rootPath, boolean distributed, CallBack callBack) {
		this.rootPath = rootPath;
		this.callBack = callBack;
		this.distributed = distributed;
		try {
			if (distributed) {
				// 连接zookeeper
				zk = new ZooKeeper(config, sessionTimeout, this);
				Stat stat = zk.exists(rootPath, false);
				if (stat == null) {
					zk.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此方法覆盖父类的方法
	 * 
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.WatchedEvent)
	 */
	public void process(WatchedEvent event) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (event == null) {
			return;
		}
		// 连接状态
		KeeperState keeperState = event.getState();
		// 事件类型
		EventType eventType = event.getType();
		// 受影响的path
		String path = event.getPath();
		String logPrefix = "【Watcher-" + this.getClass().getName() + "】";

		log.info(logPrefix + "收到Watcher通知");
		log.info(logPrefix + "连接状态:\t" + keeperState.toString());
		log.info(logPrefix + "事件类型:\t" + eventType.toString());

		if (KeeperState.SyncConnected == keeperState) {
			// 成功连接上ZK服务器
			if (EventType.None == eventType) {
				log.info(logPrefix + "成功连接上ZK服务器");
			} else if (EventType.NodeCreated == eventType) {
				log.info(logPrefix + "节点创建");
			} else if (EventType.NodeDataChanged == eventType) {
				log.info(logPrefix + "节点数据更新" + this.readData(rootPath, true));
				log.info(logPrefix + "数据内容: ");
			} else if (EventType.NodeChildrenChanged == eventType) {
				log.info(logPrefix + "子节点变更");
				log.info(logPrefix + "子节点列表：" + this.getChildren(rootPath, true));
			} else if (EventType.NodeDeleted == eventType) {
				log.info(logPrefix + "节点 " + path + " 被删除");
				if (this.countDownLatch != null) {
					this.countDownLatch.countDown();
				}
				log.info("countDownLatch.countDown");
			}
		} else if (KeeperState.Disconnected == keeperState) {
			log.info(logPrefix + "与ZK服务器断开连接");
		} else if (KeeperState.AuthFailed == keeperState) {
			log.info(logPrefix + "权限检查失败");
		} else if (KeeperState.Expired == keeperState) {
			log.info(logPrefix + "会话失效");
		}

	}

	public void lock() {
		if (!distributed) {
			callBack.callBack();
		} else {
			if (exceptionList.size() > 0) {
				throw new LockException(exceptionList.get(0));
			}
			try {
				if (this.tryLock()) {
					log.info(Thread.currentThread().getName() + " " + lockName + "获得了锁");

					callBack.callBack();
					return;
				} else {
					// 等待锁
					waitForLock(WAIT_LOCK, sessionTimeout);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 获取锁
	 * 
	 * @return boolean
	 * @exception/throws
	 */
	public boolean tryLock() {
		try {
			String splitStr = "_lock_";
			if (lockName.contains(splitStr)) {
				throw new LockException("锁名有误");
			}
			if (StringUtils.isBlank(CURRENT_LOCK)) {
				CURRENT_LOCK = zk.create(rootPath + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			} else if (StringUtils.isNotBlank(CURRENT_LOCK) && null == zk.exists(CURRENT_LOCK, true)) {
				CURRENT_LOCK = zk.create(rootPath + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
			log.info(Thread.currentThread().getName() + ":" + CURRENT_LOCK + " 已经创建");
			// 取所有子节点
			List<String> subNodes = zk.getChildren(rootPath, false);
			// 取出所有lockName的锁
			List<String> lockObjects = new ArrayList<String>();
			for (String node : subNodes) {
				String _node = node.split(splitStr)[0];
				if (_node.equals(lockName)) {
					lockObjects.add(node);
				}
			}
			Collections.sort(lockObjects);
			log.info(Thread.currentThread().getName() + " 的锁是 " + CURRENT_LOCK);
			// 若当前节点为最小节点，则获取锁成功
			if (CURRENT_LOCK.equals(rootPath + "/" + lockObjects.get(0))) {
				return true;
			}

			// 若不是最小节点，则找到自己的前一个节点
			String prevNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
			WAIT_LOCK = lockObjects.get(Collections.binarySearch(lockObjects, prevNode) - 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean tryLock(long timeout, TimeUnit unit) {
		try {
			if (this.tryLock()) {
				return true;
			}
			return waitForLock(WAIT_LOCK, timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 等待锁
	 * 
	 * @param prev
	 * @param waitTime
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 *             boolean
	 * @exception/throws
	 */
	private boolean waitForLock(String prev, long waitTime) throws KeeperException, InterruptedException {
		Stat stat = zk.exists(rootPath + "/" + prev, true);

		if (stat != null) {
			log.info(Thread.currentThread().getName() + "等待锁 " + rootPath + "/" + prev);
			this.countDownLatch = new CountDownLatch(1);
			// 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
			this.countDownLatch.await();
			lock();
		}
		return true;
	}

	/**
	 * 
	 * 释放锁
	 * 
	 * void
	 * 
	 * @exception/throws
	 */
	public void unlock() {
		try {
			log.info("释放锁 " + CURRENT_LOCK);
			zk.delete(CURRENT_LOCK, -1);
			CURRENT_LOCK = null;
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	public Condition newCondition() {
		return null;
	}

	public void lockInterruptibly() throws InterruptedException {
		// this.lock();
	}

	public class LockException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public LockException(String e) {
			super(e);
		}

		public LockException(Exception e) {
			super(e);
		}
	}

	/**
	 * 读取指定节点数据内容
	 * 
	 * @param path
	 *            节点path
	 * @return
	 */
	public String readData(String path, boolean needWatch) {
		try {
			return new String(this.zk.getData(path, needWatch, null));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取子节点
	 * 
	 * @param path
	 *            节点path
	 */
	private List<String> getChildren(String path, boolean needWatch) {
		try {
			return this.zk.getChildren(path, needWatch);
		} catch (Exception e) {
			return null;
		}
	}
}