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
import org.oscarehr.log.dao.LogReportByTemplateExplainDao;
import org.oscarehr.log.model.LogReportByTemplate;
import org.oscarehr.metrics.prometheus.service.SystemMetricsService;
import org.oscarehr.report.SQLReportHelper;
import org.oscarehr.report.reportByTemplate.dao.ReportTemplatesDao;
import org.oscarehr.report.reportByTemplate.exception.ReportByTemplateException;
import org.oscarehr.report.reportByTemplate.service.ReportByTemplateService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarDB.DBHandler;
import oscar.oscarReport.data.RptResultStruct;
import oscar.util.UtilMisc;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;


/**
 *
 * @author rjonasz
 */
public class SQLReporter implements Reporter
{

	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties properties = OscarProperties.getInstance();
	private static final Long maxRows = Long.parseLong(properties.getProperty("report_by_template.max_rows"));
	private static final Integer maxResults = Integer.parseInt(properties.getProperty("report_by_template.max_results"));
	private static final Boolean enableQueryRestrictions = properties.isPropertyActive("report_by_template.enable_restrictions");
	private static final Boolean enforceQueryRestrictions = properties.isPropertyActive("report_by_template.enforce_restrictions");

	private static ReportByTemplateService reportByTemplateService = SpringUtils.getBean(ReportByTemplateService.class);
	private static LogReportByTemplateDao logReportByTemplateDao = SpringUtils.getBean(LogReportByTemplateDao.class);
	private static LogReportByTemplateExplainDao logReportByTemplateExplainDao = SpringUtils.getBean(LogReportByTemplateExplainDao.class);
	private static ReportTemplatesDao rptTemplatesDao = SpringUtils.getBean(ReportTemplatesDao.class);
	private static SystemMetricsService systemMetricsService = SpringUtils.getBean(SystemMetricsService.class);

	/**
	 * Creates a new instance of SQLReporter
	 */
	public SQLReporter()
	{
	}

	public boolean generateReport( HttpServletRequest request)
	{
		String templateIdStr = request.getParameter("templateId");
		String providerNo = (String) request.getSession().getAttribute("user");

		String rsHtml = "An exception has occurred. If this problem persists please contact support for assistance.";
		String csv = "";
		String nativeSQL = "";
		ReportObject curReport = null;

		try
		{
			systemMetricsService.incrementCurrentRunningRbtCount();

			Instant startTime = Instant.now();
			Integer templateId = Integer.parseInt(templateIdStr);
			curReport = reportByTemplateService.getAsLegacyReport(templateId, true);
			nativeSQL = reportByTemplateService.getTemplateSQL(templateId, request.getParameterMap());
			logger.info("SQL: " + nativeSQL);

			//TODO-legacy re-design or fully remove this new functionality??
//			if(curReport.isSequence()) {
//				return generateSequencedReport(request);
//			}

			if(nativeSQL == null || nativeSQL.trim().isEmpty())
			{
				request.setAttribute("errormsg", "Error: Cannot find all parameters for the query. Check the template.");
				request.setAttribute("templateid", templateIdStr);
				return false;
			}

			LogReportByTemplate logEntry = saveInitialLog(templateId, providerNo, nativeSQL);
			if(enableQueryRestrictions)
			{
				//check for special exempt cases
				String explainExemptSql = SQLReportHelper.getExplainSkippableQuery(nativeSQL);
				if(explainExemptSql != null)
				{
					nativeSQL = explainExemptSql;
				}
				else
				{
					List<Explain> explainResultList = rptTemplatesDao.getExplainResultList(nativeSQL);
					saveExplainResults(logEntry, explainResultList);

					// admin verified reports bypass the maximum row limitations
					if(enforceQueryRestrictions && !curReport.isSuperAdminVerified())
					{
						nativeSQL = SQLReportHelper.applyEnforcedLimit(nativeSQL, maxResults);
						boolean allowRun = SQLReportHelper.allowQueryRun(explainResultList, maxRows);

						if(!allowRun)
						{
							request.setAttribute("errormsg", "Error: The report examines more than the maximum " + maxRows + " rows");
							request.setAttribute("explainResults", explainResultList);
							request.setAttribute("templateid", templateIdStr);
							return false;
						}
					}
				}
			}

			//TODO use the entityManager sql equivalent. can't get the column headers until spring upgrade to 2.0 or higher
			ResultSet rs = DBHandler.GetSQL(nativeSQL);
			rsHtml = RptResultStruct.getStructure2(rs);  //makes html from the result set
			StringWriter swr = new StringWriter();
			CSVPrinter csvp = new CSVPrinter(swr);
			csvp.writeln(UtilMisc.getArrayFromResultSet(rs));
			csv = swr.toString();

			rs.last();
			long rowCount = new Integer(rs.getRow()).longValue();
			updateLog(logEntry, rowCount);

			systemMetricsService.recordRbtRequestLatency(Duration.between(startTime, Instant.now()).toMillis());
		}
		// since users can write custom queries this error is expected and should not generate an error in the log
		catch(ReportByTemplateException | SQLException | PersistenceException e)
		{
			logger.warn("An Exception occurred while generating a report by template (from user defined query): " + e.getMessage());
			rsHtml = "An SQL query error has occurred<br>" + e.getMessage();
		}
		catch(Exception sqe)
		{
			logger.error("Error", sqe);
			rsHtml += "<br>" + sqe.getMessage();
		}
		finally
		{
			systemMetricsService.decrementCurrentRunningRbtCount();
		}

		request.getSession().setAttribute("csv", csv);
		request.setAttribute("csv", csv);
		request.setAttribute("sql", nativeSQL);
		request.setAttribute("reportobject", curReport);
		request.setAttribute("resultsethtml", rsHtml);

		return true;
	}

	private void saveExplainResults(LogReportByTemplate logEntry, List<Explain> explainResultList)
	{
		try
		{
			logReportByTemplateExplainDao.persistAll(logEntry, explainResultList);
		}
		catch(PersistenceException e)
		{
			logger.error("Failed to persist ReportByExample explain results.", e);
		}

	}
	private LogReportByTemplate saveInitialLog(Integer templateId, String providerNo, String querySql)
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
		catch(PersistenceException e)
		{
			logger.error("Failed to persist initial ReportByTemplate Log entry.", e);
		}

		return logReportByTemplate;
	}
	private void updateLog(LogReportByTemplate logReportByTemplate, Long rowCount)
	{
		try
		{
			logReportByTemplate.setDatetimeEnd(new Date());
			logReportByTemplate.setRowsReturned(rowCount);
			logReportByTemplateDao.merge(logReportByTemplate);
		}
		catch(PersistenceException e)
		{
			logger.error("Failed to update ReportByTemplate Log entry.", e);
		}
	}
    
}
