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
package org.oscarehr.integration.aqs.model;

import lombok.Getter;
import lombok.Setter;
import oscar.OscarProperties;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
public class AppointmentQueue
{
	private static final OscarProperties props = OscarProperties.getInstance();

	private UUID remoteId;
	private String name;
	private Integer queueLimit;
	private String queueColor;
	private OffsetDateTime createdAt;
	private QueueAvailability availability;
	private QueueOnDemandSettings onDemandSettings;
	private Integer defaultAppointmentDurationMinutes = 15;
	private Boolean available;

	public AppointmentQueue()
	{
		this.queueLimit = Integer.parseInt(props.getProperty("aqs_default_queue_limit", "25"));
		this.onDemandSettings = new QueueOnDemandSettings();
		this.availability = new QueueAvailability();
		this.available = false;
	}
}
