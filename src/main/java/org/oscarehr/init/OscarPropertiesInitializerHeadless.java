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

package org.oscarehr.init;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class OscarPropertiesInitializerHeadless
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final oscar.OscarProperties oscarProperties = oscar.OscarProperties.getInstance();
	private static final String PROPERTIES_FILE_ARGUMENT_NAME = "juno.propertiesFilename";

	public void initialize() throws IOException
	{
		String propertiesFilenameCli = StringUtils.trimToNull(System.getProperty(PROPERTIES_FILE_ARGUMENT_NAME));
		oscarProperties.readFromFile(propertiesFilenameCli);
	}
}
