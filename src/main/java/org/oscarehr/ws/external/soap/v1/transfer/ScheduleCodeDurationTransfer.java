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

package org.oscarehr.ws.external.soap.v1.transfer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCodeDurationTransfer
{
	private String scheduleTemplateCode;
	private int durationMinutes;

	public ScheduleCodeDurationTransfer(String scheduleTemplateCode, int durationMinutes)
	{
		this.scheduleTemplateCode = scheduleTemplateCode;
		this.durationMinutes = durationMinutes;
	}

	public String getScheduleTemplateCode()
	{
		return scheduleTemplateCode;
	}

	public void setScheduleTemplateCode(String scheduleTemplateCode)
	{
		this.scheduleTemplateCode = scheduleTemplateCode;
	}

	public int getDurationMinutes()
	{
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes)
	{
		this.durationMinutes = durationMinutes;
	}

	public static List<ScheduleCodeDurationTransfer> parse(String templateDurations) throws ParseException
	{
		List<ScheduleCodeDurationTransfer> scheduleDurationTransfers = new ArrayList<>();
		JSONArray templateDurationJsonArr = (JSONArray) new JSONParser().parse(templateDurations);

		for (Object templateDurationObj : templateDurationJsonArr)
		{
			ScheduleCodeDurationTransfer transfer = ScheduleCodeDurationTransfer.parse((JSONObject) templateDurationObj);
			scheduleDurationTransfers.add(transfer);
		}

		return scheduleDurationTransfers;
	}

	public static ScheduleCodeDurationTransfer parse(JSONObject templateDurationJson)
	{
		String templateCode = (String) templateDurationJson.get("schedule_template_id");
		Long duration = (Long) templateDurationJson.get("appointment_duration");

		return new ScheduleCodeDurationTransfer(templateCode, duration.intValue());
	}

	public static List<String> getAllTemplateCodes(List<ScheduleCodeDurationTransfer> codeDurationTransfers)
	{   List<String> mappedTransferCodes = new ArrayList<>();

		for (ScheduleCodeDurationTransfer scheduleCodeDurationTransfer : codeDurationTransfers)
		{
			mappedTransferCodes.add(scheduleCodeDurationTransfer.getScheduleTemplateCode());
		}
		return mappedTransferCodes;
	}
}
