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

import integration.tests.util.data.PatientTestCollection;
import integration.tests.util.data.PatientTestData;
import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.data.ProviderTestData;
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.ProviderSitePK;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.boot.test.context.TestComponent;

import static integration.tests.util.data.PatientTestCollection.patientLNames;
import static integration.tests.util.data.ProviderTestCollection.providerLNames;
import static integration.tests.util.data.SiteTestCollection.shortNames;
import static integration.tests.util.data.SiteTestCollection.siteNames;
import static integration.tests.util.data.SiteTestCollection.themeColors;
import static org.oscarehr.common.dao.utils.AuthUtils.TEST_PROVIDER_ID;

@TestComponent
public class DatabaseUtil
{
	@Autowired
	DemographicService demoService;

	@Autowired
	ProviderService providerService;

	@Autowired
	SiteDao siteDao;

	@Autowired
	ProviderSiteDao providerSiteDao;

	public void createTestDemographic()
	{
		for (String patientLName : patientLNames)
		{
			Demographic demo = new Demographic();
			PatientTestData patient = PatientTestCollection.patientMap.get(patientLName);
			LocalDate dob = LocalDate.of(Integer.parseInt(patient.dobYear), Integer.parseInt(patient.dobMonth), Integer.parseInt(patient.dobDate));
			demo.setDateOfBirth(dob);
			demo.setFirstName(patient.firstName);
			demo.setLastName(patient.lastName);
			demo.setSex(patient.sex);
			demo.setHin(patient.hin);
			demo.setFamilyDoctor("<rdohip></rdohip><rd></rd>");
			demoService.addNewDemographicRecord(TEST_PROVIDER_ID, demo, null, new HashSet<DemographicExt>());
		}
	}
	public void createTestProvider()
	{
		for (String provider : providerLNames)
		{
			ProviderData demoProvider = new ProviderData();
			ProviderTestData dr = ProviderTestCollection.providerMap.get(provider);
			demoProvider.setProviderNo(Integer.parseInt(dr.providerNo));
			demoProvider.setFirstName(dr.firstName);
			demoProvider.setLastName(dr.lastName);
			providerService.addNewProvider(TEST_PROVIDER_ID, demoProvider, "");
		}
	}
	public void createProviderSite()
	{
		Site newSite = new Site();
		newSite.setName(siteNames[0]);
		newSite.setBgColor(themeColors[0]);
		newSite.setShortName(shortNames[0]);
		newSite.setStatus((byte) 1);
		newSite.setProvince("BC");
		siteDao.persist(newSite);
		Integer siteAddedId = newSite.getId();

		ProviderSite providerAssignSite = new ProviderSite();
		providerAssignSite.setId(new ProviderSitePK());
		providerAssignSite.getId().setProviderNo(TEST_PROVIDER_ID);
		providerAssignSite.getId().setSiteId(siteAddedId);
		providerSiteDao.persist(providerAssignSite);
	}
}
