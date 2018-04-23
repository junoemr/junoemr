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
package org.oscarehr.ws.rest.response;

/**
 * custom headers class for rest search responses.
 */
public class RestSearchResponseHeaders extends RestResponseHeaders
{
	private int total;
	private int page;
	private int perPage;

	public RestSearchResponseHeaders()
	{
		this.total = 0;
		this.page = 0;
		this.perPage = 0;
	}
	public RestSearchResponseHeaders(int page, int perPage, int total)
	{
		this.total = total;
		this.page = page;
		this.perPage = perPage;
	}

	public int getTotal()
	{
		return total;
	}

	public void setTotal(int total)
	{
		this.total = total;
	}

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public int getPerPage()
	{
		return perPage;
	}

	public void setPerPage(int perPage)
	{
		this.perPage = perPage;
	}
}
