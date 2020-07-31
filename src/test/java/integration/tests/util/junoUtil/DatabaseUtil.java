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
package integration.tests.util.junoUtil;

import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.data.ProviderTestData;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.SpringUtils;

import java.time.LocalDate;
import java.util.ArrayList;

import static integration.tests.util.data.ProviderTestCollection.providerLNames;

public class DatabaseUtil
{
	public static Demographic createTestDemographic()
	{
		DemographicService demoService = (DemographicService)SpringUtils.getBean("demographic.service.DemographicService");
		Demographic demo = new Demographic();
		demo.setDateOfBirth(LocalDate.now());
		demo.setFirstName("test");
		demo.setLastName("test");
		demo.setSex("F");
		return demoService.addNewDemographicRecord(AuthUtils.TEST_PROVIDER_ID, demo, null, new ArrayList<DemographicExt>());
	}
	public static void createTestProvider()
	{
		ProviderService providerService = (ProviderService) SpringUtils.getBean("provider.service.ProviderService");
		for (String provider : providerLNames)
		{
			ProviderData demoProvider = new ProviderData();
			ProviderTestData dr = ProviderTestCollection.providerMap.get(provider);
			demoProvider.setProviderNo(Integer.parseInt(dr.providerNo));
			demoProvider.setFirstName(dr.firstName);
			demoProvider.setLastName(dr.lastName);
			providerService.addNewProvider(AuthUtils.TEST_PROVIDER_ID, demoProvider, null);
		}
	}


}
