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
package org.oscarehr.ws.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * custom headers class for rest search responses.
 */
@Schema(description = "Response header object for lists")
public class RestSearchResponseHeaders extends RestResponseHeaders
{
	@Schema(description = "total result count")
	private int total;
	@Schema(description = "current results page number")
	private int page;
	@Schema(description = "max results for a page")
	private int perPage;

	public RestSearchResponseHeaders(int page, int perPage, int total)
	{
		super();
		this.total = total;
		this.page = page;
		this.perPage = perPage;
	}
	public RestSearchResponseHeaders()
	{
		this(0,0,0);
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
