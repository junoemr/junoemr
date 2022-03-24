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
package org.oscarehr.fax.converter;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.transfer.FaxAccountUpdateInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaxAccountUpdateToEntityConverter extends AbstractModelConverter<FaxAccountUpdateInput, FaxAccount>
{
	@Autowired
	private FaxAccountDao faxAccountDao;

	@Override
	public FaxAccount convert(FaxAccountUpdateInput input)
	{
		FaxAccount entity = faxAccountDao.find(input.getId());
		// if the password is not changed, keep the saved one
		String password = input.getPassword();
		if (StringUtils.isNotBlank(password))
		{
			entity.setLoginPassword(password);
		}
		entity.setIntegrationEnabled(input.isEnabled());
		entity.setInboundEnabled(input.isEnableInbound());
		entity.setOutboundEnabled(input.isEnableOutbound());
		entity.setDisplayName(StringUtils.trimToNull(input.getDisplayName()));
		entity.setCoverLetterOption(input.getCoverLetterOption());
		entity.setEmail(StringUtils.trimToNull(input.getAccountEmail()));
		entity.setReplyFaxNumber(input.getOptionalFaxNumber().map(PhoneNumberModel::getNumber).orElse(null));
		return entity;
	}
}
