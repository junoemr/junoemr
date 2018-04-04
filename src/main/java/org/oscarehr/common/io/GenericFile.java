/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.common.io;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class GenericFile
{
	protected static final Logger logger = MiscUtils.getLogger();
	protected static final OscarProperties props = oscar.OscarProperties.getInstance();

	private static final Set<String> ALLOWED_CONTENT_TYPE = Sets.newHashSet(
			"application/pdf",
			"application/image",
			"application/doc",
			"text/plain");

	public static final String BASE_DIRECTORY = props.getProperty("BASE_DOCUMENT_DIR");

	public static final String DOCUMENT_BASE_DIR = props.getProperty("DOCUMENT_DIR");
	public static final String DOCUMENT_NEW_DIR = new File(DOCUMENT_BASE_DIR, props.getProperty("DOCUMENT_NEW_DIR")).getPath();
	public static final String DOCUMENT_CORRUPT_DIR = new File(DOCUMENT_BASE_DIR, props.getProperty("DOCUMENT_CORRUPT_DIR")).getPath();

	// file info
	protected File javaFile;

	// validation info
	protected boolean hasBeenValidated;
	protected boolean isValid;
	protected String reasonInvalid;

	public GenericFile(File file)
	{
		this.javaFile = file;

		this.hasBeenValidated = false;
		this.isValid = false;
		this.reasonInvalid = null;
	}

	public boolean moveToDocuments() throws IOException
	{
		return moveFile(DOCUMENT_BASE_DIR);
	}

	public boolean moveToCorrupt() throws IOException
	{
		return moveFile(DOCUMENT_CORRUPT_DIR);
	}

	public boolean moveFile(String directory) throws IOException
	{
		File directoryFile = new File(directory);
		return moveFile(directoryFile);
	}

	public boolean moveFile(File directoryFile) throws IOException
	{
		if(!directoryFile.exists())
		{
			boolean mkdir = directoryFile.mkdirs();
			if(!mkdir)
			{
				throw new IOException("Failed to create Directory: " + directoryFile.getPath());
			}
		}

		File destinationFile = new File(directoryFile.getPath(), javaFile.getName());
		logger.info("Moving file to: " + destinationFile.getPath());

		if(directoryFile.exists() && directoryFile.isDirectory())
		{
			Files.move(javaFile.toPath(), destinationFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
			javaFile = destinationFile;
			return true;
		}
		throw new IOException("Invalid Directory: " + directoryFile.getPath());
	}

	public void rename(String newName) throws IOException
	{
		File directoryFile = javaFile.getParentFile();
		if(directoryFile.exists() && directoryFile.isDirectory())
		{
			File destinationFile = new File(directoryFile, newName);
			Files.move(javaFile.toPath(), destinationFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
			javaFile = destinationFile;
		}
		else
		{
			throw new IOException("Invalid Directory: " + directoryFile.getPath());
		}
	}

	/**
	 * replace the contents of the file with those of the new file.
	 * This performs a move operation
	 * @param replacementFile - the replacement file.
	 * @throws IOException
	 */
	public void replaceWith(GenericFile replacementFile) throws IOException
	{
		File incomingFile = replacementFile.getFileObject();
		if(incomingFile.exists() && incomingFile.isFile())
		{
			Files.move(incomingFile.toPath(), javaFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		}
		else
		{
			throw new IllegalArgumentException("Invalid File: " + incomingFile.toPath());
		}

		// copy the incoming file properties
		this.hasBeenValidated = replacementFile.hasBeenValidated();
		this.isValid = replacementFile.isValid();
		this.reasonInvalid = replacementFile.getReasonInvalid();
	}

	public boolean validate() throws IOException, InterruptedException
	{
		this.hasBeenValidated = true;
		this.isValid = true;
		return true;
	}
	public void process() throws IOException, InterruptedException
	{
	}

	public boolean isValid()
	{
		return this.isValid;
	}
	public boolean hasBeenValidated()
	{
		return this.hasBeenValidated;
	}
	public String getReasonInvalid()
	{
		return this.reasonInvalid;
	}

	/**
	 * get the base file object for backwards compatibility
	 * @return java.io File
	 */
	public File getFileObject()
	{
		return this.javaFile;
	}
	public String getContentType() throws IOException
	{
		return GenericFile.getContentType(javaFile);
	}
	public int getPageCount() throws IOException
	{
		return 0;
	}
	public String getName()
	{
		return javaFile.getName();
	}
	/**
	 * returns the file content type, or null if it cannot be determined
	 * @param f - the file to read
	 * @return - content type string or null
	 * @throws IOException - if an IO error occurs
	 */
	public static String getContentType(File f) throws IOException
	{
		return Files.probeContentType(f.toPath());
	}
	public static Set<String> getAllowedContent()
	{
		return ALLOWED_CONTENT_TYPE;
	}

	/**
	 * sanitizes the incoming file name string to a friendly format
	 * @param originalName - name to be sanitized
	 * @return - the reformatted name string
	 */
	public static String getSanitizedFileName(String originalName)
	{
		return StringUtils.trimToEmpty(originalName).replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}
	public static String getFormattedFileName(String originalName)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime now = LocalDateTime.now();

		return formatter.format(now) + getSanitizedFileName(originalName);
	}
}
