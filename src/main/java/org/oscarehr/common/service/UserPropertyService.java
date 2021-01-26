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
package org.oscarehr.common.service;

import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for reading/writing user properties in the "property" table.
 */
@Service
public class UserPropertyService
{
	@Autowired
	@Qualifier("UserPropertyDAO")
	UserPropertyDAO userPropertyDao;

	/**
	 * Retrieves the Boolean value of a property in the database that is stored as a bool ("true", "false").
	 *
	 * @param name The name of the property.
	 * @return Boolean true if the property is true, false if it is false, null if the property does not exist or
	 * has a null value.
	 * @throws IllegalArgumentException If the property has a value that isn't a valid Boolean value.
	 */
	public Boolean getPropertyBoolValue(String name)
	{
		Boolean value = null;
		String valueStr = getPropertyValue(name);

		if ("TRUE".equalsIgnoreCase(valueStr))
		{
			value = Boolean.TRUE;
		}
		else if ("FALSE".equalsIgnoreCase(valueStr))
		{
			value = Boolean.FALSE;
		}
		else if (valueStr != null)
		{
			throw new IllegalArgumentException("Property \"" + name + "\" in the database is not a valid bool.");
		}

		return value;
	}

	/**
	 * Retrieves the value of a property in the database.
	 *
	 * @param name Name of the property
	 * @return The string value of the property (or null if not found).
	 */
	private String getPropertyValue(String name)
	{
		String value = null;
		UserProperty property = userPropertyDao.getProp(name);

		if (property != null)
		{
			value = property.getValue();
		}

		return value;
	}

}