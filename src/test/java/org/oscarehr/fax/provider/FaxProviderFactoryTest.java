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

import org.junit.Test;
import org.mockito.Mockito;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.integration.SRFax.SRFaxAccountProvider;
import org.oscarehr.integration.SRFax.SRFaxDownloadProvider;
import org.oscarehr.integration.SRFax.SRFaxUploadProvider;
import org.oscarehr.integration.ringcentral.RingcentralAccountProvider;
import org.oscarehr.integration.ringcentral.RingcentralDownloadProvider;
import org.oscarehr.integration.ringcentral.RingcentralUploadProvider;

import static org.junit.Assert.assertTrue;

public class FaxProviderFactoryTest
{
	@Test
	public void testCreateFaxAccountProvider_SRFax()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.SRFAX);
		FaxAccountProvider faxAccountProvider = FaxProviderFactory.createFaxAccountProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxAccountProvider instanceof SRFaxAccountProvider);
	}

	@Test
	public void testCreateFaxAccountProvider_Ringcentral()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.RINGCENTRAL);
		FaxAccountProvider faxAccountProvider = FaxProviderFactory.createFaxAccountProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxAccountProvider instanceof RingcentralAccountProvider);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFaxAccountProvider_invalid()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(null);
		FaxProviderFactory.createFaxAccountProvider(mockFaxAccount);
	}

	@Test
	public void testCreateFaxDownloadProvider_SRFax()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.SRFAX);
		FaxDownloadProvider faxDownloadProvider = FaxProviderFactory.createFaxDownloadProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxDownloadProvider instanceof SRFaxDownloadProvider);
	}

	@Test
	public void testCreateFaxDownloadProvider_Ringcentral()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.RINGCENTRAL);
		FaxDownloadProvider faxDownloadProvider = FaxProviderFactory.createFaxDownloadProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxDownloadProvider instanceof RingcentralDownloadProvider);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFaxDownloadProvider_invalid()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(null);
		FaxProviderFactory.createFaxDownloadProvider(mockFaxAccount);
	}

	@Test
	public void testCreateFaxUploadProvider_SRFax()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.SRFAX);
		FaxUploadProvider faxUploadProvider = FaxProviderFactory.createFaxUploadProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxUploadProvider instanceof SRFaxUploadProvider);
	}

	@Test
	public void testCreateFaxUploadProvider_Ringcentral()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(FaxProvider.RINGCENTRAL);
		FaxUploadProvider faxUploadProvider = FaxProviderFactory.createFaxUploadProvider(mockFaxAccount);
		assertTrue("Fax Provider does not match account type", faxUploadProvider instanceof RingcentralUploadProvider);
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateFaxUploadProvider_invalid()
	{
		FaxAccount mockFaxAccount = mockFaxAccount(null);
		FaxProviderFactory.createFaxUploadProvider(mockFaxAccount);
	}

	private FaxAccount mockFaxAccount(FaxProvider type)
	{
		FaxAccount faxAccount = Mockito.mock(FaxAccount.class);
		Mockito.when(faxAccount.getIntegrationType()).thenReturn(type);
		return faxAccount;
	}
}
