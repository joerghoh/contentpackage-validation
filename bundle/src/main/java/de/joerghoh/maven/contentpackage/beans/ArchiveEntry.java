package de.joerghoh.maven.contentpackage.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.vault.fs.api.VaultInputSource;
import org.apache.jackrabbit.vault.fs.io.Archive;

public class ArchiveEntry {

	private Archive.Entry entry;
	private ArchiveBean parentArchive;
	private ArchiveEntry parentEntry;
	
	
	private List<ArchiveEntry> children;
	
	/**
	 * 
	 * @param archive
	 * @param entry
	 * @param parent the parent Entry; null if root entry
	 */
	protected ArchiveEntry(ArchiveBean archive, Archive.Entry entry, ArchiveEntry parent) {
		parentArchive = archive;
		this.entry = entry;
		parentEntry = parent;
	}
	
	protected Archive.Entry getInternalEntry() {
		return entry;
	}
	
	public boolean isRootEntry() {
		return parentEntry == null;
	}
	
	public ArchiveEntry getParent() {
		return parentEntry;
	}
	
	public List<ArchiveEntry> getChildren() {
		if (children == null) {
			children = entry.getChildren().stream()
				.map(child -> {return new ArchiveEntry(parentArchive,child, this);})
				.collect(Collectors.toList());	
		} 
		return children;
	}
	
	public Optional<ArchiveEntry> getChild (String name) {
		return getChildren().stream()
				.filter(child -> child.getName().equals(name))
				.findFirst();
	}
	
	/**
	 * Returns an ArchiveEntry for the specified absolute path in the archive
	 * @param path the individual segments of the paths
	 * @return
	 */
	public Optional<ArchiveEntry> getNode(String... path) {
		Optional<ArchiveEntry> a = Optional.of(this);
		for (String elem : path) {
			a = a.flatMap(c -> c.getChild(elem));
		}
		return a;
	}
	
	/**
	 * Returns an ArchiveEntry for the specified absolute path in the archive
	 * @param path a unix-like path description with "/" as path separator, without leading "/"
	 * @return
	 */
	public Optional<ArchiveEntry> getNode (String path) {
		return getNode(path.split("\\/"));
	}
	
	/**
	 * get the absolute path of this ArchiveEntry in the archive
	 * @return the absolute path as string including a leading slash ("/")
	 */
	public String getAbsolutePath() {
		List<String> pathElements = new ArrayList<>();
		ArchiveEntry elem = this;
		pathElements.add(elem.getName());
		while (elem.getParent() != null ) {
			elem = elem.getParent();
			pathElements.add(elem.getName());
		}
		Collections.reverse(pathElements);
		if (getParent() == null) {
			return "/";
		}
		return String.join("/", pathElements);
	}
	
	public boolean isDirectory() {
		return entry.isDirectory();
	}
	
	public String getName() {
		return entry.getName();
	}
	
	public long getSize() throws IOException {
		return parentArchive.getArchive().getInputSource(entry).getContentLength();
	}
	
	public String getContent() throws IOException {
		VaultInputSource is = parentArchive.getArchive().getInputSource(entry);
		String encoding = is.getEncoding();
		return IOUtils.toString(is.getByteStream(),encoding);
	}
	
	/**
	 * get the content of the node as stream; the caller is responsible to close it
	 * @return the stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return parentArchive.getArchive().getInputSource(entry).getByteStream();
	}
	
	/**
	 * Return the node and its child nodes recursively in a stream
	 * @return
	 */
	public Stream<ArchiveEntry> getStream() {
		List<ArchiveEntry> result = new ArrayList<>();
		result.add(this);
		result.addAll(recurse(this));
		return Stream.of(result.toArray(new ArchiveEntry[result.size()]));
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
	
	public ArchiveBean getArchive() {
		return parentArchive;
	}
	
	public String toString() {
		return "[" + parentArchive.toString() + "]" + getAbsolutePath();
	}
	
}
