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

package org.oscarehr.ws.external.soap.v1.transfer.Appointment;

import org.oscarehr.common.model.Appointment;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

public class AppointmentConfirmationTransfer
{
	private int appointmentNo;

	private Date confirmedAt;

	private String confirmedBy;

	@Enumerated(EnumType.STRING)
	private Appointment.ConfirmedByType confirmedByType;

	public AppointmentConfirmationTransfer()
	{
	}

	public AppointmentConfirmationTransfer(int appointmentNo, Date confirmedAt,
										   String confirmedBy, Appointment.ConfirmedByType confirmedByType)
	{
		this.appointmentNo = appointmentNo;
		this.confirmedAt = confirmedAt;
		this.confirmedBy = confirmedBy;
		this.confirmedByType = confirmedByType;
	}

	public int getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(int appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public Date getConfirmedAt()
	{
		return confirmedAt;
	}

	public void setConfirmedAt(Date confirmedAt)
	{
		this.confirmedAt = confirmedAt;
	}

	public String getConfirmedBy()
	{
		return confirmedBy;
	}

	public void setConfirmedBy(String confirmedBy)
	{
		this.confirmedBy = confirmedBy;
	}

	public Appointment.ConfirmedByType getConfirmedByType()
	{
		return confirmedByType;
	}

	public void setConfirmedByType(Appointment.ConfirmedByType confirmedByType)
	{
		this.confirmedByType = confirmedByType;
	}
}
