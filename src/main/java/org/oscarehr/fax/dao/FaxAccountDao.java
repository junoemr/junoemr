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
package org.oscarehr.fax.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.search.FaxAccountCriteriaSearch;
import org.oscarehr.ws.rest.conversion.FaxTransferConverter;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.transfer.fax.FaxAccountTransferOutbound;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FaxAccountDao extends AbstractDao<FaxAccount>
{
	public FaxAccountDao()
	{
		super(FaxAccount.class);
	}

	public RestSearchResponse<FaxAccountTransferOutbound> listAccounts(Integer page, Integer perPage)
	{
		int offset = calculatedOffset(page, perPage);

		FaxAccountCriteriaSearch criteriaSearch = new FaxAccountCriteriaSearch();
		criteriaSearch.setOffset(offset);
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setSortDirAscending();

		int total = criteriaSearchCount(criteriaSearch);
		List<FaxAccount> accountList = criteriaSearch(criteriaSearch);
		return RestSearchResponse.successResponse(FaxTransferConverter.getAllAsOutboundTransferObject(accountList), page, perPage, total);
	}

	/**
	 * calculate the offset based on the current page number and resultCount
	 * @param pageNo - page
	 * @param resultsPerPage - limit of results
	 * @return offset
	 */
	protected int calculatedOffset(int pageNo, int resultsPerPage)
	{
		return resultsPerPage * (pageNo - 1);
	}
}
