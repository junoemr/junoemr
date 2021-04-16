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
package org.oscarehr.managers;
import java.util.List;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.DashboardDao;
import org.oscarehr.common.dao.IndicatorTemplateDao;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Dashboard;
import org.oscarehr.common.model.IndicatorTemplate;
import org.oscarehr.dashboard.display.beans.DashboardBean;
import org.oscarehr.dashboard.display.beans.DrilldownBean;
import org.oscarehr.dashboard.display.beans.IndicatorBean;
import org.oscarehr.dashboard.factory.DashboardBeanFactory;
import org.oscarehr.dashboard.factory.DrilldownBeanFactory;
import org.oscarehr.dashboard.factory.IndicatorBeanFactory;
import org.oscarehr.dashboard.handler.ExportQueryHandler;
import org.oscarehr.dashboard.handler.IndicatorTemplateHandler;
import org.oscarehr.dashboard.handler.IndicatorTemplateXML;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

@Service
public class DashboardManager {

	public static final boolean MULTI_THREAD_ON = Boolean.TRUE;
	private static final Logger logger = MiscUtils.getLogger();
	
	public static enum ObjectName { IndicatorTemplate, Dashboard }
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private IndicatorTemplateDao indicatorTemplateDao;
	@Autowired
	private DashboardDao dashboardDao;
	
	/**
	 * Toggles the active status of a given class name.
	 * Options are: 
	 * - IndicatorTemplate
	 * - Dashboard
	 */
	public void toggleStatus( LoggedInInfo loggedInInfo, int objectId, ObjectName objectClassName, Boolean state ) {		
		switch( objectClassName ) {
			case IndicatorTemplate: toggleIndicatorActive( loggedInInfo, objectId, state );
			break;
			case Dashboard: toggleDashboardActive( loggedInInfo, objectId, state );
			break;
		}		
	}
	
	
	/**
	 * Retrieves all the information for each Indicator Template query
	 * that is stored in the indicatorTemplate db table.
	 */
	public List<IndicatorTemplate> getIndicatorLibrary( LoggedInInfo loggedInInfo ) {
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return null;
		}
		
		List<IndicatorTemplate> indicatorTemplates = indicatorTemplateDao.getIndicatorTemplates();
		
		return indicatorTemplates;
	}
	
	/**
	 * Toggles the Indicator active boolean switch.  True for active, false for not active.
	 */
	public void toggleIndicatorActive( LoggedInInfo loggedInInfo, int indicatorId, Boolean state ) {
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return;
		}
		
		IndicatorTemplate indicator = indicatorTemplateDao.find(indicatorId);
		
		if( indicator != null ) {
			indicator.setActive(state);
			indicatorTemplateDao.merge(indicator);
		}
	}
	
	/**
	 * Returns ALL available Dashboards. 
	 * 
	 */
	public List<Dashboard> getDashboards( LoggedInInfo loggedInInfo ) {
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return null;
		}
		
		List<Dashboard> dashboards = dashboardDao.getDashboards();
		
		return dashboards;
	}
	
	/**
	 * Returns Dashboards that are active.
	 */
	public List<Dashboard> getActiveDashboards( LoggedInInfo loggedInInfo ) {
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return null;
		}
		
		List<Dashboard> dashboards = dashboardDao.getActiveDashboards();
		
		return dashboards;
	}

	
	/**
	 * Add a new Dashboard entry or edit an old one.
	 */
	public boolean addDashboard( LoggedInInfo loggedInInfo, Dashboard dashboard ) {
		
		boolean success = Boolean.FALSE;
		
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return success;
		}
		
		if( dashboard.getId() == null ) {
			// all new Dashboards are active.
			dashboard.setActive(Boolean.TRUE);
			dashboardDao.persist( dashboard );
		} else {
			dashboardDao.merge( dashboard );
		}
		
		if( dashboard.getId() > 0 ) {
			success = Boolean.TRUE;
		}
		
		return success;
	}
	
	/**
	 * Toggles the Dashboard active boolean switch.  True for active, false for not active.
	 */
	public void toggleDashboardActive( LoggedInInfo loggedInInfo, int dashboardId, Boolean state ) {
		
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return;
		}
		
		Dashboard dashboard = dashboardDao.find(dashboardId);
		
		if( dashboard != null ) {
			dashboard.setActive(state);
			dashboardDao.merge(dashboard);
		}
	}
	
	
	/**
	 * Retrieves an XML file from a servlet request object and then saves it to
	 * the local file directory and finally writes an entry in the Indicator Template db table.
	 * 
	 * Returns a JSON string: status=success, or status=error, message=[message]
	 * 
	 */
	public String importIndicatorTemplate( LoggedInInfo loggedInInfo, byte[] bytearray ) {
		JSONObject message = new JSONObject();
		IndicatorTemplate indicatorTemplate = null;

		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.CREATE)) {
			message.put("status", "error");
			message.put("message", "User missing _dashboardManager role with write access");
			return message.toString();
		}

		if( bytearray != null && bytearray.length > 0) {
			
			MiscUtils.getLogger().debug("Indicator XML Template: " + new String( bytearray ) );
			
			IndicatorTemplateHandler templateHandler = new IndicatorTemplateHandler();
			templateHandler.read( bytearray );
			
			//TODO-legacy: need to validate the SQL
			
			// check Indicator query
			
			// check Drilldown query

			if( templateHandler.isValidXML() ) {
				indicatorTemplate = templateHandler.getIndicatorTemplateEntity();
			} else {
				message.put("status", "error");
				message.put("message", templateHandler.getValidationMessage() );
			}
		}
		
		if( indicatorTemplate != null ) {
			this.indicatorTemplateDao.persist( indicatorTemplate );
			if( indicatorTemplate.getId() > 0) {
				message.put("status", "success");
				message.put("message", "Template imported successfully");
			} else {
				message.put("status", "error");
				message.put("message", "Failed to persist the Indicator Template" );
			}
		}

		return message.toString();
	}
	
	/**
	 * Overload method with a indicatorId list parameter.
	 */
	public boolean assignIndicatorToDashboard(LoggedInInfo loggedInInfo, int dashboardId, List<Integer> indicatorId ) {
		boolean success = Boolean.FALSE;
		
		for(Integer id : indicatorId) {
			success = assignIndicatorToDashboard(loggedInInfo, dashboardId, id );
			if( ! success ) {
				break;
			}
		}
		
		return success;
	}
	
	/**
	 * Assign an Indicator the Dashboard where the Indicator will be displayed. 
	 */
	public boolean assignIndicatorToDashboard(LoggedInInfo loggedInInfo, int dashboardId, int indicatorId ) {
		boolean success = Boolean.FALSE;
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.CREATE)) {
			return success;
		}
		
		IndicatorTemplate indicatorTemplate = null;
		
		if( indicatorId > 0 ) {
			indicatorTemplate = indicatorTemplateDao.find( indicatorId );
		}
		
		if( indicatorTemplate != null ) {
			
			if( dashboardId > 0 ) {			
				indicatorTemplate.setDashboardId( dashboardId );				
			} else {				
				indicatorTemplate.setDashboardId(null);
			}

			indicatorTemplateDao.merge(indicatorTemplate);
			
			if( indicatorTemplate.getId() > 0 ) {	
				success = Boolean.TRUE;
			}
		}

		return success;
	}

	
	/**
	 * Returns the raw indicator template XML for download and editing.
	 */
	public String exportIndicatorTemplate( LoggedInInfo loggedInInfo, int indicatorId ) {
		
		String template = null;
		
		if(!securityCheck(loggedInInfo, "_dashboardManager", SecurityInfoManager.READ)) {
			return template;
		}
		
		
		IndicatorTemplate indicatorTemplate = indicatorTemplateDao.find(indicatorId);
		
		if( indicatorTemplate != null ) {			
			template = indicatorTemplate.getTemplate();
		}
		
		return template;
	}
	
	/**
	 * Returns a List of ACTIVE Indicator Templates based on the DashboardId 
	 */
	public List<IndicatorTemplate> getIndicatorTemplatesByDashboardId( LoggedInInfo loggedInInfo, int dashboardId ) {
		List<IndicatorTemplate> indicatorTemplates = null; 

		if(!securityCheck(loggedInInfo, "_dashboardDisplay", SecurityInfoManager.READ)) {
			return indicatorTemplates;
		}
		
		if( dashboardId > 0 ) {
			indicatorTemplates = indicatorTemplateDao.getIndicatorTemplatesByDashboardId( dashboardId );
		}
		
		return indicatorTemplates;
	}
	
	/**
	 *  Get an entire Dashboard, with all of its Indicators in a List parameter.
	 */
	public DashboardBean getDashboard( LoggedInInfo loggedInInfo, int dashboardId) {
		
		DashboardBean dashboardBean = null;

		if(!securityCheck(loggedInInfo, "_dashboardDisplay", SecurityInfoManager.READ)) {
			return dashboardBean;
		}
		
		Dashboard dashboardEntity = null;
		DashboardBeanFactory dashboardBeanFactory = null;
		
		if( dashboardId > 0 ) {			
			dashboardEntity = dashboardDao.find( dashboardId );
			List<IndicatorTemplate> indicatorTemplates = getIndicatorTemplatesByDashboardId( loggedInInfo, dashboardId );
			dashboardEntity.setIndicators( indicatorTemplates );
		}
		
		if( dashboardEntity != null ) {
			// Add the indicators and panels.
			dashboardBeanFactory = new DashboardBeanFactory( loggedInInfo, dashboardEntity);
		}

		if( dashboardBeanFactory != null ) {
			dashboardBean = dashboardBeanFactory.getDashboardBean();
		}

		return dashboardBean;
	}
	
	/**
	 * Get an Indicator Template by Id.
	 */
	public IndicatorTemplate getIndicatorTemplate( LoggedInInfo loggedInInfo, int indicatorTemplateId ) {
		
		IndicatorTemplate indicatorTemplate = null; 
		
		if(!securityCheck(loggedInInfo, "_dashboardDrilldown", SecurityInfoManager.READ)) {
			return indicatorTemplate;
		}
		
		indicatorTemplate = indicatorTemplateDao.find( indicatorTemplateId );
		
		return indicatorTemplate;
	}
	
	/**
	 * Get the XML template that contains all the data and meta data for an Indicator display. 
	 */
	public IndicatorTemplateXML getIndicatorTemplateXML( LoggedInInfo loggedInInfo, int indicatorTemplateId, String providerNo) {
		
		IndicatorTemplateXML indicatorTemplateXML = null;
		
		if(!securityCheck(loggedInInfo, "_dashboardDrilldown", SecurityInfoManager.READ)) {
			return indicatorTemplateXML;
		}
		
		IndicatorTemplate indicatorTemplate = getIndicatorTemplate( loggedInInfo, indicatorTemplateId );
		IndicatorTemplateHandler templateHandler = new IndicatorTemplateHandler(indicatorTemplate.getTemplate().getBytes(), providerNo);
		indicatorTemplateXML = templateHandler.getIndicatorTemplateXML();
		
		return indicatorTemplateXML;
	}
	
	/**
	 * Create a DrilldownBean that contains the query results requested from a specific Indicator by ID.
	 */
	public DrilldownBean getDrilldownData( LoggedInInfo loggedInInfo, int indicatorTemplateId, String providerNo ) {

		DrilldownBean drilldownBean = null; 
		DrilldownBeanFactory drilldownBeanFactory = null;
		
		if(!securityCheck(loggedInInfo, "_dashboardDrilldown", SecurityInfoManager.READ)) {
			return drilldownBean;
		}
		
		IndicatorTemplate indicatorTemplate = getIndicatorTemplate( loggedInInfo, indicatorTemplateId );
		
		if( indicatorTemplate != null ) {
			drilldownBeanFactory = new DrilldownBeanFactory( loggedInInfo, indicatorTemplate, providerNo );
		}
		
		if( drilldownBeanFactory != null ) {
			drilldownBean = drilldownBeanFactory.getDrilldownBean();
		}
		
		return drilldownBean;

	}
	
	public GenericFile exportDrilldownQueryResultsToCSV(LoggedInInfo loggedInInfo, int indicatorId, String providerNo) {
		
		if(!securityCheck(loggedInInfo, "_dashboardDrilldown", SecurityInfoManager.READ)) {
			return null;
		}
		
		IndicatorTemplateXML templateXML = getIndicatorTemplateXML( loggedInInfo, indicatorId, providerNo);

		ExportQueryHandler exportQueryHandler = SpringUtils.getBean( ExportQueryHandler.class );
		exportQueryHandler.setLoggedInInfo( loggedInInfo );
		exportQueryHandler.setParameters( templateXML.getDrilldownParameters() );
		exportQueryHandler.setColumns( templateXML.getDrilldownExportColumns() );
		exportQueryHandler.setRanges( templateXML.getDrilldownRanges() );
		exportQueryHandler.setQuery( templateXML.getDrilldownQuery() );
		exportQueryHandler.execute();
		
		return exportQueryHandler.getCsvFile();
	}
	
	/**
	 * Get an Indicator Panel Bean with a fully executed query. 
	 */
	public IndicatorBean getIndicatorPanel( LoggedInInfo loggedInInfo, int indicatorId, String providerNo) {
		
		IndicatorBean indicatorBean = null;
		IndicatorBeanFactory indicatorBeanFactory = null;
		
		if(!securityCheck(loggedInInfo, "_dashboardDrilldown", SecurityInfoManager.READ)) {
			return indicatorBean;
		}
		
		IndicatorTemplateXML indicatorTemplateXML = getIndicatorTemplateXML( loggedInInfo, indicatorId, providerNo);
		
		// The id needs to be force set.
		if( indicatorTemplateXML != null ) {
			indicatorTemplateXML.setId( indicatorId );
			indicatorBeanFactory = new IndicatorBeanFactory( indicatorTemplateXML );
		}

		if( indicatorBeanFactory != null ) {
			indicatorBean = indicatorBeanFactory.getIndicatorBean();
		}
		return indicatorBean;
	}
	
	
	// TODO-legacy add additional error check / filter class to carry out the following methods.
	
	// TODO-legacy add check queries method.
	
	// TODO-legacy add duplicate Indicator Template upload check.
	
	// TODO-legacy add duplicate Dashboard name check.
	
	private boolean securityCheck(LoggedInInfo loggedInInfo, String module, String access_rights) {
		boolean pass = securityInfoManager.hasPrivilege(loggedInInfo, module, access_rights, null);
		if(!pass) {
			logger.warn("User missing "+ module +" role with "+access_rights+" access");
		}
		return pass;
	}
	
}
