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
package org.oscarehr.util.task;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.ZIPFile;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.dataMigration.service.PatientExportService;
import org.oscarehr.util.JunoCommandLineRunner;
import org.oscarehr.util.task.args.BooleanArg;
import org.oscarehr.util.task.args.CommandLineArg;
import org.oscarehr.util.task.args.IntegerArg;
import org.oscarehr.util.task.args.StringArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static oscar.util.ConversionUtils.DATE_TIME_FILENAME;

@Component
public class CommandLineExporter implements CommandLineTask
{
	private static final Logger logger = Logger.getLogger(JunoCommandLineRunner.class);

	@Autowired
	private PatientExportService patientExportService;

	public String taskName()
	{
		return "export";
	}
	public List<CommandLineArg<?>> argsList()
	{
		return Arrays.asList(
				new StringArg("type", null, true),
				new StringArg("patient-set", null, true),
				new StringArg("directory", "/tmp", false),
				new IntegerArg("thread-count", 1, false),
				new BooleanArg("zip-results", false, false),
				new BooleanArg("include-alerts", true, false),
				new BooleanArg("include-allergies", true, false),
				new BooleanArg("include-appointments", true, false),
				new BooleanArg("include-care-elements", true, false),
				new BooleanArg("include-notes", true, false),
				new BooleanArg("include-family-history", true, false),
				new BooleanArg("include-immunizations", true, false),
				new BooleanArg("include-labs", true, false),
				new BooleanArg("include-medications", true, false),
				new BooleanArg("include-past-health", true, false),
				new BooleanArg("include-personal-history", true, false),
				new BooleanArg("include-problems", true, false),
				new BooleanArg("include-reports", true, false),
				new BooleanArg("include-risk-factors", true, false)
		);
	}

	public void run(Map<String, CommandLineArg<?>> args)
	{
		logger.info("init exporter");

		String type = (String) args.get("type").getValue();
		String patientSet = (String) args.get("patient-set").getValue();
		String exportDirectory = (String) args.get("directory").getValue();
		Boolean zipResults = (Boolean) args.get("zip-results").getValue();

		if(!EnumUtils.isValidEnum(ImporterExporterFactory.EXPORTER_TYPE.class, type))
		{
			throw new InvalidCommandLineArgumentsException(type + " is not a valid EXPORT_TYPE enum. must be one of " +
					java.util.Arrays.asList(ImporterExporterFactory.EXPORTER_TYPE.values()));
		}
		ImporterExporterFactory.EXPORTER_TYPE exportType = ImporterExporterFactory.EXPORTER_TYPE.valueOf(type);

		ExportPreferences exportPreferences = new ExportPreferences();
		exportPreferences.setExportAlertsAndSpecialNeeds((Boolean) args.get("include-alerts").getValue());
		exportPreferences.setExportAllergiesAndAdverseReactions((Boolean) args.get("include-allergies").getValue());
		exportPreferences.setExportAppointments((Boolean) args.get("include-appointments").getValue());
		exportPreferences.setExportCareElements((Boolean) args.get("include-care-elements").getValue());
		exportPreferences.setExportClinicalNotes((Boolean) args.get("include-notes").getValue());
		exportPreferences.setExportFamilyHistory((Boolean) args.get("include-family-history").getValue());
		exportPreferences.setExportImmunizations((Boolean) args.get("include-immunizations").getValue());
		exportPreferences.setExportLaboratoryResults((Boolean) args.get("include-labs").getValue());
		exportPreferences.setExportMedicationsAndTreatments((Boolean) args.get("include-medications").getValue());
		exportPreferences.setExportPastHealth((Boolean) args.get("include-past-health").getValue());
		exportPreferences.setExportPersonalHistory((Boolean) args.get("include-personal-history").getValue());
		exportPreferences.setExportProblemList((Boolean) args.get("include-problems").getValue());
		exportPreferences.setExportReportsReceived((Boolean) args.get("include-reports").getValue());
		exportPreferences.setExportRiskFactors((Boolean) args.get("include-risk-factors").getValue());
		exportPreferences.setThreadCount((Integer) args.get("thread-count").getValue());
		exportPreferences.setExportDirectory(exportDirectory);

		logger.info("BEGIN EXPORT [ Patient Set: '" + patientSet + "']");
		try
		{
			String processId = UUID.randomUUID().toString();
			Thread.currentThread().setName(processId);

			if(zipResults)
			{
				ZIPFile zipFile = patientExportService.exportDemographicsToZip(patientSet,
						exportType, exportPreferences);

				String exportZipName = "export_" + ConversionUtils.toDateTimeString(LocalDateTime.now(), DATE_TIME_FILENAME) + "_" + patientSet + ".zip";
				zipFile.rename(ZIPFile.getSanitizedFileName(exportZipName));
				logger.info("Created zip file: " + zipFile.getPath());
			}
			else
			{
				List<GenericFile> exportFiles = patientExportService.exportDemographicsToList(patientSet,
						exportType, exportPreferences);
				logger.info("Exported files to " + exportFiles.get(0).getDirectory());
			}

		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		logger.info("EXPORT COMPLETED");
	}
}
