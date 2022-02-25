/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved. This software is published under
 * the GPL GNU General Public License. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * <p>
 * This software was written for CloudPractice Inc. Victoria, British Columbia Canada
 */

package org.oscarehr.fax.provider;


import org.oscarehr.fax.exception.FaxApiResultException;
import org.oscarehr.fax.result.FaxInboxResult;
import java.util.List;

public interface FaxDownloadProvider
{

	/**
	 * Generates a List of FaxInboxResults which represents non-downloaded faxes
	 * @param faxDaysPast Number of days to retrieve fax inbox results for
	 * @return List of non-downloaded faxes
	 * @throws FaxApiResultException on result error
	 */
	List<? extends FaxInboxResult> getFaxInbox(int faxDaysPast);

	/**
	 * Retrieves a single fax document identified by the referenceIdStr
	 * @param referenceIdStr Reference Id of the fax to retrieve
	 * @return Fax document as a base64 encoded string
	 * @throws FaxApiResultException on result error
	 */
	String retrieveFax(String referenceIdStr);

	/**
	 * Marks a fax identified by the referenceIdStr as downloaded
	 * @param referenceIdStr Reference Id of the fax to mark as downloaded
	 * @throws FaxApiResultException on result error
	 */
	void markAsDownloaded(String referenceIdStr);
}
