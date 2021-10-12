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
package org.oscarehr.message.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.MessageListDao;
import org.oscarehr.common.dao.MessageTblDao;
import org.oscarehr.common.dao.MsgDemoMapDao;
import org.oscarehr.common.model.MessageList;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.common.model.MsgDemoMap;
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MessageService
{
	@Autowired
	MessageTblDao messageTblDao;

	@Autowired
	MessageListDao messageListDao;

	@Autowired
	MsgDemoMapDao messageDemoMapDao;

	public void saveMessage(MessageTbl message, List<ProviderData> providerList, Demographic demographic)
	{
		this.saveMessage(message, providerList, demographic, MessageList.STATUS_NEW);
	}

	public void saveMessage(MessageTbl message, List<ProviderData> providerList, Demographic demographic, String messageStatus)
	{
		this.saveMessage(message, providerList, demographic, messageStatus, MessageList.DEFAULT_REMOTE_LOCATION);
	}

	public void saveMessage(MessageTbl message, List<ProviderData> providerList, Demographic demographic, String messageStatus, int remoteLocation)
	{
		// set required data values if they are not present
		if(message.getDate() == null)
		{
			message.setDate(new Date());
		}
		if(message.getTime() == null)
		{
			message.setTime(new Date());
		}
		if(message.getType() == null)
		{
			message.setType(OscarMsgType.GENERAL_TYPE);
		}
		if(message.getSentTo() == null)
		{
			StringBuilder stringBuilder = new StringBuilder();

			boolean first = true;
			for (ProviderData provider: providerList)
			{
				if(!first)
				{
					stringBuilder.append(", ");
				}
				stringBuilder.append(provider.getFirstName());
				stringBuilder.append(" ");
				stringBuilder.append(provider.getLastName());
				first = false;
			}
			message.setSentTo(stringBuilder.toString());
		}

		int maxSubjectLength = 128; // from database restrictions
		message.setSubject(StringUtils.left(message.getSubject(), maxSubjectLength));

		messageTblDao.persist(message);

		// link all providers
		for (ProviderData provider: providerList)
		{
			MessageList ml = new MessageList();
			ml.setMessage(message.getId());
			ml.setProviderNo(provider.getId());
			ml.setStatus(messageStatus);
			ml.setRemoteLocation(remoteLocation);
			messageListDao.persist(ml);
		}

		// link to demographic if available
		if(demographic != null)
		{
			MsgDemoMap demoMap = new MsgDemoMap();
			demoMap.setDemographic_no(demographic.getId());
			demoMap.setMessageID(message.getId());
			messageDemoMapDao.persist(demoMap);
		}
	}
}
