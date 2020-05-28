package org.oscarehr.config.modules;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;
import oscar.OscarProperties;

public abstract class ModuleConfigCondition implements ConfigurationCondition
{
	public abstract OscarProperties.Module getModule();

	@Override
	public ConfigurationPhase getConfigurationPhase()
	{
		return ConfigurationPhase.PARSE_CONFIGURATION;
	}

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata)
	{
		if(OscarProperties.getInstance().isModuleEnabled(getModule()))
		{
			return true;
		}

		return false;
	}
}
