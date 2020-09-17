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


package oscar.oscarProvider.data;

import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.util.SpringUtils;

/**
 * Manages provider colour for provider
 *
 */
public class ProviderColourUpdater {

	private String provider;
	private static PropertyDao propertyDao = SpringUtils.getBean(PropertyDao.class);

	/**
	 * Creates a new instance of ProviderColourUpdater
	 */
	public ProviderColourUpdater(String providerNo)
	{
		this.provider = providerNo;
	}
   

	/**
	 * Retrieve colour for current provider first by querying property table
	 * @return ProviderColor property value associated with user, null if no setting found
	 */
	public String getColour()
	{
		Property props = propertyDao.findByNameAndProvider(UserProperty.PROVIDER_COLOUR, provider);
		if (props != null)
		{
			return props.getValue();
		}

		return null;
	}


	/**
	 * set colour in property table
	 */
	public boolean setColour(String colour)
	{
		Property props = propertyDao.findByNameAndProvider(UserProperty.PROVIDER_COLOUR, provider);

		if (props != null)
		{
			props.setValueNoNull(colour);
			propertyDao.merge(props);
		}
		else
		{
			props = new Property();
			props.setValue(colour);
			props.setName(UserProperty.PROVIDER_COLOUR);
			props.setProviderNo(provider);

			propertyDao.persist(props);
		}

		return true;
	}
}
