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

package org.oscarehr.preferences.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

public class SystemPreferenceServiceTest
{
	@Autowired
	@InjectMocks
	private SystemPreferenceService systemPreferenceService;

	@Mock
	private PropertyDao propertyDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Service should return true if the property value is "true".
	 */
	@Test
	public void isPreferenceEnabled_TrueLowerCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("true");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property value is "TRUE".
	 */
	@Test
	public void isPreferenceEnabled_TrueUpperCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("TRUE");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property value is "on".
	 */
	@Test
	public void isPreferenceEnabled_OnLowerCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("on");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property value is "ON".
	 */
	@Test
	public void isPreferenceEnabled_OnUpperCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("ON");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property value is "yes".
	 */
	@Test
	public void isPreferenceEnabled_YesLowerCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("yes");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property value is "YES".
	 */
	@Test
	public void isPreferenceEnabled_YesUpperCase_ReturnsTrue()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("YES");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return false if the property value is "false".
	 */
	@Test
	public void isPreferenceEnabled_FalseLowerCase_ReturnsFalse()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("false");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertFalse(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return false if the property value is "FALSE".
	 */
	@Test
	public void isPreferenceEnabled_FalseUpperCase_ReturnsFalse()
	{
		String propertyName = "TrueProperty";
		Property property = new Property();
		property.setValue("FALSE");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertFalse(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return false if the property value is not a boolean value.
	 */
	@Test
	public void isPreferenceEnabled_OtherValue_ReturnsFalse()
	{
		String propertyName = "OtherProperty";
		Property property = new Property();
		property.setValue("Other");
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertFalse(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return true if the property is not in the database and the default value is true.
	 */
	@Test
	public void isPreferenceEnabled_PropertyNotFoundAndDefaultIsTrue_ReturnsTrue()
	{
		String propertyName = "NonExistentProperty";
		List<Property> propertyList = new ArrayList<>(0);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, true);

		assertTrue(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return false if the property is not in the database and the default value is false.
	 */
	@Test
	public void isPreferenceEnabled_PropertyNotFoundAndDefaultIsFalse_ReturnsFalse()
	{
		String propertyName = "NonExistentProperty";
		List<Property> propertyList = new ArrayList<>(0);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertFalse(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}

	/**
	 * Service should return false if the property's value is null.
	 */
	@Test
	public void isPreferenceEnabled_PropertyValueIsNull_ReturnsFalse()
	{
		String propertyName = "OtherProperty";
		Property property = new Property();
		List<Property> propertyList = new ArrayList<>(1);
		propertyList.add(property);
		Mockito.when(propertyDao.findByName(propertyName)).thenReturn(propertyList);

		boolean actual = systemPreferenceService.isPreferenceEnabled(propertyName, false);

		assertFalse(actual);
		Mockito.verify(propertyDao, times(1)).findByName(propertyName);
	}
}