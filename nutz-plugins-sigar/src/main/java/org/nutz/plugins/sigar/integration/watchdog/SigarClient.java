package org.nutz.plugins.sigar.integration.watchdog;

import java.util.HashMap;

import org.hyperic.sigar.SigarException;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.Tasks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.sigar.gather.Gathers;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-sigar
 *
 * @file SigarClient.java
 *
 * @description dog
 *
 * @time 2016年7月25日 下午6:10:15
 *
 */
public class SigarClient {

	/**
	 * 收集信息的地址
	 */
	private String gatherAddress;

	/**
	 * token
	 */
	private String token;

	private PropertiesProxy config;

	public SigarClient() {
		super();
	}

	/**
	 * 上报任务的cron表达式
	 */
	private String cron = "*/5 * * * * ?";

	public SigarClient(String gatherAddress, String token, String cron) {
		super();
		this.gatherAddress = gatherAddress;
		this.token = token;
		this.cron = cron;
		start();
	}

	public SigarClient(PropertiesProxy config) {
		super();
		this.gatherAddress = config.get("watch.gather.address");
		this.token = config.get("watch.gather.token");
		this.cron = config.get("watch.gather.cron");
		start();
	}

	public void init() {
		cron = Strings.isBlank(cron) ? config.get("watch.gather.cron") : cron;
		gatherAddress = Strings.isBlank(gatherAddress) ? config.get("watch.gather.address") : gatherAddress;
		token = Strings.isBlank(token) ? config.get("watch.gather.token") : token;
		start();
	}

	private Log log = Logs.getLog(SigarClient.class);

	@SuppressWarnings("deprecation")
	protected void start() {
		Tasks.scheduleAtFixedTime(new Runnable() {

			@Override
			public void run() {
				try {
					watch();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}, cron);
	}

	/**
	 * 
	 */
	public SigarClient(String gatherAddress, String token) {
		this.gatherAddress = gatherAddress;
		this.token = token;
		start();
	}

	public void watch() throws SigarException, InterruptedException {
		log.debug("gathering....");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		Http.post3(gatherAddress, Json.toJson(Gathers.all(), JsonFormat.compact()), Header.create().addAll(map), 5000);
	}
}
