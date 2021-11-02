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
//This action generates the report after the user filled in all the params



package oscar.oscarReport.reportByTemplate.actions;

import com.Ostermiller.util.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarReport.reportByTemplate.ReportFactory;
import oscar.oscarReport.reportByTemplate.Reporter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created on December 21, 2006, 10:47 AM
 * @author apavel (Paul)
 */
public class GenerateReportAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
    {

	    String sessionProviderNo = (String) request.getSession().getAttribute("user");
	    securityInfoManager.requireOnePrivilege(sessionProviderNo, SecurityInfoManager.READ, null, "_admin", "_report", "_admin.reporting");

	    String getCsv = StringUtils.trimToNull(request.getParameter("getCSV"));
	    String getXls = StringUtils.trimToNull(request.getParameter("getXLS"));
		boolean asCsv = StringUtils.isNotBlank(getCsv);

	    try
	    {
		    Reporter reporter = ReportFactory.getReporter(request.getParameter("type"));
			boolean prepareForFile = !StringUtils.isAllBlank(getCsv, getXls);

		    if(reporter.generateReport(request, prepareForFile))
		    {
				if(prepareForFile)
				{
					Integer sequenceLength = (Integer) request.getAttribute("sequenceLength");
					if(sequenceLength == null)
					{
						String csv = (String) request.getSession().getAttribute("csv");
						if(asCsv)
						{
							generateCsvFile(csv, response);
						}
						else
						{
							generateXlsFile(csv, response);
						}
					}
					else
					{
						if(asCsv)
						{
							generateCsvZip(request, response, sequenceLength);
						}
						else
						{
							generateXlsZip(request, response, sequenceLength);
						}
					}
					return null;
				}
				else
				{
					return mapping.findForward("success");
				}
		    }
	    }
	    catch(IOException io)
	    {
		    logger.error("Error Generating " + (asCsv ? "CSV" : "XLS") + " Zip File: ", io);
	    }
	    catch(Exception e)
	    {
		    logger.error("Error", e);
	    }
		return mapping.findForward("fail");
    }

	private void generateCsvFile(String csv, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"oscarReport.csv\"");
		response.getWriter().write(csv);
	}

	private void generateXlsFile(String csv, HttpServletResponse response) throws IOException
	{
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"oscarReport.xls\"");
		HSSFWorkbook xlsFile = generateXlsWorkbook(csv);
		xlsFile.write(response.getOutputStream());
	}

	private HSSFWorkbook generateXlsWorkbook(String csv)
	{
		String[][] data = CSVParser.parse(csv);
		HSSFWorkbook xlsWorkbook = new HSSFWorkbook();
		HSSFSheet sheet = xlsWorkbook.createSheet("OSCAR_Report");
		for(int x = 0; x < data.length; x++)
		{
			HSSFRow row = sheet.createRow(x);
			for(int y = 0; y < data[x].length; y++)
			{
				try
				{
					double d = Double.parseDouble(data[x][y]);
					row.createCell((short) y).setCellValue(d);
				}
				catch(Exception e)
				{
					row.createCell((short) y).setCellValue(data[x][y]);
				}
			}
		}

		return xlsWorkbook;
	}

	private void generateCsvZip(HttpServletRequest request, HttpServletResponse response, Integer sequenceLength) throws IOException
	{
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"oscarReports.zip\"");

		String csv;
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

		for(int i = 0; i < sequenceLength; i++)
		{
			csv = (String) request.getAttribute("csv-" + i);

			ZipEntry csvFile = new ZipEntry("oscarReport-" + i + ".csv");
			zos.putNextEntry(csvFile);
			zos.write(csv.getBytes());
			zos.closeEntry();
		}

		zos.close();
	}

	private void generateXlsZip(HttpServletRequest request, HttpServletResponse response, Integer sequenceLength) throws IOException
	{
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=\"oscarReports.zip\"");

		String csv;
		ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

		for(int i = 0; i < sequenceLength; i++)
		{
			csv = (String) request.getAttribute("csv-" + i);

			HSSFWorkbook xls = generateXlsWorkbook(csv);

			ZipEntry xlsFile = new ZipEntry("oscarReport-" + i + ".xls");
			zos.putNextEntry(xlsFile);
			zos.write(xls.getBytes());
			zos.closeEntry();
		}

		zos.close();
	}
}
