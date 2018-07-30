/*
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

import org.apache.jackrabbit.vault.fs.io.Archive;

public class ContentPackageEntry {
	
	private String path;
	private Archive.Entry entry;
	private Archive archive;
	private String archiveFilename;
	
	/**
	 * 
	 * @param path the path inside the content package
	 * @param entry -- the entry
	 * @param archive -- the archive to which is entry is taken
	 * @param archiveFilename -- the path the archive (if nested)
	 */
	
	public ContentPackageEntry (String path,Archive.Entry entry, Archive archive, String archiveFilename) {
		this.path = path;
		this.entry = entry;
		this.archive = archive;
		this.archiveFilename = archiveFilename;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public Archive.Entry getEntry() {
		return entry;
	}

	public Archive getArchive() {
		return archive;
	}
	
	public String getArchiveFilename() {
		return archiveFilename;
	}
	
	public String toString() {
		return String.format("%s:%s", archiveFilename,path);
	}
}
