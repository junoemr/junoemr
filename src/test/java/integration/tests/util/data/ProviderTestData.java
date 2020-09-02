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

package integration.tests.util.data;

public class ProviderTestData
{
	public String providerNo = "";
	public String lastName = "";
	public String firstName = "";
	public String type = "";
	public String sex = "";
	public String dob = "";
	public String specialty = "Family";

	public String sitesAssigned = "";
	public String team = "Clinic";
	public String address = "31 Bastion Square #302";
	public String homePhone = "250-686-8560";
	public String workPhone = "+1 888-686-8560";
	public String email = "ailin.zhu@cloudpractice.ca";
	public String pager = "71077777";
	public String cell = "250-250-2500";
	public String otherPhone = "250-686-8560";
	public String fax = "+1 888-686-8560";
	public String mspNo = "6060666";
	public String thirdPartyBillinNo = "1010101";
	public String billingNo = "1010102";
	public String alternateBillingNo = "1010103";
	public String bcpEligibility = "1";//yes
	public String ihaProviderMnemonic = "PROG17H17-Hydroxyprogesterone";
	public String specialtyCodeNo = "Family010";
	public String groupBillingNo = "CA-123456";
	public String cpsidNo = "987654321";
	public String billCenter = "";
	public String selfLearningUsername = "druser";
	public String selfLearningPassword = "Welcome@123";
	public String status = "1";//active


	public ProviderTestData(String providerNo, String lastName, String firstName, String type, String specialty, String sex, String dob)
	{
		this.providerNo = providerNo;
		this.lastName = lastName;
		this.firstName = firstName;
		this.type = type;
		this.specialty = specialty;
		this.sex = sex;
		this.dob = dob;
	}
}
