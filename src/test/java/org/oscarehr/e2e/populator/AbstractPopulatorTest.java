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
package org.oscarehr.e2e.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.marc.everest.rmim.uv.cdar2.pocd_mt000040uv.ClinicalDocument;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.e2e.constant.Constants;
import org.oscarehr.e2e.director.E2ECreator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractPopulatorTest extends DaoTestFixtures
{
	protected static ClinicalDocument clinicalDocument;

	@BeforeClass
	public static void abstractBeforeClass() throws Exception
	{
		SchemaUtils.restoreTable(Constants.Runtime.TABLES);
		assertEquals(0, SchemaUtils.loadFileIntoMySQL(Constants.Runtime.E2E_SETUP));

		clinicalDocument = E2ECreator.createEmrConversionDocument(Constants.Runtime.VALID_DEMOGRAPHIC);
		assertNotNull(clinicalDocument);
	}
}
