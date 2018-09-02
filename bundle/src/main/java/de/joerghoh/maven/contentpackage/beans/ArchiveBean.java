package de.joerghoh.maven.contentpackage.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jackrabbit.vault.fs.config.MetaInf;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.maven.contentpackage.rules.ArchiveFilters;

public abstract class ArchiveBean {
	
	private static Logger LOG = LoggerFactory.getLogger(ArchiveBean.class);

	private Archive archive;
	private ArchiveEntry rootEntry;
	
	protected boolean isClosed; // must be accessible from inheriting classes
	
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
	
	public abstract ArchiveBean getParentArchive();
	
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
 	
	public List<ArchiveBean> getSubpackages() throws IOException {
		Stream<ArchiveEntry> potentialPackages = this.getRoot().getNode("jcr_root/etc/packages")
				.flatMap(n -> Optional.of(n.getStream()))
				.orElseGet(() -> Stream.of());
		return potentialPackages
			.filter(ArchiveFilters.isContentPackage)
			.map ((ArchiveEntry c) -> {
				try {
					return new EmbeddedArchiveBean(c);
				} catch (IOException e) {
					LOG.debug("{} is not a contentpackage", c.getAbsolutePath());
					return null;
				}
			})
			.filter(a -> a != null)
			.collect(Collectors.toList());		
	}
	
	public void close() throws IOException {
		for (ArchiveBean a : getSubpackages()) {
			a.close();
		}
		if (!isClosed) {
			archive.close();
			isClosed = true;
		}
	}
	
	public abstract String getName();
	
	
}
