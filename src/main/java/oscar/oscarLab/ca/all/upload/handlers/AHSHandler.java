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
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

import java.util.ArrayList;

public class AHSHandler implements MessageHandler {

	private static final Logger logger = Logger.getLogger(AHSHandler.class);
	public String parse(LoggedInInfo loggedInInfo, String serviceName,
	                    String fileName, int fileId, String ipAddr) throws Exception {

		oscar.oscarLab.ca.all.parsers.AHSHandler AHSParser = new oscar.oscarLab.ca.all.parsers.AHSHandler();

		Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao) SpringUtils.getBean("hl7TextInfoDao");

		ArrayList<String> messages = Utilities.separateMessages(fileName);
		for (int i = 0; i < messages.size(); i++)
		{
			String msg = messages.get(i);

			AHSParser.init(msg);
			String accessionNumber = AHSParser.getAccessionNum();
			Hl7TextInfo hl7TextInfo = hl7TextInfoDao.findLatestVersionByAccessionNo(accessionNumber);

			// if the report exists the new version must be a correction
			if (hl7TextInfo == null || AHSParser.getOrderStatus().equals("C"))
			{
				MessageUploader.routeReport(loggedInInfo, serviceName, "CLSDI", msg, fileId);
			}
			else
			{
				logger.warn("Report Already Uploaded. Status: " + AHSParser.getOrderStatus());
			}
		}
		return ("success");
	}
}
