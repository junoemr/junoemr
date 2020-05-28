package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(Born18mModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextBORN18M.xml"})
public class Born18mModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public Born18mModuleConfig()
	{
		logger.info("Loaded BORN18M module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_BORN18M;
		}
	}
}
