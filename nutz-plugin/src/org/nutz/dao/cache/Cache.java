//$Id: Cache.java,v 1.7 2005/04/21 07:57:19 oneovthafew Exp $
package org.nutz.dao.cache;

import java.util.Map;

/**
 * 缓存接口,key现在的生成策略是类名+id
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 */
public interface Cache {
	/**
	 * Get an item from the cache
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object read(Object key) ;
	/**
	 * Get an item from the cache, nontransactionally
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object get(Object key) ;
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void put(Object key, Object value) ;
	/**
	 * Add an item to the cache
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void update(Object key, Object value) ;
	/**
	 * Remove an item from the cache
	 */
	public void remove(Object key) ;
	/**
	 * Clear the cache
	 */
	public void clear() ;
	/**
	 * Clean up
	 */
	public void destroy() ;
	/**
	 * If this is a clustered cache, lock the item
	 */
	public void lock(Object key);
	/**
	 * If this is a clustered cache, unlock the item
	 */
	public void unlock(Object key);
	/**
	 * Generate a timestamp
	 */
	public long nextTimestamp();
	/**
	 * Get a reasonable "lock timeout"
	 */
	public int getTimeout();
	
	/**
	 * Get the name of the cache region
	 */
	public String getRegionName();

	/**
	 * The number of bytes is this cache region currently consuming in memory.
	 *
	 * @return The number of bytes consumed by this region; -1 if unknown or
	 * unsupported.
	 */
	public long getSizeInMemory();

	/**
	 * The count of entries currently contained in the regions in-memory store.
	 *
	 * @return The count of entries in memory; -1 if unknown or unsupported.
	 */
	public long getElementCountInMemory();

	/**
	 * The count of entries currently contained in the regions disk store.
	 *
	 * @return The count of entries on disk; -1 if unknown or unsupported.
	 */
	public long getElementCountOnDisk();
	
	/**
	 * optional operation
	 */
	public Map toMap();
}






