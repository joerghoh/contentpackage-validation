package de.joerghoh.maven.contentpackage.rules;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;
import de.joerghoh.maven.contentpackage.beans.BeanTest;
import de.joerghoh.maven.contentpackage.beans.ZipArchiveBean;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveFiltersTest {
	
	ZipArchiveBean zipArchive;
	ArchiveEntry root;
	
	@Before
	public void setup() throws IOException {
		File f = new File (BeanTest.class.getClassLoader().getResource("ZipArchiveBeanTest.zip").getFile());
		assertTrue(f.exists());
		zipArchive = new ZipArchiveBean(f);
		assertTrue(zipArchive != null);
		root = zipArchive.getRoot();
	}
	
	@After
	public void tearDown() {
		zipArchive.close();
	}
	
	@Test
	public void testFilters() throws IOException {
		List<ArchiveEntry> jcrNodes = root.getSubnodes()
			.filter(ArchiveFilters.isJcrPath)
			.collect(Collectors.toList());
		Optional<ArchiveEntry> appsFolder = root.getNode("jcr_root/apps/");
		assertTrue(appsFolder.isPresent());
		assertTrue(jcrNodes.contains(appsFolder.get()));
	}
	
	@Test
	public void testBundleFilter() {
		assertTrue(
			root.getSubnodes()
			.filter(ArchiveFilters.isBundle)
			.findFirst()
			.get()
			.getAbsolutePath()
			.equals("/jcr_root/apps/myapp/install/mycustombundle.jar")
		);
	}
	
	
}
