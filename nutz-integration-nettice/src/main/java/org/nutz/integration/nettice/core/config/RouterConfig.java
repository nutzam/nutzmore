package org.nutz.integration.nettice.core.config;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RouterConfig {

	private List<String> actionPackages = new ArrayList<String>();

	public static RouterConfig parse(String filePath) throws Exception {
		File file = Files.findFile(filePath);
		if (Lang.isEmpty(file)) {
			throw new IllegalArgumentException("config file [" + filePath + "] not exists");
		}
		return parse(Streams.fileIn(file));
	}

	private static RouterConfig parse(InputStream ins) throws Exception {
		RouterConfig config = new RouterConfig();
		org.w3c.dom.Document document = Lang.xmls().parse(ins);
		NodeList list = document.getElementsByTagName("action-package");
		parseActionPackages(config.actionPackages, list);
		return config;
	}

	private static void parseActionPackages(List<String> actionPackages, NodeList actionPackage) {
		for (int i = 0; i < actionPackage.getLength(); i++) {
			Element actionackage = (Element) actionPackage.item(i);
			NodeList pkg = actionackage.getElementsByTagName("package");
			int number = pkg.getLength();
			for (int num = 0; num < number; num++) {
				actionPackages.add(Strings.trim(pkg.item(num).getTextContent()));
			}
		}
	}

	public List<String> getActionPacages() {
		return actionPackages;
	}
}
