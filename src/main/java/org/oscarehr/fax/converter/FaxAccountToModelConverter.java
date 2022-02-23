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

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.springframework.stereotype.Component;

@Component
public class FaxAccountToModelConverter extends AbstractModelConverter<FaxAccount, FaxAccountTransferOutbound>
{
	@Override
	public FaxAccountTransferOutbound convert(FaxAccount entity)
	{
		FaxAccountTransferOutbound model = new FaxAccountTransferOutbound();
		model.setId(entity.getId());
		model.setAccountLogin(entity.getLoginId());
		model.setAccountType(entity.getIntegrationType());
		model.setEnabled(entity.isIntegrationEnabled());
		model.setEnableInbound(entity.isInboundEnabled());
		model.setEnableOutbound(entity.isOutoundEnabled());
		model.setDisplayName(entity.getDisplayName());
		model.setCoverLetterOption(entity.getCoverLetterOption());
		model.setAccountEmail(entity.getEmail());
		model.setFaxNumber(entity.getReplyFaxNumber());
		return model;
	}
}
