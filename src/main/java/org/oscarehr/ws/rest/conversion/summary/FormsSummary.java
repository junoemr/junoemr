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
package org.oscarehr.ws.rest.conversion.summary;


import java.util.Collections;
import java.util.List;

import org.oscarehr.eform.model.EFormData;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.oscarEncounter.data.EctFormData;
import oscar.util.ConversionUtils;


@Component
public class FormsSummary implements Summary{
	//private static Logger logger = MiscUtils.getLogger();
	
	@Autowired
	private FormsManager formsManager;// = SpringUtils.getBean(FormsManager.class);
    	
	public SummaryTo1 getSummary(LoggedInInfo loggedInInfo,Integer demographicNo,String summaryCode){
		
		SummaryTo1 summary = new SummaryTo1("Assessments",0,SummaryTo1.FORMS_CODE);
		
		List<SummaryItemTo1> list = summary.getSummaryItem();
		
	    fillEforms( loggedInInfo, list,demographicNo);
		list.sort(Collections.reverseOrder(SummaryItemTo1.SUMMARY_ITEM_TO_1_COMPARATOR_DATE));

		return summary;
	}
	
	
	
	private  void fillEforms(LoggedInInfo loggedInInfo,List<SummaryItemTo1> list,Integer demographicNo){

		List<EFormData> completedEforms = formsManager.findInstancedByDemographicId(loggedInInfo,demographicNo);
		List<EctFormData.PatientForm> completedForms = formsManager.getCompletedEncounterForms(demographicNo.toString());
			
		for(EFormData eformData: completedEforms){
			list.add(new SummaryItemTo1(eformData.getId(), eformData.getFormName(),"record.forms.completed","eform", ConversionUtils.combineDateAndTime(eformData.getFormDate(), eformData.getFormTime())));
		}

		for(EctFormData.PatientForm form : completedForms)
		{
			try
			{
				list.add(new SummaryItemTo1(Integer.parseInt(form.getFormId()), form.getFormName(), "record.forms.completed", "form", form.edited));
			}
			catch (NumberFormatException e)
			{
 				MiscUtils.getLogger().warn("Could not add from to summary list with error: " + e.toString(), e);
			}
		}
	}
	
}
