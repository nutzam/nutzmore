package org.nutz.mvc.view;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.Ioc;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 从Ioc中加载ViewMaker,得到View就返回
 * @author wendal
 *
 */
public class ViewMakerProxy implements ViewMaker {
	
	private List<ViewMaker> list;
	
	private Object lock = new Object();

	@Override
	public View make(Ioc ioc, String type, String value) {
		if (list == null)
			synchronized (lock) {
				if (list == null) {
					list = new ArrayList<ViewMaker>();
					for (String name : ioc.getNames()) {
						try {
							Object obj = ioc.get(null, name);
							if (obj instanceof ViewMaker)
								list.add((ViewMaker)obj);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
					Logs.getLog(getClass()).infof("Found %d ViewMaker in ioc.",list.size());
				}
			}
		View view = null;
		for (ViewMaker viewMaker : list) {
			view = viewMaker.make(ioc, type, value);
			if (view != null)
				break;
		}
		return view;
	}

}
