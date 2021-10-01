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
package org.oscarehr.olis.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.olis.service.OLISPollingService;
import org.oscarehr.olis.transfer.OLISSystemSettingsTransfer;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Component
public class OLISSystemPreferencesToTransferConverter extends AbstractModelConverter<OLISSystemPreferences, OLISSystemSettingsTransfer>
{
	@Override
	public OLISSystemSettingsTransfer convert(OLISSystemPreferences input)
	{
		OLISSystemSettingsTransfer transfer = new OLISSystemSettingsTransfer();
		List<String> warnings = new LinkedList<>();

		if(input != null)
		{
			transfer.setFrequency(input.getPollFrequency());
			transfer.setFilterPatients(input.isFilterPatients());

			transfer.setStartDateTime(input.getOptionalStartTime()
					.map((str) -> ConversionUtils.toZonedDateTime(str, DateTimeFormatter.ofPattern(OLISPollingService.OLIS_DATE_FORMAT)))
					.orElse(null));
		}
		else
		{
			warnings.add("OLIS system settings have not been set");
		}
		transfer.setWarnings(warnings);

		// always have a polling frequency
		if(transfer.getFrequency() == null)
		{
			transfer.setFrequency(OLISSystemPreferences.DEFAULT_POLLING_FREQUENCY);
		}

		return transfer;
	}
}
