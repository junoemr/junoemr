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
package org.oscarehr.fax.transfer;

import org.junit.Test;
import org.oscarehr.fax.model.FaxStatusCombined;
import org.oscarehr.fax.model.FaxStatusInternal;
import org.oscarehr.fax.model.FaxStatusRemote;

import static org.junit.Assert.assertEquals;

public class FaxOutboxTransferOutboundTest
{
	@Test
	public void testCombinedStatus_Queued()
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setSystemStatus(FaxStatusInternal.QUEUED);

		assertEquals("Combined status should be QUEUED", FaxStatusCombined.QUEUED, model.getCombinedStatus());
	}

	@Test
	public void testCombinedStatus_Error()
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setSystemStatus(FaxStatusInternal.ERROR);

		assertEquals("Combined status should be ERROR", FaxStatusCombined.ERROR, model.getCombinedStatus());
	}

	@Test
	public void testCombinedStatus_InProgress()
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setSystemStatus(FaxStatusInternal.SENT);
		model.setRemoteStatus(null);

		assertEquals("Combined status should be IN_PROGRESS", FaxStatusCombined.IN_PROGRESS, model.getCombinedStatus());
	}

	@Test
	public void testCombinedStatus_IntegrationFailed()
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setSystemStatus(FaxStatusInternal.SENT);
		model.setRemoteStatus(FaxStatusRemote.ERROR);

		assertEquals("Combined status should be INTEGRATION_FAILED", FaxStatusCombined.INTEGRATION_FAILED, model.getCombinedStatus());
	}

	@Test
	public void testCombinedStatus_IntegrationSuccess()
	{
		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setSystemStatus(FaxStatusInternal.SENT);
		model.setRemoteStatus(FaxStatusRemote.SENT);

		assertEquals("Combined status should be INTEGRATION_SUCCESS", FaxStatusCombined.INTEGRATION_SUCCESS, model.getCombinedStatus());
	}
}
