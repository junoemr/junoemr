/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.oscarehr.common.model.FlowSheetCustomization;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.common.model.Validations;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import oscar.oscarEncounter.oscarMeasurements.FlowSheetItem;
import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypeBeanHandler;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean;
import oscar.util.ConversionUtils;

import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.SecRole;

import oscar.oscarEncounter.data.EctProgram;

import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.apache.log4j.Logger;

public class FormUpdateAction extends Action {
	
	private static Logger log = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String date = request.getParameter("date");

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_measurement", "w", null))
		{
			throw new SecurityException("missing required security object (_measurement)");
		}
		
		String testOutput = "";
		String textOnEncounter = ""; // ********CDM Indicators Update******** \\n";
		boolean valid = true;
		boolean errorPage = false;
		boolean addToNote = false;
		// Juno & classic UI both hit this page, only one wants to be able to add *new* notes
		boolean addNewNote = ConversionUtils.fromBoolString(request.getParameter("addNewNote"));

		HttpSession session = request.getSession();

		String template = request.getParameter("template");	//"diab3";
		session.setAttribute("temp", template);
		String demographicNo = request.getParameter("demographic_no");
		String providerNo = (String) session.getAttribute("user");
		String note = "";
		String apptNo = (String) session.getAttribute("cur_appointment_no");
		int apptNoInt = 0;
		if(apptNo != null)
		{
			apptNoInt = Integer.parseInt(apptNo);
		}
		String userNo = (String) session.getAttribute("user");
		String programNo = new EctProgram(session).getProgram(userNo);

		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());

		FlowSheetCustomizationDao flowSheetCustomizationDao = (FlowSheetCustomizationDao) ctx.getBean("flowSheetCustomizationDao");

		List<FlowSheetCustomization> custList = flowSheetCustomizationDao.getFlowSheetCustomizations(template, providerNo, Integer.parseInt(demographicNo));

		MeasurementTemplateFlowSheetConfig templateConfig = MeasurementTemplateFlowSheetConfig.getInstance();
		MeasurementFlowSheet mFlowsheet = templateConfig.getFlowSheet(template, custList);

	    List<String> measurements = new ArrayList<>(mFlowsheet.getMeasurementList());
		EctMeasurementTypeBeanHandler mType = new EctMeasurementTypeBeanHandler();

		List<Measurement> measurementsToSave = new ArrayList<>();

		for (String measure : measurements)
		{
			Map<String, String> flowSheetInfo = mFlowsheet.getMeasurementFlowSheetInfo(measure);
	        FlowSheetItem item =  mFlowsheet.getFlowSheetItem(measure);
					
	        mFlowsheet.getMeasurementFlowSheetInfo(measure);
	        EctMeasurementTypesBean mtypeBean = mFlowsheet.getFlowsheetMeasurement(measure);
	        // the above can return null if the MeasurementFlowSheet didn't load the entries correctly
			if (mtypeBean == null)
			{
			   mtypeBean = mType.getMeasurementType(measure);
			}

			String name = flowSheetInfo.get("display_name").replaceAll("\\W","");
			String displayName = flowSheetInfo.get("display_name");

			if (request.getParameter(name) != null && !request.getParameter(name).isEmpty())
			{
				String comment = "";
				if (request.getParameter(name + "_comments") != null && !request.getParameter(name + "_comments").isEmpty())
				{
					comment = request.getParameter(name + "_comments");
				}

				if(request.getParameter(name + "_date") != null && !request.getParameter(name + "_date").isEmpty())
				{
					date=request.getParameter(name + "_date");
				}

				//create note text
				if(request.getParameter(name + "_note") != null && !request.getParameter(name + "_note").isEmpty())
				{
					addToNote = true;

					note = note + displayName + ": " + request.getParameter(name);
					if(name.equals("BP")){
						note = note + " " + mtypeBean.getMeasuringInstrc();
					}

					note = note + "\n Date Observed: " + date;
					if (request.getParameter(name + "_comments") != null && !request.getParameter(name + "_comments").isEmpty())
					{
						note = note + "\n comment: " + comment;
					}
					note = note +"\n\n ";
				}

				// For now, the validation and saving of measurements is split into two calls
				// It should have been to begin with,
				valid = isValidMeasurement(request, mtypeBean, item.getDisplayName(), request.getParameter(name), date);

				if (!valid)
				{
					testOutput += name + ": " + request.getParameter(name) + "\n";
					errorPage = true;
					log.error("ERROR: " + testOutput);
				}
				else
				{
					String savingProvider = (String) session.getAttribute("user");
					textOnEncounter += name + " " + request.getParameter(name) + "\\n";
					Measurement measurement = buildMeasurement(comment, request.getParameter(name), demographicNo, mtypeBean, date, apptNo, savingProvider);
					if (measurement != null)
					{
						measurementsToSave.add(measurement);
					}
				}
			}
		}

		// NOTE: This really should be moved down below mapping for the failure.
		// Unfortunately the Health Tracker is unable to actually render the errors that we've processed
		// because this is so tightly coupled to using an ActionMessages workflow.
		// Currently, this will take all measurements that we've confirmed are OK to save and save them.
		// If there are any invalid measurements, they will be silently dropped.
		MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");
		for (Measurement measurement : measurementsToSave)
		{
			measurementDao.persist(measurement);
		}

		if (errorPage)
		{
			request.setAttribute("testOutput", testOutput);
			return mapping.findForward("failure");
		}

		String submit = request.getParameter("submit");
		request.setAttribute("textOnEncounter", textOnEncounter);

		if (addToNote && addNewNote)
		{
			addNote(demographicNo, providerNo, programNo, note, apptNoInt, request);
		}

		if (submit == null  || "Save".equals(submit) || "Save All".equals(submit))
		{
			return mapping.findForward("reload");
		}

		return mapping.findForward("success");
	}

	public void addNote(String demographic_no, String providerNo, String prog_no, String note, int apptNoInt, HttpServletRequest request){
		HttpSession session = request.getSession();
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
		CaseManagementManager cmm = (CaseManagementManager) ctx.getBean("caseManagementManager");

		
		SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
		SecRole doctorRole = secRoleDao.findByName("doctor");
		String reporter_caisi_role=doctorRole.getId().toString();

		Date date = new Date();
		    
		CaseManagementNote cmn = new CaseManagementNote();
		cmn.setUpdate_date(date);
		cmn.setObservation_date(date);
		cmn.setDemographic_no(demographic_no);
		cmn.setProviderNo(providerNo);
		cmn.setNote(note);
		cmn.setSigned(true);
		cmn.setSigning_provider_no(providerNo);
		cmn.setProgram_no(prog_no);
		cmn.setReporter_caisi_role(reporter_caisi_role);

		cmn.setReporter_program_team("0");
		cmn.setPassword("NULL");
		cmn.setLocked(false);
		cmn.setHistory(note+"-----hi story----");
		cmn.setPosition(0);
		cmn.setAppointmentNo(apptNoInt);

		cmm.saveNoteSimple(cmn);
	}

	/**
	 * Given a potential measurement and augmenting information, determine whether it's good to save.
	 * @param request the HTTP request that started this whole thing
	 * @param mtypeBean info around the measurement type we want to record against
	 * @param displayName the front-facing measurement name, for error purposes
	 * @param value the value of the measurement we wanna add
	 * @param date associated date observed
	 * @return true if it's fine to add measurement, false otherwise
	 *
	 * Note this function has a side effect of recording into an ActionMessages object to return for displaying.
	 * This is highly coupled to the Classic UI currently...
	 */
	public boolean isValidMeasurement(HttpServletRequest request, EctMeasurementTypesBean mtypeBean, String displayName, String value, String date)
	{
		String mInstructions = StringUtils.trimToEmpty(mtypeBean.getMeasuringInstrc());
		String inputType = mtypeBean.getType();

		EctValidation ectValidation = new EctValidation();
		ActionMessages errors = new ActionMessages();

		String regExp = null;
		Double dMax = 0.0;
		Double dMin = 0.0;
		Integer iMax = 0;
		Integer iMin = 0;
		boolean isDate = false;

		List<Validations> validationsList = ectValidation.getValidationType(inputType, mInstructions);

		boolean valid = true;

		if (!validationsList.isEmpty()) {
			Validations validation = validationsList.iterator().next();
			dMax = validation.getMaxValue();
			dMin = validation.getMinValue();
			iMax = validation.getMaxLength();
			iMin = validation.getMinLength();
			regExp = validation.getRegularExp();
			isDate = validation.isDate() == null ? false : validation.isDate();
		}

		String inputTypeDisplay = mtypeBean.getTypeDisplayName();

		if (!ectValidation.isInRange(dMax, dMin, value))
		{
			errors.add(displayName, new ActionMessage("errors.range", inputTypeDisplay, Double.toString(dMin), Double.toString(dMax)));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.maxLength(iMax, value))
		{
			errors.add(displayName, new ActionMessage("errors.maxlength", inputTypeDisplay, Integer.toString(iMax)));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.minLength(iMin, value))
		{
			errors.add(displayName, new ActionMessage("errors.minlength", inputTypeDisplay, Integer.toString(iMin)));
			saveErrors(request, errors);
			valid = false;
		}

		if (!ectValidation.matchRegExp(regExp, value))
		{
			errors.add(displayName, new ActionMessage("errors.invalid", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.isValidBloodPressure(regExp, value))
		{
			errors.add(displayName, new ActionMessage("error.bloodPressure"));
			valid = false;
		}
		if (isDate && !ectValidation.isDate(value) && value.compareTo("") != 0)
		{
			errors.add(displayName, new ActionMessage("errors.invalidDate", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.isDate(date) && value.compareTo("") != 0)
		{
			errors.add("Date", new ActionMessage("errors.invalidDate", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}
		return valid;
	}

	/**
	 * Given a bunch of supporting information around a measurement, build a model that is ready for persistence.
	 * Procedure also checks to see if there is a duplicate with the same value+date.
	 * @param comments any comments associated with measurement
	 * @param inputValue the value of the measurement
	 * @param demographicNo demographic this is being recorded for
	 * @param mtypeBean contains stuff relating to the measurement type
	 * @param dateObserved the date when the measurement was observed
	 * @param apptNo an associated appointment, if we have one
	 * @param providerNo whichever provider recorded this
	 * @return a Measurement model if it will be a new measurement (not blank, null, or already recorded), null otherwise
	 */
	public Measurement buildMeasurement(String comments, String inputValue, String demographicNo, EctMeasurementTypesBean mtypeBean, String dateObserved, String apptNo, String providerNo)
	{
		String mInstructions = StringUtils.trimToEmpty(mtypeBean.getMeasuringInstrc());
		String inputType = mtypeBean.getType();
		comments = org.apache.commons.lang.StringEscapeUtils.escapeSql(comments);
		if (!GenericValidator.isBlankOrNull(inputValue))
		{
			Measurement measurement = new Measurement();
			measurement.setDemographicId(Integer.parseInt(demographicNo));
			measurement.setDataField(inputValue);
			measurement.setMeasuringInstruction(mInstructions);
			if (comments.isEmpty())
			{
				comments = " ";
			}
			measurement.setComments(comments);
			measurement.setDateObserved(ConversionUtils.fromDateString(dateObserved));
			measurement.setType(inputType);
			if (apptNo != null)
			{
				measurement.setAppointmentNo(Integer.parseInt(apptNo));
			}
			else
			{
				measurement.setAppointmentNo(0);
			}
			measurement.setProviderNo(providerNo);

			//Find if the same data has already been entered into the system
			MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");
			List<Measurement> measurements = measurementDao.findMatching(measurement);

			if (measurements.size() == 0)
			{
				return measurement;
			}
		}

		// Either the measurement is blank, null, or already recorded
		return null;
	}
}