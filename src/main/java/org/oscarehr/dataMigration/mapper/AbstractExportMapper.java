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

import lombok.Data;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Component
public abstract class AbstractExportMapper<I, E>
{
	@Autowired
	protected PatientExportContextService patientExportContextService;

	public AbstractExportMapper()
	{
	}

	/**
	 * build the import structure from the provided export structure.
	 * this method creates a new object to use as the import structure.
	 * @param exportStructure
	 */
	public abstract I exportFromJuno(E exportStructure) throws Exception;

	public List<I> exportAll(Collection<E> exportStructures) throws Exception
	{
		List<I> list = new ArrayList<>();
		for(E exportStructure : exportStructures)
		{
			I importStructure = exportFromJuno(exportStructure);
			if(importStructure != null)
			{
				list.add(importStructure);
			}
		}
		return list;
	}

	/**
	 * shortcut to the export logger for use in mappers
	 */
	public void logEvent(String message)
	{
		PatientExportContext context = patientExportContextService.getContext();
		context.getExportLogger().logEvent("[" + context.getCurrentProcessIdentifier() + "] " + message);
	}
}
