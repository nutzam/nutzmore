/**
 * 
 */
package org.nutz.dao.cache;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.nutz.dao.Dao;
import org.nutz.dao.cache.method.FetchMethodHandler;
import org.nutz.dao.cache.method.IDaoCacheMethodHandler;
import org.nutz.dao.convent.utils.CommonUtils;
import org.nutz.dao.entity.EntityHolder;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.impl.ConventionEntityMaker;

/**
 * 缓存实现的主要处理器
 * 1.建议Dao接口添加setEntityMaker方法
 * 2.建议Dao接口getEntityHolder方法,以及EntityHolder中添加getMappings方法
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class CacheNutDaoInvocationHandler implements InvocationHandler {

	private Dao dao;
	private EntityMaker entityMaker;
	private Cache cache;
	/**
	 * 存储DAO中方法对应的缓存处理器
	 * key:dao中的方法名
	 * value:对应的处理器
	 */
	private static Map<String,IDaoCacheMethodHandler> DAO_METHOD_HANDLERS=new HashMap<String,IDaoCacheMethodHandler>();
	
	static{
		DAO_METHOD_HANDLERS.put("fetch", new FetchMethodHandler());
	}
	public CacheNutDaoInvocationHandler(Dao dao) {
		super();
		this.dao = dao;
		this.cache=new HashtableCache(dao.toString());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(entityMaker!=null){
			CommonUtils.invokeMethod(dao, "setEntityMaker", new Class[]{EntityMaker.class}, new Object[]{new ConventionEntityMaker()});
		}
		
		CacheStrategy cacheUtils=new CacheStrategy();
		cacheUtils.setDao(dao);
		ObsArgClass msg=new ObsArgClass(method,args,cacheUtils,cache);
		IDaoCacheMethodHandler daoHandler=DAO_METHOD_HANDLERS.get(method.getName());
		if(daoHandler!=null){
			return daoHandler.handler(msg);
		}
		return method.invoke(dao, args);
	}
	
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public EntityMaker getEntityMaker() {
		return entityMaker;
	}
	public void setEntityMaker(EntityMaker entityMaker) {
		this.entityMaker = entityMaker;
	}


}
