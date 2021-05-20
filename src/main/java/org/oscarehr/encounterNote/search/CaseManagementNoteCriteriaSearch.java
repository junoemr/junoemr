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
package org.oscarehr.encounterNote.search;

import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.e2e.constant.Constants;
import org.oscarehr.encounterNote.model.CaseManagementIssue;

@Data
public class CaseManagementNoteCriteriaSearch extends AbstractCriteriaSearch
{
	public enum RESULT_SET
	{
		ALL,
		LATEST_REVISIONS,
	}

	private Integer demographicId;
	private String uuid;
	private RESULT_SET resultSet = RESULT_SET.LATEST_REVISIONS;
	private Constants.IssueCodes issueCode = null;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();
		DetachedCriteria issueSubquery = DetachedCriteria.forClass(CaseManagementIssue.class, "cmni");

		if(issueCode == null)
		{
			// only get regular notes, left join issues where issues are null, or issue codes not in the list

			Disjunction or = Restrictions.disjunction();
			criteria.createAlias(alias + ".issueNoteList", "issueNote", Criteria.LEFT_JOIN);
			or.add(Restrictions.isNull("issueNote.id"));


			issueSubquery.createAlias(issueSubquery.getAlias() + ".issue", "issue", Criteria.INNER_JOIN);

			// not any of these codes
			String[] notIn = new String[] {
					Constants.IssueCodes.OMeds.name(),
					Constants.IssueCodes.SocHistory.name(),
					Constants.IssueCodes.MedHistory.name(),
					Constants.IssueCodes.Concerns.name(),
					Constants.IssueCodes.Reminders.name(),
					Constants.IssueCodes.FamHistory.name(),
					Constants.IssueCodes.RiskFactors.name(),
			};
			issueSubquery.add(Restrictions.in("issue.code", notIn));

			// filter subquery by demographic
			if(getDemographicId() != null)
			{
				issueSubquery.add(Restrictions.eq("cmni.demographic.demographicId", getDemographicId()));
			}

			issueSubquery.setProjection(Projections.property("cmni.id"));
			or.add(Subqueries.propertyNotIn("issueNote.id.caseManagementIssue.id", issueSubquery));
			criteria.add(or);
		}
		else
		{
			criteria.createAlias(alias + ".issueNoteList", "issueNote", Criteria.INNER_JOIN);

			// get history notes based on issue code
			issueSubquery.createAlias(issueSubquery.getAlias() + ".issue", "issue", Criteria.INNER_JOIN);
			issueSubquery.add(Restrictions.eq("issue.code", issueCode.name()));

			// filter subquery by demographic
			if(getDemographicId() != null)
			{
				issueSubquery.add(Restrictions.eq("cmni.demographic.demographicId", getDemographicId()));
			}

			issueSubquery.setProjection(Projections.property("cmni.id"));
			criteria.add(Subqueries.propertyEq("issueNote.id.caseManagementIssue.id", issueSubquery));
		}


		// set the search filters
		if(getUuid() != null)
		{
			criteria.add(Restrictions.eq("uuid", String.valueOf(getUuid())));
		}
		if(getDemographicId() != null)
		{
			criteria.add(Restrictions.eq("demographic.demographicId", getDemographicId()));
		}

		// filter note revisions out, getting the latest as the 'max id' since that's how the old dao does it.
		if(resultSet == RESULT_SET.LATEST_REVISIONS)
		{
			// use raw sql because I couldn't get projections to select a single column result
			criteria.add(Restrictions.sqlRestriction(
					"{alias}.note_id = (SELECT max(cmn2.note_id) FROM casemgmt_note cmn2 WHERE cmn2.uuid = {alias}.uuid GROUP BY cmn2.uuid)"
			));
		}

		return criteria;
	}

	public void setIssueCodeNone()
	{
		this.setIssueCode(null);
	}
	public void setIssueCodeFamilyHistory()
	{
		this.setIssueCode(Constants.IssueCodes.FamHistory);
	}
	public void setIssueCodeSocialHistory()
	{
		this.setIssueCode(Constants.IssueCodes.SocHistory);
	}
	public void setIssueCodeMedicalHistory()
	{
		this.setIssueCode(Constants.IssueCodes.MedHistory);
	}
	public void setIssueCodeReminders()
	{
		this.setIssueCode(Constants.IssueCodes.Reminders);
	}
	public void setIssueCodeRiskFactors()
	{
		this.setIssueCode(Constants.IssueCodes.RiskFactors);
	}
	public void setIssueCodeConcerns()
	{
		this.setIssueCode(Constants.IssueCodes.Concerns);
	}
}
