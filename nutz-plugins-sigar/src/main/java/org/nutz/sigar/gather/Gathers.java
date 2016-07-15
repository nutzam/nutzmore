package org.nutz.sigar.gather;

import java.util.List;

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

	public static NutMap all() throws SigarException {
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

		List<DISKGather> disks = DISKGather.gather(sigar);
		data.put("disk", disks);
		long totle = 0, used = 0;
		for (DISKGather disk : disks) {
			if (disk.getStat() != null) {
				totle += disk.getStat().getTotal();
				used += disk.getStat().getUsed();
			}
		}
		data.put("diskUsage", used * 100 / totle);

		NetInterfaceGather ni = NetInterfaceGather.gather(sigar);
		data.put("network", ni);
		data.put("niUsage", ni.getRxbps() * 100 / ni.getStat().getSpeed());
		data.put("noUsage", ni.getTxbps() * 100 / ni.getStat().getSpeed());

		data.put("system", OSGather.init(sigar));
		return data;
	}
}
