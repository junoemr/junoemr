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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Billing;
import org.oscarehr.util.DateRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BillingDaoTest extends DaoTestFixtures
{
	@Autowired
	protected BillingDao billingDao;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("billing", "billingdetail","billingmaster", "provider","appointment","demographic", "lst_gender", "admission", "demographic_merged", "program", "health_safety", "provider", "providersite", "site", "program_team","log");
	}

	@Test
	public void testFindBillings() {
		List<Object[]> billings = billingDao.findBillings(1, new ArrayList<String>());
		assertNotNull(billings);

		billings = billingDao.findBillings(1, Arrays.asList(new String[] { "BLIYSD", "GVNUIO" }));
		assertNotNull(billings);
	}

	@Test
	public void testFindOtherBillings() {
		List<Billing> bs = billingDao.findBillings(1, "STA", "01", new Date(), new Date());
		assertNotNull(bs);

		bs = billingDao.findBillings(1, "STA", "01", new Date(), new Date());
		assertNotNull(bs);

		bs = billingDao.findBillings(1, "STA", "01", null, new Date());
		assertNotNull(bs);

		bs = billingDao.findBillings(1, "STA", "01", new Date(), null);
		assertNotNull(bs);

		bs = billingDao.findBillings(1, "STA", null, new Date(), new Date());
		assertNotNull(bs);

		bs = billingDao.findBillings(null, "STA", null, null, null);
		assertNotNull(bs);
	}

	@Test
	public void testFindMoreBillings() {
		List<Object[]> bs = billingDao.findBillings(10);
		assertNotNull(bs);
	}

	@Test
	public void testFindByProviderStatusAndDates() {
		assertNotNull(billingDao.findByProviderStatusAndDates("100", new ArrayList<String>(), null));
		assertNotNull(billingDao.findByProviderStatusAndDates("100", new ArrayList<String>(), new DateRange(null, null)));
		assertNotNull(billingDao.findByProviderStatusAndDates("100", new ArrayList<String>(), new DateRange(new Date(), null)));
		assertNotNull(billingDao.findByProviderStatusAndDates("100", new ArrayList<String>(), new DateRange(null, new Date())));
		assertNotNull(billingDao.findByProviderStatusAndDates("100", Arrays.asList(new String[] { "A", "B", "C" }), null));

	}

	@Test
	public void testGetMyMagicBillings() {
		assertNotNull(billingDao.getMyMagicBillings());
	}
	
	@Test
	public void testFindByManyThings() {
		boolean[] tt = new boolean[] { true,   	  true,   	  true,   	  true,   
				true, 	true, 	true, 	false,
				true, 	true, 	false,	true, 
				true, 	true, 	false,	false,
				true, 	false,	true, 	true, 
				true, 	false,	true, 	false,
				true, 	false,	false,	true, 
				true, 	false,	false,	false,
				false,	true, 	true, 	true, 
				false,	true, 	true, 	false,
				false,	true, 	false,	true, 
				false,	true, 	false,	false,
				false,	false,	true, 	true, 
				false,	false,	true, 	false,
				false,	false,	false,	true, 
				false,	false,	false,	false};
		
		for(int i = 0; i < tt.length; i += 4) {
			assertNotNull(billingDao.findByManyThings(null, null, null, null, null, tt[i], tt[i + 1], tt[i + 2], tt[i + 3]));
			assertNotNull(billingDao.findByManyThings("STS", "100", "2010-01-01", "2012-12-31", "100", tt[i], tt[i + 1], tt[i + 2], tt[i + 3]));
		}
	} 
	public void testGetByManyThings() {
		assertNotNull(billingDao.findBillingsByManyThings(100, new Date(), "OHIP", "SVC"));
	}
	@Test
	public void testCountBillings() {
		assertNotNull(billingDao.countBillings("DAI", "CR", new Date(), new Date()));
	}
        
    @Test
	public void testCountBillingVisitsByCreator() {
        assertNotNull(billingDao.countBillingVisitsByCreator("100", new Date(), new Date()));
    }

    @Test
	public void testCountBillingVisitsByProvider() {
        assertNotNull(billingDao.countBillingVisitsByProvider("100", new Date(), new Date()));
	}

    @Test
    public void testFindByProviderStatusForTeleplanFileWriter() {
	    assertNotNull(billingDao.findByProviderStatusForTeleplanFileWriter("HIN"));
    }

    @Test
    public void testFindOutstandingBills() {
	    assertNotNull(billingDao.findOutstandingBills(10, "BT", new ArrayList<String>()));
	    assertNotNull(billingDao.findOutstandingBills(10, "BT", Arrays.asList(new String[] {"S"})));
    }
}
