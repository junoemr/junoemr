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
import org.oscarehr.common.dao.BillingreferralDao;
import org.oscarehr.common.dao.ProfessionalSpecialistDao;
import org.oscarehr.common.model.Billingreferral;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.ws.rest.conversion.ReferralDoctorConverterBC;
import org.oscarehr.ws.rest.conversion.ReferralDoctorConverterON;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.ReferralDoctorTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/referralDoctors")
@Component("ReferralDoctorsService")
public class ReferralDoctorsService extends AbstractServiceImpl
{
	private Logger logger = Logger.getLogger(ReferralDoctorsService.class);
	private OscarProperties props = OscarProperties.getInstance();

	@Autowired
	private BillingreferralDao billingreferralDao;

	@Autowired
	private ProfessionalSpecialistDao specialistDao;

	private ReferralDoctorConverterBC converterBC = new ReferralDoctorConverterBC();
	private ReferralDoctorConverterON converterON = new ReferralDoctorConverterON();


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<ReferralDoctorTo1>> searchSpecialists(@QueryParam("searchName") String searchName,
	                                                                       @QueryParam("searchRefNo") String searchRefNo,
	                                                                       @QueryParam("page") @DefaultValue("1") Integer page,
	                                                                       @QueryParam("perPage") @DefaultValue("10") Integer perPage)
	{
		logger.debug("SEARCH REFERRAL DOCS: '" + searchName + "', '" + searchRefNo + "', " + page + ", " + perPage);
		try
		{
			if(page < 1) page = 1;
			int offset = perPage * (page-1);

			searchName = StringUtils.trimToNull(searchName);
			searchRefNo = StringUtils.trimToNull(searchRefNo);

			String province = props.getInstanceType();

			List<ReferralDoctorTo1> referralDocList;
			switch (province)
			{
				case "BC":
				{
					referralDocList = searchReferralDocsBC(searchName,searchRefNo,offset,perPage);
					break;
				}
				case "ON":
				default:
				{
					referralDocList = searchReferralDocsON(searchName,searchRefNo,offset,perPage);
					break;
				}
			}
			return RestResponse.successResponse(referralDocList);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
	}

	private List<ReferralDoctorTo1> searchReferralDocsBC(String searchName, String referralNo, int offset, int limit)
	{
		String[] names = SpecialistsService.splitSearchString(searchName);
		List<Billingreferral> referralDocs = billingreferralDao.findByFullNameAndReferralNo(names[0], names[1], referralNo, offset, limit);
		return converterBC.getAllAsTransferObjects(getLoggedInInfo(), referralDocs);
	}
	private List<ReferralDoctorTo1> searchReferralDocsON(String searchName, String referralNo, int offset, int limit)
	{
		List<ProfessionalSpecialist> specialists = SpecialistsService.getSpecialistSearchResults(specialistDao, searchName, referralNo, offset, limit);
		return converterON.getAllAsTransferObjects(getLoggedInInfo(), specialists);
	}
}
