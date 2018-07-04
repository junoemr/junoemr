/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.common.io;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileFactory
{
	protected static final Logger logger = MiscUtils.getLogger();

	/**
	 * save and load a new document with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createDocumentFile(InputStream fileInputStream, String fileName) throws IOException
	{
		return createNewFile(fileInputStream, fileName, GenericFile.DOCUMENT_BASE_DIR);
	}

	/**
	 * Write the input stream to a tempfile
	 * @param fileInputStream - input stream of the new file
	 * @return the file
	 * @throws IOException
	 */
	public static GenericFile createTempFile(InputStream fileInputStream) throws IOException
	{
		File file = File.createTempFile("juno", "tempfile");
		logger.info("Created tempfile: " + file.getPath());
		return writeInputStream(fileInputStream, file, true);
	}

	/**
	 * load an existing document with the given name
	 * @param fileName - name of the file to load
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getDocumentFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.DOCUMENT_BASE_DIR, fileName);
	}

	/**
	 * Overwrite an existing file with new stream content
	 * @param oldFile - generic file to overwrite
	 * @param fileInputStream - stream to save over existing file
	 * @return the new GenericFile
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile overwriteFileContents(GenericFile oldFile, InputStream fileInputStream) throws IOException
	{
		File file = oldFile.getFileObject();

		if(!file.exists() || !file.isFile())
		{
			throw new IOException("Attempt to overwrite an invalid File: " + file.getPath());
		}
		logger.info("Overwriting file contents: " + file.getPath());
		return writeInputStream(fileInputStream, file, true);
	}

	/**
	 * save and load a new file with the given name, folder, and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file, or null if no file exists with the given filename
	 */
	private static GenericFile createNewFile(InputStream fileInputStream, String fileName, String folder) throws IOException
	{
		String formattedFileName = GenericFile.getFormattedFileName(fileName);

		File directory = new File(folder);
		if(!directory.exists())
		{
			boolean mkdir = directory.mkdirs();
			if(!mkdir)
			{
				throw new IOException("Failed to create Directory: " + directory.getPath());
			}
		}

		File file = new File(directory.getPath(), formattedFileName);
		return writeInputStream(fileInputStream, file, false);
	}
	private static GenericFile writeInputStream(InputStream fileInputStream, File file, boolean allowOverwrite) throws IOException
	{
		// copy the stream to the file
		if(allowOverwrite)
		{
			Files.copy(fileInputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		else
		{
			Files.copy(fileInputStream, file.toPath());
		}
		// close the stream
		IOUtils.closeQuietly(fileInputStream);

		GenericFile returnFile = getExistingFile(file);

		// for now, only warn with unknown file content types.
		String contentType = returnFile.getContentType();
		if(!GenericFile.getAllowedContent().contains(contentType))
		{
			//TODO - how to handle unknown content type. must have complete list of allowed types first
			logger.warn("Unknown file content type: " + contentType);
		}
		return returnFile;
	}

	/**
	 * load an existing file with the given name and folder location
	 * @param fileName - name of the file to load
	 * @param folder - directory of the file to load
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getExistingFile(String folder, String fileName) throws IOException
	{
		return getExistingFile(new File(folder, fileName));
	}

	public static GenericFile getExistingFile(String fileName) throws IOException
	{
		return getExistingFile(new File(fileName));
	}

	/**
	 * load an existing file with the given name and folder location
	 * @param file - the file to load
	 * @return - the file as a GenericFile
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	private static GenericFile getExistingFile(File file) throws IOException
	{
		logger.info("Load File: " + file.getPath());

		GenericFile genFile;
		if(file.exists() && file.isFile())
		{
			String fileContent = GenericFile.getContentType(file);
			logger.info("FileContent: " + fileContent);
			if("application/pdf".equals(fileContent))
			{
				genFile = new PDFFile(file);
			}
			else if("application/xml".equals(fileContent))
			{
				genFile = new XMLFile(file);
			}
			else
			{
				genFile = new GenericFile(file);
			}
		}
		else
		{
			throw new FileNotFoundException("No Valid File Exists: " + file.getPath());
		}
		return genFile;
	}
}
