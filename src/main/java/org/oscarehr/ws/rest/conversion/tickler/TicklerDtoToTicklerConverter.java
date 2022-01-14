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

package org.oscarehr.ws.rest.conversion.tickler;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ws.rest.transfer.tickler.TicklerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicklerDtoToTicklerConverter extends AbstractModelConverter<TicklerDto, Tickler>
{

	private TicklerLinkDtoToTicklerLinkConverter ticklerLinkDtoToTicklerLinkConverter;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public TicklerDtoToTicklerConverter(TicklerLinkDtoToTicklerLinkConverter ticklerLinkDtoToTicklerLinkConverter)
	{
		this.ticklerLinkDtoToTicklerLinkConverter = ticklerLinkDtoToTicklerLinkConverter;
	}

	// ==========================================================================
	// AbstractModelConverter Overrides
	// ==========================================================================

	@Override
	public Tickler convert(TicklerDto input)
	{
		if (input == null)
		{
			return null;
		}

		Tickler tickler = new Tickler();

		tickler.setId(input.getTicklerNo());
		tickler.setDemographicNo(input.getDemographicNo());
		tickler.setTaskAssignedTo(input.getTaskAssignedTo());
		tickler.setMessage(input.getMessage());
		tickler.setPriority(input.getPriority());
		tickler.setStatus(input.getStatus());
		tickler.setServiceDate(input.getServiceDate());
		tickler.setTicklerLink(ticklerLinkDtoToTicklerLinkConverter.convert(input.getAttachments()));
		tickler.getTicklerLink().forEach((link) -> link.setTickler(tickler));

		return tickler;
	}
}
