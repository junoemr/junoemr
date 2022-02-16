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

package org.oscarehr.providerBilling.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.providerBilling.model.ProviderBilling;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;

@Repository
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProviderBillingDao  extends AbstractDao<ProviderBilling>
{
	public ProviderBillingDao() {
		super(ProviderBilling.class);
	}

	/**
	 * get the billing record for the specified provider
	 * @param providerNo - id of the Provider linked to the billing record.
	 * @return - the billing record.
	 */
	public ProviderBilling findByProviderId(String providerNo)
	{
		Query query = entityManager.createQuery("SELECT p.billingOpts FROM ProviderData p WHERE p.id = :providerNo");
		query.setParameter("providerNo", providerNo);
		return getSingleResultOrNull(query);
	}
}
