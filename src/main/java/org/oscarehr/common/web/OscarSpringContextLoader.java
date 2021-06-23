/**
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package org.oscarehr.common.web;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import oscar.OscarProperties;

import javax.servlet.ServletContext;
import java.util.ArrayList;

// TODO: SPRINGUPGRADE: Not used anymore
public final class OscarSpringContextLoader extends ContextLoader
{
	private static final Logger log = MiscUtils.getLogger();

	private static final String MODULE_PROPERTY_NAME = "ModuleNames";
	private static final String BASE_CONTEXT_NAME = "classpath:applicationContext";

	@Override
	protected void configureAndRefreshWebApplicationContext(
			ConfigurableWebApplicationContext wac, ServletContext sc)
	{
		super.configureAndRefreshWebApplicationContext(wac, sc);
		String[] configLocations = wac.getConfigLocations();
		String moduleNames = (String) OscarProperties.getInstance().get(MODULE_PROPERTY_NAME);
		ArrayList<String> moduleLocations = new ArrayList<String>();
		moduleLocations.add(BASE_CONTEXT_NAME + ".xml");
		if (moduleNames != null && moduleNames.trim().length() > 0)
		{
			String[] moduleList = moduleNames.split(",");
			for (String module : moduleList)
			{
				log.error("Adding module: " + module);
				moduleLocations.add(BASE_CONTEXT_NAME + module.trim() + ".xml");
			}
		}
		configLocations = ArrayUtils.addAll(
				configLocations, moduleLocations.toArray(new String[0]));
		wac.setConfigLocations(configLocations);
		wac.refresh();
	}

	@Override
	protected WebApplicationContext createWebApplicationContext(ServletContext sc)
	{
		WebApplicationContext wc = super.createWebApplicationContext(sc);
		if (SpringUtils.beanFactory == null) SpringUtils.beanFactory = wc;
		return wc;
	}
}
