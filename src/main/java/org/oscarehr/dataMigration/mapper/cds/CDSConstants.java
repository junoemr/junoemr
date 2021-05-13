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
package org.oscarehr.dataMigration.mapper.cds;

public class CDSConstants
{
	public static final String DEFAULT_DOCUMENT_DESCRIPTION = "Imported Report";
	public static final String DEFAULT_HRM_DESCRIPTION = "Imported HRM Document";

	public static final String RESIDUAL_INFO_DATA_NAME_NOTE = "Note";
	public static final String RESIDUAL_INFO_DATA_NAME_OBS_DATE = "Observation Datetime";
	public static final String RESIDUAL_INFO_DATA_NAME_START_DATE = "Start Date";
	public static final String RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE = "Resolution Date";
	public static final String RESIDUAL_INFO_DATA_NAME_ANNOTATION = "Annotation";
	public static final String RESIDUAL_INFO_DATA_NAME_PROVIDER = "ProviderName";

	public static final String RESIDUAL_INFO_DATA_NAME_AGE_OF_ONSET = "Age Of Onset";
	public static final String RESIDUAL_INFO_DATA_NAME_ONSET_REACTION = "Onset Of Reaction";

	public static final String RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_NEXT_DATE = "Next Immunization Date";
	public static final String RESIDUAL_INFO_DATA_NAME_IMMUNIZATION_TYPE = "ImmunizationType";

	public enum ResidualInfoDataType
	{
		TEXT,
		NUMERIC,
		DATE,
		TIME,
		DATETIME,
		DATE_PARTIAL,
	}

	public enum LabAbnormalFlag
	{
		BELOW_LOW_NORMAL("L", true),
		ABOVE_HIGH_NORMAL("H", true),
		BELOW_LOWER_LIMITS("LL", true),
		ABOVE_HIGH_LIMITS("HH", true),
		NORMAL("N", false),
		ABNORMAL("A", true),
		VERY_ABNORMAL("AA", true),
		SUSCEPTIBLE("S", true),
		RESISTANT("R", true),
		INTERMEDIATE("I", true),
		MODERATE_SUSCEPTIBLE("MS", true),
		VERY_SUSCEPTIBLE("VS", true),
		UNKNOWN("U", false);

		private final String value;
		private final boolean abnormal;

		LabAbnormalFlag(String value, boolean isAbnormal)
		{
			this.value = value;
			this.abnormal = isAbnormal;
		}

		public String getValue()
		{
			return value;
		}

		public boolean isAbnormal()
		{
			return abnormal;
		}

		public static LabAbnormalFlag fromValue(String value)
		{
			for(LabAbnormalFlag abnormalFlag : LabAbnormalFlag.values())
			{
				if(abnormalFlag.getValue().equalsIgnoreCase(value))
				{
					return abnormalFlag;
				}
			}
			return null;
		}
	}

	//TODO where should these live?
	public static final String COUNTRY_CODE_CANADA = "CA";
	public static final String COUNTRY_CODE_USA = "US";

	public static final String ENROLLMENT_STATUS_TRUE = "1";
	public static final String ENROLLMENT_STATUS_FALSE = "0";

	public static final String Y_INDICATOR_TRUE = "Y";
	public static final String Y_INDICATOR_FALSE = "N";

	public static final String DRUG_IDENTIFICATION_NUMBER = "DIN";

	public static final String DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE = "EC";
	public static final String DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE = "SDM";

	// this document-class value shows up in HRM and CDS documentation but does not match the schema
	public static final String DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE = "Medical Record Report";
}
