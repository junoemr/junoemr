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


package oscar.oscarDemographic.pageUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.model.Facility;
import org.oscarehr.util.SessionConstants;

import oscar.oscarDemographic.data.DemographicRelationship;

/**
 *
 * @author Jay Gallagher
 */
public class AddDemographicRelationshipAction extends Action {
    
    
    public AddDemographicRelationshipAction() {
        
    }
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
        
        String origDemo = request.getParameter("origDemo");
        String linkingDemo = request.getParameter("linkingDemo");
        String relation = request.getParameter("relation");
        String sdm = request.getParameter("sdm");
        String emergContact = request.getParameter("emergContact");
        String notes = request.getParameter("notes");
        
        request.setAttribute("demographicNo",origDemo);
        if(request.getParameter("pmmClient") !=null && request.getParameter("pmmClient").equals("Finished")){
            return mapping.findForward("pmmClient");
        }
        
        
        
        
        String providerNo = (String) request.getSession().getAttribute("user");
        
        boolean sdmBool = false;
        boolean eBool = false;
        if (sdm != null && sdm.equals("yes")){
            sdmBool = true;
        }
        if (emergContact != null && emergContact.equals("yes")){
            eBool = true;
        }

        // if we're in a facility tag this association with the facility
        Facility facility=(Facility)request.getSession().getAttribute(SessionConstants.CURRENT_FACILITY);
        Integer facilityId=null;
        if (facility!=null) facilityId=facility.getId();
        
        DemographicRelationship demo = new DemographicRelationship();
        demo.addDemographicRelationships(origDemo,linkingDemo,relation,sdmBool,eBool,notes,providerNo, facilityId);
        
        request.setAttribute("demo", origDemo);
        
        return mapping.findForward("success");
    }
    
}
