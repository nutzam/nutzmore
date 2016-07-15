package org.nutz.sigar.gather;

import java.util.ArrayList;
import java.util.List;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 磁盘信息收集器
 * 
 * @author wkipy
 *
 */
public class DISKGather {

	private String userHome = System.getProperty("user.home");;

	private String tempDir = System.getProperty("java.io.tmpdir");

	private FileSystem config;
	private FileSystemUsage stat;

	public void populate(Sigar sigar, FileSystem fs) throws SigarException {
		config = fs;
		try {
			stat = sigar.getFileSystemUsage(fs.getDirName());
		} catch (SigarException e) {

		}
	}

	public static List<DISKGather> gather(Sigar sigar) throws SigarException {
		FileSystem[] fsArr = sigar.getFileSystemList();
		List<DISKGather> fsList = new ArrayList<DISKGather>();
		for (FileSystem fs : fsArr) {
			if (fs.getType() == FileSystem.TYPE_LOCAL_DISK) {
				DISKGather fsData = DISKGather.gather(sigar, fs);
				fsList.add(fsData);
			}
		}

		return fsList;
	}

	public static DISKGather gather(Sigar sigar, FileSystem fs) throws SigarException {
		DISKGather data = new DISKGather();
		data.populate(sigar, fs);
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
