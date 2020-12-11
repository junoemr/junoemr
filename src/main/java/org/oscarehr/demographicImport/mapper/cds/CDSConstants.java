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
package org.oscarehr.demographicImport.mapper.cds;

public class CDSConstants
{

	public static final String RESIDUAL_INFO_DATA_NAME_NOTE = "Note";
	public static final String RESIDUAL_INFO_DATA_NAME_OBS_DATE = "Observation Datetime";
	public static final String RESIDUAL_INFO_DATA_NAME_START_DATE = "Start Date";
	public static final String RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE = "Resolution Date";
	public static final String RESIDUAL_INFO_DATA_NAME_ANNOTATION = "Annotation";
	public static final String RESIDUAL_INFO_DATA_NAME_PROVIDER = "ProviderName";

	public enum RESIDUAL_INFO_DATA_TYPE
	{
		TEXT,
		NUMERIC,
		DATE,
		TIME,
		DATETIME,
		DATE_PARTIAL,
	}

	public enum LAB_ABNORMAL_FLAG
	{
		U,
		Y,
		N,
	}

	//TODO where should these live?
	public static final String COUNTRY_CODE_CANADA = "CA";
	public static final String COUNTRY_CODE_USA = "US";

	public static final String ENROLLMENT_STATUS_TRUE = "1";
	public static final String ENROLLMENT_STATUS_FALSE = "0";

	public static final String Y_INDICATOR_TRUE = "T";
	public static final String Y_INDICATOR_FALSE = "F";
}
