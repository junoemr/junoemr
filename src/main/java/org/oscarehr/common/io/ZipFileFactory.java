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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileFactory
{
	protected static final Logger logger = MiscUtils.getLogger();

	public static ZIPFile packageZipFile(List<GenericFile> filesToZip) throws IOException
	{
		return packageZipFile(filesToZip, false);
	}

	/**
	 * zip a list of files. this will create a zip file with all files in the base location. no sub-directories
	 */
	public static ZIPFile packageZipFile(List<GenericFile> filesToZip, boolean deleteAfterZip) throws IOException
	{
		GenericFile tmpFile = FileFactory.createTempFile(".zip");
		ZIPFile zipFile = new ZIPFile(tmpFile.getFileObject());
		ZipOutputStream zipOutputStream = new ZipOutputStream(zipFile.asFileOutputStream());

		for(GenericFile file : filesToZip)
		{
			ZipEntry ze = new ZipEntry(file.getName());
			zipOutputStream.putNextEntry(ze);
			zipOutputStream.write(file.toByteArray());
			zipOutputStream.closeEntry();
		}
		zipOutputStream.close();

		if(deleteAfterZip)
		{
			for(GenericFile file : filesToZip)
			{
				file.deleteFile();
			}
		}
		return zipFile;
	}

	public static ZIPFile packageZipFile(Path directoryToZip) throws IOException
	{
		return packageZipFile(directoryToZip, false);
	}

	/**
	 * zip a directory and all it's contents. keep folder structure intact
	 */
	public static ZIPFile packageZipFile(Path directoryToZip, boolean deleteAfterZip) throws IOException
	{
		GenericFile tmpFile = FileFactory.createTempFile(".zip");
		ZIPFile zipFile = new ZIPFile(tmpFile.getFileObject());
		ZipOutputStream zipOutputStream = new ZipOutputStream(zipFile.asFileOutputStream());

		Files.walk(directoryToZip)
				.filter(path -> !Files.isDirectory(path))
				.forEach(path -> {
					ZipEntry zipEntry = new ZipEntry(directoryToZip.relativize(path).toString());
					try
					{
						zipOutputStream.putNextEntry(zipEntry);
						Files.copy(path, zipOutputStream);
						zipOutputStream.closeEntry();
					}
					catch(IOException e)
					{
						FileFactory.logger.error("Directory Zip Error", e);
					}
				});
		zipOutputStream.close();

		if(deleteAfterZip)
		{
			FileUtils.deleteDirectory(directoryToZip.toFile());
		}
		return zipFile;
	}
}
