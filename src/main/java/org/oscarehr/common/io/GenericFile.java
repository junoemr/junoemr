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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GenericFile
{
	protected static final Logger logger = MiscUtils.getLogger();
	protected static final OscarProperties props = oscar.OscarProperties.getInstance();

	public static final String BASE_DIRECTORY = props.getProperty("BASE_DOCUMENT_DIR");

	public static final String DOCUMENT_BASE = props.getProperty("DOCUMENT_DIR");
	public static final String DOCUMENT_NEW = new File(props.getProperty("DOCUMENT_DIR"), props.getProperty("DOCUMENT_NEW")).getPath();
	public static final String DOCUMENT_CORRUPT = new File(props.getProperty("DOCUMENT_DIR"), props.getProperty("DOCUMENT_CORRUPT")).getPath();

	// file info
	protected File javaFile;
	protected String fileType;
	protected String contentType;
	protected int pageCount;

	// validation info
	protected boolean hasBeenValidated;
	protected boolean isValid;
	protected String reasonInvalid;

	public GenericFile(File file)
	{
		this(file, null);
	}
	public GenericFile(File file, String fileType)
	{
		this.javaFile = file;
		this.fileType = fileType;
		this.pageCount = 0;
		this.contentType = null;

		this.hasBeenValidated = false;
		this.isValid = false;
		this.reasonInvalid = null;
	}

	public boolean moveToDocuments()
	{
		return moveFile(DOCUMENT_BASE);
	}
	public boolean moveToCorrupt()
	{
		return moveFile(DOCUMENT_CORRUPT);
	}
	public boolean moveToDemographicDocuments(Integer demographicNo)
	{
		return moveFile(new File(DOCUMENT_BASE, String.valueOf(demographicNo)));
	}

	public boolean moveFile(String directory)
	{
		File directoryFile = new File(directory);
		return moveFile(directoryFile);
	}

	public boolean moveFile(File directoryFile)
	{
		File destinationFile = new File(directoryFile.getPath(), javaFile.getName());

		boolean validDir = directoryFile.exists() && directoryFile.isDirectory();

		if(!directoryFile.exists())
		{
			validDir = directoryFile.mkdirs();
		}
		return validDir && javaFile.renameTo(destinationFile);
	}


	public boolean validate() throws IOException, InterruptedException
	{
		this.hasBeenValidated = true;
		this.isValid = true;
		return true;
	}
	public void onFailedValidation()
	{
		throw new RuntimeException("Validation Failure");
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
	 * get the base file object for backwards compatability
	 * @return java.io File
	 */
	public File getFileObject()
	{
		return this.javaFile;
	}
	public String getFileType()
	{
		return fileType;
	}
	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}
	public String getContentType()
	{
		return this.contentType;
	}
	public void setContentType(String type)
	{
		this.contentType = type;
	}
	public int getPageCount()
	{
		return pageCount;
	}



	/**
	 * write a file input stream to the targetFile
	 * @param fileInputStream - input stream of the file
	 * @throws IOException - if file cannot be written
	 */
	public void writeFileStream(InputStream fileInputStream) throws IOException
	{
		// copy the stream to the file
		Files.copy(fileInputStream, this.javaFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		// close the stream
		IOUtils.closeQuietly(fileInputStream);
	}
}
