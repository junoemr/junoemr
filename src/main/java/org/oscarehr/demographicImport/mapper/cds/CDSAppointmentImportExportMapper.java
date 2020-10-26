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
package org.oscarehr.demographicImport.mapper.cds;

import org.oscarehr.common.xml.cds.v5_0.model.Appointments;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameSimple;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.provider.Provider;
import oscar.util.ConversionUtils;

import java.math.BigInteger;
import java.time.LocalDate;

public class CDSAppointmentImportExportMapper extends AbstractCDSImportExportMapper<Appointments, Appointment>
{
	public CDSAppointmentImportExportMapper()
	{
		super();
	}

	@Override
	public Appointment importToJuno(Appointments importStructure)
	{
		Appointment appointment = new Appointment();

		appointment.setProvider(getImportProvider(importStructure));
		appointment.setReason(importStructure.getAppointmentPurpose());

		return appointment;
	}

	@Override
	public Appointments exportFromJuno(Appointment exportStructure)
	{
		Appointments appointments = objectFactory.createAppointments();

		appointments.setProvider(getExportProvider(exportStructure));
		appointments.setAppointmentDate(getExportAppointmentDate(exportStructure));
		appointments.setDuration(BigInteger.valueOf(exportStructure.getDurationMin()));
		appointments.setAppointmentStatus(exportStructure.getStatus());
		appointments.setAppointmentPurpose(exportStructure.getReason());

		return appointments;
	}


	protected Provider getImportProvider(Appointments importStructure)
	{
		Provider provider = new Provider();
		provider.setFirstName(importStructure.getProvider().getName().getFirstName());
		provider.setLastName(importStructure.getProvider().getName().getLastName());
		provider.setOhipNumber(importStructure.getProvider().getOHIPPhysicianId());
		return provider;
	}

	protected Appointments.Provider getExportProvider(Appointment exportStructure)
	{
		Provider provider = exportStructure.getProvider();
		Appointments.Provider primaryPhysician = null;
		if(provider != null)
		{
			PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();
			personNameSimple.setFirstName(provider.getFirstName());
			personNameSimple.setLastName(provider.getLastName());

			primaryPhysician = objectFactory.createAppointmentsProvider();
			primaryPhysician.setName(personNameSimple);
			primaryPhysician.setOHIPPhysicianId(provider.getOhipNumber());
		}
		return primaryPhysician;
	}

	protected DateFullOrPartial getExportAppointmentDate(Appointment exportStructure)
	{
		DateFullOrPartial fullOrPartial = objectFactory.createDateFullOrPartial();
		LocalDate appointmentDate = exportStructure.getAppointmentStartDateTime().toLocalDate();
		fullOrPartial.setFullDate(ConversionUtils.toXmlGregorianCalendar(appointmentDate));

		return fullOrPartial;
	}
}
