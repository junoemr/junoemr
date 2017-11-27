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
		String sanitizedFileName = sanitizeFileName(fileName);

		File directory = new File(GenericFile.DOCUMENT_NEW);
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
	 * load an existing document with the given name
	 * @param fileName - name of the file to load
	 * @return - the file, or null if no file exists with the given filename
	 */
	public static GenericFile getExistingDocumentFile(String fileName)
	{
		File file = new File(GenericFile.DOCUMENT_BASE, fileName);
		GenericFile genFile = null;

		if(file.exists() && file.isFile())
		{
			genFile = new GenericFile(file);
		}
		return genFile;
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
}
