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


package oscar.oscarDemographic.pageUtil;

import cds.AlertsAndSpecialNeedsDocument.AlertsAndSpecialNeeds;
import cds.AllergiesAndAdverseReactionsDocument.AllergiesAndAdverseReactions;
import cds.AppointmentsDocument.Appointments;
import cds.CareElementsDocument.CareElements;
import cds.ClinicalNotesDocument.ClinicalNotes;
import cds.DemographicsDocument.Demographics;
import cds.DemographicsDocument.Demographics.Enrolment;
import cds.FamilyHistoryDocument.FamilyHistory;
import cds.ImmunizationsDocument.Immunizations;
import cds.LaboratoryResultsDocument.LaboratoryResults;
import cds.MedicationsAndTreatmentsDocument.MedicationsAndTreatments;
import cds.OmdCdsDocument;
import cds.PastHealthDocument.PastHealth;
import cds.PatientRecordDocument.PatientRecord;
import cds.PersonalHistoryDocument.PersonalHistory;
import cds.ProblemListDocument.ProblemList;
import cds.ReportsReceivedDocument.ReportsReceived;
import cds.RiskFactorsDocument.RiskFactors;
import cdsDt.DiabetesComplicationScreening.ExamCode;
import cdsDt.DiabetesMotivationalCounselling.CounsellingPerformed;
import cdsDt.PersonNameStandard.LegalName;
import cdsDt.PersonNameStandard.OtherNames;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.xmlbeans.XmlException;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.AdmissionManager;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.casemgmt.dao.CaseManagementIssueDAO;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.AdmissionDao;
import org.oscarehr.common.dao.DemographicArchiveDao;
import org.oscarehr.common.dao.DemographicContactDao;
import org.oscarehr.common.dao.DrugReasonDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicArchive;
import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.MeasurementsExt;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentCommentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentSubClassDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentComment;
import org.oscarehr.hospitalReportManager.model.HRMDocumentSubClass;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import oscar.MyDateFormat;
import oscar.OscarProperties;
import oscar.appt.ApptStatusData;
import oscar.oscarDemographic.data.DemographicAddResult;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarDemographic.data.DemographicRelationship;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.oscarMeasurements.data.ImportExportMeasurements;
import oscar.oscarEncounter.oscarMeasurements.data.Measurements;
import oscar.oscarLab.LabRequestReportLink;
import oscar.oscarLab.ca.on.LabResultImport;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarProvider.data.ProviderData;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;
import oscar.util.UtilDateUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Ronnie Cheng
 */
    public class ImportDemographicDataAction4 extends Action {

    	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    	
    private static final Logger logger = MiscUtils.getLogger();
    private static final String PATIENTID = "Patient";
    private static final String ALERT = "Alert";
    private static final String ALLERGY = "Allergy";
    private static final String APPOINTMENT = "Appointment";
    private static final String CAREELEMENTS = "Care";
    private static final String CLINICALNOTE = "Clinical";
    private static final String FAMILYHISTORY = "Family";
    private static final String IMMUNIZATION = "Immunization";
    private static final String LABS = "Labs";
    private static final String MEDICATION = "Medication";
    private static final String PASTHEALTH = "Past";
    private static final String PERSONALHISTORY = "Personal";
    private static final String PROBLEMLIST = "Problem";
    private static final String REPORTBINARY = "Binary";
    private static final String REPORTTEXT = "Text";
    private static final String RISKFACTOR = "Risk";


    boolean matchProviderNames = true;
    String admProviderNo = null;
    String demographicNo = null;
    String patientName = null;
    String programId = null;
    HashMap<String, Integer> entries = new HashMap<String, Integer>();
    Integer importNo = 0;
    OscarProperties oscarProperties = OscarProperties.getInstance();

    ProgramManager programManager = (ProgramManager) SpringUtils.getBean("programManager");
    AdmissionManager admissionManager = (AdmissionManager) SpringUtils.getBean("admissionManager");
    AdmissionDao admissionDao = (AdmissionDao) SpringUtils.getBean("admissionDao");
    CaseManagementManager caseManagementManager = (CaseManagementManager) SpringUtils.getBean("caseManagementManager");
    DrugDao drugDao = (DrugDao) SpringUtils.getBean("drugDao");
    DrugReasonDao drugReasonDao = (DrugReasonDao) SpringUtils.getBean("drugReasonDao");
    DemographicArchiveDao demoArchiveDao = (DemographicArchiveDao) SpringUtils.getBean("demographicArchiveDao");
    ProviderDataDao providerDataDao = (ProviderDataDao) SpringUtils.getBean("providerDataDao");
    PartialDateDao partialDateDao = (PartialDateDao) SpringUtils.getBean("partialDateDao");
    DemographicExtDao demographicExtDao = (DemographicExtDao) SpringUtils.getBean("demographicExtDao");
    OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
    CaseManagementIssueDAO caseManagementIssueDAO = (CaseManagementIssueDAO) SpringUtils.getBean("caseManagementIssueDAO");
	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);

	@Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception  {
    	
    	if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "w", null)) {
			throw new SecurityException("missing required security object (_demographic)");
		}
    	
    	logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");
    	
        admProviderNo = (String) request.getSession().getAttribute("user");
        programId = new EctProgram(request.getSession()).getProgram(admProviderNo);
        String tmpDir = oscarProperties.getProperty("TMP_DIR");
        tmpDir = Util.fixDirName(tmpDir);
        if (!Util.checkDir(tmpDir)) {
            logger.debug("Error! Cannot write to TMP_DIR - Check oscar.properties or dir permissions.");
        }

        ImportDemographicDataForm frm = (ImportDemographicDataForm) form;
        logger.info("import to course id "  + frm.getCourseId() + " using timeshift value " + frm.getTimeshiftInDays());
        List<Provider> students = new ArrayList<Provider>();
        int courseId = 0;
        if(frm.getCourseId()!=null && frm.getCourseId().length()>0) {
            courseId = Integer.valueOf(frm.getCourseId());
            if(courseId>0) {
                logger.info("need to apply this import to a learning environment");
                //get all the students from this course
                List<ProgramProvider> courseProviders = programManager.getProgramProviders(String.valueOf(courseId));
                for(ProgramProvider pp:courseProviders) {
                    if(pp.getRole().getName().equalsIgnoreCase("student")) {
                        students.add(pp.getProvider());
                    }
                }
            }
        }
        logger.info("apply this patient to " + students.size() + " students");
        matchProviderNames = frm.getMatchProviderNames();
        FormFile imp = frm.getImportFile();
        String ifile = tmpDir + imp.getFileName();
        ArrayList<String> warnings = new ArrayList<String>();
        ArrayList<String[]> logs = new ArrayList<String[]>();
        File importLog = null;
        String defaultSite = frm.getDefaultSite();

	try {
            InputStream is = imp.getInputStream();
            OutputStream os = new FileOutputStream(ifile);
            byte[] buf = new byte[1024];
            int len;
            while ((len=is.read(buf)) > 0) os.write(buf,0,len);
            is.close();
            os.close();

            if (matchFileExt(ifile, "zip")) {
                ZipInputStream in = new ZipInputStream(new FileInputStream(ifile));
                boolean noXML = true;
                ZipEntry entry = in.getNextEntry();
                String entryDir = "";

                while (entry!=null) {
                    String entryName = entry.getName();
                    if (entry.isDirectory()) entryDir = entryName;
                    if (entryName.startsWith(entryDir)) entryName = entryName.substring(entryDir.length());

                    String ofile = tmpDir + entryName;
                    if (matchFileExt(ofile, "xml")) {
                        noXML = false;
                        OutputStream out = new FileOutputStream(ofile);
                        while ((len=in.read(buf)) > 0) out.write(buf,0,len);
                        out.close();
                        logs.add(importXML(LoggedInInfo.getLoggedInInfoFromSession(request) , ofile, warnings, request,frm.getTimeshiftInDays(),students,courseId, defaultSite));
                        importNo++;
                        demographicNo=null;
                    }
                    entry = in.getNextEntry();
                }
                if (noXML) {
                    Util.cleanFile(ifile);
                        throw new Exception ("Error! No .xml file in zip");
                } else {
                    importLog = makeImportLog(logs, tmpDir);
                }
                in.close();
                Util.cleanFile(ifile);

            } else if (matchFileExt(ifile, "xml")) {
                logs.add(importXML(LoggedInInfo.getLoggedInInfoFromSession(request), ifile, warnings, request,frm.getTimeshiftInDays(),students,courseId, defaultSite));
                demographicNo=null;
                importLog = makeImportLog(logs, tmpDir);
            } else {
                Util.cleanFile(ifile);
                throw new Exception ("Error! Import file must be .xml or .zip");
            }
	} catch (Exception e) {
            warnings.add("Error processing file: " + imp.getFileName());
            logger.error("Error", e);
            importLog = makeImportLog(logs, tmpDir);
    }

        //channel warnings and importlog to browser
        request.setAttribute("warnings",warnings);
        if (importLog!=null) request.setAttribute("importlog",importLog.getPath());

        logger.info("IMPORT PROCESS COMPLETE");
        return mapping.findForward("success");
    }

    String[] importXML(LoggedInInfo loggedInInfo, String xmlFile, ArrayList<String> warnings, HttpServletRequest request, int timeShiftInDays,List<Provider> students, int courseId, String defaultSite) throws SQLException, Exception {
        if(students == null || students.isEmpty()) {
            return importXMLForProvider(loggedInInfo, xmlFile,warnings,request,timeShiftInDays,null,null,0, defaultSite);
        }

        List<String> logs = new ArrayList<String>();

        for(Provider student:students) {
            logger.info("importing patient for student " +  student.getFormattedName());
            //need that student's personal program
            Integer pid = programManager.getProgramIdByProgramName("program"+student.getProviderNo());
            if(pid == null) {
                logger.warn("student's program not found");
                continue;
            }
            Program p = programManager.getProgram(pid);

            String[] result = importXMLForProvider(loggedInInfo, xmlFile,warnings,request,timeShiftInDays,student,p,courseId, defaultSite);
            logs.addAll(convertLog(result));
        }
        return logs.toArray(new String[logs.size()]);
    }

    private List<String> convertLog(String[] logs) {
    	List<String> tmp = new ArrayList<String>();
        tmp.addAll(Arrays.asList(logs));
    	return tmp;
    }



    String[] importXMLForProvider(LoggedInInfo loggedInInfo, String xmlFile, ArrayList<String> warnings, HttpServletRequest request, int timeShiftInDays, Provider student, Program admitTo, int courseId, String defaultSite) throws SQLException, Exception {
        ArrayList<String> err_demo = new ArrayList<String>(); //errors: duplicate demographics
        ArrayList<String> err_data = new ArrayList<String>(); //errors: discrete data
        ArrayList<String> err_summ = new ArrayList<String>(); //errors: summary
        ArrayList<String> err_othe = new ArrayList<String>(); //errors: other categories
        ArrayList<String> err_note = new ArrayList<String>(); //non-errors: notes

        String docDir = oscarProperties.getProperty("DOCUMENT_DIR");
        docDir = Util.fixDirName(docDir);
        if (!Util.checkDir(docDir)) {
                logger.debug("Error! Cannot write to DOCUMENT_DIR - Check oscar.properties or dir permissions.");
        }

        File xmlF = new File(xmlFile);
        OmdCdsDocument.OmdCds omdCds=null;
        try {
            omdCds = OmdCdsDocument.Factory.parse(xmlF).getOmdCds();
        } catch (IOException ex) {logger.error("Error", ex);
        } catch (XmlException ex) {logger.error("Error", ex);
        }
        PatientRecord patientRec = omdCds.getPatientRecord();

        //DEMOGRAPHICS
        Demographics demo = patientRec.getDemographics();
        cdsDt.PersonNameStandard.LegalName legalName = demo.getNames().getLegalName();
        String lastName="", firstName="";
        String lastNameQualifier=null, firstNameQualifier=null;
        if (legalName!=null) {
            if (legalName.getLastName()!=null) {
            	lastName = StringUtils.noNull(legalName.getLastName().getPart());
            	if (legalName.getLastName().getPartQualifier()!=null) {
            		lastNameQualifier = legalName.getLastName().getPartQualifier().toString();
            	}
            }
            if (legalName.getFirstName()!=null) {
            	firstName = StringUtils.noNull(legalName.getFirstName().getPart());
            	if (legalName.getFirstName().getPartQualifier()!=null) {
            		firstNameQualifier = legalName.getFirstName().getPartQualifier().toString();
            	}
            }
            patientName = lastName+","+firstName;
        } else {
            err_data.add("Error! No Legal Name");
        }

        //other names
        String legalOtherNameTxt=null, otherNameTxt=null;

        LegalName.OtherName[] legalOtherNames = legalName.getOtherNameArray();
        for (LegalName.OtherName otherName : legalOtherNames) {
            if (legalOtherNameTxt==null) legalOtherNameTxt = otherName.getPart();
            else legalOtherNameTxt += ", "+otherName.getPart();
        }

        OtherNames[] otherNames = demo.getNames().getOtherNamesArray();
        for (OtherNames otherName : otherNames) {
        	OtherNames.OtherName[] otherNames2 = otherName.getOtherNameArray();
        	for (OtherNames.OtherName otherName2 : otherNames2) {
        		if (otherNameTxt==null) otherNameTxt = otherName2.getPart();
        		else otherNameTxt += ", "+otherName2.getPart();
        	}
        	if (otherName.getNamePurpose()!=null) {
        		otherNameTxt = Util.addLine(mapNamePurpose(otherName.getNamePurpose())+": ", otherNameTxt);
        	}
        }
        otherNameTxt = Util.addLine(legalOtherNameTxt, otherNameTxt);

        String title = demo.getNames().getNamePrefix()!=null ? demo.getNames().getNamePrefix().toString() : "";
        String suffix = demo.getNames().getLastNameSuffix()!=null ? demo.getNames().getLastNameSuffix().toString() : "";
        String sex = demo.getGender()!=null ? demo.getGender().toString() : "";
        if (StringUtils.empty(sex)) {
            err_data.add("Error! No Gender");
        }
        String birthDate = getCalDate(demo.getDateOfBirth(), timeShiftInDays);
        if (StringUtils.empty(birthDate)) {
            birthDate = null;
            err_data.add("Error! No Date Of Birth");
        }
        String versionCode="", hin="", hc_type="", hc_renew_date="";
        cdsDt.HealthCard healthCard = demo.getHealthCard();
        if (healthCard!=null) {
            hin = StringUtils.noNull(healthCard.getNumber());
            if (hin.equals("")) {
                err_data.add("Error! No health card number");
            }
            hc_type = getProvinceCode(healthCard.getProvinceCode());
            if (hc_type.equals("")) {
                err_data.add("Error! No Province Code for health card");
            }
            versionCode = StringUtils.noNull(healthCard.getVersion());
            hc_renew_date = getCalDate(healthCard.getExpirydate());
        }

        //Check duplicate
        DemographicData dd = new DemographicData();
        ArrayList<Demographic> demodup = null;
        if (StringUtils.filled(hin)) demodup = dd.getDemographicWithHIN(loggedInInfo, hin);
        else demodup = dd.getDemographicWithLastFirstDOB(loggedInInfo, lastName, firstName, birthDate);
        if (demodup.size()>0) {
            err_data.clear();
            err_demo.add("Error! Patient "+patientName+" already exist! Not imported.");
            return packMsgs(err_demo, err_data, err_summ, err_othe, err_note, warnings);
        }


        String patient_status = null;
        Demographics.PersonStatusCode personStatusCode = demo.getPersonStatusCode();
        if (personStatusCode!=null) {
            if (personStatusCode.getPersonStatusAsEnum()!=null) {
                if (personStatusCode.getPersonStatusAsEnum().equals(cdsDt.PersonStatus.A)) patient_status = "AC";
                if (personStatusCode.getPersonStatusAsEnum().equals(cdsDt.PersonStatus.I)) patient_status = "IN";
                if (personStatusCode.getPersonStatusAsEnum().equals(cdsDt.PersonStatus.D)) patient_status = "DE";
            } else if (personStatusCode.getPersonStatusAsPlainText()!=null) {
                patient_status = personStatusCode.getPersonStatusAsPlainText();
            } else {
                err_data.add("Error! No Person Status Code");
            }
        } else {
            err_data.add("Error! No Person Status Code");
        }

        Enrolment[] enrolments = demo.getEnrolmentArray();
        int enrolTotal = enrolments.length;
        String[] roster_status=new String[enrolTotal],
        		 roster_date=new String[enrolTotal],
        		 term_date=new String[enrolTotal],
        		 term_reason=new String[enrolTotal];

        String rosterInfo = null;
        Calendar enrollDate=null, currentEnrollDate=null;

        for (int i=0; i<enrolTotal; i++) {
            roster_status[i] = enrolments[i].getEnrollmentStatus()!=null ? enrolments[i].getEnrollmentStatus().toString() : "";
            if	(roster_status[i].equals("1")) roster_status[i] = "RO";
            else if (roster_status[i].equals("0")) roster_status[i] = "NR";
            roster_date[i] = getCalDate(enrolments[i].getEnrollmentDate(), timeShiftInDays);
            term_date[i] = getCalDate(enrolments[i].getEnrollmentTerminationDate(), timeShiftInDays);
            if (enrolments[i].getTerminationReason()!=null)
            	term_reason[i] = enrolments[i].getTerminationReason().toString();

            //Sort enrolments by date
            if (enrolments[i].getEnrollmentDate()!=null) currentEnrollDate = enrolments[i].getEnrollmentDate();
            else if (enrolments[i].getEnrollmentTerminationDate()!=null) currentEnrollDate = enrolments[i].getEnrollmentTerminationDate();
            else currentEnrollDate = null;

            for (int j=i-1; j>=0; j--) {
                if (enrolments[j].getEnrollmentDate()!=null) enrollDate = enrolments[j].getEnrollmentDate();
                else if (enrolments[j].getEnrollmentTerminationDate()!=null) enrollDate = enrolments[j].getEnrollmentTerminationDate();
                else break;

                if (currentEnrollDate==null || currentEnrollDate.before(enrollDate)) {
                    rosterInfo=roster_status[j]; roster_status[j]=roster_status[i]; roster_status[i]=rosterInfo;
                    rosterInfo=roster_date[j];   roster_date[j]=roster_date[i];     roster_date[i]=rosterInfo;
            		rosterInfo=term_date[j];     term_date[j]=term_date[i];         term_date[i]=rosterInfo;
    				rosterInfo=term_reason[j];   term_reason[j]=term_reason[i];     term_reason[i]=rosterInfo;
                }
            }
        }

        String rosterStatus=null, rosterDate=null, termDate=null, termReason=null;
        if (enrolTotal>0) {
        	rosterStatus=roster_status[enrolTotal-1];
        	rosterDate=roster_date[enrolTotal-1];
        	termDate=term_date[enrolTotal-1];
        	termReason=term_reason[enrolTotal-1];
        }

        String sin = StringUtils.noNull(demo.getSIN());

        String chart_no = StringUtils.noNull(demo.getChartNumber());
        String official_lang = null;
        if (demo.getPreferredOfficialLanguage()!=null) {
            official_lang = demo.getPreferredOfficialLanguage().toString();
            official_lang = official_lang.equals("ENG") ? "English" : official_lang;
            official_lang = official_lang.equals("FRE") ? "French" : official_lang;
        }

        String spoken_lang = null;
        if (demo.getPreferredSpokenLanguage()!=null) {
        	spoken_lang = Util.convertCodeToLanguage(demo.getPreferredSpokenLanguage());
        	if (StringUtils.empty(spoken_lang)) err_data.add("Error! Cannot map spoken language code "+demo.getPreferredSpokenLanguage());
        }

        String dNote = StringUtils.noNull(demo.getNoteAboutPatient());
        String uvID = demo.getUniqueVendorIdSequence();
        String psDate = getCalDate(demo.getPersonStatusDate(), timeShiftInDays);
        String extra = null;

        if (StringUtils.filled(lastNameQualifier)) {
        	extra = Util.addLine(extra, "Lastname Qualifier: ", lastNameQualifier);
        }

        if (StringUtils.filled(firstNameQualifier)) {
        	extra = Util.addLine(extra, "Firstname Qualifier: ", firstNameQualifier);
        }

        if (StringUtils.filled(otherNameTxt)) {
            extra = Util.addLine(extra, "Other name: ", otherNameTxt);
        }

        if (StringUtils.filled(suffix)) {
        	extra = Util.addLine(extra, "Lastname suffix: ", suffix);
        }

        if (StringUtils.filled(uvID)) {
        	extra = Util.addLine(extra, "Unique Vendor ID: ", uvID);
        } else {
            err_data.add("Error! No Unique Vendor ID Sequence");
        }

        String address="", city="", province="", postalCode="";
        if (demo.getAddressArray().length>0) {
            cdsDt.Address addr = demo.getAddressArray(0);	//only get 1st address, other ignored
            if (addr!=null) {
                if (StringUtils.filled(addr.getFormatted())) {
                    address = addr.getFormatted();
                } else {
                    cdsDt.AddressStructured addrStr = addr.getStructured();
                    if (addrStr!=null) {
                        address = StringUtils.noNull(addrStr.getLine1()) + StringUtils.noNull(addrStr.getLine2()) + StringUtils.noNull(addrStr.getLine3());
                        city = StringUtils.noNull(addrStr.getCity());
                        province = getCountrySubDivCode(addrStr.getCountrySubdivisionCode());
                        cdsDt.PostalZipCode postalZip = addrStr.getPostalZipCode();
                        if (postalZip!=null) postalCode = StringUtils.noNull(postalZip.getPostalCode());
                    }
                }
            }
        }
        cdsDt.PhoneNumber[] pn = demo.getPhoneNumberArray();
        String workPhone="", workExt="", homePhone="", homeExt="", cellPhone="", ext="", patientPhone="";
        for (int i=0; i<pn.length; i++) {
            String phone = pn[i].getPhoneNumber();
            if (StringUtils.empty(phone)) {
                if (StringUtils.filled(pn[i].getNumber())) {
                    String areaCode = StringUtils.filled(pn[i].getAreaCode()) ? "("+pn[i].getAreaCode()+")" : "";
                    phone = areaCode + pn[i].getNumber();
                }
            }
            if (StringUtils.filled(phone)) {
                if (StringUtils.filled(pn[i].getExtension())) ext = pn[i].getExtension();
                else if (StringUtils.filled(pn[i].getExchange())) ext = pn[i].getExchange();

                if (pn[i].getPhoneNumberType()==cdsDt.PhoneNumberType.W) {
                    workPhone = phone;
                    workExt   = ext;
                } else if (pn[i].getPhoneNumberType()==cdsDt.PhoneNumberType.R) {
                    homePhone = phone;
                    homeExt   = ext;
                } else if (pn[i].getPhoneNumberType()==cdsDt.PhoneNumberType.C) {
                    cellPhone = phone;
                }
            }
        }
        if      (StringUtils.filled(homePhone)) patientPhone = homePhone+" "+homeExt;
        else if (StringUtils.filled(workPhone)) patientPhone = workPhone+" "+workExt;
        else if (StringUtils.filled(cellPhone)) patientPhone = cellPhone;
        String email = StringUtils.noNull(demo.getEmail());

        String primaryPhysician = "";
        if(student == null){
            Demographics.PrimaryPhysician demoPrimaryPhysician = demo.getPrimaryPhysician();
            if (demoPrimaryPhysician!=null) {
                HashMap<String,String> personName = getPersonName(demoPrimaryPhysician.getName());
                String personOHIP = demoPrimaryPhysician.getOHIPPhysicianId();
                if (StringUtils.empty(personName.get("firstname"))) err_data.add("Error! No Primary Physician first name");
                if (StringUtils.empty(personName.get("lastname"))) err_data.add("Error! No Primary Physician last name");
                if (StringUtils.empty(personOHIP)) err_data.add("Error! No Primary Physician OHIP billing number");
                String personCPSO = demoPrimaryPhysician.getPrimaryPhysicianCPSO();
                primaryPhysician = writeProviderData(personName.get("firstname"), personName.get("lastname"), personOHIP, personCPSO);
            }
            if (StringUtils.empty(primaryPhysician)) {
                primaryPhysician = defaultProviderNo();
                err_data.add("Error! No Primary Physician; patient assigned to \"doctor oscardoc\"");
            }
        } else {
            primaryPhysician = student.getProviderNo();
        }

        String year_of_birth = null;
        String month_of_birth = null;
        String date_of_birth = null;
        if (birthDate!=null)
        {
            Date bDate = UtilDateUtilities.StringToDate(birthDate,"yyyy-MM-dd");
            year_of_birth = UtilDateUtilities.DateToString(bDate,"yyyy");
            month_of_birth = UtilDateUtilities.DateToString(bDate,"MM");
            date_of_birth = UtilDateUtilities.DateToString(bDate,"dd");
        }

        DemographicAddResult demoRes = null;

        //Check if Contact-only demographic exists
        org.oscarehr.common.model.Demographic demographic = null;

        if(courseId == 0) {
            demographicNo = dd.getDemoNoByNamePhoneEmail(loggedInInfo, firstName, lastName, homePhone, workPhone, email);
            demographic = dd.getDemographic(loggedInInfo, demographicNo);
        }

        if (demographic!=null && StringUtils.nullSafeEqualsIgnoreCase(demographic.getPatientStatus(), "Contact-only")) {
        	//found contact-only demo, replace!
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            demographic.setTitle(title);
            demographic.setAddress(address);
            demographic.setCity(city);
            demographic.setProvince(province);
            demographic.setPostal(postalCode);
            demographic.setYearOfBirth(year_of_birth);
            demographic.setMonthOfBirth(month_of_birth);
            demographic.setDateOfBirth(date_of_birth);
            demographic.setHin(hin);
            demographic.setVer(versionCode);
            demographic.setRosterStatus(rosterStatus);
            
            Date dDate;
            try {
            	dDate = formatter.parse(rosterDate);
            }
            catch( Exception e ) {
            	dDate = null;
            }
            
            demographic.setRosterDate(dDate);
            
            
            try {
            	dDate = formatter.parse(termDate);
            }
            catch( Exception e ) {
            	dDate = null;
            }
            
            demographic.setRosterTerminationDate(dDate);
            demographic.setRosterTerminationReason(termReason);
            demographic.setPatientStatus(patient_status);
            
            try {
            	dDate = formatter.parse(psDate);
            }
            catch( Exception e ) {
            	dDate = null;
            }
            
            demographic.setPatientStatusDate(dDate);
            demographic.setChartNo(chart_no);
            demographic.setOfficialLanguage(official_lang);
            demographic.setSpokenLanguage(spoken_lang);
            demographic.setFamilyDoctor(primaryPhysician);
            demographic.setSex(sex);
            demographic.setHcType(hc_type);
            
            try {
            	dDate = formatter.parse(hc_renew_date);
            }
            catch( Exception e ) {
            	dDate = null;
            }
            
            demographic.setHcRenewDate(dDate);
            demographic.setSin(sin);
            dd.setDemographic(loggedInInfo, demographic);
            err_note.add("Replaced Contact-only patient "+patientName+" (Demo no="+demographicNo+")");

        } else { //add patient!
            demoRes = dd.addDemographic(loggedInInfo, title, lastName, firstName, address, city, province, postalCode, homePhone, workPhone, year_of_birth, month_of_birth, date_of_birth, hin, versionCode, rosterStatus, rosterDate, termDate, termReason, patient_status, psDate, ""/*date_joined*/, chart_no, official_lang, spoken_lang, primaryPhysician, sex, ""/*end_date*/, ""/*eff_date*/, ""/*pcn_indicator*/, hc_type, hc_renew_date, ""/*family_doctor*/, email, ""/*pin*/, ""/*alias*/, ""/*previousAddress*/, ""/*children*/, ""/*sourceOfIncome*/, ""/*citizenship*/, sin);
            demographicNo = demoRes.getId();
            logger.info("Created a new demographic with id #" + demographicNo);
        }

        if (StringUtils.filled(demographicNo))
        {
        	logger.info("IMPORT FOR DEMOGRAPHIC " + demographicNo);
            //TODO: Course - Admit to student program

            entries.put(PATIENTID+importNo, Integer.valueOf(demographicNo));

            if(admitTo == null) {
                insertIntoAdmission(demographicNo);
            } else {
                admissionManager.processAdmission(Integer.valueOf(demographicNo), student.getProviderNo(), admitTo, "", "batch import");
            }

            //Put enrolment history into demographicArchive
            demographic = dd.getDemographic(loggedInInfo, demographicNo);
            for (int i=0; i<roster_status.length-1; i++) {
            	DemographicArchive demographicArchive = archiveDemographic(demographic);
            	demographicArchive.setRosterStatus(roster_status[i]);
            	demographicArchive.setRosterDate(UtilDateUtilities.StringToDate(roster_date[i]));
            	demographicArchive.setRosterTerminationDate(UtilDateUtilities.StringToDate(term_date[i]));
            	demographicArchive.setRosterTerminationReason(term_reason[i]);
            	demoArchiveDao.persist(demographicArchive);
            }

            //Patient notes
            if (StringUtils.filled(dNote)) dd.addDemographiccust(demographicNo, dNote);

            if (!workExt.equals("")) demographicExtDao.addKey(primaryPhysician, Integer.parseInt(demographicNo), "wPhoneExt", workExt);
            if (!homeExt.equals("")) demographicExtDao.addKey(primaryPhysician, Integer.parseInt(demographicNo), "hPhoneExt", homeExt);
            if (!cellPhone.equals("")) demographicExtDao.addKey(primaryPhysician, Integer.parseInt(demographicNo), "demo_cell", cellPhone);
            if(courseId>0) demographicExtDao.addKey(primaryPhysician, Integer.parseInt(demographicNo), "course", String.valueOf(courseId));


            //Demographic Contacts
            DemographicContactDao contactDao = (DemographicContactDao) SpringUtils.getBean("demographicContactDao");

            Demographics.Contact[] contt = demo.getContactArray();
            for (int i=0; i<contt.length; i++) {
                HashMap<String,String> contactName = getPersonName(contt[i].getName());
                String cFirstName = StringUtils.noNull(contactName.get("firstname"));
                String cLastName  = StringUtils.noNull(contactName.get("lastname"));
                String cEmail = StringUtils.noNull(contt[i].getEmailAddress());

                pn = contt[i].getPhoneNumberArray();
                workPhone=""; workExt=""; homePhone=""; homeExt=""; cellPhone=""; ext="";
                for (int j=0; j<pn.length; j++) {
                    String phone = pn[j].getPhoneNumber();
                    if (phone==null) {
                        if (pn[j].getNumber()!=null) {
                            if (pn[j].getAreaCode()!=null) phone = pn[j].getAreaCode()+"-"+pn[j].getNumber();
                            else phone = pn[j].getNumber();
                        }
                    }
                    if (phone!=null) {
                        if (pn[j].getExtension()!=null) ext = pn[j].getExtension();
                        else if (pn[j].getExchange()!=null) ext = pn[j].getExchange();

                        if (pn[j].getPhoneNumberType()==cdsDt.PhoneNumberType.W) {
                            workPhone = phone;
                            workExt   = ext;
                        } else if (pn[j].getPhoneNumberType()==cdsDt.PhoneNumberType.R) {
                            homePhone = phone;
                            homeExt   = ext;
                        } else if (pn[j].getPhoneNumberType()==cdsDt.PhoneNumberType.C) {
                            cellPhone = phone;
                        }
                    }
                }

                String contactNote = StringUtils.noNull(contt[i].getNote());
                String cDemoNo = dd.getDemoNoByNamePhoneEmail(loggedInInfo, cFirstName, cLastName, homePhone, workPhone, cEmail);
                String cPatient = cLastName+","+cFirstName;
                if (StringUtils.empty(cDemoNo)) {   //add new demographic as contact
                    psDate = UtilDateUtilities.DateToString(new Date(),"yyyy-MM-dd");
                    demoRes = dd.addDemographic(loggedInInfo, ""/*title*/, cLastName, cFirstName, ""/*address*/, ""/*city*/, ""/*province*/, ""/*postal*/,
                    			homePhone, workPhone, ""/*year_of_birth*/, ""/*month_*/, ""/*date_*/, ""/*hin*/, ""/*ver*/, ""/*roster_status*/, "", "", "",
                    			"Contact-only", psDate, ""/*date_joined*/, ""/*chart_no*/, ""/*official_lang*/, ""/*spoken_lang*/, ""/*provider_no*/,
                    			"F", ""/*end_date*/, ""/*eff_date*/, ""/*pcn_indicator*/, ""/*hc_type*/, ""/*hc_renew_date*/, ""/*family_doctor*/,
                    			cEmail, "", "", "", "", "", "", "");
                	cDemoNo = demoRes.getId();
                    err_note.add("Contact-only patient "+cPatient+" (Demo no="+cDemoNo+") created");

                    if (!workExt.equals("")) demographicExtDao.addKey("", Integer.parseInt(cDemoNo), "wPhoneExt", workExt);
                    if (!homeExt.equals("")) demographicExtDao.addKey("", Integer.parseInt(cDemoNo), "hPhoneExt", homeExt);
                    if (!cellPhone.equals("")) demographicExtDao.addKey("", Integer.parseInt(cDemoNo), "demo_cell", cellPhone);
                }
                insertIntoAdmission(cDemoNo);

                cdsDt.PurposeEnumOrPlainText[] contactPurposes = contt[i].getContactPurposeArray();
                String sdm="", emc="", cPurpose=null;
                String[] rel = new String[contactPurposes.length];

                for (int j=0; j<contactPurposes.length; j++) {
                    cPurpose = contactPurposes[j].getPurposeAsPlainText();
                    if (cPurpose==null) cPurpose = contactPurposes[j].getPurposeAsEnum().toString();
                    if (cPurpose!=null) cPurpose = cPurpose.trim();
                    else continue;

                    if (cPurpose.equals("EC") || cPurpose.equalsIgnoreCase("emergency contact"))
                    	emc = "true";
                    else if (cPurpose.equals("SDM") || cPurpose.equalsIgnoreCase("substitute decision maker"))
                    	sdm = "true";
                    else if (cPurpose.equals("NK")) rel[j] = "Next of Kin";
                    else if (cPurpose.equals("AS")) rel[j] = "Administrative Staff";
                    else if (cPurpose.equals("CG")) rel[j] = "Care Giver";
                    else if (cPurpose.equals("PA")) rel[j] = "Power of Attorney";
                    else if (cPurpose.equals("IN")) rel[j] = "Insurance";
                    else if (cPurpose.equals("GT")) rel[j] = "Guarantor";
                    else {
                        rel[j] = cPurpose;
                    }
                }

                if (StringUtils.filled(cDemoNo)) {
                	if (oscarProperties.isPropertyActive("NEW_CONTACTS_UI")) {
                        for (int j=0; j<rel.length; j++) {
                        	if (rel[j]==null) continue;

                            DemographicContact demoContact = new DemographicContact();
                            demoContact.setCreated(new Date());
                            demoContact.setUpdateDate(new Date());
                            demoContact.setDemographicNo(Integer.valueOf(demographicNo));
                            demoContact.setContactId(cDemoNo);
                            demoContact.setType(1); //should be "type" - display problem
                            demoContact.setCategory("personal");
                        	demoContact.setRole(rel[j]);
                            demoContact.setEc(emc);
                            demoContact.setSdm(sdm);
                            demoContact.setNote(contactNote);
                        	contactDao.persist(demoContact);

                        	//clear emc, sdm, contactNote after 1st save
                        	emc = "";
                        	sdm = "";
                        	contactNote = "";
                        }
                	} else {
				        Facility facility = (Facility) request.getSession().getAttribute(SessionConstants.CURRENT_FACILITY);
				        Integer facilityId = null;
				        if (facility!=null) facilityId = facility.getId();

				        for (int j=0; j<rel.length; j++) {
				        	if (rel[j]==null) continue;

							DemographicRelationship demoRel = new DemographicRelationship();
							demoRel.addDemographicRelationship(demographicNo, cDemoNo, rel[j], sdm.equals("true"), emc.equals("true"), contactNote, admProviderNo, facilityId);

                        	//clear emc, sdm, contactNote after 1st save
                        	emc = "";
                        	sdm = "";
                        	contactNote = "";
				        }
                	}
                }
            }

            Set<CaseManagementIssue> scmi = null;	//Declare a set for CaseManagementIssues
            //PERSONAL HISTORY
            PersonalHistory[] pHist = patientRec.getPersonalHistoryArray();
            logger.info("IMPORT PERSONAL HISTORY: " + pHist.length + " entries found");
            final String historyPrefix = "<cds:CategorySummaryLine xmlns:cds=\"cds\" xmlns:cdsd=\"cds_dt\">[Personal History]:";
            final String historySuffix = "</cds:CategorySummaryLine>";
            for (int i=0; i<pHist.length; i++)
            {
                if (i == 0)
                {
                    scmi = getCMIssue("SocHistory");
                }
                CaseManagementNote cmNote = prepareCMNote(programId, null);
                cmNote.setIssues(scmi);

                String history = org.apache.commons.lang.StringUtils.trimToNull(pHist[i].toString())
                        .replace(historyPrefix, "")
                        .replace(historySuffix, "");

                cmNote.setNote(history);
                caseManagementManager.saveNoteSimple(cmNote);
                addOneEntry(PERSONALHISTORY);
            }

            //FAMILY HISTORY
            FamilyHistory[] fHist = patientRec.getFamilyHistoryArray();
            logger.info("IMPORT FAMILY HISTORY: " + fHist.length + " entries found");
            for (int i=0; i<fHist.length; i++) {
                if (i==0) scmi = getCMIssue("FamHistory");
                CaseManagementNote cmNote = prepareCMNote(programId, null);

                //diagnosis code
                if (fHist[i].getDiagnosisProcedureCode()==null) {
                    cmNote.setIssues(scmi);
                } else {
                    cmNote.setIssues(getCMIssue("FamHistory", fHist[i].getDiagnosisProcedureCode()));
                }

                //main field
                String familyHist = fHist[i].getProblemDiagnosisProcedureDescription();
                if (StringUtils.empty(familyHist)) familyHist = "Imported Family History";
                cmNote.setNote(familyHist);
                caseManagementManager.saveNoteSimple(cmNote);
                addOneEntry(FAMILYHISTORY);

                //annotation
                Long hostNoteId = cmNote.getId();
                cmNote = prepareCMNote(programId, null);
                String note = StringUtils.noNull(fHist[i].getNotes());
                cmNote.setNote(note);
                saveLinkNote(hostNoteId, cmNote);

                //to dumpsite
                //String dump = "imported.cms4.2011.06";
                /*
                String summary = fHist[i].getCategorySummaryLine();
                if (StringUtils.empty(summary)) {
                        err_summ.add("No Summary for Family History ("+(i+1)+")");
                }
                dump = Util.addLine(dump, summary);
                */
                /*String diagCode = getCode(fHist[i].getDiagnosisProcedureCode(),"Diagnosis/Procedure");
                dump = Util.addLine(dump, diagCode);
                dump = Util.addLine(dump, getResidual(fHist[i].getResidualInfo()));
                cmNote = prepareCMNote("2",null);
                cmNote.setNote(dump);
                saveLinkNote(hostNoteId, cmNote);*/

                //extra fields
                CaseManagementNoteExt cme = new CaseManagementNoteExt();
                cme.setNoteId(hostNoteId);
                if (fHist[i].getStartDate()!=null) {
                    cme.setKeyVal(CaseManagementNoteExt.STARTDATE);
                    cme.setDateValue(dateFPtoDate(fHist[i].getStartDate(), timeShiftInDays));
                    cme.setValue(dateFPGetPartial(fHist[i].getStartDate()));
                    caseManagementManager.saveNoteExt(cme);
                }
                if (fHist[i].getAgeAtOnset()!=null) {
                    cme.setKeyVal(CaseManagementNoteExt.AGEATONSET);
                    cme.setDateValue((Date)null);
                    cme.setValue(fHist[i].getAgeAtOnset().toString());
                    caseManagementManager.saveNoteExt(cme);
                }
                if (StringUtils.filled(fHist[i].getRelationship())) {
                    cme.setKeyVal(CaseManagementNoteExt.RELATIONSHIP);
                    cme.setDateValue((Date)null);
                    cme.setValue(fHist[i].getRelationship());
                    caseManagementManager.saveNoteExt(cme);
                }
                if (StringUtils.filled(fHist[i].getTreatment())) {
                    cme.setKeyVal(CaseManagementNoteExt.TREATMENT);
                    cme.setDateValue((Date)null);
                    cme.setValue(fHist[i].getTreatment());
                    caseManagementManager.saveNoteExt(cme);
                }
                if (fHist[i].getLifeStage()!=null) {
                    cme.setKeyVal(CaseManagementNoteExt.LIFESTAGE);
                    cme.setDateValue((Date)null);
                    cme.setValue(fHist[i].getLifeStage().toString());
                    caseManagementManager.saveNoteExt(cme);
                }
            }

            //PAST HEALTH
            PastHealth[] pHealth = patientRec.getPastHealthArray();
            logger.info("IMPORT PAST HEALTH: " + pHealth.length + " entries found");
            for (int i=0; i< pHealth.length; i++) {
                if (i==0) scmi = getCMIssue("MedHistory");
                CaseManagementNote cmNote = prepareCMNote(programId, null);

                //diagnosis code
                if (pHealth[i].getDiagnosisProcedureCode()==null) {
                    cmNote.setIssues(scmi);
                } else {
                    cmNote.setIssues(getCMIssue("MedHistory", pHealth[i].getDiagnosisProcedureCode()));
                }

                //main field
                String medicalHist = pHealth[i].getPastHealthProblemDescriptionOrProcedures();
                if (StringUtils.empty(medicalHist)) medicalHist = "Imported Medical History";
                cmNote.setNote(medicalHist);
                caseManagementManager.saveNoteSimple(cmNote);
                addOneEntry(FAMILYHISTORY);

                //annotation
                Long hostNoteId = cmNote.getId();
                cmNote = prepareCMNote(programId, null);
                String note = pHealth[i].getNotes();
                cmNote.setNote(note);
                saveLinkNote(hostNoteId, cmNote);


                //to dumpsite
                //String dump = "imported.cms4.2011.06";
                /*
                String summary = pHealth[i].getCategorySummaryLine();
                if (StringUtils.empty(summary)) {
                    err_summ.add("No Summary for Past Health ("+(i+1)+")");
                }
                dump = Util.addLine(dump, summary);
                */
                /*String diagCode = isICD9(pHealth[i].getDiagnosisProcedureCode()) ? null : getCode(pHealth[i].getDiagnosisProcedureCode(),"Diagnosis/Procedure");
                dump = Util.addLine(dump, diagCode);
                dump = Util.addLine(dump, getResidual(pHealth[i].getResidualInfo()));
                cmNote = prepareCMNote("2",null);
                cmNote.setNote(dump);
                saveLinkNote(hostNoteId, cmNote);*/

                //extra fields
                CaseManagementNoteExt cme = new CaseManagementNoteExt();
                cme.setNoteId(hostNoteId);
                if (pHealth[i].getOnsetOrEventDate()!=null) {
                    cme.setKeyVal(CaseManagementNoteExt.STARTDATE);
                    cme.setDateValue(dateFPtoDate(pHealth[i].getOnsetOrEventDate(), timeShiftInDays));
                    cme.setValue(dateFPGetPartial(pHealth[i].getOnsetOrEventDate()));
                    caseManagementManager.saveNoteExt(cme);
                }
                    if (pHealth[i].getProcedureDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.PROCEDUREDATE);
                        cme.setDateValue(dateFPtoDate(pHealth[i].getProcedureDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(pHealth[i].getProcedureDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (pHealth[i].getResolvedDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.RESOLUTIONDATE);
                        cme.setDateValue(dateFPtoDate(pHealth[i].getResolvedDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(pHealth[i].getResolvedDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (pHealth[i].getLifeStage()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.LIFESTAGE);
                        cme.setDateValue((Date)null);
                        cme.setValue(pHealth[i].getLifeStage().toString());
                        caseManagementManager.saveNoteExt(cme);
                    }
                }

                //PROBLEM LIST
                ProblemList[] probList = patientRec.getProblemListArray();
                logger.info("IMPORT PROBLEM LIST: " + probList.length + " entries found");
                for (int i=0; i<probList.length; i++) {
                    if (i==0) scmi = getCMIssue("Concerns");
                    CaseManagementNote cmNote = prepareCMNote(programId, null);

                    //diagnosis code
                    if (probList[i].getDiagnosisCode()==null) {
                        cmNote.setIssues(scmi);
                    } else {
                        cmNote.setIssues(getCMIssue("Concerns", probList[i].getDiagnosisCode()));
                    }

                    //main field
                    String ongConcerns = probList[i].getProblemDiagnosisDescription();
                    if (StringUtils.empty(ongConcerns)) ongConcerns = "Imported Concern";
                    cmNote.setNote(ongConcerns);
                    caseManagementManager.saveNoteSimple(cmNote);
                    addOneEntry(PROBLEMLIST);

                    //annotation
                    Long hostNoteId = cmNote.getId();
                    cmNote = prepareCMNote(programId, null);
                    String note = probList[i].getNotes();
                    cmNote.setNote(note);
                    saveLinkNote(hostNoteId, cmNote);


                    //to dumpsite
                    //String dump = "imported.cms4.2011.06";
                    /*
                    String summary = probList[i].getCategorySummaryLine();
                    if (StringUtils.empty(summary)) {
                            err_summ.add("No Summary for Problem List ("+(i+1)+")");
                    }
                    dump = Util.addLine(dump, summary);
                    */
                    /*String diagCode = isICD9(probList[i].getDiagnosisCode()) ? null : getCode(probList[i].getDiagnosisCode(),"Diagnosis");
                    dump = Util.addLine(dump, diagCode);
                    dump = Util.addLine(dump, getResidual(probList[i].getResidualInfo()));
                    cmNote = prepareCMNote("2",null);
                    cmNote.setNote(dump);
                    saveLinkNote(hostNoteId, cmNote);*/

                    //extra fields
                    CaseManagementNoteExt cme = new CaseManagementNoteExt();
                    cme.setNoteId(hostNoteId);
                    if (StringUtils.filled(probList[i].getProblemDescription())) {
                        cme.setKeyVal(CaseManagementNoteExt.PROBLEMDESC);
                        cme.setDateValue((Date)null);
                        cme.setValue(probList[i].getProblemDescription());
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (probList[i].getOnsetDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.STARTDATE);
                        cme.setDateValue(dateFPtoDate(probList[i].getOnsetDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(probList[i].getOnsetDate()));
                        caseManagementManager.saveNoteExt(cme);
                    } else {
                        err_data.add("Error! No Onset Date for Problem List ("+(i+1)+")");
                    }
                    if (probList[i].getResolutionDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.RESOLUTIONDATE);
                        cme.setDateValue(dateFPtoDate(probList[i].getResolutionDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(probList[i].getResolutionDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (StringUtils.filled(probList[i].getProblemStatus())) {
                        cme.setKeyVal(CaseManagementNoteExt.PROBLEMSTATUS);
                        cme.setDateValue((Date)null);
                        cme.setValue(probList[i].getProblemStatus());
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (probList[i].getLifeStage()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.LIFESTAGE);
                        cme.setDateValue((Date)null);
                        cme.setValue(probList[i].getLifeStage().toString());
                        caseManagementManager.saveNoteExt(cme);
                    }
                }

                //RISK FACTORS
                RiskFactors[] rFactors = patientRec.getRiskFactorsArray();
                logger.info("IMPORT RISK FACTORS: " + rFactors.length + " entries found");
                for (int i=0; i<rFactors.length; i++) {
                    if (i==0) scmi = getCMIssue("RiskFactors");
                    CaseManagementNote cmNote = prepareCMNote(programId, null);
                    cmNote.setIssues(scmi);

                    //main field
                    String riskFactors = rFactors[i].getRiskFactor();
                    if (StringUtils.empty(riskFactors)) riskFactors = "Imported Risk Factor";
                    cmNote.setNote(riskFactors);
                    caseManagementManager.saveNoteSimple(cmNote);
                    addOneEntry(RISKFACTOR);

                    //annotation
                    Long hostNoteId = cmNote.getId();
                    cmNote = prepareCMNote(programId, null);
                    String note = rFactors[i].getNotes();
                    cmNote.setNote(note);
                    saveLinkNote(hostNoteId, cmNote);

                    //extra fields
                    CaseManagementNoteExt cme = new CaseManagementNoteExt();
                    cme.setNoteId(hostNoteId);
                    if (rFactors[i].getStartDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.STARTDATE);
                        cme.setDateValue(dateFPtoDate(rFactors[i].getStartDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(rFactors[i].getStartDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (rFactors[i].getEndDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.RESOLUTIONDATE);
                        cme.setDateValue(dateFPtoDate(rFactors[i].getEndDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(rFactors[i].getEndDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (rFactors[i].getAgeOfOnset()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.AGEATONSET);
                        cme.setDateValue((Date)null);
                        cme.setValue(rFactors[i].getAgeOfOnset().toString());
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (StringUtils.filled(rFactors[i].getExposureDetails())) {
                        cme.setKeyVal(CaseManagementNoteExt.EXPOSUREDETAIL);
                        cme.setDateValue((Date)null);
                        cme.setValue(rFactors[i].getExposureDetails());
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (rFactors[i].getLifeStage()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.LIFESTAGE);
                        cme.setDateValue((Date)null);
                        cme.setValue(rFactors[i].getLifeStage().toString());
                        caseManagementManager.saveNoteExt(cme);
                    }
                }

                //ALERTS & SPECIAL NEEDS
                AlertsAndSpecialNeeds[] alerts = patientRec.getAlertsAndSpecialNeedsArray();
                logger.info("IMPORT ALERTS & SPECIAL NEEDS: " + alerts.length + " entries found");
                for (int i=0; i<alerts.length; i++) {
                    if (i==0) scmi = getCMIssue("Reminders");
                    CaseManagementNote cmNote = prepareCMNote(programId, null);
                    cmNote.setIssues(scmi);

                    //main field
                    String reminders = alerts[i].getAlertDescription();
                    if (StringUtils.empty(reminders)) {
                        err_data.add("Error! No Alert Description ("+(i+1)+")");
                        reminders = "Imported Alert";
                    }
                    cmNote.setNote(reminders);
                    caseManagementManager.saveNoteSimple(cmNote);
                    addOneEntry(ALERT);

                    //annotation
                    Long hostNoteId = cmNote.getId();
                    cmNote = prepareCMNote(programId, null);
                    String note = alerts[i].getNotes();
                    cmNote.setNote(note);
                    saveLinkNote(hostNoteId, cmNote);

                    //extra fields
                    CaseManagementNoteExt cme = new CaseManagementNoteExt();
                    cme.setNoteId(hostNoteId);
                    if (alerts[i].getDateActive()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.STARTDATE);
                        cme.setDateValue(dateFPtoDate(alerts[i].getDateActive(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(alerts[i].getDateActive()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                    if (alerts[i].getEndDate()!=null) {
                        cme.setKeyVal(CaseManagementNoteExt.RESOLUTIONDATE);
                        cme.setDateValue(dateFPtoDate(alerts[i].getEndDate(), timeShiftInDays));
                        cme.setValue(dateFPGetPartial(alerts[i].getEndDate()));
                        caseManagementManager.saveNoteExt(cme);
                    }
                }

                //CLINICAL NOTES
                ClinicalNotes[] cNotes = patientRec.getClinicalNotesArray();
                logger.info("IMPORT CLINICAL NOTES: " + cNotes.length + " entries found");
                Date observeDate = new Date(), createDate = new Date();
                for (int i=0; i<cNotes.length; i++) {
                    //encounter note
                    String encounter = cNotes[i].getMyClinicalNotesContent();
                    if (StringUtils.empty(encounter)) {
                    	err_data.add("Empty clinical note - not added ("+(i+1)+")");
                    	continue;
                    }

                    //create date
                    if (cNotes[i].getEnteredDateTime()!=null) {
                    	createDate = dateTimeFPtoDate(cNotes[i].getEnteredDateTime(),timeShiftInDays);
                    	observeDate = createDate;
                    }

                    //observation date
                    if (cNotes[i].getEventDateTime()!=null) {
                    	observeDate = dateTimeFPtoDate(cNotes[i].getEventDateTime(),timeShiftInDays);
                    	if (cNotes[i].getEnteredDateTime()==null) createDate = observeDate;

                    }

                    CaseManagementNote cmNote = prepareCMNote(programId,null);
                    cmNote.setCreate_date(createDate);
                    cmNote.setObservation_date(observeDate);
                    cmNote.setNote(encounter);

                    String uuid = null;
                    ClinicalNotes.ParticipatingProviders[] participatingProviders = cNotes[i].getParticipatingProvidersArray();
                    ClinicalNotes.NoteReviewer[] noteReviewers = cNotes[i].getNoteReviewerArray();

                    int p_total = participatingProviders.length + noteReviewers.length;
                    for (int p=0; p<p_total; p++) {
                        if (p>0) {
                            cmNote = prepareCMNote(programId,uuid);
                            cmNote.setObservation_date(observeDate);
                            cmNote.setCreate_date(createDate);
                            cmNote.setNote(encounter);
                        }

                        //participating providers
                        if (p<participatingProviders.length) {
                            if (participatingProviders[p].getDateTimeNoteCreated()==null) cmNote.setUpdate_date(new Date());
                            else cmNote.setUpdate_date(dateTimeFPtoDate(participatingProviders[p].getDateTimeNoteCreated(), timeShiftInDays));

                            if (participatingProviders[p].getName()!=null) {
                                HashMap<String,String> authorName = getPersonName(participatingProviders[p].getName());
                                String authorOHIP = participatingProviders[p].getOHIPPhysicianId();
                                String authorProvider = writeProviderData(authorName.get("firstname"), authorName.get("lastname"), authorOHIP);
                                if (StringUtils.empty(authorProvider)) {
                                    authorProvider = defaultProviderNo();
                                    err_note.add("Clinical notes have no author; assigned to \"doctor oscardoc\" ("+(i+1)+")");
                                }
                                cmNote.setProviderNo(authorProvider);
                                cmNote.setSigning_provider_no(authorProvider);
                            }
                        } else {

                        	//note reviewers
                        	int r = p-participatingProviders.length;
                            if (noteReviewers[r].getName()!=null) {
                                if (noteReviewers[r].getDateTimeNoteReviewed()==null) cmNote.setUpdate_date(new Date());
                                else cmNote.setUpdate_date(dateTimeFPtoDate(noteReviewers[r].getDateTimeNoteReviewed(), timeShiftInDays));

                                HashMap<String,String> authorName = getPersonName(noteReviewers[r].getName());
                                String reviewerOHIP = noteReviewers[r].getOHIPPhysicianId();
                                String reviewer = writeProviderData(authorName.get("firstname"), authorName.get("lastname"), reviewerOHIP);

                                cmNote.setProviderNo(reviewer);
                                cmNote.setSigning_provider_no(reviewer);
                                Util.writeVerified(cmNote);
                            }
                        }
                        
                        if( cmNote.getProviderNo() == null ) {
                        	cmNote.setProviderNo(defaultProviderNo());
                        }
                        
                        if( cmNote.getSigning_provider_no() == null ) {
                        	cmNote.setSigning_provider_no(defaultProviderNo());
                        }
                        
                        caseManagementManager.saveNoteSimple(cmNote);

                        //prepare for extra notes
                        if (p==0) {
                            addOneEntry(CLINICALNOTE);
                            uuid = cmNote.getUuid();
                        }
                    }
                    if (p_total==0) {
                        err_note.add("Clinical notes have no author; assigned to \"doctor oscardoc\" ("+(i+1)+")");
                    	caseManagementManager.saveNoteSimple(cmNote);
                    }
                }

                //ALLERGIES & ADVERSE REACTIONS
                AllergiesAndAdverseReactions[] aaReactArray = patientRec.getAllergiesAndAdverseReactionsArray();
                logger.info("IMPORT ALLERGIES & ADVERSE REACTIONS: " + aaReactArray.length + " entries found");
                for (int i=0; i<aaReactArray.length; i++) {
                    String description="", regionalId="", reaction="", severity="", entryDate="", startDate="", lifeStage="", alg_extra="";
                    String entryDateFormat=null, startDateFormat=null;
                    int typeCode=0;

                    reaction = StringUtils.noNull(aaReactArray[i].getReaction());
                    description = StringUtils.noNull(aaReactArray[i].getOffendingAgentDescription());
                    entryDate = dateFPtoString(aaReactArray[i].getRecordedDate(), timeShiftInDays);
                    startDate = dateFPtoString(aaReactArray[i].getStartDate(), timeShiftInDays);
                    if (aaReactArray[i].getLifeStage()!=null) lifeStage = aaReactArray[i].getLifeStage().toString();

                    if (StringUtils.empty(entryDate)) entryDate = null;
                    else entryDateFormat = dateFPGetPartial(aaReactArray[i].getRecordedDate());
                    if (StringUtils.empty(startDate)) startDate = null;
                    else startDateFormat = dateFPGetPartial(aaReactArray[i].getStartDate());

                    if (aaReactArray[i].getCode()!=null) regionalId = StringUtils.noNull(aaReactArray[i].getCode().getCodeValue());
                    alg_extra = Util.addLine(alg_extra,"Offending Agent Description: ",aaReactArray[i].getOffendingAgentDescription());
                    if (aaReactArray[i].getReactionType()!=null) alg_extra = Util.addLine(alg_extra,"Reaction Type: ",aaReactArray[i].getReactionType().toString());

                    if (aaReactArray[i].getPropertyOfOffendingAgent()!=null) {
                        if (aaReactArray[i].getPropertyOfOffendingAgent()==cdsDt.PropertyOfOffendingAgent.DR) typeCode=13; //drug
                        else if (aaReactArray[i].getPropertyOfOffendingAgent()==cdsDt.PropertyOfOffendingAgent.ND) typeCode=0; //non-drug
                    else if (aaReactArray[i].getPropertyOfOffendingAgent()==cdsDt.PropertyOfOffendingAgent.UK) typeCode=0; //unknown
                    }
                    if (aaReactArray[i].getSeverity()!=null) {
                        if (aaReactArray[i].getSeverity()==cdsDt.AdverseReactionSeverity.MI) severity="1"; //mild
                        else if (aaReactArray[i].getSeverity()==cdsDt.AdverseReactionSeverity.MO) severity="2"; //moderate
                        else if (aaReactArray[i].getSeverity()==cdsDt.AdverseReactionSeverity.LT) severity="3"; //severe
                        else if (aaReactArray[i].getSeverity()==cdsDt.AdverseReactionSeverity.NO) {
                        	severity="4"; //No reaction, map to unknown
                        	alg_extra = Util.addLine(alg_extra,  "Severity: No reaction");
                        }
                    } else {
                    	severity = "4"; //severity unknown
                    }

                    Date entryDateDate=toDateFromString(entryDate);
                    Date startDateDate=toDateFromString(startDate);
                    Integer allergyId = saveRxAllergy(Integer.valueOf(demographicNo), entryDateDate, description, typeCode, reaction, startDateDate, severity, regionalId, lifeStage);
                    addOneEntry(ALLERGY);

                    //write partial dates
                    if (entryDateFormat!=null) partialDateDao.setPartialDate(PartialDate.ALLERGIES, allergyId.intValue(), PartialDate.ALLERGIES_ENTRYDATE, entryDateFormat);
                    if (startDateFormat!=null) partialDateDao.setPartialDate(PartialDate.ALLERGIES, allergyId.intValue(), PartialDate.ALLERGIES_STARTDATE, startDateFormat);

                    //annotation
                    String note = StringUtils.noNull(aaReactArray[i].getNotes());
                    CaseManagementNote cmNote = prepareCMNote(programId,null);
                    cmNote.setNote(note);
                    saveLinkNote(cmNote, CaseManagementNoteLink.ALLERGIES, Long.valueOf(allergyId));
                }


                //MEDICATIONS & TREATMENTS
                MedicationsAndTreatments[] medArray = patientRec.getMedicationsAndTreatmentsArray();
                logger.info("IMPORT MEDICATIONS & TREATMENTS: " + medArray.length + " entries found");
                String duration, quantity, dosage, special;
                for (int i=0; i<medArray.length; i++) {
                    Drug drug = new Drug();
                    Date writtenDate = dateTimeFPtoDate(medArray[i].getPrescriptionWrittenDate(), timeShiftInDays);
                    drug.setCreateDate(writtenDate);
                    drug.setWrittenDate(writtenDate);
                    String writtenDateFormat = dateFPGetPartial(medArray[i].getPrescriptionWrittenDate());

                    drug.setRxDate(dateFPtoDate(medArray[i].getStartDate(), timeShiftInDays));
                    if (medArray[i].getStartDate()==null) drug.setRxDate(drug.getWrittenDate());

                    duration = medArray[i].getDuration();
                    if (StringUtils.filled(duration)) {
                    	duration = duration.trim();
                    	if (duration.toLowerCase().endsWith("days")) duration = Util.leadingNum(duration);
                    	if (NumberUtils.isDigits(duration)) {
                    		drug.setDuration(duration);
    	                    drug.setDurUnit("D");
                    	}
                    	else err_data.add("Error! Invalid Duration ["+medArray[i].getDuration()+"] for Medications");
                    }

                    quantity = medArray[i].getQuantity();
                    if (StringUtils.filled(quantity)) {
                    	quantity = Util.leadingNum(quantity.trim());
                    	if (NumberUtils.isNumber(quantity)) {
                    		drug.setQuantity(quantity);
                    	}
                    	else err_data.add("Error! Invalid Quantity ["+medArray[i].getQuantity()+"] for Medications");
                    }

                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(drug.getRxDate());
                    if (StringUtils.filled(duration))
                    	endDate.add(Calendar.DAY_OF_YEAR, Integer.valueOf(duration)+timeShiftInDays);
                    drug.setEndDate(endDate.getTime());

                    // coerce the frequency code into oscar's format
                    String freq = StringUtils.noNull(medArray[i].getFrequency());
					int prnPos = freq.toUpperCase().indexOf("PRN");
					drug.setPrn(false);
	
					if (prnPos >= 0) { // frequency code contains PRN
						drug.setPrn(true);
						// Remove PRN from frequency code
						freq = freq.substring(0, prnPos).trim() + freq.substring(prnPos+3).trim();
					}
					drug.setFreqCode(freq);

                    drug.setRegionalIdentifier(medArray[i].getDrugIdentificationNumber());
                    drug.setRoute(medArray[i].getRoute());
                    drug.setDrugForm(medArray[i].getForm());
                    drug.setLongTerm(getYN(medArray[i].getLongTermMedication()).equals("Yes"));
                    drug.setPastMed(getYN(medArray[i].getPastMedications()).equals("Yes"));
                    drug.setPatientCompliance(getYN(medArray[i].getPatientCompliance()));

                    if (NumberUtils.isDigits(medArray[i].getNumberOfRefills())) drug.setRepeat(Integer.valueOf(medArray[i].getNumberOfRefills()));
                    duration = medArray[i].getRefillDuration();
                    if (StringUtils.filled(duration)) {
                    	duration = duration.trim();
                    	if (duration.toLowerCase().endsWith("days")) duration = Util.leadingNum(duration);
                    	if (NumberUtils.isDigits(duration)) drug.setRefillDuration(Integer.valueOf(duration));
                    	else err_data.add("Error! Invalid Refill Duration ["+medArray[i].getRefillDuration()+"] for Medications");
                    }

                    quantity = medArray[i].getRefillQuantity();
                    if (StringUtils.filled(quantity)) {
                    	quantity = Util.leadingNum(quantity.trim());
                    	if (NumberUtils.isNumber(quantity)) drug.setRefillQuantity(Integer.valueOf(quantity));
                    	else err_data.add("Error! Invalid Refill Quantity ["+medArray[i].getRefillQuantity()+"] for Medications");
                    }

                    drug.setETreatmentType(medArray[i].getTreatmentType());

                    String take = StringUtils.noNull(medArray[i].getDosage()).trim();
                    drug.setTakeMin(Util.leadingNumF(take));
                    int sep = take.indexOf("-");
                    if (sep>0) drug.setTakeMax(Util.leadingNumF(take.substring(sep+1)));
                    else drug.setTakeMax(drug.getTakeMin());
                    
                    String unit = medArray[i].getDosageUnitOfMeasure();
                    if ("tablet".equalsIgnoreCase(unit)) unit = "tab";
                    drug.setUnit(unit);

                    drug.setDemographicId(Integer.valueOf(demographicNo));
                    drug.setArchived(false);

                    drug.setBrandName(medArray[i].getDrugName());
                    drug.setCustomName(medArray[i].getDrugDescription());

                    special = StringUtils.noNull(drug.getBrandName());
                    if (special.equals("")) {
                        special = StringUtils.noNull(drug.getCustomName());
                        drug.setCustomInstructions(true);
                    }

                    cdsDt.DrugMeasure strength = medArray[i].getStrength();
                    if (strength!=null) {
                    	String dosageValue = StringUtils.noNull(strength.getAmount());
                    	String dosageUnit = StringUtils.noNull(strength.getUnitOfMeasure());

                    	if (dosageValue.contains("/")) {
                    		String[] dValue = dosageValue.split("/");
                    		String[] dUnit = dosageUnit.split("/");
                    		dosage = dValue[0] + dUnit[0] + " / " + dValue[1] + (dUnit.length>1 ? dUnit[1] : "unit");
                    	} else {
                    		dosage = dosageValue + " " + dosageUnit;
                    	}
                		drug.setDosage(dosage);
                    }

                    special = addSpaced(special, medArray[i].getDosage());
                    special = addSpaced(special, drug.getRoute());
                    special = addSpaced(special, drug.getFreqCode());

                    if (drug.getDuration()!=null) {
                    	special = addSpaced(special, "for "+drug.getDuration()+" days");
                    }
                    drug.setSpecial(special);

                    if (StringUtils.filled(medArray[i].getPrescriptionInstructions())) {
                    	drug.setSpecialInstruction(medArray[i].getPrescriptionInstructions());
                    }

                    if (medArray[i].getPrescribedBy()!=null) {
                        HashMap<String,String> personName = getPersonName(medArray[i].getPrescribedBy().getName());
                        String personOHIP = medArray[i].getPrescribedBy().getOHIPPhysicianId();
                        ProviderData pd = getProviderByOhip(personOHIP);
                        if (pd!=null && Integer.valueOf(pd.getProviderNo())>-1000) drug.setProviderNo(pd.getProviderNo());
                        else { //outside provider
                            drug.setOutsideProviderName(StringUtils.noNull(personName.get("lastname"))+", "+StringUtils.noNull(personName.get("firstname")));
                            drug.setOutsideProviderOhip(personOHIP);
                            drug.setProviderNo(writeProviderData(personName.get("firstname"), personName.get("lastname"), personOHIP));
                        }
                    } else {
                        drug.setProviderNo(admProviderNo);
                    }
                    
                    if( drug.getProviderNo() == null ) {
                    	drug.setProviderNo("-1");
                    }
                    
                    drug.setPosition(0);
                    drug.setDispenseInterval(0);
                    drugDao.persist(drug);
                    addOneEntry(MEDICATION);

                    //partial date
                    partialDateDao.setPartialDate(PartialDate.DRUGS, drug.getId(), PartialDate.DRUGS_WRITTENDATE, writtenDateFormat);

                    //annotation
                    CaseManagementNote cmNote = prepareCMNote(programId,null);
                    String note = StringUtils.noNull(medArray[i].getNotes());
                    cmNote.setNote(note);
                    saveLinkNote(cmNote, CaseManagementNoteLink.DRUGS, (long)drug.getId());
                }


                //IMMUNIZATIONS
                Immunizations[] immuArray = patientRec.getImmunizationsArray();
                logger.info("IMPORT IMMUNIZATIONS: " + immuArray.length + " entries found");
                for (int i=0; i<immuArray.length; i++) {
                    String preventionDate="", refused="0";
                    String preventionType=null, immExtra=null;
                    ArrayList<Map<String,String>> preventionExt = new ArrayList<Map<String,String>>();

                    if (StringUtils.filled(immuArray[i].getImmunizationName())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("name", immuArray[i].getImmunizationName());
                        preventionExt.add(ht);
                    } else {
                        err_data.add("Error! No Immunization Name ("+(i+1)+")");
                    }

                    if (immuArray[i].getImmunizationType()!=null)
                        preventionType = Util.getPreventionType(immuArray[i].getImmunizationType().toString());
//					if (preventionType==null)
//                    	preventionType = mapPreventionTypeByCode(immuArray[i].getImmunizationCode());
                    if (preventionType==null) {
                    	preventionType = "OtherA";
                    	err_note.add("Cannot map Immunization Type, "+immuArray[i].getImmunizationName()+" mapped to Other Layout A");
                    }

                    if (StringUtils.filled(immuArray[i].getManufacturer())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("manufacture", immuArray[i].getManufacturer());
                        preventionExt.add(ht);
                    }
                    if (StringUtils.filled(immuArray[i].getLotNumber())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("lot", immuArray[i].getLotNumber());
                        preventionExt.add(ht);
                    }
                    if (StringUtils.filled(immuArray[i].getRoute())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("route", immuArray[i].getRoute());
                        preventionExt.add(ht);
                    }
                    if (StringUtils.filled(immuArray[i].getSite())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("location", immuArray[i].getSite());
                        preventionExt.add(ht);
                    }
                    if (StringUtils.filled(immuArray[i].getDose())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("dose", immuArray[i].getDose());
                        preventionExt.add(ht);
                    }

                    if (StringUtils.filled(immuArray[i].getNotes())) {
                        Map<String,String> ht = new HashMap<String,String>();
                        ht.put("comments", immuArray[i].getNotes());
                        preventionExt.add(ht);
                    }

                    preventionDate = dateFPtoString(immuArray[i].getDate(), timeShiftInDays);
                    refused = getYN(immuArray[i].getRefusedFlag()).equals("Yes") ? "1" : "0";
                    if (immuArray[i].getRefusedFlag()==null) err_data.add("Error! No Refused Flag for Immunizations ("+(i+1)+")");

                    immExtra = Util.addLine(immExtra, getCode(immuArray[i].getImmunizationCode(),"Immunization Code"));
                    immExtra = Util.addLine(immExtra, "Instructions: ", immuArray[i].getInstructions());
                    immExtra = Util.addLine(immExtra, getResidual(immuArray[i].getResidualInfo()));

                    Integer preventionId = PreventionData.insertPreventionData(admProviderNo, demographicNo, preventionDate, defaultProviderNo(), "", preventionType, refused, "", "", preventionExt);
                    addOneEntry(IMMUNIZATION);
                }

                //LABORATORY RESULTS
                LaboratoryResults[] labResultArr = patientRec.getLaboratoryResultsArray();
                logger.info("IMPORT LAB RESULTS: " + labResultArr.length + " entries found");
                String[] _accession = new String[labResultArr.length];
                String[] _coll_date = new String[labResultArr.length];
                String[] _title	    = new String[labResultArr.length];
                String[] _testName  = new String[labResultArr.length];
                String[] _abn	    = new String[labResultArr.length];
                String[] _minimum   = new String[labResultArr.length];
                String[] _maximum   = new String[labResultArr.length];
                String[] _result    = new String[labResultArr.length];
                String[] _unit	    = new String[labResultArr.length];
                String[] _labnotes  = new String[labResultArr.length];
                String[] _location  = new String[labResultArr.length];
                String[] _reviewer  = new String[labResultArr.length];
                String[] _lab_ppid  = new String[labResultArr.length];
                String[] _rev_date  = new String[labResultArr.length];
                String[] _req_date  = new String[labResultArr.length];

                // Save to labPatientPhysicianInfo, labTestResults, patientLabRouting
                for (int i=0; i<labResultArr.length; i++) {
                    _location[i] = StringUtils.noNull(labResultArr[i].getLaboratoryName());
                    _accession[i] = StringUtils.noNull(labResultArr[i].getAccessionNumber());
                    _coll_date[i] = dateFPtoString(labResultArr[i].getCollectionDateTime(), timeShiftInDays);
                    
                    if(_coll_date[i].length() > 10) { //hack to ensure the date string is max 10 digits.
                    	_coll_date[i] = _coll_date[i].substring(0, 11);
                    }
                    _req_date[i] = dateFPtoString(labResultArr[i].getLabRequisitionDateTime(), timeShiftInDays);
                    if (StringUtils.empty(_req_date[i])) _req_date[i] = _coll_date[i];

                    _testName[i] = StringUtils.noNull(labResultArr[i].getTestName());
                    if (StringUtils.filled(labResultArr[i].getTestNameReportedByLab())) {
                    	_testName[i] += StringUtils.filled(_testName[i]) ? " / " : "";
                    	_testName[i] += labResultArr[i].getTestNameReportedByLab();
                    }
                    _title[i] = _testName[i];

                    if (StringUtils.filled(labResultArr[i].getNotesFromLab()))
                        _labnotes[i] = "Notes: "+labResultArr[i].getNotesFromLab();

                    if (labResultArr[i].getResultNormalAbnormalFlag()!=null) {
                        cdsDt.ResultNormalAbnormalFlag.Enum flag = labResultArr[i].getResultNormalAbnormalFlag();
                        if (flag.equals(cdsDt.ResultNormalAbnormalFlag.Y)) _abn[i] = "A";
                        if (flag.equals(cdsDt.ResultNormalAbnormalFlag.N)) _abn[i] = "N";
                    }

                    if (labResultArr[i].getResult()!=null) {
                        _result[i] = StringUtils.noNull(labResultArr[i].getResult().getValue());
                        _unit[i] = StringUtils.noNull(labResultArr[i].getResult().getUnitOfMeasure());
                    }

                    if (labResultArr[i].getReferenceRange()!=null) {
                        LaboratoryResults.ReferenceRange ref = labResultArr[i].getReferenceRange();
                        if (StringUtils.filled(ref.getReferenceRangeText())) {
                            _minimum[i] = ref.getReferenceRangeText();
                        } else {
                            _maximum[i] = StringUtils.noNull(ref.getHighLimit());
                            _minimum[i] = StringUtils.noNull(ref.getLowLimit());
                        }
                    }

                    LaboratoryResults.ResultReviewer[] resultReviewers = labResultArr[i].getResultReviewerArray();
                    if (resultReviewers.length>0) {
                        HashMap<String,String> revName = getPersonName(resultReviewers[0].getName());
                        String revOhip = StringUtils.noNull(resultReviewers[0].getOHIPPhysicianId());
                        _reviewer[i] = writeProviderData(revName.get("firstname"), revName.get("lastname"), revOhip);
                        _rev_date[i] = dateFPtoString(resultReviewers[0].getDateTimeResultReviewed(), timeShiftInDays);
                    }
                }

                ArrayList<String> uniq_labs = getUniques(_location);
                ArrayList<String> uniq_accs = getUniques(_accession);
                for (String lab : uniq_labs) {
                    boolean labNew = true;
                    String rptInfoId="";
                    for (String acc : uniq_accs) {
                        boolean accNew = true;
                        String paPhysId="";
                        for (int i=0; i<_location.length; i++) {
                            if (!(_location[i].equals(lab) && _accession[i].equals(acc))) continue;

                            if (labNew) {
                                rptInfoId = LabResultImport.saveLabReportInfo(_location[i]);
                                labNew = false;
                            }
                            if (accNew) {
                                paPhysId = LabResultImport.saveLabPatientPhysicianInfo(rptInfoId, _accession[i], _coll_date[i], firstName, lastName, sex, hin, birthDate, patientPhone);

                                LabResultImport.savePatientLabRouting(demographicNo, paPhysId);
                                LabRequestReportLink.save(null,null,_req_date[i],"labPatientPhysicianInfo",Long.valueOf(paPhysId));

                                String status = StringUtils.filled(_reviewer[i]) ? "A" : "N";
                                _reviewer[i] = status.equals("A") ? _reviewer[i] : "0";
                                LabResultImport.saveProviderLabRouting(_reviewer[i], paPhysId, status, "", _rev_date[i]);

                                accNew = false;
                            }
                            LabResultImport.saveLabTestResults(_title[i], _testName[i], _abn[i], _minimum[i], _maximum[i], _result[i], _unit[i], "", _location[i], paPhysId, "C", "Y");
                            LabResultImport.saveLabTestResults(_title[i], _testName[i], "", "", "", "", "", _labnotes[i], _location[i], paPhysId, "D", "Y");
                            addOneEntry(LABS);

                            _lab_ppid[i] = paPhysId;
                        }
                    }
                }

                // Save to measurements, measurementsExt
                for (LaboratoryResults labResults : labResultArr) {
                    Measurements meas = new Measurements(Long.valueOf(demographicNo), admProviderNo);
                    LaboratoryResults.Result result = labResults.getResult();

                    //save lab result & get unit
                    String unit = null;
                    if (result!=null) {
                        meas.setDataField(StringUtils.noNull(result.getValue()));
                        unit = StringUtils.noNull(result.getUnitOfMeasure());
                    } else {
                        meas.setDataField("");
                    }

                    //save lab result unit
                    meas.setDateEntered(new Date());
                    meas.setMeasuringInstruction("");
                    ImportExportMeasurements.saveMeasurements(meas);
                    Long measId = meas.getId();
                    saveMeasurementsExt(measId, "unit", unit);

                    //save lab test code, test name
                    String testCode = labResults.getLabTestCode();
                    String testName = labResults.getTestName();
                    String testNameLab = labResults.getTestNameReportedByLab();

                    saveMeasurementsExt(measId, "identifier", testCode);
                    saveMeasurementsExt(measId, "name_internal", testName);
                    saveMeasurementsExt(measId, "name", testNameLab);

                    //save lab collection datetime
                    cdsDt.DateTimeFullOrPartial collDate = labResults.getCollectionDateTime();
                    String coll_date = null;
                    if (collDate!=null) {
                    	coll_date = dateFPtoString(collDate, timeShiftInDays);
                        saveMeasurementsExt(measId, "datetime", coll_date);
                    } else {
                        err_data.add("Error! No Collection DateTime for Lab Test "+testCode+" for Patient "+demographicNo);
                    }

                    //save lab requisition datetime
                    cdsDt.DateTimeFullOrPartial reqDate = labResults.getLabRequisitionDateTime();
                    if (reqDate!=null) {
                    	saveMeasurementsExt(measId, "request_datetime", dateFPtoString(reqDate, timeShiftInDays));
                    }

                    //save laboratory name
                    String labname = StringUtils.noNull(labResults.getLaboratoryName());
                    if (StringUtils.filled(labname)) {
                        saveMeasurementsExt(measId, "labname", labname);
                    } else {
                        err_data.add("Error! No Laboratory Name for Lab Test "+testCode+" for Patient "+demographicNo);
                    }

                    //save lab normal/abnormal flag
                    cdsDt.ResultNormalAbnormalFlag.Enum abnFlag = labResults.getResultNormalAbnormalFlag();
                    if (abnFlag!=null) {
                        String abn = abnFlag.toString();
                        if (!abn.equals("U")) {
                            saveMeasurementsExt(measId, "abnormal", (abn.equals("Y")?"A":abn));
                        }
                    }

                    //save lab accession number
                    String accnum = labResults.getAccessionNumber();
                    saveMeasurementsExt(measId, "accession", accnum);

                    //save lab reference range
                    LaboratoryResults.ReferenceRange refRange = labResults.getReferenceRange();
                    if (refRange!=null) {
                        if (StringUtils.filled(refRange.getReferenceRangeText())) {
                            saveMeasurementsExt(measId, "range", refRange.getReferenceRangeText());
                        } else {
                            saveMeasurementsExt(measId, "maximum", refRange.getHighLimit());
                            saveMeasurementsExt(measId, "minimum", refRange.getLowLimit());
                        }
                    }

                    //save OLIS test result status
                    String olis_status = labResults.getOLISTestResultStatus();
                    if (StringUtils.filled(olis_status)) saveMeasurementsExt(measId, "olis_status", olis_status);

                    //save notes from lab
                    String labNotes = labResults.getNotesFromLab();
                    if (StringUtils.filled(labNotes)) saveMeasurementsExt(measId, "comments", labNotes);

                    //retrieve lab_ppid
                    String lab_ppid = null;
                    for (int i=0; i<labResultArr.length; i++) {
                        if (!(_location[i].equals(labname) && _coll_date[i].equals(coll_date))) continue;
                        saveMeasurementsExt(measId, "lab_ppid", _lab_ppid[i]);
                        lab_ppid = _lab_ppid[i];
                        break;
                    }

                    if (lab_ppid!=null) {

                        //save lab physician notes (annotation)
                        String annotation = labResults.getPhysiciansNotes();
                        if (StringUtils.filled(annotation)) {
                            saveMeasurementsExt(measId, "other_id", "0-0");
                            CaseManagementNote cmNote = prepareCMNote(programId,null);
                            cmNote.setNote(annotation);
                            saveLinkNote(cmNote, CaseManagementNoteLink.LABTEST2, Long.valueOf(lab_ppid), "0-0");
                        }
                    } else {
                        logger.error("No lab no! (demo="+demographicNo+")");
                    }
                }

                //APPOINTMENTS
                Appointments[] appArray = patientRec.getAppointmentsArray();
                Date appointmentDate = null;
                String notes="", reason="", status="", startTime="", endTime="", apptProvider="";
                ApptStatusData asd = new ApptStatusData();
                String[] allStatus = asd.getAllStatus();
                String[] allTitle = asd.getAllTitle();

                for (int i=0; i<appArray.length; i++) {
                    String apptDateStr = dateFPtoString(appArray[i].getAppointmentDate(), timeShiftInDays);
                    if (StringUtils.filled(apptDateStr)) {
                        appointmentDate = UtilDateUtilities.StringToDate(apptDateStr);
                    } else {
                        err_data.add("Error! No Appointment Date ("+(i+1)+")");
                    }
                    if (appArray[i].getAppointmentTime()!=null) {
                        startTime = getCalTime(appArray[i].getAppointmentTime());
                        if (appArray[i].getDuration()!=null) {
                            Date d_startTime = appArray[i].getAppointmentTime().getTime();
                            Date d_endTime = new Date();
                            d_endTime.setTime(d_startTime.getTime() + (appArray[i].getDuration().longValue()-1)*60000);
                            endTime = UtilDateUtilities.DateToString(d_endTime,"HH:mm:ss");
                        } else {
                            Date d_startTime = appArray[i].getAppointmentTime().getTime();
                            Date d_endTime = new Date();
                            d_endTime.setTime(d_startTime.getTime() + 14*60000);
                            endTime = UtilDateUtilities.DateToString(d_endTime,"HH:mm:ss");
                        }
                    } else {
                        err_data.add("Error! No Appointment Time ("+(i+1)+")");
                    }
                    if (StringUtils.filled(appArray[i].getAppointmentNotes())) {
                        notes = appArray[i].getAppointmentNotes();
                    }
                    String apptStatus = appArray[i].getAppointmentStatus();
                    for (int j=1; j<allStatus.length; j++) {
                        if (allTitle[j].equalsIgnoreCase(org.apache.commons.lang.StringUtils.trimToEmpty(apptStatus))) {
                            status = allStatus[j];
                            apptStatus = null;
                            break;
                        }
                    }
                    if (StringUtils.empty(status)) {
                        status = allStatus[0];
                    }

                    reason = StringUtils.noNull(appArray[i].getAppointmentPurpose());
                    if (appArray[i].getProvider()!=null) {
                        HashMap<String,String> providerName = getPersonName(appArray[i].getProvider().getName());
                        String personOHIP = appArray[i].getProvider().getOHIPPhysicianId();
                        apptProvider = writeProviderData(providerName.get("firstname"), providerName.get("lastname"), personOHIP);
                        if (StringUtils.empty(apptProvider)) {
                            apptProvider = defaultProviderNo();
                            err_note.add("Appointment has no provider; assigned to \"doctor oscardoc\" ("+(i+1)+")");
                        }
                    }
                   
                    Appointment appt = new Appointment();
                    appt.setProviderNo(apptProvider);
                    appt.setAppointmentDate(appointmentDate);
                    appt.setStartTime(ConversionUtils.fromTimeString(startTime));
                    appt.setEndTime(ConversionUtils.fromTimeString(endTime));
                    appt.setName(patientName);
                    appt.setDemographicNo(Integer.parseInt(demographicNo));
                    appt.setCreator(admProviderNo);
                    appt.setLastUpdateUser(admProviderNo);
                    if (defaultSite != null && !defaultSite.isEmpty())
                    {
                        appt.setLocation(defaultSite);
                    }
                    appt.setNotes(notes);
                    appt.setReason(reason);
                    appt.setStatus(status);
                    appt.setImportedStatus(apptStatus);
                    
                    appointmentDao.persist(appt);
                    
                    addOneEntry(APPOINTMENT);
                }

                //REPORTS RECEIVED

                HRMDocumentDao hrmDocDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
                HRMDocumentCommentDao hrmDocCommentDao = (HRMDocumentCommentDao) SpringUtils.getBean("HRMDocumentCommentDao");
                HRMDocumentSubClassDao hrmDocSubClassDao = (HRMDocumentSubClassDao) SpringUtils.getBean("HRMDocumentSubClassDao");
                HRMDocumentToDemographicDao hrmDocToDemoDao = (HRMDocumentToDemographicDao) SpringUtils.getBean("HRMDocumentToDemographicDao");

                ReportsReceived[] repR = patientRec.getReportsReceivedArray();
                List<ReportsReceived> HRMreports = new ArrayList<ReportsReceived>();
                String HRMfile = docDir + "HRM_"+UtilDateUtilities.getToday("yyyy-MM-dd.HH.mm.ss");
                for (int i=0; i<repR.length; i++) {

                    if (repR[i].getHRMResultStatus()!=null || repR[i].getOBRContentArray().length>0) { //HRM reports
                        HRMDocument hrmDoc = new HRMDocument();
                        HRMDocumentComment hrmDocComment = new HRMDocumentComment();
                        HRMDocumentToDemographic hrmDocToDemo = new HRMDocumentToDemographic();

                        hrmDoc.setReportFile(HRMfile);
                        if (repR[i].getSourceFacility()!=null) hrmDoc.setSourceFacility(repR[i].getSourceFacility());
                        if (repR[i].getReceivedDateTime()!=null) {
                            hrmDoc.setTimeReceived(dateTimeFPtoDate(repR[i].getReceivedDateTime(), timeShiftInDays));
                        } else {
                            hrmDoc.setTimeReceived(new Date());
                        }
                        if (repR[i].getHRMResultStatus()!=null) hrmDoc.setReportStatus(repR[i].getHRMResultStatus());
                        if (repR[i].getClass1()!=null) hrmDoc.setReportType(repR[i].getClass1().toString());
                        if (repR[i].getEventDateTime()!=null) hrmDoc.setReportDate(dateTimeFPtoDate(repR[i].getEventDateTime(), timeShiftInDays));
                        hrmDocDao.persist(hrmDoc);

                        if (repR[i].getNotes()!=null) {
                            hrmDocComment.setHrmDocumentId(hrmDoc.getId());
                            hrmDocComment.setComment(repR[i].getNotes());
                            hrmDocCommentDao.persist(hrmDocComment);
                        }

                        hrmDocToDemo.setDemographicNo(demographicNo);
                        hrmDocToDemo.setHrmDocumentId(hrmDoc.getId().toString());
                        hrmDocToDemoDao.persist(hrmDocToDemo);

                        ReportsReceived.OBRContent[] obr = repR[i].getOBRContentArray();
                        for (int j=0; j<obr.length; j++) {
                            HRMDocumentSubClass hrmDocSc = new HRMDocumentSubClass();
                            if (obr[j].getAccompanyingSubClass()!=null) hrmDocSc.setSubClass(obr[j].getAccompanyingSubClass());
                            if (obr[j].getAccompanyingDescription()!=null) hrmDocSc.setSubClassDescription(obr[j].getAccompanyingDescription());
                            if (obr[j].getAccompanyingMnemonic()!=null) hrmDocSc.setSubClassMnemonic(obr[j].getAccompanyingMnemonic());
                            if (obr[j].getObservationDateTime()!=null) hrmDocSc.setSubClassDateTime(dateTimeFPtoDate(obr[j].getObservationDateTime(), timeShiftInDays));
                            hrmDocSc.setHrmDocumentId(hrmDoc.getId());
                            hrmDocSc.setActive(true);
                            hrmDocSubClassDao.persist(hrmDocSc);
                        }
                        HRMreports.add(repR[i]);

                    } else { //non-HRM reports
                        boolean binaryFormat = false;
                        if (repR[i].getFormat()!=null) {
                            if (repR[i].getFormat().equals(cdsDt.ReportFormat.BINARY)) binaryFormat = true;
                        } else {
                            err_data.add("Error! No Report Format for Report ("+(i+1)+")");
                        }
                        cdsDt.ReportContent repCt = repR[i].getContent();
                        if (repCt!=null) {
                            byte[] byteArray = null;
                            if (repCt.getMedia()!=null) byteArray = repCt.getMedia();
                            else if (repCt.getTextContent()!=null) byteArray = repCt.getTextContent().getBytes();
                            if (byteArray==null) {
                                err_othe.add("Error! No report file in xml ("+(i+1)+")");
                            } else {
                                String docFileName = "ImportReport"+(i+1)+"-"+UtilDateUtilities.getToday("yyyy-MM-dd.HH.mm.ss");
                                String docClass=null, docSubClass=null, contentType="", contentDateTime=null, observationDate=null, updateDateTime=null, docCreator=admProviderNo;
                                String reviewer=null, reviewDateTime=null, source=null, sourceFacility=null, reportExtra=null;

                                if (StringUtils.filled(repR[i].getFileExtensionAndVersion())) {
                                    contentType = repR[i].getFileExtensionAndVersion();
                                    docFileName += Util.mimeToExt(contentType);
                                } else {
                                    if (binaryFormat) err_data.add("Error! No File Extension for Report ("+(i+1)+")");
                                }
                                String docDesc = repR[i].getSubClass();
                                if (StringUtils.empty(docDesc)) docDesc = repR[i].getNotes();
                                if (StringUtils.empty(docDesc)) docDesc = "ImportReport"+(i+1);

                                if (repR[i].getClass1()!=null) {
                                    docClass = repR[i].getClass1().toString();
                                } else {
                                    err_data.add("Error! No Class Type for Report ("+(i+1)+")");
                                }
                                if (repR[i].getSubClass()!=null) {
                                    docSubClass = repR[i].getSubClass();
                                }

                                if (repR[i].getSourceFacility()!=null) {
                                	sourceFacility = repR[i].getSourceFacility();
                                }

                                if (repR[i].getMedia()!=null) {
                                	reportExtra = Util.addLine(reportExtra, "Media: ", repR[i].getMedia().toString());
                                }

                                ReportsReceived.SourceAuthorPhysician authorPhysician = repR[i].getSourceAuthorPhysician();
                                if (authorPhysician!=null) {
                                    if (authorPhysician.getAuthorName()!=null) {
                                        HashMap<String,String> author = getPersonName(authorPhysician.getAuthorName());
                                        source = StringUtils.noNull(author.get("firstname")) + " " + StringUtils.noNull(author.get("lastname"));
                                    } else if (authorPhysician.getAuthorFreeText()!=null) {
                                        source = authorPhysician.getAuthorFreeText();
                                    }
                                }

                                ReportsReceived.ReportReviewed[] reportReviewed = repR[i].getReportReviewedArray();
                                if (reportReviewed.length>0) {
                                    HashMap<String,String> reviewerName = getPersonName(reportReviewed[0].getName());
                                    reviewer = writeProviderData(reviewerName.get("firstname"), reviewerName.get("lastname"), reportReviewed[0].getReviewingOHIPPhysicianId());
                                    reviewDateTime = dateFPtoString(reportReviewed[0].getDateTimeReportReviewed(), timeShiftInDays);
                                }

                                InputStream fileInputStream = new ByteArrayInputStream(byteArray);
                                observationDate = dateFPtoString(repR[i].getEventDateTime(), timeShiftInDays);
                                contentDateTime= dateFPtoString(repR[i].getReceivedDateTime(), timeShiftInDays);

	                            Document document = new Document();
	                            document.setPublic1(false);
	                            document.setResponsible(admProviderNo);
	                            document.setDocCreator(docCreator);
	                            document.setDocdesc(docDesc);
	                            document.setDoctype("");
	                            document.setDocfilename(docFileName);
	                            document.setSource(source);
	                            document.setSourceFacility(sourceFacility);
	                            document.setDocClass(docClass);
	                            document.setDocSubClass(docSubClass);
	                            document.setObservationdate(MyDateFormat.getSysDate(observationDate));
	                            document.setContentdatetime(MyDateFormat.getSysDate(contentDateTime));
	                            document.setReviewer(reviewer);
	                            document.setReviewdatetime(MyDateFormat.getSysDate(reviewDateTime));

	                            documentService.uploadNewDemographicDocument(document, fileInputStream, Integer.parseInt(demographicNo));

                                if (binaryFormat) addOneEntry(REPORTBINARY);
                                else addOneEntry(REPORTTEXT);
                            }
                        }
                    }
                }
                CreateHRMFile.create(demo, HRMreports, HRMfile);

                //CARE ELEMENTS
                CareElements[] careElems = patientRec.getCareElementsArray();
                for (int i=0; i<careElems.length; i++) {
                    CareElements ce = careElems[i];
                    cdsDt.Height[] heights = ce.getHeightArray();
                    for (cdsDt.Height ht : heights) {
                        Date dateObserved = ht.getDate()!=null ? ht.getDate().getTime() : null;
                        String dataField = StringUtils.noNull(ht.getHeight());
                        String dataUnit = ht.getHeightUnit()!=null ? "in "+ht.getHeightUnit().toString() : "";

                        if (ht.getDate()==null) err_data.add("Error! No Date for Height in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(ht.getHeight())) err_data.add("Error! No value for Height in Care Element ("+(i+1)+")");
                        if (ht.getHeightUnit()==null) err_data.add("Error! No unit for Height in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("HT", demographicNo, admProviderNo, dataField, dataUnit, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.Weight[] weights = ce.getWeightArray();
                    for (cdsDt.Weight wt : weights) {
                        Date dateObserved = wt.getDate()!=null ? wt.getDate().getTime() : null;
                        String dataField = StringUtils.noNull(wt.getWeight());
                        String dataUnit = wt.getWeightUnit()!=null ? "in "+wt.getWeightUnit().toString() : "";

                        if (wt.getDate()==null) err_data.add("Error! No Date for Weight in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(wt.getWeight())) err_data.add("Error! No value for Weight in Care Element ("+(i+1)+")");
                        if (wt.getWeightUnit()==null) err_data.add("Error! No unit for Weight in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("WT", demographicNo, admProviderNo, dataField, dataUnit, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.WaistCircumference[] waists = ce.getWaistCircumferenceArray();
                    for (cdsDt.WaistCircumference wc : waists) {
                        Date dateObserved = wc.getDate()!=null ? wc.getDate().getTime() : null;
                        String dataField = StringUtils.noNull(wc.getWaistCircumference());
                        String dataUnit = wc.getWaistCircumferenceUnit()!=null ? "in "+wc.getWaistCircumferenceUnit().toString() : "";

                        if (wc.getDate()==null) err_data.add("Error! No Date for Waist Circumference in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(wc.getWaistCircumference())) err_data.add("Error! No value for Waist Circumference in Care Element ("+(i+1)+")");
                        if (wc.getWaistCircumferenceUnit()==null) err_data.add("Error! No unit for Waist Circumference in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("WC", demographicNo, admProviderNo, dataField, dataUnit, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.BloodPressure[] bloodp = ce.getBloodPressureArray();
                    for (cdsDt.BloodPressure bp : bloodp) {
                        Date dateObserved = bp.getDate()!=null ? bp.getDate().getTime() : null;
                        String dataField = StringUtils.noNull(bp.getSystolicBP())+"/"+StringUtils.noNull(bp.getDiastolicBP());
                        String dataUnit = bp.getBPUnit()!=null ? "in "+bp.getBPUnit().toString() : "";

                        if (bp.getDate()==null) err_data.add("Error! No Date for Blood Pressure in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(bp.getSystolicBP())) err_data.add("Error! No value for Systolic Blood Pressure in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(bp.getDiastolicBP())) err_data.add("Error! No value for Diastolic Blood Pressure in Care Element ("+(i+1)+")");
                        if (bp.getBPUnit()==null) err_data.add("Error! No unit for Blood Pressure in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("BP", demographicNo, admProviderNo, dataField, dataUnit, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.SmokingStatus[] smoks = ce.getSmokingStatusArray();
                    for (cdsDt.SmokingStatus ss : smoks) {
                        Date dateObserved = ss.getDate()!=null ? ss.getDate().getTime() : null;
                        String dataField = ss.getStatus()!=null ? ss.getStatus().toString() : "";

                        if (ss.getDate()==null) err_data.add("Error! No Date for Smoking Status in Care Element ("+(i+1)+")");
                        if (ss.getStatus()==null) err_data.add("Error! No value for Smoking Status in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("SKST", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.SmokingPacks[] smokp = ce.getSmokingPacksArray();
                    for (cdsDt.SmokingPacks sp : smokp) {
                        Date dateObserved = sp.getDate()!=null ? sp.getDate().getTime() : null;
                        String dataField = sp.getPerDay()!=null ? sp.getPerDay().toString() : "";

                        if (sp.getDate()==null) err_data.add("Error! No Date for Smoking Packs/Day in Care Element ("+(i+1)+")");
                        if (sp.getPerDay()==null) err_data.add("Error! No value for Smoking Packs/Day in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("POSK", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.SelfMonitoringBloodGlucose[] smbg = ce.getSelfMonitoringBloodGlucoseArray();
                    for (cdsDt.SelfMonitoringBloodGlucose sg : smbg) {
                        Date dateObserved = sg.getDate()!=null ? sg.getDate().getTime() : null;
                        String dataField = sg.getSelfMonitoring()!=null ? sg.getSelfMonitoring().toString() : "";

                        if (sg.getDate()==null) err_data.add("Error! No Date for Self-monitoring Blood Glucose in Care Element ("+(i+1)+")");
                        if (sg.getSelfMonitoring()==null) err_data.add("Error! No value for Self-monitoring Blood Glucose in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("SMBG", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.DiabetesEducationalSelfManagement[] desm = ce.getDiabetesEducationalSelfManagementArray();
                    for (cdsDt.DiabetesEducationalSelfManagement dm : desm) {
                        Date dateObserved = dm.getDate()!=null ? dm.getDate().getTime() : null;
                        String dataField = dm.getEducationalTrainingPerformed()!=null ? dm.getEducationalTrainingPerformed().toString() : null;

                        if (dm.getDate()==null) err_data.add("Error! No Date for Diabetes Educational/Self-management Training in Care Element ("+(i+1)+")");
                        if (dm.getEducationalTrainingPerformed()==null) err_data.add("Error! No value for Diabetes Educational/Self-management Training in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("DMME", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.DiabetesSelfManagementChallenges[] dsmc = ce.getDiabetesSelfManagementChallengesArray();
                    for (cdsDt.DiabetesSelfManagementChallenges dc : dsmc) {
                        Date dateObserved = dc.getDate().getTime();
                        String dataField = dc.getChallengesIdentified().toString();
                        String dataCode = dc.getCodeValue()!=null ? "LOINC="+dc.getCodeValue().toString() : "";

                        if (dc.getDate()==null) err_data.add("Error! No Date for Diabetes Self-management Challenges in Care Element ("+(i+1)+")");
                        if (dc.getChallengesIdentified()==null) err_data.add("Error! No Challenges Identified for Diabetes Self-management Challenges in Care Element ("+(i+1)+")");
                        if (dc.getCodeValue()==null) err_data.add("Error! No Code value for Diabetes Self-management Challenges in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("SMCD", demographicNo, admProviderNo, dataField, dataCode, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.DiabetesMotivationalCounselling[] dmc = ce.getDiabetesMotivationalCounsellingArray();
                    for (cdsDt.DiabetesMotivationalCounselling dc : dmc) {
                        Date dateObserved = dc.getDate()!=null ? dc.getDate().getTime() : null;
                        String dataField = "Yes";
                        if (dc.getCounsellingPerformed()!=null) {
                            if (dc.getCounsellingPerformed().equals(CounsellingPerformed.NUTRITION)) {
                                ImportExportMeasurements.saveMeasurements("MCCN", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            }
                            else if (dc.getCounsellingPerformed().equals(CounsellingPerformed.EXERCISE)) {
                                ImportExportMeasurements.saveMeasurements("MCCE", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            }
                            else if (dc.getCounsellingPerformed().equals(CounsellingPerformed.SMOKING_CESSATION)) {
                                ImportExportMeasurements.saveMeasurements("MCCS", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            }
                            else if (dc.getCounsellingPerformed().equals(CounsellingPerformed.OTHER)) {
                                ImportExportMeasurements.saveMeasurements("MCCO", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            }
                        }
                        if (dc.getDate()==null) err_data.add("Error! No Date for Diabetes Motivational Counselling in Care Element ("+(i+1)+")");
                        if (dc.getCounsellingPerformed()==null) err_data.add("Error! No Diabetes Motivational Counselling Performed value for Diabetes Self-management Challenges in Care Element ("+(i+1)+")");
                    }
                    cdsDt.DiabetesComplicationScreening[] dcs = ce.getDiabetesComplicationsScreeningArray();
                    for (cdsDt.DiabetesComplicationScreening ds : dcs) {
                        Date dateObserved = ds.getDate()!=null ? ds.getDate().getTime() : null;
                        String dataField = "Yes";
                        if (ds.getExamCode()!=null) {
                            if (ds.getExamCode().equals(ExamCode.X_32468_1)) {
                                ImportExportMeasurements.saveMeasurements("EYEE", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            } else if (ds.getExamCode().equals(ExamCode.X_11397_7)) {
                                ImportExportMeasurements.saveMeasurements("FTE", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            } else if (ds.getExamCode().equals(ExamCode.NEUROLOGICAL_EXAM)) {
                                ImportExportMeasurements.saveMeasurements("FTLS", demographicNo, admProviderNo, dataField, dateObserved);
                                addOneEntry(CAREELEMENTS);
                            }
                        }
                        if (ds.getDate()==null) err_data.add("Error! No Date for Diabetes Complications Screening in Care Element ("+(i+1)+")");
                        if (ds.getExamCode()==null) err_data.add("Error! No Exam Code for Diabetes Complications Screening in Care Element ("+(i+1)+")");
                    }
                    cdsDt.DiabetesSelfManagementCollaborative[] dsco = ce.getDiabetesSelfManagementCollaborativeArray();
                    for (cdsDt.DiabetesSelfManagementCollaborative dc : dsco) {
                        Date dateObserved = dc.getDate()!=null ? dc.getDate().getTime() : null;
                        String dataField = StringUtils.noNull(dc.getDocumentedGoals());
                        if (dc.getDate()==null) err_data.add("Error! No Date for Diabetes Self-management/Collaborative Goal Setting in Care Element ("+(i+1)+")");
                        if (StringUtils.empty(dc.getDocumentedGoals())) err_data.add("Error! No Documented Goal for Diabetes Self-management/Collaborative Goal Setting in Care Element ("+(i+1)+")");
                        if (dc.getCodeValue()==null) err_data.add("Error! No Code Value for Diabetes Self-management/Collaborative Goal Setting in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("CGSD", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                    cdsDt.HypoglycemicEpisodes[] hype = ce.getHypoglycemicEpisodesArray();
                    for (cdsDt.HypoglycemicEpisodes he : hype) {
                        Date dateObserved = he.getDate()!=null ? he.getDate().getTime() : null;
                        String dataField = he.getNumOfReportedEpisodes()!=null ? he.getNumOfReportedEpisodes().toString() : "";

                        if (he.getDate()==null) err_data.add("Error! No Date for Hypoglycemic Episodes in Care Element ("+(i+1)+")");
                        if (he.getNumOfReportedEpisodes()==null) err_data.add("Error! No Reported Episodes value for Hypoglycemic Episodes in Care Element ("+(i+1)+")");
                        ImportExportMeasurements.saveMeasurements("HYPE", demographicNo, admProviderNo, dataField, dateObserved);
                        addOneEntry(CAREELEMENTS);
                    }
                }
            }
            if(demoRes != null) {
                err_demo.addAll(demoRes.getWarningsCollection());
            }
            Util.cleanFile(xmlFile);

            return packMsgs(err_demo, err_data, err_summ, err_othe, err_note, warnings);
	}


	File makeImportLog(ArrayList<String[]> demo, String dir) throws IOException {
		String[][] keyword = new String[2][16];
		keyword[0][0] = PATIENTID;
		keyword[1][0] = "ID";
                keyword[0][1] = " "+PERSONALHISTORY;
                keyword[1][1] = " History";
                keyword[0][2] = " "+FAMILYHISTORY;
                keyword[1][2] = " History";
                keyword[0][3] = " "+PASTHEALTH;
                keyword[1][3] = " Health";
                keyword[0][4] = " "+PROBLEMLIST;
                keyword[1][4] = " List";
                keyword[0][5] = " "+RISKFACTOR;
                keyword[1][5] = " Factor";
                keyword[0][6] = " "+ALLERGY;
                keyword[0][7] = " "+MEDICATION;
                keyword[0][8] = " "+IMMUNIZATION;
                keyword[0][9] = " "+LABS;
                keyword[0][10] = " "+APPOINTMENT;
                keyword[0][11] = " "+CLINICALNOTE;
                keyword[1][11] = " Note";
                keyword[0][12] = "    Report    ";
                keyword[1][12] = " "+REPORTTEXT;
                keyword[1][13] = " "+REPORTBINARY;
                keyword[0][14] = " "+CAREELEMENTS;
                keyword[1][14] = " Elements";
                keyword[0][15] = " "+ALERT;

                for (int i=0; i<keyword[0].length; i++) {
                    if (keyword[0][i].contains("Report")) {
                        keyword[0][i+1] = "Report2";
                        i++;
                        continue;
                    }
                    if (keyword[1][i]==null) keyword[1][i] = " ";
                    if (keyword[0][i].length()>keyword[1][i].length()) keyword[1][i] = fillUp(keyword[1][i], ' ', keyword[0][i].length());
                    if (keyword[0][i].length()<keyword[1][i].length()) keyword[0][i] = fillUp(keyword[0][i], ' ', keyword[1][i].length());
                }

		File importLog = new File(dir, "ImportEvent-"+UtilDateUtilities.getToday("yyyy-MM-dd.HH.mm.ss")+".log");
		BufferedWriter out = new BufferedWriter(new FileWriter(importLog));
                int tableWidth = 0;
                for (int i=0; i<keyword.length; i++) {
                    for (int j=0; j<keyword[i].length; j++) {
                        out.write(keyword[i][j]+" |");
                        if (keyword[i][j].trim().equals("Report")) j++;
                        if (i==1) tableWidth += keyword[i][j].length()+2;
                    }
                    out.newLine();
                }
                out.write(fillUp("",'-',tableWidth)); out.newLine();

                //general log data
                if (importNo==0) importNo = 1;
                for (int i=0; i<importNo; i++) {
                    for (int j=0; j<keyword[0].length; j++) {
                        String category = keyword[0][j].trim();
                        if (category.contains("Report")) category = keyword[1][j].trim();
                        Integer occurs = entries.get(category+i);
                        if (occurs==null) occurs = 0;
                        out.write(fillUp(occurs.toString(), ' ', keyword[1][j].length()));
                        out.write(" |");
                    }
                    out.newLine();
                    out.write(fillUp("",'-',tableWidth)); out.newLine();
                }
                out.newLine();
                out.newLine();
                out.newLine();

                //error log
                String column1 = "Patient ID";
                out.write(column1+" |");
                out.write("Errors/Notes");
                out.newLine();
                out.write(fillUp("",'-',tableWidth)); out.newLine();

                for (int i=0; i < demo.size(); i++) {
                    Integer id = entries.get(PATIENTID+i);
                    if (id==null) id = 0;
                    out.write(fillUp(id.toString(), ' ', column1.length()));
                    out.write(" |");
                    String[] info = demo.get(i);
                    if (info!=null && info.length>0) {
                        String[] text = info[info.length-1].split("\n");
                        out.write(text[0]);
                        out.newLine();
                        for (int j=1; j<text.length; j++) {
                            out.write(fillUp("",' ',column1.length()));
                            out.write(" |");
                            out.write(text[j]);
                            out.newLine();
                        }
                    }
                    out.write(fillUp("",'-',tableWidth)); out.newLine();
                }
		out.close();
                importNo = 0;
                entries.clear();
		return importLog;
	}


	boolean matchFileExt(String filename, String ext) {
		if (StringUtils.empty(filename) || StringUtils.empty(ext)) return false;
		if (filename.length()<ext.length()+2) return false;
		if (filename.charAt(filename.length()-ext.length()-1)!='.') return false;

		if (filename.toLowerCase().substring(filename.length()-ext.length()).equals(ext.toLowerCase())) return true;
		else return false;
	}

	String fillUp(String filled, char c, int size) {
		if (size>=filled.length()) {
			int fill = size-filled.length();
			for (int i=0; i<fill; i++) filled += c;
		}
		return filled;
	}

	String getCalDate(Calendar c) {
		if (c==null) return "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return f.format(c.getTime());
	}

	String getCalDate(Calendar c, int timeShiftInDays) {
		if (c==null) return "";

		c.add(Calendar.DAY_OF_YEAR, timeShiftInDays);
		return getCalDate(c);
	}

	String getCalDateTime(Calendar c) {
		if (c==null) return "";

		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date());

		//Cancel out timezone difference
		int diff = c.getTimeZone().getRawOffset() - c1.getTimeZone().getRawOffset();
		c.add(Calendar.MILLISECOND, diff);

		//Cancel out daylight saving
		diff = c.getTimeZone().useDaylightTime() && c.getTimeZone().inDaylightTime(c.getTime()) ? 1 : 0;
		diff -= c1.getTimeZone().useDaylightTime() && c1.getTimeZone().inDaylightTime(c.getTime()) ? 1 : 0;
		c.add(Calendar.HOUR, diff);

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(c.getTime());
	}

	String getCalTime(Calendar c) {
		if (c==null) return "";
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		return f.format(c.getTime());
	}

	String getCountrySubDivCode(String countrySubDivCode) {
		if (StringUtils.empty(countrySubDivCode)) return "";
		String[] csdc = countrySubDivCode.split("-");
		if (csdc.length==2) {
			if (csdc[0].trim().equals("CA")) return csdc[1].trim(); //return w/o "CA-"
			if (csdc[1].trim().equals("US")) return countrySubDivCode.trim(); //return w/ "US-"
		}
		return "OT"; //Other
	}

    String dateFPtoString(cdsDt.DateTimeFullOrPartial dtfp, int timeshiftInDays) {
		if (dtfp==null) return "";

		if (dtfp.getFullDateTime()!=null)  {
			dtfp.getFullDateTime().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDateTime(dtfp.getFullDateTime());
		}
		if (dtfp.getFullDate()!=null)  {
			dtfp.getFullDate().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dtfp.getFullDate());
		}
		else if (dtfp.getYearMonth()!=null) {
			dtfp.getYearMonth().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dtfp.getYearMonth());
		}
		else if (dtfp.getYearOnly()!=null)
		{
			dtfp.getYearOnly().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dtfp.getYearOnly());
		}
		else
			return "";
    }

    String dateFPtoString(cdsDt.DateFullOrPartial dfp, int timeshiftInDays) {
		if (dfp==null) return "";

		if (dfp.getFullDate()!=null)  {
			dfp.getFullDate().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dfp.getFullDate());
		}
		else if (dfp.getYearMonth()!=null) {
			dfp.getYearMonth().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dfp.getYearMonth());
		}
		else if (dfp.getYearOnly()!=null)
		{
			dfp.getYearOnly().add(Calendar.DAY_OF_YEAR, timeshiftInDays);
			return getCalDate(dfp.getYearOnly());
		}
		else
			return "";
    }

    String dateFPGetPartial(cdsDt.DateFullOrPartial dfp) {
		if (dfp==null) return "";

		if (dfp.getYearMonth()!=null) return PartialDate.YEARMONTH;
		else if (dfp.getYearOnly()!=null) return PartialDate.YEARONLY;
		else return "";
    }

    String dateFPGetPartial(cdsDt.DateTimeFullOrPartial dfp) {
		if (dfp==null) return "";

		if (dfp.getYearMonth()!=null) return PartialDate.YEARMONTH;
		else if (dfp.getYearOnly()!=null) return PartialDate.YEARONLY;
		else return "";
    }

    Date dateTimeFPtoDate(cdsDt.DateTimeFullOrPartial dtfp, int timeShiftInDays) {
		String sdate = dateFPtoString(dtfp,timeShiftInDays);
		Date dDate = UtilDateUtilities.StringToDate(sdate, "yyyy-MM-dd HH:mm:ss");
		if (dDate==null)
			dDate = UtilDateUtilities.StringToDate(sdate, "yyyy-MM-dd");
		if (dDate==null)
			dDate = UtilDateUtilities.StringToDate(sdate, "HH:mm:ss");

		return dDate;
    }

    Date dateFPtoDate(cdsDt.DateFullOrPartial dfp, int timeShiftInDays) {
            String sdate = dateFPtoString(dfp,timeShiftInDays);
            return UtilDateUtilities.StringToDate(sdate, "yyyy-MM-dd");
    }

    String dateOnly(String d) {
            return UtilDateUtilities.DateToString(UtilDateUtilities.StringToDate(d),"yyyy-MM-dd");
    }

    HashMap<String, String> getPersonName(cdsDt.PersonNameSimple person) {
            HashMap<String,String> name = new HashMap<String,String>();
            if (person!=null) {
                name.put("firstname", StringUtils.noNull(person.getFirstName()).trim());
                name.put("lastname", StringUtils.noNull(person.getLastName()).trim());
            }
            return name;
    }

    HashMap<String, String> getPersonName(cdsDt.PersonNameSimpleWithMiddleName person) {
            HashMap<String,String> name = new HashMap<String,String>();
            if (person!=null) {
                name.put("firstname", StringUtils.noNull(person.getFirstName()).trim()+" "+StringUtils.noNull(person.getMiddleName()).trim());
                name.put("lastname", StringUtils.noNull(person.getLastName()).trim());
            }
            return name;
    }

	String defaultProviderNo() {
		ProviderData pd = getProviderByNames("doctor", "oscardoc", true);
		if (pd!=null) return pd.getProviderNo();

		return writeProviderData("doctor", "oscardoc", "");
	}

	boolean isICD9(cdsDt.StandardCoding diagCode) {
		if (diagCode == null) return false;

		String codingSystem = StringUtils.noNull(diagCode.getStandardCodingSystem()).toLowerCase();
		return (codingSystem.contains("icd") && codingSystem.contains("9"));
	}

	boolean isICD9(cdsDt.Code diagCode) {
		if (diagCode == null) return false;

		String codingSystem = StringUtils.noNull(diagCode.getCodingSystem()).toLowerCase();
		return (codingSystem.contains("icd") && codingSystem.contains("9"));
	}

	/**
	 * retrieves the CasemanagementIssue associated with the given code. If none
	 * exists, it adds one if there is a matching issue with the code
	 */
	Set<CaseManagementIssue> getCMIssue(String code) {

		Set<CaseManagementIssue> sCmIssu = new HashSet<CaseManagementIssue>();
		CaseManagementIssue cmIssue = caseManagementIssueDAO.getIssuebyIssueCode(demographicNo, StringUtils.noNull(code));
		if (cmIssue == null) {
			Issue issue = caseManagementManager.getIssueInfoByCode(StringUtils.noNull(code));
			if (issue != null) {
				// add a new CaseManagementIssue with a valid issue
				cmIssue = new CaseManagementIssue();
				cmIssue.setDemographic_no(demographicNo);
				cmIssue.setIssue(issue);
				cmIssue.setIssue_id(issue.getId());
				String type = issue.getType();
				// casemgmt_issue likes empty strings over null values...apparently
				cmIssue.setType((type != null)? type : "");
				caseManagementIssueDAO.saveIssue(cmIssue);
			}
		}
		if (cmIssue != null) {
			sCmIssu.add(cmIssue);
		}
		return sCmIssu;
	}

	Set<CaseManagementIssue> getCMIssue(String issueCode, cdsDt.StandardCoding diagCode) {

		Set<CaseManagementIssue> sCmIssu = new HashSet<CaseManagementIssue>();
		sCmIssu.addAll(getCMIssue(issueCode));
		if (isICD9(diagCode)) {
			sCmIssu.addAll(getCMIssue(noDot(diagCode.getStandardCode())));
		}
		return sCmIssu;
	}

	String getCode(cdsDt.StandardCoding dCode, String dTitle) {
		if (dCode==null) return "";

		String ret = StringUtils.filled(dTitle) ? dTitle+" -" : "";
		ret = Util.addLine(ret, "Coding System: ", dCode.getStandardCodingSystem());
		ret = Util.addLine(ret, "Value: ", dCode.getStandardCode());
		ret = Util.addLine(ret, "Description: ", dCode.getStandardCodeDescription());

		return ret;
	}

	String getCode(cdsDt.Code dCode, String dTitle) {
		if (dCode==null) return "";

		String ret = StringUtils.filled(dTitle) ? dTitle+" -" : "";
		ret = Util.addLine(ret, "Coding System: ", dCode.getCodingSystem());
		ret = Util.addLine(ret, "Value: ", dCode.getValue());
		ret = Util.addLine(ret, "Description: ", dCode.getDescription());

		return ret;
	}

	ProviderData getProviderByNames(String firstName, String lastName, boolean matchAll) {
		ProviderData pd = new ProviderData();
		if (matchAll) {
			pd.getProviderWithNames(firstName, lastName);
		} else {
			pd.getExternalProviderWithNames(firstName, lastName);
		}
		if (StringUtils.filled(pd.getProviderNo())) {
			pd.getProvider(pd.getProviderNo());
			return pd;
		}
		else return null;
	}

	ProviderData getProviderByOhip(String OhipNo) {
		ProviderData pd = new ProviderData();
		pd.getProviderWithOHIP(OhipNo);
		if (StringUtils.filled(pd.getProviderNo())) {
			pd.getProvider(pd.getProviderNo());
			return pd;
		}
		else return null;
	}

	String getProvinceCode(cdsDt.HealthCardProvinceCode.Enum provinceCode) {
		String pcStr = provinceCode.toString();
		return getCountrySubDivCode(pcStr);
	}

	String getResidual(cdsDt.ResidualInformation resInfo) {
		String ret = "";
		if (resInfo==null) return ret;

		cdsDt.ResidualInformation.DataElement[] resData = resInfo.getDataElementArray();
		if (resData.length>0) ret = "- Residual Information -";
		for (int i=0; i<resData.length; i++) {
			if (StringUtils.filled(resData[i].getName())) {
				ret = Util.addLine(ret, "Data Name: ",   resData[i].getName());
				ret = Util.addLine(ret, "Data Type: ",   resData[i].getDataType());
				ret = Util.addLine(ret, "Content: ",     resData[i].getContent());
			}
		}
		return ret;
	}

	ArrayList<String> getUniques(String[] arr) {
		ArrayList<String> uniques = new ArrayList<String>();
		for (int i=0; i<arr.length; i++) {
			boolean match = false;
			for (String x : uniques) {
				if (arr[i].equals(x)) {
					match = true;
					break;
				}
			}
			if (!match) uniques.add(arr[i]);
		}
		return uniques;
	}

	String getYN(cdsDt.YnIndicator yn) {
		if (yn==null) return "";

		String ret = "No";
		if (yn.getYnIndicatorsimple()==cdsDt.YnIndicatorsimple.Y || yn.getYnIndicatorsimple()==cdsDt.YnIndicatorsimple.Y_2) {
			ret = "Yes";
		} else if (yn.getBoolean()) {
			ret = "Yes";
		}
		return ret;
	}

        Boolean getYN(cdsDt.YnIndicatorAndBlank yn) {
		if (yn==null) return null;

		boolean ret = false;
		if (yn.getYnIndicatorsimple()==cdsDt.YnIndicatorsimple.Y || yn.getYnIndicatorsimple()==cdsDt.YnIndicatorsimple.Y_2) {
			ret = true;
		} else if (yn.getBoolean()) {
			ret = true;
		}
		return ret;
        }

	String mapPreventionTypeByCode(cdsDt.Code imCode) {
		if (imCode==null) return null;
		if (!imCode.getCodingSystem().equalsIgnoreCase("DIN")) return null;

		ArrayList<String> dinFlu = new ArrayList<String>();
		ArrayList<String> dinHebAB = new ArrayList<String>();
		ArrayList<String> dinCHOLERA = new ArrayList<String>();

		dinFlu.add("01914510");
		dinFlu.add("02015986");
		dinFlu.add("02269562");
		dinFlu.add("02223929");

		dinHebAB.add("02230578");
		dinHebAB.add("02237548");

		dinCHOLERA.add("00074969");
		dinCHOLERA.add("02247208");

		if (dinFlu.contains(StringUtils.noNull(imCode.getValue()))) return "Flu";
		if (dinHebAB.contains(StringUtils.noNull(imCode.getValue()))) return "HebAB";
		if (dinCHOLERA.contains(StringUtils.noNull(imCode.getValue()))) return "CHOLERA";
		return null;
	}

	String[] packMsgs(ArrayList<String> err_demo, ArrayList<String> err_data, ArrayList<String> err_summ, ArrayList<String> err_othe, ArrayList<String> err_note, ArrayList<String> warnings) {
		if (!(err_demo.isEmpty() && err_data.isEmpty() && err_summ.isEmpty() && err_othe.isEmpty() && err_note.isEmpty())) {
			String title = "Fail to import patient "+patientName;
			if (StringUtils.filled(demographicNo)) {
				title = "Patient "+patientName+" (Demographic no="+demographicNo+")";
			}
			warnings.add(fillUp("---- "+title, '-', 100));
		}
		warnings.addAll(err_demo);
		warnings.addAll(err_data);
		warnings.addAll(err_summ);
		warnings.addAll(err_othe);
		warnings.addAll(err_note);

		String err_all = aListToMsg(err_demo);
		err_all = Util.addLine(err_all, aListToMsg(err_data));
		err_all = Util.addLine(err_all, aListToMsg(err_summ));
		err_all = Util.addLine(err_all, aListToMsg(err_othe));
		err_all = Util.addLine(err_all, aListToMsg(err_note));

		String[] msgs = {demographicNo, err_data.isEmpty()?"Yes":"No",
		err_summ.isEmpty()?"Yes":"No",
		err_othe.isEmpty()?"Yes":"No", err_all};
		return msgs;
	}

	CaseManagementNote prepareCMNote(String caisi_role, String uuid) {
		CaseManagementNote cmNote = new CaseManagementNote();
		cmNote.setUpdate_date(new Date());
		cmNote.setObservation_date(new Date());
		cmNote.setDemographic_no(demographicNo);
		cmNote.setProviderNo(admProviderNo);
		cmNote.setSigning_provider_no(admProviderNo);
		cmNote.setSigned(true);
		cmNote.setHistory("");
		cmNote.setReporter_program_team("0");
		cmNote.setProgram_no(programId);
                if (StringUtils.filled(uuid)) cmNote.setUuid(uuid);
                else cmNote.setUuid(UUID.randomUUID().toString());

                if (caisi_role==null || (!caisi_role.equals("1") && !caisi_role.equals("2"))) caisi_role="1";
                cmNote.setReporter_caisi_role(caisi_role);  //"1" for doctor, "2" for nurse - note hidden in echart

		return cmNote;
	}

	void saveLinkNote(Long hostId, CaseManagementNote cmn) {
		saveLinkNote(cmn, CaseManagementNoteLink.CASEMGMTNOTE, hostId);
	}

	void saveLinkNote(CaseManagementNote cmn, Integer tableName, Long tableId) {
                saveLinkNote(cmn, tableName, tableId, null);
	}

	void saveLinkNote(CaseManagementNote cmn, Integer tableName, Long tableId, String otherId) {
		if (StringUtils.filled(cmn.getNote())) {
			
            /* prevent the update_date from being null */
            Date updateDate = cmn.getUpdate_date();
            if(updateDate == null) updateDate = cmn.getCreate_date();
            if(updateDate == null) updateDate = new Date();
            cmn.setUpdate_date(updateDate);
            
            /* prevent the observation_date from being null */
            Date obserDate = cmn.getObservation_date();
            if(obserDate == null) obserDate = cmn.getCreate_date();
            if(obserDate == null) obserDate = new Date();
            cmn.setObservation_date(obserDate);
			
			caseManagementManager.saveNoteSimple(cmn);    //new note id created

			CaseManagementNoteLink cml = new CaseManagementNoteLink();
			cml.setTableName(tableName);
			cml.setTableId(tableId);
			cml.setNoteId(cmn.getId()); //new note id
            cml.setOtherId(otherId);
            
			caseManagementManager.saveNoteLink(cml);
		}
	}

	void saveMeasurementsExt(Long measurementId, String key, String val) {
		if (measurementId!=null && StringUtils.filled(key) && StringUtils.filled(val)) {
			MeasurementsExt mx = new MeasurementsExt(measurementId.intValue());
			mx.setKeyVal(key);
			mx.setVal(StringUtils.noNull(val));
			ImportExportMeasurements.saveMeasurementsExt(mx);
		}
	}

	String updateExternalProvider(String firstName, String lastName, String ohipNo, String cpsoNo, ProviderData pd) {
		// For external provider only
		if (pd==null) return null; 
		if( pd.getProviderNo().charAt(0)!='-') return pd.getProviderNo();

		org.oscarehr.provider.model.ProviderData newpd = providerDataDao.findByProviderNo(pd.getProviderNo());
		if (StringUtils.empty(pd.getFirst_name()))
			newpd.setFirstName(StringUtils.noNull(firstName));
		if (StringUtils.empty(pd.getLast_name()))
			newpd.setLastName(StringUtils.noNull(lastName));
		if (StringUtils.empty(pd.getOhip_no()))
			newpd.setOhipNo(ohipNo);
		if (StringUtils.empty(pd.getPractitionerNo()))
			newpd.setPractitionerNo(cpsoNo);

		providerDataDao.merge(newpd);
		return newpd.getId();
	}

	String writeProviderData(String firstName, String lastName, String ohipNo) {
		return writeProviderData(firstName, lastName, ohipNo, null);
	}

	/**
	 * helper method to parse special cases where entire provider names exist in the first_name or last_name fields
	 * @return string array of [first name, last name] where entries are not null or empty. null if it fails
	 */
	private String[] coerceProviderNames(String fullName) {
		try {
			fullName = fullName.trim();
			int i = fullName.lastIndexOf(" ");//get last space in the string
			if (i >= 0 ) {
				String[] a =  {fullName.substring(0, i), fullName.substring(i).trim()};
				
				if(!StringUtils.empty(a[0]) && !StringUtils.empty(a[1])) {
					return a;
				}
			}
		}
		catch(Exception e) {
			logger.warn("Exception encounterd in coerceProviderNames");
		}
		return null;
	}
	String writeProviderData(String firstName, String lastName, String ohipNo, String cpsoNo) {
		//TODO convert this to newer providerData type. the current process uses it anyways as the deprecated one basically just calls the new one.
		ProviderData pd = getProviderByOhip(ohipNo);
		if (pd == null) {
			pd = getProviderByNames(firstName, lastName, matchProviderNames);
	    }
		/* attempt to split single names into first and last parts */
		if (pd == null && (StringUtils.empty(firstName) || StringUtils.empty(lastName))) {
	    	String[] parsedName = null;
	    	if (!StringUtils.empty(firstName)) {
	    		parsedName = coerceProviderNames(firstName);
	    	}
	    	else if (!StringUtils.empty(lastName)) {
	    		parsedName = coerceProviderNames(lastName);
	    	}
	    	if (parsedName != null) {
	    		firstName = parsedName[0];
	    		lastName  = parsedName[1];
	    		pd = getProviderByNames(firstName, lastName, matchProviderNames);
	    		logger.warn("Provider name recovered through string parsing. Using firstName: "+firstName+" lastName: "+lastName+" as provider");
	    	}
		}
	
	    if (pd == null) {
	    	logger.warn("No provider found for firstName: " + firstName +
	    			" lastName: " + lastName + " ohipNo: " + ohipNo);
		
		    if (StringUtils.empty(firstName) || StringUtils.empty(lastName)) {
		    	return ""; //no information at all!
		    }
			pd = new ProviderData();
			MiscUtils.getLogger().info("ADD EXTERNAL PROVIDER");
			pd.addExternalProvider(firstName, lastName, ohipNo, cpsoNo);
	    }
	    return pd.getProviderNo();
	}

	String aListToMsg(ArrayList<String> alist) {
		String msgs = "";
		for (int i=0; i<alist.size(); i++) {
			String msg = alist.get(i);
			if (i>0) msgs += "\n";
			msgs += msg;
		}
		return msgs;
	}

	void insertIntoAdmission(String demoNo) {
		Admission admission = new Admission();
		admission.setClientId(Integer.valueOf(demoNo));
		admission.setProviderNo(admProviderNo);
		admission.setProgramId(Integer.valueOf(programId));
		admission.setAdmissionDate(new Date());
		admission.setAdmissionFromTransfer(false);
		admission.setDischargeFromTransfer(false);
		admission.setAdmissionStatus("current");
		admission.setTeamId(null);
		admission.setTemporaryAdmission(false);
		admission.setRadioDischargeReason("0");
		admission.setClientStatusId(null);
		admission.setAutomaticDischarge(false);

		admissionDao.saveAdmission(admission);
	}

	String getLabDline(LaboratoryResults labRes, int timeShiftInDays){
		StringBuilder s = new StringBuilder();
		appendIfNotNull(s,"LaboratoryName",labRes.getLaboratoryName());
		appendIfNotNull(s,"TestNameReportedByLab", labRes.getTestNameReportedByLab());
		appendIfNotNull(s,"LabTestCode",labRes.getLabTestCode());
		appendIfNotNull(s,"TestName", labRes.getTestName());
		appendIfNotNull(s,"AccessionNumber",labRes.getAccessionNumber());

		if (labRes.getResult ()!=null) {
			appendIfNotNull(s,"Value",labRes.getResult().getValue());
			appendIfNotNull(s,"UnitOfMeasure",labRes.getResult().getUnitOfMeasure());
		}
		if (labRes.getReferenceRange()!=null) {
			LaboratoryResults.ReferenceRange ref = labRes.getReferenceRange();
			appendIfNotNull(s,"LowLimit",ref.getLowLimit());
			appendIfNotNull(s,"HighLimit",ref.getHighLimit());
			appendIfNotNull(s,"ReferenceRangeText", ref.getReferenceRangeText());
		}

		appendIfNotNull(s,"LabRequisitionDateTime",dateFPtoString(labRes.getLabRequisitionDateTime(), timeShiftInDays));
		appendIfNotNull(s,"CollectionDateTime",dateFPtoString( labRes.getCollectionDateTime(), timeShiftInDays));

                LaboratoryResults.ResultReviewer[] resultReviewers = labRes.getResultReviewerArray();
                if (resultReviewers.length>0) {
                    appendIfNotNull(s,"DateTimeResultReviewed",dateFPtoString(resultReviewers[0].getDateTimeResultReviewed(), timeShiftInDays));
                    appendIfNotNull(s,"OHIP ID :", resultReviewers[0].getOHIPPhysicianId());
                    cdsDt.PersonNameSimple reviewerName = resultReviewers[0].getName();
                    if (reviewerName!=null) {
                        appendIfNotNull(s,"Reviewer First Name:", reviewerName.getFirstName());
                        appendIfNotNull(s,"Reviewer Last Name:", reviewerName.getLastName());
                    }
                }

		appendIfNotNull(s,"ResultNormalAbnormalFlag",""+labRes.getResultNormalAbnormalFlag());
		appendIfNotNull(s,"TestResultsInformationreportedbytheLaboratory",labRes.getTestResultsInformationReportedByTheLab());
		appendIfNotNull(s,"NotesFromLab",labRes.getNotesFromLab());
		appendIfNotNull(s,"PhysiciansNotes",labRes.getPhysiciansNotes());

		return s.toString();
	}

	void appendIfNotNull(StringBuilder s, String name, String object){
		if (object != null){
			s.append(name);
                        s.append(": ");
                        s.append(object);
                        s.append("\n");
		}
	}

    void addOneEntry(String category) {
        if (StringUtils.empty(category)) return;

        Integer n = entries.get(category+importNo);
        n = n==null ? 1 : n+1;
        entries.put(category+importNo, n);
    }

    DemographicArchive archiveDemographic(org.oscarehr.common.model.Demographic d) {
    	DemographicArchive da = new DemographicArchive();

    	da.setDemographicNo(Integer.valueOf(d.getDemographicNo()));
    	da.setFirstName(d.getFirstName());
    	da.setLastName(d.getLastName());
    	da.setTitle(d.getTitle());
    	da.setSex(d.getSex());
    	da.setYearOfBirth(d.getYearOfBirth());
    	da.setMonthOfBirth(d.getMonthOfBirth());
    	da.setDayOfBirth(d.getDateOfBirth());
    	da.setMonthOfBirth(d.getMonthOfBirth());
    	da.setYearOfBirth(d.getYearOfBirth());
    	da.setAddress(d.getAddress());
    	da.setCity(d.getCity());
    	da.setProvince(d.getProvince());
    	da.setPostal(d.getPostal());
    	da.setAlias(d.getAlias());
    	da.setEmail(d.getEmail());
    	da.setAnonymous(d.getAnonymous());
    	da.setChartNo(d.getChartNo());
    	da.setChildren(d.getChildren());
    	da.setCitizenship(d.getCitizenship());
    	da.setCountryOfOrigin(d.getCountryOfOrigin());
    	da.setDateJoined(d.getDateJoined());
    	da.setEndDate(d.getEndDate());
    	da.setFamilyDoctor(d.getFamilyDoctor());
    	da.setHin(d.getHin());
    	da.setVer(d.getVer());
    	da.setHcType(d.getHcType());
    	da.setEffDate(d.getEffDate());
    	da.setHcRenewDate(d.getHcRenewDate());
    	da.setMyOscarUserName(d.getMyOscarUserName());
    	da.setNewsletter(d.getNewsletter());
    	da.setOfficialLanguage(d.getOfficialLanguage());
    	da.setSpokenLanguage(d.getSpokenLanguage());
    	da.setPatientStatus(d.getPatientStatus());
    	da.setPatientStatusDate(d.getPatientStatusDate());
    	da.setPcnIndicator(d.getPcnIndicator());
    	da.setPhone(d.getPhone());
    	da.setPhone2(d.getPhone2());
    	da.setPreviousAddress(d.getPreviousAddress());
    	da.setProviderNo(d.getProviderNo());
    	da.setRosterStatus(d.getRosterStatus());
    	da.setRosterDate(d.getRosterDate());
    	da.setRosterTerminationDate(d.getRosterTerminationDate());
    	da.setRosterTerminationReason(d.getRosterTerminationReason());
    	da.setSin(d.getSin());
    	da.setSourceOfIncome(d.getSourceOfIncome());
    	da.setLastUpdateDate(d.getLastUpdateDate());
    	da.setLastUpdateUser(d.getLastUpdateUser());

    	return da;
    }

	String mapNamePurpose(cdsDt.PersonNamePurposeCode.Enum namePurpose) {
		if (namePurpose.equals(cdsDt.PersonNamePurposeCode.HC))
			return "Health Card Name";
		if (namePurpose.equals(cdsDt.PersonNamePurposeCode.L))
			return "Legal Name";
		if (namePurpose.equals(cdsDt.PersonNamePurposeCode.AL))
			return "Alias Name";
		if (namePurpose.equals(cdsDt.PersonNamePurposeCode.C))
			return "License Name";
		return "";
	}

    String noDot(String s) {
        if (s==null) return null;
        s = s.trim();

        for (int i=0; i<s.length(); i++) {
            if (".".contains(s.substring(i, i+1))) {
                s = s.substring(0,i)+s.substring(i+1,s.length());
                i--;
            }
        }
        return s;
    }

    String addSpaced(String s, String ss) {
    	s = StringUtils.noNull(s).trim();

    	if (!s.equals("") && StringUtils.filled(ss)) s += " " + ss.trim();
    	else s += StringUtils.noNull(ss).trim();

    	return s;
    }

	private static Integer saveRxAllergy(Integer demographicNo, Date entryDate, String description, Integer typeCode, String reaction, Date startDate, String severity, String regionalId, String lifeStage) {

		AllergyDao allergyDao=(AllergyDao) SpringUtils.getBean("allergyDao");

		Allergy allergy=new Allergy();
		allergy.setDemographicNo(demographicNo);
		allergy.setEntryDate(entryDate);
		allergy.setDescription(description);
		allergy.setTypeCode(typeCode);
		allergy.setReaction(reaction);
		allergy.setStartDate(startDate);
		allergy.setSeverityOfReaction(severity);
		allergy.setRegionalIdentifier(regionalId);
		allergy.setLifeStage(lifeStage);

		allergyDao.persist(allergy);
		return(allergy.getId());
	}

	/**
	 * Terrible method.
	 * Not my fault, you should have used a Date object to begin with not a String. Now I have to undo your mess.
	 */
	private static Date toDateFromString(String s)
	{
        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return(sdf.parse(s));
        }
        catch (Exception e)
        {
        	// okay we couldn't parse it, we'll try another format
        }

        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat(DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());
            return(sdf.parse(s));
        }
        catch (Exception e)
        {
        	// okay we couldn't parse it, we'll try another format
        }

        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());
            return(sdf.parse(s));
        }
        catch (Exception e)
        {
        	// okay we couldn't parse it, we'll try another format
        }

        // no more formats to try, we lose :(
        logger.warn("UnParsable date string : "+s);

        return(null);

	}
}
