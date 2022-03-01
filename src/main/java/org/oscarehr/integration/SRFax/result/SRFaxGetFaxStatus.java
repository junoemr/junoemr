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

package org.oscarehr.integration.SRFax.result;

import org.oscarehr.fax.externalApi.srfax.result.GetFaxStatusResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import oscar.util.ConversionUtils;
import java.util.Date;
import java.util.Optional;

/**
 * Intermediate object, Allows the GetFaxStatus Result class and it's wrapper to implement the GetFaxStatusResult interface;
 */
public class SRFaxGetFaxStatus implements org.oscarehr.fax.result.GetFaxStatusResult
{
	private SingleWrapper<GetFaxStatusResult> srFaxResult;

	public SRFaxGetFaxStatus(SingleWrapper<GetFaxStatusResult> result)
	{
		this.srFaxResult = result;
	}

	@Override
	public boolean isSuccess()
	{
		return this.srFaxResult.isSuccess();
	}

	@Override
	public String getRemoteSentStatus()
	{
		return this.srFaxResult.getResult().getSentStatus();
	}


	@Override
	public Optional<Date> getRemoteSendTime()
	{
		Date remoteSendTime = null;
		String secondsSinceEpoch = this.srFaxResult.getResult().getEpochTime();

		if (ConversionUtils.hasContent(secondsSinceEpoch))
		{
			remoteSendTime = ConversionUtils.fromEpochStringSeconds(secondsSinceEpoch);
		}

		return Optional.ofNullable(remoteSendTime);
	}

	@Override
	public Optional<String> getErrorCode()
	{
		return Optional.ofNullable(this.srFaxResult.getResult().getErrorCode());
	}

	@Override
	public Optional<String> getError()
	{
		return Optional.ofNullable(this.srFaxResult.getError());
	}
}