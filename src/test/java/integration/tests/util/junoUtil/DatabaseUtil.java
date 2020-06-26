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
package integration.tests.util.junoUtil;

import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.service.ProviderService;
import org.oscarehr.util.SpringUtils;

import java.time.LocalDate;
import java.util.ArrayList;

import static integration.tests.util.junoUtil.ProviderDataUtil.*;

public class DatabaseUtil
{/*
	public static final String[] providerNo = {"600006", "200002", "200003"};
	public static final String[] lastNames = {"Drlname", "Adminlname", "Nurselname"};
	public static final String[] firstNames = {"Drfname", "Adminfname", "Nursefname"};
	public static final String[] type = {"doctor", "admin", "nurse"};
	public static final String[] specialty = {"Family", "111111111", "9874397159"};
	public static final String[] team = {"Clinic", "111111111", "9874397159"};
	public static final String[] sex = {"M", "F", "F"};
	public static final String[] dob = {"1980-02-02", "1988-08-08", "2000-08-08"};
	public static final String address = "31 Bastion Square #302";
	public static final String homePhone = "250-686-8560";
	public static final String workPhone = "+1 888-686-8560";
	public static final String email = "ailin.zhu@cloudpractice.ca";
	public static final String pager = "71077777";
	public static final String cell = "250-250-2500";
	public static final String otherPhone = "250-686-8560";
	public static final String fax = "+1 888-686-8560";
	public static final String mspNo = "6060666";
	public static final String thirdPartyBillinNo = "1010101";
	public static final String billingNo = "1010102";
	public static final String alternateBillingNo = "1010103";
	public static final String bcpEligibility = "1";//yes
	public static final String ihaProviderMnemonic = "PROG17H17-Hydroxyprogesterone";
	public static final String specialtyCodeNo = "Family010";
	public static final String groupBillingNo = "CA-123456";
	public static final String cpsidNo = "987654321";
	public static final String selfLearningUsername = "druser";
	public static final String selfLearningPassword = "Welcome@123";
	public static final String status = "1";//active
*/
	public static Demographic createTestDemographic()
	{
		DemographicService demoService = (DemographicService)SpringUtils.getBean("demographic.service.DemographicService");
		Demographic demo = new Demographic();
		demo.setDateOfBirth(LocalDate.now());
		demo.setFirstName("test");
		demo.setLastName("test");
		demo.setSex("F");
		return demoService.addNewDemographicRecord(AuthUtils.TEST_PROVIDER_ID, demo, null, new ArrayList<DemographicExt>());
	}
	public static void createTestProvider()
	{
		int length = providerNo.length;
		for (int i=0; i<length; i++) {
			ProviderService providerService = (ProviderService) SpringUtils.getBean("provider.service.ProviderService");
			ProviderData demoProvider = new ProviderData();
			demoProvider.setProviderNo(Integer.parseInt(providerNo[i]));
			demoProvider.setFirstName(firstNames[i]);
			demoProvider.setLastName(lastNames[i]);
			providerService.addNewProvider(AuthUtils.TEST_PROVIDER_ID, demoProvider, null);
		}
	}

}
