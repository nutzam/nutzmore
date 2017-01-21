package org.nutz.plugins.sigar.gather;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.json.JsonField;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 网卡信息收集器
 * 
 * @author wkipy
 *
 */
public class NetInterfaceGather {

	private static String ip;

	private static String hostName;

	@JsonField(ignore = true)
	private String activeCard;

	@JsonField(ignore = true)
	static private Log log = Logs.getLog(NetInterfaceGather.class);

	static {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error(e);
		}
	}

	private NetInterfaceConfig config;
	private NetInterfaceStat stat;
	private NetInfo info;

	private long rxbps;
	private long txbps;

	private List<NutMap> detail = new ArrayList<NutMap>();

	public static NetInterfaceGather gather(final Sigar sigar) {

		final NetInterfaceGather data = new NetInterfaceGather();

		String active;
		try {
			active = data.fetActiveNetInterfaceName(sigar);
			data.config = sigar.getNetInterfaceConfig(active);
			data.info = sigar.getNetInfo();
			data.stat = sigar.getNetInterfaceStat(active);
			long start = System.currentTimeMillis();
			long rxBytesStart = data.stat.getRxBytes();
			long txBytesStart = data.stat.getTxBytes();
			Thread.sleep(1000);
			long end = System.currentTimeMillis();
			NetInterfaceStat statEnd = sigar.getNetInterfaceStat(active);
			long rxBytesEnd = statEnd.getRxBytes();
			long txBytesEnd = statEnd.getTxBytes();

			data.rxbps = (rxBytesEnd - rxBytesStart) * 8 / (end - start) * 1000;
			data.txbps = (txBytesEnd - txBytesStart) * 8 / (end - start) * 1000;

			Lang.each(sigar.getNetInterfaceList(), new Each<String>() {

				@Override
				public void invoke(int arg0, String name, int arg2) throws ExitLoop, ContinueLoop, LoopException {
					NutMap temp = NutMap.NEW();

					try {
						temp.addv("stat", sigar.getNetInterfaceStat(name));
						temp.addv("config", sigar.getNetInterfaceConfig(name));
					} catch (SigarException e) {
						e.printStackTrace();
					}

					data.detail.add(temp);
				}

			});
		} catch (SigarException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		return data;
	}

	private String fetActiveNetInterfaceName(final Sigar sigar) throws SigarException {
		Lang.each(sigar.getNetInterfaceList(), new Each<String>() {

			@Override
			public void invoke(int arg0, String name, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				try {
					if (Strings.equals(ip, sigar.getNetInterfaceConfig(name).getAddress())) {
						activeCard = name;
						throw new ExitLoop();
					}
				} catch (SigarException e) {
					e.printStackTrace();
				}
			}
		});
		return activeCard;
	}

	/**
	 * @return the ip
	 */
	public static String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public static void setIp(String ip) {
		NetInterfaceGather.ip = ip;
	}

	/**
	 * @return the hostName
	 */
	public static String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public static void setHostName(String hostName) {
		NetInterfaceGather.hostName = hostName;
	}

	/**
	 * @return the config
	 */
	public NetInterfaceConfig getConfig() {
		return config;
	}

	/**
	 * @return the rxbps
	 */
	public long getRxbps() {
		return rxbps;
	}

	/**
	 * @param rxbps
	 *            the rxbps to set
	 */
	public void setRxbps(long rxbps) {
		this.rxbps = rxbps;
	}

	/**
	 * @return the txbps
	 */
	public long getTxbps() {
		return txbps;
	}

	/**
	 * @param txbps
	 *            the txbps to set
	 */
	public void setTxbps(long txbps) {
		this.txbps = txbps;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(NetInterfaceConfig config) {
		this.config = config;
	}

	/**
	 * @return the stat
	 */
	public NetInterfaceStat getStat() {
		return stat;
	}

	/**
	 * @param stat
	 *            the stat to set
	 */
	public void setStat(NetInterfaceStat stat) {
		this.stat = stat;
	}

	/**
	 * @return the info
	 */
	public NetInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(NetInfo info) {
		this.info = info;
	}

	/**
	 * @return the detail
	 */
	public List<NutMap> getDetail() {
		return detail;
	}

	/**
	 * @param detail
	 *            the detail to set
	 */
	public void setDetail(List<NutMap> detail) {
		this.detail = detail;
	}

}
