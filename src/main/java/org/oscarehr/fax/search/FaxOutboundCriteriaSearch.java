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
package org.oscarehr.fax.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FaxOutboundCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE
	{
		DemographicNo,
		CreationDate
	}

	private String sentTo;
	private String providerNo;
	private Integer demographicNo;
	private FaxOutbound.FileType fileType;
	private FaxOutbound.Status status;
	private FaxOutbound.NotificationStatus notificationStatus;
	private Long faxAccountId;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean archived;
	private List<String> remoteStatusList;
	private boolean includeExternalStatuses;
	private FaxOutboxTransferOutbound.CombinedStatus combinedStatus;

	private SORTMODE sortMode = SORTMODE.CreationDate;


	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();

		// left join demographic merged and only return the result if it isn't merged
		criteria.createAlias(alias + ".faxAccount", "fa", Criteria.INNER_JOIN);

		if(getFaxAccountId() != null)
		{
			criteria.add(Restrictions.eq("fa.id", getFaxAccountId()));
		}
		if(getFileType() != null)
		{
			criteria.add(Restrictions.eq("fileType", getFileType()));
		}
		if(getSentTo() != null)
		{
			criteria.add(Restrictions.eq("sentTo", getSentTo()));
		}
		if(getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("providerNo", getProviderNo()));
		}
		if(getDemographicNo() != null)
		{
			criteria.add(Restrictions.eq("demographicNo", getDemographicNo()));
		}
		if(getEndDate() != null)
		{
			criteria.add(Restrictions.le("createdAt", Timestamp.valueOf(getEndDate().atTime(LocalTime.MAX))));
		}
		if(getStartDate() != null)
		{
			criteria.add(Restrictions.ge("createdAt", Timestamp.valueOf(getStartDate().atTime(LocalTime.MIN))));
		}
		if(getArchived() != null)
		{
			criteria.add(Restrictions.eq("archived", getArchived()));
		}

		/* the combined status will override the individual status parameters */
		if(combinedStatus != null)
		{
			criteria.add(getCombinedStatusCriteria(combinedStatus));
		}
		else
		{
			if(getStatus() != null)
			{
				criteria.add(Restrictions.eq("status", getStatus()));
			}
			if(getRemoteStatusList() != null && !getRemoteStatusList().isEmpty())
			{
				Criterion criterion = Restrictions.in("externalStatus", getRemoteStatusList());
				if(!includeExternalStatuses)
				{
					criterion = Restrictions.or(Restrictions.not(criterion), Restrictions.isNull("externalStatus"));
				}
				criteria.add(criterion);
			}
		}

		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case DemographicNo: criteria.addOrder(getOrder("demographicNo")); break;
			case CreationDate:
			default: criteria.addOrder(getOrder("createdAt")); break;
		}
	}

	private Criterion getCombinedStatusCriteria(FaxOutboxTransferOutbound.CombinedStatus combinedStatus)
	{
		Criterion criterion = null;
		switch(combinedStatus)
		{
			case ERROR:
			{
				criterion = Restrictions.eq("status", FaxOutbound.Status.ERROR); break;
			}
			case QUEUED:
			{
				criterion = Restrictions.eq("status", FaxOutbound.Status.QUEUED); break;
			}
			case IN_PROGRESS:
			{
				criterion = Restrictions.and(
						Restrictions.eq("status", FaxOutbound.Status.SENT),
						Restrictions.or(
								Restrictions.not(Restrictions.in("externalStatus", SRFaxApiConnector.RESPONSE_STATUSES_FINAL)),
								Restrictions.isNull("externalStatus")
						)
				); break;
			}
			case INTEGRATION_FAILED:
			{
				criterion = Restrictions.and(
						Restrictions.eq("status", FaxOutbound.Status.SENT),
						Restrictions.eq("externalStatus", SRFaxApiConnector.RESPONSE_STATUS_FAILED)
				); break;
			}
			case INTEGRATION_SUCCESS:
			{
				criterion = Restrictions.and(
						Restrictions.eq("status", FaxOutbound.Status.SENT),
						Restrictions.eq("externalStatus", SRFaxApiConnector.RESPONSE_STATUS_SENT)
				); break;
			}
		}
		return criterion;
	}

	public String getSentTo()
	{
		return sentTo;
	}

	public void setSentTo(String sentTo)
	{
		this.sentTo = sentTo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public FaxOutbound.FileType getFileType()
	{
		return fileType;
	}

	public void setFileType(FaxOutbound.FileType fileType)
	{
		this.fileType = fileType;
	}

	public FaxOutbound.Status getStatus()
	{
		return status;
	}

	public void setStatus(FaxOutbound.Status status)
	{
		this.status = status;
	}

	public FaxOutbound.NotificationStatus getNotificationStatus() { return notificationStatus; }

	public void setNotificationStatus (FaxOutbound.NotificationStatus notificationStatus) { this.notificationStatus = notificationStatus; }

	public Long getFaxAccountId()
	{
		return faxAccountId;
	}

	public void setFaxAccountId(Long faxAccountId)
	{
		this.faxAccountId = faxAccountId;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public Boolean getArchived()
	{
		return archived;
	}

	public void setArchived(Boolean archived)
	{
		this.archived = archived;
	}

	/**
	 * the list of remote statuses values to filter.
	 */
	public List<String> getRemoteStatusList()
	{
		return remoteStatusList;
	}

	/**
	 * set the list of remote statuses values to filter.
	 * @param exclude
	 * if true, results will be filtered to included only statuses in the given list.
	 * if false, results will be filtered to exclude these statuses
	 * this setting is ignored in the remote status list is not set or is empty
	 */
	public void setRemoteStatusList(List<String> remoteStatusList, boolean exclude)
	{
		this.remoteStatusList = remoteStatusList;
		this.includeExternalStatuses = exclude;
	}

	/**
	 * if true, results will be filtered to included only statuses in the given list.
	 * if false, results will be filtered to exclude these statuses
	 * this setting is ignored in the remote status list is not set or is empty
	 */
	public boolean isIncludeExternalStatuses()
	{
		return includeExternalStatuses;
	}

	/** set the combined status to filter on.
	 * the combined status will override the individual (local and remote) status parameters
	 */
	public void setCombinedStatus(FaxOutboxTransferOutbound.CombinedStatus combinedStatus)
	{
		this.combinedStatus = combinedStatus;
	}

	public FaxOutboxTransferOutbound.CombinedStatus getCombinedStatus()
	{
		return combinedStatus;
	}
}