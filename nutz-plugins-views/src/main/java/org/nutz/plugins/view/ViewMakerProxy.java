package org.nutz.plugins.view;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 从Ioc中加载ViewMaker,得到View就返回
 * @author wendal
 */
public class ViewMakerProxy implements ViewMaker {
	
	private List<ViewMaker> list;

	public View make(Ioc ioc, String type, String value) {
		if (list == null) {
		    list = new ArrayList<ViewMaker>();
		    for (String name : ioc.getNames()) {
		        if (name.toLowerCase().contains("viewmaker"))
		            list.add((ViewMaker)ioc.get(ViewMaker.class, name));
		    }
		}
		View view = null;
		for (ViewMaker viewMaker : list) {
			view = viewMaker.make(ioc, type, value);
			if (view != null)
				return view;
		}
		return null;
	}

	
}
