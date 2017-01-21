package org.nutz.plugins.sigar.gather;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.lang.util.NutMap;

/**
 * 磁盘信息收集器
 * 
 * @author wkipy
 *
 */
public class DISKGather {

	private String userHome = System.getProperty("user.home");;

	private String tempDir = System.getProperty("java.io.tmpdir");

	List<NutMap> details = new ArrayList<NutMap>();

	private FileSystem config;
	private FileSystemUsage stat;

	public static DISKGather gather(Sigar sigar) {
		DISKGather data = new DISKGather();
		FileSystem[] fsArr;
		try {
			fsArr = sigar.getFileSystemList();
			for (FileSystem fs : fsArr) {
				NutMap temp = new NutMap();
				temp.addv("fileSystem", fs);
				temp.addv("usage", sigar.getFileSystemUsage(fs.getDirName()));
				temp.addv("fileInfo", sigar.getFileInfo(fs.getDirName()));
				data.details.add(temp);
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * @return the userHome
	 */
	public String getUserHome() {
		return userHome;
	}

	/**
	 * @param userHome
	 *            the userHome to set
	 */
	public void setUserHome(String userHome) {
		this.userHome = userHome;
	}

	/**
	 * @return the tempDir
	 */
	public String getTempDir() {
		return tempDir;
	}

	/**
	 * @param tempDir
	 *            the tempDir to set
	 */
	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

	/**
	 * @return the details
	 */
	public List<NutMap> getDetails() {
		return details;
	}

	/**
	 * @param details
	 *            the details to set
	 */
	public void setDetails(List<NutMap> details) {
		this.details = details;
	}

	/**
	 * @return the config
	 */
	public FileSystem getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public void setConfig(FileSystem config) {
		this.config = config;
	}

	/**
	 * @return the stat
	 */
	public FileSystemUsage getStat() {
		return stat;
	}

	/**
	 * @param stat
	 *            the stat to set
	 */
	public void setStat(FileSystemUsage stat) {
		this.stat = stat;
	}

}
