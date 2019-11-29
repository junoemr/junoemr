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


package oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class EctConAddSpecialistAction extends Action {

	private static final Logger logger=MiscUtils.getLogger();
	private static final int[] refNoLengthRange = {4, 9}; // inclusive

	private ProfessionalSpecialistDao professionalSpecialistDao=(ProfessionalSpecialistDao) SpringUtils.getBean("professionalSpecialistDao");
	private static SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	  	if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_con", "w", null)) {
			throw new SecurityException("missing required security object (_con)");
		}
		
		ProfessionalSpecialist professionalSpecialist=null;
		EctConAddSpecialistForm addSpecialistForm = (EctConAddSpecialistForm)form;

		int whichType = addSpecialistForm.getwhichType();
		if(whichType == 1) //create
		{
			professionalSpecialist=new ProfessionalSpecialist();
			populateFields(professionalSpecialist, addSpecialistForm);
			if (professionalSpecialist.getReferralNo() != null && professionalSpecialist.getReferralNo().length() > 0) {
				if (referralNoValid(professionalSpecialist.getReferralNo())) {
					if (referralNoInUse(professionalSpecialist.getReferralNo())) {
						request.setAttribute("refnoinuse", true);
                		return mapping.findForward("success");
					}
				} else {
					request.setAttribute("refnoinvalid", true);
                	return mapping.findForward("success");
				}
			}
			professionalSpecialistDao.persist(professionalSpecialist);
		}
		else if (whichType == 2) // update
		{
            request.setAttribute("upd", true);

			Integer specId = Integer.parseInt(addSpecialistForm.getSpecId());
			professionalSpecialist=professionalSpecialistDao.find(specId);
			populateFields(professionalSpecialist, addSpecialistForm);
			if (professionalSpecialist.getReferralNo() != null && professionalSpecialist.getReferralNo().length() > 0) {
				if (referralNoValid(professionalSpecialist.getReferralNo())) {
					if (referralNoInUse(professionalSpecialist.getReferralNo(), specId)) {
						request.setAttribute("refnoinuse", true);
						return mapping.findForward("success");
					}
				} else {
					request.setAttribute("refnoinvalid", true);
					return mapping.findForward("success");
				}
			}
			professionalSpecialistDao.merge(professionalSpecialist);
		}
		else
		{
			logger.error("missed a case, whichType="+whichType);
		}

		String added=""+professionalSpecialist.getFirstName()+" "+professionalSpecialist.getLastName();
		request.setAttribute("Added", added);
		request.setAttribute("specId", addSpecialistForm.getSpecId());
		request.setAttribute("fName", addSpecialistForm.getFirstName());
		request.setAttribute("lName", addSpecialistForm.getLastName());
		request.setAttribute("proLetters", addSpecialistForm.getProLetters());
		request.setAttribute("address", addSpecialistForm.getAddress());
		request.setAttribute("phone", addSpecialistForm.getPhone());
		request.setAttribute("fax", addSpecialistForm.getFax());
		request.setAttribute("website", addSpecialistForm.getWebsite());
		request.setAttribute("email", addSpecialistForm.getEmail());
		request.setAttribute("specType", addSpecialistForm.getSpecType());
		request.setAttribute("eDataUrl", addSpecialistForm.geteDataUrl());
		request.setAttribute("eDataOscarKey", addSpecialistForm.geteDataOscarKey());
		request.setAttribute("eDataServiceKey", addSpecialistForm.geteDataServiceKey());
		request.setAttribute("eDataServiceName", addSpecialistForm.geteDataServiceName());
		request.setAttribute("annotation", addSpecialistForm.getAnnotation());
		request.setAttribute("referralNo", addSpecialistForm.getReferralNo());
		request.setAttribute("institution", addSpecialistForm.getInstitution());
		request.setAttribute("department", addSpecialistForm.getDepartment());
		request.setAttribute("privatePhoneNumber", addSpecialistForm.getPrivatePhoneNumber());
		request.setAttribute("cellPhoneNumber", addSpecialistForm.getCellPhoneNumber());
		request.setAttribute("pagerNumber", addSpecialistForm.getPagerNumber());
		request.setAttribute("salutation", addSpecialistForm.getSalutation());
		request.setAttribute("hideFromView", addSpecialistForm.getHideFromView());
		request.setAttribute("eformId", addSpecialistForm.getEformId());

		addSpecialistForm.resetForm();
		return mapping.findForward("success");
	}

	private boolean referralNoInUse(String referralNo) {
		return professionalSpecialistDao.getByReferralNo(referralNo) != null;
	}

	private boolean referralNoInUse(String referralNo, Integer specId) {
		ProfessionalSpecialist specialist = professionalSpecialistDao.getByReferralNo(referralNo);
		return (specialist != null && (specialist.getId().intValue() != specId.intValue()));
	}

	/** 
	 * Check that the given string is a valid referral number.
	 * @param referralNo a String representing the referral number
	 * @return true if string contains a valid referral number, false otherwise
	 */
	private boolean referralNoValid(String referralNo) {
		try {
			// confirm that refno is valid integer value
			// Ensure referral number is within a valid length range
			return (   referralNo.matches("\\d*") 
					&& referralNo.length() >= refNoLengthRange[0] 
					&& referralNo.length() <= refNoLengthRange[1]);
		}
		catch (Exception e) {
			MiscUtils.getLogger().info("Specified referral number invalid (" + referralNo + ")", e);
		}
		return false;
	}

	private void populateFields(ProfessionalSpecialist professionalSpecialist, EctConAddSpecialistForm addSpecailistForm) {
		professionalSpecialist.setFirstName(addSpecailistForm.getFirstName());
		professionalSpecialist.setLastName(addSpecailistForm.getLastName());
		professionalSpecialist.setProfessionalLetters(addSpecailistForm.getProLetters());

		String address = addSpecailistForm.getAddress();
		StringBuilder sb = new StringBuilder();
		for (int i =0 ; i < address.length(); i++){
			int a = address.charAt(i);
			if ( a == 13 || a == 10 ){
				sb.append(" ");
			}else{
				sb.append((char)a);
			}
		}
		address = sb.toString();
		professionalSpecialist.setStreetAddress(addSpecailistForm.getAddress());

		professionalSpecialist.setPhoneNumber(addSpecailistForm.getPhone());
		professionalSpecialist.setFaxNumber(addSpecailistForm.getFax());
		professionalSpecialist.setWebSite(addSpecailistForm.getWebsite());
		professionalSpecialist.setEmailAddress(addSpecailistForm.getEmail());
		professionalSpecialist.setSpecialtyType(addSpecailistForm.getSpecType());
		professionalSpecialist.seteDataUrl(addSpecailistForm.geteDataUrl());
		professionalSpecialist.seteDataOscarKey(addSpecailistForm.geteDataOscarKey());
		professionalSpecialist.seteDataServiceKey(addSpecailistForm.geteDataServiceKey());
		professionalSpecialist.seteDataServiceName(addSpecailistForm.geteDataServiceName());
		professionalSpecialist.setAnnotation(addSpecailistForm.getAnnotation());
		professionalSpecialist.setReferralNo(addSpecailistForm.getReferralNo());
		professionalSpecialist.setInstitutionId(Integer.parseInt(addSpecailistForm.getInstitution()));
		professionalSpecialist.setDepartmentId(Integer.parseInt(addSpecailistForm.getDepartment()));
		professionalSpecialist.setPrivatePhoneNumber(addSpecailistForm.getPrivatePhoneNumber());
		professionalSpecialist.setCellPhoneNumber(addSpecailistForm.getCellPhoneNumber());
		professionalSpecialist.setPagerNumber(addSpecailistForm.getPagerNumber());
		professionalSpecialist.setSalutation(addSpecailistForm.getSalutation());
		professionalSpecialist.setHideFromView(addSpecailistForm.getHideFromView());
		professionalSpecialist.setEformId(addSpecailistForm.getEformId());
	}
}
