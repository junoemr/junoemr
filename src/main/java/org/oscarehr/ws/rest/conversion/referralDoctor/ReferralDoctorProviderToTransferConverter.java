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

package org.oscarehr.ws.rest.conversion.referralDoctor;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.to.model.ReferralDoctorTo1;
import org.springframework.stereotype.Component;

@Component
public class ReferralDoctorProviderToTransferConverter extends AbstractModelConverter<Provider, ReferralDoctorTo1>
{
	@Override
	public ReferralDoctorTo1 convert(Provider provider)
	{
		if (provider == null)
		{
			return null;
		}
		ReferralDoctorTo1 transfer = new ReferralDoctorTo1();
		// Due to this being legacy provider model, BeanUtils copy actually wouldn't bring many properties over
		transfer.setId(Integer.parseInt(provider.getProviderNo()));
		transfer.setFirstName(provider.getFirstName());
		transfer.setLastName(provider.getLastName());
		transfer.setReferralNo(provider.getOhipNo());
		transfer.setStreetAddress(provider.getAddress());
		transfer.setPhoneNumber(provider.getPhone());
		transfer.setFaxNumber(null); // no like field
		transfer.setSpecialtyType(null); // no like field

		return transfer;
	}
}
