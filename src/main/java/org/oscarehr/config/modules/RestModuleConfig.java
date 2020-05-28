package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(RestModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextREST.xml"})
public class RestModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public RestModuleConfig()
	{
		logger.info("Loaded REST module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.REST;
		}
	}
}
