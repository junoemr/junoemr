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


package oscar.oscarRx.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.caisi_integrator.ws.CachedDemographicAllergy;
import org.oscarehr.common.dao.AllergyDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DiseasesDao;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Diseases;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;


public class RxPatientData {
	private static Logger logger = MiscUtils.getLogger();
        private static final DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");	

	private RxPatientData() {
		// prevent instantiation
	}

	/* Patient Search */

	public static Patient[] PatientSearch(String surname, String firstName) {

            Patient[] arr = {};	
            List<Patient> patients = new ArrayList<Patient>();
            List<Demographic> demographics = demographicDao.searchDemographic(surname + "," + firstName);     
            for (Demographic demographic : demographics) {
                Patient p = new Patient(demographic);
                patients.add(p);
            }
            return patients.toArray(arr);                
	}

	/* Patient Information */

	public static Patient getPatient(int demographicNo) {
            Demographic demographic = demographicDao.getDemographicById(demographicNo);	
            return new Patient(demographic);		
	}

	public static Patient getPatient(String demographicNo) {	
            Demographic demographic = demographicDao.getDemographicById(Integer.parseInt(demographicNo));
            return new Patient(demographic);
	}

	private static java.util.Date calcDate(String year, String month, String day) {
		if (StringUtils.isBlank(year) || StringUtils.isBlank(month) || StringUtils.isBlank(day)) return null;
		if (!NumberUtils.isDigits(year) || !NumberUtils.isDigits(month) || !NumberUtils.isDigits(day)) return null;

		int iYear = Integer.parseInt(year);
		int iMonth = Integer.parseInt(month) - 1;
		int iDay = Integer.parseInt(day);

		GregorianCalendar ret = new GregorianCalendar(iYear, iMonth, iDay);
		return ret.getTime();
	}

	private static int calcAge(java.util.Date DOB) {
		if (DOB == null) return 0;

		GregorianCalendar now = new GregorianCalendar();
		int curYear = now.get(Calendar.YEAR);
		int curMonth = (now.get(Calendar.MONTH) + 1);
		int curDay = now.get(Calendar.DAY_OF_MONTH);

		Calendar cal = new GregorianCalendar();
		cal.setTime(DOB);
		int iYear = cal.get(Calendar.YEAR);
		int iMonth = (cal.get(Calendar.MONTH) + 1);
		int iDay = cal.get(Calendar.DAY_OF_MONTH);
		int age = 0;

		if (curMonth > iMonth || (curMonth == iMonth && curDay >= iDay)) {
			age = curYear - iYear;
		} else {
			age = curYear - iYear - 1;
		}

		return age;
	}

	public static class Patient implements Serializable {
		private Demographic demographic = null;
		private static AllergyDao allergyDao = (AllergyDao) SpringUtils.getBean("allergyDao");
		private PartialDateDao partialDateDao = (PartialDateDao) SpringUtils.getBean("partialDateDao");
                
                public Patient (Demographic demographic) {
                    this.demographic = demographic;
                    
                     if (demographic == null)
                            MiscUtils.getLogger().warn("Demographic is not set!");
                }
                		
                public Demographic getDemographic() {
                    return this.demographic;
                }
                
		public int getDemographicNo() {
                    if (demographic != null) {
			return demographic.getDemographicNo();
                    }else {
                        MiscUtils.getLogger().warn("DemographicNo is not set!");
                        return -1;
                    }
		}

		public String getSurname() {
                    if (demographic != null)
			return demographic.getLastName();
                    else
                        return "";
		}

		public String getFirstName() {
                    if (demographic != null)
			return demographic.getFirstName();
                    else
                        return "";
		}

		public String getSex() {
                    if (demographic != null)
			return demographic.getSex();
                    else
                        return "";
		}

		public String getHin() {
                    if (demographic != null)
			return demographic.getHin();
                    else
                        return "";
		}

		public java.util.Date getDOB() {
                    Date dob = null;
                    if (demographic != null)
                        dob=demographic.getBirthDay().getTime();
			                    
                        return dob;
		}

		public int getAge() {
			return calcAge(this.getDOB());
		}

		public String getAddress() {
			if (demographic != null)
			return demographic.getAddress();
                    else
                        return "";
		}

		public String getCity() {
                    if (demographic != null)
			return demographic.getCity();
                    else
                        return "";
		}

		public String getProvince() {
                    if (demographic != null)
			return demographic.getProvince();
                    else
                        return "";
		}

		public String getPostal() {
                    if (demographic != null)
			return demographic.getPostal();
                    else
                        return "";
		}

		public String getPhone() {
                    if (demographic != null)
			return demographic.getPhone();
                    else
                        return "";
		}

                public String getChartNo() {
                    if (demographic != null)
			return demographic.getChartNo();
                    else
                        return "";
                }
                
		public org.oscarehr.common.model.Allergy getAllergy(int id) {

			// I know none of this method makes sense, but I'm only converting this to JPA right now, too much work to fix it all to make sense.
			org.oscarehr.common.model.Allergy allergy = allergyDao.find(id);

			return allergy;
		}

		public org.oscarehr.common.model.Allergy[] getAllergies() {
			ArrayList<org.oscarehr.common.model.Allergy> results = new ArrayList<org.oscarehr.common.model.Allergy>();
                        Integer demographicNo = getDemographicNo();
			List<org.oscarehr.common.model.Allergy> allergies = allergyDao.findAllergies(demographicNo);
			results.addAll(allergies);

			LoggedInInfo loggedInInfo = LoggedInInfo.loggedInInfo.get();
			if (loggedInInfo.currentFacility.isIntegratorEnabled()) {
				try {	
					List<CachedDemographicAllergy> remoteAllergies  = null;
					try {
						if (!CaisiIntegratorManager.isIntegratorOffline()){
							remoteAllergies = CaisiIntegratorManager.getDemographicWs().getLinkedCachedDemographicAllergies(demographicNo);
						}
					} catch (Exception e) {
						MiscUtils.getLogger().error("Unexpected error.", e);
						CaisiIntegratorManager.checkForConnectionError(e);
					}
					
					if(CaisiIntegratorManager.isIntegratorOffline()){
						remoteAllergies = IntegratorFallBackManager.getRemoteAllergies(demographicNo);	
					}

					for (CachedDemographicAllergy remoteAllergy : remoteAllergies) {
						Date date = null;
						if (remoteAllergy.getEntryDate() != null)
							date = remoteAllergy.getEntryDate().getTime();

						org.oscarehr.common.model.Allergy a = new org.oscarehr.common.model.Allergy();
						a.setDemographicNo(demographicNo);
						a.setId(remoteAllergy.getFacilityIdIntegerCompositePk().getCaisiItemId().intValue());
						a.setEntryDate(date);
						a.setDescription(remoteAllergy.getDescription());
						a.setHiclSeqno(remoteAllergy.getHiclSeqNo());
						a.setHicSeqno(remoteAllergy.getHicSeqNo());
						a.setAgcsp(remoteAllergy.getAgcsp());
						a.setAgccs(remoteAllergy.getAgccs());
						a.setTypeCode(remoteAllergy.getTypeCode());
						a.setIntegratorResult(true);
						a.setReaction(remoteAllergy.getReaction());

						if (remoteAllergy.getStartDate() != null)
							date = remoteAllergy.getStartDate().getTime();

						a.setStartDate(date);
						a.setAgeOfOnset(remoteAllergy.getAgeOfOnset());
						a.setSeverityOfReaction(remoteAllergy.getSeverityCode());
						a.setOnsetOfReaction(remoteAllergy.getOnSetCode());
						a.setRegionalIdentifier(remoteAllergy.getRegionalIdentifier());
						a.setLifeStage(remoteAllergy.getLifeStage());
						a.setDrugrefId(String.valueOf(remoteAllergy.getPickId()));

						results.add(a);
					}
				} catch (Exception e) {
					logger.error("error getting remote allergies", e);
				}
			}

			return (results.toArray(new org.oscarehr.common.model.Allergy[0]));
		}

		public org.oscarehr.common.model.Allergy[] getActiveAllergies() {
			List<org.oscarehr.common.model.Allergy> allergies = allergyDao.findActiveAllergies(getDemographicNo());
			return  allergies.toArray(new org.oscarehr.common.model.Allergy[allergies.size()]);
		}

		public org.oscarehr.common.model.Allergy addAllergy(java.util.Date entryDate, org.oscarehr.common.model.Allergy allergy) {

			allergy.setEntryDate(entryDate);
			allergyDao.persist(allergy);
			partialDateDao.setPartialDate(PartialDate.ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());
			return allergy;
		}

		private static boolean setAllergyArchive(int allergyId, String archiveString) {
			org.oscarehr.common.model.Allergy allergy=allergyDao.find(allergyId);
			if (allergy!=null)
			{
				allergy.setArchived(archiveString);
				allergyDao.merge(allergy);
				return(true);
			}

			return(false);
		}

		public boolean deleteAllergy(int allergyId) {
			return(setAllergyArchive(allergyId, "1"));
		}

		public boolean activateAllergy(int allergyId) {
			return(setAllergyArchive(allergyId, "0"));
		}

		public Diseases[] getDiseases() {
			DiseasesDao diseasesDao = SpringUtils.getBean(DiseasesDao.class);
			List<Diseases> diseases = diseasesDao.findByDemographicNo(getDemographicNo());
			return diseases.toArray(new Diseases[diseases.size()]);
		}

		public Diseases addDisease(String ICD9, java.util.Date entryDate)  {
			DiseasesDao diseasesDao = SpringUtils.getBean(DiseasesDao.class);
			Diseases disease = new Diseases();
			disease.setDemographicNo(getDemographicNo());
			disease.setIcd9Entry(ICD9);
			disease.setEntryDate(entryDate);
			diseasesDao.persist(disease);
			return disease;
		}

		public RxPrescriptionData.Prescription[] getPrescribedDrugsUnique() {
			return new RxPrescriptionData().getUniquePrescriptionsByPatient(this.getDemographicNo());
		}

		public RxPrescriptionData.Prescription[] getPrescribedDrugs() {
			return new RxPrescriptionData().getPrescriptionsByPatient(this.getDemographicNo());
		}

		public RxPrescriptionData.Prescription[] getPrescribedDrugScripts() {
			return new RxPrescriptionData().getPrescriptionScriptsByPatient(this.getDemographicNo());
		}


	}

}
