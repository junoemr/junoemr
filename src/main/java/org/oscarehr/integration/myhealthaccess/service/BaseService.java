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

package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.config.JunoProperties;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;


@Service("mha_BaseService")
public class BaseService extends org.oscarehr.integration.BaseService
{
	private static final JunoProperties junoProps = SpringUtils.getBean(JunoProperties.class);

	// ==========================================================================
	// MHA Endpoints
	// ==========================================================================

	// Appointments
	protected static final String APPOINTMENT_CACHE_UPDATE = "/clinic/%s/appointment/%s/cache";
	protected static final String APPOINTMENT_BOOK = "/clinic_user/appointment/book";
	protected static final String APPOINTMENT_LINK_AQS_TO_CLINIC_APPOINTMENT = "/clinic_user/self/appointment/%s/aqs_link";
	protected static final String APPOINTMENT_SEND_TELEHEALTH_NOTIFICATION = "/clinic_user/self/clinic/appointment/%s/send_telehealth_notification";
	protected static final String APPOINTMENT_SEND_GENERAL_NOTIFICATION = "/clinic_user/self/juno/appointment/%s/send_general_notification";
	protected static final String APPOINTMENT_SEARCH = "/clinic/%s/appointments";
	protected static final String APPOINTMENT_GET = "/clinic/%s/appointment/%s/";
	protected static final String APPOINTMENT_GET_TELEHEALTH_SESSION_INFO = "/clinic/%s/appointment/%s/session";



	@Autowired
	IntegrationService integrationService;

	protected static OscarProperties oscarProps = OscarProperties.getInstance();
	protected final String MYHEALTHACCESS_PROTOCOL = junoProps.getMyhealthaccess().getMyhealthaccessProtocol();

	public String buildUrl(String endPoint)
	{
		endPoint = endPoint.replaceAll("http(s)?://", "");
		return MYHEALTHACCESS_PROTOCOL + "://" + endPoint;
	}

	/*
	 * Helper Methods
	 */

	/**
	 * return the passed in integration if not null. If null throw InvalidIntegrationException.
	 * @param integration - integration
	 * @return - the same integration you passed in.
	 */
	protected Integration integrationOrException(Integration integration)
	{
		if (integration == null)
		{
			throw new InvalidIntegrationException();
		}
		else
		{
			return integration;
		}
	}
}
