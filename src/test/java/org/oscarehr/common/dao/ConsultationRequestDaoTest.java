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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.ConsultationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.util.ConversionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConsultationRequestDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ConsultationRequestDao consultationRequestDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"consultationRequests","professionalSpecialists","consultationServices", "demographic", "lst_gender", "admission", "demographic_merged", "program",
			"health_safety", "provider", "providersite", "site", "program_team","log", "Facility", "demographicExt"
		};
	}

	@Test
	public void testGetReferrals() {
		Date past = ConversionUtils.fromDateString("1799-06-06");
		ConsultationRequest cr = new ConsultationRequest();
		cr.setProviderNo("0");
		cr.setReferralDate(past);
		consultationRequestDao.persist(cr);
		
		cr = new ConsultationRequest();
		cr.setProviderNo("0");
		cr.setReferralDate(ConversionUtils.fromDateString("1891-05-15"));
		consultationRequestDao.persist(cr);
		
		

		// should include both - cutoff is in the future
		List<ConsultationRequest> crs = consultationRequestDao.getReferrals("1", new Date());
		assertNotNull(crs);
		assertTrue(crs.isEmpty());

		// should include both - cutoff is in the future
		crs = consultationRequestDao.getReferrals("0", new Date());
		assertNotNull(crs);
		assertTrue(crs.size() == 2);

		// should include only one - current referral should not be included
		crs = consultationRequestDao.getReferrals("0", ConversionUtils.fromDateString("1800-01-01"));

		assertNotNull(crs);
		assertTrue(crs.size() == 1);
	}

    @Test
    public void testFindRequestsByDemoNo() {
	    assertNotNull(consultationRequestDao.findRequestsByDemoNo(100, new Date()));
    }
	
}
