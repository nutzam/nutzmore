package org.nutz.filepool;

import java.io.File;
import java.io.IOException;

import org.nutz.filepool.FilePool;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

public class UuidFilePool implements FilePool {
	
	protected String root;
	
	public File createFile(String suffix) {
		String id = R.UU32();
		String path = String.format("%s/%s/%s", root, id.substring(0, 2), id.substring(2));
		try {
			return Files.createFileIfNoExists(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void clear() {
		Files.clearDir(new File(root));
	}

	
	public UuidFilePool(String root) {
		this.root = root;
	}


	public long current() {
		throw Lang.noImplement();
	}

	
	public boolean hasFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	
	public File removeFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	public long getFileId(File f) {
		throw Lang.noImplement();
	}

	
	public File getFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	
	public File returnFile(long fId, String suffix) {
		throw Lang.noImplement();
	}

	
	public boolean hasDir(long fId) {
		throw Lang.noImplement();
	}

	
	public File removeDir(long fId) {
		throw Lang.noImplement();
	}

	
	public File createDir() {
		File r = new File(root);
		r.mkdirs();
		return r;
	}

	
	public File getDir(long fId) {
		throw Lang.noImplement();
	}

	
	public File returnDir(long fId) {
		throw Lang.noImplement();
	}


}
