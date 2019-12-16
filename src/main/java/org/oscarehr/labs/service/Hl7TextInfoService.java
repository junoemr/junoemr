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
package org.oscarehr.labs.service;

import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Hl7TextInfoService
{
	/**
	 * map that maps between report status enum and display string
	 */
	private static final Map<Hl7TextInfo.REPORT_STATUS, String> REPORT_STATUS_DISPLAY_STRING = new HashMap<>();
	static
	{
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.C, "Corrected");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.F, "Final");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.X, "Cancelled");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.P, "Partial");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.E, "Preliminary");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.A, "Authenticated");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.D, "Dictated");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.O, "Documented");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.I, "Incomplete");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.N, "In Progress");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.L, "Legally Authenticated");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.R, "Pre-authenticated");
		REPORT_STATUS_DISPLAY_STRING.put(Hl7TextInfo.REPORT_STATUS.S, "Signed");
	}

	/**
	 * get the display string associated with the given report status
	 * @param repStatus - report stats of which to get the display string
	 * @return - the display string
	 */
	public static String getReportStatusDisplayString(Hl7TextInfo.REPORT_STATUS repStatus)
	{
		String reportStatus = REPORT_STATUS_DISPLAY_STRING.get(repStatus);
		if (reportStatus == null)
		{
			reportStatus = "Partial";
		}
		return reportStatus;
	}

	/**
	 * get a report status enum from the string from of said enum
	 * @param reportStatusStr - string name of enum
	 * @return - enum
	 */
	public static Hl7TextInfo.REPORT_STATUS getReportStatusFromString(String reportStatusStr)
	{
		try
		{
			return Hl7TextInfo.REPORT_STATUS.valueOf(reportStatusStr);
		}
		catch (IllegalArgumentException e)
		{
			MiscUtils.getLogger().error("Failed hl7 report status mapping for type [" + reportStatusStr + "] with error: " + e.getMessage(), e);
		}
		return null;
	}

}
