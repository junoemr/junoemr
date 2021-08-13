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

import org.oscarehr.util.persistence.OscarMySQL5Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class HibernateConfig
{
	@Autowired
	private DataSource dataSource;

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	public LocalSessionFactoryBean sessionFactory() throws IOException
	{
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setMappingLocations(loadResources());
		sessionFactoryBean.setPackagesToScan(new String[] {"org.oscarehr", "oscar"});
		sessionFactoryBean.setHibernateProperties(hibernateProperties());
		return sessionFactoryBean;
	}

	private Resource[] loadResources() throws IOException
	{
		return ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
					.getResources("classpath*:/**/*.hbm.xml");
	}

	private Properties hibernateProperties()
	{
		Properties properties = new Properties();
		properties.put("hibernate.dialect", OscarMySQL5Dialect.class.getName());
		//properties.put("hibernate.dialect", MariaDBDialect.class.getName());
		//properties.put("hibernate.show_sql", "hibernate.show_sql");
		//properties.put("hibernate.format_sql", "hibernate.format_sql");
		//properties.put("hibernate.hbm2ddl.auto", "hibernate.hbm2ddl.auto");
		return properties;
	}

/*
	@Bean
	public LocalSessionFactoryBean sessionFactory()
	{
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(new String[]{"org.oscarehr"});
		sessionFactory.setMappingResources(
				"org/oscarehr/common/model/Demographic.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramFunctionalUser.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeJavascript.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeLabel.hbm.xml",
				"org/oscarehr/PMmodule/model/AccessType.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramQueue.hbm.xml",
				"org/oscarehr/PMmodule/model/FunctionalUserType.hbm.xml",
				"org/oscarehr/PMmodule/model/SecUserRole.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswerElement.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeType.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswer.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramClientRestriction.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNode.hbm.xml",
				"org/oscarehr/PMmodule/model/Agency.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramTeam.hbm.xml",
				"org/oscarehr/PMmodule/model/ClientReferral.hbm.xml",
				"org/oscarehr/PMmodule/model/DefaultRoleAccess.hbm.xml",
				"org/oscarehr/PMmodule/model/Intake.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramProvider.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramSignature.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeTemplate.hbm.xml",
				"org/oscarehr/PMmodule/model/Program.hbm.xml",
				"org/oscarehr/PMmodule/model/HealthSafety.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramClientStatus.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswerValidation.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramAccess.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_cpp.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note_link.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note_ext.hbm.xml",
				"org/oscarehr/casemgmt/model/ClientImage.hbm.xml",
				"org/oscarehr/casemgmt/model/issue.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_issue.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note.hbm.xml",
				"org/oscarehr/phr/model/PHRDocument.hbm.xml",
				"org/oscarehr/phr/model/PHRAction.hbm.xml",
				"org/oscarehr/common/model/Demographic.hbm.xml",
				"org/oscarehr/common/model/Provider.hbm.xml"
		);
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}
*/



	/*
	@Bean
	public DataSource dataSource()
	{
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
		dataSource.setUrl("jdbc:mariadb://localhost:3306/canadian_cann_db");
		dataSource.setUsername("canadian_cann_u");
		dataSource.setPassword("945d15ab45b906cd");

		return dataSource;
	}

	 */

/*
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource)
	{
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan("org.oscarehr");
		em.setMappingResources(
				"org/oscarehr/common/model/Demographic.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramFunctionalUser.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeJavascript.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeLabel.hbm.xml",
				"org/oscarehr/PMmodule/model/AccessType.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramQueue.hbm.xml",
				"org/oscarehr/PMmodule/model/FunctionalUserType.hbm.xml",
				"org/oscarehr/PMmodule/model/SecUserRole.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswerElement.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeType.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswer.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramClientRestriction.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNode.hbm.xml",
				"org/oscarehr/PMmodule/model/Agency.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramTeam.hbm.xml",
				"org/oscarehr/PMmodule/model/ClientReferral.hbm.xml",
				"org/oscarehr/PMmodule/model/DefaultRoleAccess.hbm.xml",
				"org/oscarehr/PMmodule/model/Intake.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramProvider.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramSignature.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeNodeTemplate.hbm.xml",
				"org/oscarehr/PMmodule/model/Program.hbm.xml",
				"org/oscarehr/PMmodule/model/HealthSafety.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramClientStatus.hbm.xml",
				"org/oscarehr/PMmodule/model/IntakeAnswerValidation.hbm.xml",
				"org/oscarehr/PMmodule/model/ProgramAccess.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_cpp.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note_link.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note_ext.hbm.xml",
				"org/oscarehr/casemgmt/model/ClientImage.hbm.xml",
				"org/oscarehr/casemgmt/model/issue.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_issue.hbm.xml",
				"org/oscarehr/casemgmt/model/casemgmt_note.hbm.xml",
				"org/oscarehr/phr/model/PHRDocument.hbm.xml",
				"org/oscarehr/phr/model/PHRAction.hbm.xml",
				"org/oscarehr/common/model/Demographic.hbm.xml",
				"org/oscarehr/common/model/Provider.hbm.xml"
		);

		//these needed to be added to have all hibernate config done in one place.
		em.getJpaPropertyMap().put(
				AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS,
				SpringSessionContext.class.getName());
		em.getJpaPropertyMap().put(AvailableSettings.DIALECT,
				MariaDBDialect.class.getName());

		HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
		vendor.setShowSql(false);
		em.setJpaVendorAdapter(vendor);

		return em;
	}
*/
}
