package org.nutz.plugins.nop.core.sign;

import org.nutz.lang.Lang;

public interface AppsecretFetcher {
	
	public static  AppsecretFetcher defaultFetcher = new AppsecretFetcher() {
		
		@Override
		public String fetch(String key) {
			return Lang.md5(key);
		}
	}; 
	
	public String fetch(String key) ;

}
