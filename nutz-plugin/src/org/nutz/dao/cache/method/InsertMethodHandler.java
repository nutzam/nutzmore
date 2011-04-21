/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * @author Administrator
 *
 */
public class InsertMethodHandler implements IDaoCacheMethodHandler {

	/* (non-Javadoc)
	 * @see org.nutz.dao.cache.method.IDaoCacheMethodHandler#handler(org.nutz.dao.cache.ObsArgClass)
	 */
	public Object handler(ObsArgClass msg) {
		Object returnValue=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
		Object[] args=msg.getArgs();
		if(args.length==1){//<T> T insert(T obj)
			Object key=msg.getCacheStrategy().getKey(args[0]);
			msg.getCache().remove(key);
		}
		if(args.length==2){
			if(args[0].getClass()==Class.class){
				
			}else if(args[0].getClass()==String.class){
				
			}else{
				throw new RuntimeException("不支持的方法");
			}
		}
		return returnValue;
	}

}
