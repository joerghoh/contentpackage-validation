package de.joerghoh.maven.contentpackage.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;
import de.joerghoh.maven.contentpackage.beans.ZipArchiveBean;

@RunWith(MockitoJUnitRunner.class)
public class ZipArchiveBeanTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZipArchiveBeanTest.class);
	
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
	public void testSimpleZip() throws IOException {

		List<ArchiveEntry> content = zipArchive.getEntriesFlat();
		assertTrue(content != null);
		assertTrue(content.size() > 5);
	
	}
	
	@Test
	public void simpleIteration() throws IOException {
		ArchiveEntry root = zipArchive.getRoot();
		assertNotNull (root);
		assertNull (root.getParent());
		assertTrue(root.getAbsolutePath().equals("/"));
		ArchiveEntry subnode1 = root.getChild("META-INF").get();
		assertNotNull (subnode1);
		assertTrue (subnode1.getParent().equals(root));
		assertTrue(subnode1.isDirectory());
	}

	

	
	@Test
	public void testIterationAndList() throws IOException {
		List<ArchiveEntry> content = zipArchive.getEntriesFlat();
		ArchiveEntry root = zipArchive.getRoot();
		assertTrue("Root node not part of the archive!",content.contains(root));
		assertTrue(root.isRootEntry());
		ArchiveEntry subnode1 = root.getChild("META-INF").get();
		assertNotNull (subnode1);
		assertTrue(subnode1.getName().equals("META-INF"));
		assertFalse(subnode1.isRootEntry());
		assertTrue (subnode1.getParent().equals(root));
		assertTrue(content.contains(subnode1));
	}
	
	@Test
	public void deepIterationTest() throws IOException {
		ArchiveEntry root = zipArchive.getRoot();
		Optional<ArchiveEntry> config = root.getChild("META-INF")
			.flatMap(c -> c.getChild("vault"))
			.flatMap(c -> c.getChild("config.xml"));
		assertTrue(config.isPresent());
		
		Optional<ArchiveEntry> nonExisting = root.getChild("non-existing")
				.flatMap(c -> c.getChild("vault"))
				.flatMap(c -> c.getChild("config.xml"));
		assertFalse(nonExisting.isPresent());
	}
	
	@Test
	public void testNamesAndPaths() throws IOException {
		ArchiveEntry root = zipArchive.getRoot();
		Optional<ArchiveEntry> config = root.getNode("META-INF","vault","properties.xml");
		Optional<ArchiveEntry> config1 = root.getNode("META-INF/vault/properties.xml");
		assertTrue(config.equals( config1));
		assertTrue(config.isPresent());
		String path = config.get().getAbsolutePath();
		assertTrue("META-INF/vault/properties.xml".equals(path));
	}
	
	@Test
	public void readContent() throws IOException {
		ArchiveEntry root = zipArchive.getRoot();
		Optional<ArchiveEntry> config = root.getNode("META-INF","vault","properties.xml");
		assertTrue(config.isPresent());
		String fileContent = config.get().getContent();
		Pattern regex = Pattern.compile(".*<comment>Testcontent</comment>.*",Pattern.DOTALL);
		assertTrue(regex.matcher(fileContent).matches());
		
	}
	
	
	
	
}
