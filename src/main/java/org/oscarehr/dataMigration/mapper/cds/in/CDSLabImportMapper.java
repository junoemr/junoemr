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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.model.Hl7TextInfo;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Component
public class CDSLabImportMapper extends AbstractCDSImportMapper<List<LaboratoryResults>, List<Lab>>
{
	public static final String UNKNOWN_OBR_NAME = "UNKNOWN TEST TYPE";
	public static final String UNKNOWN_OBX_ID = "UNKNOWN_ID";
	public static final String UNKNOWN_OBX_NAME = "UNKNOWN TEST NAME";

	public CDSLabImportMapper()
	{
		super();
	}

	@Override
	public List<Lab> importToJuno(List<LaboratoryResults> importLabResultList)
	{
		List<Lab> labList = new ArrayList<>();
		int nonce = 0;

		// Group all the results based on labName and accession number
		// results with matching accession number coming from the same lab will be treated as a single lab in Juno
		HashMap<String, List<LaboratoryResults>> groupedLabHash = new HashMap<>();
		for(LaboratoryResults laboratoryResults : importLabResultList)
		{
			String labName = laboratoryResults.getLaboratoryName();
			String accessionNumber = laboratoryResults.getAccessionNumber();

			String hashKey;
			if(StringUtils.trimToNull(accessionNumber) == null)
			{
				// use the nonce to ensure unique keys if there is no accession number.
				// null accession labs should not be grouped together.
				hashKey = labName + nonce;
				nonce++;
				logEvent(labName + " lab is missing accession number. A generated accession number will be assigned");
			}
			else
			{
				hashKey = labName + accessionNumber;
			}

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

		lab.setAccessionNumber(StringUtils.trimToNull(importLabGroup.get(0).getAccessionNumber()));
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
				if (importReviewer == null)
				{
					continue;
				}
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
		labObservation.setName(getOBRName(result0));
		labObservation.setRequestDateTime(toNullableLocalDateTime(result0.getLabRequisitionDateTime()));
		labObservation.setObservationDateTime(toNullableLocalDateTime(result0.getCollectionDateTime()));
		labObservation.setReportStatus(getReportStatusEnum(result0));

		for(LaboratoryResults importLabResults : importLabGroup)
		{
			LabObservationResult result = new LabObservationResult();
			result.setName(getOBXName(importLabResults));
			result.setIdentifier(getOBXId(importLabResults));
			result.setObservationDateTime(toNullableLocalDateTime(importLabResults.getCollectionDateTime()));

			LaboratoryResults.Result labResult = importLabResults.getResult();
			if(labResult != null)
			{
				result.setValue(labResult.getValue());
				result.setUnits(labResult.getUnitOfMeasure());
			}

			result.setRange(getReferenceRange(importLabResults.getReferenceRange()));
			result.setAbnormal(getAbnormalFlag(importLabResults.getResultNormalAbnormalFlag()));

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
			// if any lab result is blocked, the observation is blocked (OLIS feature)
			labObservation.setBlockedResult(labObservation.isBlockedResult() || (importLabResults.getBlockedTestResult() == YIndicator.Y));

			labObservation.addResult(result);
		}

		return labObservation;
	}

	private String getOBRName(LaboratoryResults importLabResults)
	{
		String name = StringUtils.trimToNull(importLabResults.getTestName());
		String nameReportedByLab = StringUtils.trimToNull(importLabResults.getTestNameReportedByLab());

		if(name != null )
		{
			return name;
		}
		else if (nameReportedByLab != null)
		{
			return nameReportedByLab;
		}
		else
			return UNKNOWN_OBR_NAME;
	}

	private String getOBXName(LaboratoryResults importLabResults)
	{
		String name = StringUtils.trimToNull(importLabResults.getTestNameReportedByLab());
		String testCode = StringUtils.trimToNull(importLabResults.getLabTestCode());
		/*
		Return "{testCode} : {name}" if both exist
		else return "{testCode}" or "{name}" if one exists.
		If neither exist, return UNKNOWN_OBX_NAME
		 */
		if (name != null && testCode != null)
		{
			return testCode + " : " + name;
		}
		else if (name == null && testCode != null)
		{
			return testCode;
		}
		else if (name != null)
		{
			return name;
		}
		return UNKNOWN_OBX_NAME;
	}

	private String getOBXId(LaboratoryResults importLabResults)
	{
		String id = StringUtils.trimToNull(importLabResults.getLabTestCode());
		if(id != null)
		{
			// This is the ideal outcome, where the other EMR gave us a LabTestCode
			return id;
		}

		String name = StringUtils.trimToNull(importLabResults.getTestNameReportedByLab());
		if (name != null)
		{
			// Less ideal, use the TestNameReportedByLab as the LabTestCode if it exists
			return name;
		}

		// Least ideal, generate a unique LabTestCode
		// The UUID ensures that each test is considered unique and won't be grouped together with
		// other labs missing the ID when viewing /lab/CA/ON/labValues.jsp
		return UNKNOWN_OBX_ID + UUID.randomUUID().toString().replace("-", "");
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
			//map additional statuses here manually to the enum
			return Hl7TextInfo.REPORT_STATUS.F; // unknown status is final, since labs will never get updates, are probably old.
		}
	}

	protected String getReferenceRange(LaboratoryResults.ReferenceRange referenceRange)
	{
		String rangeText = null;
		if(referenceRange != null)
		{
			String lowLimit = referenceRange.getLowLimit();
			String highLimit = referenceRange.getHighLimit();
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
		if(abnormalFlag == null || CDSConstants.LabAbnormalFlag.UNKNOWN.getValue().equals(abnormalFlag.getResultNormalAbnormalFlagAsEnum()))
		{
			flag = null;
		}
		else
		{
			CDSConstants.LabAbnormalFlag enumValue = CDSConstants.LabAbnormalFlag.fromValue(abnormalFlag.getResultNormalAbnormalFlagAsEnum());
			flag = (enumValue != null && enumValue.isAbnormal()) || getAbnormalFlagFromText(abnormalFlag.getResultNormalAbnormalFlagAsPlainText());
		}
		return flag;
	}
	protected boolean getAbnormalFlagFromText(String abnormalFlag)
	{
		boolean flag = false;
		if(abnormalFlag != null)
		{
			flag = CDSConstants.LabAbnormalFlag.ABNORMAL.getValue().equals(abnormalFlag);
			//handle additional free text abnormal values here
		}
		return flag;
	}
}
