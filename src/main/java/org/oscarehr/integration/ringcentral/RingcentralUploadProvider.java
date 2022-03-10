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

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.result.FaxStatusResult;

import java.util.ArrayList;
import java.util.List;

public class RingcentralUploadProvider implements FaxUploadProvider
{
	protected FaxAccount faxAccount;

	public RingcentralUploadProvider(FaxAccount faxAccount)
	{
		this.faxAccount = faxAccount;
	}

	@Override
	public FaxOutbound sendQueuedFax(FaxOutbound faxOutbound, GenericFile file) throws Exception
	{
		//TODO
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<String> getRemoteFinalStatusIndicators()
	{
		//TODO
		return new ArrayList<>();
	}

	@Override
	public boolean isFaxInRemoteSentState(String externalStatus)
	{
		//TODO
		return false;
	}

	@Override
	public boolean isFaxInRemoteFailedState(String externalStatus)
	{
		//TODO
		return false;
	}


	@Override
	public FaxStatusResult getFaxStatus(FaxOutbound faxOutbound) throws Exception
	{
		//TODO
		throw new FaxApiConnectionException("not implemented");
	}
}