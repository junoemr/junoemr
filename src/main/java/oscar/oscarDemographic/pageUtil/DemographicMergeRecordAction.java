/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


/*
 * DemographicMergeRecordAction.java
 *
 * Created on September 11, 2007, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oscar.oscarDemographic.pageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.demographic.dao.DemographicMergedDao;
import org.oscarehr.demographic.entity.DemographicMerged;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.log.LogConst;

/**
 *
 * @author wrighd
 */
public class DemographicMergeRecordAction  extends Action {

    Logger logger = Logger.getLogger(DemographicMergeRecordAction.class);
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    private DemographicMergedDao demographicMergedDao = SpringUtils.getBean(DemographicMergedDao.class);
    
    public DemographicMergeRecordAction() {

    }

    @Transactional
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)
    {

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.CREATE, null, "_demographic");

		if (request.getParameterValues("records") == null)
		{
			return mapping.findForward("failure");
		}

		String outcome = "success";
		ArrayList<String> records = new ArrayList<String>(Arrays.asList(request.getParameterValues("records")));
		String head = request.getParameter("head");
		String action = request.getParameter("mergeAction");
		String providerNo = request.getParameter("provider_no");

		if (action.equals("merge") && head != null && records.size() > 1 && records.contains(head))
		{
			for (String record : records)
			{
				if (!record.equals(head))
				{
					try
					{
						int demographicNo = Integer.parseInt(record);
						int headRecord = Integer.parseInt(head);
						// Before merging demographics, check if demographicNo has already been merged
						// If it has, we need to get the parent ID for whatever demographic it's merged to (legacy behaviour)
						DemographicMerged currentMerged = demographicMergedDao.getCurrentHead(demographicNo);
						if (currentMerged != null)
						{
							demographicNo = currentMerged.getMergedTo();
						}
						boolean success = demographicMergedDao.mergeDemographics(providerNo, demographicNo, headRecord);
						if (success)
						{
							outcome = "success";
							LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
									headRecord,
									LogConst.ACTION_ADD,
									LogConst.CON_DEMOGRAPHIC_MERGE,
									LogConst.STATUS_SUCCESS,
									"parentDemographic=" + demographicNo,
									loggedInInfo.getIp(),
									"mergedDemographic=" + demographicNo);
						}
						else
						{
							outcome = "alreadyMerged";
						}
					}
					catch (NumberFormatException e)
					{
						logger.error("Error occurred when trying to use parseInt", e);
						outcome = "failure";
					}
				}
			}
		}
		else if(action.equals("unmerge") && records.size() > 0)
		{
			outcome = "successUnMerge";
			for (String record : records)
			{
				try
				{
					int mergedDemographicNo = Integer.parseInt(record);
					demographicMergedDao.unmergeDemographics(providerNo, mergedDemographicNo);
					LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
							mergedDemographicNo,
							LogConst.ACTION_DELETE,
							LogConst.CON_DEMOGRAPHIC_MERGE,
							LogConst.STATUS_SUCCESS,
							"",
							loggedInInfo.getIp(),
							"mergedDemographic=" + mergedDemographicNo);
				}
				catch (NumberFormatException e)
				{
					logger.error("Error occurred when trying to unmerge demographicNo " + record, e);
					outcome = "failureUnMerge";
				}
				catch (NoSuchElementException e)
				{
					logger.error("Couldn't find active merge record for demographicNo: " + record + ", has this already been unmerged?");
					outcome = "alreadyUnMerged";
				}
			}
		}
		else
		{
			outcome = "failure";
		}
		request.setAttribute("mergeoutcome", outcome);

		if (request.getParameter("caisiSearch") != null && request.getParameter("caisiSearch").equalsIgnoreCase("yes")){
			outcome = "caisiSearch";
		}

		return mapping.findForward(outcome);
	}
}
