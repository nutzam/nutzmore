//$Id: EhCache.java,v 1.10 2005/04/21 07:57:19 oneovthafew Exp $
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 - 2004 Greg Luck.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by Greg Luck
 *       (http://sourceforge.net/users/gregluck) and contributors.
 *       See http://sourceforge.net/project/memberlist.php?group_id=93232
 *       for a list of contributors"
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "EHCache" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For written
 *    permission, please contact Greg Luck (gregluck at users.sourceforge.net).
 *
 * 5. Products derived from this software may not be called "EHCache"
 *    nor may "EHCache" appear in their names without prior written
 *    permission of Greg Luck.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL GREG LUCK OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by contributors
 * individuals on behalf of the EHCache project.  For more
 * information on EHCache, please see <http://ehcache.sourceforge.net/>.
 *
 */
package org.nutz.dao.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * EHCache plugin for Hibernate
 *
 * EHCache uses a {@link net.sf.ehcache.store.MemoryStore} and a
 * {@link net.sf.ehcache.store.DiskStore}. The {@link net.sf.ehcache.store.DiskStore}
 * requires that both keys and values be {@link Serializable}. For this reason
 * this plugin throws Exceptions when either of these are not castable to {@link Serializable}.
 *
 * @version Taken from EhCache 0.9
 * @author Greg Luck
 * @author Emmanuel Bernard
 */
public class EhCache implements Cache {
    private static final Log log = LogFactory.getLog(EhCache.class);

    private net.sf.ehcache.Cache cache;

    /**
     * Creates a new Hibernate pluggable cache based on a cache name.
     * <p>
     * @param cache The underlying EhCache instance to use.
     */
    public EhCache(net.sf.ehcache.Cache cache) {
	    this.cache = cache;
    }

    /**
     * Gets a value of an element which matches the given key.
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     * @throws CacheException
     */
    public Object get(Object key) throws CacheException {
        try {
            if ( log.isDebugEnabled() ) {
                log.debug("key: " + key);
            }
            if (key == null) {
                return null;
            } 
            else {
                Element element = cache.get( (Serializable) key );
                if (element == null) {
                    if ( log.isDebugEnabled() ) {
                        log.debug("Element for " + key + " is null");
                    }
                    return null;
                } 
                else {
                    return element.getValue();
                }
            }
        } 
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }
	
	public Object read(Object key) throws CacheException {
		return get(key);
	}


    /**
     * Puts an object into the cache.
     * @param key a {@link Serializable} key
     * @param value a {@link Serializable} value
     * @throws CacheException if the parameters are not {@link Serializable}, the {@link CacheManager}
     * is shutdown or another {@link Exception} occurs.
     */
    public void update(Object key, Object value) throws CacheException {
		put(key, value);
    }
	
    /**
     * Puts an object into the cache.
     * @param key a {@link Serializable} key
     * @param value a {@link Serializable} value
     * @throws CacheException if the parameters are not {@link Serializable}, the {@link CacheManager}
     * is shutdown or another {@link Exception} occurs.
     */
	public void put(Object key, Object value) throws CacheException {
        try {
            Element element = new Element( (Serializable) key, (Serializable) value );
            cache.put(element);
        } 
        catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } 
        catch (IllegalStateException e) {
            throw new CacheException(e);
        }

    }

    /**
     * Removes the element which matches the key.
     * <p>
     * If no element matches, nothing is removed and no Exception is thrown.
     * @param key the key of the element to remove
     * @throws CacheException
     */
    public void remove(Object key) throws CacheException {
        try {
            cache.remove( (Serializable) key );
        } 
        catch (ClassCastException e) {
            throw new CacheException(e);
        } 
        catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    /**
     * Remove all elements in the cache, but leave the cache
     * in a useable state.
     * @throws CacheException
     */
    public void clear() throws CacheException {
        try {
            cache.removeAll();
        } 
        catch (IllegalStateException e) {
            throw new CacheException(e);
        } 
        catch (IOException e) {
            throw new CacheException(e);
        }
    }

    /**
     * Remove the cache and make it unuseable.
     * @throws CacheException
     */
    public void destroy() throws CacheException {
        try {
            CacheManager.getInstance().removeCache( cache.getName() );
        } 
        catch (IllegalStateException e) {
            throw new CacheException(e);
        } 
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    /**
     * Calls to this method should perform there own synchronization.
     * It is provided for distributed caches. Because EHCache is not distributed
     * this method does nothing.
     */
    public void lock(Object key) throws CacheException {
    }

    /**
     * Calls to this method should perform there own synchronization.
     * It is provided for distributed caches. Because EHCache is not distributed
     * this method does nothing.
     */
    public void unlock(Object key) throws CacheException {
    }

    /**
     * Gets the next timestamp;
     */
    public long nextTimestamp() {
        return Timestamper.next();
    }

    /**
     * Returns the lock timeout for this cache.
     */
    public int getTimeout() {
        // 60 second lock timeout
        return Timestamper.ONE_MS * 60000;
    }

	public String getRegionName() {
		return cache.getName();
	}

	public long getSizeInMemory() {
		try {
			return cache.calculateInMemorySize();
		}
		catch(Throwable t) {
			return -1;
		}
	}

	public long getElementCountInMemory() {
		try {
			return cache.getSize();
		}
		catch (net.sf.ehcache.CacheException ce) {
			throw new CacheException(ce);
		}
	}

	public long getElementCountOnDisk() {
		return cache.getDiskStoreSize();
	}

	public Map toMap() {
		try {
			Map result = new HashMap();
			Iterator iter = cache.getKeys().iterator();
			while ( iter.hasNext() ) {
				Object key = iter.next();
				result.put( key, cache.get( (Serializable) key ).getValue() );
			}
			return result;
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public String toString() {
		return "EHCache(" + getRegionName() + ')';
	}

}