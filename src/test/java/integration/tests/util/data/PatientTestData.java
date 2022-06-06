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

public class PatientTestData
{

	public String lastName = "";
	public String firstName = "";
	public String dobYear = "";
	public String dobMonth = "";
	public String dobDate = "";
	public String sex = "";
	public String hin = "";

	public String language = "English";
	public String title = "MS";
	public String spoken = "English";
	public String address = "31 Bastion Square #302";
	public String city = "Victoria";
	public String province = "BC";
	public String postal = "V8W 1J1";
	public String preferredPhone = "HOME";
	public String homePhone = "250-686-8560";
	public String homePhoneExt = "101";
	public String workPhone = "250-250-2500";
	public String workPhoneExt = "102";
	public String cellPhone = "250-250-2500";
	public String phoneComment = "Prefer Cell";
	public String newsletter = "No";
	public String aboriginal = "No";
	public String email = "ailin.zhu@cloudpractice.ca";
	public String phrUserName = "TTestLastName";
	public String effYear = "2020";
	public String effMonth = "06";
	public String effDate = "01";
	public String hcType = "BC";
	public String hcRenewYear = "2018";
	public String hcRenewMonth = "08";
	public String hcRenewDate = "08";
	public String countryOfOrigin = "CA";
	public String sin = "987654321";
	public String cytology = "123456";
	public String motherName = "Mom TestLastName";
	public String fatherName = "Dad TestLastName";
	public String referralDoctor = "Dr Referral";
	public String referralDoctorNo = "111111";//max 6 numbers
	public String rosterStatus = "RO";
	public String rosteredYear = "2018";
	public String rosteredMonth = "08";
	public String rosteredDate = "08";
	public String patientStatus = "AC";
	public String chartNo = "10001";

	public PatientTestData(String lastName, String firstName, String dobYear, String dobMonth, String dobDate, String sex, String hin)
	{
		this.lastName = lastName;
		this.firstName = firstName;
		this.dobYear = dobYear;
		this.dobMonth = dobMonth;
		this.dobDate = dobDate;
		this.sex = sex;
		this.hin = hin;
	}
}
