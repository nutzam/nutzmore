package org.nutz.plugins.slog.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.nutz.Nutz;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.ClassMetaReader;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.plugins.slog.bean.SlogBean;

/**
 * slog的服务类
 * @author wendal
 *
 */
public class SlogService {
	
	private static final Log log = Logs.get();
	
	/**
	 * 原始Dao实例
	 */
	protected Dao dao;
	
	/**
	 * 按月分表的dao实例
	 */
	protected Map<String, Dao> ymDaos = new HashMap<String, Dao>();
	
	/**
	 * 异步插入日志
	 * @param slog 日志对象
	 */
	@Async
	public void async(Object syslog) {
		this.sync(syslog);
	}
	
	/**
     * 同步插入日志
     * @param slog 日志对象
     */
	public void sync(Object syslog) {
		try {
			dao().insert(syslog);
		} catch (Throwable e) {
			log.info("insert syslog sync fail", e);
		}
	}
    
    public SlogBean c(String t, String tag, String source, String msg) {
        SlogBean sysLog = new SlogBean();
        sysLog.setCreateTime(new Date());
        if (t == null || tag == null || msg == null) {
            throw new RuntimeException("t/tag/msg can't null");
        }
        if (source == null) {
            StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
            if (tmp.length > 3) {
                source = tmp[3].getClassName() + "#" + tmp[3].getMethodName();
            } else {
                source = "main";
            }
        }
        sysLog.setT(t);;
        sysLog.setTag(tag);;
        sysLog.setSource(source);;
        sysLog.setMsg(msg);;
        if (Mvcs.getReq() != null) {
            sysLog.setIp(Lang.getIP(Mvcs.getReq()));
        }
        return sysLog;
    }
    
    public void log(String t, String tag, String source, String msg, boolean async) {
        SlogBean slog = c(t, tag, source, msg);
        try {
            Object uid = GET_USER_ID.call();
            if (uid != null && uid instanceof Number)
                slog.setUid(((Number)uid).longValue());
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("get user id fail", e);
        }
        try {
            Object uname = GET_USER_NAME.call();
            slog.setUsername(Strings.sBlank(uname));
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("get user name fail", e);
        }
        if (async)
            async(slog);
        else
            sync(slog);
    }
	
    /**
     * 获取用户id, 长整型
     */
	public static Callable<Object> GET_USER_ID = new Callable<Object>() {
        public Object call() throws Exception {
            Object u;
            try {
                u = SecurityUtils.getSubject().getPrincipal();
            } catch (Throwable e) {
                return 0;
            }
            if (u != null) {
                return u;
            }
            return 0;
        };
    };
    
    /**
     * 获取用户名称, 字符串类型
     */
    public static Callable<Object> GET_USER_NAME = new Callable<Object>() {
        public Object call() throws Exception {
            Object uid = GET_USER_ID.call();
            if (uid != null && ((Number)uid).longValue() > 0)
                return uid;
            return "";
        };
    };
    
    /**
     * 获取按月分表的Dao实例,即当前日期的dao实例
     * @return
     */
    public Dao dao() {
        Calendar cal = Calendar.getInstance();
        String key = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
        return dao(key);
    }
    
    /**
     * 获取特定月份的Dao实例
     * @param key
     * @return
     */
    public Dao dao(String key) {
        Dao dao = ymDaos.get(key);
        if (dao == null) {
            synchronized (this) {
                dao = ymDaos.get(key);
                if (dao == null) {
                    dao = Daos.ext(this.dao, key);
                    dao.create(SlogBean.class, false);
                    ymDaos.put(key, dao);
                    try {
                        Daos.migration(dao, SlogBean.class, true, false);
                    }
                    catch (Throwable e) {}
                }
            }
        }
        return dao;
    }
	

    /**
     * 本方法通常由aop拦截器调用.
     * @param t 日志类型
     * @param tag 标签
     * @param source 源码位置
     * @param seg 消息模板
     * @param els 消息模板的EL表达式预处理表
     * @param async 是否异步插入
     * @param args 方法参数
     * @param re 方法返回值
     * @param method 方法实例
     * @param obj 被拦截的对象
     * @param e 异常对象
     */
    public void log(String t, String tag, String source, 
                    CharSegment seg, Map<String, El> els, 
                    boolean async, 
                    Object[] args, Object re, Method method, Object obj,
                    Throwable e) {
        String _msg = null;
        if (seg.hasKey()) {
            Context ctx = Lang.context();
            List<String> names = null;
            if (Nutz.majorVersion() == 1 && Nutz.minorVersion() < 60) {
                Class<?> klass = obj.getClass();
                if (klass.getName().endsWith("$$NUTZAOP"))
                    klass = klass.getSuperclass();
                String key = klass.getName();
                if (caches.containsKey(key))
                    names = caches.get(key).get(ClassMetaReader.getKey(method));
                else {
                    try {
                        Map<String, List<String>> tmp = MethodParamNamesScaner.getParamNames(klass);
                        names = tmp.get(ClassMetaReader.getKey(method));
                        caches.put(key, tmp);
                    }
                    catch (IOException e1) {
                        log.debug("error when reading param name");
                    }
                }
            } else {
                names = MethodParamNamesScaner.getParamNames(method);
            }
            if (names != null) {
                for (int i = 0; i < names.size() && i < args.length; i++) {
                    ctx.set(names.get(i), args[i]);
                }
            }
            ctx.set("obj", obj);
            ctx.set("args", args);
            ctx.set("re", re);
            ctx.set("return", re);
            ctx.set("req", Mvcs.getReq());
            ctx.set("resp", Mvcs.getResp());
            Context _ctx = Lang.context();
            for (String key :seg.keys()) {
                _ctx.set(key, els.get(key).eval(ctx));
            }
            _msg = seg.render(_ctx).toString();
        } else {
            _msg = seg.getOrginalString();
        }
        log(t, tag, source, _msg, async);
    }
    
    protected static Map<String, Map<String, List<String>>> caches = new HashMap<String, Map<String,List<String>>>();
}
