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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.to.model.ReferralDoctorTo1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ReferralDoctorProviderToTransferConverterTest
{
	@InjectMocks
	private ReferralDoctorProviderToTransferConverter referralDoctorProviderToTransferConverter;

	@Test
	public void convert_testNull()
	{
		Provider provider = null;
		assertNull(referralDoctorProviderToTransferConverter.convert(provider));
	}

	@Test
	public void convert_typicalProvider()
	{
		Provider provider = new Provider();
		ReferralDoctorTo1 expectedReferralDoctor = new ReferralDoctorTo1();
		// Set up both concurrently on our side with what we expect
		provider.setProviderNo("21");
		provider.setFirstName("Test");
		provider.setLastName("Conversion");
		provider.setPhone("1-23-456-7890");
		provider.setOhipNo("12345");
		provider.setAddress("501 Belleville St, Victoria, B.C.");

		expectedReferralDoctor.setId(21);
		expectedReferralDoctor.setFirstName("Test");
		expectedReferralDoctor.setLastName("Conversion");
		expectedReferralDoctor.setPhoneNumber("1-23-456-7890");
		expectedReferralDoctor.setReferralNo("12345");
		expectedReferralDoctor.setStreetAddress("501 Belleville St, Victoria, B.C.");
		expectedReferralDoctor.setSpecialtyType(null);
		expectedReferralDoctor.setFaxNumber(null);

		// These two objects are guaranteed to be different objects, so a direct equality will fail
		// Check individual fields instead
		ReferralDoctorTo1 convertedRecord = referralDoctorProviderToTransferConverter.convert(provider);
		assertEquals("IDs differ", expectedReferralDoctor.getId(), convertedRecord.getId());
		assertEquals("First name differs", expectedReferralDoctor.getFirstName(), convertedRecord.getFirstName());
		assertEquals("Last name differs", expectedReferralDoctor.getLastName(), convertedRecord.getLastName());
		assertEquals("Phone number differs", expectedReferralDoctor.getPhoneNumber(), convertedRecord.getPhoneNumber());
		assertEquals("Address differs", expectedReferralDoctor.getStreetAddress(), convertedRecord.getStreetAddress());
		assertEquals("Referral no differs", expectedReferralDoctor.getReferralNo(), convertedRecord.getReferralNo());
		assertEquals("Specialty differs", expectedReferralDoctor.getSpecialtyType(), convertedRecord.getSpecialtyType());
		assertEquals("Fax number differs", expectedReferralDoctor.getFaxNumber(), convertedRecord.getFaxNumber());
	}
}