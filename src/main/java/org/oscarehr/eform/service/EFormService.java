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

package org.oscarehr.eform.service;

import org.oscarehr.casemgmt.service.MultiSearchResult;
import org.oscarehr.casemgmt.service.impl.DefaultMultiSearchResult;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("service_EFormService")
public class EFormService
{
	@Autowired
	private EFormDao eFormDao;

	public List<MultiSearchResult> getEFormsForSearch(String contextPath, Integer demographicNo, String appointmentNo)
	{
		List<EForm> allEForms = eFormDao.findAll(true);

		List<MultiSearchResult> searchResults = new ArrayList<>();

		for(EForm eform: allEForms)
		{
			MultiSearchResult searchResult = new DefaultMultiSearchResult();

			searchResult.setText(eform.getFormName() + " (new)");
			searchResult.setOnClick(getEformSearchOnClickUrl(
					contextPath,
					eform.getId(),
					eform.getFormName(),
					demographicNo,
					appointmentNo));

			searchResults.add(searchResult);
		}

		return searchResults;
	}

	private String getEformSearchOnClickUrl(String contextPath, Integer formId, String formName,
											Integer demographicNo, String appointmentNo)
	{
		String winName = formName + demographicNo;
		int hash = Math.abs(winName.hashCode());

		String url = contextPath + "/eform/efmformadd_data.jsp" +
				"?fid=" + UrlUtils.encodeUrlParam(formId.toString()) +
				"&demographic_no=" + UrlUtils.encodeUrlParam(demographicNo.toString()) +
				"&appointment=" + UrlUtils.encodeUrlParam(appointmentNo) +
				"&parentAjaxId=eforms";

		return "popupPage( 700, 800, '" + hash + "', '" + url +"');";
	}
}
