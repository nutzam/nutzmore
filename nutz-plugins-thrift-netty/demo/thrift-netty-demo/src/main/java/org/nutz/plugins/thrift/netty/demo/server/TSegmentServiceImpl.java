package org.nutz.plugins.thrift.netty.demo.server;

import java.util.Map;

import org.apache.thrift.TException;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.sada.common.thrift.TSegmentService1;

@ThriftService
public class TSegmentServiceImpl implements TSegmentService1.Iface {


	@Override
	@ThriftMethod
	public String getArabicWords(String inputStr) throws TException {
		return "abc >> " + inputStr;
	}

	@Override
	public Map<String, Integer> getArabicWordTimes(String inputStr) throws TException {
		return null;
	}

}
