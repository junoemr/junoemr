/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.ws.rest.conversion.messaging;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.messaging.model.Messageable;
import org.oscarehr.ws.rest.transfer.messaging.MessageableDto;
import org.springframework.beans.BeanUtils;

public class MessageableToMessageableDtoConverter extends AbstractModelConverter<Messageable<?>, MessageableDto>
{
	// ==========================================================================
	// AbstractModelConverter Overrides
	// ==========================================================================

	@Override
	public MessageableDto convert(Messageable<?> input)
	{
		MessageableDto messgeableDto = new MessageableDto();
		BeanUtils.copyProperties(input, messgeableDto, "entity");

		return messgeableDto;
	}
}
