package de.joerghoh.maven.contentpackage.rules;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;

public class ArchiveFilters {
	
	static Pattern installFolder = Pattern.compile("jcr_content/(libs|apps)/.+/install.*/[^/]+.jar");
	static Pattern configFolder = Pattern.compile("jcr_content/(libs|apps)/.+/conf.*/[^/]+");
	
	
	static Predicate<ArchiveEntry> isBundle = ae ->  installFolder.matcher(ae.getAbsolutePath()).find();

	static Predicate<ArchiveEntry> isJcrPath = ae -> ae.getAbsolutePath().startsWith("jcr_content/");
	
	
	BiPredicate<ArchiveEntry,String> jcrPath = (ae,path) -> {
		Pattern p = Pattern.compile(path);
		return ae.getAbsolutePath().startsWith("jcr_content/") && p.matcher(ae.getAbsolutePath()).find();	
	};
	
	

}
