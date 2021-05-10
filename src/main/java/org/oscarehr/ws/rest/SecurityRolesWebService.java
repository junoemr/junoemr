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
package org.oscarehr.ws.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.service.SecurityRolesService;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.transfer.security.SecurityPermissionTransfer;
import org.oscarehr.ws.rest.transfer.security.SecurityRoleTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/security")
@Component("SecurityRolesWebService")
@Tag(name = "securityRoles")
public class SecurityRolesWebService extends AbstractServiceImpl
{
	@Autowired
	private SecurityRolesService securityRolesService;

	@GET
	@Path("/permissions/all")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<SecurityPermissionTransfer> getAllPermissions()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_SECURITY_ROLES_READ);
		return RestSearchResponse.successResponseOnePage(securityRolesService.getAllSecurityPermissionsTransfer());
	}

	@GET
	@Path("roles")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<SecurityRoleTransfer> getRoles()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_SECURITY_ROLES_READ);
		return RestSearchResponse.successResponseOnePage(securityRolesService.getAllRoles());
	}

	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<SecurityRoleTransfer> addRole(SecurityRoleTransfer transfer)
	{
		String providerNo = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(providerNo, Permission.CONFIGURE_SECURITY_ROLES_CREATE);
		return RestResponse.successResponse(securityRolesService.addRole(providerNo, transfer));
	}

	@GET
	@Path("/role/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<SecurityRoleTransfer> getRole(@PathParam("roleId") Integer roleId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CONFIGURE_SECURITY_ROLES_READ);
		return RestResponse.successResponse(securityRolesService.getRole(roleId));
	}

	@PUT
	@Path("/role/{roleId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<SecurityRoleTransfer> updateRole(@PathParam("roleId") Integer roleId, SecurityRoleTransfer transfer) throws IllegalAccessException
	{
		String providerNo = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(providerNo, Permission.CONFIGURE_SECURITY_ROLES_UPDATE);
		return RestResponse.successResponse(securityRolesService.updateRole(providerNo, roleId, transfer));
	}

	@DELETE
	@Path("/role/{roleId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteRole(@PathParam("roleId") Integer roleId) throws IllegalAccessException
	{
		String providerNo = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(providerNo, Permission.CONFIGURE_SECURITY_ROLES_DELETE);
		return RestResponse.successResponse(securityRolesService.deleteRole(providerNo, roleId));
	}
}
