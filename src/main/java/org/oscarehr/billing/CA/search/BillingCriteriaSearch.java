package org.oscarehr.billing.CA.search;

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
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.util.Date;

public class BillingCriteriaSearch extends AbstractCriteriaSearch
{
	public enum ORDER_BY {
		ID,
		UPDATE_DATE
	}

	private Integer billingNo;
	private Integer clinicNo;
	private Integer demographicNo;
	private Integer providerNo;
	private Integer appointmentNo;
	private String  status;
	private Date updateDate;
	private ORDER_BY orderBy = ORDER_BY.ID;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		// set the search filters
		if (getBillingNo() != null)
		{
			criteria.add(Restrictions.eq("id", getBillingNo()));
		}
		if (getClinicNo() != null)
		{
			criteria.add(Restrictions.eq("clinicNo", getClinicNo()));
		}
		if (getDemographicNo() != null)
		{
			criteria.add(Restrictions.eq("demographicNo", getDemographicNo()));
		}
		if (getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("providerNo", getProviderNo()));
		}
		if (getAppointmentNo() != null)
		{
			criteria.add(Restrictions.eq("appointmentNo", getAppointmentNo()));
		}
		if (getStatus() != null)
		{
			criteria.add(Restrictions.eq("status", getStatus()));
		}
		if (getUpdateDate() != null)
		{
			criteria.add(Restrictions.eq("updateDate", getUpdateDate()));
		}

		setOrderByCriteria(criteria);

		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria) {
		switch (this.orderBy)
		{
			case ID:
			{
				criteria.addOrder(getOrder("id"));
				break;
			}
			case UPDATE_DATE:
			{
				criteria.addOrder(getOrder("updateDate"));
				break;
			}
		}
	}

	public Integer getBillingNo()
	{
		return billingNo;
	}

	public Integer getClinicNo()
	{
		return clinicNo;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public String getStatus()
	{
		return status;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public ORDER_BY getOrderBy()
	{
		return orderBy;
	}

	public void setBillingNo(Integer billingNo)
	{
		this.billingNo = billingNo;
	}

	public void setClinicNo(Integer clinicNo)
	{
		this.clinicNo = clinicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public void setProviderNo(Integer providerNo)
	{
		this.providerNo = providerNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}

	public void setOrderBy(ORDER_BY order)
	{
		this.orderBy = order;
	}

}
