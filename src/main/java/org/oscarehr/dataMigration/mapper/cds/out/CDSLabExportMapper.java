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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.model.lab.LabObservation;
import org.oscarehr.dataMigration.model.lab.LabObservationResult;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.LaboratoryResults;
import xml.cds.v5_0.ResultNormalAbnormalFlag;
import xml.cds.v5_0.YIndicator;

import java.util.ArrayList;
import java.util.List;

@Component
public class CDSLabExportMapper extends AbstractCDSExportMapper<List<LaboratoryResults>, Lab>
{
	public CDSLabExportMapper()
	{
		super();
	}

	@Override
	public List<LaboratoryResults> exportFromJuno(Lab exportLab)
	{
		List<LaboratoryResults> laboratoryResults = new ArrayList<>();
		for(LabObservation labObr : exportLab.getLabObservationList())
		{
			for(LabObservationResult labObrResult : labObr.getResults())
			{
				LaboratoryResults laboratoryResult = objectFactory.createLaboratoryResults();

				laboratoryResult.setLaboratoryName(exportLab.getSendingFacility());
				laboratoryResult.setTestNameReportedByLab(labObrResult.getName());
				laboratoryResult.setLabTestCode(labObrResult.getIdentifier());
				laboratoryResult.setTestName(labObr.getName());
				laboratoryResult.setAccessionNumber(exportLab.getAccessionNumber());

				String resultValue = labObrResult.getValue();
				if(resultValue != null)
				{
					LaboratoryResults.Result result = objectFactory.createLaboratoryResultsResult();
					result.setValue(resultValue);
					result.setUnitOfMeasure(labObrResult.getUnits());
					laboratoryResult.setResult(result);
				}
				
				String range = labObrResult.getRange();
				LaboratoryResults.ReferenceRange referenceRange = objectFactory.createLaboratoryResultsReferenceRange();
				String[] splitRange = splitLabReferenceRange(range);
				
				// If we can split the range, the set low and high limits.
				if (splitRange != null)
				{
					referenceRange.setLowLimit(splitRange[0]);
					referenceRange.setHighLimit(splitRange[1]);
				}
				else
				{
					referenceRange.setReferenceRangeText(range);
				}
				
				laboratoryResult.setReferenceRange(referenceRange);

				laboratoryResult.setLabRequisitionDateTime(toNullableDateTimeFullOrPartial(labObr.getRequestDateTime()));
				laboratoryResult.setCollectionDateTime(toNullableDateTimeFullOrPartial(exportLab.getMessageDateTime()));

				for(Reviewer reviewProvider : exportLab.getReviewers())
				{
					LaboratoryResults.ResultReviewer resultReviewer = objectFactory.createLaboratoryResultsResultReviewer();
					resultReviewer.setName(toPersonNameSimple(reviewProvider));
					resultReviewer.setOHIPPhysicianId(reviewProvider.getOhipNumber());
					resultReviewer.setDateTimeResultReviewed(toNullableDateTimeFullOrPartial(reviewProvider.getReviewDateTime()));

					laboratoryResult.getResultReviewer().add(resultReviewer);
				}

				laboratoryResult.setResultNormalAbnormalFlag(getAbnormalFlag(labObrResult.getAbnormal()));
				laboratoryResult.setTestResultsInformationReportedByTheLab(getLabObrComments(labObr));
				laboratoryResult.setNotesFromLab(getLabObxComments(labObrResult));
				laboratoryResult.setPhysiciansNotes(getPhysiciansNotes(labObrResult));
				laboratoryResult.setTestResultStatus(labObrResult.getResultStatus());
				laboratoryResult.setBlockedTestResult(labObr.isBlockedResult() ? YIndicator.Y : null);

				laboratoryResults.add(laboratoryResult);
			}
		}

		return laboratoryResults;
	}
	
	/**
	 * Helper function to split the LabObrResult reference range string into an array for low and high
	 * 
	 * @param range a string representing the lab reference range
	 * @return      an array if the reference range can be split, otherwise null
	 */
	private String[] splitLabReferenceRange(String range)
	{
		// If we can split the range, the set high low limit. Else, use text.
		if (range != null && range.matches(".*-.*"))
		{
			String[] splitRange = range.split("-");
			if (splitRange.length == 2)
			{
				splitRange[0] = StringUtils.trimToNull(splitRange[0]);
				splitRange[1] = StringUtils.trimToNull(splitRange[1]);
				return splitRange;
			}
			else if (splitRange.length == 1)
			{
				return new String[] {StringUtils.trimToNull(splitRange[0]), null};
			} 
			return null;
			
		}
		return null;
	}

	private ResultNormalAbnormalFlag getAbnormalFlag(Boolean isAbnormal)
	{
		ResultNormalAbnormalFlag resultNormalAbnormalFlag = objectFactory.createResultNormalAbnormalFlag();

		CDSConstants.LabAbnormalFlag abnormalEnum;
		if(isAbnormal == null)
		{
			abnormalEnum = CDSConstants.LabAbnormalFlag.UNKNOWN;
		}
		else if(isAbnormal)
		{
			abnormalEnum = CDSConstants.LabAbnormalFlag.ABNORMAL;
		}
		else
		{
			abnormalEnum = CDSConstants.LabAbnormalFlag.NORMAL;
		}
		resultNormalAbnormalFlag.setResultNormalAbnormalFlagAsEnum(abnormalEnum.getValue());

		return resultNormalAbnormalFlag;
	}

	protected String getPhysiciansNotes(LabObservationResult labObrResult)
	{
		EncounterNote annotationNote = labObrResult.getAnnotation();
		if(annotationNote != null)
		{
			return annotationNote.getNoteText();
		}
		return null;
	}

	protected String getLabObxComments(LabObservationResult labObrResult)
	{
		List<String> comments = labObrResult.getComments();
		if(comments != null && !comments.isEmpty())
		{
			return String.join("\n", comments);
		}
		return null;
	}
	protected String getLabObrComments(LabObservation labObr)
	{
		List<String> comments = labObr.getComments();
		if(comments != null && !comments.isEmpty())
		{
			return String.join("\n", comments);
		}
		return null;
	}
}
