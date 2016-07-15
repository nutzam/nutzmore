package org.nutz.sigar.gather;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 收集的cpu信息
 * 
 * @author wkipy
 *
 */
public class CPUGather {

	private CpuInfo info;
	private CpuPerc perc;
	private Cpu timer;

	public CPUGather() {
	}

	/**
	 * @return the info
	 */
	public CpuInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(CpuInfo info) {
		this.info = info;
	}

	/**
	 * @return the perc
	 */
	public CpuPerc getPerc() {
		return perc;
	}

	/**
	 * @param perc
	 *            the perc to set
	 */
	public void setPerc(CpuPerc perc) {
		this.perc = perc;
	}

	/**
	 * @return the timer
	 */
	public Cpu getTimer() {
		return timer;
	}

	/**
	 * @param timer
	 *            the timer to set
	 */
	public void setTimer(Cpu timer) {
		this.timer = timer;
	}

	public void populate(Sigar sigar) throws SigarException {
		info = sigar.getCpuInfoList()[0];
		perc = sigar.getCpuPerc();
		timer = sigar.getCpu();
	}

	public static CPUGather gather(Sigar sigar) throws SigarException {
		CPUGather data = new CPUGather();
		data.populate(sigar);
		return data;
	}

	public CPUGather(Sigar sigar) throws SigarException {
		populate(sigar);
	}

}
