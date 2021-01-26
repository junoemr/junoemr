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
	 * Service should return true if the property value is "true".
	 */
	@Test
	public void getPropertyBoolValue_TrueLowerCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		UserProperty property = new UserProperty();
		property.setValue("true");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return true if the property value is "TRUE".
	 */
	@Test
	public void getPropertyBoolValue_TrueUpperCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		UserProperty property = new UserProperty();
		property.setValue("TRUE");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return true if the property value is "ON".
	 */
	@Test
	public void getPropertyBoolValue_OnUpperCase_ReturnsTrue()
	{
		String propertyName = "OnProperty";
		UserProperty property = new UserProperty();
		property.setValue("ON");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return true if the property value is "on".
	 */
	@Test
	public void getPropertyBoolValue_OnLowerCase_ReturnsTrue()
	{
		String propertyName = "OnProperty";
		UserProperty property = new UserProperty();
		property.setValue("on");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return true if the property value is "YES".
	 */
	@Test
	public void getPropertyBoolValue_YesUpperCase_ReturnsTrue()
	{
		String propertyName = "OnProperty";
		UserProperty property = new UserProperty();
		property.setValue("YES");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return true if the property value is "yes".
	 */
	@Test
	public void getPropertyBoolValue_YesLowerCase_ReturnsTrue()
	{
		String propertyName = "OnProperty";
		UserProperty property = new UserProperty();
		property.setValue("yes");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertTrue(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return false if the property value is "FALSE".
	 */
	@Test
	public void getPropertyBoolValue_FalseUpperCase_ReturnsFalse()
	{
		String propertyName = "FalseProperty";
		UserProperty property = new UserProperty();
		property.setValue("FALSE");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return false if the property value is "false".
	 */
	@Test
	public void getPropertyBoolValue_FalseLowerCase_ReturnsFalse()
	{
		String propertyName = "FalseProperty";
		UserProperty property = new UserProperty();
		property.setValue("false");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return false if the property value is not a boolean value.
	 */
	@Test
	public void getPropertyBoolValue_OtherValue_ReturnsFalse()
	{
		String propertyName = "OtherProperty";
		UserProperty property = new UserProperty();
		property.setValue("Other");
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return false if the property is not in the database.
	 */
	@Test
	public void getPropertyBoolValue_PropertyNotFound_ReturnsNull()
	{
		String propertyName = "NonExistentProperty";
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(null);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}

	/**
	 * Service should return false if the property's value is null.
	 */
	@Test
	public void getPropertyBoolValue_PropertyValueIsNull_ReturnsFalse()
	{
		String propertyName = "NullProperty";
		UserProperty property = new UserProperty();
		Mockito.when(userPropertyDAO.getProp(propertyName)).thenReturn(property);

		boolean actual = userPropertyService.getPropertyBoolValue(propertyName);

		assertFalse(actual);
		Mockito.verify(userPropertyDAO, times(1)).getProp(propertyName);
	}
}
