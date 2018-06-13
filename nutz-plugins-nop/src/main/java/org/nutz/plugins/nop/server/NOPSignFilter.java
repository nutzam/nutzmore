package org.nutz.plugins.nop.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugins.nop.core.NOPData;
import org.nutz.plugins.nop.core.sign.AppsecretFetcher;
import org.nutz.plugins.nop.core.sign.DigestSigner;
import org.nutz.plugins.nop.core.sign.Signer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPSignFilter.java
 *
 * @description NOP 签名检查拦截器
 *
 * @time 2016年8月31日 下午4:01:18
 *
 */
public class NOPSignFilter implements ActionFilter {

	public static class ResettableStreamHttpServletRequest extends
			HttpServletRequestWrapper {

		private byte[] rawData;
		private HttpServletRequest request;

		private Map<String, String[]> _paramMap;

		// private Collection<Part> _parts;

		public ResettableStreamHttpServletRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
			try {
				_paramMap = parsrParaMap();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * @return
		 * @throws IOException
		 */
		private Map<String, String[]> parsrParaMap() throws IOException {
			Map<String, String[]> target = new HashMap<String, String[]>();
			String info = null;
			if (Strings.equalsIgnoreCase(request.getMethod(), "GET")) {// get请求
				info = getQueryString();
			} else {
				info = new String(Streams.readBytes(getInputStream()), request.getCharacterEncoding());
			}
			if (Strings.isBlank(info)) {
				return target;
			}

			if (Strings.isNotBlank(getHeader("Content-Type"))
					&& getHeader("Content-Type").startsWith("application/x-www-form-urlencoded")) {// 表单参数
				for (String seg : info.split("&")) {
					String key = seg.split("=")[0];
					String val = seg.split("=")[1] == null ? null : seg.split("=")[1];

					String[] v = new String[0];
					if (val == null) {
						v = target.get(key);// 获取之前的
					} else {
						List<String> vals = new ArrayList<String>();
						for (String v1 : val.split(",")) {
							vals.add(URLDecoder.decode(v1, request.getCharacterEncoding()));
						}
						String[] v2 = target.get(key);
						if (v2 != null) {
							vals.addAll(Lang.array2list(v2));
						}
						v = Lang.collection2array(vals);
					}
					target.put(key, v);

				}
			}

			return target;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletRequestWrapper#getParameterMap()
		 */
		@Override
		public Map<String, String[]> getParameterMap() {
			return _paramMap;
		}

		// /*
		// * (non-Javadoc)
		// *
		// * @see javax.servlet.http.HttpServletRequestWrapper#getParts()
		// */
		// @Override
		// public Collection<Part> getParts() throws IOException,
		// ServletException {
		// return _parts;
		// }
		//
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// javax.servlet.http.HttpServletRequestWrapper#getPart(java.lang.String)
		// */
		// @Override
		// public Part getPart(String name) throws IOException, ServletException
		// {
		// for (Part part : _parts) {
		// if (Strings.equals(part.getName(), name)) {
		// return part;
		// }
		// }
		// return null;
		// }

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
		 */
		@Override
		public String getParameter(String name) {
			return _paramMap.get(name) == null ? null : Strings.join(",", _paramMap.get(name));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletRequestWrapper#getParameterNames()
		 */
		@Override
		public Enumeration<String> getParameterNames() {
			return Collections.enumeration(_paramMap.keySet());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.
		 * String)
		 */
		@Override
		public String[] getParameterValues(String name) {
			return _paramMap.get(name);
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			if (rawData == null) {
				rawData = Streams.readBytes(this.request.getInputStream());
				System.err.println(new String(rawData));
				System.err.println(request.getHeader("Content-Type"));
			}
			return new ResettableServletInputStream(new ByteArrayInputStream(rawData));
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(getInputStream()));
		}

		public class ResettableServletInputStream extends ServletInputStream {

			private ByteArrayInputStream stream;

			/**
			 * 
			 */
			public ResettableServletInputStream(ByteArrayInputStream stream) {
				this.stream = stream;
			}

			@Override
			public int read() throws IOException {
				return stream.read();
			}

			@Override
			public boolean isFinished() {
				return stream.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new RuntimeException("Not implemented");
			}
		}
	}

	String digestName;

	AppsecretFetcher fetcher = AppsecretFetcher.defaultFetcher;

	/**
	 * 
	 */
	public NOPSignFilter() {
	}

	public NOPSignFilter(String digestName) {
		this.digestName = digestName;
	}

	/**
	 * @return the digestName
	 */
	public String getDigestName() {
		return digestName;
	}

	/**
	 * @param digestName
	 *            the digestName to set
	 */
	public void setDigestName(String digestName) {
		this.digestName = digestName;
	}

	Log log = Logs.get();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.mvc.ActionFilter#match(org.nutz.mvc.ActionContext)
	 */
	@Override
	public View match(ActionContext ac) {
		ac.setRequest(new ResettableStreamHttpServletRequest(ac.getRequest()));// 重复读流
		HttpServletRequest request = ac.getRequest();
		try {
			fetcher = ac.getIoc().get(AppsecretFetcher.class);
		} catch (Exception e) {
		}
		Signer signer = new DigestSigner(digestName);
		if (signer.check(request, fetcher)) {
			return null;
		} else {
			return new UTF8JsonView().setData(NOPData.exception("checkSign failed"));
		}
	}

}
