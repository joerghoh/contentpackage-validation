package de.joerghoh.maven.contentpackage.beans;

import java.io.File;
import java.io.IOException;

import org.apache.jackrabbit.vault.fs.io.ZipArchive;

public class ZipArchiveBean extends ArchiveBean {
	
	ZipArchive zipArchive;
	

	public ZipArchiveBean (File archiveFile) throws IOException {
		super();
		zipArchive = new ZipArchive (archiveFile);
		zipArchive.open(true);
		setArchive(zipArchive);
	}
	
	
	public void close() {
		zipArchive.close();
	}
	
}
