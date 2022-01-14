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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.entities.Billingmaster;
import oscar.entities.WCB;
import oscar.oscarBilling.ca.bc.administration.TeleplanCorrectionFormWCB;
import oscar.oscarBilling.ca.bc.data.BillingmasterDAO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BillingmasterDAOTest extends DaoTestFixtures
{
	@Autowired
	public BillingmasterDAO billingmasterDAO;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"billingmaster", "wcb", "billing", "teleplanC12", "demographic"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Billingmaster master = new Billingmaster();
		EntityDataGenerator.generateTestDataForModelClass(master);
		master.setBillingNo(99999);
		billingmasterDAO.save(master);

		int count = billingmasterDAO.updateBillingUnitForBillingNumber("NIHRENASEIBE", 99999);
		assertTrue(count == 1);
	}
	
	@Test
	public void testUpdateBillingUnitForBillingNumber() {
		Billingmaster b = new Billingmaster();
		b.setBillingUnit("AS");
		b.setBillingNo(999);
		billingmasterDAO.save(b);
		
		int i = billingmasterDAO.updateBillingUnitForBillingNumber("BU", 999);
		assertTrue(i == 1);
	}
	
	@Test
	public void testMarkListAsBilled() {
		Billingmaster b = new Billingmaster();
		b.setBillingUnit("AS");
		b.setBillingNo(999);
		billingmasterDAO.save(b);
		
		int i = billingmasterDAO.markListAsBilled(Arrays.asList(new String[] {String.valueOf(b.getBillingmasterNo())}));
		assertTrue(i == 1);
	}
	
	@Test
	public void testGetWcbByBillingNo() throws Exception {
		WCB wcb = new WCB();
		EntityDataGenerator.generateTestDataForModelClass(wcb);
		wcb.setBilling_no(999);
		billingmasterDAO.save(wcb);
		
		assertNotNull(billingmasterDAO.getWcbByBillingNo(999));
	}

	@Test
	public void testGetBillingMasterByVariousFields() {
		billingmasterDAO.getBillingMasterByVariousFields("ST", null, null, null);
		billingmasterDAO.getBillingMasterByVariousFields("ST", null, null, "01-01-2012");
		billingmasterDAO.getBillingMasterByVariousFields("ST", null, "01-01-2011", "01-01-2012");
		billingmasterDAO.getBillingMasterByVariousFields("ST", "01", null, null);
		billingmasterDAO.getBillingMasterByVariousFields("ST", "01", "01-01-2011", "01-01-2012");
		
	}
	
	@Test
	public void testSelect_user_bill_report_wcb2() throws Exception {
		assertEquals(SchemaUtils.loadFileIntoMySQL(System.getProperty("basedir") + "/src/test/resources/select_user_bill_report_wcb_data.sql"),0);
		
		List<Object[]> results = billingmasterDAO.select_user_bill_report_wcb(1);
		assertNotNull(results);
		TeleplanCorrectionFormWCB f = new TeleplanCorrectionFormWCB(results);
		assertNotNull(f);
		
	}
	
	@Test
	public void testSearch_teleplanbill() {
		assertNotNull(billingmasterDAO.search_teleplanbill(1));
	}

    @Test
    public void testFindByDemoNoCodeAndStatuses() {
	    assertNotNull(billingmasterDAO.findByDemoNoCodeAndStatuses(100, "10", Arrays.asList(new String[] {"A"})));
    }
    
    @Test
    public void testCountByDemoNoCodeStatusesAndYear() {
	    assertNotNull(billingmasterDAO.findByDemoNoCodeStatusesAndYear(100, new Date(), "CODE"));
    }
	
}
