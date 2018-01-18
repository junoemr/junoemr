/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.oscarehr.common.exception.HtmlToPdfConversionException;
import oscar.OscarProperties;

public class WKHtmlToPdfUtils
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final int TIMEOUT_MILLISECONDS = 40000;
	private static final String CONVERT_COMMAND;
	private static final String CONVERT_COMMAND_LABEL;

	static
	{
		String convertCommand = OscarProperties.getInstance().getProperty("WKHTMLTOPDF_COMMAND");
		if (convertCommand != null) CONVERT_COMMAND = convertCommand;
		else
			throw (new RuntimeException("Properties file is missing property : WKHTMLTOPDF_COMMAND"));

		String convertCommandLabel = OscarProperties.getInstance().getProperty("WKHTMLTOPDF_COMMAND_LABEL");
		if (convertCommandLabel != null) CONVERT_COMMAND_LABEL = convertCommandLabel;
		else CONVERT_COMMAND_LABEL = CONVERT_COMMAND;
	}

	private WKHtmlToPdfUtils()
	{
		// not meant for instantiation
	}

	/**
	 * This method should convert the html page at the sourceUrl into a pdf as returned by the byte[]. This method requires wkhtmltopdf to be installed on the machine.
	 *
	 * @throws IOException
	 * @throws HtmlToPdfConversionException
	 */
	public static byte[] convertToPdf(String sourceUrl) throws IOException, HtmlToPdfConversionException
	{
		File outputFile = null;

		try
		{
			outputFile = File.createTempFile("wkhtmltopdf.", ".pdf");
			outputFile.deleteOnExit();

			convertToPdf(sourceUrl, outputFile);

			FileInputStream fis = new FileInputStream(outputFile);
			try
			{
				byte[] results = IOUtils.toByteArray(fis);
				return (results);
			}
			finally
			{
				if (fis != null) fis.close();
			}
		}
		finally
		{
			if (outputFile != null) outputFile.delete();
		}
	}

	/**
	 * This method should convert the html page at the sourceUrl into a pdf written to the outputFile. This method requires wkhtmltopdf to be installed on the machine. In general the outputFile should be a unique temp file. If you're not sure what you're
	 * doing don't call this method as you will leave lingering data everywhere or you may overwrite important files...
	 */
	public static void convertToPdf(String sourceUrl, File outputFile) throws IOException, HtmlToPdfConversionException
	{
		String outputFilename = outputFile.getCanonicalPath();
		// example command : wkhtmltopdf-i386 "https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms" /tmp/out.pdf
		String elements = CONVERT_COMMAND + " " + sourceUrl + " " + outputFilename;
		String[] command = elements.split(" ");
		logger.info("Running : " + elements);

		try
		{
			int exitValue = runtimeExec(command);
			if (exitValue != 0)
			{
				throw new HtmlToPdfConversionException("Attempting to convert eForm to pdf returned a nonzero exit value: " + exitValue);
			}
		}
		catch (TimeoutException e)
		{
			throw new HtmlToPdfConversionException("Attempting to convert eForm to pdf timed out before operation could be completed.", e);
		}
	}

	/**
	 * This method should convert the html page at the sourceUrl into a pdf (of label size) written to the outputFile. This method requires wkhtmltopdf to be installed on the machine. In general the outputFile should be a unique temp file. If you're not sure what you're
	 * doing don't call this method as you will leave lingering data everywhere or you may overwrite important files...
	 */
	public static void convertToPdfLabel(String sourceUrl, File outputFile) throws IOException, HtmlToPdfConversionException
	{
		String outputFilename = outputFile.getCanonicalPath();
		// example command : wkhtmltopdf-i386 "https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms" /tmp/out.pdf
		String elements = CONVERT_COMMAND_LABEL + " " + sourceUrl + " " + outputFilename;
		String[] command = elements.split(" ");
		logger.info("Running : " + elements);

		try
		{
			int exitValue = runtimeExec(command);
			if (exitValue != 0)
			{
				throw new HtmlToPdfConversionException("Attempting to convert eForm to pdf returned a nonzero exit value: " + exitValue);
			}
		}
		catch (TimeoutException e)
		{
			throw new HtmlToPdfConversionException("Attempting to convert eForm to pdf timed out before operation could be completed.", e);
		}
	}

	/**
	 * Executes the given command.
	 *
	 * @param command array containing the command to call and its arguments
	 * @return The command's return code.
	 * @throws TimeoutException if command does not finish execution within TIMEOUT_MILLISECONDS
	 * @throws IOException      if an I/O error occurs
	 */
	private static int runtimeExec(String[] command) throws IOException, TimeoutException
	{
		Process process = Runtime.getRuntime().exec(command);

		try
		{
			Timer timer = new Timer();
			timer.schedule(new InterruptTask(Thread.currentThread()), TIMEOUT_MILLISECONDS);

			int exitValue = process.waitFor();

			timer.cancel();
			timer.purge();

			if (exitValue != 0)
			{
				logger.debug("Nonzero exit value running command : " + command[0]);

				String errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getInputStream()));
				if (errorMsg != null) logger.debug(errorMsg);

				errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getErrorStream()));
				if (errorMsg != null) logger.debug(errorMsg);
			}

			return exitValue;
		}
		catch (InterruptedException e)
		{
			// clear interrupted flag
			Thread.interrupted();
			throw new TimeoutException("Command timed out after " + TIMEOUT_MILLISECONDS + "ms: " + command[0]);
		}
		finally
		{
			process.destroy();
		}

	}

	static class InterruptTask extends TimerTask
	{
		Thread target = null;

		private InterruptTask(Thread target)
		{
			this.target = target;
		}

		@Override
		public void run()
		{
			target.interrupt();
		}

	}
}


