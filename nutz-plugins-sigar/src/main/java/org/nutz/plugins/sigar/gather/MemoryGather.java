package org.nutz.plugins.sigar.gather;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

public class MemoryGather {
	private Mem mem;
	private Swap swap;

	private Jvm jvm = new Jvm();

	/**
	 * @return the jvm
	 */
	public Jvm getJvm() {
		return jvm;
	}

	/**
	 * @param jvm
	 *            the jvm to set
	 */
	public void setJvm(Jvm jvm) {
		this.jvm = jvm;
	}

	/**
	 * @param mem
	 *            the mem to set
	 */
	public void setMem(Mem mem) {
		this.mem = mem;
	}

	/**
	 * @param swap
	 *            the swap to set
	 */
	public void setSwap(Swap swap) {
		this.swap = swap;
	}

	public void populate(Sigar sigar) throws SigarException {
		mem = sigar.getMem();
		swap = sigar.getSwap();
	}

	public static MemoryGather gather(Sigar sigar) {
		MemoryGather data = new MemoryGather();
		try {
			data.populate(sigar);
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return data;
	}

	public Mem getMem() {
		return mem;
	}

	public Swap getSwap() {
		return swap;
	}

}
