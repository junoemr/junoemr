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


package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;

public class AHSHandler implements MessageHandler
{
	private static final Logger logger = Logger.getLogger(AHSHandler.class);

	public String parse(LoggedInInfo loggedInInfo, String serviceName,
	                    String fileName, int fileId, String ipAddr) throws Exception
	{
		ArrayList<String> messages = Utilities.separateMessages(fileName);
		for(String msg : messages)
		{
			logger.debug("Handle message:\n" + msg);
			oscar.oscarLab.ca.all.parsers.MessageHandler parser = Factory.getHandler("AHS", msg);
			// just in case
			if(parser == null)
				throw new RuntimeException("No Parser available for lab");

			// allow each lab type to make modifications to the hl7 if needed.
			// This is for special cases only most labs return an identical string to the input parameter
			msg = parser.preUpload(msg);

			// check if the lab has passed validation and can be saved
			if(parser.canUpload())
			{
				MessageUploader.routeReport(loggedInInfo.getLoggedInProviderNo(), serviceName, parser.getMsgType(), msg, fileId);
				parser.postUpload();
			}
			else
			{
				logger.warn("Hl7 Report Could Not be Uploaded");
			}
		}
		return ("success");
	}
}
