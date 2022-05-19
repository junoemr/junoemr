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

package oscar.oscarLab.ca.all;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.Hl7TextInfoDao;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.model.ConsultResponseDoc;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.consultations.dao.ConsultDocsDao;
import org.oscarehr.consultations.dao.ConsultResponseDocDao;
import org.oscarehr.consultations.model.ConsultDocs;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConversionUtils;
import oscar.util.UtilDateUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Hl7textResultsData {

	private static Logger logger = MiscUtils.getLogger();
	private static MeasurementDao measurementDao = SpringUtils.getBean(MeasurementDao.class);
	private static ConsultDocsDao consultDocsDao = SpringUtils.getBean(ConsultDocsDao.class);
	private static ConsultResponseDocDao consultResponseDocDao = SpringUtils.getBean(ConsultResponseDocDao.class);
	private static Hl7TextInfoDao hl7TxtInfoDao = SpringUtils.getBean(Hl7TextInfoDao.class);
	private static PatientLabRoutingDao patientLabRoutingDao = SpringUtils.getBean(PatientLabRoutingDao.class);

	private Hl7textResultsData() {
		// no one should instantiate this
	}

	public static void populateMeasurementsTable(String lab_no, String demographic_no)
	{
		MessageHandler messageHandler = Factory.getHandler(lab_no);

		//Check for other versions of this lab
		String[] matchingLabs = getMatchingLabs(lab_no).split(",");

		//if this lab is the latest version delete the measurements from the previous version and add the new ones
		measurementDao.deleteMatchingLabs(matchingLabs, lab_no);
	 	measurementDao.populateMeasurements( messageHandler, lab_no, demographic_no, new Date());
	}

	/** get lab id's of other versions matching this segmentId
	 * @return csv list of id's
	 * @deprecated use list version instead */
	@Deprecated
	public static String getMatchingLabs(String segmentId)
	{
		return getMatchingLabs(segmentId, true);
	}
	/** get lab id's of other versions matching this segmentId
	 * @return csv list of id's
	 * @deprecated use list version instead */
	@Deprecated
	public static String getMatchingLabs(String segmentId, boolean prioritizeFinalCount)
	{
		Integer labNo = ConversionUtils.fromIntString(segmentId);
		List<Integer> labIds = getMatchingLabs(labNo, prioritizeFinalCount);
		if(labIds.isEmpty())
		{
			labIds.add(labNo); // not sure why we do this?
		}
		return idsToString(labIds);
	}

	public static String idsToString(List<Integer> idList)
	{
		return idList.stream()
				.map(String::valueOf)
				.collect(Collectors.joining(","));
	}

	/** get lab id's of other versions matching this segmentId
	 * @return list of id's, empty if no matches
	 */
	public static List<Integer> getMatchingLabs(Integer segmentId)
	{
		return getMatchingLabs(segmentId, true);
	}
	/** get lab id's of other versions matching this segmentId
	 * @return list of id's, empty if no matches
	 */
	public static List<Integer> getMatchingLabs(Integer segmentId, boolean prioritizeFinalCount)
	{
		List<Integer> labIds = new LinkedList<>();
		for (Object[] o : hl7TxtInfoDao.findByLabIdViaMagic(segmentId, prioritizeFinalCount))
		{
			Hl7TextInfo a = (Hl7TextInfo) o[0];
			Hl7TextInfo b = (Hl7TextInfo) o[1];

			int labNo = a.getLabNumber();
			
			
			//Accession numbers may be recycled, accession
			//numbers for a lab should have lab dates within less than 4
			//months of each other even this is a large time span
			Date dateA = ConversionUtils.fromTimestampString(a.getObrDate());
			Date dateB = ConversionUtils.fromTimestampString(b.getObrDate());
			if (dateA==null || dateB==null) continue;

			int monthsBetween = 0;
			if (dateA.before(dateB)) {
				monthsBetween = UtilDateUtilities.getNumMonths(dateA, dateB);
			} else {
				monthsBetween = UtilDateUtilities.getNumMonths(dateB, dateA);
			}

			logger.debug("monthsBetween: " + monthsBetween);
			logger.debug("lab_no: " + labNo + " lab: " + segmentId);

			if (monthsBetween < 4)
			{
				labIds.add(labNo);
			}
		}
		return labIds;
	}

	/**
	 * Populates ArrayList with labs attached to a consultation request
	 */
	// Populates labs to consult request
	public static ArrayList<LabResultData> populateHL7ResultsData(String demographicNo, String consultationId, boolean attached) {
		List<LabResultData> attachedLabs = new ArrayList<LabResultData>();
		for (Object[] o : consultDocsDao.findLabs(ConversionUtils.fromIntString(consultationId))) {
			ConsultDocs c = (ConsultDocs) o[0];
			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			lbData.labPatientId = ConversionUtils.toIntString(c.getDocumentNo());
			attachedLabs.add(lbData);
		}
		List<Object[]> labsHl7 = hl7TxtInfoDao.findByDemographicId(ConversionUtils.fromIntString(demographicNo));
		return populateHL7ResultsData(attachedLabs, labsHl7, attached);
	}
	
	// Populates labs to consult response
	public static ArrayList<LabResultData> populateHL7ResultsDataConsultResponse(String demographicNo, String consultationId, boolean attached) {
		List<LabResultData> attachedLabs = new ArrayList<LabResultData>();
		for (Object[] o : consultResponseDocDao.findLabs(ConversionUtils.fromIntString(consultationId))) {
			ConsultResponseDoc c = (ConsultResponseDoc) o[0];
			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			lbData.labPatientId = ConversionUtils.toIntString(c.getDocumentNo());
			attachedLabs.add(lbData);
		}
		List<Object[]> labsHl7 = hl7TxtInfoDao.findByDemographicId(ConversionUtils.fromIntString(demographicNo));
		return populateHL7ResultsData(attachedLabs, labsHl7, attached);
	}
	
	// Populates labs private shared method
	private static ArrayList<LabResultData> populateHL7ResultsData(List<LabResultData> attachedLabs, List<Object[]> labsHl7, boolean attached) {
		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();

		LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
		LabResultData.CompareId c = lbData.getComparatorId();
		for (Object[] o : labsHl7) {
			Hl7TextInfo i = (Hl7TextInfo) o[0];
			PatientLabRouting p = (PatientLabRouting) o[1];

			lbData.segmentID = ConversionUtils.toIntString(i.getLabNumber());
			lbData.labPatientId = ConversionUtils.toIntString(p.getLabNo());
			lbData.dateTime = i.getObrDate();
			lbData.discipline = i.getDiscipline();
			lbData.accessionNumber = i.getUniqueIdentifier();
			lbData.finalResultsCount = i.getFinalResultCount();
			lbData.label = i.getLabel();

			if (attached && Collections.binarySearch(attachedLabs, lbData, c) >= 0) labResults.add(lbData);
			else if (!attached && Collections.binarySearch(attachedLabs, lbData, c) < 0) labResults.add(lbData);

			lbData = new LabResultData(LabResultData.HL7TEXT);
		}

		return labResults;
	}
	/**
	 * End Populates labs attached to consultation
	 */
	

	public static ArrayList<LabResultData> getNotAckLabsFromLabNos(List<String> labNos) {
		ArrayList<LabResultData> ret = new ArrayList<LabResultData>();
		LabResultData lrd = new LabResultData();
		for (String labNo : labNos) {
			lrd = getNotAckLabResultDataFromLabNo(labNo);
			ret.add(lrd);
		}
		return ret;
	}

	public static LabResultData getNotAckLabResultDataFromLabNo(String labNo) {
		LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
		// note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
		// for the provider number if unable to find correct provider

		List<Hl7TextInfo> infos = hl7TxtInfoDao.findByLabId(ConversionUtils.fromIntString(labNo));
		if (infos.isEmpty()) return lbData;

		Hl7TextInfo info = infos.get(0);

		lbData.labType = LabResultData.HL7TEXT;
		lbData.segmentID = "" + info.getLabNumber();
		//check if any demographic is linked to this lab
		if (lbData.isMatchedToPatient()) {
			//get matched demographic no
			List<PatientLabRouting> rs = patientLabRoutingDao.findByLabNoAndLabType(Integer.parseInt(lbData.segmentID), lbData.labType);
			if (!rs.isEmpty()) {
				lbData.setLabPatientId("" + rs.get(0).getDemographicNo());
			} else {
				lbData.setLabPatientId("-1");
			}
		} else {
			lbData.setLabPatientId("-1");
		}
		lbData.acknowledgedStatus = "U";
		lbData.accessionNumber = info.getUniqueIdentifier();
		lbData.healthNumber = info.getHealthNumber();
		lbData.patientName = info.getLastName() + ", " + info.getFirstName();
		lbData.sex = info.getSex();

		lbData.resultStatus = info.getResultStatus();
		if (lbData.resultStatus != null && lbData.resultStatus.equals("A")) {
			lbData.abn = true;
		}

		lbData.dateTime = info.getObrDate();

		//priority
		String priority = info.getPriority();

		if (priority != null && !priority.equals("")) {
			switch (priority.charAt(0)) {
			case 'C':
				lbData.priority = "Critical";
				break;
			case 'S':
				lbData.priority = "Stat/Urgent";
				break;
			case 'U':
				lbData.priority = "Unclaimed";
				break;
			case 'A':
				lbData.priority = "ASAP";
				break;
			case 'L':
				lbData.priority = "Alert";
				break;
			default:
				lbData.priority = "Routine";
				break;
			}
		} else {
			lbData.priority = "----";
		}

		lbData.requestingClient = info.getRequestingProvider();
		lbData.reportStatus = info.getReportStatus();

		// the "C" is for corrected excelleris labs
		if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))) {
			lbData.finalRes = true;
		} else {
			lbData.finalRes = false;
		}

		lbData.discipline = info.getDiscipline();
		lbData.finalResultsCount = info.getFinalResultCount();

		return lbData;
	}

	public static ArrayList<LabResultData> populateHl7ResultsData(String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status, Integer labNo) {

		if (providerNo == null) {
			providerNo = "";
		}
		if (patientFirstName == null) {
			patientFirstName = "";
		}
		if (patientLastName == null) {
			patientLastName = "";
		}
		if (status == null) {
			status = "";
		}

		patientHealthNumber = StringUtils.trimToNull(patientHealthNumber);

		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();

		List<Object[]> routings = null;

		if(labNo != null && labNo.intValue()>0) {
			routings = new ArrayList<Object[]>();
			for(Hl7TextInfo info : hl7TxtInfoDao.findByLabId(labNo)) {
				routings.add(new Object[]{info});
			}
		} else {
			if (demographicNo == null) {
				// note to self: lab reports not found in the providerLabRouting table will not show up - 
				// need to ensure every lab is entered in providerLabRouting, with '0'
				// for the provider number if unable to find correct provider				
				routings = hl7TxtInfoDao.findLabsViaMagic(status, providerNo, patientFirstName, patientLastName, patientHealthNumber);
			} else {
				routings = hl7TxtInfoDao.findByDemographicId(ConversionUtils.fromIntString(demographicNo));
			}
		}

		for (Object[] o : routings) {
			Hl7TextInfo hl7 = (Hl7TextInfo) o[0];
			//PatientLabRouting p = (PatientLabRouting) o[1];

			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			lbData.labType = LabResultData.HL7TEXT;
			lbData.segmentID = "" + hl7.getLabNumber();

			//check if any demographic is linked to this lab
			if (lbData.isMatchedToPatient()) {
				//get matched demographic no
				List<PatientLabRouting> lst = patientLabRoutingDao.findByLabNoAndLabType(Integer.parseInt(lbData.segmentID), lbData.labType);

				if (!lst.isEmpty()) {
					lbData.setLabPatientId("" + lst.get(0).getDemographicNo());
				} else {
					lbData.setLabPatientId("-1");
				}
			} else {
				lbData.setLabPatientId("-1");
			}

			if(o.length == 1) {
				lbData.acknowledgedStatus = "U";
			} else {
				if (demographicNo == null && !providerNo.equals("0")) {
					lbData.acknowledgedStatus = hl7.getResultStatus();
				} else {
					lbData.acknowledgedStatus = "U";
				}
			}

			lbData.accessionNumber = hl7.getUniqueIdentifier();
			lbData.healthNumber = hl7.getHealthNumber();
			lbData.patientName = hl7.getLastName() + ", " + hl7.getFirstName();
			lbData.sex = hl7.getSex();
			lbData.label = hl7.getLabel();

			lbData.resultStatus = hl7.getResultStatus();
			if (lbData.resultStatus != null && lbData.resultStatus.equals("A")) { 
				lbData.abn = true;
			}

			lbData.dateTime = hl7.getObrDate();

			//priority
			String priority = hl7.getPriority();

			if (priority != null && !priority.equals("")) {
				switch (priority.charAt(0)) {
				case 'C':
					lbData.priority = "Critical";
					break;
				case 'S':
					lbData.priority = "Stat/Urgent";
					break;
				case 'U':
					lbData.priority = "Unclaimed";
					break;
				case 'A':
					lbData.priority = "ASAP";
					break;
				case 'L':
					lbData.priority = "Alert";
					break;
				default:
					lbData.priority = "Routine";
					break;
				}
			} else {
				lbData.priority = "----";
			}

			lbData.requestingClient = hl7.getRequestingProvider();
			lbData.reportStatus = hl7.getReportStatus();

			// the "C" is for corrected excelleris labs
			if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))) {
				lbData.finalRes = true;
			} else {
				lbData.finalRes = false;
			}

			lbData.discipline = hl7.getDiscipline();
			lbData.finalResultsCount = hl7.getFinalResultCount();
			labResults.add(lbData);
		}

		return labResults;
	}

	public static ArrayList<LabResultData> populateHl7ResultsData(String providerNo, String demographicNo, String patientFirstName, String patientLastName, String patientHealthNumber, String status, boolean isPaged, Integer page, Integer pageSize, boolean mixLabsAndDocs, Boolean isAbnormal) {

		if (providerNo == null) {
			providerNo = "";
		}
		boolean searchProvider = !"-1".equals(providerNo) && !"".equals(providerNo);
		if (patientFirstName == null) {
			patientFirstName = "";
		}
		if (patientLastName == null) {
			patientLastName = "";
		}
		if (patientHealthNumber == null) {
			patientHealthNumber = "";
		}
		if (status == null || "U".equals(status)) {
			status = "";
		}

		boolean patientSearch = !"".equals(patientFirstName) || !"".equals(patientLastName) || !"".equals(patientHealthNumber);

		ArrayList<LabResultData> labResults = new ArrayList<LabResultData>();
		// note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
		// for the provider number if unable to find correct provider

		for (Object[] i : hl7TxtInfoDao.findLabAndDocsViaMagic(providerNo, demographicNo, patientFirstName, patientLastName, patientHealthNumber, status, isPaged, page, pageSize, mixLabsAndDocs, isAbnormal, searchProvider, patientSearch)) {

			String label = String.valueOf(i[0]);
			String lab_no = String.valueOf(i[1]);
			String sex = String.valueOf(i[2]);
			String health_no = String.valueOf(i[3]);
			String result_status = String.valueOf(i[4]);
			String obr_date = String.valueOf(i[5]);
			String priority = String.valueOf(i[6]);
			String requesting_client = String.valueOf(i[7]);
			String discipline = String.valueOf(i[8]);
			String last_name = String.valueOf(i[9]);
			String first_name = String.valueOf(i[10]);
			String report_status = String.valueOf(i[11]);
			String accessionNum = String.valueOf(i[12]);
			String final_result_count = String.valueOf(i[13]);
			String routingStatus = String.valueOf(i[14]);

			LabResultData lbData = new LabResultData(LabResultData.HL7TEXT);
			lbData.labType = LabResultData.HL7TEXT;
			lbData.segmentID = lab_no;

			if (demographicNo == null && !providerNo.equals("0")) {
				lbData.acknowledgedStatus = routingStatus;
			} else {
				lbData.acknowledgedStatus = "U";
			}

			lbData.accessionNumber = accessionNum;
			lbData.healthNumber = health_no;
			lbData.patientName = last_name + ", " + first_name;
			lbData.sex = sex;
			lbData.label = label;

			lbData.resultStatus = result_status;
			if (lbData.resultStatus != null && lbData.resultStatus.equals("A")) {
				lbData.abn = true;
			}

			lbData.dateTime = obr_date;

			if (priority != null && !priority.equals("")) {
				switch (priority.charAt(0)) {
				case 'C':
					lbData.priority = "Critical";
					break;
				case 'S':
					lbData.priority = "Stat/Urgent";
					break;
				case 'U':
					lbData.priority = "Unclaimed";
					break;
				case 'A':
					lbData.priority = "ASAP";
					break;
				case 'L':
					lbData.priority = "Alert";
					break;
				default:
					lbData.priority = "Routine";
					break;
				}
			} else {
				lbData.priority = "----";
			}

			lbData.requestingClient = requesting_client;
			lbData.reportStatus = report_status;

			// the "C" is for corrected excelleris labs
			if (lbData.reportStatus != null && (lbData.reportStatus.equals("F") || lbData.reportStatus.equals("C"))) {
				lbData.finalRes = true;
			} else if (lbData.reportStatus != null && lbData.reportStatus.equals("X")) {
				lbData.cancelledReport = true;
			} else {
				lbData.finalRes = false;
			}

			lbData.discipline = discipline;
			lbData.finalResultsCount = ConversionUtils.fromIntString(final_result_count);
			labResults.add(lbData);
		}

		return labResults;
	}
}
