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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	public static GenericFile createDocumentFile(InputStream fileInputStream, String fileName) throws IOException, InterruptedException
	{
		return createNewFormattedFile(fileInputStream, fileName, GenericFile.DOCUMENT_BASE_DIR, true);
	}

	/**
	 * save and load a new eform image with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createEformImage(InputStream fileInputStream, String fileName) throws IOException, InterruptedException
	{
		if(!isFileInDirectory(fileName, GenericFile.EFORM_IMAGE_DIR))
		{
			throw new IOException("SECURITY WARNING: Illegal file path detected, client attempted to navigate away from the eform images directory");
		}
		GenericFile file = createNewFormattedFile(fileInputStream, fileName, GenericFile.EFORM_IMAGE_DIR, true);
		file.restrictContentType = false;
		return file;
	}

	/**
	 * save and load a new document with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws InterruptedException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createEmbeddedLabFile(InputStream fileInputStream, String fileName) throws IOException, InterruptedException
	{
		return createNewFormattedFile(fileInputStream, fileName, GenericFile.DOCUMENT_BASE_DIR, false);
	}

	/**
	 * save and load a new document with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createOutboundFaxFile(InputStream fileInputStream, String fileName) throws IOException, InterruptedException
	{
		return createNewFormattedFile(fileInputStream, fileName, GenericFile.OUTBOUND_FAX_DIR_PENDING, true);
	}

	/**
	 * save and load a new log with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createImportLogFile(InputStream fileInputStream, String fileName, String identifier) throws IOException, InterruptedException
	{
		return createNewFormattedFile(fileInputStream, fileName, Paths.get(GenericFile.LOG_IMPORT_DIR, identifier).toString(), false);
	}

	/**
	 * save and load a new log with the given name and input stream
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the file to be saved and opened
	 * @return - the file
	 * @throws FileNotFoundException - if the given file is invalid for use as a GenericFile
	 */
	public static GenericFile createExportLogFile(InputStream fileInputStream, String fileName, String identifier) throws IOException, InterruptedException
	{
		return createNewFormattedFile(fileInputStream, fileName, Paths.get(GenericFile.LOG_EXPORT_DIR, identifier).toString(), false);
	}

	/**
	 * create a new temp file
	 * @param suffix - suffix of filename, usually the desired extension
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createTempFile(String suffix) throws IOException
	{
		File file = File.createTempFile("juno", suffix);
		logger.info("Created tempfile: " + file.getPath());
		return getExistingFile(file);
	}

	/**
	 * Write the input stream to a tempfile
	 * @param directory - the directory to create the tempFile in
	 * @param suffix - suffix of filename, usually the desired extension
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createTempFile(Path directory, String suffix) throws IOException
	{
		if(directory == null)
		{
			return createTempFile(suffix);
		}
		File file = Files.createTempFile(directory, "juno", suffix).toFile();
		logger.info("Created tempfile: " + file.getPath());
		return getExistingFile(file);
	}

	/**
	 * Write the input stream to a tempfile
	 * @param fileInputStream - input stream of the new file
	 * @param suffix - suffix of filename, usually the desired extension
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createTempFile(InputStream fileInputStream, String suffix) throws IOException, InterruptedException
	{
		File file = File.createTempFile("juno", suffix);
		logger.info("Created tempfile: " + file.getPath());
		return writeInputStream(fileInputStream, file, true, true);
	}

	/**
	 * Write the input stream to a tempfile
	 * @param fileInputStream - input stream of the new file
	 * @param directory - the directory to create the tempFile in
	 * @param suffix - suffix of filename, usually the desired extension
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createTempFile(InputStream fileInputStream, Path directory, String suffix) throws IOException, InterruptedException
	{
		File file = Files.createTempFile(directory, "juno", suffix).toFile();
		logger.info("Created tempfile: " + file.getPath());
		return writeInputStream(fileInputStream, file, true, true);
	}
	/**
	 * Write the input stream to a tempfile
	 * @param outputStream - input stream of the new file
	 * @param suffix - suffix of filename, usually the desired extension
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createTempFile(ByteArrayOutputStream outputStream, String suffix) throws IOException
	{
		File file = File.createTempFile("juno", suffix);
		logger.info("Created tempfile: " + file.getPath());
		return writeOutputStream(outputStream, file);
	}

	/**
	 * create a temp directory
	 * @return - the directory path
	 * @throws IOException
	 */
	public static Path createTempDirectory() throws IOException
	{
		Path tempDir = Files.createTempDirectory("juno");
		logger.info("Created temp directory: " + tempDir.toString());
		return tempDir;
	}

	/**
	 * create a sub directory within the base directory
	 * @return - the directory path
	 * @throws IOException
	 */
	public static Path createSubDirectoryIfNotExists(Path baseDirectory, String subDirectory) throws IOException
	{
		Path subDir = Files.createDirectory(Paths.get(baseDirectory.toString(), subDirectory));
		logger.info("Created sub directory: " + subDir.toString());
		return subDir;
	}

	/**
	 * Write the input stream to a resource file
	 * @param fileInputStream - input stream of the new file
	 * @param fileName - name of the new file
	 * @return the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile createResourceFile(InputStream fileInputStream, String fileName) throws IOException, InterruptedException
	{
		return createNewSanitizedFile(fileInputStream, fileName, GenericFile.RESOURCE_BASE_DIR);
	}

	/**
	 * load an existing document with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getDocumentFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.DOCUMENT_BASE_DIR, fileName);
	}

	/**
	 * load an existing hrm file with the given name
	 * checks the hrm directory with a fallback to the main document location
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getHrmFile(String fileName) throws IOException
	{
		if(fileExists(GenericFile.HRM_BASE_DIR, fileName))
		{
			return getExistingFile(GenericFile.HRM_BASE_DIR, fileName);
		}
		else
		{
			return getDocumentFile(fileName);
		}
	}

	/**
	 * load an existing eform image with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getEformImage(String fileName) throws IOException
	{
		if(!isFileInDirectory(fileName, GenericFile.EFORM_IMAGE_DIR))
		{
			throw new IOException("SECURITY WARNING: Illegal file path detected, client attempted to navigate away from the eform images directory");
		}
		GenericFile file = getExistingFile(GenericFile.EFORM_IMAGE_DIR, fileName);
		file.restrictContentType = false;
		file.forceSetValidation(true);
		return file;
	}

	/**
	 * load an existing remittance file with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getRemittanceFile(String fileName) throws IOException
	{
		// remittance files previously were saved to documents, if it is not in the new location, check the old
		if(fileExists(GenericFile.BILLING_REMITTANCE_DIR, fileName))
		{
			return getExistingFile(GenericFile.BILLING_REMITTANCE_DIR, fileName);
		}
		else
		{
			return getDocumentFile(fileName);
		}
	}

	/**
	 * load an existing pending fax file with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getOutboundPendingFaxFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.OUTBOUND_FAX_DIR_PENDING, fileName);
	}

	/**
	 * load an existing unsent fax file with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getOutboundUnsentFaxFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.OUTBOUND_FAX_DIR_UNSENT, fileName);
	}

	/**
	 * load an existing sent fax file with the given name
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getOutboundSentFaxFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.OUTBOUND_FAX_DIR_SENT, fileName);
	}

	/**
	 * load an existing resource file
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getResourceFile(String fileName) throws IOException
	{
		return getExistingFile(GenericFile.RESOURCE_BASE_DIR, fileName);
	}

	/**
	 * load an existing log file
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getImportLogFile(String identifier, String fileName) throws IOException
	{
		return getExistingFile(Paths.get(GenericFile.LOG_IMPORT_DIR, identifier).toString(), fileName);
	}

	/**
	 * load an existing log file
	 * @param fileName - name of the file to load
	 * @return - the file
	 */
	public static GenericFile getExportLogFile(String identifier, String fileName) throws IOException
	{
		return getExistingFile(Paths.get(GenericFile.LOG_EXPORT_DIR, identifier).toString(), fileName);
	}

	/**
	 * overwrite a resource file
	 * @param newFileContent the content to overwrite the file with
	 * @param fileName the name of the file to overwrite
	 * @return the overwriten file
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static GenericFile overwriteResourceFile(InputStream newFileContent, String fileName) throws IOException, InterruptedException
	{
		return overwriteFileContents(getResourceFile(fileName), newFileContent);
	}

	public static boolean isResourceFileExist(String fileName)
	{
		return fileExists(GenericFile.RESOURCE_BASE_DIR, fileName);
	}

	/**
	 * Copy the given file to a new file.
	 * This will be placed in the tempfile location, and should be moved afterwards
	 * @param fileToCopy - the file to be copied
	 * @return a new copy of the file
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile copy(GenericFile fileToCopy) throws IOException
	{
		File fileObjToCopy = fileToCopy.getFileObject();
		String extension = FilenameUtils.getExtension(fileObjToCopy.getPath());
		File newFile = File.createTempFile("juno", "." + extension);
		Files.copy(fileObjToCopy.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return getExistingFile(newFile);
	}


	/**
	 * Overwrite an existing file with new stream content
	 * @param oldFile - generic file to overwrite
	 * @param fileInputStream - stream to save over existing file
	 * @return the new GenericFile
	 * @throws IOException - if an error occurs
	 */
	public static GenericFile overwriteFileContents(GenericFile oldFile, InputStream fileInputStream) throws IOException, InterruptedException
	{
		File file = oldFile.getFileObject();

		if(!fileExists(file))
		{
			throw new IOException("Attempt to overwrite an invalid File: " + file.getPath());
		}
		logger.info("Overwriting file contents: " + file.getPath());
		return writeInputStream(fileInputStream, file, true, true);
	}

	/**
	 * unpacks a zip file to the same directory as the zip file. does not attempt to modify, rename, or reprocess contained files.
	 * preserves sub directory structure within the zip
	 * @param zip - the zip file
	 * @return - the list of files within the zip
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<GenericFile> unpackZipFile(ZIPFile zip) throws IOException, InterruptedException
	{
		return unpackZipFile(zip, zip.getDirectory());
	}

	/**
	 * unpacks a zip file to the specified directory. does not attempt to modify, rename, or reprocess contained files.
	 * preserves sub directory structure within the zip
	 * @param zip - the zip file
	 * @param directory - the output directory
	 * @return - the list of files within the zip
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<GenericFile> unpackZipFile(ZIPFile zip, String directory) throws IOException, InterruptedException
	{
		List<GenericFile> unzippedFiles = new ArrayList<>();

		ZipFile zipFile = new ZipFile(zip.getFileObject());
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		try
		{
			while(entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();

				String entryName = FilenameUtils.getName(entry.getName());
				String entryDirectory = FilenameUtils.getFullPath(entry.getName());
				String fullPath = Paths.get(directory, entryDirectory).toString();

				if(!entry.isDirectory())
				{
					unzippedFiles.add(createNewFile(zipFile.getInputStream(entry), entryName, fullPath, false));
				}
			}
		}
		catch(Exception e)
		{
			// clean up files in the event of an error
			for(GenericFile file : unzippedFiles)
			{
				file.deleteFile();
			}
			throw e;
		}

		return unzippedFiles;
	}

	/**
	 * creates a new file with the given name an content. NOTE a datatime string is prepended to the file name.
	 * @param fileInputStream content to be placed in the file
	 * @param fileName the file name to use for the file
	 * @param folder the folder in which to place the file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static GenericFile createNewFormattedFile(InputStream fileInputStream, String fileName, String folder, boolean reprocess) throws IOException, InterruptedException
	{
		String formattedFileName = GenericFile.getFormattedFileName(fileName);

		return createNewFile(fileInputStream, formattedFileName, folder, reprocess);
	}

	/**
	 * creates a new file with the given name an content
	 * @param fileInputStream content to be placed in the file
	 * @param fileName the file name to use for the file
	 * @param folder the folder in which to place the file
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static GenericFile createNewSanitizedFile(InputStream fileInputStream, String fileName, String folder) throws IOException, InterruptedException
	{
		String sanitizedFileName = GenericFile.getSanitizedFileName(fileName);

		return createNewFile(fileInputStream, sanitizedFileName, folder, true);
	}

	private static GenericFile createNewFile(InputStream fileInputStream, String fileName, String folder, boolean reprocess) throws IOException, InterruptedException
	{
		File directory = new File(folder);
		if(!directory.exists())
		{
			boolean mkdir = directory.mkdirs();
			if(!mkdir)
			{
				throw new IOException("Failed to create Directory: " + directory.getPath());
			}
		}

		File file = new File(directory.getPath(), fileName);
		return writeInputStream(fileInputStream, file, false, reprocess);
	}

	private static GenericFile writeInputStream(InputStream fileInputStream, File file, boolean allowOverwrite, boolean reprocess) throws IOException, InterruptedException
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
		if (reprocess)
		{
			returnFile.process();
		}

		checkAllowedContentType(returnFile);
		return returnFile;
	}

	private static GenericFile writeOutputStream(ByteArrayOutputStream outputStream, File file) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(file);
		outputStream.writeTo(fos);
		fos.close();
		IOUtils.closeQuietly(fos);

		GenericFile returnFile = getExistingFile(file);
		checkAllowedContentType(returnFile);
		return returnFile;
	}
	private static void checkAllowedContentType(GenericFile genericFile) throws IOException
	{
		// for now, only warn with unknown file content types.
		String contentType = genericFile.getContentType();
		if(!GenericFile.getAllowedContent().contains(contentType))
		{
			//TODO - how to handle unknown content type. must have complete list of allowed types first
			logger.warn("Unknown file content type: " + contentType);
		}
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
	public static GenericFile getExistingFile(File file) throws IOException
	{
		logger.info("Load File: " + file.getPath());

		GenericFile genFile;
		if(fileExists(file))
		{
			String fileContent = GenericFile.getContentType(file);
			logger.info("FileContent: " + fileContent);
			if("application/pdf".equals(fileContent))
			{
				genFile = new PDFFile(file);
			}
			else if("application/xml".equals(fileContent) || "text/xml".equals(fileContent))
			{
				genFile = new XMLFile(file);
			}
			else if("application/zip".equals(fileContent))
			{
				genFile = new ZIPFile(file);
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

	private static boolean fileExists(String folder, String fileName)
	{
		return fileExists(new File(folder, fileName));
	}

	private static boolean fileExists(File file)
	{
		return file.exists() && file.isFile();
	}

	private static boolean isFileInDirectory(String fileName, String directory)
	{
		File directoryFile = new File(directory);
		File file = new File(directory, fileName);
		return directoryFile.equals(file.getParentFile());
	}
}
