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
//This action generates the CSV and XLS files on request



package oscar.oscarReport.reportByTemplate.actions;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.Ostermiller.util.CSVParser;

/**
 * Created on December 21, 2006, 10:47 AM
 * @author apavel (Paul)
 * 
 * @Deprecated 2017 - This can be done on the front end
 */
@Deprecated
public class GenerateOutFilesAction extends Action {
	
	private static Logger logger = Logger.getLogger(GenerateOutFilesAction.class);
	
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
    	
    	logger.info("GENERATE SPREADSHEET FOR REPORT BY TEMPLATE");

        String csv = (String) request.getSession().getAttribute("csv");
        if (csv ==null){
            csv = request.getParameter("csv");
        }
        String action = request.getParameter("getCSV");
        if (action != null) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"oscarReport.csv\"");
            try {
                response.getWriter().write(csv);
            } catch (Exception ioe) {
            	logger.error("Error writing csv file", ioe);
            }
            return null;
        }
        action = request.getParameter("getXLS");
        if (action != null) {
        	logger.debug("Generating Spread Sheet file for the 'report by template' module ..");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"oscarReport.xls\"");
            String[][] data = CSVParser.parse(csv);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("OSCAR_Report");
            for (int x=0; x<data.length; x++) {
                HSSFRow row = sheet.createRow((short)x);
                for (int y=0; y<data[x].length; y++) {
                    try{
                       double d = Double.parseDouble(data[x][y]);
                        row.createCell((short)y).setCellValue(d);
                    }catch(Exception e){
                       row.createCell((short)y).setCellValue(data[x][y]);
                    }
                }
            }
            try {    
                wb.write(response.getOutputStream());
            } catch(Exception e) {
            	logger.error("Error writing xls file", e);
            }
            return null;
        }
        logger.error("FAILED");
        //return mapping.findForward("success");
        return null;
    }
    
}
