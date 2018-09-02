/*
 * 
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.joerghoh.maven.contentpackage.mojos.validation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.joerghoh.maven.contentpackage.beans.ArchiveBean;
import de.joerghoh.maven.contentpackage.beans.ArchiveEntry;
import de.joerghoh.maven.contentpackage.beans.ZipArchiveBean;

@Mojo (name="validate", defaultPhase=LifecyclePhase.VERIFY, requiresProject=false )
public class ContentValidationMojo extends AbstractMojo {
	
	private static final String SUBPACKAGE_EXPRESSION = "/jcr_root/etc/packages/.*.zip";
	
	@Parameter (property="validation.whitelistedPaths")
	ArrayList<String> whitelistedPaths;
	
	@Parameter (property="validation.filename", defaultValue="${project.build.directory}/${project.build.finalName}")
	protected File target;
	
	@Parameter(property="validation.breakBuildOnValiationFailures", defaultValue="false")
	private boolean breakBuild;
	
	@Parameter(property="validation.allowSubpackages", defaultValue="true")
	private boolean allowSubpackages;
	
	List<String> positiveStatements = new ArrayList<>();
	List<String> negativeStatements = new ArrayList<>();

	private static final String TARGET_EXTENSION = ".zip";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		if (!target.getName().endsWith(TARGET_EXTENSION)) {
			target = new File (target.getAbsolutePath() + TARGET_EXTENSION);
		}
		if (!target.exists()) {
			getLog().error(String.format("File %s does not exist", target.getAbsolutePath()));
			return;
		}
		processRules();

		try {
			ArchiveBean archive = new ZipArchiveBean (target);
			reportViolations(validateArchive(archive));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
		}
		
	}
	
	protected void processRules() {
		whitelistedPaths.forEach(regex -> {
			if (regex.startsWith("!")) {
				negativeStatements.add(regex.substring(1));
			} else {
				positiveStatements.add(regex);
				
			}
		});
	}
	
	public List<Violation> validateArchive (ArchiveBean archive) throws IOException {
		List<Violation> policyViolations = new ArrayList<>();
		archive.getRoot().getStream()
			.filter(e -> filterPathViolations(e,policyViolations))
			.filter(e -> checkForSubpackages(e, policyViolations))
			.collect(Collectors.toList());
		
		archive.getSubpackages().forEach (s -> {
			try {
				policyViolations.addAll(validateArchive(s));
			} catch (IOException e1) {
				getLog().error(e1);
			}
		});
		
		return policyViolations;
	}
	
	
	/**
	 * validate a content package entry against the path filter rules
	 * @param entry
	 * @param policyViolations the list of violations; in case
	 * @return false if the violation is deteced, true otherwise
	 */
	boolean filterPathViolations (ArchiveEntry entry, List<Violation> policyViolations) {
			
		// there must be at least 1 match on the positiveStatement list
		boolean positiveMatch = positiveStatements.stream()
				.filter((String regex) -> entry.getAbsolutePath().matches(regex))
				.findFirst().isPresent();
		
		// but no match on the negativeStatement list
		boolean negativeMatch = negativeStatements.stream()
				.filter((String regex) -> entry.getAbsolutePath().matches(regex))
				.findFirst().isPresent();
		
		getLog().debug(String.format("%s: positiveMatch=%s, negativeMatch=%s",entry.getAbsolutePath(),positiveMatch, negativeMatch));
		boolean isCompliant = (positiveMatch && !negativeMatch);
		if (! isCompliant) {
			String msg = String.format("detected violation of path rules: %s", entry.getAbsolutePath());
			getLog().debug(msg);
			policyViolations.add(new Violation(entry, msg));
		}
		return isCompliant;
	}
	
	boolean checkForSubpackages (ArchiveEntry archiveEntry, List<Violation> policyViolations) {
		boolean isSubPackage = archiveEntry.getAbsolutePath().matches(SUBPACKAGE_EXPRESSION);
		if (isSubPackage && !allowSubpackages) {
			String msg = String.format("detected subpackage at: %s", archiveEntry.getAbsolutePath());
			policyViolations.add(new Violation(archiveEntry, msg));
		}
		return !isSubPackage;
	}
	
	
	
	
	/////////////////////////////
	
	/**
	 * report policy violations
	 * @param policyViolations
	 * @throws MojoExecutionException
	 */
	protected void reportViolations(List<Violation> policyViolations) throws MojoExecutionException {
		
		if (policyViolations.size() > 0) {
			String msg = String.format("violation(s) against policy (%s)", getPolicyString()); 
			getLog().warn(msg);
			long violationCount = policyViolations.stream()
				.filter(v -> !v.getEntry().isDirectory())
				.filter(v -> {
					String message = String.format("[%s] %s", v.getEntry().getArchive().getName(),v.getMessage());
					getLog().warn(message);
					return true;
				})
				.count();
			if (breakBuild) {
				throw new MojoExecutionException(String.format(" %d policy violation(s) detected, "
						+ "please check build logs",violationCount));
			}
		}
	}
	

	String getPolicyString() {
		return String.format("whitelisted paths = [%s], allowSubpackages = %s",String.join(",", whitelistedPaths),allowSubpackages );
	}
	
	
	
	protected class Violation {
		
		private ArchiveEntry entry;
		private String message;
		
		public Violation (ArchiveEntry e, String msg ) {
			this.entry = e;
			this.message = msg;
		}
		
		ArchiveEntry getEntry () {
			return entry;
		}
		
		String getMessage () {
			return message;
		}
		
 	}

	
	
}
