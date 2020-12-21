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
package org.oscarehr.demographicImport.model.allergy;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.model.provider.Provider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Allergy extends AbstractTransientModel
{
	// enum with custom values, for backwards compatibility
	public enum REACTION_ONSET {
		IMMEDIATE(1, "Immediate"),
		GRADUAL(2, "Gradual"),
		SLOW(3, "Slow"),
		UNKNOWN(4, "Unknown")
		;

		private final int onsetCode;
		private final String description;

		REACTION_ONSET(int onsetCode, String description)
		{
			this.onsetCode = onsetCode;
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

		//Lookup table
		private static final Map<Integer, REACTION_ONSET> codeLookup = new HashMap<>();
		private static final Map<String, REACTION_ONSET> descriptionLookup = new HashMap<>();

		//Populate the lookup table on loading time
		static
		{
			for(REACTION_ONSET onset : REACTION_ONSET.values())
			{
				codeLookup.put(onset.getOnsetCode(), onset);
				descriptionLookup.put(onset.getDescription(), onset);
			}
		}

		public static REACTION_ONSET fromCodeString(Integer code)
		{
			if(code != null)
			{
				return codeLookup.get(code);
			}
			return null;
		}
		public static REACTION_ONSET fromDescription(String description)
		{
			if(description != null)
			{
				return descriptionLookup.get(description);
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
	private String severityOfReaction;
	private REACTION_ONSET onsetOfReaction;
	private String drugIdentificationNumber;

	private LocalDateTime entryDateTime;
	private PartialDate startDate;

	private Provider provider;
	private String annotation;
}
