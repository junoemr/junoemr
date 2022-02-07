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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.billing.CA.BC.model.TeleplanS00;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeleplanS00DaoTest extends DaoTestFixtures
{
	@Autowired
	public TeleplanS00Dao teleplanS00Dao;

	public TeleplanS00DaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"teleplanS00","provider"
		};
	}

	@Test
	public void testCreate() throws Exception {
		TeleplanS00 entity = new TeleplanS00();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		teleplanS00Dao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testFindByBillingNo() {
		teleplanS00Dao.findByBillingNo("101");
	}

	@Test
	public void testFindByOfficeNumbers() {
		assertNotNull(teleplanS00Dao.findByOfficeNumbers(Arrays.asList(new String[] {})));
		assertNotNull(teleplanS00Dao.findByOfficeNumbers(Arrays.asList(new String[] { "10", "20" })));
	}
	
	@Test
	public void testFindBgs() {
		assertNotNull(teleplanS00Dao.findBgs());
	}
}
