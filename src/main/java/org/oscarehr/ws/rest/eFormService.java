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

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.EFormDao;
import org.oscarehr.common.model.EForm;
import org.oscarehr.ws.rest.conversion.EFormConverter;
import org.oscarehr.ws.rest.to.model.EFormTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/eform")
@Component("eFormService")
public class eFormService extends AbstractServiceImpl {
	Logger logger = Logger.getLogger(eFormService.class);

	@Autowired
	private EFormDao eFormDao;


	/**
	 * Saves an eform. Performs an update if the eform has an fid, otherwise it will save a new eform.
	 * @param jsonString
	 * @return ResponseEntity with data from the eform saved. response does not include html
	 */
	@POST
	@Path("/saveEForm")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<EFormTo1, String> saveEForm(String jsonString) {

		HttpHeaders responseHeaders = new HttpHeaders();

		JSONObject jsonObject = JSONObject.fromObject(jsonString);

		Integer fid = jsonObject.optInt("id");
		String formName = jsonObject.getString("formName");
		String formSubject = jsonObject.optString("formSubject", null);
		String formHtml = jsonObject.getString("formHtml");

		String roleType = jsonObject.optString("roleType", null);
		Boolean showLatestFormOnly = jsonObject.optBoolean("showLatestFormOnly", false);
		Boolean patientIndependant = jsonObject.optBoolean("patientIndependant", false);

		EForm eform;
		// try to update an existing eform
		if (fid != null && fid > 0) {
			eform = eFormDao.findById(fid);

			Date now = new Date();
			eform.setFormDate(now);
			eform.setFormTime(now);
		}
		// new eform
		else {
			EForm nameMatch = eFormDao.findByName(formName);
			if(nameMatch != null) {
				logger.warn("EForm Name Already in Use. Save Aborted");
				return RestResponse.errorResponse(responseHeaders, "EForm Name Already in Use");
			}

			eform = new EForm();
			String creatorId = getLoggedInInfo().getLoggedInProviderNo();
			eform.setCreator(creatorId);
		}

		eform.setFormName(formName);
		eform.setSubject(formSubject);
		eform.setFormHtml(formHtml);
		eform.setCurrent(true);
		eform.setShowLatestFormOnly(showLatestFormOnly);
		eform.setPatientIndependent(patientIndependant);
		eform.setRoleType(roleType);

		//validate eform objects before saving
		/*ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		for(EForm eform : eformList) {
			final Set<ConstraintViolation<EForm>> formErrors = validator.validate(eform);
			for(ConstraintViolation<EForm> violation : formErrors) {
				errors.add(eform.getFormName() + ": " + violation.getMessage());
			}
		}*/

		if(eform.getId() != null) {
			eFormDao.merge(eform);
			LogAction.addLogEntry(getLoggedInInfo().getLoggedInProviderNo(), null,
					LogConst.ACTION_UPDATE, LogConst.CON_EFORM, LogConst.STATUS_SUCCESS,
					String.valueOf(eform.getId()), getLoggedInInfo().getIp(), eform.getFormName());
		}
		else {
			eFormDao.persist(eform);
			LogAction.addLogEntry(getLoggedInInfo().getLoggedInProviderNo(), null,
					LogConst.ACTION_ADD, LogConst.CON_EFORM, LogConst.STATUS_SUCCESS,
					String.valueOf(eform.getId()), getLoggedInInfo().getIp(), eform.getFormName());
		}

		EFormTo1 transferObj = new EFormConverter(true).getAsTransferObject(getLoggedInInfo(), eform);
		return RestResponse.successResponse(responseHeaders, transferObj);
	}

	/**
	 * retrieves an eForm with the given id.
	 * @return ResponseEntity containing data for the eForm. includes full html
	 */
	@GET
	@Path("/{dataId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<EFormTo1, String> loadEForm(@PathParam("dataId") Integer id) {

		HttpHeaders responseHeaders = new HttpHeaders();
		EForm eform = null;

		// try to update an existing eform
		if (id != null && id > 0) {
			eform = eFormDao.findById(id);
		}
		if(eform == null) {
			return RestResponse.errorResponse(responseHeaders, "Failed to find EForm");
		}
		EFormTo1 transferObj = new EFormConverter(false).getAsTransferObject(getLoggedInInfo(), eform);
		return RestResponse.successResponse(responseHeaders, transferObj);
	}
}
