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
package org.oscarehr.util;

import org.apache.log4j.Logger;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.util.task.CommandLineExporter;
import org.oscarehr.util.task.CommandLineImporter;
import org.oscarehr.util.task.CommandLineTask;
import org.oscarehr.util.task.args.CommandLineArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Juno custom command line interface.
 * you can run a task on the command line with this argument structure:
 * java [vm options] war_file.war {task name} --{task_name}.option1=value1 --{task_name}.option2=value2 ...
 */
@Component
@ConditionalOnNotWebApplication
public class JunoCommandLineRunner implements ApplicationRunner
{
	private static final Logger logger = Logger.getLogger(JunoCommandLineRunner.class);

	@Autowired
	private CommandLineImporter commandLineImporter;

	@Autowired
	private CommandLineExporter commandLineExporter;

	@Override
	public void run(ApplicationArguments args)
	{
		try
		{
			if(args.getNonOptionArgs().isEmpty())
			{
				throw new InvalidCommandLineArgumentsException("A task must be specified");
			}
			String taskName = args.getNonOptionArgs().get(0);
			CommandLineTask task = findTask(taskName);
			task.run(toArgsMap(task, args));
		}
		catch(InvalidCommandLineArgumentsException e)
		{
			logger.error(e.getMessage());
			System.exit(2);
		}
		catch(Exception e)
		{
			logger.error("Unknown Error", e);
			System.exit(1);
		}
		System.exit(0);
	}

	private List<CommandLineTask> taskList()
	{
		return Arrays.asList(commandLineImporter, commandLineExporter);
	}

	private CommandLineTask findTask(String taskName)
	{
		for(CommandLineTask commandLineTask : taskList())
		{
			if(commandLineTask.taskName().equals(taskName))
			{
				return commandLineTask;
			}
		}
		throw new InvalidCommandLineArgumentsException("Unknown task: " + taskName);
	}

	private Map<String, CommandLineArg<?>> toArgsMap(CommandLineTask task, ApplicationArguments args)
	{
		Map<String, CommandLineArg<?>> argsMap = new HashMap<>();
		List<CommandLineArg<?>> taskArgs = task.argsList();

		if(taskArgs != null)
		{
			for(CommandLineArg<?> taskArg : taskArgs)
			{
				String taskName = task.taskName() + "." + taskArg.getName();
				argsMap.put(taskArg.getName(), taskArg.set(args.getOptionValues(taskName)));
			}
		}

		return argsMap;
	}
}
