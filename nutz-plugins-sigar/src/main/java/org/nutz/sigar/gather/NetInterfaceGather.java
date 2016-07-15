package org.nutz.sigar.gather;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

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
import org.nutz.lang.Times;
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
	private long rxbps;
	private long txbps;

	private static long rTemp = 0;

	private static long tTemp = 0;

	private static String activeCard = null;

	private static Date last;

	public NetInterfaceGather() {
	}

	public void populate(Sigar sigar, String name) throws SigarException {

		config = sigar.getNetInterfaceConfig(name);

		try {

			NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
			if (rTemp == 0 || tTemp == 0) {
				rTemp = statStart.getRxBytes();
				tTemp = statStart.getTxBytes();
				last = Times.now();
			} else {
				long rt = statStart.getRxBytes();
				long tt = statStart.getTxBytes();
				Date now = Times.now();

				rxbps = (rt - rTemp) * 1000 * 1024 / (now.getTime() - last.getTime());
				txbps = (tt - tTemp) * 1000 * 1024 / (now.getTime() - last.getTime());

				rTemp = rt;
				tTemp = tt;
				last = now;
			}

			stat = sigar.getNetInterfaceStat(name);
		} catch (SigarException e) {

		} catch (Exception e) {

		}
	}

	public static NetInterfaceGather gather(Sigar sigar) throws SigarException {
		return NetInterfaceGather.gather(sigar, fetActiveNetInterfaceName(sigar));
	}

	private static String fetActiveNetInterfaceName(final Sigar sigar) throws SigarException {
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

	public static NetInterfaceGather gather(Sigar sigar, String name) throws SigarException {
		NetInterfaceGather data = new NetInterfaceGather();
		data.populate(sigar, name);
		return data;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @return the config
	 */
	public NetInterfaceConfig getConfig() {
		return config;
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

}
