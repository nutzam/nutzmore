package org.nutz.plugins.ioc.loader.chain;

import java.util.List;
import java.util.Map;

import org.nutz.ioc.Ioc;

/**
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public class ImmutableIocSetup implements IocSetup {
	private final IocSetup[] IocSetups;

	public ImmutableIocSetup(final IocSetup... setups) {
		super();
		if (setups != null) {
			final int l = setups.length;
			this.IocSetups = new IocSetup[l];
			System.arraycopy(setups, 0, this.IocSetups, 0, l);
		} else {
			this.IocSetups = new IocSetup[0];
		}
	}

	public ImmutableIocSetup(final List<IocSetup> setups) {
		super();
		if (setups != null) {
			final int l = setups.size();
			this.IocSetups = setups.toArray(new IocSetup[l]);
		} else {
			this.IocSetups = new IocSetup[0];
		}
	}

	@Override
	public void init(Map<String, Ioc> iocs) {
		for (final IocSetup setup : this.IocSetups) {
			setup.init(iocs);
		}
	}

	@Override
	public void destroy(Map<String, Ioc> iocs) {
		for (final IocSetup setup : this.IocSetups) {
			setup.destroy(iocs);
		}
	}
}
