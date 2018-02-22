package org.nutz.plugins.view;

import org.nutz.mvc.ViewMaker2;

/**
 * 多视图接口 继承ViewMaker2 常量定义在此
 * 
 * @author 邓华锋 http://dhf.ink
 *
 */
public interface MultiView extends ViewMaker2 {
	String VIEW_PREFIX = "viewPrefix";
	String VIEW_SUFFIX = "viewSuffix";
	String DEFAULT_ENCODING = "UTF-8";
	String DEFAULT_CONTENT_TYPE = "text/html";
	String DEFAULT_PREFIX = "/WEB-INF/template/";
	String DEFAULT_SUFFIX = ".html";
	String OBJ = "obj";
	String REQUEST = "request";
	String RESPONSE = "response";
	String SESSION = "session";
	String APPLICATION = "application";
	String VIEW_NAME = "viewName";
	String PATH = "path";
	String BASE_PATH = "basePath";
	String SERVLET_EXTENSION = "servletExtension";
	String SERVLET_EXTENSION_KEY = "servlet.extension";
	String TPL_DIR = "tplDir";
	String RESOURCE_DIR = "resource.dir";
	String RES_PATH = "resPath";
	String TPL_RES_PATH = "tplResPath";
	String WEB_INF = "WEB-INF/";
	String PROPS = "props";
	String MSGS = "msgs";
	String CFG = "cfg";
	String EVAL_PATH = "evalPath";
	String DEST = "dest";
	String INNER_VIEW_TYPE = "json|raw|re|void|http|redirect|forward|>>|->";
	String DEFAULT_VIEW = "defaultView";
}
