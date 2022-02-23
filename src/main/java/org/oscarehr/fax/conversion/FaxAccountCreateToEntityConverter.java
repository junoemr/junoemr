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
package org.oscarehr.fax.conversion;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.transfer.FaxAccountCreateInput;
import org.springframework.stereotype.Component;

@Component
public class FaxAccountCreateToEntityConverter extends AbstractModelConverter<FaxAccountCreateInput, FaxAccount>
{
	@Override
	public FaxAccount convert(FaxAccountCreateInput input)
	{
		FaxAccount entity = new FaxAccount();
		entity.setLoginId(StringUtils.trimToNull(input.getAccountLogin()));
		entity.setLoginPassword(StringUtils.trimToNull(input.getPassword()));
		entity.setIntegrationEnabled(input.isEnabled());
		entity.setInboundEnabled(input.isEnableInbound());
		entity.setOutboundEnabled(input.isEnableOutbound());
		entity.setDisplayName(StringUtils.trimToNull(input.getDisplayName()));
		entity.setCoverLetterOption(input.getCoverLetterOption());
		entity.setEmail(StringUtils.trimToNull(input.getAccountEmail()));
		entity.setReplyFaxNumber(StringUtils.trimToNull(input.getFaxNumber()));
		return entity;
	}
}
