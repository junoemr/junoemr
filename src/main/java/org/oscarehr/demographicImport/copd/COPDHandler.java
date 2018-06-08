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
package org.oscarehr.demographicImport.copd;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Component;

@Component
public class COPDHandler
{
	private static final Logger logger = MiscUtils.getLogger();

	public static boolean isCOPDFormat(Message message)
	{
		String version = message.getVersion();
		if(version.equals("2.4"))
		{
			logger.info("Version 2.4 detected!");
			return true;
		}
		else
		{
			logger.warn("Version not detected!");
		}
		return false;
	}

	public COPDHandler()
	{
	}
	public COPDHandler(Message message) throws HL7Exception
	{
		ZPD_ZTR zpd_ztr = (ZPD_ZTR) message;
//		PID pid = zpd_ztr.getMESSAGE().getPATIENT().getPID();

//		logger.info("PID-3-0 name:" + pid.getPid3_PatientIdentifierList(0).getName());



//		Terser terser = new Terser(message);
//
//		String msh_7 = terser.get("/.MSH-7-1");
//		logger.info("MSH-7: " + msh_7);
////		Segment zpiGenericSegment = terser.get("/.PID-7");
////		String zs1_1  = zpiGenericSegment.getField(1, 0).encode();
//		String zs1_1 = terser.get("/.ZS1-1");
//		logger.info("ZS1.1: " + zs1_1);
//
////		String zs1_6  = zpiGenericSegment.getField(6, 0).encode();
//		String zs1_6 = terser.get("/.ZS1-6-1");
//		logger.info("ZS1.6: " + zs1_6);
	}
}
