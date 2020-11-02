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

public class CDSPersonalHistoryExportMapper extends AbstractCDSExportMapper<PersonalHistory, SocialHistoryNote>
{
	public enum DATA_TYPE
	{
		TEXT,
		NUMERIC,
		DATE,
		TIME,
		DATETIME,
	}

	public String ELEMENT_NAME_NOTE = "Note";
	public String ELEMENT_NAME_OBS_DATE = "Observation Datetime";
	public String ELEMENT_NAME_START_DATE = "Start Date";
	public String ELEMENT_NAME_RESOLVE_DATE = "Resolution Date";

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
				DATA_TYPE.TEXT,
				ELEMENT_NAME_NOTE,
				exportStructure.getNoteText());
		addNonNullDataElements(
				residualInformation,
				DATA_TYPE.DATETIME,
				ELEMENT_NAME_OBS_DATE,
				ConversionUtils.toDateTimeString(exportStructure.getObservationDate()));
		addNonNullDataElements(
				residualInformation,
				DATA_TYPE.DATE,
				ELEMENT_NAME_START_DATE,
				ConversionUtils.toDateString(exportStructure.getStartDate()));
		addNonNullDataElements(
				residualInformation,
				DATA_TYPE.DATE,
				ELEMENT_NAME_RESOLVE_DATE,
				ConversionUtils.toDateString(exportStructure.getResolutionDate()));

		personalHistory.setResidualInfo(residualInformation);
		return personalHistory;
	}

	protected void addNonNullDataElements(ResidualInformation residualInformation, DATA_TYPE dataType, String name, String value)
	{
		if(value != null && !value.isEmpty())
		{
			residualInformation.getDataElement().add(createDataElement(dataType, name, value));
		}
	}

	protected ResidualInformation.DataElement createDataElement(DATA_TYPE dataType, String name, String value)
	{
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setDataType(dataType.name());
		dataElement.setName(name);
		dataElement.setContent(value);

		return dataElement;
	}

}
