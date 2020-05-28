package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(Emeralda04ModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextEmeraldA04.xml"})
public class Emeralda04ModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public Emeralda04ModuleConfig()
	{
		logger.info("Loaded EmeraldA04 module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_EMERALDA04;
		}
	}
}
