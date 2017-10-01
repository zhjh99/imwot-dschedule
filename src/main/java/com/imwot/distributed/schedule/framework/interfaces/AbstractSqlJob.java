/**
 * 文件：AbstractMySqlJob.java
 * 公司：深圳通联金融网络科技服务有限公司
 * 版权：Copyright (c) 2015 allinpay, Inc. All rights reserved.
 * 系统：iCrawler-Server
 * 描述：
 * 作者：周金红
 * 日期：2017-10-1
 */
package com.imwot.distributed.schedule.framework.interfaces;

import com.imwot.db.DBContext;
import com.imwot.db.sql.AbstractSqlOperator;

/**
 * mysql数据库job支持
 * 
 * @author 周金红
 * @version V0.1 2017-10-1[版本号, YYYY-MM-DD]
 * @see [相关类/方法]
 * @since iCrawler-Server
 */
public abstract class AbstractSqlJob extends AbstractJob {

	/**
	 * mysql数据库操作1
	 */
	protected AbstractSqlOperator mysql1;

	public AbstractSqlJob() {
		mysql1 = DBContext.getDbInstance().getDbOperator("mysql1");
	}
}
