/**
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * This software was written for the
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 */
package org.oscarehr.e2e.director;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.e2e.constant.Constants;
import org.oscarehr.util.DatabaseTestBase;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class E2ECreatorTest extends DaoTestFixtures
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	protected DemographicDao demographicDao = null;
	Demographic demographic = null;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"demographic", "lst_gender", "admission",
			"demographic_merged", "program", "health_safety", "provider", "providersite",
			"site", "program_team","log", "Facility","demographicExt", "issue",
			"casemgmt_issue", "ResourceStorage", "clinic",
			"casemgmt_note", "preventions", "patientLabRouting", "drugs", "dxresearch",
			"allergies", "measurements", "secRole"
		};
	}

	@Before
	public void before() throws Exception
	{
		if(!SchemaUtils.inited)
		{
			logger.info("dropAndRecreateDatabase");
			SchemaUtils.dropAndRecreateDatabase();
		}
		//DaoTestFixtures.setupBeanFactory();
		//demographicDao = SpringUtils.getBean(DemographicDao.class);

		demographic = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(demographic);
		demographic.setDemographicNo(null);
		demographic.setPatientStatus("AC");
		demographic.setProviderNo(null);
		demographicDao.save(demographic);
	}

	@SuppressWarnings("unused")
	@Test(expected=UnsupportedOperationException.class)
	public void instantiationTest() {
		new E2ECreator();
	}

	@Test
	public void createEmrConversionDocumentTest() {
		assertNotNull(E2ECreator.createEmrConversionDocument(Constants.Runtime.VALID_DEMOGRAPHIC));
	}

	@Test
	public void emptyCreateEmrConversionDocumentTest() {
		assertNull(E2ECreator.createEmrConversionDocument(Constants.Runtime.EMPTY_DEMOGRAPHIC));
	}
}
