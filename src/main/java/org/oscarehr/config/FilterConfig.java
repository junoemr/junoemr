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

import net.sf.cookierevolver.CRFilter;
import org.apache.catalina.filters.FailedRequestFilter;
import org.caisi.comp.web.WebComponentFilter;
import org.displaytag.filter.ResponseOverrideFilter;
import org.oscarehr.PMmodule.web.PMMFilter;
import org.oscarehr.common.printing.PrivacyStatementAppendingFilter;
import org.oscarehr.util.DbConnectionFilter;
import org.oscarehr.util.LoggedInUserFilter;
import org.oscarehr.util.ProblemCheckFilter;
import org.oscarehr.util.ResponseDefaultsFilter;
import org.oscarehr.ws.WebServiceSessionInvalidatingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import oscar.oscarSecurity.LoginFilter;

@Configuration
public class FilterConfig
{
	// The charset filter was switched to use a built in Spring Boot feature in application.properties
	//<filter-name>CharsetFilter</filter-name>

	// This filter was disabled for the time being
	//<filter-name>monitoring</filter-name>

	private static final int WEB_SERVICE_SESSION_INVALIDATING_FILTER_ORDER = 1;
	private static final int FAILED_REQUEST_FILTER_ORDER = 2;
	private static final int RESPONSE_DEFAULTS_FILTER_ORDER = 3;
	private static final int PROBLEM_CHECK_FILTER_ORDER = 4;
	private static final int PRIVACY_STATEMENT_APPENDING_FILTER_ORDER = 5;
	private static final int WEB_COMPONENT_FILTER_ORDER = 6;
	private static final int DB_CONNECTION_FILTER_ORDER = 7;
	private static final int LOGGED_IN_USER_FILTER_ORDER = 8;
	private static final int PMM_FILTER_ORDER = 9;
	private static final int RESPONSE_OVERRIDE_FILTER_ORDER = 10;
	private static final int CR_FILTER_ORDER = 11;
	private static final int LOGIN_FILTER_ORDER = 12;

	@Bean
	public FilterRegistrationBean<LoginFilter> registerLoginFilter()
	{
		FilterRegistrationBean<LoginFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new LoginFilter());
		filterRegistrationBean.setOrder(LOGIN_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<DbConnectionFilter> registerDbConnectionFilter()
	{
		FilterRegistrationBean<DbConnectionFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new DbConnectionFilter());
		filterRegistrationBean.setOrder(DB_CONNECTION_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<LoggedInUserFilter> registerLoggedInUserFilter()
	{
		FilterRegistrationBean<LoggedInUserFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new LoggedInUserFilter());
		filterRegistrationBean.setOrder(LOGGED_IN_USER_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<ProblemCheckFilter> registerProblemCheckFilter()
	{
		FilterRegistrationBean<ProblemCheckFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new ProblemCheckFilter());
		filterRegistrationBean.setOrder(PROBLEM_CHECK_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<WebServiceSessionInvalidatingFilter> registerWebServiceSessionInvalidatingFilter()
	{
		FilterRegistrationBean<WebServiceSessionInvalidatingFilter> filterRegistrationBean =
				new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new WebServiceSessionInvalidatingFilter());
		filterRegistrationBean.addUrlPatterns("/ws/*");
		filterRegistrationBean.setOrder(WEB_SERVICE_SESSION_INVALIDATING_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<FailedRequestFilter> registerFailedRequestFilter()
	{
		FilterRegistrationBean<FailedRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new FailedRequestFilter());
		filterRegistrationBean.addUrlPatterns("/eform/addEForm.do");
		filterRegistrationBean.setOrder(FAILED_REQUEST_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<ResponseDefaultsFilter> registerResponseDefaultsFilter()
	{
		FilterRegistrationBean<ResponseDefaultsFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new ResponseDefaultsFilter());
		filterRegistrationBean.addInitParameter("noCacheEndings", ".jsp,.jsf,.json,.do");
		filterRegistrationBean.setOrder(RESPONSE_DEFAULTS_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<PrivacyStatementAppendingFilter> registerPrivacyStatementAppendingFilter()
	{
		FilterRegistrationBean<PrivacyStatementAppendingFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new PrivacyStatementAppendingFilter());
		filterRegistrationBean.addUrlPatterns("*.jsp");
		filterRegistrationBean.addUrlPatterns("*.do");
		filterRegistrationBean.addUrlPatterns("*.html");
		filterRegistrationBean.addUrlPatterns("*.htm");
		filterRegistrationBean.addInitParameter("exclusions",
				"/oscarMessenger," +
						"/demographic/demographiccontrol.jsp," +
						"/demographic/demographiceditdemographic.js.jsp," +
						"/demographic/demographicprintdemographic.jsp," +
						"/provider/schedulePage.js.jsp"
		);
		filterRegistrationBean.setOrder(PRIVACY_STATEMENT_APPENDING_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<WebComponentFilter> registerWebComponentFilter()
	{
		FilterRegistrationBean<WebComponentFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new WebComponentFilter());
		filterRegistrationBean.addUrlPatterns("/mod/*");
		filterRegistrationBean.setOrder(WEB_COMPONENT_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<CRFilter> registerCRFilter()
	{
		FilterRegistrationBean<CRFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new CRFilter());
		filterRegistrationBean.addInitParameter("cr.filter.ignore", "/login.do /images/* /lab/CMLlabUpload.do /lab/CA/ON/uploadComplete.jsp /logout.jsp /PopulationReport.do");
		filterRegistrationBean.addInitParameter("cr.auth.method", "CUSTOM");
		filterRegistrationBean.addInitParameter("cr.auth.loginURL", "index.jsp");
		filterRegistrationBean.addInitParameter("cr.policy.default", "REMOTE-ACCESS");
		filterRegistrationBean.addInitParameter("cr.rolesProvider", "oscar.oscarSecurity.CRHelper");
		filterRegistrationBean.addInitParameter("cr.config.provider", "oscar.oscarDB.OscarHibernateProperties");
		filterRegistrationBean.addInitParameter("hibernate.connection.pool_size", "10");
		filterRegistrationBean.addInitParameter("hibernate.connection.autocommit", "true");
		filterRegistrationBean.setOrder(CR_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<ResponseOverrideFilter> registerResponseOverrideFilter()
	{
		FilterRegistrationBean<ResponseOverrideFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new ResponseOverrideFilter());
		filterRegistrationBean.addUrlPatterns("*.do");
		filterRegistrationBean.setOrder(RESPONSE_OVERRIDE_FILTER_ORDER);

		return filterRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean<PMMFilter> registerPMMFilter()
	{
		FilterRegistrationBean<PMMFilter> filterRegistrationBean = new FilterRegistrationBean<>();

		filterRegistrationBean.setFilter(new PMMFilter());
		filterRegistrationBean.addUrlPatterns("/PMmodule/*");
		filterRegistrationBean.setOrder(PMM_FILTER_ORDER);

		return filterRegistrationBean;
	}
}
