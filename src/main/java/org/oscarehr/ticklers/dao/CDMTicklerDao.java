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

package org.oscarehr.ticklers.dao;

import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ticklers.entity.CDMTicklerInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This is an entity-less DAO used to determine which demographics need CDM billing reminder ticklers to be created or deleted.
 * It does not use JPA or Hibernate mapping.
 */
@Component
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class CDMTicklerDao
{
    // TODO: SPRINGUPGRADE: make multiple connections work
	@PersistenceContext
    private EntityManager entityManager;

    /**
     * Get a list of all active demographics which have been diagnosed with the CDM dx code, but haven't been billed with the
     * corresponding CDM billing code ever, or haven't been billed for the CDM billing code in the past 365 days; for which
     * there is no active reminder tickler message in the system.
     *
     * A CDM reminder tickler for a billing code is one that contains the exact string 'SERVICE CODE {CDM billing code}'
     *
     * @param cdmDxCodes CDM dx codes
     * @return CDM tickler info for patients which need a CDM reminder tickler generated.
     */
    public List<CDMTicklerInfo> getCDMTicklerCreationInfo(Set<Integer> cdmDxCodes, Set<String> inactivePatientStatuses)
    {
        Query query = entityManager.createNativeQuery(
         "SELECT d.demographic_no, d.provider_no, dxr.dxresearch_code, sc.serviceCode, bm.date, " +
                 " t.tickler_no " +
            "FROM demographic d " +
            "JOIN dxresearch dxr " +
                "ON d.demographic_no = dxr.demographic_no " +
                "AND dxr.dxresearch_code IN (:cdmCodes) " +
                "AND dxr.status = 'A' " +
                "AND d.patient_status NOT IN (:inactivePatientStatuses) " +
            "JOIN billing_cdm_service_codes sc " +
                "ON dxr.dxresearch_code = sc.cdmCode " +
            "LEFT JOIN (" +
                "SELECT demographic_no, billing_code, billingstatus, max(service_date) AS date " +
                "FROM billingmaster " +
                "WHERE billingstatus NOT IN ('D', 'R', 'F')" +
                "GROUP BY demographic_no, billing_code, billingstatus " +
            ") bm " +
                "ON d.demographic_no = bm.demographic_no " +
                "AND bm.billing_code = sc.serviceCode " +
            "LEFT JOIN tickler t ON t.demographic_no = d.demographic_no " +
                "AND t.message LIKE CONCAT('%', 'SERVICE CODE ', sc.serviceCode, '%') " +
                "AND t.status = :ticklerStatus " +
            "WHERE ((DATEDIFF(NOW(), bm.date) >= 365) OR bm.date IS NULL)" +
                "AND t.tickler_no IS NULL " +
                "AND NOT EXISTS (" +
                    "SELECT * " +
                    "FROM billing_service_code_conditions sc_con " +
                    "JOIN " +
                    "("+
                        "SELECT demographic_no, billing_code, billingstatus, max(service_date) AS date " +
                        "FROM billingmaster " +
                        "GROUP BY demographic_no, billing_code, billingstatus " +
                    ") bm2 " +
                    "ON sc_con.conditionCode = bm2.billing_code " +
                    "WHERE d.demographic_no = bm2.demographic_no " +
                    "AND sc_con.serviceCode = sc.serviceCode " +
                    "AND ((DATEDIFF(NOW(), bm2.date) <= 365) AND billingstatus NOT IN ('D', 'R', 'F')) " +
            ")");

        query.setParameter("inactivePatientStatuses", inactivePatientStatuses);
        query.setParameter("cdmCodes", cdmDxCodes);
        query.setParameter("ticklerStatus", Tickler.ACTIVE);
        List<Object[]> rawResult = query.getResultList();

        return toEntityList(rawResult);
    }

    /**
     * Get information on which CDM ticklers should be archived.  These are patients for which an active CDM code has
     * been billed less than 365 days ago, for which an active CDM reminder tickler exists.
     *
     * A CDM reminder tickler for a billing code is one that contains the exact string 'SERVICE CODE {CDM billing code}'
     *
     * @param cdmTicklerDxCodes CDM dx codes
     * @return Information on which ticklers can be archived
     */
    public List<CDMTicklerInfo> getCDMTicklersToDelete(Set<Integer> cdmTicklerDxCodes)
    {
        Query query = entityManager.createNativeQuery(
           "SELECT d.demographic_no, d.provider_no, dxr.dxresearch_code, bm.billing_code, bm.date, t.tickler_no " +
            "FROM demographic d " +
            "JOIN dxresearch dxr " +
                "ON d.demographic_no = dxr.demographic_no " +
                "AND dxr.dxresearch_code IN (:cdmTicklerDxCodes) " +
                "AND dxr.status = 'A' " +
            "JOIN billing_cdm_service_codes sc " +
                "ON dxr.dxresearch_code = sc.cdmCode " +
            "LEFT JOIN (" +
                "SELECT demographic_no, billing_code, billingstatus, max(service_date) AS date " +
                "FROM billingmaster " +
                "GROUP BY demographic_no, billing_code, billingstatus" +
            ") bm " +
                "ON d.demographic_no = bm.demographic_no " +
                "AND bm.billing_code = sc.serviceCode " +
                "AND bm.date IS NOT NULL " +
            "JOIN tickler t " +
                "ON t.demographic_no = d.demographic_no " +
                "AND t.message LIKE CONCAT('%', 'SERVICE CODE ', sc.serviceCode, '%') " +
                "AND t.status = :ticklerStatus " +
            "WHERE (billingstatus NOT IN ('D', 'R', 'F') AND DATEDIFF(NOW(), bm.date) < 365) " +
            "OR EXISTS (" +
                   "SELECT * " +
                   "FROM billing_service_code_conditions sc_con " +
                   "JOIN " +
                   "("+
                       "SELECT demographic_no, billing_code, billingstatus, max(service_date) AS date " +
                       "FROM billingmaster " +
                       "GROUP BY demographic_no, billing_code, billingstatus " +
                   ") bm2 " +
                   "ON bm2.billing_code = sc_con.conditionCode " +
                   "WHERE d.demographic_no = bm2.demographic_no " +
                   "AND sc_con.serviceCode = sc.serviceCode " +
                   "AND ((DATEDIFF(NOW(), bm2.date) <= 365) AND billingstatus NOT IN ('D', 'R', 'F')) " +
           ")");

        query.setParameter("cdmTicklerDxCodes", cdmTicklerDxCodes);
        query.setParameter("ticklerStatus", Tickler.ACTIVE);
        List<Object[]> rawResult = query.getResultList();

        return toEntityList(rawResult);
    }

    private List<CDMTicklerInfo> toEntityList(List<Object[]> rawResults)
    {
        List<CDMTicklerInfo> ticklerInfoList = new ArrayList<>();
        for (Object[] result : rawResults)
        {
            CDMTicklerInfo ticklerInfo = toEntity(result);
            ticklerInfoList.add(ticklerInfo);
        }

        return ticklerInfoList;
    }

    private CDMTicklerInfo toEntity(Object[] rawResult)
    {
        return new CDMTicklerInfo(rawResult);
    }
}
