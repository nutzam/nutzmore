//$Id: HashtableCache.java,v 1.8 2005/04/21 07:57:19 oneovthafew Exp $
package org.nutz.dao.cache;

import java.util.Collections;
import java.util.Hashtable;

import java.util.Map;

/**
 * A lightweight implementation of the <tt>Cache</tt> interface
 * @author Gavin King
 */
public class HashtableCache implements Cache {
	
	private final Map hashtable = new Hashtable();
	private final String regionName;
	
	public HashtableCache(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionName() {
		return regionName;
	}

	public Object read(Object key) {
		return hashtable.get(key);
	}

	public Object get(Object key) {
		return hashtable.get(key);
	}

	public void update(Object key, Object value) {
		put(key, value);
	}
	
	public void put(Object key, Object value) {
		hashtable.put(key, value);
	}

	public void remove(Object key) {
		hashtable.remove(key);
	}

	public void clear() {
		hashtable.clear();
	}

	public void destroy(){

	}

	public void lock(Object key){
		// local cache, so we use synchronization
	}

	public void unlock(Object key){
		// local cache, so we use synchronization
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public int getTimeout() {
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}

	public long getSizeInMemory() {
		return -1;
	}

	public long getElementCountInMemory() {
		return hashtable.size();
	}

	public long getElementCountOnDisk() {
		return 0;
	}
	
	public Map toMap() {
		return Collections.unmodifiableMap(hashtable);
	}

	public String toString() {
		return "HashtableCache(" + regionName + ')';
	}

}
