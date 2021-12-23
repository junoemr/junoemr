/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang.StringUtils;

@Repository
@Transactional
@SuppressWarnings("unchecked")
public class ProviderLabRoutingDao extends AbstractDao<ProviderLabRoutingModel> {

	public static final Integer PROVIDER_UNMATCHED = 0;
	public static final String LAB_TYPE_HL7 = "HL7";
	public static final String LAB_TYPE_DOC = "DOC";

	public ProviderLabRoutingDao() {
		super(ProviderLabRoutingModel.class);
	}

	
	private List<ProviderLabRoutingModel> getProviderLabRoutings(Integer labNo, String labType, String providerNo, String status) {
		labType = StringUtils.trimToNull(labType);
		providerNo = StringUtils.trimToNull(providerNo);
		status = StringUtils.trimToNull(status);

		Query q = entityManager.createQuery("SELECT x FROM " +
												modelClass.getName() +
												" x WHERE (:labNo IS NULL OR x.labNo=:labNo) " +
												"AND (:labType IS NULL OR x.labType=:labType) " +
												"AND (:providerNo IS NULL OR x.providerNo=:providerNo) " +
												"AND (:status IS NULL OR x.status=:status)");
		q.setParameter("labNo", labNo);
		q.setParameter("labType", labType);
		q.setParameter("providerNo", providerNo);
		q.setParameter("status", status);

		return q.getResultList();
	}

	
    public List<ProviderLabRoutingModel> findByLabNoAndLabTypeAndProviderNo(int labNo, String labType, String providerNo) {
		Query q = entityManager.createQuery("select x from " + modelClass.getName() + " x where x.labNo=?1 and x.labType=?2 and x.providerNo=?3");
		q.setParameter(1, labNo);
		q.setParameter(2, labType);
		q.setParameter(3, providerNo);
	
		return q.getResultList();
	}
	
	public List<ProviderLabRoutingModel> getProviderLabRoutingDocuments(Integer labNo) {
		return getProviderLabRoutings(labNo, "DOC", null, null);
	}

	public List<ProviderLabRoutingModel> getProviderLabRoutingForLabProviderType(Integer labNo, String providerNo, String labType) {
		return getProviderLabRoutings(labNo, labType, providerNo, null);
	}

	public List<ProviderLabRoutingModel> getProviderLabRoutingForLabAndType(Integer labNo, String labType) {
		return getProviderLabRoutings(labNo, labType, null, ProviderInboxItem.NEW);
	}

	public void updateStatus(Integer labNo, String labType) {
		String updateString = "UPDATE " + modelClass.getName() + " x set x.status='N' WHERE x.labNo=?1 AND x.labType=?2";

		Query query = entityManager.createQuery(updateString);
		query.setParameter(1, labNo);
		query.setParameter(2, labType);

		query.executeUpdate();
	}
    /**
     * Retrieves a single ProviderLabRoutingModel from the database of type DOC, with the given lab id
     * @param labNo
     * @return ProviderLabRoutingModel or null
     */
	public ProviderLabRoutingModel findSingleDocRoute(Integer labNo) {
		return findByLabNoAndLabType(labNo, "DOC");
	}
    /**
     * Retrieves a single ProviderLabRoutingModel from the database of type HL7, with the given lab id
     * @param labNo
     * @return ProviderLabRoutingModel or null
     */
    public ProviderLabRoutingModel findSingleLabRoute(Integer labNo) {
    	return findByLabNoAndLabType(labNo, "HL7");
    }
    /**
     * Retrieves a single ProviderLabRoutingModel from the database with the given lab id and type
     * @param labNo
     * @param labType
     * @return ProviderLabRoutingModel or null
     */
	public ProviderLabRoutingModel findByLabNoAndLabType(int labNo, String labType) {
		String sqlCommand="SELECT x FROM "+ this.modelClass.getName() +" x WHERE x.labType=:labType AND x.labNo=:labNo";
		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter("labType", labType);
		query.setParameter("labNo", labNo);
		return(getSingleResultOrNull(query));
	}

	/**
	 * Finds all providers and lab routing models for the specified lab
	 * 
	 * @param labNo
	 * 		Lab number to find data for
	 * @param labType
	 * 		Lab type to find data for
	 * @return
	 * 		Returns a list containing {@link ProviderLabRoutingModel} objects.
	 */
	public List<ProviderLabRoutingModel> getProviderLabRoutings(Integer labNo, String labType) {
		return getProviderLabRoutings(labNo, labType, null, null);
	}

	
    public List<ProviderLabRoutingModel> findByStatusANDLabNoType(Integer labNo, String labType, String status) {
	    Query query = createQuery("r", "r.labNo = :labNo and r.labType = :labType and r.status = :status");
	    query.setParameter("labNo", labNo);
	    query.setParameter("labType", labType);
	    query.setParameter("status", status);
	    return query.getResultList();
    }

	
    public List<Object[]> findByProviderNo(String providerNo, String status) {
	    String sql = "SELECT " +
			" proLR.comment, " +
			" proLR.lab_no, " +
			" proLR.lab_type, " +
			" proLR.provider_no, " +
			" proLR.status, " +
			" proLR.timestamp " +
			"FROM providerLabRouting proLR " +
			"LEFT JOIN document doc ON proLR.lab_type = 'DOC' AND proLR.lab_no = doc.document_no AND doc.status <> 'D' " +
			"LEFT JOIN HRMDocument hrm ON proLR.lab_type = 'HRM' AND proLR.lab_no = hrm.id " +
			"LEFT JOIN hl7TextInfo lab ON proLR.lab_type = 'HL7' AND proLR.lab_no = lab.lab_no " +
			"LEFT JOIN hl7TextInfo lab2 ON lab.accessionNum = lab2.accessionNum AND (lab2.obr_date > lab.obr_date OR (lab2.obr_date = lab.obr_date AND lab2.lab_no > lab.lab_no)) " +
			"WHERE (doc.document_no IS NOT NULL OR lab.lab_no IS NOT NULL OR hrm.id IS NOT NULL) " +
			"AND proLR.provider_no = :provider_no " +
			"AND proLR.status = :status " +
			"AND lab2.lab_no IS NULL " +
			"ORDER BY lab.obr_date";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("provider_no", providerNo);
		query.setParameter("status", status);
		return query.getResultList();
    }
	
	public List<ProviderLabRoutingModel> findByLabNoTypeAndStatus(int labId, String labType, String status) {
		Query query = createQuery("p", "p.labNo = :lNo AND p.status = :sts AND p.labType = :lType");
		query.setParameter("lNo", labId);
		query.setParameter("sts", status);
		query.setParameter("lType", labType);
		return query.getResultList();
    }
        
    public List<Integer> findLastRoutingIdGroupedByProviderAndCreatedByDocCreator(String docCreator) {
        Query query = entityManager.createQuery("SELECT max(r.id) FROM ProviderLabRoutingModel r, Document d "
                + "WHERE d.documentNo=r.labNo AND d.doccreator= :docCreator AND r.providerNo!=0 AND r.providerNo!=-1 AND r.providerNo IS NOT NULL group by r.providerNo");
        query.setParameter("docCreator", docCreator);
        return query.getResultList();
    }

    public List<Object[]> findProviderAndLabRoutingById(Integer id) {
        String sql = "FROM Provider provider, ProviderLabRoutingModel providerLabRouting "
                + "WHERE provider.ProviderNo = providerLabRouting.providerNo "
                + "AND providerLabRouting.id = :id ";
        Query query = entityManager.createQuery(sql);
        query.setParameter("id", id);
        return query.getResultList();
    }

	@NativeSql({"providerLabRouting", "mdsMSH","mdsPID","mdsPV1","mdsZFR","mdsOBR","mdsZRG"})
	public List<Object[]> findMdsResultResultDataByManyThings(String status, String providerNo, String patientLastName, String patientFirstName, String patientHealthNumber) {
        // note to self: lab reports not found in the providerLabRouting table will not show up - need to ensure every lab is entered in providerLabRouting, with '0'
        // for the provider number if unable to find correct provider
		String sql;
        sql = "SELECT mdsMSH.segmentID, mdsMSH.messageConID AS accessionNum, providerLabRouting.status, mdsPID.patientName, mdsPID.healthNumber, " +
                "mdsPID.sex, max(mdsZFR.abnormalFlag) as abnormalFlag, mdsMSH.dateTime, mdsOBR.quantityTiming, mdsPV1.refDoctor, " +
                "min(mdsZFR.reportFormStatus) as reportFormStatus, mdsZRG.reportGroupDesc " +
                "FROM " +
                "providerLabRouting "+
                "LEFT JOIN mdsMSH on providerLabRouting.lab_no = mdsMSH.segmentID "+
                "LEFT JOIN mdsPID on providerLabRouting.lab_no = mdsPID.segmentID "+
                "LEFT JOIN mdsPV1 on providerLabRouting.lab_no = mdsPV1.segmentID "+
                "LEFT JOIN mdsZFR on providerLabRouting.lab_no = mdsZFR.segmentID "+
                "LEFT JOIN mdsOBR on providerLabRouting.lab_no = mdsOBR.segmentID "+
                "LEFT JOIN mdsZRG on providerLabRouting.lab_no = mdsZRG.segmentID "+
                "WHERE " +
                "providerLabRouting.lab_type = 'MDS' " +
                "AND providerLabRouting.status like '%"+status+"%' AND providerLabRouting.provider_no like '"+(providerNo.equals("")?"%":providerNo)+"' " +
                "AND mdsPID.patientName like '"+patientLastName+"%^"+patientFirstName+"%^%' AND mdsPID.healthNumber like '%"+patientHealthNumber+"%' group by mdsMSH.segmentID";
        Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	    
    }
	
	@NativeSql({"providerLabRouting", "mdsMSH","mdsPID","mdsPV1","mdsZFR","mdsOBR","mdsZRG"})
	public List<Object[]> findMdsResultResultDataByDemographicNoAndLabNo(Integer demographicNo, Integer labNo) {
       
		String sql;
        sql = "SELECT mdsMSH.segmentID, mdsMSH.messageConID AS accessionNum, providerLabRouting.status, mdsPID.patientName, mdsPID.healthNumber, " +
                "mdsPID.sex, max(mdsZFR.abnormalFlag) as abnormalFlag, mdsMSH.dateTime, mdsOBR.quantityTiming, mdsPV1.refDoctor, " +
                "min(mdsZFR.reportFormStatus) as reportFormStatus, mdsZRG.reportGroupDesc " +
                "FROM " +
                "patientLabRouting "+
                "LEFT JOIN providerLabRouting on patientLabRouting.lab_no = providerLabRouting.lab_no "+
                "LEFT JOIN mdsMSH on patientLabRouting.lab_no = mdsMSH.segmentID "+
                "LEFT JOIN mdsPID on patientLabRouting.lab_no = mdsPID.segmentID "+
                "LEFT JOIN mdsPV1 on patientLabRouting.lab_no = mdsPV1.segmentID "+
                "LEFT JOIN mdsZFR on patientLabRouting.lab_no = mdsZFR.segmentID "+
                "LEFT JOIN mdsOBR on patientLabRouting.lab_no = mdsOBR.segmentID "+
                "LEFT JOIN mdsZRG on patientLabRouting.lab_no = mdsZRG.segmentID "+
                "WHERE " +
                "patientLabRouting.lab_type = 'MDS' " +
                "AND patientLabRouting.demographic_no =  " + demographicNo +" and mdsMSH.segmentID = " + labNo + " group by mdsMSH.segmentID";
        Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	    
    }
	
	
	@NativeSql({"providerLabRouting", "mdsMSH","mdsPID","mdsPV1","mdsZFR","mdsOBR","mdsZRG"})
	public List<Object[]> findMdsResultResultDataByDemoId(String demographicNo) {
		String sql = "SELECT mdsMSH.segmentID, mdsMSH.messageConID AS accessionNum, mdsPID.patientName, mdsPID.healthNumber, " +
                "mdsPID.sex, max(mdsZFR.abnormalFlag) as abnormalFlag, mdsMSH.dateTime, mdsOBR.quantityTiming, mdsPV1.refDoctor, " +
                "min(mdsZFR.reportFormStatus) as reportFormStatus, mdsZRG.reportGroupDesc " +
                "FROM patientLabRouting "+
                "LEFT JOIN mdsMSH on patientLabRouting.lab_no = mdsMSH.segmentID "+
                "LEFT JOIN mdsPID on patientLabRouting.lab_no = mdsPID.segmentID "+
                "LEFT JOIN mdsPV1 on patientLabRouting.lab_no = mdsPV1.segmentID "+
                "LEFT JOIN mdsZFR on patientLabRouting.lab_no = mdsZFR.segmentID "+
                "LEFT JOIN mdsOBR on patientLabRouting.lab_no = mdsOBR.segmentID "+
                "LEFT JOIN mdsZRG on patientLabRouting.lab_no = mdsZRG.segmentID "+
                "WHERE " +
                "patientLabRouting.lab_type = 'MDS' " +
                "AND patientLabRouting.demographic_no='"+demographicNo+"' group by mdsMSH.segmentID";
		Query query = entityManager.createNativeQuery(sql);		
		return query.getResultList();
    }
	

	public List<Object[]> findProviderAndLabRoutingByIdAndLabType(Integer id, String labType) {
		String sql = "FROM Provider provider, ProviderLabRoutingModel providerLabRouting " +
				"WHERE provider.ProviderNo = providerLabRouting.providerNo " +
				"AND providerLabRouting.labNo = :id " +
				"AND providerLabRouting.labType = :labType";
		Query query = entityManager.createQuery(sql);
		query.setParameter("id", id);
		query.setParameter("labType", labType);
		return query.getResultList();
	}
	
}
