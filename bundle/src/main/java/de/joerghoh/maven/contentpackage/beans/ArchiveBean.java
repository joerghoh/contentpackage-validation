package de.joerghoh.maven.contentpackage.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.jackrabbit.vault.fs.config.MetaInf;
import org.apache.jackrabbit.vault.fs.io.Archive;

public class ArchiveBean {

	private Archive archive;
	private ArchiveBean parent; // the parent Archive
	private ArchiveEntry rootEntry;
	
	protected ArchiveBean() {
		// private constructor
	}
	
	protected Archive getArchive() {
		return archive;
	}
	
	protected void setArchive (Archive archive) {
		this.archive = archive;
	}
	

	private List<ArchiveEntry> recurse (ArchiveEntry e) {
		List<ArchiveEntry> result = new ArrayList<>();
		Collection<ArchiveEntry> col = e.getChildren();
		col.forEach(entry -> {
			result.add(entry);
			result.addAll(recurse(entry));
			
		});
		return result;
	}
	
	
	// public
	
	public ArchiveBean getParentArchive() {
		return parent;
	}
	
	public ArchiveEntry getRoot() throws IOException {
		if (rootEntry == null) {
			rootEntry = new ArchiveEntry (this,archive.getRoot(),null);
		}
		return rootEntry;
	}
	
	public List<ArchiveEntry> getEntriesFlat() throws IOException {
		List<ArchiveEntry> result = new ArrayList<>();
		result.add(getRoot());
		result.addAll(recurse(getRoot()));
		return result;
	}
	
	public MetaInf getMetaInf () {
		return archive.getMetaInf();
	}
 	
//	public List<ArchiveBean> getSubpackages() {
//		return archive.getSubArchive(arg0, arg1)
//	}
	
	
	
	
}
