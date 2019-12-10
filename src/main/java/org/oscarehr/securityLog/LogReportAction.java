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
package org.oscarehr.securityLog;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.OscarLogDao;
import org.oscarehr.common.model.OscarLog;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.securityLog.search.SecurityLogCriteriaSearch;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LogReportAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static OscarLogDao oscarLogDao = SpringUtils.getBean(OscarLogDao.class);
	private static ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);

	public static final Integer DEFAULT_PAGE = 1;
	public static final Integer DEFAULT_PAGE_LIMIT = 1000;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String startDateStr = StringUtils.trimToNull(request.getParameter("startDate"));
		String endDateStr = StringUtils.trimToNull(request.getParameter("endDate"));
		String demographicNoStr = StringUtils.trimToNull(request.getParameter("demographicNo"));
		String providerNo = StringUtils.trimToNull(request.getParameter("providerNo"));
		String contentType = StringUtils.trimToNull(request.getParameter("contentType"));
		String actionType = StringUtils.trimToNull(request.getParameter("actionType"));
		boolean restrictResultsBySite = Boolean.parseBoolean(request.getParameter("restrictBySite"));
		String pageNoStr = StringUtils.trimToNull(request.getParameter("page"));
		String perPageStr = StringUtils.trimToNull(request.getParameter("perPage"));

		List<OscarLog> resultList = null;
		Integer total = 0;
		try
		{
			// do some formatting and edge case checking
			LocalDate startDate = ConversionUtils.toNullableLocalDate(startDateStr);
			LocalDate endDate = ConversionUtils.toNullableLocalDate(endDateStr);
			Integer demographicNo = (StringUtils.isNumeric(demographicNoStr))? Integer.parseInt(demographicNoStr): null;
			Integer pageNo = (StringUtils.isNumeric(pageNoStr))? Integer.parseInt(pageNoStr): DEFAULT_PAGE;
			Integer perPage = (StringUtils.isNumeric(pageNoStr))? Integer.parseInt(perPageStr): DEFAULT_PAGE_LIMIT;
			pageNo = (pageNo < 1)? DEFAULT_PAGE : pageNo;

			// set up the search
			SecurityLogCriteriaSearch criteriaSearch = new SecurityLogCriteriaSearch();
			criteriaSearch.setProviderNo(providerNo);
			criteriaSearch.setDemographicId(demographicNo);
			criteriaSearch.setContentType(contentType);
			criteriaSearch.setAction(actionType);
			criteriaSearch.setStartDate(startDate);
			criteriaSearch.setEndDate(endDate);

			criteriaSearch.setOffset(perPage * (pageNo - 1));
			criteriaSearch.setLimit(perPage);
			criteriaSearch.setSortDirDescending();

			// if this flag is set, results need to be limited to providers within the site restrictions
			if(restrictResultsBySite)
			{
				String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
				List<ProviderData> providerList = providerDao.findByProviderSite(loggedInProviderNo);
				List<String> providerIdList = new ArrayList<>(providerList.size());
				for(ProviderData provider : providerList)
				{
					providerIdList.add(provider.getId());
				}
				criteriaSearch.setProviderIdFilterList(providerIdList);
			}

			total = oscarLogDao.criteriaSearchCount(criteriaSearch);
			resultList = oscarLogDao.criteriaSearch(criteriaSearch);
		}
		catch(DateTimeParseException e)
		{
			logger.warn("Invalid date parameter: " + e.getParsedString());
		}
		request.setAttribute("resultList", resultList);
		request.setAttribute("total", total);
		return mapping.findForward("success");
	}
}
