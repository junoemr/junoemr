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
import org.oscarehr.securityLog.search.SecurityLogCriteriaSearch;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

public class LogReportAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static OscarLogDao oscarLogDao = SpringUtils.getBean(OscarLogDao.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
	                             HttpServletRequest request, HttpServletResponse response)
	{
		String startDateStr = StringUtils.trimToNull(request.getParameter("startDate"));
		String endDateStr = StringUtils.trimToNull(request.getParameter("endDate"));
		String providerNo = StringUtils.trimToNull(request.getParameter("providerNo"));
		String contentType = StringUtils.trimToNull(request.getParameter("contentType"));

		LocalDate startDate = ConversionUtils.toNullableLocalDate(startDateStr);
		LocalDate endDate = ConversionUtils.toNullableLocalDate(endDateStr);

		logger.info("LogReportAction true");

		SecurityLogCriteriaSearch criteriaSearch = new SecurityLogCriteriaSearch();
		criteriaSearch.setProviderNo(providerNo);
		criteriaSearch.setContentType(contentType);
		criteriaSearch.setStartDate(startDate);
		criteriaSearch.setEndDate(endDate);
		criteriaSearch.setSortDirDescending();

		List<OscarLog> resultList = oscarLogDao.criteriaSearch(criteriaSearch);

		request.setAttribute("resultList", resultList);
		return mapping.findForward("success");
	}
}
