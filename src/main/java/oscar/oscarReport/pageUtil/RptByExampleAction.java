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


package oscar.oscarReport.pageUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.exception.SQLGrammarException;
import org.oscarehr.common.dao.ReportByExamplesDao;
import org.oscarehr.common.dao.ReportByExamplesExplainDao;
import org.oscarehr.common.model.Explain;
import org.oscarehr.common.model.ReportByExamples;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.report.SQLReportHelper;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarReport.bean.RptByExampleQueryBeanHandler;
import oscar.oscarReport.data.RptByExampleData;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class RptByExampleAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties properties = OscarProperties.getInstance();

	private static final Boolean enableQueryRestrictions = properties.isPropertyActive("report_by_example.enable_restrictions");
	private static final Boolean enforceQueryRestrictions = properties.isPropertyActive("report_by_example.enforce_restrictions");
	private static final Long maxRows = Long.parseLong(properties.getProperty("report_by_example.max_rows"));
	private static final Integer maxResults = Integer.parseInt(properties.getProperty("report_by_example.max_results"));

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ReportByExamplesDao reportByExamplesDao = SpringUtils.getBean(ReportByExamplesDao.class);
	private ReportByExamplesExplainDao reportByExamplesExplainDao = SpringUtils.getBean(ReportByExamplesExplainDao.class);


	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String userSql = "";
		try
		{
			RptByExampleForm frm = (RptByExampleForm) form;

			String providerNo = (String) request.getSession().getAttribute("user");
			securityInfoManager.requireAllPrivilege(providerNo, Permission.ADMIN_READ, Permission.REPORT_READ);

			RptByExampleQueryBeanHandler hd = new RptByExampleQueryBeanHandler();
			Collection favorites = hd.getFavoriteCollection(providerNo);
			request.setAttribute("favorites", favorites);

			userSql = frm.getSql();
			String preparedUserSql = RptByExampleData.prepareUserQuery(userSql);

			if(preparedUserSql != null)
			{
				logger.info("User Query: " + userSql);

				boolean allowRun = true;
				List<Explain> explainResultList = null;

				ReportByExamples logEntry = saveInitialLog(providerNo, userSql);

				if(enableQueryRestrictions)
				{
					//check for special exempt cases
					String explainExemptSql = SQLReportHelper.getExplainSkippableQuery(preparedUserSql);
					if(explainExemptSql != null)
					{
						preparedUserSql = explainExemptSql;
					}
					else
					{
						explainResultList = reportByExamplesDao.getExplainResultList(preparedUserSql);
						saveExplainResults(logEntry, explainResultList);

						if(enforceQueryRestrictions)
						{
							preparedUserSql = SQLReportHelper.applyEnforcedLimit(preparedUserSql, maxResults);
							allowRun = SQLReportHelper.allowQueryRun(explainResultList, maxRows);
						}
					}
				}

				if(allowRun)
				{
					RptByExampleData exampleData = new RptByExampleData();
					String results = exampleData.exampleReportGenerate(preparedUserSql, properties);

					updateLog(logEntry, exampleData.resultSet);

					request.setAttribute("results", results);
					request.setAttribute("resultText", results);

					return mapping.findForward("success");
				}
				else
				{
					request.setAttribute("explainResults", explainResultList);
					request.setAttribute("errorMessage", "The report examines more than the maximum " + maxRows + " rows");
				}
			}
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			logger.error("Query Attempt: " + userSql);
			request.setAttribute("errorMessage", "Security Error");
		}
		catch(SQLException | SQLGrammarException e)
		{
			logger.warn("Report By Example user SQL Error: " + e.getMessage());
			logger.warn("Query Attempt: " + userSql);
			request.setAttribute("errorMessage", "Invalid SQL");
		}
		catch(Exception e)
		{
			logger.error("Report By Example Unknown Error", e);
			logger.error("Query Attempt: " + userSql);
			request.setAttribute("errorMessage", "Error");
		}
		return mapping.findForward("failure");
	}

	private void saveExplainResults(ReportByExamples logEntry, List<Explain> explainResultList)
	{
		try
		{
			reportByExamplesExplainDao.persistAll(logEntry, explainResultList);
		}
		catch(PersistenceException e)
		{
			logger.error("Failed to persist ReportByExample explain results.", e);
		}

	}
	private ReportByExamples saveInitialLog(String providerNo, String querySql)
	{
		ReportByExamples reportByExamples = new ReportByExamples();
		try
		{
			reportByExamples.setProviderNo(providerNo);
			reportByExamples.setQuery(querySql);
			reportByExamples.setDate(new Date());
			reportByExamplesDao.persist(reportByExamples);
		}
		catch(PersistenceException e)
		{
			logger.error("Failed to persist initial ReportByExample entry.", e);
		}

		return reportByExamples;
	}
	private void updateLog(ReportByExamples reportByExamples, ResultSet resultSet)
	{
		try
		{
			Long rowCount = -1L;
			if(resultSet != null)
			{
				resultSet.last();
				rowCount = new Integer(resultSet.getRow()).longValue();
			}

			reportByExamples.setDatetimeEnd(new Date());
			reportByExamples.setRowsReturned(rowCount);
			reportByExamplesDao.merge(reportByExamples);
		}
		catch(SQLException | PersistenceException e)
		{
			logger.error("Failed to update ReportByExample entry.", e);
		}
	}
}
