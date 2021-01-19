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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.eform.exception.EFormMeasurementException;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.eform.EFormLoader;
import oscar.eform.EFormUtil;
import oscar.eform.data.DatabaseAP;
import oscar.eform.data.EForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddEFormAction extends Action {

	private static final Logger logger=MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private EFormDataService eFormService = SpringUtils.getBean(EFormDataService.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_eform", "w", null)) {
			throw new SecurityException("missing required security object (_eform)");
		}
		
		logger.info("================== SAVING EFORM ==============");
		HttpSession session = request.getSession();

		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNoStr=loggedInInfo.getLoggedInProviderNo();

		boolean fax = "true".equals(request.getParameter("fax"));
		boolean print = "true".equals(request.getParameter("print"));

		String fid = request.getParameter("efmfid");
		String oldFormDataId = org.apache.commons.lang.StringUtils.trimToNull(request.getParameter("efmfdid"));
		String demographicNoStr = request.getParameter("efmdemographic_no");
		String eFormLink = request.getParameter("eform_link");
		String subject = org.apache.commons.lang.StringUtils.trimToEmpty(request.getParameter("subject"));
		boolean doDatabaseUpdate = "on".equalsIgnoreCase(request.getParameter("_oscardodatabaseupdate"));

		Integer eformTemplateId = Integer.parseInt(fid);
		Integer demographicNo = Integer.parseInt(demographicNoStr);
		Integer providerNo = Integer.parseInt(providerNoStr);

		@SuppressWarnings("unchecked")
		Map<String, String[]> unfilteredParamValueMap = request.getParameterMap();
		Map<String, String> paramValueMap = new HashMap<>();
		Map<String,String> formOpenerMap = new HashMap<>();
		ActionMessages updateErrors = new ActionMessages();

		// special oscar override input names
		String dateOverrideValue = request.getParameter("_oscarOverrideFormDate");

		// The fields in the _oscarupdatefields parameter are separated by %s.
		if (!print && !fax && doDatabaseUpdate && request.getParameter("_oscarupdatefields") != null) {

			List<String> oscarUpdateFields = Arrays.asList(request.getParameter("_oscarupdatefields").split("%"));

			boolean validationError = false;

			for (String field : oscarUpdateFields) {
				EFormLoader.getInstance();
				// Check for existence of appropriate databaseap
				DatabaseAP currentAP = EFormLoader.getAP(field);
				if (currentAP != null) {
					if (!currentAP.isInputField()) {
						// Abort! This field can't be updated
						updateErrors.add(field, new ActionMessage("errors.richeForms.noInputMethodError", field));
						validationError = true;
					}
				} else {
					// Field doesn't exit
					updateErrors.add(field, new ActionMessage("errors.richeForms.noSuchFieldError", field));
					validationError = true;
				}
			}

			if (!validationError) {
				for (String field : oscarUpdateFields) {
					EFormLoader.getInstance();
					DatabaseAP currentAP = EFormLoader.getAP(field);
					// We can add more of these later...
					if (currentAP != null) {
						String inSQL = currentAP.getApInSQL();

						inSQL = DatabaseAP.parserReplace("demographic", demographicNoStr, inSQL);
						inSQL = DatabaseAP.parserReplace("provider", providerNoStr, inSQL);
						inSQL = DatabaseAP.parserReplace("fid", fid, inSQL);

						inSQL = DatabaseAP.parserReplace("value", request.getParameter(field), inSQL);

						//if(currentAP.getArchive() != null && currentAP.getArchive().equals("demographic")) {
						//	demographicArchiveDao.archiveRecord(demographicManager.getDemographic(loggedInInfo,demographic_no));
						//}

						// Run the SQL query against the database
						//TODO-legacy: do this a different way.
						MiscUtils.getLogger().error("Error",new Exception("EForm is using disabled functionality for updating fields..update not performed"));
					}
				}
			}
		}

		// filter incoming parameter values and remove unwanted (null/empty, etc.) pairs.
		for(Map.Entry<String, String[]> entry : unfilteredParamValueMap.entrySet())
		{
			String key = entry.getKey();
			String value = String.join(",", entry.getValue());// most parameters will be single value.
			if(value != null && !value.trim().isEmpty() && !value.equalsIgnoreCase("parentAjaxId"))
			{
				paramValueMap.put(key, value);
			}
		}
		// java 8 filtering
//		paramValueMap.entrySet().removeIf(entry -> (entry.getValue() == null
//				|| entry.getValue().trim().isEmpty()
//				|| entry.getValue().equalsIgnoreCase("parentAjaxId")));



		// for some reason, stuff is stored on the session, so it needs to be pulled off.
		Enumeration sessionAttr = session.getAttributeNames();
		String attrPattern = providerNoStr + "_" + demographicNoStr + "_" + fid + "_";
		while (sessionAttr.hasMoreElements())
		{
			Object key = sessionAttr.nextElement();
			if(key instanceof String)
			{
				String attribute = (String) key;
				if(attribute.startsWith(attrPattern))
				{
					String name = attribute.substring(attrPattern.length());
					String value = (String) session.getAttribute(attribute);
					formOpenerMap.put(name, value);
					if(value != null) session.removeAttribute(attribute);
				}
			}
		}

		try
		{
			EFormData eForm;
			if(StringUtils.filled(oldFormDataId))
			{
				Integer oldFdid = Integer.parseInt(oldFormDataId);
				eForm = eFormService.saveExistingEForm(oldFdid, demographicNo, providerNo, subject,
						formOpenerMap, paramValueMap, eFormLink, getOverrideDate(dateOverrideValue));
			}
			else
			{
				eForm = eFormService.saveNewEForm(eformTemplateId, demographicNo, providerNo, subject,
						formOpenerMap, paramValueMap, eFormLink, getOverrideDate(dateOverrideValue));
			}

			List<String> unmappedWarnings = eFormService.checkUnmappedMeasurements(eForm, paramValueMap);

			boolean sameForm = (eForm == null);
			String fdid = (sameForm) ? oldFormDataId : eForm.getId().toString();

			if(!sameForm)
			{
				LogAction.addLogEntry(providerNoStr, demographicNo, LogConst.ACTION_ADD, LogConst.CON_EFORM_DATA, LogConst.STATUS_SUCCESS,
						String.valueOf(eForm.getId()), loggedInInfo.getIp(), eForm.getFormName());
			}

			//post fdid to {eform_link} attribute
			if(!sameForm && eFormLink != null)
			{
				session.setAttribute(eFormLink, fdid);
			}
			if(fax)
			{
				request.setAttribute("fdid", fdid);
				return (mapping.findForward("fax"));
			}
			else if(print)
			{
				request.setAttribute("fdid", fdid);
				return (mapping.findForward("print"));
			}
			else if(!sameForm)
			{
				//write template message to echart
				String program_no = new EctProgram(session).getProgram(providerNoStr);
				String path = request.getRequestURL().toString();
				String uri = request.getRequestURI();
				path = path.substring(0, path.indexOf(uri));
				path += request.getContextPath();

				ArrayList<String> paramNames = new ArrayList<>(paramValueMap.keySet());
				ArrayList<String> paramValues = new ArrayList<>(paramValueMap.values());
				EForm curForm = new EForm(eForm);

				EFormUtil.writeEformTemplate(LoggedInInfo.getLoggedInInfoFromSession(request), paramNames, paramValues, curForm, fdid, program_no, path);
			}

			// If there were unmapped measurements, we want to redirect back to the main eForm page
			if (!unmappedWarnings.isEmpty())
			{
				request.setAttribute("measurements_unsaved", unmappedWarnings);
				return mapping.findForward("close");
			}

		}
		catch(EFormMeasurementException e)
		{
			// this is an expected error that happens when the measurement save fails. log as a warning
			logger.warn("Save aborted: Invalid measurement data");
			saveErrors(request, e.getErrors());
			request.setAttribute("curform", e.getEformData());
			request.setAttribute("page_errors", true);
			return mapping.getInputForward();
		}
		return(mapping.findForward("close"));
	}

	private Date getOverrideDate(String dateOverrideValue)
	{
		Date eformDate = new Date();
		if(dateOverrideValue != null && !dateOverrideValue.trim().isEmpty())
		{
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
				eformDate = format.parse(dateOverrideValue);
			}
			catch(ParseException e) {
				logger.error("Failed to parse eform date override string. current date (default) used.", e);
			}
		}

		return eformDate;
	}
}
