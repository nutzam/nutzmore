package org.nutz.plugins.slog.service;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
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
			dao().fastInsert(syslog);
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
            if (tmp.length > 2) {
                source = tmp[2].getClassName() + "#" + tmp[2].getMethodName();
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
                slog.setUid(((Number)uid).intValue());
        }
        catch (Exception e) {
            log.debug("get user id fail", e);
        }
        if (async)
            async(slog);
        else
            sync(slog);
    }
	
	public static Callable<Object> GET_USER_ID = new Callable<Object>() {
        public Object call() throws Exception {
            Object u;
            try {
                u = SecurityUtils.getSubject().getPrincipal();
            } catch (Throwable e) {
                return Integer.valueOf(-1);
            }
            if (u != null) {
                return u.toString();
            }
            return Integer.valueOf(0);
        };
    };
    
    /**
     * 获取按月分表的Dao实例,即当前日期的dao实例
     * @return
     */
    protected Dao dao() {
        Calendar cal = Calendar.getInstance();
        String key = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);
        return dao(key);
    }
    
    /**
     * 获取特定月份的Dao实例
     * @param key
     * @return
     */
    protected Dao dao(String key) {
        Dao dao = ymDaos.get(key);
        if (dao == null) {
            synchronized (this) {
                dao = ymDaos.get(key);
                if (dao == null) {
                    dao = Daos.ext(this.dao, key);
                    dao.create(SlogBean.class, false);
                    ymDaos.put(key, dao);
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
}
