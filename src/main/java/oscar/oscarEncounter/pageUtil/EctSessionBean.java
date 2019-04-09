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


package oscar.oscarEncounter.pageUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.oscarehr.common.dao.EChartDao;
import org.oscarehr.common.dao.EncounterTemplateDao;
import org.oscarehr.common.dao.MeasurementGroupStyleDao;
import org.oscarehr.common.dao.MessageTblDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.EChart;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.MeasurementGroupStyle;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.SxmlMisc;
import oscar.oscarEncounter.oscarConsultation.data.EctConProviderData;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

@Deprecated
public class EctSessionBean implements java.io.Serializable {
    //data passed from the oscar appointment screen these members are constant for the duration of
    // a session
    public Date currentDate;
    public String eChartId;
    public Date eChartTimeStamp;
    public String providerNo;
    public String userName;
    public String demographicNo;
    public String appointmentNo;
    public String curProviderNo;
    public String reason;
    public String encType;
    public String appointmentDate;
    public String startTime;
    public String status;
    public String date;
    public String check;
    public String patientFirstName;
    public String patientLastName;
    public String patientSex;
    public String patientAge;
    public String chartNo;
    public String familyDoctorNo;
    public String monthOfBirth;
    public String yearOfBirth;
    public String dateOfBirth;
    public String address;
    public String city;
    public String postal;
    public String phone;
    public String roster;
    public String team = null;
    public String consultationRequestId = null;
    public String currentTeam = null;
    public String socialHistory;
    public String familyHistory;
    public String medicalHistory;
    public String ongoingConcerns;
    public String reminders;
    public String encounter;
    public String subject;
    public String template = "";
    public String oscarMsgID;
    public String oscarMsg = "";
    public String myoscarMsgId = null;
    public ArrayList<String> appointmentsIdArray;
    public ArrayList<String> appointmentsNamesArray;
    public ArrayList<String> templateNames;
    public ArrayList<String> measurementGroupNames;
    public String source;
    public String patientBirthdate;
    public String referringDoctorName;
    public String referringDoctorNumber;
    public Date rosterDate;

    public void resetAll() {
        eChartTimeStamp = null;
        eChartId = "";
        patientLastName = "";
        patientFirstName = "";
        yearOfBirth = "";
        monthOfBirth = "";
        dateOfBirth = "";
        patientBirthdate = "";
        patientSex = "";
        patientAge = "";
        chartNo = "";
        familyDoctorNo = "";
        socialHistory = "";
        familyHistory = "";
        medicalHistory = "";
        ongoingConcerns = "";
        reminders = "";
        encounter = "";
        subject = "";
        address = "";
        city = "";
        postal = "";
        phone = "";
        roster = "";
        template = "";
        oscarMsg = "";
        referringDoctorName = "";
        referringDoctorNumber = "";
        rosterDate = null;
    }

    /**
     * sets up the encounter page as befits entrance into the oscarEncounter module from the oscar
     * appointment scheduling screen
     */
    public void setUpEncounterPage(LoggedInInfo loggedInInfo) {
        resetAll();

        appointmentsIdArray = new ArrayList<String>();
        appointmentsNamesArray = new ArrayList<String>();
        templateNames = new ArrayList<String>();
        measurementGroupNames = new ArrayList<String>();

        setupDemographicInfo(loggedInInfo, demographicNo);

        OscarAppointmentDao apptDao = SpringUtils.getBean(OscarAppointmentDao.class);
        for(Appointment appt : apptDao.findByProviderAndDate(curProviderNo, ConversionUtils.fromDateString(appointmentDate))){
            appointmentsIdArray.add(appt.getId().toString());
            appointmentsNamesArray.add(appt.getName() + " " + ConversionUtils.toTimeString(appt.getStartTime()));
        }
        
        EncounterTemplateDao ectDao = SpringUtils.getBean(EncounterTemplateDao.class);
        for(EncounterTemplate ect : ectDao.findAll()) {
            templateNames.add(ect.getEncounterTemplateName());
        }
        
        MeasurementGroupStyleDao mgsDao = SpringUtils.getBean(MeasurementGroupStyleDao.class);
        for(MeasurementGroupStyle mgs : mgsDao.findAll()) {
            measurementGroupNames.add(mgs.getGroupName());
        }

        OscarProperties properties = OscarProperties.getInstance();
		if( !Boolean.parseBoolean(properties.getProperty("AbandonOldChart", "false"))) {
            setupOldEchart();
		}

        if (oscarMsgID != null) {
        	MessageTblDao mtDao = SpringUtils.getBean(MessageTblDao.class);
        	MessageTbl mt = mtDao.find(ConversionUtils.fromIntString(oscarMsgID));
            if (mt != null) {
                String message = mt.getMessage();
                String subject = mt.getSubject();
                String sentby = mt.getSentBy();
                String sentto = mt.getSentTo();
                String thetime = ConversionUtils.toTimeString(mt.getTime());
                String thedate = ConversionUtils.toDateString(mt.getDate());
                oscarMsg = "From: " + sentby + "\n" + "To: " + sentto + "\n" + "Date: " + thedate + " " + thetime
                        + "\n" + "Subject: " + subject + "\n" + message;
            }
        }

        if(myoscarMsgId != null){
        	oscarMsg = myoscarMsgId;
        }

    }

    /**
     * over loaded method sets up the encounter page as befits entrance from the select box of
     * today's appointments on the oscarEncounter.Index.jsp page
     *
     * @param appointmentNo
     */
    public void setUpEncounterPage(LoggedInInfo loggedInInfo, String appointmentNo) {
        resetAll();
        appointmentsIdArray = new ArrayList<String>();
        appointmentsNamesArray = new ArrayList<String>();
        templateNames = new ArrayList<String>();

        OscarAppointmentDao apptDao = SpringUtils.getBean(OscarAppointmentDao.class);
		Appointment appt = apptDao.find(ConversionUtils.fromIntString(appointmentNo));
		demographicNo = Integer.toString(appt.getDemographicNo());
		this.appointmentNo = appointmentNo;
		reason = appt.getReason();
		encType = new String("face to face encounter with client");
		appointmentDate = ConversionUtils.toDateString(appt.getAppointmentDate());
		startTime = ConversionUtils.toDateString(appt.getStartTime());
		status = appt.getStatus();
        
        for(Appointment a : apptDao.findByProviderAndDate(curProviderNo, appt.getAppointmentDate())){
            appointmentsIdArray.add(a.getId().toString());
            appointmentsNamesArray.add(a.getName() + " " + ConversionUtils.toTimeString(a.getStartTime()));
        }
        
        EncounterTemplateDao ectDao = SpringUtils.getBean(EncounterTemplateDao.class);
        for(EncounterTemplate ect : ectDao.findAll()) {
            templateNames.add(ect.getEncounterTemplateName());
        }
        
    	OscarProperties properties = OscarProperties.getInstance();
		if( !Boolean.parseBoolean(properties.getProperty("AbandonOldChart", "false"))) {
            setupOldEchart();
		}
      
        //apointmentsIdArray and the appointmentsNamesArray are
        //already set up so no need to get them again

        setupDemographicInfo(loggedInInfo, demographicNo);
    }

 /**
  * over loaded method sets up the encounter page for print
  * @param echartid
  * @param demographicNo
  */
    public void setUpEncounterPage(LoggedInInfo loggedInInfo, String echartid, String demographicNo) {
        resetAll();

        OscarProperties properties = OscarProperties.getInstance();
		if( !Boolean.parseBoolean(properties.getProperty("AbandonOldChart", "false"))) {
	        EChartDao ecDao = SpringUtils.getBean(EChartDao.class);
			EChart ec = ecDao.find(ConversionUtils.fromIntString(echartid));
			if (ec.getDemographicNo() != ConversionUtils.fromIntString(demographicNo)) {
				ec = null;
			}
			
			if (ec != null) {
	            eChartId = ec.getId().toString();
	            eChartTimeStamp =  ec.getTimestamp();
	            socialHistory = ec.getSocialHistory();
	            familyHistory = ec.getFamilyHistory();
	            medicalHistory = ec.getMedicalHistory();
	            ongoingConcerns = ec.getOngoingConcerns();
	            reminders = ec.getReminders();
	            encounter = ec.getEncounter();
	            subject = ec.getSubject();
	        }
		}

        this.demographicNo = demographicNo;
		setupDemographicInfo(loggedInInfo, demographicNo);
    }

    public String getTeam() {
        if (team == null) {
            //          oscar.oscarEncounter.oscarConsultation.data.ProviderData providerData;
            //          providerData = new oscar.oscarEncounter.oscarConsultation.data.ProviderData();
            EctConProviderData providerData = new EctConProviderData();
            team = providerData.getTeam(providerNo);
        }
        return team;
    }

    public void setDemographicNo(String str) {
        demographicNo = str;
    }

    public String getDemographicNo() {
        return demographicNo;
    }

    public String getCurProviderNo() {
        return curProviderNo;
    }

    public void setConsultationRequestId(String str) {
        consultationRequestId = str;
    }

    public String getConsultationRequestId() {
        return consultationRequestId;
    }

    public void unsetConsultationRequestId() {
        consultationRequestId = null;
    }

    public String getCurrentTeam() {
        if (currentTeam == null) {
            currentTeam = new String();
        }
        return currentTeam;
    }

    public void setCurrentTeam(String str) {
        currentTeam = str;
    }

    public void unsetCurrentTeam() {
        currentTeam = null;
    }

    public boolean isCurrentTeam() {
        boolean retval = true;
        if (currentTeam == null) {
            retval = false;
        }
        return retval;
    }

    public boolean isValid() {
        return demographicNo.length() > 0 && providerNo != null && providerNo.length() > 0;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public boolean hasRosterDate()
    {
        return rosterDate != null && !"".equals(rosterDate.toString());
    }

    private void setupDemographicInfo(LoggedInInfo loggedInInfo, String demoNo)
    {
        // Note: We do not set the demographicNo in this method because the natural overload to
        // SetupEncounterPage(LoggedInInfo, String) is already used with appointmentNo as a parameter.
        // Therefore, we assume that the demographic number is already set prior to calling this method.

        DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
        Demographic demographic = demographicManager.getDemographic(loggedInInfo, demoNo);

        patientLastName = demographic.getLastName();
        patientFirstName = demographic.getFirstName();
        address = demographic.getAddress();
        city = demographic.getCity();
        postal = demographic.getPostal();
        phone = demographic.getPhone();
        familyDoctorNo = demographic.getProviderNo();
        chartNo = demographic.getChartNo();
        yearOfBirth = demographic.getYearOfBirth();
        monthOfBirth = demographic.getMonthOfBirth();
        dateOfBirth = demographic.getDateOfBirth();
        roster = demographic.getRosterStatus();
        patientSex = demographic.getSex();
        rosterDate = demographic.getRosterDate();

        if (yearOfBirth == null || yearOfBirth.equals("null") || yearOfBirth.equals(""))
        {
            yearOfBirth = "0";
        }
        if (monthOfBirth == null || monthOfBirth.equals("null") || monthOfBirth.equals(""))
        {
            monthOfBirth = "0";
        }
        if (dateOfBirth == null || dateOfBirth.equals("null") || dateOfBirth.equals(""))
        {
            dateOfBirth = "0";
        }

        if (yearOfBirth == null || yearOfBirth.equals("0"))
        {
            MiscUtils.getLogger().warn("Demographic no " + demographicNo + " does not have birth year. Unable to calculate age");
        }
        else
        {
            patientAge = UtilDateUtilities.calcAge(UtilDateUtilities.calcDate(yearOfBirth, monthOfBirth, dateOfBirth));
            patientBirthdate = yearOfBirth + "-" + monthOfBirth + "-" + dateOfBirth;
        }

        String referringDoctor = demographic.getFamilyDoctor();
        if (referringDoctor != null)
        {
            referringDoctorName = SxmlMisc.getXmlContent(referringDoctor, "rd") != null ? SxmlMisc.getXmlContent(referringDoctor, "rd") : "";
            referringDoctorNumber = SxmlMisc.getXmlContent(referringDoctor, "rdohip") != null ? SxmlMisc.getXmlContent(referringDoctor, "rdohip") : "";
        }
    }

    private void setupOldEchart()
    {
        EChartDao ecDao = SpringUtils.getBean(EChartDao.class);
        List<EChart> ecs = ecDao.getChartsForDemographic(ConversionUtils.fromIntString(demographicNo));
        if (!ecs.isEmpty()) {
            EChart ec = ecs.get(0);
            eChartId = ec.getId().toString();
            eChartTimeStamp =  ec.getTimestamp();
            socialHistory = ec.getSocialHistory();
            familyHistory = ec.getFamilyHistory();
            medicalHistory = ec.getMedicalHistory();
            ongoingConcerns = ec.getOngoingConcerns();
            reminders = ec.getReminders();
            encounter = ec.getEncounter();
            subject = ec.getSubject();
        } else {
            eChartTimeStamp = null;
            socialHistory = "";
            familyHistory = "";
            medicalHistory = "";
            ongoingConcerns = "";
            reminders = "";
            encounter = "";
            subject = "";
        }
    }
}
