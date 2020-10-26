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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.Appointments;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.provider.Provider;

public class CDSAppointmentImportMapper extends AbstractCDSImportMapper<Appointments, Appointment>
{
	public CDSAppointmentImportMapper()
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

	protected Provider getImportProvider(Appointments importStructure)
	{
		Provider provider = new Provider();
		provider.setFirstName(importStructure.getProvider().getName().getFirstName());
		provider.setLastName(importStructure.getProvider().getName().getLastName());
		provider.setOhipNumber(importStructure.getProvider().getOHIPPhysicianId());
		return provider;
	}
}
