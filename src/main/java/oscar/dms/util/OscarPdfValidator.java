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

package oscar.dms.util;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class OscarPdfValidator
{
	private static final Set<String> allowedErrors = new HashSet<>();

	private static Logger logger = MiscUtils.getLogger();
	private static OscarProperties props = oscar.OscarProperties.getInstance();

	private File file;
	private boolean hasBeenValidated;
	private boolean isValid;

	private String reasonInvalid;
	private int exitValue;

	public OscarPdfValidator(File file)
	{
		this.file = file;
		this.hasBeenValidated = false;
		this.isValid = false;

		this.reasonInvalid = null;
		this.exitValue = -1;
	}

	public boolean validate()
	{
		this.isValid = false;
		try
		{
			this.isValid = runExec();
		}
		catch(IOException | InterruptedException e)
		{
			logger.error("Error with pdf validate",e);
		}

		this.hasBeenValidated = true;
		return this.isValid;
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
	public int getExitValue()
	{
		return this.exitValue;
	}


	private boolean runExec() throws IOException,InterruptedException
	{
		boolean isValid = true;
		String pdfInfo = props.getProperty("document.pdfinfo_path", "/usr/bin/pdfinfo");

		String[] command = {pdfInfo, file.getPath()};
		Process process = Runtime.getRuntime().exec(command);

		BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String line;

		logger.info("validator error output:");

		while((line = in.readLine()) != null)
		{
			logger.info(line);
			// if error is allowed and flag not already set to fail
			isValid = (isValid && allowedErrors.contains(line.toLowerCase()));
			this.reasonInvalid = (this.reasonInvalid == null)? line : this.reasonInvalid + ", " + line;
		}
		process.waitFor();
		in.close();

		this.exitValue = process.exitValue();
		if(this.exitValue != 0) {
			isValid = false;
		}

		logger.info("Passed Validation: " + isValid);
		logger.info("Exit Value: " + this.exitValue);
		return isValid;
	}
}
