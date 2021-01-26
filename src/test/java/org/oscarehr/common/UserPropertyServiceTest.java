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
package org.oscarehr.common;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.common.service.UserPropertyService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

public class UserPropertyServiceTest
{
	@Autowired
	@InjectMocks
	private UserPropertyService userPropertyService;

	@Mock
	private UserPropertyDAO userPropertyDAO;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Service should return the True Boolean if the property value is "true".
	 */
	@Test
	public void getPropertyBoolValue_TrueLowerCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		UserProperty property = new UserProperty();
		property.setValue("true");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return the True Boolean if the property value is "TRUE".
	 */
	@Test
	public void getPropertyBoolValue_TrueUpperCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		UserProperty property = new UserProperty();
		property.setValue("TRUE");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return the False Boolean if the property value is "false".
	 */
	@Test
	public void getPropertyBoolValue_FalseLowerCase_ReturnsFalse()
	{
		String propertyName = "FalseProperty";
		UserProperty property = new UserProperty();
		property.setValue("false");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return the False Boolean if the property value is "FALSE".
	 */
	@Test
	public void getPropertyBoolValue_FalseUpperCase_ReturnsFalse()
	{
		String propertyName = "FalseProperty";
		UserProperty property = new UserProperty();
		property.setValue("FALSE");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return null if the property is not in the database.
	 */
	@Test
	public void getPropertyBoolValue_PropertyNotFound_ReturnsNull()
	{
		String propertyName = "NonExistentProperty";
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(null);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertNull(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return null if the property's value is null.
	 */
	@Test
	public void getPropertyBoolValue_PropertyValueIsNull_ReturnsNull()
	{
		String propertyName = "NullProperty";
		UserProperty property = new UserProperty();
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		Boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertNull(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should throw an exception if the property's value is not true, false, or null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getPropertyBoolValue_PropertyValueIsInvalid_ThrowsException()
	{
		String propertyName = "InvalidProperty";
		UserProperty property = new UserProperty();
		property.setValue("Invalid Value");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		userPropertyService.getPropertyBoolValue(propertyName);
	}
}
