package org.nutz.plugins.view.freemarker;

import java.util.ArrayList;
import java.util.List;

public class FreemarkerDirectiveFactory {
	private List<FreemarkerDirective> list = new ArrayList<FreemarkerDirective>();
	private String freemarker;
	private FreemarkerDirective[] objs;

	public FreemarkerDirectiveFactory(FreemarkerDirective... objs) {
		this.objs = objs;
	}

	public List<FreemarkerDirective> getList() {
		return list;
	}

	public String getFreemarker() {
		return freemarker;
	}

	public void init() {
		for (FreemarkerDirective freemarkerDirective : objs) {
			list.add(freemarkerDirective);
		}
	}
}
