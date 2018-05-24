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
	public enum SORTMODE
	{
		DemographicNo,
		DemographicName,
		Phone,
		Address,
		DOB,
		ChartNo,
		Sex,
		ProviderName
	}

	public enum STATUSMODE
	{
		all, active, inactive, deceased
	}

	public enum SORTDIR
	{
		asc, desc
	}

	private MatchMode matchMode = MatchMode.START;

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

	private SORTMODE sortMode = SORTMODE.DemographicNo;
	private SORTDIR sortDir = SORTDIR.asc;
	private STATUSMODE statusMode = STATUSMODE.all;
	private boolean customWildcardsEnabled = false;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();

		// left join demographic merged and only return the result if it isn't merged
		criteria.createAlias(alias + ".mergedDemographicsList", "dm", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.or(Restrictions.isNull("dm.id"), Restrictions.ne("dm.deleted", 0)));

		// set the search filters
		if(getDemographicNo() != null)
		{
			criteria.add(getRestrictionCriterion("demographicId", String.valueOf(getDemographicNo())));
		}
		if(getFirstName() != null)
		{
			criteria.add(getRestrictionCriterion("firstName", getFirstName()));
		}
		if(getLastName() != null)
		{
			criteria.add(getRestrictionCriterion("lastName", getLastName()));
		}
		if(getHin() != null)
		{
			criteria.add(getRestrictionCriterion("hin", getHin()));
		}

		// birthdate searches are always exact due to how the values are stored
		if(getDateOfBirth() != null)
		{
			criteria.add(Restrictions.eq("dayOfBirth", String.valueOf(getDateOfBirth().getDayOfMonth())));
			criteria.add(Restrictions.eq("monthOfBirth", String.valueOf(getDateOfBirth().getMonthValue())));
			criteria.add(Restrictions.eq("yearOfBirth", String.valueOf(getDateOfBirth().getYear())));
		}

		if(getAddress() != null)
		{
			criteria.add(getRestrictionCriterion("address", getAddress()));
		}
		if(getPhone() != null)
		{
			criteria.add(getRestrictionCriterion("phone", getPhone()));
			criteria.add(getRestrictionCriterion("phone2", getPhone()));
		}
		if(getChartNo() != null)
		{
			criteria.add(getRestrictionCriterion("chartNo", getChartNo()));
		}
		if(getSex() != null)
		{
			criteria.add(getRestrictionCriterion("sex", getSex()));
		}
		if(getProviderNo() != null)
		{
			criteria.add(getRestrictionCriterion("providerNo", getProviderNo()));
		}

		// set status filters and result ordering
		setStatusCriteria(criteria);
		setOrderByCriteria(criteria);
		return criteria;
	}

	private Criterion getRestrictionCriterion(String propertyName, String value)
	{
		MatchMode matchMode = this.matchMode;
		if(customWildcardsEnabled)
		{
			// convert the * character to a wildcard for mysql
			value = value.replaceAll("\\*", "%");
			matchMode = MatchMode.EXACT;
		}
		else
		{
			// escape mysql wildcard characters for literal search
			value = value.replaceAll("%", "\\\\%");
			value = value.replaceAll("_", "\\\\_");
		}
		return Restrictions.ilike(propertyName, value, matchMode);
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

	public MatchMode getMatchMode()
	{
		return matchMode;
	}

	public boolean isCustomWildcardsEnabled()
	{
		return customWildcardsEnabled;
	}

	/**
	 * Enable custom wildcard use in search strings. Enabling this forces the search matching to EXACT mode.
	 * @param customWildcardsEnabled - true to enable custom wildcard use, false otherwise
	 */
	public void setCustomWildcardsEnabled(boolean customWildcardsEnabled)
	{
		this.customWildcardsEnabled = customWildcardsEnabled;
	}

	/**
	 * Set the query keyword matching mode.
	 * When custom wildcards are enabled searches will always use EXACT
	 * @param matchMode - match mode
	 */
	private void setMatchMode(MatchMode matchMode)
	{
		this.matchMode = matchMode;
	}

	/**
	 * Set the query keyword matching mode to match anywhere in the search string.
	 * When custom wildcards are enabled searches will always use EXACT
	 */
	public void setMatchModeAnywhere()
	{
		setMatchMode(MatchMode.ANYWHERE);
	}

	/**
	 * Set the query keyword matching mode to exact matching.
	 * When custom wildcards are enabled searches will always use EXACT
	 */
	public void setMatchModeExact()
	{
		setMatchMode(MatchMode.EXACT);
	}

	/**
	 * Set the query keyword matching mode to anything starting with the search string.
	 * When custom wildcards are enabled searches will always use EXACT
	 */
	public void setMatchModeStart()
	{
		setMatchMode(MatchMode.START);
	}

	/**
	 * Set the query keyword matching mode to anything ending with the search string.
	 * When custom wildcards are enabled searches will always use EXACT
	 */
	public void setMatchModeEnd()
	{
		setMatchMode(MatchMode.END);
	}
}
