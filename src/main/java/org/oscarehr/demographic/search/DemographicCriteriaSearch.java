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
package org.oscarehr.demographic.search;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.demographic.model.DemographicExt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DemographicCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORT_MODE
	{
		DemographicNo,
		DemographicName,
		DemographicLastName,
		DemographicFirstName,
		Status,
		RosterStatus,
		Phone,
		Address,
		DOB,
		ChartNo,
		Sex,
		Hin,
		Email,
		ProviderName
	}

	public enum STATUS_MODE
	{
		all,
		active,
		inactive,
		deceased,
		fired,
		ic,
		id,
		moved
	}

	private MatchMode matchMode = MatchMode.START;

	private Integer DemographicNo;
	private String hin;
	
	@Getter
	@Setter
	private String healthCardVersion;
	
	@Getter
	@Setter
	private String healthCardProvince;
	
	@Getter
	@Setter
	private String notHealthCardVersion;

	private String firstName;
	private String lastName;
	private String phone;
	private String address;
	private LocalDate dateOfBirth;
	private String chartNo;
	private String sex;
	private String providerNo;
	private String email;

	private SORT_MODE sortMode = SORT_MODE.DemographicNo;
	private List<STATUS_MODE> statusModes = new ArrayList<>();
	private boolean negateStatus = false;
	private boolean customWildcardsEnabled = false;
	private boolean forceConjoinOnNames = false;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();

		// left join demographic merged and only return the result if it isn't merged
		criteria.createAlias(alias + ".mergedDemographicsList", "dm", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.or(
				Restrictions.isNull("dm.id"),
				Restrictions.eq("dm.deleted", true)
		));

		// determine criteria join mode ('AND' filter criteria vs 'OR' filter criteria)
		Junction junction = getEmptyJunction();

		// set the search filters
		if(getDemographicNo() != null)
		{
			junction.add(Restrictions.eq("demographicId", getDemographicNo()));
		}

		/* sometimes (such as when using 'OR' for the junction type) you need to force the search to still and the names
		* setting the forceConjoinOnNames will do this here */
		if(forceConjoinOnNames && getFirstName() != null && getLastName() != null)
		{
			Criterion conjunction = Restrictions.conjunction()
					.add(getRestrictionCriterion("firstName", getFirstName()))
					.add(getRestrictionCriterion("lastName", getLastName()));
			junction.add(conjunction);
		}
		else
		{
			if(getFirstName() != null)
			{
				junction.add(getRestrictionCriterion("firstName", getFirstName()));
			}
			if(getLastName() != null)
			{
				junction.add(getRestrictionCriterion("lastName", getLastName()));
			}
		}
		if(getHin() != null)
		{
			junction.add(getRestrictionCriterion("hin", getHin()));
			// If we get a version code to verify, we want one of two things: exactly that version code, or anything but
			if (getHealthCardVersion() != null)
			{
				junction.add(getRestrictionCriterion("ver", getHealthCardVersion()));
			}
			else if (getNotHealthCardVersion() != null)
			{
				junction.add(Restrictions.or(
						Restrictions.isNull("ver"),
						Restrictions.ne("ver", getNotHealthCardVersion())));
			}
		}
		
		if (getHealthCardProvince() != null)
		{
			junction.add(getRestrictionCriterion("hcType", getHealthCardProvince()));
		}

		// birthdate searches are always exact due to how the values are stored
		if(getDateOfBirth() != null)
		{
			Criterion conjunction = Restrictions.conjunction()
					.add(Restrictions.eq("yearOfBirth", String.valueOf(getDateOfBirth().getYear())))
					.add(Restrictions.eq("monthOfBirth", StringUtils.leftPad(String.valueOf(getDateOfBirth().getMonthValue()), 2, "0")))
					.add(Restrictions.eq("dayOfBirth", StringUtils.leftPad(String.valueOf(getDateOfBirth().getDayOfMonth()), 2, "0")));
			junction.add(conjunction);
		}

		if(getAddress() != null)
		{
			junction.add(getRestrictionCriterion("address", getAddress()));
		}
		if(getPhone() != null)
		{
			criteria.createAlias(alias + ".demographicExtSet", "dx", Criteria.LEFT_JOIN, Restrictions.eq("dx.key", DemographicExt.KEY_DEMO_CELL));

			junction.add(Restrictions.or(getRestrictionCriterion("phone", getPhone()),
					getRestrictionCriterion("phone2", getPhone()),
					getRestrictionCriterion("dx.value", getPhone())));
		}
		if(getChartNo() != null)
		{
			junction.add(getRestrictionCriterion("chartNo", getChartNo()));
		}
		if(getSex() != null)
		{
			junction.add(getRestrictionCriterion("sex", getSex()));
		}
		if(getProviderNo() != null)
		{
			junction.add(getRestrictionCriterion("providerNo", getProviderNo()));
		}
		if(getEmail() != null)
		{
			junction.add(getRestrictionCriterion("email", getEmail()));
		}

		criteria.add(junction);

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
		//build list of status names
		ArrayList<String> statuses = new ArrayList<>();
		for (STATUS_MODE status : statusModes)
		{
			if (status == STATUS_MODE.all)
			{
				// if we see status "all" at any point just bail.
				return;
			} else
			{
				switch (status)
				{
					case active:
						statuses.add(Demographic.PatientStatus.AC.name());
						break;
					case inactive:
						statuses.add(Demographic.PatientStatus.IN.name());
						break;
					case deceased:
						statuses.add(Demographic.PatientStatus.DE.name());
						break;
					case fired:
						statuses.add(Demographic.PatientStatus.FI.name());
						break;
					case ic:
						statuses.add(Demographic.PatientStatus.IC.name());
						break;
					case id:
						statuses.add(Demographic.PatientStatus.ID.name());
						break;
					case moved:
						statuses.add(Demographic.PatientStatus.MO.name());
						break;
				}
			}
		}

		//set hibernate restrictions
		if (statuses.size() > 0)
		{
			if (negateStatus)
			{
				criteria.add(Restrictions.not(Restrictions.in("patientStatus", statuses)));
			}
			else
			{
				criteria.add(Restrictions.in("patientStatus", statuses));
			}
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
			case DemographicFirstName:
			{
				criteria.addOrder(getOrder("firstName"));
				break;
			}
			case DemographicLastName:
			{
				criteria.addOrder(getOrder("lastName"));
				break;
			}
			case Status:
			{
				criteria.addOrder(getOrder("patientStatus"));
				break;
			}
			case RosterStatus:
			{
				criteria.addOrder(getOrder("rosterStatus"));
				break;
			}
			case Hin:
			{
				criteria.addOrder(getOrder("hin"));
				break;
			}
			case ProviderName:
			{
				criteria.addOrder(getOrder("provider.id"));
				break;
			}
			case Sex: criteria.addOrder(getOrder("sex")); break;
			case Phone: criteria.addOrder(getOrder("phone"));break;
			case Address: criteria.addOrder(getOrder("address")); break;
			case ChartNo: criteria.addOrder(getOrder("chartNo")); break;
			case DemographicNo:
			default: criteria.addOrder(getOrder("demographicId")); break;
		}
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

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public SORT_MODE getSortMode()
	{
		return sortMode;
	}

	public void setSortMode(SORT_MODE sortMode)
	{
		this.sortMode = sortMode;
	}

	public STATUS_MODE getStatusMode()
	{
		if (statusModes.size() > 0)
		{
			return statusModes.get(0);
		}
		else
		{
			return STATUS_MODE.all;
		}
	}

	public List<STATUS_MODE> getStatusModeList()
	{
		return this.statusModes;
	}

	public void setStatusMode(STATUS_MODE statusMode)
	{
		this.statusModes = new ArrayList<>();
		this.statusModes.add(statusMode);
	}

	public void setStatusModeList(List<STATUS_MODE> statusModes)
	{
		this.statusModes = new ArrayList<>(statusModes);
	}

	public void addStatusMode(STATUS_MODE statusMode)
	{
		this.statusModes.add(statusMode);
	}

	public MatchMode getMatchMode()
	{
		return matchMode;
	}

	public boolean isCustomWildcardsEnabled()
	{
		return customWildcardsEnabled;
	}

	public boolean isNegateStatus()
	{
		return negateStatus;
	}

	public void setNegateStatus(boolean negateStatus)
	{
		this.negateStatus = negateStatus;
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

	/**
	 * Set this to true when you always need the name filter to AND the first and last name values.
	 * By default this will have no effect, but when the Junction type is set to disjunction (OR), this will be important
	 * @return true if the flag is enabled, false otherwise
	 */
	public boolean isForceConjoinOnNames()
	{
		return forceConjoinOnNames;
	}

	/**
	 * Set this to true when you always need the name filter to AND the first and last name values.
	 * By default this will have no effect, but when the Junction type is set to disjunction (OR), this will be important
	 * @param forceConjoinOnNames - default false
	 */
	public void setForceConjoinOnNames(boolean forceConjoinOnNames)
	{
		this.forceConjoinOnNames = forceConjoinOnNames;
	}
}
