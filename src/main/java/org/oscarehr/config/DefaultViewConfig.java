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

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DefaultViewConfig implements WebMvcConfigurer
{
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
		registry.addViewController("/web/").setViewName("forward:/web/index.jsp");
		registry.addViewController("/administration/").setViewName("forward:/administration/index.jsp");
		registry.addViewController("/ticklerPlus/").setViewName("forward:/ticklerPlus/index.jsp");
		registry.addViewController("/lab/CA/BC/").setViewName("forward:/lab/CA/BC/index.jsp");
		registry.addViewController("/mcedt/mailbox/").setViewName("forward:/mcedt/mailbox/index.jsp");
		registry.addViewController("/mcedt/").setViewName("forward:/mcedt/index.jsp");
		registry.addViewController("/scratch/").setViewName("forward:/scratch/index.jsp");
		registry.addViewController("/schedule/").setViewName("forward:/schedule/index.jsp");
		registry.addViewController("/survey/").setViewName("forward:/survey/index.jsp");
		registry.addViewController("/oscarPrevention/").setViewName("forward:/oscarPrevention/index.jsp");
		registry.addViewController("/oscarEncounter/").setViewName("forward:/oscarEncounter/Index.jsp");
		registry.addViewController("/eaaps/").setViewName("forward:/eaaps/index.jsp");
		registry.addViewController("/administration/").setViewName("forward:/administration/index.jsp");
		registry.addViewController("/appointment/").setViewName("forward:/appointment/index.jsp");
		registry.addViewController("/casemgmt/").setViewName("forward:/casemgmt/index.jsp");
		registry.addViewController("/tickler/").setViewName("forward:/tickler/index.jsp");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
}