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
import org.oscarehr.common.model.Provider;
import org.oscarehr.provider.model.ProviderData;

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

	@JsonProperty ("practitioner_type")
	private String practitionerType = "Other";

	@JsonProperty ("other_type")
	private String otherType = "Juno Practitioner";


	/* OPTIONAL FIELDS (Implementation TBD)

	private String gender;
	private String preferredLocale;
	private String prefix;
	*/

	public static SSOUser fromProvider(Provider provider)
	{
		SSOUser user = new SSOUser();

		user.externalId = provider.getImdHealthUuid();
		user.firstName = provider.getFirstName();
		user.lastName = provider.getLastName();

		return user;
	}

	public static SSOUser fromProvider(ProviderData provider)
	{
		SSOUser user = new SSOUser();

		user.externalId = provider.getImdHealthUuid();
		user.firstName = provider.getFirstName();
		user.lastName = provider.getLastName();

		return user;
	}

	/**
	 * Check if a provider can be converted to an valid SSOUser.
	 *
	 * @param provider provider to check
	 * @return true if able to convert
	 */
	public static boolean canConvertProvider(ProviderData provider)
	{
		return provider != null;
	}
}
