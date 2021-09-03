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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

// These properties can be used in the application.properties files and any other method of setting
// properties according to this: https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config
// It should be possible to autocomplete these settings in application.properties with intellij,
// but if you add a new one you might need to "Rebuild Project" for them to show up (it has to
// generate a metadata file to let intellij know they exist)
@ConfigurationProperties(prefix = "juno")
@Data
public class JunoProperties
{
	private JunoPropertiesConfig properties;
	private RedisSessionStore redisSessionStore;
	private Test test;

	@Data
	public static class JunoPropertiesConfig
	{
		private String filename;
	}

	@Data
	public static class RedisSessionStore
	{
		private boolean enabled = false;
		private String endpoint;
		private String password;
	}

	@Data
	public static class Test
	{
		private boolean headless = true;
	}
}
