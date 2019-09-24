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
package org.oscarehr.dx.service;

import org.oscarehr.common.dao.AbstractCodeSystemDao;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.util.Date;
import java.util.List;

@Component
public class DxResearchService
{
	@Autowired
	DxresearchDAO dxresearchDAO;

	@Transactional
	public void assignDxCodeToDemographic(Integer demographicNo, Integer providerNo, String dxCode, String codingSystem)
	{
		if (dxCode.compareTo("") != 0) {
			List<Dxresearch> research = dxresearchDAO.findByDemographicNoResearchCodeAndCodingSystem(ConversionUtils.fromIntString(demographicNo), dxCode, codingSystem);

			int count = 0;
			for (Dxresearch r : research) {
				count = count + 1;

				r.setUpdateDate(new Date());
				r.setStatus('A');

				dxresearchDAO.save(r);
			}

			if (count == 0) {
				String daoName = AbstractCodeSystemDao.getDaoName(AbstractCodeSystemDao.codingSystem.valueOf(codingSystem));

				@SuppressWarnings("unchecked")
				AbstractCodeSystemDao<AbstractCodeSystemModel<?>> csDao = (AbstractCodeSystemDao<AbstractCodeSystemModel<?>>) SpringUtils.getBean(daoName);

				AbstractCodeSystemModel<?> codingSystemEntity = csDao.findByCodingSystem(codingSystem);
				boolean isCodingSystemAvailable = codingSystemEntity == null;

				if (!isCodingSystemAvailable) {
					throw new RuntimeException("Error dx code not found. Code: " + dxCode + " Coding System: " + codingSystem);
				} else {
					Dxresearch dr = new Dxresearch();
					dr.setDemographicNo(demographicNo);
					dr.setStartDate(new Date());
					dr.setUpdateDate(new Date());
					dr.setStatus('A');
					dr.setDxresearchCode(dxCode);
					dr.setCodingSystem(codingSystem);
					dr.setProviderNo(providerNo.toString());
					dxresearchDAO.persist(dr);
				}
			}
		}
	}
}
