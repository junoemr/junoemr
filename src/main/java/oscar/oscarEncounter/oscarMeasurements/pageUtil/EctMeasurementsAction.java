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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;
import oscar.oscarEncounter.pageUtil.EctSessionBean;


public class EctMeasurementsAction extends Action {
	
	private static Logger logger = Logger.getLogger(EctMeasurementsAction.class);
	
	private static MeasurementDao measurementDao = (MeasurementDao) SpringUtils.getBean("measurementDao");

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        EctMeasurementsForm frm = (EctMeasurementsForm) form;

        HttpSession session = request.getSession();

        EctSessionBean bean = (EctSessionBean)session.getAttribute("EctSessionBean");


        String demographicNoStr = (String)frm.getValue("demographicNo");
        String providerNo = (String) session.getAttribute("user");
        	
        //if form has demo use it since session bean could have been overwritten
		if (demographicNoStr == null && bean != null) {
			logger.warn("Demographic Not in form getValue. value is " + demographicNoStr);
			demographicNoStr = bean.getDemographicNo();
		}
		Integer demographicNo = Integer.parseInt(demographicNoStr);
		if(demographicNo <= 0) {
			throw new IllegalArgumentException("Demographic Number Invalid: " + demographicNo);
		}

        String template = request.getParameter("template");
        MeasurementFlowSheet mFlowsheet = null;
        if (template != null){
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(session.getServletContext());
            FlowSheetCustomizationDao flowSheetCustomizationDao = (FlowSheetCustomizationDao) ctx.getBean("flowSheetCustomizationDao");
            MeasurementTemplateFlowSheetConfig templateConfig = MeasurementTemplateFlowSheetConfig.getInstance();

            List<FlowSheetCustomization> custList = flowSheetCustomizationDao.getFlowSheetCustomizations( template,(String) session.getAttribute("user"),demographicNoStr);
            mFlowsheet = templateConfig.getFlowSheet(template,custList);
        }

        String numType = (String) frm.getValue("numType");
        int iType = Integer.parseInt(numType);

        String textOnEncounter = "";

        //if parent window content has changed then we need to propagate change so we do not write to parent
        String parentChanged = (String)frm.getValue("parentChanged");
        request.setAttribute("parentChanged", parentChanged);

        boolean valid = true;
        try {

                EctValidation ectValidation = new EctValidation();
                ActionMessages errors = new ActionMessages();

                String inputValueName, inputTypeName, inputTypeDisplayName, mInstrcName, commentsName;
                String dateName,validationName, inputValue, inputType, inputTypeDisplay, mInstrc;
                String comments, dateObservedStr, validation;

                String regExp = null;
                double dMax = 0;
                double dMin = 0;
                int iMax = 0;
                int iMin = 0;

                ResultSet rs;
                //goes through each type to check if the input value is valid
                for(int i=0; i<iType; i++){
                    inputValueName = "inputValue-" + i;
                    inputTypeName = "inputType-" + i;
                    inputTypeDisplayName = "inputTypeDisplayName-" + i;
                    mInstrcName = "inputMInstrc-" + i;
                    commentsName = "comments-" + i;
                    dateName = "date-" + i;
                    inputValue = (String) frm.getValue(inputValueName);
                    inputType = (String) frm.getValue(inputTypeName);
                    inputTypeDisplay = (String) frm.getValue(inputTypeDisplayName);
                    mInstrc = (String) frm.getValue(mInstrcName);
                    comments = (String) frm.getValue(commentsName);
                    dateObservedStr = (String) frm.getValue(dateName);


                    regExp = null;
                    dMax = 0;
                    dMin = 0;
                    iMax = 0;
                    iMin = 0;

                    rs = ectValidation.getValidationType(inputType, mInstrc);
                    if (rs.next()){
                        dMax = rs.getDouble("maxValue");
                        dMin = rs.getDouble("minValue");
                        iMax = rs.getInt("maxLength");
                        iMin = rs.getInt("minLength");
                        regExp = oscar.Misc.getString(rs,"regularExp");
                    }
                    rs.close();

                    if(!ectValidation.isInRange(dMax, dMin, inputValue)){
                        errors.add(inputValueName, new ActionMessage("errors.range", inputTypeDisplay, Double.toString(dMin), Double.toString(dMax)));
                        saveErrors(request, errors);
                        valid = false;
                    }
                    if(!ectValidation.maxLength(iMax, inputValue)){
                        errors.add(inputValueName, new ActionMessage("errors.maxlength", inputTypeDisplay, Integer.toString(iMax)));
                        saveErrors(request, errors);
                        valid = false;
                    }
                    if(!ectValidation.minLength(iMin, inputValue)){
                        errors.add(inputValueName, new ActionMessage("errors.minlength", inputTypeDisplay, Integer.toString(iMin)));
                        saveErrors(request, errors);
                        valid = false;
                    }

                    if(!ectValidation.matchRegExp(regExp, inputValue)){
                        errors.add(inputValueName,
                        new ActionMessage("errors.invalid", inputTypeDisplay));
                        saveErrors(request, errors);
                        valid = false;
                    }
                    if(!ectValidation.isValidBloodPressure(regExp, inputValue)){
                        errors.add(inputValueName,
                        new ActionMessage("error.bloodPressure"));
                        saveErrors(request, errors);
                        valid = false;
                    }
                    if(!ectValidation.isDate(dateObservedStr)&&inputValue.compareTo("")!=0){
                        errors.add(dateName,
                        new ActionMessage("errors.invalidDate", inputTypeDisplay));
                        saveErrors(request, errors);
                        valid = false;
                    }
			}

			// Write to database and to encounter form if all the input values are valid
			if (valid) {
				for (int i = 0; i < iType; i++) {

					inputValueName = "inputValue-" + i;
					inputTypeName = "inputType-" + i;
					mInstrcName = "inputMInstrc-" + i;
					commentsName = "comments-" + i;
					validationName = "validation-" + i;
					dateName = "date-" + i;

					inputValue = (String) frm.getValue(inputValueName);
					inputType = (String) frm.getValue(inputTypeName);
					mInstrc = (String) frm.getValue(mInstrcName);
					comments = (String) frm.getValue(commentsName);
					comments = org.apache.commons.lang.StringEscapeUtils.escapeSql(comments);
					validation = (String) frm.getValue(validationName);
					dateObservedStr = (String) frm.getValue(dateName);

					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date dateObserved = format.parse(dateObservedStr);

					if (!GenericValidator.isBlankOrNull(inputValue)) {

						Measurement measurement = new Measurement();
						measurement.setType(inputType);
						measurement.setDemographicId(demographicNo);
						measurement.setProviderNo(providerNo);
						measurement.setDataField(inputValue);
						measurement.setMeasuringInstruction(mInstrc);
						measurement.setComments(comments);
						measurement.setDateObserved(dateObserved);
						
						List<Measurement> matches = measurementDao.findMatching(measurement);
						if(matches.isEmpty()) {
							measurementDao.persist(measurement);
						}

						if (mFlowsheet == null) {
							textOnEncounter = textOnEncounter + inputType + "    " + inputValue + " " + mInstrc + " " + comments + "\\n";
						}
						else {
							textOnEncounter += mFlowsheet.getFlowSheetItem(inputType).getDisplayName() + "    " + inputValue + " " + comments + "\\n";
						}
					}
				}
			}
			else {
				String groupName = (String) frm.getValue("groupName");
				String css = (String) frm.getValue("css");
				request.setAttribute("groupName", groupName);
				request.setAttribute("css", css);
				request.setAttribute("demographicNo", demographicNoStr);
				return (new ActionForward(mapping.getInput()));
			}
		}
		catch (SQLException e) {
			logger.error("Sql Error", e);
		}
		catch (ParseException e) {
			logger.error("Parse Error", e);
		}

		// put the inputvalue to the encounter form
		session.setAttribute("textOnEncounter", textOnEncounter);

		return mapping.findForward("success");
	}
}
