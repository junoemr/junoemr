/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.oscarEncounter.oscarMeasurements.pageUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
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
import org.oscarehr.measurements.service.FlowsheetService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.oscarMeasurements.FlowSheetItem;
import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypeBeanHandler;
import oscar.oscarEncounter.oscarMeasurements.bean.EctMeasurementTypesBean;
import oscar.util.ConversionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormUpdateAction extends Action {
	
	private static Logger log = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private FlowsheetService flowsheetService = SpringUtils.getBean(FlowsheetService.class);
	
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

		HttpSession session = request.getSession();

		String temp = request.getParameter("template");	//"diab3";
		session.setAttribute("temp", temp);
		String demographic_no = request.getParameter("demographic_no");
		String providerNo = (String) session.getAttribute("user");
		String note = "";
		String apptNo = (String) session.getAttribute("cur_appointment_no");
		int apptNoInt=0;
		if(apptNo!=null){
		apptNoInt = Integer.parseInt(apptNo);
		}
		String user_no = (String) session.getAttribute("user");
		String prog_no = new EctProgram(session).getProgram(user_no);

		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());

		FlowSheetCustomizationDao flowSheetCustomizationDao = (FlowSheetCustomizationDao) ctx.getBean("flowSheetCustomizationDao");

		List<FlowSheetCustomization> custList = flowSheetCustomizationDao.getFlowSheetCustomizations(temp, providerNo, Integer.parseInt(demographic_no));

		MeasurementFlowSheet mFlowsheet = flowsheetService.getCustomizedFlowsheet(temp, custList);

		//List<MeasurementTemplateFlowSheetConfig.Node> nodes = mFlowsheet.getItemHeirarchy();
	    
	    List<String> measurementLs = mFlowsheet.getMeasurementList();
	    ArrayList<String> measurements = new ArrayList(measurementLs);

		EctMeasurementTypeBeanHandler mType = new EctMeasurementTypeBeanHandler();

		//FlowSheetItem item;
		//String measure;

		/*for (int i = 0; i < nodes.size(); i++) {
			MeasurementTemplateFlowSheetConfig.Node node = nodes.get(i);

			for (int j = 0; j < node.children.size(); j++) {
				MeasurementTemplateFlowSheetConfig.Node child = node.children.get(j);
				if (child.children == null && child.flowSheetItem != null) {*/
		
			
		for (String measure:measurements)
		{
			Map<String, String> h2 = mFlowsheet.getMeasurementFlowSheetInfo(measure);
	        FlowSheetItem item =  mFlowsheet.getFlowSheetItem(measure);
					
	               mFlowsheet.getMeasurementFlowSheetInfo(measure);
	               EctMeasurementTypesBean mtypeBean = mFlowsheet.getFlowsheetMeasurement(measure);
				   // the above can return null if the MeasurementFlowSheet didn't load the entries correctly
				   if (mtypeBean == null)
				   {
					   mtypeBean = mType.getMeasurementType(measure);
				   }
					String name = h2.get("display_name").toString().replaceAll("\\W","");
					String displayName=h2.get("display_name").toString();
							
					if (request.getParameter(name) != null && !request.getParameter(name).equals(""))
					{

						String comment = "";
						if (request.getParameter(name + "_comments") != null && !request.getParameter(name + "_comments").equals(""))
						{
							comment = request.getParameter(name + "_comments");
						}
						
						if(request.getParameter(name + "_date") !=null && !request.getParameter(name + "_date").equals(""))
						{
							date=request.getParameter(name + "_date");
						}
						
						
							//create note text
							if(request.getParameter(name + "_note") !=null && !request.getParameter(name + "_note").equals(""))
							{
								addToNote = true;

								note = note + displayName + ": " + request.getParameter(name);
								if(name.equals("BP")){
									note = note + " " + mtypeBean.getMeasuringInstrc();
								}
										
								note = note + "\n Date Observed: " + date;
								if (request.getParameter(name + "_comments") != null && !request.getParameter(name + "_comments").equals(""))
								{
									note = note + "\n comment: " + comment;
								}
								note=note +"\n\n ";
							}
							
							
						
						valid = doInput(item, mtypeBean, mFlowsheet, mtypeBean.getType(), StringUtils.trimToEmpty(mtypeBean.getMeasuringInstrc()), request.getParameter(name), comment, date, apptNo, request);
						
												
						if (!valid)
						{
							testOutput += name + ": " + request.getParameter(name) + "\n";
							errorPage = true;
							log.error("ERROR: " + testOutput);
						}
						else
						{
							textOnEncounter += name + " " + request.getParameter(name) + "\\n";
						}

					}/* why are comments being allowed here with an empty value?
					
					else if (request.getParameter(name) != null && request.getParameter(name + "_comments") != null && !request.getParameter(name + "_comments").equals("")) {
						String comment = request.getParameter(name + "_comments");
						if(request.getParameter(name + "_date") !=null && !request.getParameter(name + "_date").equals("")){
							date=request.getParameter(name + "_date");
						}
						
						doCommentInput(item, mtypeBean, mFlowsheet, mtypeBean.getType(), mtypeBean.getMeasuringInstrc(), comment, date, apptNo, request);
					}*/

				}
		
				
		//if (request.getParameter("ycoord") != null) {
		//	request.setAttribute("ycoord", request.getParameter("ycoord"));
		//}

		if (errorPage)
		{
			request.setAttribute("testOutput", testOutput);
			return mapping.findForward("failure");
		}

		String submit = request.getParameter("submit");
		request.setAttribute("textOnEncounter", textOnEncounter);

		if (submit == null  || "Save".equals(submit) || "Save All".equals(submit))
		{
			return mapping.findForward("reload");
		}

		return mapping.findForward("success");
	}

	public void doCommentInput(FlowSheetItem item, EctMeasurementTypesBean mtypeBean, MeasurementFlowSheet mFlowsheet, String inputType, String mInstructions, String comment, String date, String apptNo, HttpServletRequest request) {
		String demographicNo = request.getParameter("demographic_no");
		HttpSession session = request.getSession();
		String providerNo = (String) session.getAttribute("user");
		String comments = comment;
		
		MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");

		Measurement measurement = new Measurement();
		measurement.setDemographicId(Integer.parseInt(demographicNo));
		measurement.setDataField("");
		measurement.setMeasuringInstruction(mInstructions);
		measurement.setComments(comments);
		measurement.setDateObserved(ConversionUtils.fromDateString(date));
		measurement.setType(inputType);
		if (apptNo != null) {
			measurement.setAppointmentNo(Integer.parseInt(apptNo));
		} else {
			measurement.setAppointmentNo(0);
		}
		measurement.setProviderNo(providerNo);

		measurementDao.persist(measurement);

	}

	public boolean doInput(FlowSheetItem item, EctMeasurementTypesBean mtypeBean, MeasurementFlowSheet mFlowsheet, String inputType, String mInstructions, String value, String comment, String date, String apptNo, HttpServletRequest request) {
		EctValidation ectValidation = new EctValidation();
		ActionMessages errors = new ActionMessages();
		
		String demographicNo = request.getParameter("demographic_no");
		HttpSession session = request.getSession();
		String providerNo = (String) session.getAttribute("user");
		
		String regExp = null;
		Double dMax = 0.0;
		Double dMin = 0.0;
		Integer iMax = 0;
		Integer iMin = 0;
		Boolean isDate = false;

		List<Validations> vs = ectValidation.getValidationType(inputType, mInstructions);
		ectValidation.getRegCharacterExp();

		boolean valid = true;

		if (!vs.isEmpty()) {
			Validations v = vs.iterator().next();
			dMax = v.getMaxValue();
			dMin = v.getMinValue();
			iMax = v.getMaxLength();
			iMin = v.getMinLength();
			regExp = v.getRegularExp();
			isDate = v.isDate() == null ? false : v.isDate();
		}

		String inputTypeDisplay = mtypeBean.getTypeDisplayName();
		String inputValueName = item.getDisplayName();
		String inputValue = value;
		String comments = comment;
		String dateObserved = date;

		if (!ectValidation.isInRange(dMax, dMin, inputValue)) {
			errors.add(inputValueName, new ActionMessage("errors.range", inputTypeDisplay, Double.toString(dMin), Double.toString(dMax)));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.maxLength(iMax, inputValue)) {
			errors.add(inputValueName, new ActionMessage("errors.maxlength", inputTypeDisplay, Integer.toString(iMax)));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.minLength(iMin, inputValue)) {
			errors.add(inputValueName, new ActionMessage("errors.minlength", inputTypeDisplay, Integer.toString(iMin)));
			saveErrors(request, errors);
			valid = false;
		}

		if (!ectValidation.matchRegExp(regExp, inputValue)) {
			errors.add(inputValueName, new ActionMessage("errors.invalid", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.isValidBloodPressure(regExp, inputValue)) {
			errors.add(inputValueName, new ActionMessage("error.bloodPressure"));
			valid = false;
		}
		if (isDate && !ectValidation.isDate(inputValue) && inputValue.compareTo("") != 0) {
			errors.add(inputValueName, new ActionMessage("errors.invalidDate", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}
		if (!ectValidation.isDate(dateObserved) && inputValue.compareTo("") != 0) {
			errors.add("Date", new ActionMessage("errors.invalidDate", inputTypeDisplay));
			saveErrors(request, errors);
			valid = false;
		}

		if (valid) {
			comments = org.apache.commons.lang.StringEscapeUtils.escapeSql(comments);
			if (!GenericValidator.isBlankOrNull(inputValue)) {

				Measurement measurement = new Measurement();
				measurement.setDemographicId(Integer.parseInt(demographicNo));
				measurement.setDataField(inputValue);
				measurement.setMeasuringInstruction(mInstructions);
				if (comments.equals("")) {
					comments = " ";
				}
				measurement.setComments(comments);
				measurement.setDateObserved(ConversionUtils.fromDateString(dateObserved));
				measurement.setType(inputType);
				if (apptNo != null) {
					measurement.setAppointmentNo(Integer.parseInt(apptNo));
				} else {
					measurement.setAppointmentNo(0);
				}
				measurement.setProviderNo(providerNo);

				//Find if the same data has already been entered into the system
				MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");
				List<Measurement> measurements = measurementDao.findMatching(measurement);
				
				if (measurements.size() == 0) {
					//Write to the Database if all input values are valid
					measurementDao.persist(measurement);
				}
			}

		}

		return valid;
	}

}