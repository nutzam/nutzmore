package org.nutz.integration.nettice.core.config;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.ConfigurationException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.xml.sax.InputSource;

public class RouterConfig {

	private List<String> actionPackages;
	
	public static RouterConfig parse(String filePath) throws Exception {
		File file = Files.findFile(filePath);
		if (Lang.isEmpty(file)) {
			throw new IllegalArgumentException("config file [" + filePath + "] not exists");
		}
		return parse(Streams.fileIn(file));
	}

	private static RouterConfig parse(InputStream ins) throws Exception {
		RouterConfig config = new RouterConfig();
		SAXReader saxReader = new SAXReader();
		Document document = null;
		try {
			document = saxReader.read(new InputSource(ins));
		} catch (DocumentException e) {
			throw new ConfigurationException("config file parse error");
		}
		if (document != null) {
			Element rootElement = document.getRootElement();
			Iterator<?> ie = rootElement.elementIterator();
			while (ie.hasNext()) {
				Element element = (Element) ie.next();
				if ("action-package".equals(element.getName())) {
					config.actionPackages = parseActionPackages(element);
				}
			}
		}
		return config;
	}

	private static List<String> parseActionPackages(Element actionPackagesElem) {
		Iterator<?> packageInterator = actionPackagesElem.elementIterator();
		List<String> packages = new ArrayList<String>();
		while (packageInterator.hasNext()) {
			Element element = (Element) packageInterator.next();
			packages.add(element.getTextTrim());
		}
		return packages;
	}

	public List<String> getActionPacages() {
		return actionPackages;
	}
}
