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
package org.oscarehr.billing.CA.BC.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.billing.CA.BC.model.BillingTrayFee;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.oscarBilling.ca.bc.data.SupServiceCodeAssocDAO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupServiceCodeAssocDAOTest extends DaoTestFixtures
{
	@Autowired
	public SupServiceCodeAssocDAO supServiceCodeAssocDAO;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"billing_trayfees", "billingservice"
		};
	}

	@Test
	public void testAllAtOnce() throws Exception {
		BillingTrayFee fee = new BillingTrayFee();
		EntityDataGenerator.generateTestDataForModelClass(fee);
		fee.setBillingServiceNo("999999");
		fee.setBillingServiceTrayNo("0000000");
		fee.setId(null);
		
		supServiceCodeAssocDAO.persist(fee);
		
		List<?> list = supServiceCodeAssocDAO.getServiceCodeAssociactions();
		assertFalse(list.isEmpty());
		
		Map<?, ?> map = supServiceCodeAssocDAO.getAssociationKeyValues();
		assertFalse(map.isEmpty());
		
		supServiceCodeAssocDAO.saveOrUpdateServiceCodeAssociation(fee.getId().toString(), "888888");
		supServiceCodeAssocDAO.saveOrUpdateServiceCodeAssociation("77777777", "888888");
		
		supServiceCodeAssocDAO.deleteServiceCodeAssociation(fee.getId().toString());
		
		fee = supServiceCodeAssocDAO.find(fee.getId());
		assertTrue(fee == null);
	}
}
