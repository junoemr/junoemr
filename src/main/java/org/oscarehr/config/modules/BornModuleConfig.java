package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(BornModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextBORN.xml"})
public class BornModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public BornModuleConfig()
	{
		logger.info("Loaded BORN module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_BORN;
		}
	}
}
