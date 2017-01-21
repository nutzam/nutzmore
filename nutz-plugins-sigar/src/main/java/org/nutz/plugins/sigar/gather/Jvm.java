package org.nutz.plugins.sigar.gather;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project gather
 *
 * @file Jvm.java
 *
 * @description jvm 信息
 *
 * @copyright 内部代码,禁止转发
 *
 * @time 2016年5月12日 上午12:44:10
 *
 */
public class Jvm {

	long total = 0L;

	long max = 0L;

	long jvm = 0L;

	long usable = 0L;

	long free = 0L;

	double usedPercent = 0.0D;

	double freePercent = 0.0D;

	{
		Runtime runtime = Runtime.getRuntime();

		max = runtime.maxMemory();
		total = runtime.totalMemory();
		free = runtime.freeMemory();
		jvm = total / 1024L / 1024L;
		usable = max - total + free;

		freePercent = 100 * usable / max;
		usedPercent = 100 - freePercent;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

	/**
	 * @return the jvm
	 */
	public long getJvm() {
		return jvm;
	}

	/**
	 * @param jvm
	 *            the jvm to set
	 */
	public void setJvm(long jvm) {
		this.jvm = jvm;
	}

	/**
	 * @return the max
	 */
	public long getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the max to set
	 */
	public void setMax(long max) {
		this.max = max;
	}

	/**
	 * @return the usable
	 */
	public long getUsable() {
		return usable;
	}

	/**
	 * @param usable
	 *            the usable to set
	 */
	public void setUsable(long usable) {
		this.usable = usable;
	}

	/**
	 * @return the free
	 */
	public long getFree() {
		return free;
	}

	/**
	 * @param free
	 *            the free to set
	 */
	public void setFree(long free) {
		this.free = free;
	}

	/**
	 * @return the usedPercent
	 */
	public double getUsedPercent() {
		return usedPercent;
	}

	/**
	 * @param usedPercent
	 *            the usedPercent to set
	 */
	public void setUsedPercent(double usedPercent) {
		this.usedPercent = usedPercent;
	}

	/**
	 * @return the freePercent
	 */
	public double getFreePercent() {
		return freePercent;
	}

	/**
	 * @param freePercent
	 *            the freePercent to set
	 */
	public void setFreePercent(double freePercent) {
		this.freePercent = freePercent;
	}

}
