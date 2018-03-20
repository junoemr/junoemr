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
import org.oscarehr.common.dao.LogReportByTemplateDao;
import org.oscarehr.common.model.LogReportByTemplate;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarDB.DBHandler;
import oscar.oscarReport.data.RptResultStruct;
import oscar.util.UtilMisc;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;


/**
 *
 * @author rjonasz
 */
public class SQLReporter implements Reporter {

	private LogReportByTemplateDao logReportByTemplateDao = SpringUtils.getBean(LogReportByTemplateDao.class);

    /** Creates a new instance of SQLReporter */
    public SQLReporter() {
    }
    
    public boolean generateReport( HttpServletRequest request) {
        String templateId = request.getParameter("templateId");
	    String providerNo = (String) request.getSession().getAttribute("user");

        ReportObject curReport = (new ReportManager()).getReportTemplateNoParam(templateId);
        Map parameterMap = request.getParameterMap();
        String sql = curReport.getPreparedSQL(parameterMap);
        if (sql == "" || sql == null) {
            request.setAttribute("errormsg", "Error: Cannot find all parameters for the query.  Check the template.");
            request.setAttribute("templateid", templateId);
            return false;
        }
        ResultSet rs = null;
        String rsHtml = "An SQL querry error has occured";
        String csv = "";
	    try
	    {
		    LogReportByTemplate logEntry = saveInitialLog(Integer.parseInt(templateId), Integer.parseInt(providerNo), sql);

	        rs = DBHandler.GetSQL(sql);
            rsHtml = RptResultStruct.getStructure2(rs);  //makes html from the result set
            StringWriter swr = new StringWriter();
            CSVPrinter csvp = new CSVPrinter(swr);
            csvp.writeln(UtilMisc.getArrayFromResultSet(rs));
            csv = swr.toString();
            //csv = csv.replace("\\", "\"");  //natural quotes in the data create '\' characters in CSV, xls works fine
                                              //this line fixes it but messes up XLS generation.
            //csv = UtilMisc.getCSV(rs);
		    logEntry.setDatetimeEnd(new Date());
		    logReportByTemplateDao.merge(logEntry);
        } 
        // since users can write custom queries this error is expected and should not generate an error in the log
        catch (SQLException sqe) {
        	MiscUtils.getLogger().warn("An SQL Exception occured while generating a report by template (from user defined query).");
        }
        catch (Exception sqe) {
            MiscUtils.getLogger().error("Error", sqe);
        }
        request.getSession().setAttribute("csv", csv);
        request.setAttribute("csv", csv);
        request.setAttribute("sql", sql);
        request.setAttribute("reportobject", curReport);
        request.setAttribute("resultsethtml", rsHtml);
        
        return true;
    }

    private LogReportByTemplate saveInitialLog(Integer templateId, Integer providerNo, String querySql)
    {
	    LogReportByTemplate logReportByTemplate = new LogReportByTemplate();
	    logReportByTemplate.setTemplateId(templateId);
	    logReportByTemplate.setProviderNo(providerNo);
	    logReportByTemplate.setQueryString(querySql);
	    logReportByTemplate.setDatetimeStart(new Date());
	    logReportByTemplateDao.persist(logReportByTemplate);

	    return logReportByTemplate;
    }
}
