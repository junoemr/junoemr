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
import org.oscarehr.common.model.Explain;
import org.oscarehr.common.model.ReportByExamples;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarReport.bean.RptByExampleQueryBeanHandler;
import oscar.oscarReport.data.RptByExampleData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class RptByExampleAction extends Action
{
	private static final OscarProperties properties = OscarProperties.getInstance();
	private static final Long maxRows = Long.parseLong(properties.getProperty("rpt_by_example.max_rows"));
	private static final Logger logger = MiscUtils.getLogger();

	private ReportByExamplesDao rptByExampleDao = SpringUtils.getBean(ReportByExamplesDao.class);
	private ReportByExamplesDao dao = SpringUtils.getBean(ReportByExamplesDao.class);
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);


	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String userSql = "";
		try
		{
			RptByExampleForm frm = (RptByExampleForm) form;

			String providerNo = (String) request.getSession().getAttribute("user");
			securityInfoManager.requireAllPrivilege(providerNo, SecurityInfoManager.READ, null, "_admin", "_report");

			RptByExampleQueryBeanHandler hd = new RptByExampleQueryBeanHandler();
			Collection favorites = hd.getFavoriteCollection(providerNo);
			request.setAttribute("favorites", favorites);

			userSql = frm.getSql();
			String preparedUserSql = RptByExampleData.prepareUserQuery(userSql);

			if(preparedUserSql != null)
			{
				logger.info("User Query: " + userSql);

				List<Explain> explainResultList = rptByExampleDao.getExplainResultList(preparedUserSql);

				// save the query record
				write2Database(userSql, providerNo);

				if(allowQueryRun(explainResultList))
				{
					RptByExampleData exampleData = new RptByExampleData();

					String results = exampleData.exampleReportGenerate(preparedUserSql, properties);

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
			logger.warn("Report By Example user SQL Error");
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

	private void write2Database(String query, String providerNo)
	{
		if(query != null && query.compareTo("") != 0)
		{
			ReportByExamples r = new ReportByExamples();
			r.setProviderNo(providerNo);
			r.setQuery(query);
			r.setDate(new Date());
			dao.persist(r);
		}
	}

	private boolean allowQueryRun(List<Explain> explainResults)
	{
		for(Explain result : explainResults)
		{
			logger.info("Explain Result:\n" + result.toString());

			BigInteger rows = result.getRows();
			// if rows > maxRows
			if(rows != null && BigInteger.valueOf(maxRows).compareTo(rows) < 0)
			{
				return false;
			}
		}
		return true;
	}
}
