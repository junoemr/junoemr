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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.integration.SRFax.SRFaxAccountProvider;
import org.oscarehr.integration.SRFax.SRFaxDownloadProvider;
import org.oscarehr.integration.SRFax.SRFaxUploadProvider;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;
import org.oscarehr.integration.ringcentral.RingcentralAccountProvider;
import org.oscarehr.integration.ringcentral.RingcentralDownloadProvider;
import org.oscarehr.integration.ringcentral.RingcentralUploadProvider;

public class FaxProviderFactory
{
	public static FaxAccountProvider createFaxAccountProvider(FaxAccount faxAccount)
	{
		switch (faxAccount.getIntegrationType())
		{
			case SRFAX:
				return new SRFaxAccountProvider(faxAccount);
			case RINGCENTRAL:
				return new RingcentralAccountProvider(faxAccount);
			case NONE:
			default:
				throw new IllegalStateException("Fax account " + faxAccount.getId() + " has invalid integration type");
		}
	}

	public static FaxDownloadProvider createFaxDownloadProvider(FaxAccount faxAccount)
	{
		switch (faxAccount.getIntegrationType())
		{
			case SRFAX:
				return new SRFaxDownloadProvider(faxAccount);
			case RINGCENTRAL:
				return new RingcentralDownloadProvider(faxAccount);
			case NONE:
			default:
				throw new IllegalStateException("Fax account " + faxAccount.getId() + " has invalid integration type");
		}
	}

	public static FaxUploadProvider createFaxUploadProvider(FaxAccount faxAccount)
	{
		switch (faxAccount.getIntegrationType())
		{
			case SRFAX:
				return new SRFaxUploadProvider();
			case RINGCENTRAL:
				return new RingcentralUploadProvider(faxAccount);
			case NONE:
			default:
				throw new IllegalStateException("Fax account " + faxAccount.getId() + " has invalid integration type");
		}
	}

	public static Criterion getStatusSearchRestrictionsInProgress(String accountTypePropertyName, String statusPropertyName)
	{
		return Restrictions.or(
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.SRFAX),
						Restrictions.not(Restrictions.in(statusPropertyName, SRFaxApiConnector.RESPONSE_STATUSES_FINAL))
				),
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.RINGCENTRAL),
						Restrictions.not(Restrictions.in(statusPropertyName, "TODO in progress"))
				)
		);
	}

	public static Criterion getStatusSearchRestrictionsIntegrationFailed(String accountTypePropertyName, String statusPropertyName)
	{
		return Restrictions.or(
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.SRFAX),
						Restrictions.in(statusPropertyName, SRFaxApiConnector.RESPONSE_STATUS_FAILED)
				),
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.RINGCENTRAL),
						Restrictions.in(statusPropertyName, "TODO Failed")
				)
		);
	}

	public static Criterion getStatusSearchRestrictionsIntegrationSuccess(String accountTypePropertyName, String statusPropertyName)
	{
		return Restrictions.or(
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.SRFAX),
						Restrictions.in(statusPropertyName, SRFaxApiConnector.RESPONSE_STATUS_SENT)
				),
				Restrictions.and(
						Restrictions.eq(accountTypePropertyName, FaxProvider.RINGCENTRAL),
						Restrictions.in(statusPropertyName, "TODO Sent")
				)
		);
	}
}