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
package org.oscarehr.integration.srfax;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.integration.SRFax.SRFaxUploadProvider;
import org.oscarehr.integration.SRFax.api.SRFaxApiConnector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SRFaxUploadProviderTest
{
	@Test
	public void testIsFaxInRemoteSentState_True()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider();
		assertTrue(uploadProvider.isFaxInRemoteSentState(SRFaxApiConnector.RESPONSE_STATUS_SENT));
	}

	@Test
	public void testIsFaxInRemoteSentState_False()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider();
		assertFalse(uploadProvider.isFaxInRemoteSentState("Any Status"));
	}

	@Test
	public void testIsFaxInRemoteFailedState_True()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider();
		assertTrue(uploadProvider.isFaxInRemoteFailedState(SRFaxApiConnector.RESPONSE_STATUS_FAILED));
	}

	@Test
	public void testIsFaxInRemoteFailedState_False()
	{
		FaxUploadProvider uploadProvider = new SRFaxUploadProvider();
		assertFalse(uploadProvider.isFaxInRemoteFailedState("Any Status"));
	}
}
