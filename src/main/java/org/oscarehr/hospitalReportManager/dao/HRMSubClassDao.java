/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.hospitalReportManager.model.HRMSubClass;
import org.springframework.stereotype.Repository;

@Repository
public class HRMSubClassDao extends AbstractDao<HRMSubClass> {

	public HRMSubClassDao() {
		super(HRMSubClass.class);
	}

	public List<HRMSubClass> findById(int id) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.id=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<HRMSubClass> documents = query.getResultList();
		return documents;
	}

	public List<HRMSubClass> listAll() {
		String sql = "select x from " + this.modelClass.getName() + " x ";
		Query query = entityManager.createQuery(sql);

		@SuppressWarnings("unchecked")
		List<HRMSubClass> subclasses = query.getResultList();
		return subclasses;
	}

	public boolean subClassMappingExists(String className, String subClassName) {
		return subClassMappingExists(className, subClassName, "*");
	}

	public boolean subClassMappingExists(String className, String subClassName, String sendingFacilityId) {
		return subClassMappingExists(className, subClassName, null, sendingFacilityId);
	}

	public boolean subClassMappingExists(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.className=?1 and x.subClassName=?2  and x.subClassMnemonic=?3 and x.sendingFacilityId=?4";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, className);
		query.setParameter(2, subClassName);
		query.setParameter(3, subClassMnemonic);
		query.setParameter(4, sendingFacilityId);

		try {
			return (query.getSingleResult() != null);
		} catch (Exception e) {
			return false;
		}
	}

	private HRMSubClass findSubClassMapping(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
		String sql = null;

		if (subClassMnemonic != null){
			sql = "select x from " + this.modelClass.getName() + " x where x.className=?1 and x.subClassName=?2 and x.sendingFacilityId=?3 and x.subClassMnemonic=?4";
		}else{
			sql = "select x from " + this.modelClass.getName() + " x where x.className=?1 and x.subClassName=?2 and x.sendingFacilityId=?3";
		}
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, className);
		query.setParameter(2, subClassName);
		query.setParameter(3, sendingFacilityId);

		if (subClassMnemonic != null){
			query.setParameter(4, subClassMnemonic);
		}

		return (HRMSubClass) query.getSingleResult();
	}

	public HRMSubClass findApplicableSubClassMapping(String className, String subClassName, String subClassMnemonic, String sendingFacilityId) {
		HRMSubClass mapping = null;
		try {
			mapping = findSubClassMapping(className, subClassName, subClassMnemonic, sendingFacilityId);
		} catch (Exception e) {
			// Didn't find one... try a wildcard search
			try {
				mapping = findSubClassMapping(className, subClassName, subClassMnemonic, "*");
			} catch (Exception e2) {
				// Didn't find one that way either
				mapping = null;
			}
		}
		return mapping;
	}
}