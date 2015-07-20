package org.nutz.plugins.protobuf.mvc.view;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class ProtoViewMaker implements ViewMaker {

	@Override
	public View make(Ioc ioc, String type, String value) {
		if ("proto".equals(type)) {
			return new ProtoView();
		} else if ("jproto".equals(type)) {
			return new JProtoView();
		}
		return null;
	}

}
