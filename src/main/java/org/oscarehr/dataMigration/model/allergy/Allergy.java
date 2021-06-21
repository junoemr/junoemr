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
package org.oscarehr.dataMigration.model.allergy;

import lombok.Data;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.Provider;

import java.time.LocalDateTime;
import java.util.List;

import static org.oscarehr.allergy.model.Allergy.ONSET_DESC_GRADUAL;
import static org.oscarehr.allergy.model.Allergy.ONSET_DESC_IMMEDIATE;
import static org.oscarehr.allergy.model.Allergy.ONSET_DESC_SLOW;
import static org.oscarehr.allergy.model.Allergy.ONSET_DESC_UNKNOWN;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_DESC_MILD;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_DESC_MODERATE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_DESC_SEVERE;
import static org.oscarehr.allergy.model.Allergy.SEVERITY_DESC_UNKNOWN;

@Data
public class Allergy extends AbstractTransientModel
{
	// enum with custom values, for backwards compatibility
	public enum REACTION_ONSET
	{
		IMMEDIATE(1, ONSET_DESC_IMMEDIATE),
		GRADUAL(2, ONSET_DESC_GRADUAL),
		SLOW(3, ONSET_DESC_SLOW),
		UNKNOWN(4, ONSET_DESC_UNKNOWN);

		private final int onsetCode;
		private final String description;

		REACTION_ONSET(int code, String description)
		{
			this.onsetCode = code;
			this.description = description;
		}
		public int getOnsetCode()
		{
			return onsetCode;
		}
		public String getDescription()
		{
			return description;
		}

		public static REACTION_ONSET fromCodeString(Integer code)
		{
			for(REACTION_ONSET onset : REACTION_ONSET.values())
			{
				if(onset.getOnsetCode() == code)
				{
					return onset;
				}
			}
			return null;
		}
		public static REACTION_ONSET fromDescription(String description)
		{
			for(REACTION_ONSET onset : REACTION_ONSET.values())
			{
				if(onset.getDescription().equalsIgnoreCase(description))
				{
					return onset;
				}
			}
			return null;
		}
	}

	// enum with custom values, for backwards compatibility
	public enum REACTION_SEVERITY
	{
		MILD(1, SEVERITY_DESC_MILD),
		MODERATE(2, SEVERITY_DESC_MODERATE),
		SEVERE(3, SEVERITY_DESC_SEVERE),
		UNKNOWN(4, SEVERITY_DESC_UNKNOWN);

		private final int severityCode;
		private final String description;

		REACTION_SEVERITY(int code, String description)
		{
			this.severityCode = code;
			this.description = description;
		}
		public int getSeverityCode()
		{
			return severityCode;
		}
		public String getDescription()
		{
			return description;
		}

		public static REACTION_SEVERITY fromCodeString(Integer code)
		{
			for(REACTION_SEVERITY onset : REACTION_SEVERITY.values())
			{
				if(onset.getSeverityCode() == code)
				{
					return onset;
				}
			}
			return null;
		}
		public static REACTION_SEVERITY fromDescription(String description)
		{
			for(REACTION_SEVERITY onset : REACTION_SEVERITY.values())
			{
				if(onset.getDescription().equalsIgnoreCase(description))
				{
					return onset;
				}
			}
			return null;
		}
	}


	private Integer id;
	private String description;
	private String reaction;
	private Integer typeCode;
	private Long ageOfOnset;
	private String lifeStage;
	private REACTION_SEVERITY severityOfReaction;
	private REACTION_ONSET onsetOfReaction;
	private String drugIdentificationNumber;

	private LocalDateTime entryDateTime;
	private PartialDate startDate;

	private Provider provider;
	private String annotation;
	private List<ResidualInfo> residualInfo;
}
