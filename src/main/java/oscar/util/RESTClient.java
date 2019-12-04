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
package oscar.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.util.Map;

@SuppressWarnings("unchecked")
public class RESTClient
{
	//https://ben.ohdev.ca/ben1/ws/rs/integrations/iceFall/settings


	public static <T> T doGet(String URI, String media, Class to1Class)
	{
		return doGet(URI, null, null, media, to1Class);
	}

	public static <T> T doGet(String URI, Map<String, String> queryParams, Map<String, Object> headers, String media, Class to1Class)
	{
		Client client = ClientBuilder.newClient();
		return (T) setHeaders(setQueryParams(client.target(URI), queryParams).request(media), headers).get(to1Class);
	}

	public static <K,T> T doPost(String URI, Map<String, String> queryParams, String responseMedia, String requestMedia, K postObject)
	{
		Client client = ClientBuilder.newClient();
		return (T)setQueryParams(client.target(URI), queryParams).request(responseMedia).post(Entity.entity(postObject, requestMedia));
	}

	/**
	 * apply the query params in the map to the specified WebTarget
	 * @param target - the target to set the query params on
	 * @param queryParams - the params to set
	 * @return - a new WebTarget with the query params set.
	 */
	private static WebTarget setQueryParams(WebTarget target, Map<String, String> queryParams)
	{
		if (queryParams != null)
		{
			for (Map.Entry<String, String> param : queryParams.entrySet())
			{
				target = target.queryParam(param.getKey(), param.getValue());
			}
		}

		return target;
	}

	/**
	 * set the headers on the request
	 * @param target - the request to have the headers set
	 * @param headers - the headers to set
	 * @return - a new Invocation Builder with the headers set
	 */
	private static Invocation.Builder setHeaders(Invocation.Builder target, Map<String, Object> headers)
	{
		if (headers != null)
		{
			for (Map.Entry<String, Object> param : headers.entrySet())
			{
				target = target.header(param.getKey(), param.getValue());
			}
		}

		return target;
	}
}
