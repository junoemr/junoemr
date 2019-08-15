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

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.caisi_integrator.ws.CachedDemographicAllergy;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.common.dao.DiseasesDao;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Diseases;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

public class RxPatientData {
	private static Logger logger = MiscUtils.getLogger();
	private static final DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);

	private RxPatientData() {
		// prevent instantiation
	}

	/* Patient Search */

	public static Patient[] PatientSearch(LoggedInInfo loggedInInfo, String surname, String firstName) {

		Patient[] arr = {};
		List<Patient> patients = new ArrayList<Patient>();
		List<Demographic> demographics = demographicManager.searchDemographic(loggedInInfo, surname + "," + firstName);
		for (Demographic demographic : demographics) {
			Patient p = new Patient(demographic);
			patients.add(p);
		}
		return patients.toArray(arr);
	}

	/* Patient Information */

	public static Patient getPatient(LoggedInInfo loggedInInfo, int demographicNo) {
		Demographic demographic = demographicManager.getDemographic(loggedInInfo, demographicNo);
		return new Patient(demographic);
	}

	public static Patient getPatient(LoggedInInfo loggedInInfo, String demographicNo) {
		Demographic demographic = demographicManager.getDemographic(loggedInInfo,demographicNo);
		return new Patient(demographic);
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
		private transient PartialDateDao partialDateDao = null;

		public Patient(Demographic demographic) {
			this.demographic = demographic;

			if (demographic == null) MiscUtils.getLogger().warn("Demographic is not set!");
		}

		private PartialDateDao getPartialDateDao() {
			if(this.partialDateDao == null) {
				this.partialDateDao = (PartialDateDao) SpringUtils.getBean("partialDateDao");
			}
			return this.partialDateDao;
		}

		public Demographic getDemographic() {
			return this.demographic;
		}

		public int getDemographicNo() {
			if (demographic != null) {
				return demographic.getDemographicNo();
			} else {
				MiscUtils.getLogger().warn("DemographicNo is not set!");
				return -1;
			}
		}

		public String getSurname() {
			if (demographic != null) return demographic.getLastName();
			else return "";
		}

		public String getFirstName() {
			if (demographic != null) return demographic.getFirstName();
			else return "";
		}

		public String getSex() {
			if (demographic != null) return demographic.getSex();
			else return "";
		}

		public String getHin() {
			if (demographic != null) return demographic.getHin();
			else return "";
		}

		public String getVer()
		{
			if (demographic != null)
			{
				return demographic.getVer();
			}
			else
			{
				return "";
			}
		}

		public java.util.Date getDOB() {
			Date dob = null;
			if (demographic != null)
			{
				dob = demographic.getBirthDay().getTime();
			}

			return dob;
		}

		public int getAge() {
			return calcAge(this.getDOB());
		}

		public String getAddress() {
			if (demographic != null) return demographic.getAddress();
			else return "";
		}

		public String getCity() {
			if (demographic != null) return demographic.getCity();
			else return "";
		}

		public String getProvince() {
			if (demographic != null) return demographic.getProvince();
			else return "";
		}

		public String getPostal() {
			if (demographic != null) return demographic.getPostal();
			else return "";
		}

		public String getPhone() {
			if (demographic != null) return demographic.getPhone();
			else return "";
		}

		public String getChartNo() {
			if (demographic != null) return demographic.getChartNo();
			else return "";
		}

		public Allergy getAllergy(int id) {

			// I know none of this method makes sense, but I'm only converting this to JPA right now, too much work to fix it all to make sense.
			Allergy allergy = allergyDao.find(id);

			return allergy;
		}

		public Allergy[] getAllergies(LoggedInInfo loggedInInfo) {
			ArrayList<Allergy> results = new ArrayList<Allergy>();
			Integer demographicNo = getDemographicNo();
			List<Allergy> allergies = allergyDao.findAllergies(demographicNo);
			results.addAll(allergies);

			if (loggedInInfo.getCurrentFacility().isIntegratorEnabled()) {
				try {
					List<CachedDemographicAllergy> remoteAllergies = null;
					try {
						if (!CaisiIntegratorManager.isIntegratorOffline(loggedInInfo.getSession())) {
							remoteAllergies = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility()).getLinkedCachedDemographicAllergies(demographicNo);
						}
					} catch (Exception e) {
						MiscUtils.getLogger().error("Unexpected error.", e);
						CaisiIntegratorManager.checkForConnectionError(loggedInInfo.getSession(), e);
					}

					if (CaisiIntegratorManager.isIntegratorOffline(loggedInInfo.getSession())) {
						remoteAllergies = IntegratorFallBackManager.getRemoteAllergies(loggedInInfo, demographicNo);
					}

					for (CachedDemographicAllergy remoteAllergy : remoteAllergies) {
						Date date = null;
						if (remoteAllergy.getEntryDate() != null) date = remoteAllergy.getEntryDate().getTime();

						Allergy a = new Allergy();
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

						if (remoteAllergy.getStartDate() != null) date = remoteAllergy.getStartDate().getTime();

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

			return (results.toArray(new Allergy[0]));
		}

		public Allergy[] getActiveAllergies() {
			List<Allergy> allergies = allergyDao.findActiveAllergies(getDemographicNo());
			return allergies.toArray(new Allergy[allergies.size()]);
		}

		public Allergy addAllergy(java.util.Date entryDate, Allergy allergy) {
			allergy.setEntryDate(entryDate);
			allergyDao.persist(allergy);
			this.getPartialDateDao().setPartialDate(PartialDate.ALLERGIES, allergy.getId(), PartialDate.ALLERGIES_STARTDATE, allergy.getStartDateFormat());
			return allergy;
		}

		private static boolean setAllergyArchive(int allergyId, boolean archive) {
			Allergy allergy = allergyDao.find(allergyId);
			if (allergy != null) {
				allergy.setArchived(archive);
				allergyDao.merge(allergy);
				return (true);
			}

			return (false);
		}

		public boolean deleteAllergy(int allergyId) {
			return (setAllergyArchive(allergyId, true));
		}

		public boolean activateAllergy(int allergyId) {
			return (setAllergyArchive(allergyId, false));
		}

		public Diseases[] getDiseases() {
			DiseasesDao diseasesDao = SpringUtils.getBean(DiseasesDao.class);
			List<Diseases> diseases = diseasesDao.findByDemographicNo(getDemographicNo());
			return diseases.toArray(new Diseases[diseases.size()]);
		}

		public Diseases addDisease(String ICD9, java.util.Date entryDate) {
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
