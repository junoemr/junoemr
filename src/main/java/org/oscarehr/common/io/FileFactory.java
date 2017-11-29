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

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileFactory
{
	protected static final Logger logger = MiscUtils.getLogger();

	/**
	 * save and load a new document with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getNewDocumentFile(InputStream fileInputStream, String fileName) throws IOException
	{
		return getNewFile(fileInputStream, fileName, GenericFile.DOCUMENT_BASE);
	}

	/**
	 * load an existing document with the given name
	 * @param fileName - name of the file to load
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getExistingDocumentFile(String fileName)
	{
		return getExistingFile(fileName, GenericFile.DOCUMENT_BASE);
	}

	/**
	 * load an existing document with the given name and demographic. when the demographic number is present,
	 * this method checks for a demographic subdirectory. otherwise returns results from the base directory.
	 * @param fileName - name of the file to load
	 * @param demographicNo - the demographic that the document is linked to.
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getExistingDocumentFile(String fileName, String demographicNo)
	{
		File demoDir = new File(GenericFile.DOCUMENT_BASE, demographicNo);
		GenericFile returnFile = null;

		if(demoDir.exists() && demoDir.isDirectory())
		{
			returnFile = getExistingFile(fileName, demoDir.getPath());
		}
		if(returnFile == null)
		{
			returnFile = getExistingFile(fileName, GenericFile.DOCUMENT_BASE);
		}
		return returnFile;
	}


	/**
	 * sanitizes the incoming file name string to a friendly format
	 * @param originalName - name to be sanitized
	 * @return - the reformatted name string
	 */
	public static String sanitizeFileName(String originalName)
	{
		String sanitized = originalName.trim();
		sanitized = sanitized.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

		sanitized = System.currentTimeMillis() + "-" + sanitized;
		return sanitized;
	}

	/**
	 * save and load a new file with the given name, folder, and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file, or null if no file exists with the given filename
	 */
	private static GenericFile getNewFile(InputStream fileInputStream, String fileName, String folder) throws IOException
	{
		String sanitizedFileName = sanitizeFileName(fileName);

		File directory = new File(folder);
		if(!directory.exists())
		{
			boolean mkdir = directory.mkdirs();
			if(!mkdir)
			{
				throw new IOException("Failed to create Directory: " + directory.getPath());
			}
		}

		File file = new File(directory.getPath(), sanitizedFileName);
		String fileContent = GenericFile.getContentType(file);
		logger.info("FileContent: " + fileContent);

		GenericFile genFile;
		if("application/pdf".equals(fileContent))
		{
			genFile = new PDFFile(file);
		}
		else
		{
			genFile = new GenericFile(file);
		}
		genFile.writeFileStream(fileInputStream);

		return genFile;
	}
	/**
	 * load an existing file with the given name and folder location
	 * @param fileName - name of the file to load
	 * @return - the file, or null if no file exists with the given filename
	 */
	private static GenericFile getExistingFile(String fileName, String folder)
	{
		File file = new File(folder, fileName);
		GenericFile genFile = null;

		if(file.exists() && file.isFile())
		{
			genFile = new GenericFile(file);
		}
		return genFile;
	}
}
