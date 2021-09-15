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

import ca.cloudpractice.aqs.client.model.QueuedAppointmentDto;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.aqs.model.CommunicationType;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.integration.aqs.model.RemoteUserType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class QueuedAppointmentDtoQueuedAppointmentConverter extends AbstractModelConverter<QueuedAppointmentDto, QueuedAppointment>
{
	@Override
	public QueuedAppointment convert(QueuedAppointmentDto input)
	{
		QueuedAppointment queuedAppointment = new QueuedAppointment();

		BeanUtils.copyProperties(input, queuedAppointment, "id", "integrationPatientId", "extraInfo", "createdByType", "communicationType");

		queuedAppointment.setCreatedByType(new RemoteUserType(input.getCreatedByType()));
		queuedAppointment.setId(input.getId());
		queuedAppointment.setDemographicNo(Integer.parseInt(StringUtils.trimToEmpty(input.getIntegrationPatientId())));
		if (input.getCommunicationType() != null)
		{
			queuedAppointment.setCommunicationType(CommunicationType.fromString(input.getCommunicationType().toString()));
		}

		if (input.getExtraInfo() != null)
		{
			@SuppressWarnings("unchecked")
			Map<String, String> extrasMap = (Map<String, String>)input.getExtraInfo();
			if (extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_NOTES) != null)
			{
				queuedAppointment.setNotes(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_NOTES));
			}
			if (extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_CLINIC_ID) != null)
			{
				queuedAppointment.setClinicId(UUID.fromString(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_CLINIC_ID)));
			}

			Optional.ofNullable(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_REASON_TYPE_ID))
					.ifPresent((reasonTypeId) -> queuedAppointment.setReasonTypeId(Integer.parseInt(reasonTypeId)));
			Optional.ofNullable(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_DURATION_MINUTES))
					.ifPresent((durationMinutes) -> queuedAppointment.setDurationMinutes(Integer.parseInt(durationMinutes)));
			Optional.ofNullable(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_SITE_ID))
					.ifPresent((siteId) -> queuedAppointment.setSiteId(Integer.parseInt(siteId)));
			Optional.ofNullable(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_VIRTUAL))
					.ifPresent((virtual) -> queuedAppointment.setVirtual(Boolean.parseBoolean(virtual)));
			Optional.ofNullable(extrasMap.get(QueuedAppointmentExtrasConverter.EXTRAS_CRITICAL))
					.ifPresent((critical) -> queuedAppointment.setCritical(Boolean.parseBoolean(critical)));
		}

		// If this queued appointment was booked thought MHA force virtual
		if (input.getCreatedByType() == ca.cloudpractice.aqs.client.model.RemoteUserType.MHA_PATIENT)
		{
			queuedAppointment.setVirtual(true);
		}

		return queuedAppointment;
	}
}
