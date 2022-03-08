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
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.provider.FaxProviderFactory;
import org.oscarehr.fax.provider.FaxUploadProvider;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class FaxOutboundToModelConverter extends AbstractModelConverter<FaxOutbound, FaxOutboxTransferOutbound>
{
	@Override
	public FaxOutboxTransferOutbound convert(FaxOutbound entity)
	{
		FaxAccount faxAccount = entity.getFaxAccount();
		FaxUploadProvider uploadProvider = FaxProviderFactory.createFaxUploadProvider(faxAccount);

		FaxOutboxTransferOutbound model = new FaxOutboxTransferOutbound();
		model.setId(entity.getId());
		model.setFaxAccountId(faxAccount.getId());
		model.setProviderId(entity.getProviderNo());
		model.setProviderName(entity.getProvider().getDisplayName());
		model.setDemographicId(entity.getDemographicNo());
		model.setSystemStatus(entity.getStatus());
		model.setSystemStatusMessage(entity.getStatusMessage());
		model.setArchived(entity.getArchived());
		model.setNotificationStatus(entity.getNotificationStatus());
		model.setSystemSentDateTime(ConversionUtils.toNullableLocalDateTime(entity.getCreatedAt()));
		model.setToFaxNumber(PhoneNumberModel.of(entity.getSentTo(), PhoneNumberModel.PHONE_TYPE.FAX));
		model.setFileType(entity.getFileType());
		model.setIntegrationStatus(entity.getExternalStatus());
		model.setIntegrationQueuedDateTime(ConversionUtils.toNullableLocalDateTime(entity.getCreatedAt()));
		model.setIntegrationSentDateTime(ConversionUtils.toNullableLocalDateTime(entity.getExternalDeliveryDate()));
		model.setCombinedStatus(entity.getCombinedStatus(uploadProvider));

		return model;
	}
}