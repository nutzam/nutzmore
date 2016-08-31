package org.nutz.plugins.nop.core.serialize;

import java.io.File;

import org.nutz.filepool.FilePool;
import org.nutz.filepool.NutFilePool;
import org.nutz.json.ToJson;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.repo.Base64;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file UploadFile.java
 *
 * @description 文件上传
 *
 * @time 2016年8月31日 下午5:05:51
 *
 */
@ToJson(value = "serialize")
public class UploadFile extends SerizlizeObject<UploadFile> {

	private String name;

	private byte[] content;

	FilePool pool = new NutFilePool(System.getProperty("java.io.tmpdir"), 1000);

	// new UU32FilePool(System.getProperty("java.io.tmpdir"));

	public UploadFile() {

	}

	public UploadFile(File file) {
		this.name = Files.getName(file);
		this.content = Files.readBytes(file);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		if (Strings.isBlank(name))
			return null;
		return name.substring(name.lastIndexOf("."));
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.serialize.SerizlizeObject#serialize()
	 */
	@Override
	public String serialize() {
		return String.format("\"%s\"", Base64.encodeToString(name.getBytes(), false) + "@" + Base64.encodeToString(content, false));
	}

	public File getFile() {

		File f = pool.createFile(getType());
		Files.write(f, content);

		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nutz.plugins.nop.core.serialize.SerizlizeObject#serialize(java.lang
	 * .String)
	 */
	@Override
	public UploadFile serialize(String data) {
		if (Strings.isBlank(data) || data.indexOf("@") <= 0) {
			return null;
		}
		String[] infos = data.split("@");
		if (infos == null || infos.length < 2) {
			return null;
		}
		UploadFile temp = new UploadFile();
		temp.setName(new String(Base64.decode(infos[0])));
		temp.setContent(Base64.decode(infos[1]));
		return temp;
	}
}
