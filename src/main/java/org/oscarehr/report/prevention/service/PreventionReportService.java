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
package org.oscarehr.report.prevention.service;

import org.oscarehr.util.LoggedInInfo;
import org.springframework.stereotype.Service;
import org.oscarehr.report.prevention.model.PreventionReportModel;
import oscar.oscarPrevention.pageUtil.PreventionReportDisplay;
import oscar.oscarPrevention.reports.PreventionReport;
import oscar.oscarPrevention.reports.PreventionReportFactory;
import oscar.oscarReport.data.RptDemographicQueryBuilder;
import oscar.oscarReport.data.RptDemographicQueryLoader;
import oscar.oscarReport.pageUtil.RptDemographicReportForm;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

@Service
public class PreventionReportService
{
	/**
	 * Run the prevention report.
	 * built on legacy structures
	 */
	public PreventionReportModel runPreventionReport(LoggedInInfo loggedInInfo, String setName, Date asofDate, PreventionReport.PreventionReportType prevention)
	{
		String dateString = ConversionUtils.toDateString(asofDate);

		RptDemographicReportForm frm = new RptDemographicReportForm();
		frm.setSavedQuery(setName);
		RptDemographicQueryLoader demoL = new RptDemographicQueryLoader();
		frm = demoL.queryLoader(frm);
		frm.addDemoIfNotPresent();
		frm.setAsofDate(dateString);
		RptDemographicQueryBuilder demoQ = new RptDemographicQueryBuilder();


		PreventionReport report = PreventionReportFactory.getPreventionReport(prevention);
		ArrayList<ArrayList<String>> list = demoQ.buildQuery(loggedInInfo, frm, dateString);

		Hashtable<String, Object> hash = report.runReport(loggedInInfo, list, asofDate);

		PreventionReportModel model = hashToModel(hash);
		if (report.displayNumShots())
		{
			model.setReportType("yes");
		}
		model.setAsOfDateTime(asofDate);
		model.setPreventionType(prevention.name());
		model.setPrevention(prevention.name());
		model.setPatientSet(setName);

		return model;
	}

	private PreventionReportModel hashToModel(Hashtable<String, Object> h)
	{
		PreventionReportModel model = new PreventionReportModel();
		model.setUpToDate( (String) h.get("up2date"));
		model.setPercent( (String) h.get("percent"));
		model.setPercentWithGrace((String) h.get("percentWithGrace"));
		model.setInEligible( (String) h.get("inEligible"));
		model.setEformSearch( (String) h.get("eformSearch"));
		model.setFollowUpType( (String) h.get("followUpType"));
		model.setBillCode( (String) h.get("BillCode"));
		model.setReturnReport((List<PreventionReportDisplay>) h.get("returnReport"));
		return model;
	}
}
