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
package org.oscarehr.dataMigration.model.dx;

import lombok.Data;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.AbstractTransientModel;
import org.oscarehr.dataMigration.model.provider.Provider;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DxRecord extends AbstractTransientModel
{
	public enum Status
	{
		ACTIVE("A"),
		COMPLETE("C"),
		DELETED("D");

		private final String value;
		Status(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static Status fromValue(char value)
		{
			return fromValue(String.valueOf(value));
		}
		public static Status fromValue(String value)
		{
			for(Status status : Status.values())
			{
				if(status.getValue().equalsIgnoreCase(value))
				{
					return status;
				}
			}
			return null;
		}
	}

	private Integer id;
	private Status status;
	private String dxCode;
	private CDSConstants.CodingSystem codingSystem;
	private String codeDescription;
	private Provider provider;
	private LocalDate startDate;
	private LocalDateTime updateDate;
}
