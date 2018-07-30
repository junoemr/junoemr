/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package oscar.oscarReport.reportByTemplate;

import com.Ostermiller.util.CSVPrinter;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.Explain;
import org.oscarehr.log.dao.LogReportByTemplateDao;
import org.oscarehr.log.model.LogReportByTemplate;
import org.oscarehr.report.reportByTemplate.dao.ReportTemplatesDao;
import org.oscarehr.report.reportByTemplate.exception.ReportByTemplateException;
import org.oscarehr.report.reportByTemplate.model.PreparedSQLTemplate;
import org.oscarehr.report.reportByTemplate.model.ReportTemplates;
import org.oscarehr.report.reportByTemplate.service.ReportByTemplateService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarDB.DBHandler;
import oscar.oscarReport.data.RptResultStruct;
import oscar.util.UtilMisc;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * @author rjonasz
 */
public class SQLReporter implements Reporter
{

	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties properties = OscarProperties.getInstance();
	private static final Long maxRows = Long.parseLong(properties.getProperty("rpt_by_template.max_rows"));

	private static ReportByTemplateService reportByTemplateService = SpringUtils.getBean(ReportByTemplateService.class);
	private static LogReportByTemplateDao logReportByTemplateDao = SpringUtils.getBean(LogReportByTemplateDao.class);
	private static ReportTemplatesDao rptTemplatesDao = SpringUtils.getBean(ReportTemplatesDao.class);

	/**
	 * Creates a new instance of SQLReporter
	 */
	public SQLReporter()
	{
	}

	public boolean generateReport( HttpServletRequest request)
	{
		String templateId = request.getParameter("templateId");
		String providerNo = (String) request.getSession().getAttribute("user");

		String rsHtml = "An SQL query error has occurred";
		String csv = "";
		String preparedSql = "";
		String rawSql;
		ReportObject curReport = null;

		try
		{
			ReportTemplates template = rptTemplatesDao.find(Integer.parseInt(templateId));
			curReport = reportByTemplateService.getAsLegacyReport(Integer.parseInt(templateId), true);

			//TODO re-design or fully remove this new functionality??
//			if(curReport.isSequence()) {
//				return generateSequencedReport(request);
//			}

			rawSql = template.getTemplateSql();
			PreparedSQLTemplate preparedSQLTemplate = reportByTemplateService.getPreparedSQLTemplate(rawSql, request.getParameterMap());
			preparedSql = preparedSQLTemplate.getPreparedSQL();
			Map<Integer, String[]> indexedParamMap = preparedSQLTemplate.getParameterMap();
			logger.info(preparedSQLTemplate.toString());

			if(preparedSql == null || preparedSql.trim().isEmpty())
			{
				request.setAttribute("errormsg", "Error: Cannot find all parameters for the query. Check the template.");
				request.setAttribute("templateid", templateId);
				return false;
			}
			// admin verified reports bypass the maximum row limitations
			if(!curReport.isSuperAdminVerified())
			{
				List<Explain> explainResultList = rptTemplatesDao.getIndexPreparedExplainResultList(preparedSql, indexedParamMap);
				if(!reportByTemplateService.allowQueryRun(explainResultList, maxRows))
				{
					logger.info("User Template Query: " + preparedSql);
					request.setAttribute("errormsg", "Error: The report examines more than the maximum " + maxRows + " rows");
					request.setAttribute("explainResults", explainResultList);
					request.setAttribute("templateid", templateId);
					return false;
				}
			}

			LogReportByTemplate logEntry = saveInitialLog(Integer.parseInt(templateId), Integer.parseInt(providerNo), rawSql);

			//TODO use the entityManager sql equivalent. can't get the column headers until spring upgrade to 2.0 or higher
			ResultSet rs = DBHandler.GetSQL(preparedSql, indexedParamMap);
			rsHtml = RptResultStruct.getStructure2(rs);  //makes html from the result set
			StringWriter swr = new StringWriter();
			CSVPrinter csvp = new CSVPrinter(swr);
			csvp.writeln(UtilMisc.getArrayFromResultSet(rs));
			csv = swr.toString();

			updateLog(logEntry);
		}
		// since users can write custom queries this error is expected and should not generate an error in the log
		catch(ReportByTemplateException e)
		{
			logger.warn("An Exception occurred while generating a report by template (from user defined query).");
		}
		catch(SQLException e)
		{
			logger.warn("An SQL Exception occurred while generating a report by template (from user defined query).");
		}
		catch(Exception sqe)
		{
			logger.error("Error", sqe);
		}

		request.getSession().setAttribute("csv", csv);
		request.setAttribute("csv", csv);
		request.setAttribute("sql", preparedSql);
		request.setAttribute("reportobject", curReport);
		request.setAttribute("resultsethtml", rsHtml);

		return true;
	}

	private LogReportByTemplate saveInitialLog(Integer templateId, Integer providerNo, String querySql)
	{
		LogReportByTemplate logReportByTemplate = new LogReportByTemplate();
		try
		{
			logReportByTemplate.setTemplateId(templateId);
			logReportByTemplate.setProviderNo(providerNo);
			logReportByTemplate.setQueryString(querySql);
			logReportByTemplate.setDatetimeStart(new Date());
			logReportByTemplateDao.persist(logReportByTemplate);
		}
		catch(Exception e)
		{
			logger.error("Failed to persist initial ReportByTemplate Log entry.", e);
		}

		return logReportByTemplate;
	}
	private void updateLog(LogReportByTemplate logReportByTemplate)
	{
		try
		{
			logReportByTemplate.setDatetimeEnd(new Date());
			logReportByTemplateDao.merge(logReportByTemplate);
		}
		catch(Exception e)
		{
			logger.error("Failed to update ReportByTemplate Log entry.", e);
		}
	}
    
}
