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
package org.oscarehr.dataMigration.model.hrm;

import lombok.Data;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.model.provider.Reviewer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE;

@Data
public class HrmDocument extends AbstractTransientModel
{
	public enum REPORT_STATUS
	{
		SIGNED("S"),
		CANCELLED("C");

		private final String value;

		REPORT_STATUS(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static REPORT_STATUS fromValueString(String value)
		{
			for(REPORT_STATUS status : REPORT_STATUS.values())
			{
				if(status.getValue().equalsIgnoreCase(value))
				{
					return status;
				}
			}
			return null;
		}
	}

	public enum REPORT_CLASS
	{
		DIAGNOSTIC_IMAGING("Diagnostic Imaging Report"),
		DIAGNOSTIC_TEST("Diagnostic Test Report"),
		CARDIO_RESPIRATORY("Cardio Respiratory Report"),
		MEDICAL_RECORDS("Medical Records Report"),
		CONSULTANT("Consultant Report"),
		LAB("Lab Report"),
		OTHER("Other Letter");

		private final String value;

		REPORT_CLASS(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static REPORT_CLASS fromValueString(String value)
		{
			for(REPORT_CLASS status : REPORT_CLASS.values())
			{
				if(status.getValue().equalsIgnoreCase(value))
				{
					return status;
				}
			}
			if(DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE.equalsIgnoreCase(value))
			{
				return REPORT_CLASS.MEDICAL_RECORDS;
			}
			return null;
		}
	}

	private Integer id;
	private String messageUniqueId;
	private String deliverToUserId;
	
	private LocalDateTime reportDateTime;
	private LocalDateTime receivedDateTime;
	private Provider createdBy;

	private String description;
	private REPORT_CLASS reportClass;
	private String reportSubClass;
	private REPORT_STATUS reportStatus;
	private GenericFile reportFile;
	private String reportFileSchemaVersion;
	private Document document;
	private String sourceFacility;
	private String sendingFacilityId;
	private String sendingFacilityReport;
	private HrmDocument parentReport;
	private HrmCategory category;
	private HrmDocumentMatchingData hashData;

	private List<HrmComment> comments;
	private List<HrmObservation> observations;
	private List<Reviewer> internalReviewers;       // TODO:  Confirm that these are the internal reviewers

	public HrmDocument()
	{
		comments = new ArrayList<>();
		observations = new ArrayList<>();
		internalReviewers = new ArrayList<>();
	}

	public void addComment(HrmComment comment)
	{
		if(this.comments == null)
		{
			this.comments = new ArrayList<>();
		}
		this.comments.add(comment);
	}

	public void addObservation(HrmObservation observation)
	{
		if(this.observations == null)
		{
			this.observations = new ArrayList<>();
		}
		this.observations.add(observation);
	}

	public void addReviewer(Reviewer reviewer)
	{
		if(this.internalReviewers == null)
		{
			this.internalReviewers = new ArrayList<>();
		}
		this.internalReviewers.add(reviewer);
	}
}
