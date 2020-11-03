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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.PersonalHistory;
import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.demographicImport.model.encounterNote.SocialHistoryNote;
import oscar.util.ConversionUtils;

import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_NOTE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_OBS_DATE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_START_DATE;

public class CDSPersonalHistoryImportMapper extends AbstractCDSImportMapper<PersonalHistory, SocialHistoryNote>
{
	public CDSPersonalHistoryImportMapper()
	{
		super();
	}

	@Override
	public SocialHistoryNote importToJuno(PersonalHistory importStructure)
	{
		SocialHistoryNote note = new SocialHistoryNote();

		for(ResidualInformation.DataElement dataElement : importStructure.getResidualInfo().getDataElement())
		{
			String name = dataElement.getName();
			String content = dataElement.getContent();
			String type = dataElement.getDataType();

			//TODO how to handle types? do we need/want to check them?
			if(name != null)
			{
				switch(name)
				{
					case RESIDUAL_INFO_DATA_NAME_NOTE:
					{
						note.setNoteText(content); break;
					}
					case RESIDUAL_INFO_DATA_NAME_OBS_DATE:
					{
						note.setObservationDate(ConversionUtils.toLocalDateTime(content)); break;
					}
					case RESIDUAL_INFO_DATA_NAME_START_DATE:
					{
						note.setStartDate(ConversionUtils.toLocalDate(content)); break;
					}
					case RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE:
					{
						note.setResolutionDate(ConversionUtils.toLocalDate(content)); break;
					}
				}
			}
		}

		return note;
	}
}
