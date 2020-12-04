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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.oscarehr.common.xml.cds.v5_0.model.PersonalHistory;
import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.demographicImport.model.encounterNote.SocialHistoryNote;
import oscar.util.ConversionUtils;

import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_ANNOTATION;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_NOTE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_OBS_DATE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_PROVIDER;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_NAME_START_DATE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.RESIDUAL_INFO_DATA_TYPE;

public class CDSPersonalHistoryExportMapper extends AbstractCDSNoteExportMapper<PersonalHistory, SocialHistoryNote>
{


	public CDSPersonalHistoryExportMapper()
	{
		super();
	}

	@Override
	public PersonalHistory exportFromJuno(SocialHistoryNote exportStructure)
	{
		PersonalHistory personalHistory = objectFactory.createPersonalHistory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.TEXT,
				RESIDUAL_INFO_DATA_NAME_NOTE,
				exportStructure.getNoteText());
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.DATETIME,
				RESIDUAL_INFO_DATA_NAME_OBS_DATE,
				ConversionUtils.toDateTimeString(exportStructure.getObservationDate()));
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.DATE,
				RESIDUAL_INFO_DATA_NAME_START_DATE,
				ConversionUtils.toDateString(exportStructure.getStartDate()));
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.DATE,
				RESIDUAL_INFO_DATA_NAME_RESOLVE_DATE,
				ConversionUtils.toDateString(exportStructure.getResolutionDate()));
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.TEXT,
				RESIDUAL_INFO_DATA_NAME_ANNOTATION,
				exportStructure.getAnnotation());
		addNonNullDataElements(
				residualInformation,
				RESIDUAL_INFO_DATA_TYPE.TEXT,
				RESIDUAL_INFO_DATA_NAME_PROVIDER,
				exportStructure.getProvider().getLastName() + "," + exportStructure.getProvider().getFirstName());

		personalHistory.setResidualInfo(residualInformation);
		return personalHistory;
	}
}
