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


package oscar.oscarMDS.data;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.oscarehr.common.dao.InboxResultsDao;
import org.oscarehr.util.SpringUtils;

import oscar.oscarLab.ca.on.LabSummaryData;

public class CategoryData {

	private int totalDocs = 0;
	private int totalLabs = 0;
	private int unmatchedLabs = 0;
	private int unmatchedDocs = 0;
    private int totalNumDocs = 0;
    private int abnormalCount = 0;
    private int normalCount = 0;
    private HashMap<Integer,PatientInfo> patients;

	public int getTotalDocs() {
		return totalDocs;
	}

	public int getTotalLabs() {
		return totalLabs;
	}

	public int getUnmatchedLabs() {
		return unmatchedLabs;
	}

	public int getUnmatchedDocs() {
		return unmatchedDocs;
	}

	public int getTotalNumDocs() {
		return totalNumDocs;
	}

	public int getAbnormalCount() {
		return abnormalCount;
	}

	public int getNormalCount() {
		return normalCount;
	}

	public HashMap<Integer, PatientInfo> getPatients() {
		return patients;
	}

	private String patientLastName;
	private String searchProviderNo;
	private String status;
	private String patientFirstName;
	private String patientHealthNumber;
	private Date startDate;
	private Date endDate;
	private boolean patientSearch;
	private boolean providerSearch;

	public CategoryData(String patientLastName, String patientFirstName, String patientHealthNumber, boolean patientSearch,
			boolean providerSearch, String searchProviderNo, String status, Date startDate, Date endDate)  {

		this.patientLastName = patientLastName;
		this.searchProviderNo = searchProviderNo;
		this.status = status;
		this.patientFirstName = patientFirstName;
		this.patientHealthNumber = patientHealthNumber;
		this.patientSearch = patientSearch;
		this.providerSearch = providerSearch;
		this.startDate = startDate;
		this.endDate = endDate;

    	totalDocs = 0;
		totalLabs = 0;
		unmatchedLabs = 0;
	    unmatchedDocs = 0;
	    totalNumDocs = 0;
	    abnormalCount = 0;
	    normalCount = 0;

        patients = new HashMap<Integer,PatientInfo>();

	}
	public void populateCountsAndPatients() throws SQLException {

		InboxResultsDao inboxResultsDao = (InboxResultsDao) SpringUtils.getBean("inboxResultsDao");
		LabSummaryData result = inboxResultsDao.getInboxSummary(searchProviderNo, null, patientFirstName,
			patientLastName, patientHealthNumber, status, false, 0, 0, false, null, null, 
			false, null, startDate, endDate);
		
		this.patients = result.getPatients();

		// Retrieving documents and labs.
		totalDocs = result.getDocumentCount();
        totalLabs = result.getLabCount();

        // If this is not a patient search, then we need to find the unmatched documents.
        if (!patientSearch) {
            unmatchedDocs += result.getUnmatchedDocumentCount();
            unmatchedLabs += result.getUnmatchedLabCount();
            totalDocs += unmatchedDocs;
            totalLabs += unmatchedLabs;
        }

        // The total overall items is the sum of docs and labs.
        totalNumDocs = result.getTotalCount();

        // Retrieving abnormal labs.
        abnormalCount = result.getAbnormalCount();

        // Cheaper to subtract abnormal from total to find the number of normal docs.
        normalCount = totalNumDocs - abnormalCount;
	}

	public Long getCategoryHash() {
		return Long.valueOf("" + (int)'A' + totalNumDocs)
			 + Long.valueOf("" + (int)'D' + totalDocs)
			 + Long.valueOf("" + (int)'L' + totalLabs);
	}
}
