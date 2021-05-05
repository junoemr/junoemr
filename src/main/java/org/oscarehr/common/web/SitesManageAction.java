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

package org.oscarehr.common.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.BillingBCDao;
import org.oscarehr.common.dao.BillingServiceDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Site;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.transfer.billing.BCBillingLocationTo1;
import org.oscarehr.ws.rest.transfer.billing.BCBillingVisitCodeTo1;

public class SitesManageAction extends DispatchAction {

    private SiteDao siteDao;
    private BillingBCDao bcBillingDao = SpringUtils.getBean(BillingBCDao.class);

    @Override
    protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
        return view(mapping, form, request, response);
    }

    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
        List<Site> sites = siteDao.getAllSites();
        request.setAttribute("sites", sites);
        
        List<Object[]> codes = bcBillingDao.findBillingLocations(BillingServiceDao.BC);
        List<BCBillingLocationTo1> serviceLocationCodes = BCBillingLocationTo1.fromList(codes);
        request.setAttribute("serviceLocationCodes", serviceLocationCodes);
        
        return mapping.findForward("list");
    }

    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
    	DynaBean lazyForm = (DynaBean) form;

    	Site s = new Site();
    	lazyForm.set("site", s);
		
		List<Object[]> codes = bcBillingDao.findBillingVisits(BillingServiceDao.BC);
		List<BCBillingVisitCodeTo1> serviceLocationCodes = BCBillingVisitCodeTo1.fromList(codes);
		
		request.setAttribute("serviceLocationCodes", serviceLocationCodes);

        return mapping.findForward("details");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		DynaBean lazyForm = (DynaBean) form;
		Site siteFromForm = (Site) lazyForm.get("site");

		// verify mandatories
		if (StringUtils.isBlank(siteFromForm.getName()) || StringUtils.isBlank(siteFromForm.getShortName()))
		{
			ActionMessages errors = this.getErrors(request);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Site name or short name"));
			this.saveErrors(request, errors);
		}
		if (StringUtils.isBlank(siteFromForm.getBgColor()))
		{
			ActionMessages errors = this.getErrors(request);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.required", "Theme color"));
			this.saveErrors(request, errors);
		}

		// check that site with this name does not already exist
		Site siteByName = siteDao.findByName(siteFromForm.getName());
		if (siteByName != null && !siteByName.getId().equals(siteFromForm.getId()))
		{
			ActionMessages errors = this.getErrors(request);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.exists", "A site with the name: " + siteFromForm.getName()));
			this.saveErrors(request, errors);
		}

		// check that site with this short name does not already exist
		Site siteByShortName = siteDao.findByShortName(siteFromForm.getShortName());
		if (siteByShortName != null && !siteByShortName.getId().equals(siteFromForm.getId()))
		{
			ActionMessages errors = this.getErrors(request);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.exists", "A site with the short name: " + siteFromForm.getShortName()));
			this.saveErrors(request, errors);
		}

    	if (this.getErrors(request).size() > 0)
		{
			return mapping.findForward("details");
		}

		Site oldSite = siteDao.getById(siteFromForm.getSiteId());

		if (oldSite != null)
		{
			oldSite.setName(siteFromForm.getName());
			oldSite.setShortName(siteFromForm.getShortName());
			oldSite.setBgColor(siteFromForm.getBgColor());
			oldSite.setStatus(siteFromForm.getStatus());
			oldSite.setPhone(siteFromForm.getPhone());
			oldSite.setFax(siteFromForm.getFax());
			oldSite.setAddress(siteFromForm.getAddress());
			oldSite.setCity(siteFromForm.getCity());
			oldSite.setProvince(siteFromForm.getProvince());
			oldSite.setPostal(siteFromForm.getPostal());
			oldSite.setAlbertaConnectCareLabId(siteFromForm.getAlbertaConnectCareLabId());
			oldSite.setAlbertaConnectCareDepartmentId(siteFromForm.getAlbertaConnectCareDepartmentId());

			siteDao.save(oldSite);
		}
		else
		{
			siteDao.save(siteFromForm);
		}

        return view(mapping, form, request, response);
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
    	DynaBean lazyForm = (DynaBean) form;

    	String siteId = request.getParameter("siteId");
        Site s = siteDao.getById(new Integer(siteId));

        lazyForm.set("site", s);
        return mapping.findForward("details");
    }

	public void setSiteDao(SiteDao siteDao)
	{
		this.siteDao = siteDao;
	}
}
