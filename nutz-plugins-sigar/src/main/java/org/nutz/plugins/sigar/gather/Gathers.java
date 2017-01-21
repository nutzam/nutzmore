package org.nutz.plugins.sigar.gather;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.lang.util.NutMap;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project gather
 *
 * @file Gathers.java
 *
 * @description 系统信息全收集
 *
 * @time 2016年3月15日 下午8:36:58
 *
 */
public class Gathers {

	public static NutMap all() throws SigarException, InterruptedException {
		Sigar sigar = new Sigar();
		NutMap data = NutMap.NEW();

		CPUGather cpu = CPUGather.gather(sigar);
		data.put("cpu", cpu);
		data.put("cpuUsage", cpu.getPerc().getCombined() * 100);

		MemoryGather memory = MemoryGather.gather(sigar);
		data.put("memory", memory);
		data.put("ramUasge", memory.getMem().getUsedPercent());
		data.put("jvmUasge", memory.getJvm().getUsedPercent());
		if (memory.getSwap().getTotal() == 0) {
			data.put("swapUasge", 0);
		} else {
			data.put("swapUasge", memory.getSwap().getUsed() * 100 / memory.getSwap().getTotal());
		}

		data.put("disk", DISKGather.gather(sigar));

		data.put("network", NetInterfaceGather.gather(sigar));

		data.put("system", OSGather.init(sigar));
		return data;
	}

	// public static void main(String[] args) throws SigarException,
	// InterruptedException {
	// long start, end = 0;
	// System.err.println(start = System.currentTimeMillis());
	// System.err.println(Json.toJson(Gathers.all()));
	// System.err.println(end = System.currentTimeMillis());
	// System.err.println(end - start);
	// }
}
