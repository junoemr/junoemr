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


package oscar.login;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Component;

/**
 * This ContextListener is used to Initialize classes at startup - Initialize the DBConnection Pool.
 * 
 * @author Jay Gallagher
 */
@Component
public class Startup implements ServletContextListener
{
	private static Logger logger = MiscUtils.getLogger();
	private oscar.OscarProperties p = oscar.OscarProperties.getInstance();

	public void contextInitialized(ServletContextEvent sc)
	{
		try
		{
			logger.debug("contextInit");

			String contextPath = "";

			// TODO: SPRINGUPGRADE: make this better, perhaps read from the properties file?
			try
			{
				// Anyone know a better way to do this?
				String url = sc.getServletContext().getResource("/").getPath();
				logger.debug(url);
				int idx = url.lastIndexOf('/');
				url = url.substring(0, idx);

				idx = url.lastIndexOf('/');
				url = url.substring(idx + 1);

				idx = url.lastIndexOf('.');
				if (idx > 0) url = url.substring(0, idx);

				contextPath = url;
			}
			catch (Exception e)
			{
				logger.error("Error", e);
			}

			try {
				// Specify who will see new casemanagement screen
				ArrayList<String> listUsers;
				String casemgmtscreen = p.getProperty("CASEMANAGEMENT");
				if (casemgmtscreen != null) {
					String[] arrUsers = casemgmtscreen.split(",");
					listUsers = new ArrayList<String>(Arrays.asList(arrUsers));
					Collections.sort(listUsers);
				} else listUsers = new ArrayList<String>();

				sc.getServletContext().setAttribute("CaseMgmtUsers", listUsers);

				// Temporary Testing of new ECHART
				// To be removed
				String newDocs = p.getProperty("DOCS_NEW_ECHART");

				if (newDocs != null) {
					String[] arrnewDocs = newDocs.split(",");
					ArrayList<String> newDocArr = new ArrayList<String>(Arrays.asList(arrnewDocs));
					Collections.sort(newDocArr);
					sc.getServletContext().setAttribute("newDocArr", newDocArr);
				}

				String echartSwitch = p.getProperty("USE_NEW_ECHART");
				if (echartSwitch != null && echartSwitch.equalsIgnoreCase("yes")) {
					sc.getServletContext().setAttribute("useNewEchart", true);
				}

				logger.info("BILLING REGION : " + p.getBillingType());
				logger.info("DB PROPS: Username :" + p.getProperty("db_username", "NOTSET") + " db name: " + p.getProperty("db_name", "NOTSET"));
				p.setProperty("OSCAR_START_TIME", "" + System.currentTimeMillis());

			}
			catch (Exception e)
			{
				String s="Error initializing properties";
				logger.error(s, e);
			}

			// CHECK FOR DEFAULT PROPERTIES
			String baseDocumentDir = p.getProperty("BASE_DOCUMENT_DIR");
			if (baseDocumentDir != null)
			{
				logger.info("Found Base Document Dir: " + baseDocumentDir);
				checkAndSetProperty(baseDocumentDir, contextPath, "HOME_DIR", "/billing/download/");
				checkAndSetProperty(baseDocumentDir, contextPath, "DOCUMENT_DIR", "/document/");
				checkAndSetProperty(baseDocumentDir, contextPath, "eform_image", "/eform/images/");

				checkAndSetProperty(baseDocumentDir, contextPath, "oscarMeasurement_css_upload_path", "/oscarEncounter/oscarMeasurements/styles/");
				checkAndSetProperty(baseDocumentDir, contextPath, "TMP_DIR", "/export/");
				checkAndSetProperty(baseDocumentDir, contextPath, "form_record_path", "/form/records/");
				
				//HRM Directories
				checkAndSetProperty(baseDocumentDir, contextPath,"OMD_hrm","/hrm/");
				checkAndSetProperty(baseDocumentDir, contextPath,"OMD_directory" , "/hrm/OMD/");
				checkAndSetProperty(baseDocumentDir, contextPath,"OMD_log_directory" , "/hrm/logs/");
				checkAndSetProperty(baseDocumentDir, contextPath,"OMD_stored", "/hrm/stored/");
				checkAndSetProperty(baseDocumentDir, contextPath,"OMD_downloads","/hrm/sftp_downloads/");
			}
			
			logger.debug("LAST LINE IN contextInitialized");
		} catch (Exception e) {
			logger.error("Unexpected error.", e);
			throw (new RuntimeException(e));
		}
	}

	// Checks for default property with name propName. If the property does not exist,
	// the property is set with value equal to the base directory, plus /, plus the webapp context
	// path and any further extensions. If the formed directory does not exist in the system,
	// it is created.
	private void checkAndSetProperty(String baseDir, String context, String propName, String endDir) {
		String propertyDir = p.getProperty(propName);
		if (propertyDir == null) {
			propertyDir = baseDir + "/" + context + endDir;
			logger.debug("Setting property " + propName + " with value " + propertyDir);
			p.setProperty(propName, propertyDir);
			// Create directory if it does not exist
			if (!(new File(propertyDir)).exists()) {
				logger.warn("Directory does not exist:  " + propertyDir + ". Creating.");
				boolean success = (new File(propertyDir)).mkdirs();
				if (!success) logger.error("An error occured when creating " + propertyDir);
			}
		}
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do right now
	}

}
