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

import org.junit.Test;
import org.mockito.Mockito;
import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.result.FaxStatusResult;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;
import org.oscarehr.integration.SRFax.api.result.SRFaxFaxStatusResult;
import org.oscarehr.integration.SRFax.api.resultWrapper.SingleWrapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SRFaxUploadProviderTest
{
	@Test
	public void testIsFaxInRemoteSentState_True()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(makeAccountMock());
		assertTrue(uploadProvider.isFaxInRemoteSentState(SRFaxApiConnector.RESPONSE_STATUS_SENT));
	}

	@Test
	public void testIsFaxInRemoteSentState_False()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(makeAccountMock());
		assertFalse(uploadProvider.isFaxInRemoteSentState("Any Status"));
	}

	@Test
	public void testIsFaxInRemoteFailedState_True()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(makeAccountMock());
		assertTrue(uploadProvider.isFaxInRemoteFailedState(SRFaxApiConnector.RESPONSE_STATUS_FAILED));
	}

	@Test
	public void testIsFaxInRemoteFailedState_False()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(makeAccountMock());
		assertFalse(uploadProvider.isFaxInRemoteFailedState("Any Status"));
	}

	/**
	 * ensure error response status when fetching fax status updates throw correct response type
	 */
	@Test(expected = FaxApiResultException.class)
	public void test_getFaxStatus_ErrorResponse()
	{
		SRFaxApiConnector mockConnector = Mockito.mock(SRFaxApiConnector.class);
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(mockConnector);
		FaxOutbound outboundMock = Mockito.mock(FaxOutbound.class);
		SingleWrapper<SRFaxFaxStatusResult> resultMock = Mockito.mock(SingleWrapper.class);

		when(mockConnector.getFaxStatus(Mockito.anyString())).thenReturn(resultMock);
		when(resultMock.isSuccess()).thenReturn(false);

		FaxStatusResult resultStatus = uploadProvider.getFaxStatus(outboundMock);
	}

	/**
	 * ensure error response status when sending queued faxes throw correct response type
	 */
	/*
	can uncomment this when SpringUtils.getBean(JunoProperties.class) can be mocked in GenericFile

	@Test(expected = FaxApiResultException.class)
	public void test_sendQueuedFax_ErrorResponse() throws Exception
	{
		String error = "An Error Message";
		SRFaxApiConnector mockConnector = Mockito.mock(SRFaxApiConnector.class);
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider(mockConnector);
		FaxOutbound outboundMock = Mockito.mock(FaxOutbound.class);
		FaxAccount mockAccount = makeAccountMock();
		when(mockAccount.getCoverLetterOption()).thenReturn(null);
		when(outboundMock.getFaxAccount()).thenReturn(mockAccount);

		GenericFile faxFileMock = Mockito.mock(GenericFile.class);
		when(faxFileMock.getName()).thenReturn("file_name");
		when(faxFileMock.toBase64()).thenReturn("some_base_64_string");

		SingleWrapper<Integer> resultMock = Mockito.mock(SingleWrapper.class);
		when(mockConnector.queueFax(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(resultMock);
		when(resultMock.isSuccess()).thenReturn(false);
		when(resultMock.getError()).thenReturn(error);

		FaxOutbound outboundResult = uploadProvider.sendQueuedFax(outboundMock, faxFileMock);
	}*/

	private FaxAccount makeAccountMock()
	{
		FaxAccount accountMock = Mockito.mock(FaxAccount.class);
		when(accountMock.getIntegrationType()).thenReturn(FaxProvider.SRFAX);
		when(accountMock.getLoginId()).thenReturn("12345");
		when(accountMock.getLoginPassword()).thenReturn("FakePassword");
		return accountMock;
	}
}
