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

package org.oscarehr.ws.rest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.ws.rest.conversion.ProfessionalSpecialistToTransferConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.ProfessionalSpecialistTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/specialists")
@Component("SpecialistsService")
public class SpecialistsService extends AbstractServiceImpl
{
	private static Logger logger = Logger.getLogger(SpecialistsService.class);

	@Autowired
	private ProfessionalSpecialistDao specialistDao;

	@Autowired
	private ProfessionalSpecialistToTransferConverter specialistToTransferConverter;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<ProfessionalSpecialistTo1>> searchSpecialists(@QueryParam("searchName") String searchName,
	                                                                               @QueryParam("searchRefNo") String searchRefNo,
	                                                                               @QueryParam("page") @DefaultValue("1") Integer page,
	                                                                               @QueryParam("perPage") @DefaultValue("10") Integer perPage)
	{
		if(page < 1) page = 1;
		int offset = perPage * (page-1);

		searchName = StringUtils.trimToNull(searchName);
		searchRefNo = StringUtils.trimToNull(searchRefNo);
		logger.debug("SEARCH SPECIALISTS: '" + searchName + "', '" + searchRefNo + "', " + page + ", " + perPage);

		try
		{
			List<ProfessionalSpecialist> specialists = getSpecialistSearchResults(specialistDao, searchName, searchRefNo, offset, perPage);
			List<ProfessionalSpecialistTo1> specialistTo1s = specialistToTransferConverter.convert(specialists);
			return RestResponse.successResponse(specialistTo1s);
		}
		catch (NumberFormatException e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("Invalid Integer Parameter");
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
	}

	public static List<ProfessionalSpecialist> getSpecialistSearchResults(ProfessionalSpecialistDao specialistDao, String searchName, String referralNo, int offset, int limit) {

		String[] names = splitSearchString(searchName);
		return specialistDao.findByFullNameAndReferralNo(names[0], names[1], referralNo, offset, limit);
	}

	/**
	 * splits the search text (on ',') into a string[] which will always have length >= 2
	 * values will be null if the search text was null, or the trimmed value after the split is an empty string
	 * @param searchText - string to split
	 * @return String[] of size 2 or more
	 */
	public static String[] splitSearchString(String searchText) {
		if(searchText == null) {
			return new String[] {null,null};
		}
		String[] searchTerms = searchText.split(",");

		// ensure 2 element array
		if(searchTerms.length == 1)
		{
			searchTerms = new String[] {searchTerms[0], ""};
		}
		// trim all elements
		for (int i = 0; i < searchTerms.length; i++)
		{
			searchTerms[i] = StringUtils.trimToNull(searchTerms[i]);
		}
		return searchTerms;
	}
}
