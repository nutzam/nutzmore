package org.nutz.plugins.sigar.gather;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Who;
import org.nutz.lang.util.NutMap;

/**
 * 操作系统信息收集
 * 
 * @author wkipy
 *
 */
public class OSGather extends NutMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OperatingSystem operatingSystem = OperatingSystem.getInstance();

	private Who[] whos;

	public OSGather() {
		super();
	}

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	public Who[] getWhos() {
		return whos;
	}

	public OSGather(Sigar sigar) {
		init(sigar);
	}

	public static OSGather gather(Sigar sigar) {
		return init(sigar);
	}

	public static OSGather init(Sigar sigar) {
		OSGather osGather = new OSGather();
		try {
			osGather.whos = sigar.getWhoList();
			Runtime r = Runtime.getRuntime();
			Properties props = System.getProperties();
			InetAddress addr = null;
			String ip = "";
			String hostName = "";
			try {
				addr = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				ip = "无法获取主机IP";
				hostName = "无法获取主机名";
			}
			if (null != addr) {
				try {
					ip = addr.getHostAddress();
				} catch (Exception e) {
					ip = "无法获取主机IP";
				}
				try {
					hostName = addr.getHostName();
				} catch (Exception e) {
					hostName = "无法获取主机名";
				}
			}
			osGather.put("hostIp", ip);// 本地ip地址
			osGather.put("hostName", hostName);// 本地主机名
			osGather.put("osName", props.getProperty("os.name"));// 操作系统的名称
			osGather.put("arch", props.getProperty("os.arch"));// 操作系统的构架
			osGather.put("osVersion", props.getProperty("os.version"));// 操作系统的版本
			osGather.put("processors", r.availableProcessors());// JVM可以使用的处理器个数
			osGather.put("javaVersion", props.getProperty("java.version"));// Java的运行环境版本
			osGather.put("vendor", props.getProperty("java.vendor"));// Java的运行环境供应商
			osGather.put("javaUrl", props.getProperty("java.vendor.url"));// Java供应商的URL
			osGather.put("javaHome", props.getProperty("java.home"));// Java的安装路径
			osGather.put("tmpdir", props.getProperty("java.io.tmpdir"));// 默认的临时文件路径
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return osGather;
	}
}
