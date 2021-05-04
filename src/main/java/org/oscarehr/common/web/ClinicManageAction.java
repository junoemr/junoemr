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


package org.oscarehr.common.web;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.clinic.dao.ClinicBillingAddressDAO;
import org.oscarehr.clinic.model.ClinicBillingAddress;
import org.oscarehr.common.dao.ClinicDAO;
import org.oscarehr.common.model.Clinic;
import oscar.OscarProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClinicManageAction extends DispatchAction
{

    private ClinicDAO clinicDAO;
    private ClinicBillingAddressDAO clinicBillingAddressDAO;
    private OscarProperties oscarProperties = OscarProperties.getInstance();

    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        return view(mapping, form, request, response);
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        String updateStatus = "";

        if (request.getAttribute("updateSuccess") != null)
        {
            updateStatus = (String) request.getAttribute("updateSuccess");
        }

        request.setAttribute("updateStatus", updateStatus);

        Clinic clinic = clinicDAO.getClinic();
        ClinicBillingAddress clinicBillingAddress = new ClinicBillingAddress();
        boolean hasCustomBillingAddress = false;

        if (clinic.getClinicBillingAddress() != null)
        {
            clinicBillingAddress = clinic.getClinicBillingAddress();
            hasCustomBillingAddress = true;
        }

        DynaActionForm frm = (DynaActionForm) form;

        frm.set("clinic", clinic);
        frm.set("clinicBillingAddress", clinicBillingAddress);

        request.setAttribute("clinicForm", form);
        request.setAttribute("hasCustomBillingAddress", hasCustomBillingAddress);

        return mapping.findForward("success");
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        DynaActionForm frm = (DynaActionForm) form;
        Clinic clinicFromForm = (Clinic) frm.get("clinic");
        Clinic oldClinic = clinicDAO.find(clinicFromForm.getId());

        //weird hack, but not sure why struts isn't filling in the id.
        if (request.getParameter("clinic.id") != null && request.getParameter("clinic.id").length() > 0 && clinicFromForm.getId() == null)
        {
            clinicFromForm.setId(Integer.parseInt(request.getParameter("clinic.id")));
        }

        if (request.getParameter("billingCheck") != null && request.getParameter("billingCheck").equals("on"))
        {
            ClinicBillingAddress clinicBillingAddress = (ClinicBillingAddress) frm.get("clinicBillingAddress");
            clinicBillingAddressDAO.save(clinicBillingAddress);
            clinicFromForm.setClinicBillingAddress(clinicBillingAddress);
        }

        if (oldClinic != null)
        {
            oldClinic.setClinicName(clinicFromForm.getClinicName());
            oldClinic.setClinicAddress(clinicFromForm.getClinicAddress());
            oldClinic.setClinicCity(clinicFromForm.getClinicCity());
            oldClinic.setClinicProvince(clinicFromForm.getClinicProvince());
            oldClinic.setClinicPostal(clinicFromForm.getClinicPostal());
            oldClinic.setClinicPhone(clinicFromForm.getClinicPhone());
            oldClinic.setClinicFax(clinicFromForm.getClinicFax());
            oldClinic.setClinicLocationCode(clinicFromForm.getClinicLocationCode());

            oldClinic.setClinicDelimPhone(clinicFromForm.getClinicDelimPhone());
            oldClinic.setClinicDelimFax(clinicFromForm.getClinicDelimFax());
            oldClinic.setClinicEmail(clinicFromForm.getClinicEmail());

            // If billing check
            if (request.getParameter("billingCheck") != null && request.getParameter("billingCheck").equals("on"))
            {
                oldClinic.setClinicBillingAddress(clinicFromForm.getClinicBillingAddress());
            }

            if (oscarProperties.isBritishColumbiaInstanceType())
            {
                oldClinic.setBcFacilityNumber(clinicFromForm.getBcFacilityNumber());
            }

            if (oscarProperties.isAlbertaInstanceType())
            {
                oldClinic.setAlbertaConnectCareDepartmentId(clinicFromForm.getAlbertaConnectCareDepartmentId());
                oldClinic.setAlbertaConnectCareLabId(clinicFromForm.getAlbertaConnectCareLabId());
            }

            clinicDAO.save(oldClinic);
        }
        else
        {
            clinicDAO.save(clinicFromForm);
        }

        request.setAttribute("updateSuccess", "Updated Successfully");

        return view(mapping, form, request, response);
    }

    public void setClinicDAO(ClinicDAO clinicDAO)
    {
        this.clinicDAO = clinicDAO;
    }

    public void setClinicBillingAddressDAO(ClinicBillingAddressDAO clinicBillingAddressDAO)
    {
        this.clinicBillingAddressDAO = clinicBillingAddressDAO;
    }
}
