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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.CtlBillingServiceAgeRules;
import org.oscarehr.util.SpringUtils;

import static org.junit.Assert.fail;

public class CtlBillingServiceAgeRulesDaoTest extends DaoTestFixtures {

	protected CtlBillingServiceAgeRulesDao dao = SpringUtils.getBean(CtlBillingServiceAgeRulesDao.class);

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("ctl_billingservice_age_rules");
	}

	@Test
	public void testFindByServiceCode() throws Exception {
		
		String serviceCode1 = "foo";
		String serviceCode2 = "bar";
		
		CtlBillingServiceAgeRules cBSAR1 = new CtlBillingServiceAgeRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSAR1);
		cBSAR1.setServiceCode(serviceCode1);
		dao.persist(cBSAR1);
		
		CtlBillingServiceAgeRules cBSAR2 = new CtlBillingServiceAgeRules();
		EntityDataGenerator.generateTestDataForModelClass(cBSAR2);
		cBSAR2.setServiceCode(serviceCode2);
		dao.persist(cBSAR2);

		List<CtlBillingServiceAgeRules> result = dao.findByServiceCode(serviceCode1);
		Assert.assertEquals("Find by code found more than one entity", 1, result.size());

		List<CtlBillingServiceAgeRules> result2 = dao.findByServiceCode(serviceCode2);
		Assert.assertEquals("Find by code found more than one entity", 1, result.size());
	}

	@Test(expected=javax.persistence.PersistenceException.class)
	public void testUniqueServiceCode()
	{
		String serviceCode = "baz";

		CtlBillingServiceAgeRules rules1 = new CtlBillingServiceAgeRules();
		rules1.setServiceCode(serviceCode);
		dao.persist(rules1);

		CtlBillingServiceAgeRules rules2 = new CtlBillingServiceAgeRules();
		rules2.setServiceCode(serviceCode);
		dao.persist(rules2);

		fail("CtlBillingServiceAgeRules must have a unique service code");
	}
}