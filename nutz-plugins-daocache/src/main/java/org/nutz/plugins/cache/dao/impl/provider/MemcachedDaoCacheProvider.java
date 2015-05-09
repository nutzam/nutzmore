package org.nutz.plugins.cache.dao.impl.provider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.impl.convert.JavaCacheSerializer;

/**
 * 还没完成的实现,无法清零
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MemcachedDaoCacheProvider extends AbstractDaoCacheProvider {
    
    private static final Log log = Logs.get();

	protected MemcachedClient client;
	
	protected int timeout = 3600;
	
	public void init() throws IOException {
		if (client == null) {
			List<InetSocketAddress> addrs = AddrUtil.getAddresses("localhost:11211");
			XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addrs);
			builder.setCommandFactory(new BinaryCommandFactory());
			client = builder.build();
		}
		if (getSerializer() == null)
		    setSerializer(new JavaCacheSerializer());
	}

	public void depose() throws Throwable {
		if (client != null)
			client.shutdown();
	}

	public Object get(String cacheName, String key) {
	    Object obj = null;
        try {
            obj = client.get(cacheName + ":" + key);
        }
        catch (Exception e) {
            log.info("get cache fail", e);
        }
	    if (obj == null)
	        return null;
		return getSerializer().back(obj);
	}

	public boolean put(String cacheName, String key, Object obj) {
	    Object data = getSerializer().from(obj);
	    if (data == null)
	        return false;
	    try {
            client.set(cacheName + ":" + key, timeout, data);
            return true;
        }
        catch (Exception e) {
            log.info("put obj fail", e);
        }
		return false;
	}

	public void clear(String cacheName) {
	    
	}
}
