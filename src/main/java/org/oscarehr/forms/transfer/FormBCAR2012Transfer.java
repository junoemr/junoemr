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

package org.oscarehr.forms.transfer;

import oscar.util.ConversionUtils;

import java.util.Date;

// Corresponds to formBCAR2012 table, however we don't want to instantiate a full model for it
public class FormBCAR2012Transfer
{

	private Integer demographicNo;
	private Date edd;
	private String lastName;
	private String firstName;
	private Date dateOfBirth;
	private String gravida;
	private String term;
	private String phone;
	private String langPreferred;
	private String phn;
	private String doula;
	private String doulaNo;

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Date getEdd()
	{
		return edd;
	}

	public String getEddAsString()
	{
		return ConversionUtils.toDateString(this.edd);
	}

	public void setEdd(Date edd)
	{
		this.edd = edd;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getFullName()
	{
		return getLastName() + ", " + getFirstName();
	}

	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}

	public String getDateOfBirthAsString()
	{
		return ConversionUtils.toDateString(this.dateOfBirth);
	}

	public void setDateOfBirth(Date dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getGravida()
	{
		return gravida;
	}

	public void setGravida(String gravida)
	{
		this.gravida = gravida;
	}

	public String getTerm()
	{
		return term;
	}

	public void setTerm(String term)
	{
		this.term = term;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getLangPreferred()
	{
		return langPreferred;
	}

	public void setLangPreferred(String langPreferred)
	{
		this.langPreferred = langPreferred;
	}

	public String getPhn()
	{
		return phn;
	}

	public void setPhn(String phn)
	{
		this.phn = phn;
	}

	public String getDoula()
	{
		return doula;
	}

	public void setDoula(String doula)
	{
		this.doula = doula;
	}

	public String getDoulaNo()
	{
		return doulaNo;
	}

	public void setDoulaNo(String doulaNo)
	{
		this.doulaNo = doulaNo;
	}
}
