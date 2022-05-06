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

package org.oscarehr.config;

import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig
{
	// This is neccessary to set the sql_mode to the MariaDB 10.1 default.  The default mode for
	// MariaDB 10.2.4+ causes errors.
	private static final String CONNECTION_STRING_SUFFIX =
		"&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION,NO_AUTO_CREATE_USER'";

	private oscar.OscarProperties oscarProperties = oscar.OscarProperties.getInstance();

	@Bean(name="dataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public DataSource getDataSource()
	{
		String dbUri = oscarProperties.getDbUri();
		String dbName = oscarProperties.getDbName();

		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(oscarProperties.getDbDriver());
		dataSourceBuilder.url(dbUri + dbName + CONNECTION_STRING_SUFFIX);
		dataSourceBuilder.username(oscarProperties.getDbUserName());
		dataSourceBuilder.password(oscarProperties.getDbPassword());
		return dataSourceBuilder.build();
	}

	@Bean(name="dataSourceReadOnly")
	public DataSource getDataSourceReadOnly()
	{
		String dbUri = oscarProperties.getDbUriReadOnly();
		String dbName = oscarProperties.getDbNameReadOnly();

		DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
		dataSourceBuilder.driverClassName(oscarProperties.getDbDriver());
		dataSourceBuilder.url(dbUri + dbName + CONNECTION_STRING_SUFFIX);
		dataSourceBuilder.username(oscarProperties.getDbUserNameReadOnly());
		dataSourceBuilder.password(oscarProperties.getDbPasswordReadOnly());

		return dataSourceBuilder.build();
	}
}
