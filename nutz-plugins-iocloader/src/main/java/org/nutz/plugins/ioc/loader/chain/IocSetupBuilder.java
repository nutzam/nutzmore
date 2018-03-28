package org.nutz.plugins.ioc.loader.chain;

/**
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public class IocSetupBuilder {
	private ChainBuilder<IocSetup> setupChainBuilder;

	IocSetupBuilder() {
		super();
	}

	public static IocSetupBuilder create() {
		return new IocSetupBuilder();
	}

	public IocSetup build() {
		return new ImmutableIocSetup(setupChainBuilder != null ? setupChainBuilder.build() : null);
	}

	private ChainBuilder<IocSetup> getIocSetupChainBuilder() {
		if (setupChainBuilder == null) {
			setupChainBuilder = new ChainBuilder<IocSetup>();
		}
		return setupChainBuilder;
	}

	public IocSetupBuilder addFirst(final IocSetup e) {
		if (e == null) {
			return this;
		}
		getIocSetupChainBuilder().addFirst(e);
		return this;
	}

	public IocSetupBuilder addLast(final IocSetup e) {
		if (e == null) {
			return this;
		}
		getIocSetupChainBuilder().addLast(e);
		return this;
	}

	public IocSetupBuilder add(final IocSetup e) {
		return addLast(e);
	}

	public IocSetupBuilder addAllFirst(final IocSetup... e) {
		if (e == null) {
			return this;
		}
		getIocSetupChainBuilder().addAllFirst(e);
		return this;
	}

	public IocSetupBuilder addAllLast(final IocSetup... e) {
		if (e == null) {
			return this;
		}
		getIocSetupChainBuilder().addAllLast(e);
		return this;
	}

	public IocSetupBuilder addAll(final IocSetup... e) {
		return addAllLast(e);
	}
}
