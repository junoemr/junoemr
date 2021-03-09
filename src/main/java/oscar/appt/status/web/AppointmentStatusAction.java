/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 */

package oscar.appt.status.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.validator.LazyValidatorForm;
import org.oscarehr.appointment.service.AppointmentStatusService;
import org.oscarehr.common.model.AppointmentStatus;

import org.oscarehr.util.SpringUtils;
import oscar.appt.status.service.AppointmentStatusMgr;
import oscar.appt.status.service.impl.AppointmentStatusMgrImpl;

public class AppointmentStatusAction extends DispatchAction {
	
    private static final AppointmentStatusService statusService = SpringUtils.getBean(AppointmentStatusService.class);

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        loadStatusAttributes(request);
        return mapping.findForward("success");
    }
    
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
	    LazyValidatorForm lazyForm = (LazyValidatorForm) form;
	    
	    AppointmentStatus status = new AppointmentStatus();
	    // statusService has business logic to take care of setting the status code for us
	    status.setDescription(lazyForm.get("description").toString());
	    status.setIcon(lazyForm.get("icon").toString());
	    status.setColor(lazyForm.get("color").toString());
	    status.setJunoColor(lazyForm.get("junoColor").toString());
	    statusService.createAppointmentStatus(status);
    	
	    loadStatusAttributes(request);
	    return mapping.findForward("success");
    }
	
	/** Forward to the edit page in create mode */
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
    	LazyValidatorForm lazyForm = (LazyValidatorForm) form;
	
    	// Initialize some default values for the new status.
	    lazyForm.set("active", 1);
	    lazyForm.set("icon", "1.gif");
	    
	    request.setAttribute("action", "add");
    	return mapping.findForward("edit");
    }
    
    /** Forward to the edit page in update mode */
    public ActionForward modify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("statusID"));
        AppointmentStatus appt = statusService.getAppointmentStatusById(id);
        
        LazyValidatorForm lazyForm = (LazyValidatorForm) form;
        lazyForm.set("id", id);
        lazyForm.set("active", appt.getStatus());
        lazyForm.set("icon", appt.getIcon());
        lazyForm.set("description", appt.getDescription());
	    
        request.setAttribute("action", "update");
        return mapping.findForward("edit");
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        LazyValidatorForm lazyForm = (LazyValidatorForm) form;
	
	    Integer statusId = Integer.parseInt(lazyForm.get("id").toString());
        
        AppointmentStatus status = statusService.getAppointmentStatusById(statusId);
        if (status.getEditable() != 1)
        {
        	throw new RuntimeException("Can't edit a readonly status");
        }
        
        status.setDescription(lazyForm.get("description").toString());
        status.setActive(Integer.parseInt(lazyForm.get("active").toString()));
        status.setIcon(lazyForm.get("icon").toString());
        status.setColor(lazyForm.get("color").toString());
        status.setJunoColor(lazyForm.get("junoColor").toString());
        statusService.updateAppointmentStatus(status);
        
        loadStatusAttributes(request);
        return mapping.findForward("success");
    }
    
    private void loadStatusAttributes(HttpServletRequest request)
    {
        AppointmentStatusMgr apptStatusMgr = getApptStatusMgr();
        List<AppointmentStatus> allStatus = statusService.getAppointmentStatuses();
        
        request.setAttribute("appointmentStatuses", allStatus);
        
        List<String> inactiveUseStatus = apptStatusMgr.checkStatusUsuage(allStatus);
        if (inactiveUseStatus.size() > 0)
        {
            request.setAttribute("useStatus", inactiveUseStatus);
        }
    }
	
    // This is a super dangerous class, need to kill it.  Eg:  Check out the reset method.
	// TODO KILL WITH FIRE
	private AppointmentStatusMgr getApptStatusMgr()
	{
		return new AppointmentStatusMgrImpl();
	}
}
