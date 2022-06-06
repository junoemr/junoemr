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
/**
 * @author Shazib
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Hl7TextInfoDaoTest extends DaoTestFixtures
{
	@Autowired
	protected Hl7TextInfoDao hl7TextInfoDao;

	@Override
	protected List<String> getSimpleExceptionTestExcludes()
	{
		List<String> result = super.getSimpleExceptionTestExcludes();
		result.add("listBasicInfoByDemographicNo");
		result.add("countByDemographicNo");
		return result;
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"hl7TextInfo", "patientLabRouting", "hl7TextInfo", "providerLabRouting", "ctl_document", "demographic", "hl7TextMessage"
		};
	}

	@Test
	public void testCreateUpdateLabelByLabNumber() {
		hl7TextInfoDao.createUpdateLabelByLabNumber("10", 10);
	}

	@Test
	public void testFindByDemographicId() {
		hl7TextInfoDao.findByDemographicId(10);
	}

	@Test
	public void testFindByLabId() {
		hl7TextInfoDao.findByLabId(10);
	}

	@Test
	public void testfindByLabIdViaMagic() {
		hl7TextInfoDao.findByLabIdViaMagic(10, true);
	}

	@Test
	public void testfindLabAndDocsViaMagic() {
		Integer page = 0;
		Integer pageSize = 10;

		boolean isPaged, mixLabsAndDocs, isAbnormal, searchProvider, patientSearch;
		boolean[] truthTable = new boolean[] { 
				true,   true,   true,   true,   true,   
				true, 	true, 	true, 	true, 	false, 
				true, 	true, 	true, 	false, 	true, 
				true, 	true, 	true, 	false, 	false, 
				true, 	true, 	false, 	true, 	true, 
				true, 	true, 	false, 	true, 	false, 
				true, 	true, 	false, 	false, 	true, 
				true, 	true, 	false, 	false, 	false, 
				true, 	false, 	true, 	true, 	true, 
				true, 	false, 	true, 	true, 	false, 
				true, 	false, 	true, 	false, 	true, 
				true, 	false, 	true, 	false, 	false, 
				true, 	false, 	false, 	true, 	true, 
				true, 	false, 	false, 	true, 	false, 
				true, 	false, 	false, 	false, 	true, 
				true, 	false, 	false, 	false, 	false, 
				false, 	true, 	true, 	true, 	true, 
				false, 	true, 	true, 	true, 	false, 
				false, 	true, 	true, 	false, 	true, 
				false, 	true, 	true, 	false, 	false, 
				false, 	true, 	false, 	true, 	true, 
				false, 	true, 	false, 	true, 	false, 
				false, 	true, 	false, 	false, 	true, 
				false, 	true, 	false, 	false, 	false, 
				false, 	false, 	true, 	true, 	true, 
				false, 	false, 	true, 	true, 	false, 
				false, 	false, 	true, 	false, 	true, 
				false, 	false, 	true, 	false, 	false, 
				false, 	false, 	false, 	true, 	true, 
				false, 	false, 	false, 	true, 	false, 
				false, 	false, 	false, 	false, 	true, 
				false, 	false, 	false, 	false, 	false
			};

		for (int i = 0; i < truthTable.length; i = i + 5) {
			isPaged = truthTable[i];
			mixLabsAndDocs = truthTable[i + 1];
			isAbnormal = truthTable[i + 2];
			searchProvider = truthTable[i + 3];
			patientSearch = truthTable[i + 4];

			hl7TextInfoDao.findLabAndDocsViaMagic("PROVIDER", "DEMOGRAPHIC", "FNAME", "LNAME", "HIN", "STATUS", isPaged, page, pageSize, mixLabsAndDocs, isAbnormal, searchProvider, patientSearch);
			hl7TextInfoDao.findLabAndDocsViaMagic("0", "0", "", "", "", "", isPaged, page, pageSize, mixLabsAndDocs, isAbnormal, searchProvider, patientSearch);
		}
	}

	@Test
	public void testfindLabId() {
		hl7TextInfoDao.findLabId(0);
	}

	@Test
	public void testfindLabsViaMagic() {
		hl7TextInfoDao.findLabsViaMagic("GVNO", "GVNO", "GVNO", "GVNO", "GVNO");
	}

	@Test
	public void testgetAllLabsByLabNumberResultStatus() {
		hl7TextInfoDao.getAllLabsByLabNumberResultStatus();
	}

	@Test
	public void testgetMatchingLabs() {
		hl7TextInfoDao.getMatchingLabs("BLYA");
	}

	@Test
	public void testsearchByFillerOrderNumber() {
		hl7TextInfoDao.searchByFillerOrderNumber("PRSHA", "ZHPA");
	}

	@Test
	public void testupdateReportStatusByLabId() {
		hl7TextInfoDao.updateReportStatusByLabId("STR", 0);
	}

	@Test
	public void testupdateResultStatusByLabId() {
		hl7TextInfoDao.updateResultStatusByLabId("STS", 0);
	}

    @Test
    public void test() {
	    assertNotNull(hl7TextInfoDao.findDisciplines(100));
    }

}
