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

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.ws.rest.conversion.ProfessionalSpecialistConverter;
import org.oscarehr.ws.rest.to.model.ProfessionalSpecialistTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/specialists")
@Component("SpecialistsService")
public class SpecialistsService extends AbstractServiceImpl
{
	Logger logger = Logger.getLogger(SpecialistsService.class);

	@Autowired
	private ProfessionalSpecialistDao specialistDao;

	private ProfessionalSpecialistConverter specialistConverter = new ProfessionalSpecialistConverter();

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<ProfessionalSpecialistTo1>, String> searchSpecialists(@QueryParam("searchName") String searchName,
	                                                                               @QueryParam("searchRefNo") Integer searchRefNo,
	                                                                               @QueryParam("page") @DefaultValue("1") Integer page,
	                                                                               @QueryParam("perPage") @DefaultValue("10")Integer perPage)
	{
		if(page < 1) page = 1;
		int offset = perPage * (page-1);

		try
		{
			List<ProfessionalSpecialist> specialists = new ArrayList<ProfessionalSpecialist>();
			if (searchName != null && searchRefNo != null) {
				specialists = specialistDao.findBySearchNameAndReferralNo(searchName, searchRefNo, offset, perPage);
			}
			else if (searchName != null) {
				specialists = specialistDao.findBySearchName(searchName, offset, perPage);
			}
			else if (searchRefNo != null) {
				specialists = specialistDao.findByReferralNo(searchRefNo, offset, perPage);
			}
			List<ProfessionalSpecialistTo1> specialistTo1s = specialistConverter.getAllAsTransferObjects(getLoggedInInfo(), specialists);
			return RestResponse.successResponse(specialistTo1s);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
	}
}
