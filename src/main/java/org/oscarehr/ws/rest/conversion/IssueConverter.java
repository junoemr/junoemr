/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest.conversion;

import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.IssueTo1;
import org.springframework.beans.BeanUtils;

public class IssueConverter extends AbstractConverter<Issue, IssueTo1> {

	@Override
	public Issue getAsDomainObject(LoggedInInfo loggedInInfo, IssueTo1 t) throws ConversionException {
		Issue d = new Issue();
		BeanUtils.copyProperties(t, d, new String[]{"issueChange"});
		return d;
	}

	@Override
	public IssueTo1 getAsTransferObject(LoggedInInfo loggedInInfo, Issue d) throws ConversionException {
		IssueTo1 t = new IssueTo1();
		BeanUtils.copyProperties(d, t);
		return t;
	}

	public static IssueTo1 getAsTransferObject(org.oscarehr.encounterNote.model.Issue issue)
	{
		IssueTo1 issueTo = new IssueTo1();

		issueTo.setId(issue.getId());
		issueTo.setCode(issue.getCode());
		issueTo.setDescription(issue.getDescription());
		issueTo.setPriority(issue.getPriority());
		issueTo.setRole(issue.getRole());
		issueTo.setSortOrderId(issue.getSortOrderId());
		issueTo.setType(issue.getType());
		issueTo.setUpdate_date(issue.getUpdateDate());

		return issueTo;
	}
}
