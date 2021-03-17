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


package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.oscarehr.common.dao.FlowSheetCustomizationDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.MeasurementDao.SearchCriteria;
import org.oscarehr.common.model.FlowSheetCustomization;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.Validations;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;
import oscar.util.ConversionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


public class EctMeasurementsAction extends Action
{

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private EncounterNoteService encounterNoteService = SpringUtils.getBean(EncounterNoteService.class);
	private CaseManagementNoteDao caseManagementNoteDao = (CaseManagementNoteDao) SpringUtils.getBean("encounterNote.dao.CaseManagementNoteDao");

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_measurement", "w", null))
		{
			throw new SecurityException("missing required security object (_measurement)");
		}
		EctMeasurementsForm frm = (EctMeasurementsForm) form;
		HttpSession session = request.getSession();

		String demographicNo = request.getParameter("demographic_no");
		String providerNo = (String) session.getAttribute("user");
		String prog_no = new EctProgram(session).getProgram(providerNo);
		String noteUUID = StringUtils.trimToNull(request.getParameter("uuid"));
		String pasteEncounterNote = request.getParameter("pasteEncounterNote");

		String template = request.getParameter("template");
		MeasurementFlowSheet mFlowsheet = null;
		if(template != null)
		{
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
			FlowSheetCustomizationDao flowSheetCustomizationDao = (FlowSheetCustomizationDao) ctx.getBean("flowSheetCustomizationDao");
			MeasurementTemplateFlowSheetConfig templateConfig = MeasurementTemplateFlowSheetConfig.getInstance();

			List<FlowSheetCustomization> custList = flowSheetCustomizationDao.getFlowSheetCustomizations(template, (String) session.getAttribute("user"), Integer.parseInt(demographicNo));
			mFlowsheet = templateConfig.getFlowSheet(template, custList);
		}

		String numType = frm.getValue("numType");
		int iType = Integer.parseInt(numType);

		String textOnEncounter = ""; //"**"+StringUtils.rightPad(by,80,"*")+"\\n";

		//if parent window content has changed then we need to propagate change so
		//we do not write to parent
		String parentChanged = frm.getValue("parentChanged");
		request.setAttribute("parentChanged", parentChanged);

		boolean valid = true;


		EctValidation ectValidation = new EctValidation();
		ActionMessages errors = new ActionMessages();

		String inputValueName, inputTypeName, inputTypeDisplayName, mInstrcName, commentsName;
		String dateName, validationName, inputValue, inputType, inputTypeDisplay, mInstrc;
		String comments, dateObserved, validation;

		String regExp = null;
		Double dMax = new Double(0);
		Double dMin = new Double(0);
		Integer iMax = 0;
		Integer iMin = 0;
		boolean isADate = false;

		List<Validations> vs = null;
		String regCharExp;
		//goes through each type to check if the input value is valid
		for(int i = 0; i < iType; i++)
		{
			inputValueName = "inputValue-" + i;
			inputTypeName = "inputType-" + i;
			inputTypeDisplayName = "inputTypeDisplayName-" + i;
			mInstrcName = "inputMInstrc-" + i;
			commentsName = "comments-" + i;
			dateName = "date-" + i;
			inputValue = frm.getValue(inputValueName);
			inputType = frm.getValue(inputTypeName);
			inputTypeDisplay = frm.getValue(inputTypeDisplayName);
			mInstrc = frm.getValue(mInstrcName);
			comments = frm.getValue(commentsName);
			dateObserved = frm.getValue(dateName);


			regExp = null;
			dMax = new Double(0);
			dMin = new Double(0);
			iMax = 0;
			iMin = 0;
			isADate = false;

			vs = ectValidation.getValidationType(inputType, mInstrc);
			regCharExp = ectValidation.getRegCharacterExp();

			if(!vs.isEmpty())
			{
				Validations valType = vs.iterator().next();
				dMax = valType.getMaxValue();
				dMin = valType.getMinValue();
				iMax = valType.getMaxLength();
				iMin = valType.getMinLength();
				regExp = valType.getRegularExp();
				isADate = (valType.isDate() != null) && valType.isDate();

			}

			if(dMax == null)
			{
				dMax = new Double(0);
			}
			if(dMin == null)
			{
				dMin = new Double(0);
			}
			if(iMax == null)
			{
				iMax = 0;
			}
			if(iMin == null)
			{
				iMin = 0;
			}
			if(!ectValidation.isInRange(dMax, dMin, inputValue))
			{
				errors.add(inputValueName, new ActionMessage("errors.range", inputTypeDisplay, Double.toString(dMin), Double.toString(dMax)));
				saveErrors(request, errors);
				valid = false;
			}
			if(!ectValidation.maxLength(iMax, inputValue))
			{
				errors.add(inputValueName, new ActionMessage("errors.maxlength", inputTypeDisplay, Integer.toString(iMax)));
				saveErrors(request, errors);
				valid = false;
			}
			if(!ectValidation.minLength(iMin, inputValue))
			{
				errors.add(inputValueName, new ActionMessage("errors.minlength", inputTypeDisplay, Integer.toString(iMin)));
				saveErrors(request, errors);
				valid = false;
			}

			if(!ectValidation.matchRegExp(regExp, inputValue))
			{
				errors.add(inputValueName,
						new ActionMessage("errors.invalid", inputTypeDisplay));
				saveErrors(request, errors);
				valid = false;
			}
			if(!ectValidation.isValidBloodPressure(regExp, inputValue))
			{
				errors.add(inputValueName,
						new ActionMessage("error.bloodPressure"));
				saveErrors(request, errors);
				valid = false;
			}
			if ((isADate) && !(ectValidation.isDate(inputValue) || inputValue.isEmpty()))
			{
					errors.add(dateName,
							new ActionMessage("errors.invalidDate", inputTypeDisplay));
					saveErrors(request, errors);
					valid = false;
			}
		}

		//Write to database and to encounter form if all the input values are valid
		if(valid)
		{
			for(int i = 0; i < iType; i++)
			{
				inputValueName = "inputValue-" + i;
				inputTypeName = "inputType-" + i;
				mInstrcName = "inputMInstrc-" + i;
				commentsName = "comments-" + i;
				validationName = "validation-" + i;
				dateName = "date-" + i;

				inputValue = frm.getValue(inputValueName);
				inputType = frm.getValue(inputTypeName);
				mInstrc = frm.getValue(mInstrcName);
				comments = frm.getValue(commentsName);
				comments = org.apache.commons.lang.StringEscapeUtils.escapeSql(comments);
				validation = frm.getValue(validationName);
				dateObserved = frm.getValue(dateName);

				if(!GenericValidator.isBlankOrNull(inputValue))
				{

					//Find if the same data has already been entered into the system
					MeasurementDao dao = SpringUtils.getBean(MeasurementDao.class);
					SearchCriteria sc = new SearchCriteria();
					sc.setDemographicNo(demographicNo);
					sc.setDataField(inputValue);
					sc.setMeasuringInstrc(mInstrc);
					sc.setComments(comments);
					sc.setDateObserved(ConversionUtils.fromDateString(dateObserved));
					sc.setType(inputType);
					List<Measurement> ms = dao.find(sc);

					if(ms.isEmpty())
					{
						//Write to the Dababase if all input values are valid
						Measurement m = new Measurement();
						m.setType(inputType);
						m.setDemographicId(Integer.parseInt(demographicNo));
						m.setProviderNo(providerNo);
						m.setDataField(inputValue);
						m.setMeasuringInstruction(mInstrc);
						m.setComments(comments);
						m.setDateObserved(ConversionUtils.fromDateString(dateObserved));
						m.setAppointmentNo(0);
						dao.persist(m);

						//prepare input values for writing to the encounter form
						if(mFlowsheet == null)
						{
							textOnEncounter = textOnEncounter + inputType + "    " + inputValue + " " + mInstrc + " " + comments + "\n";
						}
						else
						{
							textOnEncounter += mFlowsheet.getFlowSheetItem(inputType).getDisplayName() + "    " + inputValue + " " + comments + "\n";
						}
					}
				}

			}
			// textOnEncounter = textOnEncounter + "**********************************************************************************\\n";
		}
		else
		{
			String groupName = frm.getValue("groupName");
			String css = frm.getValue("css");
			request.setAttribute("groupName", groupName);
			request.setAttribute("css", css);
			request.setAttribute("demographicNo", demographicNo);
			return (new ActionForward(mapping.getInput()));
		}

		/* save a new note OR append encounter text to the note if a matching uuid exists */
		if (pasteEncounterNote == null || !pasteEncounterNote.equals("true"))
		{
			CaseManagementNote existingNote = (noteUUID != null) ? caseManagementNoteDao.findLatestByUUID(noteUUID) : null;
			if (existingNote != null)
			{
				encounterNoteService.appendTextToNote(existingNote, textOnEncounter, providerNo, Integer.parseInt(demographicNo));
			}
			else
			{
				encounterNoteService.addNewNoteWithUUID(noteUUID, textOnEncounter, providerNo, Integer.parseInt(demographicNo));
			}
		}

		request.setAttribute("textOnEncounter", StringEscapeUtils.escapeJavaScript(textOnEncounter));
		return mapping.findForward("success");
	}
}
