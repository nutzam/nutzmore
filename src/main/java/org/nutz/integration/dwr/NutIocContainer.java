package org.nutz.integration.dwr;

import java.util.Arrays;
import java.util.Collection;

import org.directwebremoting.impl.DefaultContainer;
import org.nutz.mvc.Mvcs;

public class NutIocContainer extends DefaultContainer {

	public Object getBean(String id) {
		return Mvcs.getIoc().get(null, id);
	}
	
	public Collection<String> getBeanNames() {
		return Arrays.asList(Mvcs.getIoc().getNames());
	}
}
