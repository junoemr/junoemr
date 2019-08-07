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

package org.oscarehr.common.dao;

import org.oscarehr.common.model.Explain;
import org.oscarehr.common.model.ReportByExamples;
import org.oscarehr.common.model.ReportByExamplesExplain;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class ReportByExamplesExplainDao extends AbstractDao<ReportByExamplesExplain>
{
	public ReportByExamplesExplainDao()
	{
		super(ReportByExamplesExplain.class);
	}

	public void persist(ReportByExamples template, Explain explain)
	{
		Integer keyLen = (explain.getKeyLen() != null) ? Integer.parseInt(explain.getKeyLen()) : null;
		Integer rows = (explain.getRows() != null) ? explain.getRows().intValue() : null;

		ReportByExamplesExplain examplesExplain = new ReportByExamplesExplain();
		examplesExplain.setReport(template);
		examplesExplain.setSelectType(explain.getSelectType());
		examplesExplain.setTable(explain.getTable());
		examplesExplain.setType(explain.getType());
		examplesExplain.setPossibleKeys(explain.getPossibleKeys());
		examplesExplain.setKey(explain.getKey());
		examplesExplain.setKeyLen(keyLen);
		examplesExplain.setRef(explain.getRef());
		examplesExplain.setRows(rows);
		examplesExplain.setExtra(explain.getExtra());

		persist(examplesExplain);
	}

	public void persistAll(ReportByExamples template, List<Explain> explainList)
	{
		for(Explain explain : explainList)
		{
			persist(template, explain);
		}
	}
}
