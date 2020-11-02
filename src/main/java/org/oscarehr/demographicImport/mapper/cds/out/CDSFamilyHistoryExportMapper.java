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

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.FamilyHistory;
import org.oscarehr.common.xml.cds.v5_0.model.LifeStage;
import org.oscarehr.demographicImport.model.encounterNote.FamilyHistoryNote;
import oscar.util.ConversionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

public class CDSFamilyHistoryExportMapper extends AbstractCDSExportMapper<FamilyHistory, FamilyHistoryNote>
{
	public CDSFamilyHistoryExportMapper()
	{
		super();
	}

	@Override
	public FamilyHistory exportFromJuno(FamilyHistoryNote exportStructure)
	{
		FamilyHistory familyHistory = objectFactory.createFamilyHistory();

		familyHistory.setNotes(exportStructure.getNoteText());
		familyHistory.setLifeStage(getLifeStage(exportStructure));
		familyHistory.setAgeAtOnset(getAgeAtOnset(exportStructure));
		familyHistory.setTreatment(exportStructure.getTreatment());
		familyHistory.setRelationship(exportStructure.getRelationship());

		XMLGregorianCalendar startDate = ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getStartDate());
		if(startDate != null)
		{
			DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
			dateFullOrPartial.setFullDate(startDate);
			familyHistory.setStartDate(dateFullOrPartial);
		}


		return familyHistory;
	}

	protected BigInteger getAgeAtOnset(FamilyHistoryNote exportStructure)
	{
		Long onsetAge = exportStructure.getAgeAtOnset();
		if(onsetAge != null)
		{
			return BigInteger.valueOf(onsetAge);
		}
		return null;
	}

	protected LifeStage getLifeStage(FamilyHistoryNote exportStructure)
	{
		String lifeStage = exportStructure.getLifeStage();
		if(EnumUtils.isValidEnum(LifeStage.class, lifeStage))
		{
			return LifeStage.fromValue(lifeStage);
		}
		return null;
	}
}
