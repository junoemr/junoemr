/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.demographic.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.time.LocalDate;

public class DemographicCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE {
		DemographicNo,
		DemographicName,
		Phone,
		Address,
		DOB,
		ChartNo,
		Sex,
		ProviderName
	}

	public enum STATUSMODE {
		all, active, inactive, deceased
	}

	public enum SORTDIR {
		asc,desc
	}

	private Integer DemographicNo;
	private String hin;
	private String firstName;
	private String lastName;
	private String phone;
	private String address;
	private LocalDate dateOfBirth;
	private String chartNo;
	private String sex;
	private String providerNo;
	private String providerFirstName;
	private String providerLastName;

	private boolean integrator = false;
	private boolean outOfDomain = false;
	private boolean exactMatch = false;

	private SORTMODE sortMode = SORTMODE.DemographicNo;
	private SORTDIR sortDir = SORTDIR.asc;
	private STATUSMODE statusMode = STATUSMODE.all;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		boolean exactMatching = isExactMatch();

		if(getDemographicNo() != null)
		{
			criteria.add(Restrictions.eq("demographicId", getDemographicNo()));
		}
		if(getFirstName() != null)
		{
			criteria.add(getRestrictionCriterion("firstName", getFirstName(), exactMatching, MatchMode.START));
		}
		if(getLastName() != null)
		{
			criteria.add(getRestrictionCriterion("lastName", getLastName(), exactMatching, MatchMode.START));
		}
		if(getHin() != null)
		{
			criteria.add(getRestrictionCriterion("hin", getHin(), exactMatching, MatchMode.START));
		}
		if(getDateOfBirth() != null)
		{
			criteria.add(Restrictions.eq("dayOfBirth", String.valueOf(getDateOfBirth().getDayOfMonth())));
			criteria.add(Restrictions.eq("monthOfBirth", String.valueOf(getDateOfBirth().getMonthValue())));
			criteria.add(Restrictions.eq("yearOfBirth", String.valueOf(getDateOfBirth().getYear())));
		}

		if(getAddress() != null)
		{
			criteria.add(getRestrictionCriterion("address", getAddress(), exactMatching, MatchMode.ANYWHERE));
		}
		if(getPhone() != null)
		{
			criteria.add(getRestrictionCriterion("phone", getPhone(), exactMatching, MatchMode.ANYWHERE));
			criteria.add(getRestrictionCriterion("phone2", getPhone(), exactMatching, MatchMode.ANYWHERE));
		}
		if(getChartNo() != null)
		{
			criteria.add(getRestrictionCriterion("chartNo", getChartNo(), exactMatching, MatchMode.START));
		}
		if(getSex() != null)
		{
			criteria.add(getRestrictionCriterion("sex", getSex(), exactMatching, MatchMode.START));
		}
		if(getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("providerNo", getProviderNo()));
		}

		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setStatusCriteria(Criteria criteria)
	{
		switch(statusMode)
		{
			case active: criteria.add(Restrictions.eq("patientStatus", Demographic.PatientStatus.AC.name())); break;
			case inactive: criteria.add(Restrictions.eq("patientStatus", Demographic.PatientStatus.IN.name())); break;
			case deceased: criteria.add(Restrictions.eq("patientStatus", Demographic.PatientStatus.DE.name())); break;
		}
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case DOB: {
				criteria.addOrder(getOrder("yearOfBirth"));
				criteria.addOrder(getOrder("monthOfBirth"));
				criteria.addOrder(getOrder("dayOfBirth"));
				break;
			}
			case DemographicName: {
				criteria.addOrder(getOrder("lastName"));
				criteria.addOrder(getOrder("firstName"));
				break;
			}
			case Sex: criteria.addOrder(getOrder("sex")); break;
			case Phone: criteria.addOrder(getOrder("phone")); break;
			case Address: criteria.addOrder(getOrder("address")); break;
			case ChartNo: criteria.addOrder(getOrder("chartNo")); break;
			case DemographicNo:
			default: criteria.addOrder(getOrder("demographicId")); break;
		}
	}
	private Order getOrder(String propertyName)
	{
		return (SORTDIR.asc.equals(sortDir))? Order.asc(propertyName) : Order.desc(propertyName);
	}

	private Criterion getRestrictionCriterion(String propertyName, String value, boolean exactMatch, MatchMode matchMode)
	{
		if(exactMatch)
		{
			return Restrictions.eq(propertyName, value);
		}
		return Restrictions.ilike(propertyName, value, matchMode);
	}

	public Integer getDemographicNo()
	{
		return DemographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		DemographicNo = demographicNo;
	}

	public String getHin()
	{
		return hin;
	}

	public void setHin(String hin)
	{
		this.hin = hin;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public LocalDate getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getChartNo()
	{
		return chartNo;
	}

	public void setChartNo(String chartNo)
	{
		this.chartNo = chartNo;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getProviderFirstName()
	{
		return providerFirstName;
	}

	public void setProviderFirstName(String providerFirstName)
	{
		this.providerFirstName = providerFirstName;
	}

	public String getProviderLastName()
	{
		return providerLastName;
	}

	public void setProviderLastName(String providerLastName)
	{
		this.providerLastName = providerLastName;
	}

	public boolean isIntegrator()
	{
		return integrator;
	}

	public void setIntegrator(boolean integrator)
	{
		this.integrator = integrator;
	}

	public boolean isOutOfDomain()
	{
		return outOfDomain;
	}

	public void setOutOfDomain(boolean outOfDomain)
	{
		this.outOfDomain = outOfDomain;
	}

	public boolean isExactMatch()
	{
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch)
	{
		this.exactMatch = exactMatch;
	}

	public SORTMODE getSortMode()
	{
		return sortMode;
	}

	public void setSortMode(SORTMODE sortMode)
	{
		this.sortMode = sortMode;
	}

	public SORTDIR getSortDir()
	{
		return sortDir;
	}

	public void setSortDir(SORTDIR sortDir)
	{
		this.sortDir = sortDir;
	}

	public STATUSMODE getStatusMode()
	{
		return statusMode;
	}

	public void setStatusMode(STATUSMODE statusMode)
	{
		this.statusMode = statusMode;
	}
}
