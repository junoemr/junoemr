package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(E2eModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextE2E.xml"})
public class E2eModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public E2eModuleConfig()
	{
		logger.info("Loaded E2E module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_E2E;
		}
	}
}
