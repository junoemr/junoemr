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
package org.oscarehr.managers;

import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.common.dao.InboxResultsDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.inbox.InboxManagerResponse;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oscar.oscarLab.ca.on.LabResultData;

@Service
public class InboxManager {
	
	private Logger logger = MiscUtils.getLogger();
	
	@Autowired
	private QueueDocumentLinkDao queueDocumentLinkDAO;
	@Autowired
	private SecUserRoleDao secUserRoleDao ;

	public static final String INBOX_TYPE_HL7 = "HL7";
	public static final String INBOX_TYPE_DOCUMENT = "DOC";

	public static final String STATUS_NEW = "N";

	public static final String NORMAL = "normal";
	public static final String ALL = "all";
	public static final String ABNORMAL = "abnormal";
	public static final String LABS = "labs";
	public static final String DOCUMENTS = "documents";

	public InboxManagerResponse getInboxResults(
			LoggedInInfo loggedInInfo,
			String view,
			String providerNo,
			String searchProviderNo,
			String demographicNo,
			String patientFirstName,
			String patientLastName,
			String patientHealthNumber,
			String ackStatus,
			Integer page,
			Integer pageSize,
			Date startDate,
			Date endDate
	)
	{
		if (view == null || "".equals(view)) {
			view = ALL;
		}

		Boolean isAbnormal = null;
		if (ABNORMAL.equals(view))
			isAbnormal = true;
		if (NORMAL.equals(view))
			isAbnormal = false;

		boolean mixLabsAndDocs = NORMAL.equals(view) || ALL.equals(view);

		if (ackStatus == null)
		{
			ackStatus = STATUS_NEW;
		}

		if (providerNo == null)
		{
			providerNo = "";
		}

		if (searchProviderNo == null)
		{
			searchProviderNo = providerNo;
		}

		if (page > 0)
		{
			page--;
		}

		String labType = null;
		if(LABS.equals(view))
		{
			labType = INBOX_TYPE_HL7;
		}
		else if(DOCUMENTS.equals(view))
		{
			labType = INBOX_TYPE_DOCUMENT;
		}

		InboxResultsDao inboxResultsDao =
				(InboxResultsDao) SpringUtils.getBean("inboxResultsDao");

		ArrayList<LabResultData> labdocs = inboxResultsDao.getInboxResults(
				loggedInInfo,
				searchProviderNo,
				demographicNo,
				patientFirstName,
				patientLastName,
				patientHealthNumber,
				ackStatus,
				true,
				page,
				pageSize,
				mixLabsAndDocs,
				isAbnormal,
				null,
				false,
				labType,
				startDate,
				endDate);

		logger.debug("labdocs.size()="+labdocs.size());

		InboxManagerResponse response = new InboxManagerResponse();
		response.setPageNum(page);
		response.setProviderNo(providerNo);
		response.setSearchProviderNo(searchProviderNo);
		response.setDemographicNo(demographicNo!=null?Integer.parseInt(demographicNo):null);
		response.setAckStatus(ackStatus);
		response.setLabdocs(labdocs);

		return response;
	}
}


