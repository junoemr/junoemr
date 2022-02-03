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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.BillingPaymentType;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BillingPaymentTypeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected BillingPaymentTypeDao billingPaymentTypeDao;

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"billing_payment_type"
		};
	}


	@Test
	public void testCreate() throws Exception {
		BillingPaymentType entity = new BillingPaymentType();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		billingPaymentTypeDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindAll() throws Exception {
		
		BillingPaymentType bPT1 = new BillingPaymentType();
		EntityDataGenerator.generateTestDataForModelClass(bPT1);
		billingPaymentTypeDao.persist(bPT1);
		
		BillingPaymentType bPT2 = new BillingPaymentType();
		EntityDataGenerator.generateTestDataForModelClass(bPT2);
		billingPaymentTypeDao.persist(bPT2);
		
		BillingPaymentType bPT3 = new BillingPaymentType();
		EntityDataGenerator.generateTestDataForModelClass(bPT3);
		billingPaymentTypeDao.persist(bPT3);
		
		List<BillingPaymentType> expectedResult = new ArrayList<BillingPaymentType>(Arrays.asList(bPT1, bPT2, bPT3));
		List<BillingPaymentType> result = billingPaymentTypeDao.findAll();

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