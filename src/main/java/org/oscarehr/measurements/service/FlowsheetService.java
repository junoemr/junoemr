/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.measurements.service;

import org.oscarehr.common.model.FlowSheetCustomization;
import org.oscarehr.measurements.dao.FlowSheetUserCreatedDao;
import org.oscarehr.measurements.dao.FlowsheetDao;
import org.oscarehr.measurements.model.FlowSheetUserCreated;
import org.oscarehr.measurements.model.Flowsheet;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarEncounter.oscarMeasurements.FlowSheetItem;
import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;
import oscar.oscarEncounter.oscarMeasurements.MeasurementTemplateFlowSheetConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("measurements.service.FlowsheetService")
public class FlowsheetService
{

	@Autowired
	private FlowsheetDao flowsheetDao;
	@Autowired
	private FlowSheetUserCreatedDao flowSheetUserCreatedDao;

	public MeasurementFlowSheet getFlowsheetTemplate(String name)
	{
		List<MeasurementFlowSheet> flowsheetTemplates = getFlowsheetTemplates();
		for (MeasurementFlowSheet flowsheetTemplate : flowsheetTemplates)
		{
			if (flowsheetTemplate.getName().equals(name))
			{
				return flowsheetTemplate;
			}
		}
		return null;
	}

	public List<MeasurementFlowSheet> getFlowsheetTemplates()
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		return config.getFlowsheetTemplates();
	}

	public List<Flowsheet> getSystemFlowsheets()
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		return config.getSystemFlowsheets();
	}

	public List<FlowSheetUserCreated> getUserCreatedFlowsheets()
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		return config.getUserCreatedFlowsheets();
	}

	/**
	 * Given a list of dxCodes, find the flowsheets that would be available for those codes.
	 * @param dxCodes list of dxCodes we want to get flowsheets for
	 * @return a list of flowsheets names that are available for the given dxCodes
	 */
	public List<String> getFlowsheetNamesFromDxCodes(List<String> dxCodes)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();

		List<String> dxFlowsheets = new ArrayList<>();
		List<String> dxTriggers = config.getDxTriggers();
		for (String dx : dxTriggers)
		{
			if (dxCodes.contains(dx) && !dxFlowsheets.contains(dx))
			{
				List<String> flowsheets = config.getFlowsheetForDxCode(dx);
				for (String flowsheet : flowsheets)
				{
					if (!dxFlowsheets.contains(flowsheet))
					{
						dxFlowsheets.add(flowsheet);
					}
				}
			}
		}
		return dxFlowsheets;
	}

	/**
	 * Given a list of program names, return the flowsheets that would be available with those flowsheets.
	 * @param programs program names we want to get flowsheets for
	 * @return a list of flowsheet names that would be available when using given programs
	 */
	public List<String> getFlowsheetNamesFromProgram(List<String> programs)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		List<String> alist = new ArrayList<>();
		List<String> programTriggers = config.getProgramTriggers();

		for (String programId : programTriggers)
		{
			if (programs.contains(programId) && !alist.contains(programId))
			{
				List<String> flowsheets = config.getFlowsheetForProgramId(programId);
				for (String flowsheet : flowsheets)
				{
					if (!alist.contains(flowsheet))
					{
						alist.add(flowsheet);
					}
				}
			}
		}
		return alist;
	}

	public List<String> getUniversalFlowsheetNames()
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		return config.getUniversalFlowSheets();
	}

	public String addFlowsheet(MeasurementFlowSheet measurementFlowSheet)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		if(measurementFlowSheet.getName() == null || measurementFlowSheet.getName().isEmpty())
		{
			measurementFlowSheet.setName("U" + (config.getNumCachedFlowsheets() + 1));
		}

		config.cacheFlowsheet(measurementFlowSheet.getName(), measurementFlowSheet);
		return measurementFlowSheet.getName();
	}

	/**
	 * Given the name for a flowsheet that is currently disabled, enable it.
	 * This method handles both system flowsheets and user-created flowsheets.
	 * @param flowsheetName name of the flowsheet to enable
	 */
	public void enableFlowsheet(String flowsheetName)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		FlowSheetUserCreated flowSheetUserCreated = flowSheetUserCreatedDao.findByName(flowsheetName);
		if (flowSheetUserCreated != null)
		{
			unarchiveUserFlowSheet(flowSheetUserCreated);
			// After enabling it, instead of reloading all entries only refresh the affected entry
			flowSheetUserCreated = flowSheetUserCreatedDao.findByName(flowsheetName);
			config.updateCache(flowsheetName, flowSheetUserCreated);
		}
		else
		{
			flowsheetDao.enableFlowsheet(flowsheetName);
			// After enabling it, instead of reloading all entries only refresh the affected entry
			Flowsheet flowsheet = flowsheetDao.findByName(flowsheetName);
			config.updateCache(flowsheetName, flowsheet);
		}
	}

	/**
	 * Given the name of a flowsheet that is currently enabled, disable it.
	 * This method handles both system flowsheets and user-created flowsheets.
	 * @param flowsheetName name of the flowsheet to disable
	 */
	public void disableFlowsheet(String flowsheetName)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		FlowSheetUserCreated flowSheetUserCreated = flowSheetUserCreatedDao.findByName(flowsheetName);
		if (flowSheetUserCreated != null)
		{
			archiveUserFlowSheet(flowSheetUserCreated);
			// After disabling it, instead of reloading all entries only refresh the affected entry
			flowSheetUserCreated = flowSheetUserCreatedDao.findByName(flowsheetName);
			config.updateCache(flowsheetName, flowSheetUserCreated);
		}
		else
		{
			flowsheetDao.disableFlowsheet(flowsheetName);
			// After disabling it, instead of reloading all entries only refresh the affected entry
			Flowsheet affectedFlowsheet = flowsheetDao.findByName(flowsheetName);
			config.updateCache(flowsheetName, affectedFlowsheet);
		}
	}


	/**
	 * Create a custom flowsheet given all of the required parameters.
	 * @param name Internal name for the flowsheet
	 * @param dxCodeTriggers set of ICD9 codes of which any can trigger visibility for a demographic
	 * @param displayName user-friendly name for the flowsheet
	 * @param warningColour custom colour to set for warning
	 * @param recommendationColour custom colour to set for recommendation
	 */
	public void createUserFlowSheet(String name,
									String dxCodeTriggers,
									String displayName,
									String warningColour,
									String recommendationColour)
	{
		FlowSheetUserCreated flowSheetUserCreated = new FlowSheetUserCreated();
		flowSheetUserCreated.setName(name);
		flowSheetUserCreated.setDxcodeTriggers(dxCodeTriggers);
		flowSheetUserCreated.setDisplayName(displayName);
		flowSheetUserCreated.setWarningColour(warningColour);
		flowSheetUserCreated.setRecommendationColour(recommendationColour);
		flowSheetUserCreated.setCreatedDate(new Date());
		flowSheetUserCreated.setArchived(false);

		flowSheetUserCreatedDao.persist(flowSheetUserCreated);
	}

	/**
	 * Given a user created flowsheet that's currently active, archive it.
	 * @param flowSheetUserCreated user-created flowsheet to archive
	 */
	public void archiveUserFlowSheet(FlowSheetUserCreated flowSheetUserCreated)
	{
		flowSheetUserCreated.setArchived(true);
		flowSheetUserCreatedDao.merge(flowSheetUserCreated);
	}

	/**
	 * Given a user created flowsheet that's been archived, unarchive it.
	 * @param flowSheetUserCreated user-created flowsheet to unarchive
	 */
	public void unarchiveUserFlowSheet(FlowSheetUserCreated flowSheetUserCreated)
	{
		flowSheetUserCreated.setArchived(false);
		flowSheetUserCreatedDao.merge(flowSheetUserCreated);
	}

	public MeasurementFlowSheet getCustomizedFlowsheet(String name, List<FlowSheetCustomization> customizations)
	{
		MeasurementTemplateFlowSheetConfig config = MeasurementTemplateFlowSheetConfig.getInstance();
		MeasurementFlowSheet baseFlowsheet = getFlowsheetTemplate(name);
		if (customizations.size() == 0)
		{
			return baseFlowsheet;
		}

		try
		{
			MeasurementFlowSheet personalizedFlowsheet =  config.makeNewFlowsheet(baseFlowsheet);

			for (FlowSheetCustomization customization : customizations)
			{
				if (FlowSheetCustomization.ADD.equals(customization.getAction())
						|| FlowSheetCustomization.UPDATE.equals(customization.getAction()))
				{
					FlowSheetItem item = config.getItemFromString(customization.getPayload());
					item = personalizedFlowsheet.setMeasurementRuleBase(item);
					personalizedFlowsheet.addAfter(customization.getMeasurement(), item);
				}
				else if(FlowSheetCustomization.DELETE.equals(customization.getAction()))
				{
					personalizedFlowsheet.setToHidden(customization.getMeasurement());
				}
			}
			personalizedFlowsheet.loadRuleBase();
			return personalizedFlowsheet;
		}
		catch (IOException e)
		{
			MiscUtils.getLogger().error("Error when attempting to customize flowsheet: ", e);
		}

		return baseFlowsheet;
	}
}
