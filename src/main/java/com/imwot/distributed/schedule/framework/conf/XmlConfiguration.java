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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.imwot.distributed.schedule.framework.AbstractLog;

/**
 * xml解析
 * 
 * @author jinhong zhou
 */
public class XmlConfiguration extends AbstractLog {

	/**
	 * 
	 * 解析xml配置
	 * 
	 * @param xmlName
	 * @return
	 * @throws Exception
	 *             List<Config>
	 * @exception/throws
	 */
	public Config parse(String xmlName) throws Exception {
		Config config = new Config();
		List<JobConfig> list = new ArrayList<JobConfig>();
		try {
			String url = this.getClass().getResource(xmlName).getFile();
			File filePath = new File(url);
			FileInputStream is = new FileInputStream(filePath);
			Document doc = Jsoup.parse(is, "UTF-8", "", Parser.xmlParser());
			if (doc != null) {
				Elements elments = doc.select("job");
				String zookeeperAddress = elments.attr("zookeeperAddress");
				String zookeeperPath = elments.attr("zookeeperPath");
				String distributed = elments.attr("distributed");
				for (Element element : elments) {
					String name = element.select("name").text().trim();
					String clazz = element.select("className").text().trim();
					String time = element.select("time").text().trim();

					JobConfig schedule = new JobConfig();
					schedule.setName(name);
					schedule.setClazz(clazz);
					schedule.setTime(time);
					list.add(schedule);
					config.setJobList(list);
				}

				if ("true".equalsIgnoreCase(distributed)) {
					config.setDistributed(true);
					config.setZookeeperAddress(zookeeperAddress);
					config.setZookeeperPath(zookeeperPath);
				}
			}
		} catch (Exception e) {
			log.error(null, e);
		}

		return config;
	}
}