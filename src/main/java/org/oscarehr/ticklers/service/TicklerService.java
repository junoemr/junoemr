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

package org.oscarehr.ticklers.service;

import java.util.Date;
import java.util.List;

import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.PaginationQuery;
import org.oscarehr.ticklers.dao.TicklerCategoryDao;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ticklers.dao.TicklersDao;
import org.oscarehr.ticklers.search.TicklerCriteriaSearch;
import org.oscarehr.ticklers.web.TicklerQuery;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.log.LogAction;

import javax.validation.ValidationException;

@Component
public class TicklerService extends AbstractServiceImpl
{
	@Autowired
	private TicklersDao TicklerDao;

	@Autowired
	private TicklerCategoryDao ticklerCategoryDao;

	@Autowired
	private ProgramManager programManager;

	/**
	 * Use to get ticklers count for pagination display
	 * @param paginationQuery
	 * @return int count of ticklers
	 */
	public int getTicklersCount(PaginationQuery paginationQuery) {
		return this.TicklerDao.getTicklersCount(paginationQuery);
	}
	
	/**
	 * List ticklers
	 * @param paginationQuery
	 * @return List of type Tickler
	 */
	public List<Tickler> getTicklers(LoggedInInfo loggedInInfo, PaginationQuery paginationQuery) {
		TicklerQuery query = (TicklerQuery) paginationQuery;

		List<Tickler> results = TicklerDao.getTicklers(query);
		//--- log action ---
		if (results.size()>0) {
			String resultIds=Tickler.getIdsAsStringList(results);
			LogAction.addLogSynchronous(loggedInInfo, "TicklerService.getTicklers", "ids returned=" + resultIds);
		}
				
		return results;
	}

	/**
	 * create a new tickler
	 * @param tickler - the tickler to create
	 * @return - the new tickler
	 * @throws ValidationException - if a required tickler field is missing
	 */
	public Tickler createTickler(Tickler tickler) throws ValidationException
	{
		if (tickler.getId() != null)
		{// id must be null to create new tickler
			tickler.setId(null);
		}

		//force program id to oscar default
		tickler.setProgramId(programManager.getDefaultProgramId());

		validateAndDefaultTickler(tickler);
		TicklerDao.persist(tickler);
		return tickler;
	}

	/**
	 * update a tickler record.
	 * @param tickler - tickler to update
	 * @return - the updated tickler
	 * @throws ValidationException - if a required tickler field is missing
	 */
	public Tickler updateTickler(Tickler tickler) throws ValidationException
	{
		validateAndDefaultTickler(tickler);
		tickler.setUpdateDate(new Date());

		TicklerDao.merge(tickler);
		return tickler;
	}

	/**
	 * ensure that a tickler is configured correctly and set default values on null fields
	 * @param tickler - the tickler to validate
	 * @return - the validated tickler
	 * @throws ValidationException - if a required tickler field is missing
	 */
	private Tickler validateAndDefaultTickler(Tickler tickler) throws ValidationException
	{
		if (tickler.getDemographicNo() == null)
		{
			throw new ValidationException("Demographic number is required for tickler");
		}
		if (tickler.getStatus() == null)
		{
			tickler.setStatus(Tickler.STATUS.A);
		}
		if (tickler.getUpdateDate() == null)
		{
			tickler.setUpdateDate(new Date());
		}
		if (tickler.getServiceDate() == null)
		{
			throw new ValidationException("Service date is required for tickler");
		}
		if (tickler.getCreator() == null)
		{
			throw new ValidationException("Creator number is required for tickler");
		}
		if (tickler.getTaskAssignedTo() == null)
		{
			throw new ValidationException("Assigned number is required for tickler");
		}
		if (tickler.getCategoryId() != null && ticklerCategoryDao.find(tickler.getCategoryId()) == null)
		{
			throw new ValidationException("Tickler category id is not valid");
		}

		return tickler;
	}

	public List<Tickler> getSearchResponse(TicklerCriteriaSearch criteriaSearch, int page, int perPage)
	{
		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		criteriaSearch.setLimit(perPage);
		criteriaSearch.setOffset(offset);

		return TicklerDao.criteriaSearch(criteriaSearch);
	}

	public int getTicklerCount(TicklerCriteriaSearch criteriaSearch)
	{
		return TicklerDao.criteriaSearchCount(criteriaSearch);
	}
}
