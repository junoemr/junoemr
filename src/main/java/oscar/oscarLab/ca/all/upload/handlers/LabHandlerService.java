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

import org.oscarehr.util.LoggedInInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarLab.FileUploadCheck;
import oscar.oscarLab.ca.all.upload.HandlerClassFactory;
import org.apache.log4j.Logger;

@Service
public class LabHandlerService {

	Logger logger = Logger.getLogger(MDSHandler.class);

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String importLab(String type,
						LoggedInInfo loggedInInfo,
						String serviceName,
						String filePath,
						String providerNumber,
						String ipAddr) throws Exception {

		int fileId = FileUploadCheck.addFile(filePath, providerNumber);
		MessageHandler msgHandler = HandlerClassFactory.getHandler(type);
		String audit = msgHandler.parse(loggedInInfo, serviceName, filePath, fileId, ipAddr);

		// For legacy lab handlers. Instead of throwing an exception, they return null if there is an error
		if(audit == null)
		{
			throw new Exception("Lab parsing returned null");
		}
		logger.debug("Lab service Audit: " + audit);
		return audit;
	}
}

