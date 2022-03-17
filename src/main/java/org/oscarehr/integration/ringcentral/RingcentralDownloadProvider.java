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

package org.oscarehr.integration.ringcentral;

import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxDownloadProvider;
import org.oscarehr.fax.result.FaxInboxResult;
import org.oscarehr.integration.ringcentral.api.RingcentralApiConnector;
import org.oscarehr.util.SpringUtils;

import java.util.List;

public class RingcentralDownloadProvider implements FaxDownloadProvider
{
	protected FaxAccount faxAccount;
	protected RingcentralApiConnector ringcentralApiConnector = SpringUtils.getBean(RingcentralApiConnector.class); //todo how to access in pojo?

	public RingcentralDownloadProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	/**
	 * Retrieves a List of FaxInboxResults of unread faxes from Ringcentral
	 * @param faxDaysPast Number of days to retrieve result from Ringcentral for
	 * @return The list of unread FaxInboxResults
	 * @throws FaxApiResultException if result is not success
	 */

	@Override
	public List<? extends FaxInboxResult> getFaxInbox(int faxDaysPast) throws FaxApiResultException
	{
		//TODO
		throw new FaxApiResultException("not implemented");
	}


	/**
	 * Retrieves a fax from Ringcentral
	 * @param referenceIdStr Ringcentral fax reference Id of the fax to retrieve
	 * @return Fax document as a base64 encoded string
	 * @throws FaxApiResultException if result is not success
	 */
	@Override
	public String retrieveFax(String referenceIdStr) throws FaxApiResultException
	{
		//TODO
		throw new FaxApiResultException("not implemented");
	}

	/**
	 * Marks the fax as read in Ringcentral
	 * @param referenceIdStr reference id of the fax to make as read
	 * @throws FaxApiResultException if result is not success
	 */
	@Override
	public void markAsDownloaded(String referenceIdStr) throws FaxApiResultException
	{
		//TODO
		throw new FaxApiResultException("not implemented");
	}
}