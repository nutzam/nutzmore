package org.nutz.plugins.nop.core.sign;

import org.nutz.lang.Strings;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;

/**
 * 抽象一下
 * 
 * @author kerbores
 *
 */
public abstract class AbstractSinger implements Signer {

	private AppsecretFetcher fetcher;

	public AbstractSinger(AppsecretFetcher fetcher) {
		this.fetcher = fetcher;
	}
	
	

	public AppsecretFetcher getFetcher() {
		return fetcher;
	}



	public void setFetcher(AppsecretFetcher fetcher) {
		this.fetcher = fetcher;
	}



	@Override
	public boolean check(NOPRequest request) {
		return Strings.equals(sign(request), request.getHeader().get(NOPConfig.signKey));
	}

}
