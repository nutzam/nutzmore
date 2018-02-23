package org.nutz.plugins.dict.chain;

import java.util.List;
import java.util.Map;

public class ImmutableSelectProcessor implements SelectProcessor {
	private final SelectProcessor[] selectProcessors;

	/*
	 * public ImmutableSelectProcessor(final SelectProcessor... requestInterceptors)
	 * { //this(requestInterceptors); }
	 */
	public ImmutableSelectProcessor(final SelectProcessor... selectProcessors) {
		super();
		if (selectProcessors != null) {
			final int l = selectProcessors.length;
			this.selectProcessors = new SelectProcessor[l];
			System.arraycopy(selectProcessors, 0, this.selectProcessors, 0, l);
		} else {
			this.selectProcessors = new SelectProcessor[0];
		}
	}

	public ImmutableSelectProcessor(final List<SelectProcessor> selectProcessors) {
		super();
		if (selectProcessors != null) {
			final int l = selectProcessors.size();
			this.selectProcessors = selectProcessors.toArray(new SelectProcessor[l]);
		} else {
			this.selectProcessors = new SelectProcessor[0];
		}
	}

	@Override
	public void process(String value, String text) {
		for (final SelectProcessor selectProcessor : this.selectProcessors) {
			selectProcessor.process(value, text);
		}
	}

	@Override
	public void put(String key) {
		for (final SelectProcessor selectProcessor : this.selectProcessors) {
			selectProcessor.put(key);
		}
	}

	@Override
	public void putGlobalDict(Map<String, Object> globalDictVal) {
		for (final SelectProcessor selectProcessor : this.selectProcessors) {
			selectProcessor.putGlobalDict(globalDictVal);
		}
	}
}
