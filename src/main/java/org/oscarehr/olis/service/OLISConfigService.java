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
package org.oscarehr.olis.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.dataMigration.converter.out.ProviderDbToModelConverter;
import org.oscarehr.olis.converter.OLISSystemPreferencesToTransferConverter;
import org.oscarehr.olis.dao.OLISProviderPreferencesDao;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISProviderPreferences;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.olis.transfer.OLISProviderSettingsTransfer;
import org.oscarehr.olis.transfer.OLISSystemSettingsTransfer;
import org.oscarehr.olis.transfer.OLISSystemSettingsUpdateInput;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class OLISConfigService
{
	@Autowired
	private OLISSystemPreferencesDao olisSystemPreferencesDao;

	@Autowired
	private OLISProviderPreferencesDao olisProviderPreferencesDao;

	@Autowired
	private ProviderDataDao providerDao;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

	@Autowired
	private OLISSystemPreferencesToTransferConverter olisSystemPreferencesToTransferConverter;

	@Autowired
	private ProviderDbToModelConverter providerDbToModelConverter;

	public OLISSystemSettingsTransfer getOlisSystemSettings()
	{
		OLISSystemPreferences olisSystemPreferences = olisSystemPreferencesDao.getPreferences();
		return olisSystemPreferencesToTransferConverter.convert(olisSystemPreferences);
	}

	public OLISSystemSettingsTransfer updateOlisSystemSettings(OLISSystemSettingsUpdateInput input)
	{
		OLISSystemPreferences olisSystemPreferences = olisSystemPreferencesDao.getPreferences();
		olisSystemPreferences.setFilterPatients(input.isFilterPatients());
		olisSystemPreferences.setPollFrequency(input.getFrequency());
		olisSystemPreferences.setStartTime(ConversionUtils.toDateTimeString(input.getStartDateTime(), DateTimeFormatter.ofPattern(OLISPollingService.OLIS_DATE_FORMAT)));
		olisSystemPreferencesDao.merge(olisSystemPreferences);

		return olisSystemPreferencesToTransferConverter.convert(olisSystemPreferences);
	}

	public List<OLISProviderSettingsTransfer> getAllProviderSettings()
	{
		List<ProviderData> allProvidersList = providerDao.findByActiveStatus(true);

		return allProvidersList.stream().map((provider) ->
		{
			OLISProviderPreferences olisProviderPreferences = olisProviderPreferencesDao
					.findById(provider.getId())
					.orElseGet(() -> new OLISProviderPreferences(provider.getId()));

			OLISProviderSettingsTransfer transfer = new OLISProviderSettingsTransfer();
			transfer.setStartDateTime(olisProviderPreferences.getOptionalStartDateTime()
					.map((str) -> ConversionUtils.toZonedDateTime(str, DateTimeFormatter.ofPattern(OLISPollingService.OLIS_DATE_FORMAT)))
					.orElse(null));
			transfer.setConfigured(isProviderConfigured(provider.getId()));
			transfer.setProvider(providerDbToModelConverter.convert(provider));
			return transfer;
		}).collect(Collectors.toList());
	}

	public boolean isProviderConfigured(String providerId)
	{
		String officialLastName  = userPropertyDAO.getStringValue(providerId, UserProperty.OFFICIAL_LAST_NAME);
		String olisIdType = userPropertyDAO.getStringValue(providerId, UserProperty.OFFICIAL_OLIS_IDTYPE);

		return !(StringUtils.isBlank(officialLastName) || StringUtils.isBlank(olisIdType));
	}
}
