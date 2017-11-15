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
package org.oscarehr.common.web;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.oscarehr.util.MiscUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.Histogram;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.io.BufferedReader;
import java.io.IOException;

public class UserMetricsService extends Action {

	private static Logger logger = MiscUtils.getLogger();

	static final Histogram requestLatency = Histogram.build().
			name("request_latency_seconds").
			help("Reguest latency in seconds").
			labelNames("page").
			register();

	@Override
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{
		String page = "appointment";
		try
		{
			BufferedReader bufferedReader = request.getReader();
			StringBuilder buffer = new StringBuilder();
			String line;
			while((line = bufferedReader.readLine()) != null)
			{
				buffer.append(line);
			}

			String body = buffer.toString();
			JSONObject json = new JSONObject(body);
			Double loadTime = json.getDouble("page_load_time");
			requestLatency.labels(page).observe(loadTime);

		} catch (IOException e)
		{
			logger.error("Error parsing page load time data.");
		}
		return null;
	}


}

