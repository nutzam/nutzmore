/**
 * 
 */
package org.nutz.dao.convent.utils;

import java.sql.Connection;

/**
 * @author liaohongliu
 *
 * 创建时间: 2010-12-2
 */
public interface IConnectionCallBack {
	public void callBack(Connection conn);
}
