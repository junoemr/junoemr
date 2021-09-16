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
package org.oscarehr.integration.aqs.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.integration.aqs.model.CommunicationType;

public class CommunicationTypeToVirtualAppointmentTypeConverter extends AbstractModelConverter<CommunicationType, Appointment.VirtualAppointmentType>
{

	// ==========================================================================
	// AbstractModelConverter implementation
	// ==========================================================================

	@Override
	public Appointment.VirtualAppointmentType convert(CommunicationType input)
	{
		if (input == null)
		{
			return null;
		}

		switch (input)
		{
			case VIDEO:
				return Appointment.VirtualAppointmentType.VIDEO;
			case AUDIO:
				return Appointment.VirtualAppointmentType.AUDIO;
			case CHAT:
				return Appointment.VirtualAppointmentType.CHAT;
			default:
				return Appointment.VirtualAppointmentType.NONE;
		}
	}
}
