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
package org.oscarehr.ws.rest.transfer.billing;

import org.oscarehr.billing.CA.AB.model.AlbertaSkillCode;

import java.util.ArrayList;
import java.util.List;

public class AlbertaSkillCodeTo1
{
	private String skillCode;
	private String description;

	public static List<AlbertaSkillCodeTo1> fromList(List<AlbertaSkillCode> albertaSkillCodes)
	{
		ArrayList<AlbertaSkillCodeTo1> albertaSkillCodeTo1s = new ArrayList<>();
		for (AlbertaSkillCode albertaSkillCode : albertaSkillCodes)
		{
			albertaSkillCodeTo1s.add(new AlbertaSkillCodeTo1(albertaSkillCode));
		}
		return albertaSkillCodeTo1s;
	}

	public AlbertaSkillCodeTo1(AlbertaSkillCode albertaSkillCode)
	{
		this.skillCode = albertaSkillCode.getSkillCode();
		this.description = albertaSkillCode.getDescription();
	}

	public String getSkillCode()
	{
		return skillCode;
	}

	public void setSkillCode(String skillCode)
	{
		this.skillCode = skillCode;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
