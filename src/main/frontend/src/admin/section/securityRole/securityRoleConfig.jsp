<%--
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
--%>
<div class="security-role-config">
	<juno-security-check access="$ctrl.access" permissions="$ctrl.permissions">
		<h1>Manage Security Access Roles</h1>
		<ul class="list-group">
			<li ng-repeat="role in $ctrl.rolesList" class="list-group-item">
				<span>{{role.name}}</span>
				<span>{{role.description}}</span>
				<juno-button click="$ctrl.onRoleDetails(role)">
					Details
				</juno-button>
			</li>
		</ul>
		<juno-button>
			<juno-button ng-if="!$ctrl.newRole"
			             component-style="$ctrl.componentStyle"
			             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
			             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
			             ng-click="$ctrl.onAddRole()"
			             disabled="!$ctrl.canAddRole()">
				Create New Role
			</juno-button>
		</juno-button>
	</juno-security-check>
</div>