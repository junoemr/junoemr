package org.oscarehr.ws.rest.integrations.hrm;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.service.HRMSubClassService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/integrations/hrm/subclass")
@Component("HRMSubClassWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "hrmSubClass")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmSubClassWebService extends AbstractServiceImpl
{
	@Autowired
	HRMSubClassService hrmSubClassService;

	@Autowired
	SecurityInfoManager securityService;

	@GET
	@Path("/")
	public RestResponse<HrmSubClassModel> findActiveByAttributes(
		@QueryParam("sendingFacilityId") String facilityId,
		@QueryParam("reportClass") String reportClass,
		@QueryParam("subClassName") String subClassName,
		@QueryParam("accompanyingSubClassName") String accompanyingSubClassName)
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_READ);

		HrmSubClassModel model = hrmSubClassService.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);
		return RestResponse.successResponse(model);
	}
}