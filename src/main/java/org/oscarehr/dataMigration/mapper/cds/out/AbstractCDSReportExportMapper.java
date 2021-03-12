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

import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ReportClass;
import xml.cds.v5_0.Reports;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE;

@Component
public abstract class AbstractCDSReportExportMapper<E> extends AbstractCDSExportMapper<Reports, E>
{
	public AbstractCDSReportExportMapper()
	{
		super();
	}

	protected ReportClass toReportClass(String docClass)
	{
		ReportClass reportClass = ReportClass.OTHER_LETTER;
		try
		{
			if(docClass != null)
			{
				// handle small discrepancy case
				if(DOC_CLASS_MEDICAL_RECORDS_LEGACY_VALUE.equalsIgnoreCase(docClass))
				{
					reportClass = ReportClass.MEDICAL_RECORDS_REPORT;
				}
				else
				{
					reportClass = ReportClass.fromValue(docClass);
				}
			}
		}
		catch(IllegalArgumentException e)
		{
			logEvent("Invalid document class value: " + docClass);
		}
		return reportClass;
	}

	protected List<Reports.ReportReviewed> getReportReviewedList(Reviewer ... reviewers)
	{
		List<Reports.ReportReviewed> reviewedList = new ArrayList<>();
		if(reviewers != null)
		{
			for(Reviewer reviewer : reviewers)
			{
				if(reviewer != null)
				{
					Reports.ReportReviewed reportReviewed = objectFactory.createReportsReportReviewed();
					reportReviewed.setName(toPersonNameSimple(reviewer));
					reportReviewed.setDateTimeReportReviewed(toNullableDateFullOrPartial(reviewer.getReviewDateTime()));
					reportReviewed.setReviewingOHIPPhysicianId(reviewer.getOhipNumber());

					reviewedList.add(reportReviewed);
				}
			}
		}
		return reviewedList;
	}
}
