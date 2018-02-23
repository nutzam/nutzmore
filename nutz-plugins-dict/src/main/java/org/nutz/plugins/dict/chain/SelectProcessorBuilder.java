package org.nutz.plugins.dict.chain;

/**
 * select处理构建类
 * @author 邓华锋 http://dhf.ink
 *
 */
public class SelectProcessorBuilder {
	private ChainBuilder<SelectProcessor> selectChainBuilder;

	SelectProcessorBuilder() {
		super();
	}

	public static SelectProcessorBuilder create() {
		return new SelectProcessorBuilder();
	}

	public SelectProcessor build() {
		return new ImmutableSelectProcessor(selectChainBuilder != null ? selectChainBuilder.build() : null);
	}

	private ChainBuilder<SelectProcessor> getSelectChainBuilder() {
		if (selectChainBuilder == null) {
			selectChainBuilder = new ChainBuilder<SelectProcessor>();
		}
		return selectChainBuilder;
	}

	public SelectProcessorBuilder addFirst(final SelectProcessor e) {
		if (e == null) {
			return this;
		}
		getSelectChainBuilder().addFirst(e);
		return this;
	}

	public SelectProcessorBuilder addLast(final SelectProcessor e) {
		if (e == null) {
			return this;
		}
		getSelectChainBuilder().addLast(e);
		return this;
	}

	public SelectProcessorBuilder add(final SelectProcessor e) {
		return addLast(e);
	}

	public SelectProcessorBuilder addAllFirst(final SelectProcessor... e) {
		if (e == null) {
			return this;
		}
		getSelectChainBuilder().addAllFirst(e);
		return this;
	}

	public SelectProcessorBuilder addAllLast(final SelectProcessor... e) {
		if (e == null) {
			return this;
		}
		getSelectChainBuilder().addAllLast(e);
		return this;
	}

	public SelectProcessorBuilder addAll(final SelectProcessor... e) {
		return addAllLast(e);
	}
}
