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
package oscar.oscarLab.ca.all.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.OscarAuditLogger;
import org.oscarehr.util.SpringUtils;

import oscar.log.LogConst;

/**
 *
 * @author mweston4
 */
public class UnlinkDemographicAction  extends Action {
    
    private static Logger logger = MiscUtils.getLogger();
    
    public UnlinkDemographicAction () {}
    
    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
    	
    	Integer demoNo = Integer.parseInt(request.getParameter("demographicNo"));
        String reason = request.getParameter("reason");
        Integer labNo = Integer.parseInt(request.getParameter("labNo"));
        
        PatientLabRoutingDao plrDao = SpringUtils.getBean(PatientLabRoutingDao.class);
        PatientLabRouting plr = plrDao.findSingleLabRoute(labNo, demoNo);

        //set the demographicNo in the patientLabRouting table
        plr.setDemographicNo(PatientLabRoutingDao.UNMATCHED);
        plrDao.merge(plr);
        
        OscarAuditLogger.getInstance().log(LogConst.UNLINK, LogConst.CON_HL7_LAB, String.valueOf(labNo), request.getRemoteAddr(), demoNo, reason);
        
        logger.info("Unlinked lab with segmentID: " + labNo + " from eChart of Demographic " + demoNo);
        
        return mapping.findForward("success");
    }
    
    
}