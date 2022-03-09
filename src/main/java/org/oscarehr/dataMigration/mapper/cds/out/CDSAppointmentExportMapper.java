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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.Appointments;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Component
public class CDSAppointmentExportMapper extends AbstractCDSExportMapper<Appointments, Appointment>
{
	public CDSAppointmentExportMapper()
	{
		super();
	}

	@Override
	public Appointments exportFromJuno(Appointment exportStructure)
	{
		Appointments appointments = objectFactory.createAppointments();

		appointments.setAppointmentTime(getExportAppointmentTime(exportStructure));
		appointments.setDuration(BigInteger.valueOf(exportStructure.getDurationMin()));
		appointments.setAppointmentStatus(getExportStatus(exportStructure));
		appointments.setAppointmentDate(toNullableDateFullOrPartial(exportStructure.getAppointmentStartDateTime().toLocalDate()));
		appointments.setProvider(getExportProvider(exportStructure));
		appointments.setAppointmentPurpose(exportStructure.getReason());
		appointments.setAppointmentNotes(exportStructure.getNotes());

		return appointments;
	}

	protected Appointments.Provider getExportProvider(Appointment exportStructure)
	{
		ProviderModel provider = exportStructure.getProvider();
		Appointments.Provider primaryPhysician = null;
		if(provider != null)
		{
			primaryPhysician = objectFactory.createAppointmentsProvider();
			primaryPhysician.setName(toPersonNameSimple(provider));
			primaryPhysician.setOHIPPhysicianId(provider.getOhipNumber());
		}
		return primaryPhysician;
	}

	protected XMLGregorianCalendar getExportAppointmentTime(Appointment exportStructure)
	{
		LocalDateTime appointmentDateTime = exportStructure.getAppointmentStartDateTime();
		return ConversionUtils.toXmlGregorianCalendar(appointmentDateTime);
	}

	protected String getExportStatus(Appointment exportStructure)
	{
		AppointmentStatus appointmentStatus = exportStructure.getStatus();
		return appointmentStatus.getDescription();
	}
}
