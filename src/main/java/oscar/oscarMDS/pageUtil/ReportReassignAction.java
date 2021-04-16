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


package oscar.oscarMDS.pageUtil;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.ProviderLabRoutingFavoritesDao;
import org.oscarehr.common.model.ProviderLabRoutingFavorite;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarLab.ca.on.CommonLabResultData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ReportReassignAction extends Action
{

	private static final Logger logger = Logger.getLogger(ReportReassignAction.class);
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public ReportReassignAction()
	{
	}

	public ActionForward execute(ActionMapping mapping,
	                             ActionForm form,
	                             HttpServletRequest request,
	                             HttpServletResponse response)
			throws ServletException, IOException
	{


		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireOnePrivilege(loggedInProviderNo, SecurityInfoManager.CREATE, null, "_lab");

		String providerNo = request.getParameter("providerNo");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String status = request.getParameter("status");
		if(status == null)
		{
			status = "";
		}

		String[] flaggedLabs = request.getParameterValues("flaggedLabs");
		logger.info("Flagged Labs is null " + String.valueOf(flaggedLabs == null));

		String selectedProviders = request.getParameter("selectedProviders");
		logger.info("selectedProviders " + selectedProviders);
		String newFavorites = request.getParameter("favorites");
		if(newFavorites == null || newFavorites.equals("null"))
		{
			newFavorites = "";
		}

		String ajax = request.getParameter("ajax");
		String[] labTypes = CommonLabResultData.getLabTypes();
		ArrayList<String[]> listFlaggedLabs = new ArrayList<>();

		if(flaggedLabs != null)
		{
			// use a hash set to remove duplicated IDs
			HashSet<String> uniqueLabIds = new HashSet<>(flaggedLabs.length);
			uniqueLabIds.addAll(Arrays.asList(flaggedLabs));

			logger.info("flagged Labs length " + uniqueLabIds.size());
			for(String labId : uniqueLabIds)
			{
				logger.info("FLAGGED LABS " + labId);
				for(int j = 0; j < labTypes.length; j++)
				{
					logger.info("LAB TYPE " + labTypes[j]);
					String s = request.getParameter("labType" + labId + labTypes[j]);
					logger.info(s);
					if(s != null)
					{  //This means that the lab was of this type.
						String[] la = new String[]{labId, labTypes[j]};
						listFlaggedLabs.add(la);
						j = labTypes.length;
					}
				}
			}
		}

		String newURL = "";
		try
		{
			//Only route if there are selected providers
			if(!(selectedProviders == null || selectedProviders.isEmpty()))
			{
				CommonLabResultData.updateLabRouting(listFlaggedLabs, selectedProviders);

				/* log the lab assignments in the security log */
				for(String[] flaggedLabPair : listFlaggedLabs)
				{
					String iLabId = flaggedLabPair[0];
					String iLabType = flaggedLabPair[1];
					String logConst = (iLabType.equalsIgnoreCase(ProviderLabRoutingDao.LAB_TYPE_DOC)) ? LogConst.CON_DOCUMENT : LogConst.CON_HL7_LAB;
					LogAction.addLogEntry(loggedInProviderNo, null, LogConst.ACTION_ASSIGN, logConst, LogConst.STATUS_SUCCESS,
							iLabId, request.getRemoteAddr(), selectedProviders);
				}
			}
		    //update favorites
		    ProviderLabRoutingFavoritesDao favDao = (ProviderLabRoutingFavoritesDao) SpringUtils.getBean("ProviderLabRoutingFavoritesDao");
		    String user = (String) request.getSession().getAttribute("user");
		    List<ProviderLabRoutingFavorite> currentFavorites = favDao.findFavorites(user);
        	
        	if( "".equals(newFavorites) ) {
        		for( ProviderLabRoutingFavorite fav : currentFavorites ) {
        			favDao.remove(fav.getId());
        		}
        	}
        	else {
        		String[] arrNewFavs = newFavorites.split(",");
        		
        		//Check for new favorites to add
        		boolean isNew;
        		for( int idx = 0; idx < arrNewFavs.length; ++idx ) {
        			isNew = true;
        			for( ProviderLabRoutingFavorite fav : currentFavorites ) {
        				if( fav.getRoute_to_provider_no().equals(arrNewFavs[idx])) {
        					isNew = false;
        					break;
        				}
        			}
        			if( isNew ) {
        				ProviderLabRoutingFavorite newFav = new ProviderLabRoutingFavorite();
        				newFav.setProvider_no(user);
        				newFav.setRoute_to_provider_no(arrNewFavs[idx]);
        				favDao.persist(newFav);
        			}
        		}
        		
        		//check for favorites to remove
        		boolean remove;
        		for( ProviderLabRoutingFavorite fav : currentFavorites ) {
        			remove = true;
        			for( int idx2 = 0; idx2 < arrNewFavs.length; ++idx2 ) {
        				if( fav.getRoute_to_provider_no().equals(arrNewFavs[idx2])) {
        					remove = false;
        					break;
        				}
        			}
        			if( remove ) {
        				favDao.remove(fav.getId());
        			}
        		}
        		
        	}
        	
            newURL = mapping.findForward("success").getPath();
            if(newURL.contains("labDisplay.jsp"))
                newURL = newURL + "?providerNo=" + providerNo + "&searchProviderNo=" + searchProviderNo + "&status=" + status + "&segmentID=" + flaggedLabs[0];

            // the segmentID is needed when being called from a lab display
            else newURL = newURL + "&providerNo=" + providerNo + "&searchProviderNo=" + searchProviderNo + "&status=" + status + "&segmentID=" + flaggedLabs[0];
            if (request.getParameter("lname") != null) { newURL = newURL + "&lname="+request.getParameter("lname"); }
            if (request.getParameter("fname") != null) { newURL = newURL + "&fname="+request.getParameter("fname"); }
            if (request.getParameter("hnum") != null) { newURL = newURL + "&hnum="+request.getParameter("hnum"); }
        } catch (Exception e) {
            logger.error("exception in ReportReassignAction", e);
            newURL = mapping.findForward("failure").getPath();
        }

		if (ajax != null && ajax.equals("yes"))
		{
			return null;
		}
		else
		{
			return (new ActionRedirect(newURL));
		}
	}
}
