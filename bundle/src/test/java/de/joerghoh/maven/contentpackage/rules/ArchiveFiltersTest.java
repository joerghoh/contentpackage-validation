package de.joerghoh.maven.contentpackage.rules;

import static org.junit.Assert.assertFalse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;
import de.joerghoh.maven.contentpackage.beans.ZipArchiveBean;
import de.joerghoh.maven.contentpackage.beans.ZipArchiveBeanTest;

@RunWith(MockitoJUnitRunner.class)
public class ArchiveFiltersTest {

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveFiltersTest.class);
	
	ZipArchiveBean zipArchive;
	
	@Before
	public void setup() throws IOException {
		File f = new File (ZipArchiveBeanTest.class.getClassLoader().getResource("ZipArchiveBeanTest.zip").getFile());
		assertTrue(f.exists());
		zipArchive = new ZipArchiveBean(f);
		assertTrue(zipArchive != null);
	}
	
	@After
	public void tearDown() {
		zipArchive.close();
	}
	
	@Test
	public void testFilters() throws IOException {
		ArchiveEntry root = zipArchive.getRoot();
		List<ArchiveEntry> jcrNodes = root.getStream()
			.filter(ArchiveFilters.isJcrPath)
			.collect(Collectors.toList());
		Optional<ArchiveEntry> appsFolder = root.getNode("/jcr_root/apps");
		assertTrue(jcrNodes.contains(appsFolder.get()));
		
		
	}
	
}
