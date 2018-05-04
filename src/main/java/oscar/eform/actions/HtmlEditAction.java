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


package oscar.eform.actions;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.service.EFormTemplateService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WebUtils;
import oscar.eform.EFormUtil;
import oscar.eform.data.HtmlEditForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;


public class HtmlEditAction extends Action
{
	private static Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private EFormTemplateService eFormTemplateService = SpringUtils.getBean(EFormTemplateService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_eform", "w", null))
		{
			throw new SecurityException("missing required security object (_eform)");
		}

		HtmlEditForm fm = (HtmlEditForm) form;
		try
		{
			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
			String creatorNo = loggedInInfo.getLoggedInProviderNo();

			String fidStr = fm.getFid();
			String formName = fm.getFormName();
			String formSubject = fm.getFormSubject();
			String formFileName = fm.getFormFileName();
			String formHtml = fm.getFormHtml();
			boolean showLatestFormOnly = WebUtils.isChecked(request, "showLatestFormOnly");
			boolean patientIndependent = WebUtils.isChecked(request, "patientIndependent");
			boolean instanced = WebUtils.isChecked(request, "instanced");
			String roleType = fm.getRoleType();

			boolean isNewEFormTemplate = (fidStr.length() == 0);

			HashMap<String, String> errors = new HashMap<String, String>();
			HashMap<String, Object> submittedValues;
			//validation...
			if((formName == null) || (formName.length() == 0))
			{
				errors.put("formNameMissing", "eform.errors.form_name.missing.regular");
			}
			if(isNewEFormTemplate && (EFormUtil.formExistsInDBn(formName, fidStr) > 0))
			{
				errors.put("formNameExists", "eform.errors.form_name.exists.regular");
			}

			if(errors.isEmpty())
			{
				EForm eFormTemplate;
				if(isNewEFormTemplate)
				{
					logger.info("Created new EForm Template");
					eFormTemplate = eFormTemplateService.addEFormTemplate(formName, formSubject, formFileName, formHtml,
							creatorNo, showLatestFormOnly, patientIndependent, instanced, roleType);
					LogAction.addLogEntry(creatorNo, null, LogConst.ACTION_ADD, LogConst.CON_EFORM_TEMPLATE, LogConst.STATUS_SUCCESS,
							String.valueOf(eFormTemplate.getId()), loggedInInfo.getIp(), eFormTemplate.getFormName());
				}
				else
				{
					logger.info("Update EForm Template (id: " + fidStr + ")");
					eFormTemplate = eFormTemplateService.updateEFormTemplate(Integer.parseInt(fidStr), formName, formSubject,
							formFileName, formHtml, creatorNo, showLatestFormOnly, patientIndependent, instanced, roleType);
					LogAction.addLogEntry(creatorNo, null, LogConst.ACTION_UPDATE, LogConst.CON_EFORM_TEMPLATE, LogConst.STATUS_SUCCESS,
							String.valueOf(eFormTemplate.getId()), loggedInInfo.getIp(), eFormTemplate.getFormName());
				}
				request.setAttribute("success", "true");
				submittedValues = createHashMap(eFormTemplate);
			}
			else
			{
				submittedValues = createHashMap(fidStr, formName, formSubject, formFileName, formHtml, showLatestFormOnly, patientIndependent, instanced, roleType, null);
			}

			request.setAttribute("submitted", submittedValues);
			request.setAttribute("errors", errors);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
			request.setAttribute("error", "An error occurred saving the eForm");
			return (mapping.findForward("failure"));
		}
		return (mapping.findForward("success"));
	}

	private HashMap<String, Object> createHashMap(EForm eFormTemplate)
	{
		return createHashMap(eFormTemplate.getId().toString(),
				eFormTemplate.getFormName(),
				eFormTemplate.getSubject(),
				eFormTemplate.getFileName(),
				eFormTemplate.getFormHtml(),
				eFormTemplate.isShowLatestFormOnly(),
				eFormTemplate.isPatientIndependent(),
				eFormTemplate.isInstanced(),
				eFormTemplate.getRoleType(),
				eFormTemplate.getFormDateTime());
	}
	private HashMap<String, Object> createHashMap(String fid, String formName, String formSubject, String formFileName, String formHtml, boolean showLatestFormOnly, boolean patientIndependent, boolean instanced, String roleType, Date formDateTime)
	{
		HashMap<String, Object> curht = new HashMap<>();
		curht.put("fid", fid);
		curht.put("formName", formName);
		curht.put("formSubject", formSubject);
		curht.put("formFileName", formFileName);
		curht.put("formHtml", formHtml);
		curht.put("showLatestFormOnly", showLatestFormOnly);
		curht.put("patientIndependent", patientIndependent);
		curht.put("instanced", instanced);
		curht.put("roleType", roleType);

		if(formDateTime == null)
		{
			curht.put("formDate", "--");
			curht.put("formTime", "--");
		}
		else
		{
			curht.put("formDate", ConversionUtils.toDateString(formDateTime));
			curht.put("formTime", ConversionUtils.toTimeString(formDateTime));
		}
		return curht;
	}
}
