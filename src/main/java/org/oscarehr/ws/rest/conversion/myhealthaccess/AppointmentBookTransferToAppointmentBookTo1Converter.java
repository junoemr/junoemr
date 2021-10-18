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
package org.oscarehr.ws.rest.conversion.myhealthaccess;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentBookTo1;
import org.oscarehr.ws.rest.transfer.myhealthaccess.AppointmentBookingTransfer;

import java.time.ZoneId;

public class AppointmentBookTransferToAppointmentBookTo1Converter extends AbstractModelConverter<AppointmentBookingTransfer, AppointmentBookTo1>
{
	// ==========================================================================
	// AbstractModelConverter Implementation
	// ==========================================================================

	@Override
	public AppointmentBookTo1 convert(AppointmentBookingTransfer input)
	{
		if (input == null)
		{
			return null;
		}

		AppointmentBookTo1 appointmentBookTo1 = new AppointmentBookTo1();
		appointmentBookTo1.setAppointmentNo(input.getAppointmentNo());
		appointmentBookTo1.setProviderNo(input.getProviderNo());
		appointmentBookTo1.setDemographicNo(input.getDemographicNo());
		appointmentBookTo1.setStartDateTime(input.getStartDateTime().withZoneSameInstant(ZoneId.systemDefault()));
		appointmentBookTo1.setEndDateTime(input.getEndDateTime().withZoneSameInstant(ZoneId.systemDefault()));
		appointmentBookTo1.setSite(input.getSite());
		appointmentBookTo1.setReason(input.getReason());
		appointmentBookTo1.setNotes(input.getNotes());
		appointmentBookTo1.setVirtual(input.isVirtual());
		appointmentBookTo1.setRemoteId(input.getRemotePatientId());
		appointmentBookTo1.setType(input.getType().name().toLowerCase());

		return appointmentBookTo1;
	}
}
