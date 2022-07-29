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
package oscar.oscarLab.ca.all.parsers;

import ca.uhn.hl7v2.HL7Exception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.oscarehr.util.DatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractMessageHandlerITBase extends DatabaseTestBase
{
	@Autowired
	protected ProviderDataDao providerDataDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
				"provider"
		};
	}

	@BeforeClass
	public static void classSetUp() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException
	{
		if(!SchemaUtils.inited)
		{
			SchemaUtils.dropAndRecreateDatabase();
		}
		DaoTestFixtures.setupBeanFactory();
	}

	@Before
	public void setup()
	{
		List<ProviderData> sampleProviders = getTestProviders();
		for(ProviderData provider : sampleProviders)
		{
			providerDataDao.persist(provider);
		}
	}

	protected abstract MessageHandler getTestHandler() throws HL7Exception;

	protected abstract List<ProviderData> getTestProviders();

	protected abstract Map<String, List<String>> getProviderMatchingMap();


	@Test
	public void test_getProviderMatchingCriteria() throws HL7Exception
	{
		MessageHandler handler = getTestHandler();
		Map<String, List<String>> expectedMatchingMap = getProviderMatchingMap();

		for(Map.Entry<String, List<String>> entry : expectedMatchingMap.entrySet())
		{
			String routingId = entry.getKey();
			List<String> expectedProviderIds = entry.getValue();

			ProviderCriteriaSearch criteriaSearch = handler.getProviderMatchingCriteria(routingId);
			List<ProviderData> matchingProviders = providerDataDao.criteriaSearch(criteriaSearch);

			List<String> matchingProviderIds = matchingProviders.stream().map(ProviderData::getId).collect(Collectors.toList());

			Assert.assertEquals(handler.getClass().getSimpleName() + " provider matching criteria does not match expected number of providers.\n" +
					"ProviderMatchingMap test key: " + routingId,
					expectedProviderIds.size(), matchingProviderIds.size());

			for(String providerId: expectedProviderIds)
			{
				Assert.assertTrue("expected providerId not present in the actual results", matchingProviderIds.contains(providerId));
			}
		}
	}

	protected ProviderData buildSimpleProvider(String id, String firstName, String lastName)
	{
		ProviderData providerData = new ProviderData();
		providerData.setId(id);
		providerData.setFirstName(firstName);
		providerData.setLastName(lastName);
		providerData.setSex(Person.SEX.FEMALE.getValue());
		providerData.setLastUpdateDate(new Date());
		providerData.setSpecialty("");
		providerData.setStatus(ProviderData.PROVIDER_STATUS_ACTIVE);
		providerData.setProviderType(ProviderData.PROVIDER_TYPE_DOCTOR);
		return providerData;
	}

}