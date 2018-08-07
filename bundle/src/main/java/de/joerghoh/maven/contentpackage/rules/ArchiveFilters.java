package de.joerghoh.maven.contentpackage.rules;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;

public class ArchiveFilters {
	
	private static Pattern installFolder = Pattern.compile("jcr_root/(libs|apps)/.*/install.*/[^/]+.jar");
	private static Pattern configFolder = Pattern.compile("jcr_root/(libs|apps)/.+/conf.*/[^/]+");
	
	private static Pattern contentpackagePattern = Pattern.compile("jcr_root/etc/packages/.*.zip");
	
	
	public static Predicate<ArchiveEntry> isBundle = ae ->  installFolder.matcher(ae.getAbsolutePath()).find();
	
	public static Predicate<ArchiveEntry> isContentPackage = ae -> contentpackagePattern.matcher(ae.getAbsolutePath()).find();

	public static Predicate<ArchiveEntry> isJcrPath = ae -> ae.getAbsolutePath().startsWith("jcr_root/");
	
	
	public BiPredicate<ArchiveEntry,String> jcrPath = (ae,path) -> {
		Pattern p = Pattern.compile(path);
		return ae.getAbsolutePath().startsWith("jcr_content/") && p.matcher(ae.getAbsolutePath()).find();	
	};
	
	
	

}
