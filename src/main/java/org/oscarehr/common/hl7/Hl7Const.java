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
package org.oscarehr.common.hl7;

public class Hl7Const
{
	public static final String HL7_GROUP_MEDS = "MEDS";

	public static final String HL7_SEGMENT_TS_1 = "TS.1";

	public static final String HL7_SEGMENT_XCN_1 = "XCN.1";

	public static final String HL7_SEGMENT_XTN_1 = "XTN.1";
	public static final String HL7_SEGMENT_XTN_6 = "XTN.6";
	public static final String HL7_SEGMENT_XTN_7 = "XTN.7";

	public static final String HL7_SEGMENT_ZAT_2 = "ZAT.2";

	public static final String HL7_SEGMENT_ZBA = "ZBA";
	public static final String HL7_SEGMENT_ZBA_4 = "ZBA.4";
	public static final String HL7_SEGMENT_ZBA_29 = "ZBA.29";
	public static final String HL7_SEGMENT_ZBA_31 = "ZBA.31";

	public static final String HL7_SEGMENT_ZPB_10 = "ZPB.10";

	public static final String HL7_SEGMENT_ZPV_5 = "ZPV.5";

	public static final String HL7_SEGMENT_ZQO = "ZQO";
	public static final String HL7_SEGMENT_ZQO_4 = "ZQO.4";
	public static final String HL7_SEGMENT_ZQO_5 = "ZQO.5";
	public static final String HL7_SEGMENT_ZQO_6 = "ZQO.6";
	public static final String HL7_SEGMENT_ZQO_7 = "ZQO.7";
	public static final String HL7_SEGMENT_ZQO_8 = "ZQO.8";

	public static final String HL7_SEGMENT_SCH_11 = "SCH.11";

	public static final String ABNORMAL_FLAG_NO = "N";
	public static final String ABNORMAL_FLAG_YES = "A";

	public static String getReadableSegmentName(String segment, String segmentField)
	{
		String segmentReadableName = "";
		String subSegmentReadableName = "";
		switch(segment)
		{
			case HL7_SEGMENT_ZQO:
			{
				segmentReadableName = "Measurements";
				switch(segmentField)
				{
					case "4": subSegmentReadableName = "Systolic Blood Pressure"; break;
					case "5": subSegmentReadableName = "Diastolic Blood Pressure"; break;
					case "6": subSegmentReadableName = "Height (in cm)"; break;
					case "7": subSegmentReadableName = "Weight (in kg)"; break;
					case "8": subSegmentReadableName = "Waist Circumference (in cm)"; break;
				}
				break;
			}
			case HL7_GROUP_MEDS:
			{
				segmentReadableName = "Medications";
				break;
			}
			case HL7_SEGMENT_ZBA:
			{
				segmentReadableName = "Billing - Alberta Health & Wellness";
				switch(segmentField)
				{
					case "4": subSegmentReadableName = "Facility Number"; break;
					case "31": subSegmentReadableName = "Business Arrangement"; break;
				}
				break;
			}
		}

		return segmentReadableName + (subSegmentReadableName.isEmpty() ? "" : ": " + subSegmentReadableName);
	}
}
