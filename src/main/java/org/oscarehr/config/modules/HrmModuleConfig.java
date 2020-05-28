package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(HrmModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextHRM.xml"})
public class HrmModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public HrmModuleConfig()
	{
		logger.info("Loaded HRM module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_HRM;
		}
	}
}
