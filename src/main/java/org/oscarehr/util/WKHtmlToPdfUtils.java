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
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.oscarehr.common.exception.HtmlToPdfConversionException;
import oscar.OscarProperties;

public class WKHtmlToPdfUtils
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final Logger logger = MiscUtils.getLogger();

	private static final int TIMEOUT_SECONDS = 40;
	private static final String CONVERT_COMMAND;
	private static final String CONVERT_ARGS;
	private static final String CONVERT_ARGS_LABEL;

	static
	{
		String convertCommand = props.getProperty("WKHTMLTOPDF_COMMAND");
		if (convertCommand != null) CONVERT_COMMAND = convertCommand;
		else
			throw (new RuntimeException("Properties file is missing property : WKHTMLTOPDF_COMMAND"));

		String convertParameters = props.getProperty("WKHTMLTOPDF_ARGS");
		if (convertParameters != null) CONVERT_ARGS = convertParameters;
		else CONVERT_ARGS = null;

		String convertParametersLabel = props.getProperty("WKHTMLTOPDF_ARGS_LABEL");
		if (convertParametersLabel != null) CONVERT_ARGS_LABEL = convertParametersLabel;
		else CONVERT_ARGS_LABEL = null;
	}

	private WKHtmlToPdfUtils()
	{
		// not meant for instantiation
	}


	/**
	 * This method is a copy of Apache Tomcat's ApplicationHttpRequest getRequestURL method with the
	 * exception that the uri is removed and replaced with our eform viewing uri. Note that this
	 * requires that the remote url is valid for local access. i.e. the host name from outside needs
	 * to resolve inside as well. The result needs to look something like this:
	 * https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms
	 */
	public static String getEformRequestUrl(String providerId, String formId, String httpScheme, String contextPath)
	{
		StringBuilder url = new StringBuilder();
		String scheme = httpScheme;
		String prop_scheme = props.getProperty("oscar_protocol");
		if (prop_scheme != null && !prop_scheme.isEmpty())
		{
			scheme = prop_scheme;
		}

		Integer port = PortListener.getPort();

		if(port == null)
		{
			try
			{
				port = new Integer(props.getProperty("oscar_port"));
			}
			catch (Exception e)
			{
				port = 8443;
			}
		}

		if (port < 0)
		{
			port = 80; // Work around java.net.URL bug
		}

		url.append(scheme);
		url.append("://");
		//url.append(request.getServerName());
		url.append("127.0.0.1");

		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443)))
		{
			url.append(':');
			url.append(port);
		}
		url.append(contextPath);
		url.append("/EFormViewForPdfGenerationServlet?parentAjaxId=eforms&providerId=");
		url.append(providerId);
		url.append("&fdid=");
		url.append(formId);

		return (url.toString());
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
	 * This method should convert the html page at the sourceUrl into a pdf written to the outputFile.
	 * This method requires wkhtmltopdf to be installed on the machine.
	 * In general the outputFile should be a unique temp file.
	 * If you're not sure what you're doing don't call this method as you will leave lingering data everywhere or you may overwrite important files...
	 * @throws IOException
	 * @throws HtmlToPdfConversionException
	 */
	public static void convertToPdf(String sourceUrl, File outputFile) throws IOException, HtmlToPdfConversionException
	{
		String outputFilename = outputFile.getCanonicalPath();

		// example command : wkhtmltopdf-i386 "https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms" /tmp/out.pdf
		ArrayList<String> command = new ArrayList<String>();
		command.add(CONVERT_COMMAND);
		if (CONVERT_ARGS != null)
		{
			for (String arg : CONVERT_ARGS.split("\\s"))
				command.add(arg);
		}
		command.add(sourceUrl);
		command.add(outputFilename);

		logger.info(command);

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
	 * @throws IOException
	 * @throws HtmlToPdfConversionException
	 */
	public static void convertToPdfLabel(String sourceUrl, File outputFile) throws IOException, HtmlToPdfConversionException
	{
		String outputFilename = outputFile.getCanonicalPath();

		// example command : wkhtmltopdf-i386 "https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms" /tmp/out.pdf
		ArrayList<String> command = new ArrayList<String>();
		command.add(CONVERT_COMMAND);
		if (CONVERT_ARGS_LABEL != null)
		{
			for (String arg : CONVERT_ARGS_LABEL.split("\\s"))
				command.add(arg);
		}
		command.add(sourceUrl);
		command.add(outputFilename);

		logger.info(command);

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
	 * @param  command          array containing the command to call and its arguments
	 *
	 * @return The command's return code.
	 *
	 * @throws TimeoutException if command does not finish execution within TIMEOUT_SECONDS
	 * @throws IOException      if an I/O error occurs
	 */
	private static int runtimeExec(ArrayList<String> command) throws IOException, TimeoutException
	{
		Process process = Runtime.getRuntime().exec(command.toArray(new String[0]));

		try
		{
			Boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

			if (!finished)
			{
				throw new TimeoutException("Command timed out after " + TIMEOUT_SECONDS + "s: " + command.get(0));
			}

			int exitValue = process.exitValue();
			if (exitValue != 0)
			{
				logger.debug("Nonzero exit value running command: " + command.get(0));

				String errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getInputStream()));
				if (errorMsg != null) logger.debug(errorMsg);

				errorMsg = StringUtils.trimToNull(IOUtils.toString(process.getErrorStream()));
				if (errorMsg != null) logger.debug(errorMsg);
			}

			return exitValue;
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new InterruptedIOException("Thread was interrupted while waiting for command to finish.");
		}
		finally
		{
			process.destroy();
		}
	}

}
