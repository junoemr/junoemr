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
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import com.google.common.collect.Lists;
import integration.tests.config.TestConfig;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.oscarehr.JunoApplication;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.oscarLab.ca.all.parsers.AbstractMessageHandlerTestBase;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = {JunoApplication.class, TestConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CLSDIHandlerIT extends AbstractMessageHandlerTestBase
{
	private static final String HSO_NO = "12345";

	@Override
	protected MessageHandler getTestHandler()
	{
		// use Mockito to avoid needing to parse an HL7 message in the real handler construction etc.
		MessageHandler testHandler = Mockito.mock(CLSDIHandler.class);
		Mockito.doCallRealMethod().when(testHandler).getProviderMatchingCriteria(Mockito.anyString());
		return testHandler;
	}

	@Override
	protected List<ProviderData> getTestProviders()
	{
		ProviderData provider1 = buildSimpleProvider("1", "match", "one");
		provider1.setHsoNo(HSO_NO);

		ProviderData provider2 = buildSimpleProvider("2", "match_inactive", "two");
		provider2.setHsoNo(HSO_NO);
		provider2.setStatus(ProviderData.PROVIDER_STATUS_INACTIVE);

		ProviderData provider3 = buildSimpleProvider("3", "match_none", "three");

		ProviderData provider4 = buildSimpleProvider("4", "match_invalid", "four");
		provider4.setOhipNo(HSO_NO);
		provider4.setRmaNo(HSO_NO);
		provider4.setBillingNo(HSO_NO);
		provider4.setOntarioLifeLabsId(HSO_NO);
		provider4.setOntarioCnoNumber(HSO_NO);
		provider4.setAlbertaTakNo(HSO_NO);
		provider4.setAlbertaEDeliveryIds(HSO_NO);

		return Lists.newArrayList(provider1, provider2, provider3, provider4);
	}

	@Override
	protected Map<String, List<String>> getProviderMatchingMap()
	{
		Map<String, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(HSO_NO, Lists.newArrayList("1", "2"));

		return matchingMap;
	}
}