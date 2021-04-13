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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.validator.LazyValidatorForm;
import org.oscarehr.appointment.service.AppointmentStatusService;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
		
		status.setDescription(lazyForm.get("description").toString());
		status.setIcon(lazyForm.get("icon").toString());
		status.setColor(lazyForm.get("color").toString());
		status.setJunoColor(lazyForm.get("junoColor").toString());
		status.setActive(Integer.parseInt(lazyForm.get("active").toString()));
		status.setEditable(1);
		
		synchronized(AppointmentStatus.class)
		{
			statusService.assignStatusCodeAndSave(status);
		}
		
		loadStatusAttributes(request);
		return mapping.findForward("success");
	}
	
	/** Forward to the edit page in create mode */
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LazyValidatorForm lazyForm = (LazyValidatorForm) form;
	
		// Initialize some default values for the new status.
		lazyForm.set("active", 1);
		lazyForm.set("icon", "dot.gif");
		lazyForm.set("color", "#897DF8");
		lazyForm.set("junoColor", "#AC9DF2");
		
		request.setAttribute("canDisable", 1);
		request.setAttribute("action", "add");
		return mapping.findForward("edit");
	}
	
	/** Forward to the edit page in update mode */
	public ActionForward modify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		int id = Integer.parseInt(request.getParameter("statusId"));
		AppointmentStatus apptStatus = statusService.getAppointmentStatusById(id);
		
		LazyValidatorForm lazyForm = (LazyValidatorForm) form;
		lazyForm.set("id", id);
		lazyForm.set("active", apptStatus.getActive());
		lazyForm.set("icon", apptStatus.getIcon());
		lazyForm.set("color", apptStatus.getColor());
		lazyForm.set("junoColor", apptStatus.getJunoColor());
		lazyForm.set("description", apptStatus.getDescription());
		
		request.setAttribute("canDisable", apptStatus.getEditable());
		request.setAttribute("action", "update");
		return mapping.findForward("edit");
	}
	
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LazyValidatorForm lazyForm = (LazyValidatorForm) form;
	
		Integer statusId = Integer.parseInt(lazyForm.get("id").toString());
		AppointmentStatus apptStatus = statusService.getAppointmentStatusById(statusId);
		String description = lazyForm.get("description").toString();

		int isActive = 1;
		if (lazyForm.get("active") != null)
		{
			isActive = Integer.parseInt(lazyForm.get("active").toString());
		}

		// For "non-editable statuses", the two things we want to prevent are changing name or disabling
		if (!apptStatus.isEditable() && (isActive != 1 || !description.equals(apptStatus.getDescription())))
		{
			throw new RuntimeException("Can't disable a readonly status or change its description");
		}
		
		apptStatus.setDescription(description);
		apptStatus.setActive(isActive);
		apptStatus.setIcon(lazyForm.get("icon").toString());
		apptStatus.setColor(lazyForm.get("color").toString());
		apptStatus.setJunoColor(lazyForm.get("junoColor").toString());
		statusService.updateAppointmentStatus(apptStatus);
		
		loadStatusAttributes(request);
		return mapping.findForward("success");
	}
	
	public ActionForward moveUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		int id = Integer.parseInt(request.getParameter("statusId"));
		AppointmentStatus apptStatus = statusService.getAppointmentStatusById(id);
		
		statusService.swapUp(apptStatus);
		
		loadStatusAttributes(request);
		return mapping.findForward("success");
	}
	
	public ActionForward moveDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		int id = Integer.parseInt(request.getParameter("statusId"));
		AppointmentStatus apptStatus = statusService.getAppointmentStatusById(id);
		
		statusService.swapDown(apptStatus);
		
		loadStatusAttributes(request);
		return mapping.findForward("success");
	}
	
	private void loadStatusAttributes(HttpServletRequest request)
	{
		List<AppointmentStatus> allStatuses = statusService.getAllAppointmentStatuses();
		request.setAttribute("appointmentStatuses", allStatuses);
		
		List<AppointmentStatus> inactiveStatuses = statusService.getInactiveAppointmentStatuses();
		
		List<String> inactiveStatusCodesInUse = statusService.checkStatusUsage(inactiveStatuses);
		if (inactiveStatusCodesInUse.size() > 0)
		{
			request.setAttribute("alertStatuses", inactiveStatusCodesInUse);
		}
	}
}
