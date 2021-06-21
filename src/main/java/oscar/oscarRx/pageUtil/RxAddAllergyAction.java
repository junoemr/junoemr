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


package oscar.oscarRx.pageUtil;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.allergy.service.AllergyService;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarRx.data.RxDrugData;
import oscar.oscarRx.data.RxPatientData;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public final class RxAddAllergyAction extends Action
{
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private AllergyService allergyService = (AllergyService)SpringUtils.getBean("allergy.service.AllergyService");

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(providerNo, "w", null, "_allergy");

		int id = Integer.parseInt(request.getParameter("ID"));

		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String description = request.getParameter("reactionDescription");

		String startDate = request.getParameter("startDate");
		String ageOfOnset = request.getParameter("ageOfOnset");
		String severityOfReaction = request.getParameter("severityOfReaction");
		String onSetOfReaction = request.getParameter("onSetOfReaction");
		String lifeStage = request.getParameter("lifeStage");

		String allergyToArchive = request.getParameter("allergyToArchive");
		RxPatientData.Patient patient = (RxPatientData.Patient) request.getSession().getAttribute("Patient");

		int oldAllergyId = 0;
		Allergy allergy = new Allergy();
		if (allergyToArchive != null && !allergyToArchive.isEmpty())
		{
			oldAllergyId = Integer.parseInt(allergyToArchive);
			allergy = patient.getAllergy(oldAllergyId);
		}

		allergy.setDrugrefId(String.valueOf(id));
		allergy.setDescription(name);
		allergy.setTypeCode(Integer.parseInt(type));
		allergy.setReaction(description);

		if(startDate.length() >= 8 && getCharOccur(startDate, '-') == 2)
		{
			allergy.setStartDate(ConversionUtils.fromDateString(startDate, "yyyy-MM-dd"));
		}
		else if(startDate.length() >= 6 && getCharOccur(startDate, '-') >= 1)
		{
			allergy.setStartDate(ConversionUtils.fromDateString(startDate, "yyyy-MM"));
			allergy.setStartDateFormat(PartialDate.FORMAT_YEAR_MONTH);
		}
		else if(startDate.length() >= 4)
		{
			allergy.setStartDate(ConversionUtils.fromDateString(startDate, "yyyy"));
			allergy.setStartDateFormat(PartialDate.FORMAT_YEAR_ONLY);
		}
		allergy.setAgeOfOnset(ageOfOnset);
		allergy.setSeverityOfReaction(severityOfReaction);
		allergy.setOnsetOfReaction(onSetOfReaction);
		allergy.setLifeStage(lifeStage);

		if (type.equals("13"))
		{
			RxDrugData drugData = new RxDrugData();
			try
			{
				RxDrugData.DrugMonograph f = drugData.getDrug("" + id);
				allergy.setRegionalIdentifier(f.regionalIdentifier);
			}
			catch(Exception e)
			{
				MiscUtils.getLogger().error("Error", e);
			}
		}

		allergy.setDemographicNo(patient.getDemographicNo());
		allergy.setArchived(false);

		String ip = request.getRemoteAddr();

		if (oldAllergyId > 0)
		{
			allergyService.update(allergy);
			LogAction.addLog((String)request.getSession().getAttribute("user"),
					LogConst.ACTION_UPDATE,
					LogConst.CON_ALLERGY,
					"" + oldAllergyId,
					ip,
					"" + allergy.getDemographicNo(),
					patient.getAllergy(oldAllergyId).getAuditString());
		}
		else
		{
			allergy = allergyService.addNewAllergy(allergy);
			LogAction.addLog((String)request.getSession().getAttribute("user"),
					LogConst.ACTION_ADD,
					LogConst.CON_ALLERGY,
					"" + allergy.getAllergyId(),
					ip,
					"" + allergy.getDemographicNo(),
					allergy.getAuditString());
		}

		request.setAttribute("demographicNo", allergy.getDemographicNo());
		return (mapping.findForward("success"));
	}

	private int getCharOccur(String str, char ch)
	{
		int occurence = 0, from = 0;
		while(str.indexOf(ch, from) >= 0)
		{
			occurence++;
			from = str.indexOf(ch, from) + 1;
		}
		return occurence;
	}
}
