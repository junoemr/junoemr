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

package org.oscarehr.provider.dao;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.provider.model.ProgramProvider;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Repository("provider.dao.ProgramProviderDao")
@Transactional
public class ProgramProviderDao extends AbstractDao<ProgramProvider>
{
	Logger logger = MiscUtils.getLogger();

	public ProgramProviderDao()
	{
		super(ProgramProvider.class);
	}

	public ProgramProvider getProgramProvider(String providerNo, Long programId) {
		if (providerNo == null) {
			throw new IllegalArgumentException();
		}
		if (programId == null || programId.intValue() <= 0) {
			throw new IllegalArgumentException();
		}

		Query query = entityManager.createQuery("SELECT pp FROM model.ProgramProvider pp where pp.providerNo = :providerNo and pp.programId = :programId");
		query.setParameter("providerNo", providerNo);
		query.setParameter("programId", programId);
		List<ProgramProvider> results = query.getResultList();
		ProgramProvider result =null;
		if(!results.isEmpty())
		{
			result = results.get(0);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getProgramProvider: providerNo=" + providerNo + ",programId=" + programId + ",found=" + (result != null));
		}

		return result;
	}

	public void saveProgramProvider(ProgramProvider pp) {
		if (pp == null) {
			throw new IllegalArgumentException();
		}

		persist(pp);

		if (logger.isDebugEnabled()) {
			logger.debug("saveProgramProvider: id=" + pp.getId());
		}
	}
}
