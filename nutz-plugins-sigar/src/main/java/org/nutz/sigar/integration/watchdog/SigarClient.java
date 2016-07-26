package org.nutz.sigar.integration.watchdog;

import java.util.HashMap;

import org.hyperic.sigar.SigarException;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.json.Json;
import org.nutz.lang.Tasks;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.sigar.gather.Gathers;

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
	private String token = R.UU16();

	/**
	 * 上报任务的cron表达式
	 */
	private String cron = "0/5 * * * * *";

	private Log log = Logs.getLog(SigarClient.class);

	/**
	 * 
	 */
	public SigarClient() {
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

	public void watch() throws SigarException, InterruptedException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		Http.post3(gatherAddress, Json.toJson(Gathers.all()), Header.create().addAll(map), 1000);
	}
}
