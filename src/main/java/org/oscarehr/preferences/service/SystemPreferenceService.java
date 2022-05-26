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

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SystemPreferenceService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private PropertyDao propertyDao;

	public Boolean isPreferenceEnabled(String key, Boolean defaultValue)
	{
		List<Property> propertyList = propertyDao.findByName(key);
		Boolean result = defaultValue;

		if(propertyList.size() > 1)
		{
			throw new IllegalStateException("System property '" + key + "' is not unique");
		}

		if(!propertyList.isEmpty())
		{
			Property property = propertyList.get(0);
			result = property.isPropertyEnabled();
		}
		return result;
	}

	public String getPreferenceValue(String key, String defaultValue)
	{
		List<Property> propertyList = propertyDao.findByName(key);
		String result = defaultValue;

		if(propertyList.size() > 1)
		{
			throw new IllegalStateException("System property '" + key + "' is not unique");
		}

		if(!propertyList.isEmpty())
		{
			Property property = propertyList.get(0);
			result = property.getValue();
		}

		return result;
	}

	public Optional<String> getOptionalPreferenceValue(String key)
	{
		return Optional.ofNullable(getPreferenceValue(key, null));
	}

	public Property setPreferenceValue(String key, String value)
	{
		List<Property> propertyList = propertyDao.findByName(key);

		if(propertyList.size() > 1)
		{
			throw new IllegalStateException("System property '" + key + "' is not unique");
		}

		Property property;
		if(!propertyList.isEmpty())
		{
			property = propertyList.get(0);
			property.setValue(value);
			propertyDao.merge(property);
		}
		else
		{
			property = new Property();
			property.setName(key);
			property.setValue(value);
			property.setProviderNo(ProviderData.SYSTEM_PROVIDER_NO);

			propertyDao.persist(property);
		}
		return  property;
	}

	public String getPropertyValue(String key, String defaultValue)
	{
		return OscarProperties.getInstance().getProperty(key, defaultValue);
	}

	public Boolean isPropertyEnabled(String key)
	{
		return OscarProperties.getInstance().isPropertyActive(key);
	}
}
