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


package oscar.oscarReport.data;

import org.oscarehr.metrics.prometheus.service.SystemMetricsService;
import org.oscarehr.util.SpringUtils;
import oscar.oscarDB.DBPreparedHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;


/**
 * This classes main function FluReportGenerate collects a group of patients with flu in the last specified date
 */
public class RptByExampleData
{

	public ArrayList demoList = null;
	public String sql = "";
	public ResultSet resultSet = null;
	public String results = null;
	public String connect = null;
	private DBPreparedHandler accessDB = null;
	private Properties oscarVariables = null;
	private SystemMetricsService systemMetricsService = SpringUtils.getBean(SystemMetricsService.class);

	public RptByExampleData()
	{
	}

	public String exampleTextGenerate(String sql, Properties oscarVariables) throws SQLException
	{
		return exampleReportGenerate(sql, oscarVariables);
	}

	public String exampleReportGenerate(String sql, Properties oscarVariables) throws SQLException
	{
		this.sql = prepareUserQuery(sql);
		this.oscarVariables = oscarVariables;

		accessDB = new DBPreparedHandler();

		Instant startTime = Instant.now();
		try
		{
			this.systemMetricsService.incrementCurrentRunningRbeCount();
			this.resultSet = accessDB.queryResults(this.sql);
		}
		finally
		{
			this.systemMetricsService.decrementCurrentRunningRbeCount();
			this.systemMetricsService.recordRbeRequestLatency(Duration.between(startTime, Instant.now()).toMillis());
		}

		if(resultSet != null)
		{
			results = RptResultStruct.getStructure(resultSet);
		}
		else
		{
			results = "";
		}
		return results;
	}

	public static String prepareUserQuery(String userQuery)
	{
		if(userQuery == null || userQuery.trim().isEmpty())
		{
			return null;
		}
		userQuery = replaceSQLString(";", "", userQuery);
		userQuery = replaceSQLString("\"", "\'", userQuery);

		return userQuery;
	}

	private static String replaceSQLString(String oldString, String newString, String inputString)
	{

		String outputString = "";
		int i;
		for(i = 0; i < inputString.length(); i++)
		{
			if(!(inputString.regionMatches(true, i, oldString, 0, oldString.length())))
			{
				outputString += inputString.charAt(i);
			}
			else
			{
				outputString += newString;
				i += oldString.length() - 1;
			}
		}
		return outputString;
	}
};
