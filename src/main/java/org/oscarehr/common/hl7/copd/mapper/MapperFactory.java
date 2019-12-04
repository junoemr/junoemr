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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;

/**
 * constructs mappers based on import source setting.
 */
public class MapperFactory
{
	/**
	 * get new appointment mapper
	 * @param message - message to import
	 * @param importSource - source of import
	 * @return - new appointment mapper
	 */
	public static AppointmentMapper newAppointmentMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		switch(importSource)
		{
			case MEDACCESS:
			{
				return new AppointmentMapperMedaccess(message, importSource);
			}
			case ACCURO:
			{
				return new AppointmentMapperAccuro(message, importSource);
			}
			default:
			{
				return new AppointmentMapper(message, importSource);
			}
		}
	}

	/**
	 * new alert mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new alert mapper
	 */
	public static AlertMapper newAlertMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new AlertMapper(message, providerRep, importSource);
	}

	/**
	 * new allergy mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new allergy mapper
	 */
	public static AllergyMapper newAllergyMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new AllergyMapper(message, providerRep, importSource);
	}

	/**
	 * new demographic mapper
	 * @param message - message to import
	 * @param importSource - source of import
	 * @return - new demographic mapper
	 */
	public static DemographicMapper newDemographicMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new DemographicMapper(message, importSource);
	}

	/**
	 * new document mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new document mapper
	 */
	public static DocumentMapper newDocumentMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		switch (importSource)
		{
			case MEDACCESS:
				return new DocumentMapperMedaccess(message, providerRep, importSource);
			default:
				return new DocumentMapper(message, providerRep, importSource);
		}
	}

	/**
	 * new DX mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @return - new DX mapper
	 */
	public static DxMapper newDxMapper(ZPD_ZTR message, int providerRep)
	{
		return new DxMapper(message, providerRep);
	}

	/**
	 * new Encounter mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new Encounter mapper
	 */
	public static EncounterNoteMapper newEncounterNoteMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		switch(importSource)
		{
			case MEDACCESS:
				return new EncounterNoteMapperMedAccess(message, providerRep, importSource);
			default:
				return new EncounterNoteMapper(message, providerRep, importSource);
		}
	}

	/**
	 * new history mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new history mapper
	 */
	public static HistoryNoteMapper newHistoryNoteMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		return new HistoryNoteMapper(message, providerRep, importSource);
	}

	/**
	 * new lab mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new lab mapper
	 */
	public static LabMapper newLabMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new LabMapper(message, providerRep, importSource);
	}

	/**
	 * new medication mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new medication mapper
	 */
	public static MedicationMapper newMedicationMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new MedicationMapper(message, providerRep, importSource);
	}

	/**
	 * new prevention mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @return - new prevention mapper
	 */
	public static PreventionMapper newPreventionMapper(ZPD_ZTR message, int providerRep)
	{
		return new PreventionMapper(message, providerRep);
	}

	/**
	 * new provider mapper
	 * @param message - message to import
	 * @return - new provider mapper
	 */
	public static ProviderMapper newProviderMapper(ZPD_ZTR message)
	{
		return new ProviderMapper(message);
	}

	/**
	 * new tickler mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new tickler mapper
	 */
	public static TicklerMapper newTicklerMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new TicklerMapper(message, providerRep, importSource);
	}

	/**
	 * new measurements mapper
	 * @param message - message to import
	 * @param providerRep - rep of provider
	 * @param importSource - source of import
	 * @return - new measurements mapper
	 */
	public static MeasurementsMapper newMeasurementsMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		return new MeasurementsMapper(message, providerRep, importSource);
	}
}
