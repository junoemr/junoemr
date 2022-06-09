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

package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.ConsultationRequest;
import org.oscarehr.ws.rest.to.model.ConsultationRequestTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.time.ZoneId;
import java.util.Optional;

@Component
public class ConsultationRequestToTransferConverter extends AbstractModelConverter<ConsultationRequest, ConsultationRequestTo1>
{
	@Autowired
	private ProfessionalSpecialistToTransferConverter specialistToTransferConverter;

	@Override
	public ConsultationRequestTo1 convert(ConsultationRequest request)
	{
		ConsultationRequestTo1 transfer = new ConsultationRequestTo1();
		if (request == null)
		{
			return null;
		}

		BeanUtils.copyProperties(request, transfer,
				"professionalSpecialist",
				"appointmentDate",
				"appointmentTime",
				"referralDate",
				"followUpDate");
		transfer.setReferralDate(ConversionUtils.toNullableLocalDate(request.getReferralDate()));
		transfer.setFollowUpDate(ConversionUtils.toNullableLocalDate(request.getFollowUpDate()));

		transfer.setAppointmentDateTime(
				Optional.ofNullable(request.getAppointmentDateTime())
						.map(ConversionUtils::toLocalDateTime)
						.map((localDateTime) -> localDateTime.atZone(ZoneId.systemDefault()))
						.orElse(null));

		transfer.setProfessionalSpecialist(specialistToTransferConverter.convert(request.getProfessionalSpecialist()));
		return transfer;
	}

}
