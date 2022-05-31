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

@Component
public class ConsultationRequestToDomainConverter extends AbstractModelConverter<ConsultationRequestTo1, ConsultationRequest>
{
	@Autowired
	private ProfessionalSpecialistToDomainConverter specialistToDomainConverter;

	@Override
	public ConsultationRequest convert(ConsultationRequestTo1 transfer)
	{
		ConsultationRequest request = new ConsultationRequest();
		if (transfer == null)
		{
			return null;
		}

		BeanUtils.copyProperties(transfer, request,
				"professionalSpecialist",
				"appointmentDateTime",
				"referralDate",
				"followUpDate");

		request.setReferralDate(ConversionUtils.toLegacyDate(transfer.getReferralDate()));
		request.setFollowUpDate(ConversionUtils.toLegacyDate(transfer.getFollowUpDate()));
		request.setAppointmentDate(ConversionUtils.toLegacyDateTime(transfer.getAppointmentDateTime()));
		request.setAppointmentTime(ConversionUtils.toLegacyDateTime(transfer.getAppointmentDateTime()));

		// Specialist is a many-to-one and is handled slightly differently
		request.setProfessionalSpecialist(specialistToDomainConverter.convert(transfer.getProfessionalSpecialist()));
		return request;
	}
}
