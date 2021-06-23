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
package org.oscarehr.dataMigration.mapper;

import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public abstract class AbstractImportMapper<I, E>
{
	@Autowired
	protected PatientImportContextService patientImportContextService;

	/**
	 * build the export structure from the provided import structure.
	 * this method creates a new object to use as the export structure.
	 * @param importStructure
	 */
	public abstract E importToJuno(I importStructure) throws Exception;

	public List<E> importAll(Collection<I> importStructures) throws Exception
	{
		List<E> list = new ArrayList<>();
		for(I importStructure : importStructures)
		{
			E exportStructure = importToJuno(importStructure);
			list.add(exportStructure);
		}
		return list;
	}

	/**
	 * @param partialDates - partial dates list to choose from in order of preference
	 * @return - the first non-null dateTime in the given list
	 */
	protected LocalDateTime coalescePartialDatesToDateTime(PartialDate... partialDates)
	{
		LocalDateTime observationDate = null;
		for(PartialDate partialDate : partialDates)
		{
			if(partialDate != null)
			{
				if(partialDate instanceof PartialDateTime)
				{
					observationDate = ((PartialDateTime) partialDate).toLocalDateTime();
				}
				else
				{
					observationDate = partialDate.toLocalDate().atStartOfDay();
				}
				break;
			}
		}
		return observationDate;
	}

	protected LocalDateTime coalescePartialDatesToDateTimeWithDefault(String what, PartialDate... partialDates)
	{
		LocalDateTime localDateTime = coalescePartialDatesToDateTime(partialDates);
		if(localDateTime == null)
		{
			localDateTime = patientImportContextService.getContext().getDefaultDate().atStartOfDay();
			logDefaultValueUse(what, localDateTime);
		}
		return localDateTime;
	}

	public void logDefaultValueUse(String what, Object defaultedValue)
	{
		logEvent(what + " was defaulted to " + defaultedValue);
	}

	/**
	 * shortcut to the import logger for use in mappers
	 */
	public void logEvent(String message)
	{
		PatientImportContext context = patientImportContextService.getContext();
		context.getImportLogger().logEvent("[" + context.getCurrentProcessIdentifier() + "] " + message);
	}
}
