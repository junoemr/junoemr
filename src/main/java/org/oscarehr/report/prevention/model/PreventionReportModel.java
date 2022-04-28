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
package org.oscarehr.report.prevention.model;

import lombok.Data;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static oscar.oscarPrevention.reports.PreventionReport.FIRST_LETTER;
import static oscar.oscarPrevention.reports.PreventionReport.OVERDUE;
import static oscar.oscarPrevention.reports.PreventionReport.PHONE_CALL;
import static oscar.oscarPrevention.reports.PreventionReport.REFUSED;
import static oscar.oscarPrevention.reports.PreventionReport.SECOND_LETTER;

@Data
public class PreventionReportModel
{
	private String reportType;
	private Date asOfDateTime;
	private String upToDate;
	private String percent;
	private String percentWithGrace;
	private String inEligible;
	private String eformSearch;
	private String followUpType;
	private String billCode;
	private String preventionType;
	private String patientSet;
	private String prevention;
	private List<PreventionReportDisplay> returnReport;

	public List<Integer> getL1LetterDemographicIds()
	{
		return demographicsByProcedure(FIRST_LETTER);
	}
	public List<Integer> getL2LetterDemographicIds()
	{
		return demographicsByProcedure(SECOND_LETTER);
	}
	public List<Integer> getP1LetterDemographicIds()
	{
		return demographicsByProcedure(PHONE_CALL);
	}
	public List<Integer> getRefusedLetterDemographicIds()
	{
		return demographicsByState(REFUSED);
	}
	public List<Integer> getOverdueLetterDemographicIds()
	{
		return demographicsByState(OVERDUE);
	}

	private List<Integer> demographicsByProcedure(String procedure)
	{
		return returnReport.stream()
				.filter((display) -> procedure.equals(display.nextSuggestedProcedure))
				.map((display) -> display.demographicNo)
				.collect(Collectors.toList());
	}

	private List<Integer> demographicsByState(String state)
	{
		return returnReport.stream()
				.filter((display) -> state.equals(display.state))
				.map((display) -> display.demographicNo)
				.collect(Collectors.toList());
	}
}
