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

package org.oscarehr.integration.imdhealth.transfer.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.oscarehr.util.LoggedInInfo;

import java.io.Serializable;

@Data
public class SSOUser implements Serializable
{
	private boolean kiosk = false;

	@JsonProperty("external_id")
	private String externalId;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	/* OPTIONAL FIELDS (Implementation TBD)

	private String gender;
	private String practitionerType;
	private String preferredLocale;
	private String prefix;

	*/

	public static SSOUser fromLoggedInInfo(LoggedInInfo loggedInInfo, String practiceId)
	{
		SSOUser user = new SSOUser();

		// externalId must be globally unique across the credential.  This implementation should be safe
		// regardless of whether we decide to go with issuing the credential to cloudpractice vs individual clinics

		if (loggedInInfo.getSession().getAttribute("initializingProvider") != null)
		{
			user.externalId = "juno_" + practiceId + "_" + loggedInInfo.getSession().getAttribute("initializingProvider");
		}
		else
		{
			user.externalId = "juno_" + practiceId + "_" + loggedInInfo.getLoggedInProviderNo();
		}
		user.firstName = loggedInInfo.getLoggedInProvider().getFirstName();
		user.lastName = loggedInInfo.getLoggedInProvider().getLastName();

		return user;
	}
}
