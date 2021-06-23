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

package org.oscarehr.billing.CA.service;

import org.oscarehr.PMmodule.service.ProviderManager;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderPreference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.util.StringUtils;

import java.util.Iterator;
import java.util.Set;

@Service
public class BillingUrlService
{
	@Autowired
	private ProviderManager providerManager;

	@Autowired
	private ProviderPreferenceDao providerPreferenceDao;

	@Autowired
	private DemographicDao demographicDao;

	private oscar.OscarProperties oscarProperties = oscar.OscarProperties.getInstance();

	public String buildUrl(
			String providerNo,
			String demographicNo,
			String region,
			String appointmentNo,
			String demographicDisplayName,
			String date,
			String startTime,
			String appointmentProviderNo,
			String reviewProviderNo,
			CaseManagementNote caseNote
	)
	{
		String providerview = null;
		if(reviewProviderNo != null)
		{
			Provider p = providerManager.getProvider(reviewProviderNo);
			if( p.getProviderType().equalsIgnoreCase("nurse") )
			{
				providerview = "000000";
			}
			else
			{
				providerview = reviewProviderNo;
			}
		}
		else
		{
			providerview = providerNo;
		}

		String defaultView = OscarProperties.getInstance().getProperty("default_view", "");

		Demographic demographic = demographicDao.getDemographic(demographicNo);

		// Should we get the billform based on the appointment provider or the demographic's provider?
		ProviderPreference providerPreference = null;
		if (demographic.getProviderNo() != null)
		{
			providerPreference = providerPreferenceDao.find(demographic.getProviderNo());
		}

		if (providerPreference != null &&
				providerPreference.getDefaultServiceType() != null &&
				!providerPreference.getDefaultServiceType().equals("no"))
		{
			defaultView = providerPreference.getDefaultServiceType();
		}

		StringBuilder dxCodes = new StringBuilder();
		if(caseNote != null)
		{
			Set setIssues = caseNote.getIssues();
			Iterator iter = setIssues.iterator();
			String strDxCode;
			int dxNum = 0;
			while (iter.hasNext())
			{
				CaseManagementIssue cIssue = (CaseManagementIssue) iter.next();
				dxCodes.append("&dxCode");
				strDxCode = String.valueOf(cIssue.getIssue().getCode());
				if (strDxCode.length() > 3)
				{
					strDxCode = strDxCode.substring(0, 3);
				}

				if (dxNum > 0)
				{
					dxCodes.append(String.valueOf(dxNum));
				}

				dxCodes.append("=" + StringUtils.encodeUrlParam(strDxCode));
				++dxNum;
			}
		}

		String contextPath = oscarProperties.getProjectHome();
		String url = "/" + contextPath + "/billing.do?billRegion=" + StringUtils.encodeUrlParam(region)
				+ "&billForm=" + StringUtils.encodeUrlParam(defaultView)
				+ "&hotclick="
				+ "&appointment_no=" + StringUtils.encodeUrlParam(appointmentNo)
				+ "&demographic_name=" + StringUtils.encodeUrlParam(demographicDisplayName)
				+ "&amp;status=t&demographic_no=" + StringUtils.encodeUrlParam(demographicNo)
				+ "&providerview=" + StringUtils.encodeUrlParam(providerview)
				+ "&user_no=" + StringUtils.encodeUrlParam(providerNo)
				+ "&apptProvider_no=" + StringUtils.encodeUrlParam(appointmentProviderNo)
				+ "&appointment_date=" + StringUtils.encodeUrlParam(date)
				+ "&start_time=" + StringUtils.encodeUrlParam(startTime)
				+ "&bNewForm=1"
				+ dxCodes.toString();

		if(OscarProperties.getInstance().isPropertyActive("auto_populate_billingreferral_bc")
				&& demographic.getFamilyDoctor() != null)
		{
			url += "&referral_no_1=" + getRefNo(demographic.getFamilyDoctor());
		}

		return url;
	}

	public String getRefNo(String referal)
	{
		if (referal == null) return "";
		int start = referal.indexOf("<rdohip>");
		int end = referal.indexOf("</rdohip>");
		String ref = new String();

		if (start >= 0 && end >= 0) {
			String subreferal = referal.substring(start + 8, end);
			if (!"".equalsIgnoreCase(subreferal.trim())) {
				ref = subreferal;

			}
		}
		return ref;
	}
}
