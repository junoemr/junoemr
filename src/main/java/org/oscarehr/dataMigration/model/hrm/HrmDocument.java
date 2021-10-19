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
	public enum ReportStatus
	{
		SIGNED("S"),
		CANCELLED("C");

		private final String value;

		ReportStatus(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static ReportStatus fromValueString(String value)
		{
			for(ReportStatus status : ReportStatus.values())
			{
				if(status.getValue().equalsIgnoreCase(value))
				{
					return status;
				}
			}
			return null;
		}
	}

	public enum ReportClass
	{
		DIAGNOSTIC_IMAGING("Diagnostic Imaging Report"),
		DIAGNOSTIC_TEST("Diagnostic Test Report"),
		CARDIO_RESPIRATORY("Cardio Respiratory Report"),
		MEDICAL_RECORDS("Medical Records Report"),
		CONSULTANT("Consultant Report"),
		LAB("Lab Report"),
		OTHER("Other Letter");

		private final String value;

		ReportClass(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static ReportClass fromValueString(String value)
		{
			for(ReportClass status : ReportClass.values())
			{
				if(status.getValue().equalsIgnoreCase(value))
				{
					return status;
				}
			}
			if(DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE.equalsIgnoreCase(value))
			{
				return ReportClass.MEDICAL_RECORDS;
			}
			return null;
		}
	}
	
	public enum DeliveryPrefix
	{
		DOCTOR ("D"),
		NURSE ("N");
		
		private final String prefix;
		
		DeliveryPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		public String getPrefixString()
		{
			return this.prefix;
		}
		
		public static DeliveryPrefix fromString(String prefixString)
		{
			for(DeliveryPrefix deliveryPrefix : DeliveryPrefix.values())
			{
				if(deliveryPrefix.getPrefixString().equalsIgnoreCase(prefixString))
				{
					return deliveryPrefix;
				}
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
	private ReportClass reportClass;
	private String reportSubClass;
	private ReportStatus reportStatus;
	private GenericFile reportFile;
	private String reportFileSchemaVersion;
	private Document document;
	private String sendingFacility;
	private String sendingFacilityId;
	private String sendingFacilityReport;
	private HrmDocument parentReport;
	private HrmCategory category;
	private HrmDocumentMatchingData hashData;

	private List<HrmComment> comments;
	private List<HrmObservation> observations;
	private List<Reviewer> reviewers;

	public HrmDocument()
	{
		comments = new ArrayList<>();
		observations = new ArrayList<>();
		reviewers = new ArrayList<>();
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

	public void addReviewers(List<Reviewer> reviewers)
	{
		if (this.reviewers == null)
		{
			this.reviewers = new ArrayList<>();
		}

		this.reviewers.addAll(reviewers);
	}
}