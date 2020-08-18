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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.oscarehr.util.MiscUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreventionMapper extends AbstractMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private List<String> validPreventionTypes;
	private static Map<String, String> preventionTypeMap = new HashMap<>();

	public PreventionMapper(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep);
		this.validPreventionTypes = null;
	}

	public void setValidPreventionTypes(List<String> validPreventionTypes)
	{
		this.validPreventionTypes = validPreventionTypes;
	}

	public int getNumPreventions()
	{
		return provider.getIMMUNIZATIONReps();
	}

	public List<Prevention> getPreventionList() throws HL7Exception
	{
		int numPreventions = getNumPreventions();
		List<Prevention> preventionList = new ArrayList<>(numPreventions);
		for(int i=0; i< numPreventions; i++)
		{
			Prevention prevention = getPrevention(i);
			if(prevention != null)
			{
				preventionList.add(prevention);
			}
		}
		return preventionList;
	}

	public Prevention getPrevention(int rep) throws HL7Exception
	{
		Prevention prevention = new Prevention();
		Date immunizationDate = getImmunizationDate(rep);

		prevention.setPreventionDate(immunizationDate);
		prevention.setLastUpdateDate(immunizationDate);
		prevention.setPreventionType(getTranslatedVaccineCode(rep));
		prevention.setRefused(false);
		prevention.setNever(false);

		// set the original data in the 'comment' extension
		PreventionExt commentExt = new PreventionExt();
		commentExt.setPrevention(prevention);
		commentExt.setCommentKeyValue(getVaccineCode(rep));
		prevention.addExtension(commentExt);

		return prevention;
	}

	public Date getImmunizationDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getIMMUNIZATION(rep).getZIM()
				.getZim2_immunizationDate().getTs1_TimeOfAnEvent().getValue());
	}

	public String getVaccineCode(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getIMMUNIZATION(rep).getZIM().getZim3_vaccineCode().getValue());
	}

	public String getTranslatedVaccineCode(int rep) throws HL7Exception
	{
		String typeCode = getVaccineCode(rep);

		// according to the CoPD spec, the hl7 should contain a correct code that can be directly added to the database
		// however we have found that we need to map some values to codes sometimes
		if(validPreventionTypes.contains(typeCode))
		{
			return typeCode;
		}
		if(typeCode != null && preventionTypeMap.containsKey(typeCode.toUpperCase()))
		{
			String mapValue = preventionTypeMap.get(typeCode.toUpperCase());
			logger.warn("Invalid vaccine code '" + typeCode + "' was mapped to '" + mapValue + "'");
			if(!validPreventionTypes.contains(mapValue))
			{
				logger.warn("Mapped vaccine code value does not exist in oscar's preventions. default value used");
				return "OtherA";
			}
			return mapValue;
		}
		logger.error("Invalid or unknown vaccine code: [" + typeCode + "] defaulting to generic \'OtherA\'");
		return "OtherA";// default to generic 'other' type
	}

	static
	{
		// unofficial mapping of values we have encountered during imports
		preventionTypeMap.put("Adacel".toUpperCase(), 				"dTap");
		preventionTypeMap.put("Adacel/Polio".toUpperCase(), 		"Tdap-IPV");
		preventionTypeMap.put("Adacel/Polio (Quad)".toUpperCase(), 	"Tdap-IPV");
		preventionTypeMap.put("Agriflu".toUpperCase(), 				"Flu");
		preventionTypeMap.put("Agriflu 2017/2018".toUpperCase(), 	"Flu");
		preventionTypeMap.put("Allergy".toUpperCase(), 				"Other A");
		preventionTypeMap.put("arepanix".toUpperCase(),             "H1N1");
		preventionTypeMap.put("Arepanrix H1N1".toUpperCase(), 		"H1N1");
		preventionTypeMap.put("Arepanrix (H1N1)".toUpperCase(),     "H1N1");
		preventionTypeMap.put("Avaxim".toUpperCase(), 				"HepA");
		preventionTypeMap.put("Bacillus, Calmette-Guerin (BCG)".toUpperCase(), "BCG");
		preventionTypeMap.put("BOOSTRIX- POLIO".toUpperCase(), 		"dTap");
		preventionTypeMap.put("CHI".toUpperCase(), 				    "CHOLERA");
		preventionTypeMap.put("Cholera".toUpperCase(), 				"CHOLERA");
		preventionTypeMap.put("Diphtheria/Pertussis/Tetanus".toUpperCase(), "DTwP");
		preventionTypeMap.put("Diptheria/Tetanus Toxoid".toUpperCase(), "Td");
		preventionTypeMap.put("Dipth/Tet/Acel/Pertussis".toUpperCase(), "DTaP");
		preventionTypeMap.put("DIPTH/WPERT/TET/POLIO".toUpperCase(), "DTwP-IPV");
		preventionTypeMap.put("Dipth/Tet/Acel/Pertussis/Polio".toUpperCase(), "DTaP-IPV");
		preventionTypeMap.put("Dipth/Tet/Acel/Pertussis/Polio/HIB".toUpperCase(), "DTaP-IPV-HB");
		preventionTypeMap.put("dPT (Tdap)".toUpperCase(), 			"dTap");
		preventionTypeMap.put("DPT".toUpperCase(), 					"dTap");
		preventionTypeMap.put("DPTP".toUpperCase(), 				"DTaP-IPV");
		preventionTypeMap.put("dPT/Polio/HIB".toUpperCase(), 		"TdP-IPV-Hib");
		preventionTypeMap.put("dPT/Polio".toUpperCase(), 			"DT-IPV");
		preventionTypeMap.put("DPV".toUpperCase(),                  "Other A");
		preventionTypeMap.put("dt".toUpperCase(),                   "Td");
		preventionTypeMap.put("dTap-IPV-Hib".toUpperCase(),         "DTaP-IPV-Hib");
		preventionTypeMap.put("dTap tetanus".toUpperCase(),         "dTap");
		preventionTypeMap.put("dTap tetanus-diphteria-pertussis".toUpperCase(), "dTap");
		preventionTypeMap.put("d-Tap-IPV".toUpperCase(), 			"Tdap-IPV");
		preventionTypeMap.put("Engerix B".toUpperCase(), 			"HepB");
		preventionTypeMap.put("ENGERIX-B".toUpperCase(), 			"HepB");
		preventionTypeMap.put("Fluad".toUpperCase(), 				"Flu");
		preventionTypeMap.put("FluLaval".toUpperCase(), 			"Flu");
		preventionTypeMap.put("FluLaval TETRA (2017-2018)".toUpperCase(), "Flu");
		preventionTypeMap.put("flumist".toUpperCase(), 				"Flu");
		preventionTypeMap.put("FluMist 2015-2016".toUpperCase(), 	"Flu");
		preventionTypeMap.put("FluMist Quadrivalent (2017-2018)".toUpperCase(), "Flu");
		preventionTypeMap.put("FLUVIRAL".toUpperCase(), 			"Flu");
		preventionTypeMap.put("Fluviral 2015-2016".toUpperCase(), 	"Flu");
		preventionTypeMap.put("Fluviral (2017-2018)".toUpperCase(), "Flu");
		preventionTypeMap.put("fluzone".toUpperCase(), 				"Flu");
		preventionTypeMap.put("FluZone 2015-2016".toUpperCase(), 	"Flu");
		preventionTypeMap.put("flu".toUpperCase(), 					"Flu");
		preventionTypeMap.put("flu shot".toUpperCase(), 			"Flu");
		preventionTypeMap.put("Flu Vaccine".toUpperCase(), 			"Flu");
		preventionTypeMap.put("Flu Viral".toUpperCase(), 			"Flu");
		preventionTypeMap.put("GARDASIL".toUpperCase(), 			"HPV Vaccine");
		preventionTypeMap.put("garasil".toUpperCase(), 				"HPV Vaccine");
		preventionTypeMap.put("Guardasil".toUpperCase(), 			"HPV Vaccine");
		preventionTypeMap.put("HABV".toUpperCase(), 			    "HepAB");
		preventionTypeMap.put("Haemophilus b Conjugate".toUpperCase(), "Hib");
		preventionTypeMap.put("Haemoph B (Lederle-Praxis)".toUpperCase(), "Hib");
		preventionTypeMap.put("haemophilus influenzae type b".toUpperCase(), "Hib");
		preventionTypeMap.put("Haemoph Influenza B Polysac Conj".toUpperCase(), "Hib");
		preventionTypeMap.put("Haemoph Influenza B".toUpperCase(), "Hib");
		preventionTypeMap.put("HAVRIX 1440".toUpperCase(), 			"HepA");
		preventionTypeMap.put("Havrix 720".toUpperCase(), 			"HepA");
		preventionTypeMap.put("Havrix-A 1440/ml".toUpperCase(), 	"HepA");
		preventionTypeMap.put("HA-Typh-1".toUpperCase(), 			"HA-Typh-I");
		preventionTypeMap.put("Hboc".toUpperCase(), 	            "Hib");
		preventionTypeMap.put("HBV".toUpperCase(), 	                "HepB");
		preventionTypeMap.put("Hepatitis A".toUpperCase(), 			"HepA");
		preventionTypeMap.put("Hepatitis A Vaccine Inactivated".toUpperCase(), "HepA");
		preventionTypeMap.put("Hepatitis A & B".toUpperCase(), 		"HepAB");
		preventionTypeMap.put("Hepatitis B".toUpperCase(), 			"HepB");
		preventionTypeMap.put("Hepatitis B Recombinant".toUpperCase(), "HepB");
		preventionTypeMap.put("Hep A and Hep B Combined".toUpperCase(), "HepAB");
		preventionTypeMap.put("Hep A vaccine".toUpperCase(), 		"HepA");
		preventionTypeMap.put("Hep B".toUpperCase(),                "HepB");
		preventionTypeMap.put("HEXA-VALENT".toUpperCase(), 			"Dtap-IPV-Hib-HB");
		preventionTypeMap.put("HPV".toUpperCase(), 					"HPV Vaccine");
		preventionTypeMap.put("HPV vaccine for types 16 & 18".toUpperCase(), "HPV Vaccine");
		preventionTypeMap.put("H1N1 adjuvanted".toUpperCase(), 		"H1N1");
		preventionTypeMap.put("H1N1-09-AD".toUpperCase(), 		    "H1N1");
		preventionTypeMap.put("IG".toUpperCase(), 		            "OtherA");
		preventionTypeMap.put("Immune Globulin".toUpperCase(), 		"OtherA");
		preventionTypeMap.put("INFANRIX IPV".toUpperCase(), 		"DTap-IPV");
		preventionTypeMap.put("INFANRIX IPV+HIB".toUpperCase(), 	"DTaP-IPV-Hib");
		preventionTypeMap.put("Infanrix".toUpperCase(), 			"DTaP");
		preventionTypeMap.put("Influenza".toUpperCase(), 			"Flu");
		preventionTypeMap.put("Influenza Virus".toUpperCase(), 		"Flu");
		preventionTypeMap.put("IPV-Dtap".toUpperCase(), 		    "Tdap-IPV");
		preventionTypeMap.put("Japanese encephalitis".toUpperCase(), "JE");
		preventionTypeMap.put("JEV".toUpperCase(),                  "JE");
		preventionTypeMap.put("Killed Measles".toUpperCase(),       "M");
		preventionTypeMap.put("Lifesty".toUpperCase(),              "Other A");
		preventionTypeMap.put("mantoux".toUpperCase(),              "Tuberculosis");
		preventionTypeMap.put("Measles, Mumps & Rubella".toUpperCase(), "MMR");
		preventionTypeMap.put("Measles (Red)".toUpperCase(),        "M");
		preventionTypeMap.put("MenconC".toUpperCase(),              "MenC-C");
		preventionTypeMap.put("MenC-ACYW".toUpperCase(), 		    "Men-C-ACYW-135");
		preventionTypeMap.put("MeninAC".toUpperCase(), 		        "Men-P-AC");
		preventionTypeMap.put("MENINGC".toUpperCase(), 		        "MenC-C");
		preventionTypeMap.put("Meningococcal A,C,Y,W135".toUpperCase(), "Men-C-ACYW-135");
		preventionTypeMap.put("Meningococcal A,C,Y,W135 CON DIP Toxoid".toUpperCase(), "Men-C-ACYW-135");
		preventionTypeMap.put("Meningococcal CONJ MONO C".toUpperCase(), "MenC-C");
		preventionTypeMap.put("Meningococcal C Conjugate".toUpperCase(), "MenC-C");
		preventionTypeMap.put("meningococcal C conjugate".toUpperCase(), "MenC-C");
		preventionTypeMap.put("Meningococcal C-TT Conjugate".toUpperCase(), "MenconC");
		preventionTypeMap.put("Meningococcal Group B".toUpperCase(), "rMenB");
		preventionTypeMap.put("Meningococcal Polysac 23".toUpperCase(), "Men-P-AC");
		preventionTypeMap.put("Meningococcal Polysac A,C".toUpperCase(), "Men-P-AC");
		preventionTypeMap.put("MMR/Varicella".toUpperCase(), 		"MMRV");
		preventionTypeMap.put("MMR".toUpperCase(), 				    "MMR");
		preventionTypeMap.put("MMR-Var".toUpperCase(), 				"MMRV");
		preventionTypeMap.put("MMR II".toUpperCase(), 				"MMR");
		preventionTypeMap.put("mumps".toUpperCase(), 				"Other A");
		preventionTypeMap.put("null".toUpperCase(), 				"Other A");
		preventionTypeMap.put("NEIS VAC".toUpperCase(), 			"MenC-C");
		preventionTypeMap.put("OPV".toUpperCase(), 					"OtherA");
		preventionTypeMap.put("Osler".toUpperCase(), 				"OtherA");
		preventionTypeMap.put("Pediacel".toUpperCase(), 			"DTaP-IPV-Hib");
		preventionTypeMap.put("penta".toUpperCase(), 				"DPTP-IPV-Hib");
		preventionTypeMap.put("Pentacel".toUpperCase(), 			"DTaP-IPV-Hib");
		preventionTypeMap.put("PENTAVALENT".toUpperCase(), 			"DTaP-IPV-Hib");
		preventionTypeMap.put("PNEU C".toUpperCase(), 			    "Pneu-C");
		preventionTypeMap.put("PNEU-CON".toUpperCase(), 			"Pneu-C");
		preventionTypeMap.put("PNEU-C13".toUpperCase(), 		    "Pneu-C");
		preventionTypeMap.put("PNEU-P-23".toUpperCase(), 			"Pneumovax");
		preventionTypeMap.put("pneaumovax 23".toUpperCase(), 		"Pneumovax");
		preventionTypeMap.put("Pneumococcal".toUpperCase(), 		"Pneumovax");
		preventionTypeMap.put("Pneumococcal 7-Conjugate".toUpperCase(), "PNEU-C7");
		preventionTypeMap.put("Pneumococcal 13-Conjugate".toUpperCase(), "PNEU-C");
		preventionTypeMap.put("Pneumococcal 13-valent Conjugate Vaccine".toUpperCase(), "Pneu-C");
		preventionTypeMap.put("Pneumococcal 23-Polyvalent".toUpperCase(), "Pneumovax");
		preventionTypeMap.put("pneumo 23".toUpperCase(), 		    "Pneumovax");
		preventionTypeMap.put("pneumoccal".toUpperCase(), 		    "Pneu-C");
		preventionTypeMap.put("pneumo-p".toUpperCase(), 		    "Pneu-C");
		preventionTypeMap.put("pneumovax".toUpperCase(), 		    "Pneumovax");
		preventionTypeMap.put("Pneumovax 23".toUpperCase(), 		"Pneumovax");
		preventionTypeMap.put("Polio Inactivated".toUpperCase(), 	"IPV");
		preventionTypeMap.put("Poliomyelitis Oral".toUpperCase(), 	"Other A");
		preventionTypeMap.put("Polio Injectable".toUpperCase(), 	"IPV");
		preventionTypeMap.put("Poliomyelitis Inactivated".toUpperCase(), "IPV");
		preventionTypeMap.put("PPD".toUpperCase(), 					"Tuberculosis");
		preventionTypeMap.put("PPD = TB Skin Test".toUpperCase(), 	"Tuberculosis");
		preventionTypeMap.put("Prevnar".toUpperCase(), 				"Pneu-C");
		preventionTypeMap.put("Prevnar 13".toUpperCase(), 			"Pneu-C");
		preventionTypeMap.put("PROQUAD".toUpperCase(), 				"MMRV");
		preventionTypeMap.put("PRPD".toUpperCase(), 				"Hib");
		preventionTypeMap.put("quad".toUpperCase(), 				"DPTP-IPV");
		preventionTypeMap.put("Quadracel".toUpperCase(), 			"Tdap-IPV");
		preventionTypeMap.put("QUADRIVALENT".toUpperCase(), 		"Flu");
		preventionTypeMap.put("quadrivalent HPV recombinant vaccine".toUpperCase(), "HPV Vaccine");
		preventionTypeMap.put("RAB".toUpperCase(), 				    "Rabies");
		preventionTypeMap.put("RotaTeq".toUpperCase(), 				"Rot");
		preventionTypeMap.put("rotovirus".toUpperCase(), 			"Rot");
		preventionTypeMap.put("ROTAVIRUS".toUpperCase(), 			"Rot");
		preventionTypeMap.put("Rotavirus Vaccine".toUpperCase(), 	"Rot");
		preventionTypeMap.put("Rubella".toUpperCase(), 	            "Other A");
		preventionTypeMap.put("shingles".toUpperCase(), 			"HZV");
		preventionTypeMap.put("Shingrix".toUpperCase(), 			"HZV");
		preventionTypeMap.put("smallpox".toUpperCase(), 			"Other A");
		preventionTypeMap.put("TB skin test".toUpperCase(), 		"Tuberculosis");
		preventionTypeMap.put("Td Adsorbed".toUpperCase(), 			"Td");
		preventionTypeMap.put("Td adsorbed".toUpperCase(), 			"Td");
		preventionTypeMap.put("Tdap".toUpperCase(), 				"dTap");
		preventionTypeMap.put("tetanus".toUpperCase(),              "T");
		preventionTypeMap.put("Tetanus/Diphtheria".toUpperCase(),   "Td");
		preventionTypeMap.put("Tetanus/Diphtheria/Polio".toUpperCase(), "Td-IPV");
		preventionTypeMap.put("Tetanus/Diphtheria Toxoid".toUpperCase(), "Td");
		preventionTypeMap.put("Tetanus/Diphtheria/Toxoid".toUpperCase(), "Td");
		preventionTypeMap.put("Tetanus & Diphtheria toxoids".toUpperCase(), "Td");
		preventionTypeMap.put("Tetanus-diptheria".toUpperCase(), 	"Td");
		preventionTypeMap.put("Tetanus toxoid".toUpperCase(),       "T");
		preventionTypeMap.put("TG".toUpperCase(),                   "Other A");
		preventionTypeMap.put("TP".toUpperCase(),                   "Other A");
		preventionTypeMap.put("TT".toUpperCase(),                   "T");
		preventionTypeMap.put("Tuberculin - PPD".toUpperCase(),     "Tuberculosis");
		preventionTypeMap.put("Twinrix".toUpperCase(), 				"HepAB");
		preventionTypeMap.put("TYPHERIX".toUpperCase(), 			"Thyphoid");
		preventionTypeMap.put("TYO".toUpperCase(),                  "Typh-O");
		preventionTypeMap.put("Typhoid Vaccine".toUpperCase(), 		"Typhoid");
		preventionTypeMap.put("typhoid VI polysaccharide".toUpperCase(), "Typhoid");
		preventionTypeMap.put("TYVI".toUpperCase(),                 "Typhoid");
		preventionTypeMap.put("Unknown".toUpperCase(),              "Other A");
		preventionTypeMap.put("Varicella".toUpperCase(), 			"VZ");
		preventionTypeMap.put("Varicella Zoster".toUpperCase(), 	"VZ");
		preventionTypeMap.put("varivax".toUpperCase(), 				"VZ");
		preventionTypeMap.put("Vaxigrip 2012-2013".toUpperCase(), 	"Flu");
		preventionTypeMap.put("Vivaxim".toUpperCase(), 				"HA-Typh-I");
		preventionTypeMap.put("VZIG".toUpperCase(),                 "VS");
		preventionTypeMap.put("Yellow Fever Vaccine".toUpperCase(), "YF");
		preventionTypeMap.put("zostavax".toUpperCase(), 			"Zostavax");
		preventionTypeMap.put("zoster".toUpperCase(), 				"HZV");
		preventionTypeMap.put("zoster vaccine".toUpperCase(), 		"HZV");
	}
}
