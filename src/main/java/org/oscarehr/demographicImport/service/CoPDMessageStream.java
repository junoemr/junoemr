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
package org.oscarehr.demographicImport.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.MiscUtils;

import java.io.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoPDMessageStream
{
	private static final Logger logger = MiscUtils.getLogger();

	private BufferedReader fileReader;
	private Pattern messagePattern = Pattern.compile("<ZPD_ZTR\\.MESSAGE>(.*?)<\\/ZPD_ZTR\\.MESSAGE>", Pattern.DOTALL);

	public CoPDMessageStream(GenericFile CoPDFile) throws FileNotFoundException
	{
		this.fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(CoPDFile.getFileObject())));
	}

	public CoPDMessageStream(InputStream CoPDFileInputStream)
	{
		this.fileReader = new BufferedReader(new InputStreamReader(CoPDFileInputStream));
	}

	public void forEach(Consumer<? super String> action)
	{
		String msg;
		while (!(msg = getNextMessage()).isEmpty())
		{
			action.accept(msg);
		}
	}

	public synchronized String getNextMessage()
	{
		logger.info("loading next message...");
		try
		{
			StringBuffer sb = new StringBuffer();
			int character;
			while ((character = this.fileReader.read()) != -1)
			{
				sb.append((char)character);
				if ((char)character == '>' && isCompleteMessage(sb))
				{
					return buildMessage(sb);
				}
			}
			return "";
		}
		catch (IOException e)
		{
			return "";
		}
	}

	private String buildMessage(StringBuffer sb)
	{
		Matcher messagePatternMatcher = messagePattern.matcher(sb.toString());
		if (messagePatternMatcher.find())
		{
			return "<ZPD_ZTR xmlns=\"urn:hl7-org:v2xml\">" + messagePatternMatcher.group(1) + "</ZPD_ZTR>";
		}
		return "";
	}

	private boolean isCompleteMessage(StringBuffer sb)
	{
		if (sb.substring(Math.max(0, sb.length() - 1000), sb.length()).contains("/ZPD_ZTR.MESSAGE"))
		{
			Matcher messagePatternMatcher = messagePattern.matcher(sb.toString());
			return messagePatternMatcher.find();
		}
		return false;
	}
}
