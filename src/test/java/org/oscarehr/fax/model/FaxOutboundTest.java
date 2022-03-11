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
package org.oscarehr.fax.model;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscarehr.fax.provider.FaxUploadProvider;

import static org.junit.Assert.assertEquals;

public class FaxOutboundTest
{
	@Test
	public void testCombinedStatus_Queued()
	{
		FaxOutbound entity = new FaxOutbound();
		entity.setStatus(FaxStatusInternal.QUEUED);

		FaxUploadProvider uploadProviderMock = mockUploadProvider(false, false);
		assertEquals("Combined status should be QUEUED", FaxStatusCombined.QUEUED, entity.getCombinedStatus(uploadProviderMock));
	}

	@Test
	public void testCombinedStatus_Error()
	{
		FaxOutbound entity = new FaxOutbound();
		entity.setStatus(FaxStatusInternal.ERROR);

		FaxUploadProvider uploadProviderMock = mockUploadProvider(false, false);
		assertEquals("Combined status should be ERROR", FaxStatusCombined.ERROR, entity.getCombinedStatus(uploadProviderMock));
	}

	@Test
	public void testCombinedStatus_InProgress()
	{
		FaxOutbound entity = new FaxOutbound();
		entity.setStatus(FaxStatusInternal.SENT);
		entity.setExternalStatus(null);

		FaxUploadProvider uploadProviderMock = mockUploadProvider(false, false);
		assertEquals("Combined status should be IN_PROGRESS", FaxStatusCombined.IN_PROGRESS, entity.getCombinedStatus(uploadProviderMock));
	}

	@Test
	public void testCombinedStatus_IntegrationFailed()
	{
		FaxOutbound entity = new FaxOutbound();
		entity.setStatus(FaxStatusInternal.SENT);
		entity.setExternalStatus("Failed");

		FaxUploadProvider uploadProviderMock = mockUploadProvider(false, true);
		assertEquals("Combined status should be INTEGRATION_FAILED", FaxStatusCombined.INTEGRATION_FAILED, entity.getCombinedStatus(uploadProviderMock));
	}

	@Test
	public void testCombinedStatus_IntegrationSuccess()
	{
		FaxOutbound entity = new FaxOutbound();
		entity.setStatus(FaxStatusInternal.SENT);
		entity.setExternalStatus("Sent");

		FaxUploadProvider uploadProviderMock = mockUploadProvider(true, false);
		assertEquals("Combined status should be INTEGRATION_SUCCESS", FaxStatusCombined.INTEGRATION_SUCCESS, entity.getCombinedStatus(uploadProviderMock));
	}

	private FaxUploadProvider mockUploadProvider(boolean sent, boolean failed)
	{
		FaxUploadProvider uploadProviderMock = Mockito.mock(FaxUploadProvider.class);
		Mockito.when(uploadProviderMock.isFaxInRemoteSentState(Mockito.anyString())).thenReturn(sent);
		Mockito.when(uploadProviderMock.isFaxInRemoteFailedState(Mockito.anyString())).thenReturn(failed);
		return uploadProviderMock;
	}
}
