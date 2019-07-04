/*
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

package org.oscarehr.casemgmt.web.formbeans;

import org.apache.struts.action.ActionForm;
import org.oscarehr.casemgmt.dto.EncounterHeader;
import org.oscarehr.casemgmt.dto.EncounterSection;

import java.util.List;
import java.util.Map;

public class JunoEncounterFormBean extends ActionForm
{
	private EncounterHeader header = null;
	private Map<String, EncounterSection> sections = null;
	private List<String> cppNoteSections = null;

	public EncounterHeader getHeader()
	{
		return header;
	}

	public void setHeader(EncounterHeader header)
	{
		this.header = header;
	}

	public Map<String, EncounterSection> getSections()
	{
		return sections;
	}

	public void setSections(Map<String, EncounterSection> sections)
	{
		this.sections = sections;
	}

	public List<String> getCppNoteSections()
	{
		return cppNoteSections;
	}

	public void setCppNoteSections(List<String> cppNoteSections)
	{
		this.cppNoteSections = cppNoteSections;
	}
}
