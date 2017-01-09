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


package org.oscarehr.common.dao;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessageInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class Hl7TextInfoDao extends AbstractDao<Hl7TextInfo> {

	public Hl7TextInfoDao() {
		super(Hl7TextInfo.class);
	}

	/**
	 * LabId is also refereed to as Lab_no, and segmentId.
	 */
    public Hl7TextInfo findLabId(int labId) {

    	String sqlCommand="select x from Hl7TextInfo x where x.labNumber=?1";

    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, labId);

		return(getSingleResultOrNull(query));
    }

    @SuppressWarnings("unchecked")
    public List<Hl7TextInfo> findByHealthCardNo(String hin) {
    	String sql = "select hl7 from Hl7TextInfo hl7 where hl7.healthNumber = :hin";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter("hin", hin);
    	List<Hl7TextInfo> list = query.getResultList();
    	return list;
    }
    
    public List<Hl7TextInfo> searchByAccessionNumber(String acc) {

    	String sqlCommand="select x from Hl7TextInfo x where x.accessionNumber like ?1";

    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, "%"+acc+"%");

		@SuppressWarnings("unchecked")
		List<Hl7TextInfo> results = query.getResultList();

		return results;
    }
    
    public Hl7TextInfo findLatestVersionByAccessionNo(String acc) {
    	String sqlCommand="select x from Hl7TextInfo x where x.accessionNumber = :accession " +
    			" order by obr_date, lab_no DESC";

    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter("accession", acc);

		return (getSingleResultOrNull(query));
    }

    // Calgary labs are associated by Accession number usually. Glucose labs are not, but can be 
    // found by filler number
    public Hl7TextInfo findLatestVersionByAccessionNumberOrFillerNumber(String acc, String fillerNumber) {

    	String sqlCommand="select x from Hl7TextInfo x where x.accessionNumber = ?1 " +
    			"OR x.fillerOrderNum = ?2 order by lab_no DESC";

    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, acc);
		query.setParameter(2, fillerNumber);

		return (getSingleResultOrNull(query));
    }

    public List<Hl7TextInfo> searchByFillerOrderNumber(String fon, String sending_facility){
    	String sql = "select x from Hl7TextInfo x where x.fillerOrderNum=?1 and sendingFacility=?2";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1, fon);
    	query.setParameter(2, sending_facility);

    	@SuppressWarnings("unchecked")
		List<Hl7TextInfo> lab =  query.getResultList();

		return lab;


    }

    public void updateReportStatusByLabId(String reportStatus, int labNumber){
    	Query query = entityManager.createQuery("update " + modelClass.getName() + " x set x.reportStatus=? where x.labNumber=?");
		query.setParameter(1, reportStatus);
		query.setParameter(2, labNumber);

		query.executeUpdate();

    }

    public List<Hl7TextMessageInfo> getMatchingLabs(String hl7msg) {
    	Base64 base64 = new Base64(0);
    	String sql = "SELECT a.lab_no as id, m2.message, a.lab_no AS lab_no_A, b.lab_no AS lab_no_B, a.obr_date as labDate_A, b.obr_date as labDate_B FROM hl7TextInfo a, hl7TextInfo b, hl7TextMessage m2 WHERE m2.lab_id = a.lab_no AND a.accessionNum !='' AND a.accessionNum=b.accessionNum AND b.lab_no IN ( SELECT lab_id FROM hl7TextMessage WHERE message=?1 ) ORDER BY a.obr_date, a.lab_no";   	
    	Query query = entityManager.createNativeQuery(sql, Hl7TextMessageInfo.class);
    	try {
	        query.setParameter(1, (new String(base64.encode(hl7msg.getBytes(MiscUtils.ENCODING)), MiscUtils.ENCODING)));
        } catch (UnsupportedEncodingException e) {

	        MiscUtils.getLogger().error("Error setting query parameter hl7msg ",e);
        }

    	@SuppressWarnings("unchecked")
		List<Hl7TextMessageInfo> labs =  query.getResultList();
    	return labs;

    }

    public List<Hl7TextInfo> getAllLabsByLabNumberResultStatus() {
    	String sql = "SELECT x FROM Hl7TextInfo x";
    	Query query = entityManager.createQuery(sql);

    	@SuppressWarnings("unchecked")
		List<Hl7TextInfo> labs = query.getResultList();

    	return labs;
    }

    public void updateResultStatusByLabId(String resultStatus, int labNumber){
    	Query query = entityManager.createQuery("update " + modelClass.getName() + " x set x.resultStatus=? where x.labNumber=?");
		query.setParameter(1, resultStatus);
		query.setParameter(2, labNumber);

		query.executeUpdate();

    }

    public void createUpdateLabelByLabNumber(String label, int lab_no) {
    	String sql = "update Hl7TextInfo x set x.label=?1 where x.labNumber=?2";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1, label);
    	query.setParameter(2, lab_no);
    	query.executeUpdate();
    }
}
