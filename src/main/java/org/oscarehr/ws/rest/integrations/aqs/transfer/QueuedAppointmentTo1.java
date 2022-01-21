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

package org.oscarehr.ws.rest.integrations.aqs.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.integration.aqs.model.CommunicationType;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueuedAppointmentTo1 implements Serializable
{
	private UUID id;
	private UUID queueId;
	private Integer queuePosition;
	private Integer demographicNo;
	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	private OffsetDateTime createdAt;
	private String demographicName;
	private String reason;
	private String notes;
	private Integer siteId;
	private Boolean virtual;
	private Boolean critical;
	private UUID clinicId;
	private CommunicationType communicationType;

	public static List<QueuedAppointmentTo1> fromQueuedAppointmentList(List<QueuedAppointment> queuedAppointments)
	{
		ArrayList<QueuedAppointmentTo1> queuedAppointmentTo1s = new ArrayList<>();

		for (QueuedAppointment queuedAppointment : queuedAppointments)
		{
			queuedAppointmentTo1s.add(new QueuedAppointmentTo1(queuedAppointment));
		}
		return queuedAppointmentTo1s;
	}

	// default constructor required for serialization
	public QueuedAppointmentTo1()
	{
	}

	public QueuedAppointmentTo1(QueuedAppointment queuedAppointment)
	{
		BeanUtils.copyProperties(queuedAppointment, this);

		// get demographic name
		DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
		Demographic demographic = demographicDao.find(this.getDemographicNo());
		this.demographicName = demographic.getDisplayName();
	}
}
