/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.CtlBillingServiceSexRules;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CtlBillingServiceSexRulesDaoTest extends DaoTestFixtures
{
	@Autowired
	protected CtlBillingServiceSexRulesDao ctlBillingServiceSexRulesDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"ctl_billingservice_sex_rules", "ctl_billingservice_age_rules"
		};
	}

	@Test
	public void testCreate() throws Exception {
		CtlBillingServiceSexRules entity = new CtlBillingServiceSexRules();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(null);
		ctlBillingServiceSexRulesDao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testFindByServiceCode() throws Exception {
		
		String serviceCode1 = "alpha", serviceCode2 = "bravo";
		
		CtlBillingServiceSexRules cBSSR1 = new CtlBillingServiceSexRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSSR1);
		cBSSR1.setServiceCode(serviceCode1);
		ctlBillingServiceSexRulesDao.persist(cBSSR1);
		
		CtlBillingServiceSexRules cBSSR2 = new CtlBillingServiceSexRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSSR2);
		cBSSR2.setServiceCode(serviceCode1);
		ctlBillingServiceSexRulesDao.persist(cBSSR2);
		
		CtlBillingServiceSexRules cBSSR3 = new CtlBillingServiceSexRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSSR3);
		cBSSR3.setServiceCode(serviceCode2);
		ctlBillingServiceSexRulesDao.persist(cBSSR3);
		
		CtlBillingServiceSexRules cBSSR4 = new CtlBillingServiceSexRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSSR4);
		cBSSR4.setServiceCode(serviceCode1);
		ctlBillingServiceSexRulesDao.persist(cBSSR4);
		
		List<CtlBillingServiceSexRules> expectedResult = new ArrayList<CtlBillingServiceSexRules>(Arrays.asList(cBSSR1, cBSSR2, cBSSR4));
		List<CtlBillingServiceSexRules> result = ctlBillingServiceSexRulesDao.findByServiceCode(serviceCode1);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
}