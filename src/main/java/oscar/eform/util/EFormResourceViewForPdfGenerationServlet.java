/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * 
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.util;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;

import oscar.oscarEncounter.pageUtil.EctSessionBean;

/**
 * The purpose of this servlet is to allow a local process to access eform images.
 */
public final class EFormResourceViewForPdfGenerationServlet extends HttpServlet {

	private static final Logger logger=MiscUtils.getLogger();
	
	@Override
	public final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		// ensure it's a local machine request... no one else should be calling this servlet.
		String remoteAddress=request.getRemoteAddr();
		logger.debug("EformPdfServlet request from : "+remoteAddress);
		/*
		if (!"127.0.0.1".equals(remoteAddress))
		{
			logger.warn("Unauthorised request made to EFormImageViewForPdfGenerationServlet from address : "+remoteAddress);
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
		*/
		
		String fileName = request.getParameter("filename");
		String measurementType = request.getParameter("measurementType");
		
		/*
		response.setHeader("Content-disposition","inline; filename=" + fileName);
		String javascript_dir = "share/javascript";

        File file = null;
        try{
           File directory = new File(javascript_dir);
           if(!directory.exists()){
              throw new Exception("Directory:  "+javascript_dir+ " does not exist");
           }
           file = new File(directory,fileName);
           //String canonicalPath = file.getParentFile().getCanonicalPath(); //absolute path of the retrieved file

           if (!directory.equals(file.getParentFile())) {
               MiscUtils.getLogger().debug("SECURITY WARNING: Illegal file path detected, client attempted to navigate away from the file directory");
               throw new Exception("Could not open file " + fileName + ".  Check the file path");
           }
        }catch(Exception e){
            MiscUtils.getLogger().error("Error", e);
            throw new Exception("Could not open file "+javascript_dir+fileName +" does "+javascript_dir+ " exist ?",e);
        }
        
        String contentType = "text/javascript";
        */
		
		request.setAttribute("prepareForFax", true);
		
		String url = "/share/"+fileName;
		
		// Check if we're getting measurements
		if(measurementType != null && measurementType.length() > 0 
				&& fileName == null){
			String demographic_no = request.getParameter("demographic_no");

			url = "/oscarEncounter/oscarMeasurements/" + 
					"SetupDisplayHistory.do?type=" +
					measurementType;
			
			// Some hacks to make measurements work
			EctSessionBean bean = new EctSessionBean();
			bean.demographicNo = demographic_no;

			request.getSession().setAttribute("EctSessionBean",bean);   
			
			LoggedInInfo x=new LoggedInInfo();
			Facility faci = new Facility();
			faci.setIntegratorEnabled(false);
			HttpSession session = request.getSession();
			session.setAttribute("userrole", "doctor,admin,doctor,admin");
			session.setAttribute("user", "999900");
			x.currentFacility=faci;
			x.loggedInProvider=new Provider("999900");
			x.initiatingCode=request.getRequestURI();
			x.session = session;
			LoggedInInfo.loggedInInfo.set(x);
			
			
			
			//CaisiIntegratorManager.setIntegratorOffline(true);
		}
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(url);
		logger.debug("Request URL:" +url);
		requestDispatcher.forward(request, response);
	}
}
