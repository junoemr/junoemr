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

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarLab.ca.all.upload.MessageUploader;
import oscar.oscarLab.ca.all.util.Utilities;

/**
 * Handler for:
 * Calgary Lab Services Diagnostic Imaging
 * @author Robert
 */
public class CLSDIHandler extends CLSHandler implements MessageHandler {
	
	public CLSDIHandler() {
		super();
		logger = Logger.getLogger(CLSDIHandler.class);
	}
	
	public String parse(String serviceName, String fileName, int fileId) {

        oscar.oscarLab.ca.all.parsers.CLSDIHandler newVersionCLSParser = new oscar.oscarLab.ca.all.parsers.CLSDIHandler();
        
        Hl7TextInfoDao hl7TextInfoDao = (Hl7TextInfoDao)SpringUtils.getBean("hl7TextInfoDao");
		
		try {
			ArrayList<String> messages = Utilities.separateMessages(fileName);
			for (int i = 0; i < messages.size(); i++) {
				String msg = messages.get(i);

                newVersionCLSParser.init(msg);
                String accessionNumber = newVersionCLSParser.getAccessionNum();
                Hl7TextInfo hl7TextInfo = hl7TextInfoDao.findLatestVersionByAccessionNo(accessionNumber);
                
                // if the report exists the new version must be a correction
                if(hl7TextInfo == null || newVersionCLSParser.getOrderStatus().equals("C")) {
                	MessageUploader.routeReport(serviceName, "CLSDI", msg, fileId);
                }
                else {
                	logger.warn("Report Already Uploaded. Status: " + newVersionCLSParser.getOrderStatus());
                }
			}
		} catch (Exception e) {
			MessageUploader.clean(fileId);
			logger.error("Could not upload message: ", e);
			MiscUtils.getLogger().error("Error", e);
			return null;
		}
		return ("success");

	}
}
