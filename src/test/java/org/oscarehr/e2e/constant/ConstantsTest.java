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
package org.oscarehr.e2e.constant;

import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.e2e.constant.BodyConstants;
import org.oscarehr.e2e.constant.Constants;
import org.oscarehr.e2e.constant.Mappings;

public class ConstantsTest extends DaoTestFixtures
{
	@SuppressWarnings("unused")
	@Test(expected=UnsupportedOperationException.class)
	public void bodyConstantsInstantiationTest()
	{
		new BodyConstants();
	}

	@SuppressWarnings("unused")
	@Test(expected=UnsupportedOperationException.class)
	public void constantsInstantiationTest()
	{
		new Constants();
	}

	@SuppressWarnings("unused")
	@Test(expected=UnsupportedOperationException.class)
	public void mappingsInstantiationTest()
	{
		new Mappings();
	}
}
