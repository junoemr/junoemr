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

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.result.GetFaxStatusResult;
import java.util.List;

public interface FaxUploadProvider
{

	/**
	 * Send the specified fax using this faxing provider
	 * @param faxOutbound queued fax outbound object.  This is a fax which has been marked for us to be sent, but has
	 *                    yet to be uploaded to the external faxing service.
	 * @param file file to send with the fax
	 * @return Updated faxoutbound object
	 * @throws Exception
	 */
	FaxOutbound sendQueuedFax(FaxOutbound faxOutbound, GenericFile file) throws Exception;

	/**
	 * Get a list of FaxProvider statuses which indicate that a fax has been processed by the remote service.
	 * @return List of statuses that indicate that a fax has been processed.
	 */
	List<String> getRemoteFinalStatusIndicators();

	GetFaxStatusResult getFaxStatus(FaxOutbound faxOutbound) throws Exception;

	boolean isFaxInRemoteSentState(GetFaxStatusResult result);

	boolean isFaxInRemoteFailedState(FaxOutbound faxOutbound);
}