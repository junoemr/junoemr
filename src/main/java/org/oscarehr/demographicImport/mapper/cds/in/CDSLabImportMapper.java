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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.encounterNote.EncounterNote;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.LaboratoryResults;
import xml.cds.v5_0.ResultNormalAbnormalFlag;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
public class CDSLabImportMapper extends AbstractCDSImportMapper<List<LaboratoryResults>, List<Lab>>
{
	public CDSLabImportMapper()
	{
		super();
	}

	@Override
	public List<Lab> importToJuno(List<LaboratoryResults> importLabResultList)
	{
		List<Lab> labList = new ArrayList<>();

		// Group all the results based on labName and accession number
		// results with matching accession number coming from the same lab will be treated as a single lab in Juno
		HashMap<String, List<LaboratoryResults>> groupedLabHash = new HashMap<>();
		for(LaboratoryResults laboratoryResults : importLabResultList)
		{
			String labName = laboratoryResults.getLaboratoryName();
			String accessionNumber = laboratoryResults.getAccessionNumber();
			String hashKey = labName + accessionNumber;

			if(groupedLabHash.containsKey(hashKey))
			{
				groupedLabHash.get(hashKey).add(laboratoryResults);
			}
			else
			{
				List<LaboratoryResults> labGroup = new ArrayList<>();
				labGroup.add(laboratoryResults);
				groupedLabHash.put(hashKey, labGroup);
			}
		}

		// now that the labs are grouped, we can do the conversion
		for(List<LaboratoryResults> labGroup : groupedLabHash.values())
		{
			labList.add(getAsLab(labGroup));
		}

		return labList;
	}

	private Lab getAsLab(List<LaboratoryResults> importLabGroup)
	{
		Lab lab = new Lab();

		lab.setAccessionNumber(importLabGroup.get(0).getAccessionNumber());
		lab.setSendingFacility(importLabGroup.get(0).getLaboratoryName());
		lab.setEmrReceivedDateTime(LocalDateTime.now());
		lab.setMessageDateTime(toNullableLocalDateTime(importLabGroup.get(0).getCollectionDateTime()));

		// now need to group labs on the lab test
		// juno will treat these groups as observations
		HashMap<String, List<LaboratoryResults>> groupedTestHash = new HashMap<>();

		// review hash is an easy way to check for duplicates across multiple lab results
		HashSet<String> uniqueReviewerSet = new HashSet<>();

		for(LaboratoryResults laboratoryResults : importLabGroup)
		{
			String testName = laboratoryResults.getTestNameReportedByLab();
			if(groupedTestHash.containsKey(testName))
			{
				groupedTestHash.get(testName).add(laboratoryResults);
			}
			else
			{
				List<LaboratoryResults> labGroup = new ArrayList<>();
				labGroup.add(laboratoryResults);
				groupedTestHash.put(testName, labGroup);
			}

			// add all unique reviewers
			for(LaboratoryResults.ResultReviewer importReviewer : laboratoryResults.getResultReviewer())
			{
				// use names not ohip number for check since names are required
				String reviewerKey = importReviewer.getName().getFirstName() + importReviewer.getName().getLastName();
				if(!uniqueReviewerSet.contains(reviewerKey))
				{
					Reviewer reviewer = new Reviewer();
					reviewer.setReviewDateTime(toNullablePartialDateTime(importReviewer.getDateTimeResultReviewed()));
					reviewer.setFirstName(importReviewer.getName().getFirstName());
					reviewer.setLastName(importReviewer.getName().getLastName());
					reviewer.setOhipNumber(importReviewer.getOHIPPhysicianId());

					lab.addReviewer(reviewer);
					uniqueReviewerSet.add(reviewerKey);
				}
			}
		}

		// now that the labs are grouped, we can do the conversion
		for(List<LaboratoryResults> labGroup : groupedTestHash.values())
		{
			lab.getLabObservationList().add(getAsLabObservation(labGroup));
		}

		return lab;
	}

	private LabObservation getAsLabObservation(List<LaboratoryResults> importLabGroup)
	{
		LabObservation labObservation = new LabObservation();
		LaboratoryResults result0 = importLabGroup.get(0); //they should all be the same, so use the first one
		labObservation.setName(result0.getTestName());
		labObservation.setRequestDateTime(toNullableLocalDateTime(result0.getLabRequisitionDateTime()));
		labObservation.setObservationDateTime(toNullableLocalDateTime(result0.getCollectionDateTime()));
		labObservation.setReportStatus(getReportStatusEnum(result0));

		for(LaboratoryResults importLabResults : importLabGroup)
		{
			LabObservationResult result = new LabObservationResult();
			result.setName(importLabResults.getTestNameReportedByLab());
			result.setIdentifier(importLabResults.getLabTestCode());
			result.setObservationDateTime(toNullableLocalDateTime(importLabResults.getCollectionDateTime()));

			LaboratoryResults.Result labResult = importLabResults.getResult();
			if(labResult != null)
			{
				result.setValue(labResult.getValue());
				result.setUnits(labResult.getUnitOfMeasure());
			}

			result.setRange(getReferenceRange(importLabResults.getReferenceRange()));
			result.setAbnormal(getAbnormalFlag(importLabResults.getResultNormalAbnormalFlag()));

			//TODO should these go elsewhere?
			String testResultInfoFromLab = importLabResults.getTestResultsInformationReportedByTheLab();
			if(testResultInfoFromLab != null)
			{
				result.addComment(testResultInfoFromLab);
			}
			String labNotes = importLabResults.getNotesFromLab();
			if(labNotes != null)
			{
				result.addComment(labNotes);
			}

			result.setResultStatus(importLabResults.getTestResultStatus());

			String physiciansNotes = StringUtils.trimToNull(importLabResults.getPhysiciansNotes());
			if(physiciansNotes != null)
			{
				EncounterNote annotation = new EncounterNote();
				annotation.setNoteText(physiciansNotes);
				result.setAnnotation(annotation);
			}
			//TODO blocked result?

			labObservation.addResult(result);
		}

		return labObservation;
	}

	protected Hl7TextInfo.REPORT_STATUS getReportStatusEnum(LaboratoryResults laboratoryResults)
	{
		String statusStr = laboratoryResults.getTestResultStatus();
		if(EnumUtils.isValidEnum(Hl7TextInfo.REPORT_STATUS.class, statusStr))
		{
			return Hl7TextInfo.REPORT_STATUS.valueOf(statusStr);
		}
		else
		{
			// TODO could map additional statuses here manually to the enum
			return Hl7TextInfo.REPORT_STATUS.F; // unknown status is final, since labs will never get updates, are probably old.
		}
	}

	protected String getReferenceRange(LaboratoryResults.ReferenceRange referenceRange)
	{
		String rangeText = null;
		if(referenceRange != null)
		{
			String lowLimit = referenceRange.getLowLimit();
			String highLimit = referenceRange.getLowLimit();
			if(lowLimit != null || highLimit != null)
			{
				rangeText = StringUtils.trimToEmpty(lowLimit) + "-" + StringUtils.trimToEmpty(highLimit);
			}
			else
			{
				rangeText = referenceRange.getReferenceRangeText();
			}
		}
		return rangeText;
	}

	protected Boolean getAbnormalFlag(ResultNormalAbnormalFlag abnormalFlag)
	{
		Boolean flag;
		if(abnormalFlag == null || CDSConstants.LAB_ABNORMAL_FLAG.U.name().equals(abnormalFlag.getResultNormalAbnormalFlagAsEnum()))
		{
			flag = null;
		}
		else
		{
			flag = CDSConstants.LAB_ABNORMAL_FLAG.Y.name().equals(abnormalFlag.getResultNormalAbnormalFlagAsEnum())
					|| getAbnormalFlagFromText(abnormalFlag.getResultNormalAbnormalFlagAsPlainText());
		}
		return flag;
	}
	protected boolean getAbnormalFlagFromText(String abnormalFlag)
	{
		boolean flag = false;
		if(abnormalFlag != null)
		{
			flag = CDSConstants.LAB_ABNORMAL_FLAG.Y.name().equals(abnormalFlag);
			//TODO handle additional free text abnormal values
		}
		return flag;
	}
}
