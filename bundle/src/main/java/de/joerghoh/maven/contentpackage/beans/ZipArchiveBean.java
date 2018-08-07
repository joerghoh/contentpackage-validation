package de.joerghoh.maven.contentpackage.beans;

import java.io.File;
import java.io.IOException;

import org.apache.jackrabbit.vault.fs.io.ZipArchive;

public class ZipArchiveBean extends ArchiveBean {
	
	ZipArchive zipArchive;
	String name;
	

	public ZipArchiveBean (File archiveFile) throws IOException {
		super();
		zipArchive = new ZipArchive (archiveFile);
		zipArchive.open(true);
		name = archiveFile.getName();
		setArchive(zipArchive);
		isClosed = false;
	}
	
	
	public void close() {
		zipArchive.close();
	}
	
	public ArchiveBean getParentArchive () {
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
	
	
}
