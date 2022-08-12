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
package oscar.oscarLab.ca.all.parsers.AHS;

import com.google.common.collect.Lists;
import org.oscarehr.provider.model.ProviderData;
import oscar.oscarLab.ca.all.parsers.AbstractMessageHandlerITBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractMessageHandlerConnectCareMatchITBase extends AbstractMessageHandlerITBase
{
	private static final String E_DELIVERY_ID_A= "A1111";
	private static final String E_DELIVERY_ID_B= "B2222";
	private static final String E_DELIVERY_ID_C= "C3333";
	private static final String E_DELIVERY_ID_D= "D4444";

	@Override
	protected List<ProviderData> getTestProviders()
	{
		ProviderData provider1 = buildSimpleProvider("1", "match", "one");
		provider1.setAlbertaEDeliveryIds(E_DELIVERY_ID_A);

		ProviderData provider2 = buildSimpleProvider("2", "match_inactive", "two");
		provider2.setStatus(ProviderData.PROVIDER_STATUS_INACTIVE);
		provider2.setAlbertaEDeliveryIds(Lists.newArrayList(E_DELIVERY_ID_A, E_DELIVERY_ID_B));

		ProviderData provider3 = buildSimpleProvider("3", "match", "three");
		provider3.setAlbertaEDeliveryIds(Lists.newArrayList(E_DELIVERY_ID_B, E_DELIVERY_ID_C, E_DELIVERY_ID_D));

		ProviderData provider4 = buildSimpleProvider("4", "match_none", "four");

		ProviderData provider5 = buildSimpleProvider("5", "match_invalid", "five");
		provider5.setOhipNo(E_DELIVERY_ID_A);
		provider5.setRmaNo(E_DELIVERY_ID_A);
		provider5.setBillingNo(E_DELIVERY_ID_A);
		provider5.setOntarioLifeLabsId(E_DELIVERY_ID_B);
		provider5.setOntarioCnoNumber(E_DELIVERY_ID_B);
		provider5.setAlbertaTakNo(E_DELIVERY_ID_C);
		provider5.setHsoNo(E_DELIVERY_ID_C);

		ProviderData provider6 = buildSimpleProvider("6", "match_cc_id", "six");
		provider6.setAlbertaConnectCareId(E_DELIVERY_ID_D);

		return Lists.newArrayList(provider1, provider2, provider3, provider4, provider5, provider6);
	}

	@Override
	protected Map<String, List<String>> getProviderMatchingMap()
	{
		Map<String, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(E_DELIVERY_ID_A, Lists.newArrayList("1", "2"));
		matchingMap.put(E_DELIVERY_ID_B, Lists.newArrayList("2", "3"));
		matchingMap.put(E_DELIVERY_ID_C, Lists.newArrayList("3"));
		matchingMap.put(E_DELIVERY_ID_D, Lists.newArrayList("3", "6"));

		return matchingMap;
	}
}