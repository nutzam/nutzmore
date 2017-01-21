package org.nutz.plugin.sigar;

import org.hyperic.sigar.Sigar;
import org.junit.Before;
import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.plugins.sigar.gather.CPUGather;
import org.nutz.plugins.sigar.gather.DISKGather;
import org.nutz.plugins.sigar.gather.MemoryGather;
import org.nutz.plugins.sigar.gather.NetInterfaceGather;
import org.nutz.plugins.sigar.gather.OSGather;

public class GatherTest {

	protected Sigar sigar;

	@Before
	public void init() {
		sigar = new Sigar();
	}

	@Test
	public void system() {
		OSGather os = OSGather.init(sigar);
		System.err.println("system--->\r" + Json.toJson(os));
	}

	@Test
	public void memory() {
		MemoryGather memory = MemoryGather.gather(sigar);
		System.err.println("memory--->\r" + Json.toJson(memory));
	}

	@Test
	public void disk() {
		DISKGather disk = DISKGather.gather(sigar);
		System.err.println("disk--->\r" + Json.toJson(disk));
	}

	@Test
	public void cpu() {
		CPUGather cpu = CPUGather.gather(sigar);
		System.err.println("cpu--->\r" + Json.toJson(cpu));
	}

	@Test
	public void network() {
		NetInterfaceGather network = NetInterfaceGather.gather(sigar);
		System.err.println("network--->\r" + Json.toJson(network));
	}

}
