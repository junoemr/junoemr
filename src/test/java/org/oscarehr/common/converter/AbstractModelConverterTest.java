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
package org.oscarehr.common.converter;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.util.MiscUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractModelConverterTest<C extends AbstractModelConverter<T, K>, T, K>
{
	private static final Logger logger = MiscUtils.getLogger();

	protected abstract C getConverter();

	@Test
	public void test_convertNull()
	{
		Assert.assertNull("Converter should handle null input", getConverter().convert((T) null));
	}

	/**
	 * ensure all fields on the destination object are not null
	 * @param destination the destination object
	 * @param ignoreProperties properties to ignore
	 */
	protected void testPropertiesNotNull(K destination, String... ignoreProperties)
	{
		List<String> errorFields = new LinkedList<>();

		for(Field field : destination.getClass().getDeclaredFields())
		{
			String fieldName = field.getName();
			field.setAccessible(true); // let us access private fields for this test

			// skip ignored fields
			if(Arrays.asList(ignoreProperties).contains(fieldName))
			{
				continue;
			}

			Object value = null;
			try
			{
				value = field.get(destination);
			}
			catch(IllegalAccessException e)
			{
				logger.error("Field access error", e);
				errorFields.add(fieldName);
			}
			if(value == null)
			{
				errorFields.add(fieldName);
			}
		}
		Assert.assertTrue("null fields after conversion: " + errorFields, errorFields.isEmpty());
	}
}