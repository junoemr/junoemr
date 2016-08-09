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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;
import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.oscarEncounter.oscarMeasurements.data.MeasurementTypes;

public class EctAddMeasurementTypeAction extends Action {

	static Logger log = MiscUtils.getLogger();

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		log.info("ADD MEASURMENT TYPE");

		EctAddMeasurementTypeForm frm = (EctAddMeasurementTypeForm) form;
		MeasurementTypeDao measurementTypeDao = (MeasurementTypeDao) SpringUtils.getBean("measurementTypeDao");
		MessageResources mr = getResources(request);

		request.getSession().setAttribute("EctAddMeasurementTypeForm", frm);

		List<String> messages = new LinkedList<String>();

		try {

			String type = frm.getType().trim();
			String typeDesc = frm.getTypeDesc().trim();
			String typeDisplayName = frm.getTypeDisplayName().trim();
			String measuringInstrc = frm.getMeasuringInstrc().trim();
			String validation = frm.getValidation().trim();
			
			if (!allInputIsValid(request, measurementTypeDao, type, typeDesc, typeDisplayName, measuringInstrc)) {
				return (new ActionForward(mapping.getInput()));
			}

			// Write to database
			measurementTypeDao.saveNewMeasurementType(type, typeDesc, typeDisplayName, measuringInstrc, validation);
			log.info("Measurement type added successfully");
		}
		catch (Exception e) {
			MiscUtils.getLogger().error("Error", e);
		}

		messages.add(mr.getMessage("oscarEncounter.oscarMeasurements.AddMeasurementType.successful", "!"));
		request.setAttribute("messages", messages);
		//TODO -- This is here for the benefit of other places using it, and should be removed.
		MeasurementTypes.getInstance().reInit();
		return mapping.findForward("success");
	}
	
	private boolean allInputIsValid(HttpServletRequest request, MeasurementTypeDao measurementTypeDao, String type, String typeDesc, String typeDisplayName,
			String measuringInstrc) {

		ActionMessages errors = new ActionMessages();
		
		/* -- verify type -- */
		String errorField = "The type " + type;

		if (!GenericValidator.maxLength(type, 4)) {
            errors.add(type, new ActionMessage("errors.maxlength", errorField, "4"));
            saveErrors(request, errors);
            return false;
    	}
    	else if (!GenericValidator.minLength(type, 1)) {
            errors.add(type, new ActionMessage("errors.minlength", errorField, "1"));
            saveErrors(request, errors);
            return false;
    	}
		// check database for duplicates by type
		if(measurementTypeDao.isDuplicate(type)) {
			errors.add(type, new ActionMessage("error.oscarEncounter.Measurements.duplicateTypeName"));
			saveErrors(request, errors);
			return false;
		}
		
    	/* -- verify type description -- */
    	errorField = "The type description " + typeDesc;
    	if (!GenericValidator.maxLength(typeDesc, 255)) {
            errors.add(typeDesc, new ActionMessage("errors.maxlength", errorField, "255"));
            saveErrors(request, errors);
            return false;
    	}
    	else if (!GenericValidator.minLength(typeDesc, 1)) {
            errors.add(typeDesc, new ActionMessage("errors.minlength", errorField, "1"));
            saveErrors(request, errors);
            return false;
    	}
    	
    	/* -- verify type display name -- */
    	errorField = "The type display name " + typeDisplayName;
    	if (!GenericValidator.maxLength(typeDisplayName, 255)) {
            errors.add(typeDisplayName, new ActionMessage("errors.maxlength", errorField, "255"));
            saveErrors(request, errors);
            return false;
    	}
    	else if (!GenericValidator.minLength(typeDisplayName, 1)) {
            errors.add(typeDisplayName, new ActionMessage("errors.minlength", errorField, "1"));
            saveErrors(request, errors);
            return false;
    	}
    	
    	/* -- verify type measuring instruction -- */
    	errorField = "The measuring instruction " + measuringInstrc;
    	if (!GenericValidator.maxLength(measuringInstrc, 255)) {
            errors.add(measuringInstrc, new ActionMessage("errors.maxlength", errorField, "255"));
            saveErrors(request, errors);
            return false;
    	}
    	else if (!GenericValidator.minLength(measuringInstrc, 1)) {
            errors.add(measuringInstrc, new ActionMessage("errors.minlength", errorField, "1"));
            saveErrors(request, errors);
            return false;
    	}
		return true;
	}
}
