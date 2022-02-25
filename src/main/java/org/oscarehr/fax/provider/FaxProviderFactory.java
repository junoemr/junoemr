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

package org.oscarehr.fax.provider;

import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.integration.SRFax.SRFaxAccountProvider;
import org.oscarehr.integration.SRFax.SRFaxDownloadProvider;
import org.oscarehr.integration.SRFax.SRFaxUploadProvider;

public class FaxProviderFactory
{
	private FaxProvider getSystemFaxProvider()
	{
		return FaxProvider.SRFAX;
	}

	public FaxAccountProvider createFaxAccountProvider(FaxAccount faxAccount)
	{
		switch (faxAccount.getIntegrationType())
		{
			case SRFAX:
				return new SRFaxAccountProvider(faxAccount);
			case RINGCENTRAL:
			case NONE:
			default:
				return null;
		}
	}

	public FaxUploadProvider createFaxUploadProvider()
	{
		switch (getSystemFaxProvider())
		{
			case SRFAX:
				return new SRFaxUploadProvider();
			case RINGCENTRAL:
			case NONE:
			default:
				return null;
		}
	}

	/**
	 * Creates a new FaxDownloadProvider based on the provided faxAccount integration type
	 * @param faxAccount fax account to create the provider from
	 * @return FaxDownloadProvider
	 */
	public FaxDownloadProvider createFaxDownloadProvider(FaxAccount faxAccount)
	{
		switch (faxAccount.getIntegrationType())
		{
			case SRFAX:
				return new SRFaxDownloadProvider(faxAccount);
			case RINGCENTRAL:
			case NONE:
			default:
				return null;
		}
	}
}
