/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.dataMigration.mapper.cds.out;


import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class CDSLabExportMapperTest
{
	
	@Test
	public void testsplitLabReferenceRange_Null()
	{
		CDSLabExportMapper cdsLabExportMapper = new CDSLabExportMapper();
		String range = null;
		String[] result = ReflectionTestUtils.invokeMethod(cdsLabExportMapper, "splitLabReferenceRange", range);
		assertNull(result);
	}
	
	@Test
	public void testsplitLabReferenceRange_LowerOnly()
	{
		CDSLabExportMapper cdsLabExportMapper = new CDSLabExportMapper();
		String range = "1-";
		String[] expected = {"1", null};
		String[] result = ReflectionTestUtils.invokeMethod(cdsLabExportMapper, "splitLabReferenceRange", range);
		assertArrayEquals(expected, result);
	}

	@Test
	public void testsplitLabReferenceRange_UpperOnly()
	{
		CDSLabExportMapper cdsLabExportMapper = new CDSLabExportMapper();
		String range = "-2";
		String[] expected = {null, "2"};
		String[] result = ReflectionTestUtils.invokeMethod(cdsLabExportMapper, "splitLabReferenceRange", range);
		assertArrayEquals(expected, result);
	}

	@Test
	public void testsplitLabReferenceRange_Full()
	{
		CDSLabExportMapper cdsLabExportMapper = new CDSLabExportMapper();
		String range = "1-2";
		String[] expected = {"1", "2"};
		String[] result = ReflectionTestUtils.invokeMethod(cdsLabExportMapper, "splitLabReferenceRange", range);
		assertArrayEquals(expected, result);
	}

	@Test
	public void testsplitLabReferenceRange_Invalid()
	{
		CDSLabExportMapper cdsLabExportMapper = new CDSLabExportMapper();
		String range = "1 to 2";
		String[] result = ReflectionTestUtils.invokeMethod(cdsLabExportMapper, "splitLabReferenceRange", range);
		assertNull(result);
	}
}
