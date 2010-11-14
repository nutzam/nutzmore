package org.nutz.mvc.init;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.init.MessageMap;

/**
 * From issue 226, http://code.google.com/p/nutz/issues/detail?id=226
 * 尚未调整
 * @author landraxee
 *
 */
public class NormalMessageLoader implements org.nutz.mvc.MessageLoader {

	private static final String MSG_SUFFIX = ".properties";
	private File dir;

	public Map<String, Map<String, String>> load(String path) {
		dir = Files.findFile(path);
		if (null == dir || !dir.isDirectory())
			throw Lang.makeThrow("'%s' is not a directory", path);
		Map<String, Map<String, String>> msgss = new HashMap<String, Map<String, String>>();
		// Load default
		String key = Mvcs.DEFAULT_MSGS;
		Map<String, String> msgs = _load(dir);
		if (null != msgs)
			msgss.put(key, msgs);
		// Local for each locale languange
		File[] dirs = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory())
					if (!f.getName().startsWith("."))
						return true;
				return false;
			}
		});
		for (File d : dirs) {
			key = d.getName();
			msgs = _load(d);
			if (null != msgs)
				msgss.put(key, msgs);
		}
		// return it
		return msgss.size() == 0 ? null : msgss;
	}

	private static Map<String, String> _load(File dir) {
		if (null == dir || !dir.isDirectory())
			return null;
		Map<String, String> msgs = new MessageMap();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isFile())
					if (f.getName().endsWith(MSG_SUFFIX))
						return true;
				return false;
			}
		});
		for (File f : files) {
			
			Properties p = new Properties();
			Reader reader = null;
			try {
				reader = new FileReader(f);
				p.load(reader);
			} catch (Exception e) {//FileNotFoundException IOException
				throw Lang.wrapThrow(e);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
					}
			}
			
			for (Entry<?, ?> en : p.entrySet())
				msgs.put(en.getKey().toString(), en.getValue().toString());
		}
		return msgs.size() == 0 ? null : msgs;
	}

}
