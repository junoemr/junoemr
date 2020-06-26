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

import java.util.ArrayList;
import java.util.HashMap;

public class ProviderCollection
{
	public static ArrayList<Provider> providers = new ArrayList<Provider>();
	public static HashMap<String, Provider> providerMap = new HashMap<String, Provider>();
	static {
		Provider provider = new Provider();
		provider.setProviderNo("100001");
		provider.setLastName("Apple");
		provider.setFirstName("Afname");
		provider.setType("doctor");
		provider.setSpecialty("Family");
		provider.setDob("1980-02-02");
		provider.setSex("F");
		providers.add(provider);
		providerMap.put(provider.getLastName(), provider);

		Provider provider2 = new Provider();
		provider.setProviderNo("100002");
		provider2.setLastName("Berry");
		provider2.setFirstName("Bfname");
		provider2.setType("doctor");
		provider2.setSpecialty("Family");
		provider2.setDob("1988-08-08");
		provider2.setSex("M");
		providers.add(provider2);
		providerMap.put(provider2.getLastName(), provider2);

		Provider provider3 = new Provider();
		provider3.setLastName("Cherry");
		provider3.setFirstName("Cfname");
		provider3.setType("doctor");
		provider3.setSpecialty("Maternity");
		provider3.setDob("2000-08-08");
		provider3.setSex("F");
		providers.add(provider3);
		providerMap.put(provider3.getLastName(), provider3);

	}

}
