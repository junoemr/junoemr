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


package oscar.oscarBilling.ca.bc.pageUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.BillingreferralDao;
import org.oscarehr.common.model.Billingreferral;
import org.oscarehr.util.SpringUtils;

/**
 *
 * @author Jay Gallagher
 */
public class AddReferralDocAction extends Action {

	private BillingreferralDao billingReferralDao = (BillingreferralDao)SpringUtils.getBean("BillingreferralDAO");

  public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) {
     ActionForward af = mapping.findForward("success");

     AddReferralDocForm form = (AddReferralDocForm) actionForm;

     String id = request.getParameter("id");
     if (id == null ){
    	 
    	 List<Billingreferral> billingReferrals = billingReferralDao.getBillingreferral(form.getReferral_no());
    	 
    	 if(billingReferrals.isEmpty()) {
    		 Billingreferral billingReferral = new Billingreferral();
    		 billingReferral.setReferralNo(form.getReferral_no());
    		 billingReferral.setLastName(form.getLast_name());
    		 billingReferral.setFirstName(form.getFirst_name());
    		 billingReferral.setSpecialty(form.getSpecialty());
    		 billingReferral.setAddress1(form.getAddress1());
    		 billingReferral.setAddress2(form.getAddress2());
    		 billingReferral.setCity(form.getCity());
    		 billingReferral.setProvince(form.getProvince());
    		 billingReferral.setPostal(form.getPostal());
    		 billingReferral.setPhone(form.getPhone());
    		 billingReferral.setFax(form.getFax());
    		 billingReferralDao.updateBillingreferral(billingReferral);
    	 } else {
    		 request.setAttribute("Error", "Billing # already in use");
             af = mapping.findForward("error");
    	 }

     }else{
    	Billingreferral billingReferral = billingReferralDao.getById(Integer.parseInt(id));
    	if(billingReferral!=null) {
   		 billingReferral.setReferralNo(form.getReferral_no());
   		 billingReferral.setLastName(form.getLast_name());
   		 billingReferral.setFirstName(form.getFirst_name());
   		 billingReferral.setSpecialty(form.getSpecialty());
   		 billingReferral.setAddress1(form.getAddress1());
   		 billingReferral.setAddress2(form.getAddress2());
   		 billingReferral.setCity(form.getCity());
   		 billingReferral.setProvince(form.getProvince());
   		 billingReferral.setPostal(form.getPostal());
   		 billingReferral.setPhone(form.getPhone());
   		 billingReferral.setFax(form.getFax());
   		 billingReferralDao.updateBillingreferral(billingReferral);
    	}
     }

     //af.setRedirect(true);
     return af;
  }
   public AddReferralDocAction() {
   }

}
