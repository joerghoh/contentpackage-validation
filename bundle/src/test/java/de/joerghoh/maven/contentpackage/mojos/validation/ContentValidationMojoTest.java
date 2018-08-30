package de.joerghoh.maven.contentpackage.mojos.validation;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.joerghoh.maven.contentpackage.beans.ZipArchiveBean;
import de.joerghoh.maven.contentpackage.mojos.validation.ContentValidationMojo.Violation;

public class ContentValidationMojoTest extends AbstractMojoTestCase {

	@Before
	protected void setup() throws Exception {
		super.setUp();
	}
	
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	@Test
	public void testValidation() throws Exception {
        File pom = getTestFile( "src/test/resources/validation-pom.xml");
        assertNotNull( pom );
        assertTrue( "Haven't found the validation-pom.xml test file",pom.exists() );

        ContentValidationMojo mojo = (ContentValidationMojo) lookupMojo( "validate", pom );
        mojo.target = new File("src/test/resources/ZipArchiveBeanTest.zip");
        mojo.processRules();
        
        ZipArchiveBean archive = new ZipArchiveBean(mojo.target);
		List<Violation> exceptions = mojo.validateArchive(archive);
		assertEquals(0,exceptions.size());        
	}
	
	@Test
	public void testValidationWithFailures() throws Exception {
        File pom = getTestFile( "src/test/resources/validation-pom.xml");
        assertNotNull( pom );
        assertTrue( "Haven't found the validation-pom.xml test file",pom.exists() );

        ContentValidationMojo mojo = (ContentValidationMojo) lookupMojo( "validate", pom );
        mojo.target = new File("src/test/resources/ZipArchiveBeanTest.zip");
        mojo.whitelistedPaths.add("!/jcr_root/etc/dam/video/.*");
        mojo.processRules();
        
        ZipArchiveBean archive = new ZipArchiveBean(mojo.target);
		List<Violation> exceptions = mojo.validateArchive(archive);
		assertEquals(7,exceptions.size());     
	}
	
}
