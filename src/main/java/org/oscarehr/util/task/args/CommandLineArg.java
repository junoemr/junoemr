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
package org.oscarehr.util.task.args;

import lombok.Getter;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CommandLineArg<T>
{
	private String name;
	boolean required;
	private T value;
	private T defaultValue;

	public CommandLineArg()
	{

	}
	public CommandLineArg(String name, T defaultValue, boolean required)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.required = required;
	}

	public CommandLineArg<T> set(List<String> valueList)
	{
		this.value = toValue(valueList);
		if(this.value == null)
		{
			if(defaultValue != null)
			{
				this.value = defaultValue;
			}
			else if(required)
			{
				throw new InvalidCommandLineArgumentsException(this.name + " is a required argument");
			}
		}
		return this;
	}

	protected abstract T toValue(List<String> valueList);

	public static Map<String, CommandLineArg<?>> buildOptions(CommandLineArg<?>... args)
	{
		Map<String, CommandLineArg<?>> map = new HashMap<>();
		for(CommandLineArg<?> arg : args)
		{
			map.put(arg.name, arg);
		}
		return map;
	}
}
