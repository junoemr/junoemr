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
package org.oscarehr.demographicImport.mapper;

import lombok.Data;
import org.oscarehr.demographicImport.service.ExportPreferences;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public abstract class AbstractExportMapper<I, E>
{
	protected ExportPreferences exportPreferences;

	public AbstractExportMapper()
	{
	}

	/**
	 * build the import structure from the provided export structure.
	 * this method creates a new object to use as the import structure.
	 * @param exportStructure
	 */
	public abstract I exportFromJuno(E exportStructure);

	public List<I> exportAll(Collection<E> entities)
	{
		return entities.stream().map(this::exportFromJuno).collect(Collectors.toList());
	}
}
