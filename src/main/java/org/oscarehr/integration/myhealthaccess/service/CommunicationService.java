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
package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentSearchTo1;
import org.oscarehr.integration.myhealthaccess.dto.SmsMessageDto;
import org.springframework.stereotype.Service;

@Service
public class CommunicationService extends BaseService
{

	/**
	 * sends an sms message to the specified sms number
	 * @param integration - mha integration to use
	 * @param smsNumber - the sms number to send the message to
	 * @param smsText - the text to send
	 */
	public void sendSms(Integration integration, String smsNumber, String smsText)
	{
		SmsMessageDto smsMessageDto = new SmsMessageDto();
		smsMessageDto.setSmsNumber(smsNumber);
		smsMessageDto.setSmsText(smsText);

		String url = formatEndpoint("/integration/communication/send_sms");
		AppointmentSearchTo1 result = post(url, integration.getApiKey(), smsMessageDto, null);
	}


}
