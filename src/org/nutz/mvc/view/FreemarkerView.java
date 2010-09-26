package org.nutz.mvc.view;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.lang.Files;
import org.nutz.mvc.View;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;

/**
 * Get from http://axhack.javaeye.com/blog/542441
 * 尚未调整
 * @author axhack
 *
 */
public class FreemarkerView implements View {
	private static final String CONFIG_SERVLET_CONTEXT_KEY = "freemarker.Configuration";
	private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";    
    private static final String KEY_APPLICATION = "Application";
    private static final String KEY_REQUEST_MODEL = "Request";
    private static final String KEY_SESSION_MODEL = "Session";    
    private static final String KEY_REQUEST_PARAMETER_MODEL = "Parameters";
    private static final String KEY_EXCEPTION = "exception";
    private static final String OBJ = "obj";	
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final String SESSION = "session";
    private static final String APPLICATION = "application";
    private static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    //private static final String BASE = "base";
	private String path;
	private Configuration cfg; 
	
	public FreemarkerView(String path){
		this.path=path;
	}
	
	@SuppressWarnings({"rawtype", "unchecked"})
	public void render(HttpServletRequest request, HttpServletResponse response,
			Object value) throws Throwable {
		ServletContext sc=request.getSession().getServletContext();		
		cfg = getConfiguration(sc);		
		//添加数据模型
		Map<Object,Object> root = new HashMap<Object,Object>();		
		root.put(OBJ, value);
		root.put(REQUEST, request);
		root.put(RESPONSE, response);
		root.put(SESSION, request.getSession());
		root.put(APPLICATION, sc);		
		//root.put(BASE, request.getContextPath());	
		Enumeration<String> reqs=request.getAttributeNames();
		while(reqs.hasMoreElements()){
			String strKey=reqs.nextElement();
			root.put(strKey, request.getAttribute(strKey));
		}
		//让freemarker支持jsp 标签
		jspTaglibs(sc,request,response,root,cfg.getObjectWrapper());
		//模版路径
		Template t = cfg.getTemplate(path);		
		response.setContentType("text/html; charset="+t.getEncoding());		
		t.process(root, response.getWriter());       
	}
	
	public final synchronized Configuration getConfiguration(ServletContext servletContext) throws TemplateException {
        Configuration config = (Configuration) servletContext.getAttribute(CONFIG_SERVLET_CONTEXT_KEY);
        if (config == null) {
        	config = new Configuration();        	
        	config.setServletContextForTemplateLoading(servletContext, "/");
        	//config.setDefaultEncoding("UTF-8");
            config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
            //读取freemarker配置文件
            loadSettings(servletContext,config);
            
            servletContext.setAttribute(CONFIG_SERVLET_CONTEXT_KEY, config);
        }        
        config.setWhitespaceStripping(true);        
        return config;
	}
	protected void loadSettings(ServletContext servletContext,Configuration config){		
		InputStream in = null;
        try {        	
        	in=new BufferedInputStream(new FileInputStream(Files.findFile("freemarker.properties")));         	
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                config.setSettings(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
        	 e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(IOException io) {
                	io.printStackTrace();
                }
            }
        }
	}
	
	protected void jspTaglibs(ServletContext servletContext,HttpServletRequest request,HttpServletResponse response,Map<Object,Object> model,ObjectWrapper wrapper){
		synchronized (servletContext) {
            ServletContextHashModel servletContextModel = (ServletContextHashModel) servletContext.getAttribute(ATTR_APPLICATION_MODEL);

            if (servletContextModel == null) {

                GenericServlet servlet = JspSupportServlet.jspSupportServlet;
                // TODO if the jsp support  servlet isn't load-on-startup then it won't exist
                // if it hasn't been accessed, and a JSP page is accessed
                if (servlet != null) {
                    servletContextModel = new ServletContextHashModel(servlet, wrapper);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
                    TaglibFactory taglibs = new TaglibFactory(servletContext);
                    servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibs);
                }

            }

            model.put(KEY_APPLICATION, servletContextModel);
            model.put(KEY_JSP_TAGLIBS, (TemplateModel) servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL));
        }
		
		HttpSession session = request.getSession(false);
        if (session != null) {
            model.put(KEY_SESSION_MODEL, new HttpSessionHashModel(session, wrapper));
        }
		
		HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);

        if ((requestModel == null) || (requestModel.getRequest() != request)) {
            requestModel = new HttpRequestHashModel(request, response, wrapper);
            request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
        }
        model.put(KEY_REQUEST_MODEL, requestModel);
        
        HttpRequestParametersHashModel reqParametersModel = (HttpRequestParametersHashModel) request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
        if (reqParametersModel == null || requestModel.getRequest() != request) {
            reqParametersModel = new HttpRequestParametersHashModel(request);
            request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, reqParametersModel);
        }
        model.put(KEY_REQUEST_PARAMETER_MODEL, reqParametersModel);
        
        Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

        if (exception == null) {
            exception = (Throwable) request.getAttribute("javax.servlet.error.JspException");
        }

        if (exception != null) {
            model.put(KEY_EXCEPTION, exception);
        }
	}
}
