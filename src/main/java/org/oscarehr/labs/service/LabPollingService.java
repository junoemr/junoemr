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
package org.oscarehr.labs.service;

import org.oscarehr.olis.service.OLISPollingService;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler;

/**
 * A service dedicated to polling and downloading labs
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class LabPollingService
{
	@Autowired
	private OLISPollingService olisPollingService;

	public void pullLabs(LoggedInInfo loggedInInfo, String labType)
	{
		switch(labType)
		{
			// add more lab integrations here!
			case OLISHL7Handler.OLIS_MESSAGE_TYPE: olisPollingService.requestResults(loggedInInfo); break;
			default: throw new IllegalArgumentException("Unsupported lab type: " + labType);
		}
	}
}
