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

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PDFFile extends GenericFile
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final Pattern[] allowedWarningsGS = new Pattern[3];
	private static final Pattern[] disallowedOutputGS = new Pattern[1];

	static
	{
		allowedWarningsGS[0] = Pattern.compile(".*Missing glyph .* in the font .*", Pattern.CASE_INSENSITIVE);
		allowedWarningsGS[1] = Pattern.compile(".*Failed to interpret TT instructions in font.*", Pattern.CASE_INSENSITIVE);
		allowedWarningsGS[2] = Pattern.compile(".*jbig2dec WARNING text region refers to no symbol dictionaries \\(segment 0x02\\).*", Pattern.CASE_INSENSITIVE);

		disallowedOutputGS[0] = Pattern.compile(".*Error reading a content stream\\. The page may be incomplete.*", Pattern.CASE_INSENSITIVE);
	}

	private OscarProperties oscarProperties = OscarProperties.getInstance();
	private long maxMemoryUsage = oscarProperties.getPDFMaxMemUsage();

	public PDFFile(File file)
	{
		super(file);
	}

	private boolean isAllowedWarning(String line)
	{
		for (Pattern pattern : allowedWarningsGS)
		{
			if (pattern.matcher(line).matches())
				return true;
		}
		return false;
	}
	private boolean isDisallowedOutput(String line)
	{
		for (Pattern pattern : disallowedOutputGS)
		{
			if (pattern.matcher(line).matches())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void process() throws IOException, InterruptedException
	{
		javaFile = ghostscriptReEncode();
	}
	/**
	 * Counts the number of pages in a local pdf file.
	 *
	 * @return the number of pages in the file
	 */
	@Override
	public int getPageCount() throws IOException
	{
		int numOfPage = 0;

		try
		{
			if(isValid)
			{
				PDDocument document = PDDocument.load(javaFile, MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));
				numOfPage = document.getNumberOfPages();
				document.close();
			}
		}
		catch(InvalidPasswordException e)
		{
			logger.warn("Encrypted PDF. Can't get page count");
		}

		return numOfPage;
	}

	@Override
	public String getContentType() throws IOException
	{
		String contentType;
		if(isValid)
		{
			contentType = super.getContentType();
		}
		else
		{
			contentType = getInvalidContentType();
		}
		return contentType;
	}


	private File ghostscriptReEncode() throws IOException,InterruptedException
	{
		logger.info("BEGIN PDF RE-ENCODING");

		String gs = props.getProperty("document.ghostscript_path", "/usr/bin/gs");

		File currentDir = javaFile.getParentFile();
		this.moveToOriginal();

		File newPdf = new File(currentDir, javaFile.getName());

		/*
		This has to de done through the command line and not the java library,
		due to a bug in how the java libraries read pdf files. (libraries lowagie, pdfbox, and itext all have this error)
		The error has caused an infinite loop during the initial parse and crashed tomcat.
		 */
		String[] command = {gs,
				"-sDEVICE=pdfwrite",
				"-dCompatibilityLevel=1.4",
				"-dPDFSETTINGS=/printer",
				"-dNOPAUSE",
				"-dQUIET",      // Suppresses routine information comments on standard output
				"-q",           // Quiet startup: suppress normal startup messages, and also do the equivalent of dQUIET
//				"-sstdout=%stderr", // Redirect PostScript %stdout to a file or stderr, to avoid it being mixed with device stdout.
				"-dBATCH",
				"-sOutputFile="+ newPdf.getPath(),
				javaFile.getPath()};
		
		logger.info(Arrays.toString(command));
		Process process = Runtime.getRuntime().exec(command);

		BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		String line;
		String reasonInvalid = null;
		String warnings = null;
		LocalDateTime conversionStartTime = LocalDateTime.now();
		boolean isTimeout = false;

		while((line = stdout.readLine()) != null)
		{
			logger.warn("gs stdout: " + line);
			warnings = (warnings == null)? line : warnings + ", " + line;

			if(isDisallowedOutput(line))
			{
				reasonInvalid = (reasonInvalid == null)? line : reasonInvalid + ", " + line;
			}

			if (LocalDateTime.now().isAfter(conversionStartTime.plus(60, ChronoUnit.SECONDS)))
			{
				isTimeout = true;
				break;
			}
		}

		// skip if timeout. because, reading stderr (can) block forever if we are in timeout
		while(!isTimeout && (line = stderr.readLine()) != null)
		{
			logger.warn("gs stderr: " + line);

			if (isAllowedWarning(line))
				continue;

			reasonInvalid = (reasonInvalid == null)? line : reasonInvalid + ", " + line;

			if (LocalDateTime.now().isAfter(conversionStartTime.plus(60, ChronoUnit.SECONDS)))
			{
				isTimeout = true;
				break;
			}
		}

		int exitValue = 0;
		if (isTimeout)
		{
			process.destroyForcibly();
			reasonInvalid = "Conversion Timed out";
			exitValue = 124; // timeout
		}
		else
		{
			process.waitFor();
			exitValue = process.exitValue();
		}

		stdout.close();
		stderr.close();

		if(exitValue == 0 && reasonInvalid == null && warnings != null)
		{
			// append the original file location to the log if there is unexpected output
			logger.warn("File conversion allowed with unexpected output!");
			logger.warn("Original file:  " + javaFile.getPath());
			logger.warn("Converted file: " + newPdf.getPath());
			logger.warn("---------------------------------------------");
		}

		if(exitValue != 0 || reasonInvalid != null)
		{
			logger.warn("PDF failed to re-encode. Original used and flagged as invalid: " + javaFile.getName());
			logger.warn("---------------------------------------------");

			this.isValid = false;
			this.reasonInvalid = "Ghost-script Error: " + reasonInvalid + ". ExitValue: " + exitValue;
			this.moveFile(currentDir);
			newPdf = javaFile;
		}
		else
		{
			// attempt to load the new pdf to ensure it is readable
			try
			{
				PDDocument document = PDDocument.load(newPdf, MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));
				document.close();
				this.isValid = true;
			}
			catch(Exception e)
			{
				throw new RuntimeException("Failed to load re-encoded PDF: ", e);
			}
		}
		this.hasBeenValidated = true;

		logger.info("END PDF RE-ENCODING");
		return newPdf;
	}
}
