package de.joerghoh.maven.contentpackage.beans;

import java.io.IOException;

import org.apache.jackrabbit.vault.fs.io.ZipStreamArchive;

public class EmbeddedArchiveBean extends ArchiveBean {
	
	
	ZipStreamArchive zsa;
	ArchiveBean parent;
	String name;
	
	
	public EmbeddedArchiveBean (ArchiveEntry packageNode) throws IOException {
		super();
		zsa = new ZipStreamArchive(packageNode.getInputStream());
		zsa.open(true);
		parent = packageNode.getArchive();
		name = packageNode.getName();
		setArchive(zsa);
		isClosed = false;
		
	}
	
	public void close() {
		zsa.close();
	}
	
	public ArchiveBean getParentArchive() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return parent.toString() + " > " + name;
	}
	

}
