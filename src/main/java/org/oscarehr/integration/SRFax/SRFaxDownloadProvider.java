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

package org.oscarehr.integration.SRFax;

import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GenericGetFaxInboxResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.SingleWrapper;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxDownloadProvider;
import oscar.util.ConversionUtils;
import java.time.LocalDate;

public class SRFaxDownloadProvider implements FaxDownloadProvider
{

	private final SRFaxApiConnector srFaxApiConnector;

	public SRFaxDownloadProvider(FaxAccount faxAccount) {
		this.srFaxApiConnector = new SRFaxApiConnector(faxAccount.getLoginId(), faxAccount.getLoginPassword());
	}

	@Override
	public ListWrapper<GenericGetFaxInboxResult> getFaxInbox(int faxDaysPast)
	{
		String startDate = ConversionUtils.toDateString(LocalDate.now().minusDays(faxDaysPast), SRFaxApiConnector.DATE_FORMAT);
		String endDate = ConversionUtils.toDateString(LocalDate.now(), SRFaxApiConnector.DATE_FORMAT);
		return srFaxApiConnector.getFaxInbox(
			SRFaxApiConnector.PERIOD_RANGE,
			startDate,
			endDate,
			SRFaxApiConnector.VIEWED_STATUS_UNREAD,
			null);
	}

	@Override
	public SingleWrapper<String> retrieveFax(String referenceIdStr)
	{
		return srFaxApiConnector.retrieveFax(null,
			referenceIdStr, SRFaxApiConnector.RETRIEVE_DIRECTION_IN);
	}
	

	@Override
	public SingleWrapper<String> updateViewedStatus(String referenceIdStr)
	{
		return srFaxApiConnector.updateViewedStatus(null,
			referenceIdStr,
			SRFaxApiConnector.RETRIEVE_DIRECTION_IN,
			SRFaxApiConnector.MARK_AS_READ);
	}
}
