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

import ca.cloudpractice.aqs.client.model.QueuedAppointmentStatus;
import ca.cloudpractice.aqs.client.model.RemoteUserType;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueuedAppointmentBookingTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class QueuedAppointmentBookingTransferQueuedAppointmentConverter extends AbstractModelConverter<QueuedAppointmentBookingTransfer, QueuedAppointment>
{

	@Override
	public QueuedAppointment convert(QueuedAppointmentBookingTransfer input)
	{
		QueuedAppointment queuedAppointment = new QueuedAppointment();
		BeanUtils.copyProperties(input, queuedAppointment, "reasonType");

		queuedAppointment.setStatus(QueuedAppointmentStatus.QUEUED);
		queuedAppointment.setCreatedByType(new org.oscarehr.integration.aqs.model.RemoteUserType(RemoteUserType.JUNO_SECURITY));
		queuedAppointment.setReasonTypeId(input.getReasonType());

		return queuedAppointment;
	}
}