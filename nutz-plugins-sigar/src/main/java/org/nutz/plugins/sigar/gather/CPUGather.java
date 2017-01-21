package org.nutz.plugins.sigar.gather;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.lang.util.NutMap;

/**
 * 收集的cpu信息
 * 
 * @author wkipy
 *
 */
public class CPUGather {

	private CpuPerc perc;
	private Cpu cpu;

	// 详情
	private NutMap detail = NutMap.NEW();

	public CPUGather() {
	}

	/**
	 * @return the perc
	 */
	public CpuPerc getPerc() {
		return perc;
	}

	/**
	 * @return the detail
	 */
	public NutMap getDetail() {
		return detail;
	}

	/**
	 * @param detail
	 *            the detail to set
	 */
	public void setDetail(NutMap detail) {
		this.detail = detail;
	}

	/**
	 * @param perc
	 *            the perc to set
	 */
	public void setPerc(CpuPerc perc) {
		this.perc = perc;
	}

	/**
	 * @return the cpu
	 */
	public Cpu getCpu() {
		return cpu;
	}

	/**
	 * @param cpu
	 *            the cpu to set
	 */
	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	public void populate(Sigar sigar) throws SigarException {
		perc = sigar.getCpuPerc();
		cpu = sigar.getCpu();
		detail.addv("cpus", sigar.getCpuList());
		detail.addv("infos", sigar.getCpuInfoList());
		detail.addv("percs", sigar.getCpuPercList());
	}

	public static CPUGather gather(Sigar sigar) {
		CPUGather data = new CPUGather();
		try {
			data.populate(sigar);
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return data;
	}

	public CPUGather(Sigar sigar) throws SigarException {
		populate(sigar);
	}

}
