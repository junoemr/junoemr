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
package org.oscarehr.fax.service;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.fax.dao.FaxOutboundDao;
import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.model.FaxStatusRemote;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.result.FaxStatusResult;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.oscarehr.fax.service.FaxUploadService.STATUS_MESSAGE_COMPLETED;
import static org.oscarehr.fax.service.FaxUploadService.STATUS_MESSAGE_ERROR_UNKNOWN;

public class FaxUploadServiceTest
{
	@InjectMocks
	@Autowired
	protected FaxUploadService faxUploadService;

	@Mock
	protected FaxOutboundDao faxOutboundDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		// suppress logging errors etc. stuff when running tests
		Logger loggerMock = Mockito.mock(Logger.class);
		doNothing().when(loggerMock).error(Mockito.anyString());
		doNothing().when(loggerMock).error(Mockito.anyString(), Mockito.any());
		doNothing().when(loggerMock).info(Mockito.anyString());
		doNothing().when(loggerMock).warn(Mockito.anyString());
		FaxUploadService.logger = loggerMock;
	}

	@Test
	public void test_requestPendingStatusUpdate_successState()
	{
		FaxStatusResult resultMock = Mockito.mock(FaxStatusResult.class);
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(resultMock, true, false);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();
		when(resultMock.getRemoteSentStatus()).thenReturn("Custom Sent");

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should be " + FaxStatusRemote.SENT, FaxStatusRemote.SENT, faxOutbound.getStatusRemote());
		Assert.assertEquals("status message should be " + STATUS_MESSAGE_COMPLETED, STATUS_MESSAGE_COMPLETED, faxOutbound.getStatusMessage());
		Assert.assertTrue("record should be archived", faxOutbound.getArchived());
	}

	@Test
	public void test_requestPendingStatusUpdate_pendingState()
	{
		FaxStatusResult resultMock = Mockito.mock(FaxStatusResult.class);
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(resultMock, false, false);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();
		when(resultMock.getRemoteSentStatus()).thenReturn("Custom Pending");

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should be " + FaxStatusRemote.PENDING, FaxStatusRemote.PENDING, faxOutbound.getStatusRemote());
		Assert.assertFalse("record should not be archived", faxOutbound.getArchived());
	}

	@Test
	public void test_requestPendingStatusUpdate_errorState()
	{
		FaxStatusResult resultMock = Mockito.mock(FaxStatusResult.class);
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(resultMock, false, true);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();
		when(resultMock.getRemoteSentStatus()).thenReturn("Custom Error");

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should be " + FaxStatusRemote.ERROR, FaxStatusRemote.ERROR, faxOutbound.getStatusRemote());
		Assert.assertEquals("status message should be " + STATUS_MESSAGE_ERROR_UNKNOWN, STATUS_MESSAGE_ERROR_UNKNOWN, faxOutbound.getStatusMessage());
		Assert.assertFalse("record should not be archived", faxOutbound.getArchived());
	}

	@Test
	public void test_requestPendingStatusUpdate_connectionException()
	{
		String errorMessage = "internet error test";
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(null, false, true);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();
		FaxStatusRemote initialStatusRemote = faxOutbound.getStatusRemote();

		when(uploadProviderMock.getFaxStatus(Mockito.any(FaxOutbound.class))).thenThrow(new FaxApiConnectionException(errorMessage));

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should not change", initialStatusRemote, faxOutbound.getStatusRemote());
		Assert.assertFalse("record should not be archived", faxOutbound.getArchived());
	}

	@Test
	public void test_requestPendingStatusUpdate_resultException()
	{
		String errorMessage = "FaxDetailsID Not Found";
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(null, false, true);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();

		when(uploadProviderMock.getFaxStatus(Mockito.any(FaxOutbound.class))).thenThrow(new FaxApiResultException(errorMessage));

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should be set to " + FaxStatusRemote.ERROR, FaxStatusRemote.ERROR, faxOutbound.getStatusRemote());
		Assert.assertEquals("status message should be " + errorMessage, errorMessage, faxOutbound.getStatusMessage());
		Assert.assertFalse("record should not be archived", faxOutbound.getArchived());
	}

	@Test
	public void test_requestPendingStatusUpdate_unknownException()
	{
		String errorMessage = "Unknown Error";
		FaxUploadProvider uploadProviderMock = initUploadProviderMock(null, false, true);
		FaxOutbound faxOutbound = initDefaultFaxOutbound();
		FaxStatusRemote initialStatusRemote = faxOutbound.getStatusRemote();

		when(uploadProviderMock.getFaxStatus(Mockito.any(FaxOutbound.class))).thenThrow(new RuntimeException(errorMessage));

		faxUploadService.requestPendingStatusUpdate(uploadProviderMock, faxOutbound);

		Assert.assertEquals("remote status should not change", initialStatusRemote, faxOutbound.getStatusRemote());
		Assert.assertFalse("record should not be archived", faxOutbound.getArchived());
	}

	private FaxUploadProvider initUploadProviderMock(FaxStatusResult resultMock, boolean success, boolean error)
	{
		FaxUploadProvider uploadProviderMock = Mockito.mock(FaxUploadProvider.class);
		when(uploadProviderMock.getFaxStatus(Mockito.any(FaxOutbound.class))).thenReturn(resultMock);
		when(uploadProviderMock.isFaxInRemoteSentState(Mockito.any())).thenReturn(success);
		when(uploadProviderMock.isFaxInRemoteFailedState(Mockito.any())).thenReturn(error);
		return uploadProviderMock;
	}

	private FaxOutbound initDefaultFaxOutbound()
	{
		FaxOutbound faxOutbound = new FaxOutbound();
		faxOutbound.setId(1L);
		faxOutbound.setExternalStatus("custom progress message");
		faxOutbound.setRemoteStatusPending();

		doReturn(faxOutbound).when(faxOutboundDao).merge(Mockito.any());
		return faxOutbound;
	}
}