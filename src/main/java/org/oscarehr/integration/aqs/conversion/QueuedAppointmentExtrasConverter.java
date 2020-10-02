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
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class QueuedAppointmentExtrasConverter extends AbstractModelConverter<QueuedAppointment, Map<String,String>>
{
	public static final String EXTRAS_NOTES = "notes";
	public static final String EXTRAS_REASON_TYPE_ID = "reason_type_id";
	public static final String EXTRAS_DURATION_MINUTES = "duration_minutes";
	public static final String EXTRAS_SITE_ID = "site_id";
	public static final String EXTRAS_CLINIC_ID = "clinic_id";
	public static final String EXTRAS_VIRTUAL = "virtual";
	public static final String EXTRAS_CRITICAL = "critical";

	@Override
	public Map<String, String> convert(QueuedAppointment input)
	{
		Map<String, String> extras = new HashMap<>();
		extras.put(EXTRAS_NOTES, input.getNotes());
		Optional.ofNullable(input.getClinicId()).ifPresent((clinicId) -> extras.put(EXTRAS_CLINIC_ID, clinicId.toString()));
		Optional.ofNullable(input.getReasonTypeId()).ifPresent((reasonType) -> extras.put(EXTRAS_REASON_TYPE_ID, reasonType.toString()));
		Optional.ofNullable(input.getDurationMinutes()).ifPresent((durationMinutes) -> extras.put(EXTRAS_DURATION_MINUTES, durationMinutes.toString()));
		Optional.ofNullable(input.getSiteId()).ifPresent((siteId) -> extras.put(EXTRAS_SITE_ID, siteId.toString()));
		Optional.ofNullable(input.getVirtual()).ifPresent((virtual) -> extras.put(EXTRAS_VIRTUAL, virtual.toString()));
		Optional.ofNullable(input.getCritical()).ifPresent((critical) -> extras.put(EXTRAS_CRITICAL, critical.toString()));

		return extras;
	}
}
