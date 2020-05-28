package org.oscarehr.config.modules;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import oscar.OscarProperties;

@Configuration
@Conditional(CaisiModuleConfig.Condition.class)
@ImportResource({"classpath*:applicationContextCaisi.xml"})
public class CaisiModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	public CaisiModuleConfig()
	{
		logger.info("Loaded Caisi module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_CAISI;
		}
	}
}
