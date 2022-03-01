<%--

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

--%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="fax-send_receive">
	<div class="flex-row search-filters">
		<juno-select
				label="<bean:message bundle="ui" key="admin.fax.sr.choose-account"/>"
				label-position="$ctrl.LABEL_POSITION.TOP"
				ng-model="$ctrl.selectedFaxAccountId"
				options="$ctrl.faxAccountOptions"
				on-change="$ctrl.updateSelectedAccount(value, option)">
		</juno-select>
	</div>
	<div class="tabs-heading">
		<ul class="nav nav-tabs">
			<li>
				<a data-toggle="tab" ng-click="$ctrl.changeTab($ctrl.tabEnum.inbox);">
					<bean:message bundle="ui" key="admin.fax.sr.inbox"/></a>
			</li>
			<li class="active">
				<a data-toggle="tab" ng-click="$ctrl.changeTab($ctrl.tabEnum.outbox);">
					<bean:message bundle="ui" key="admin.fax.sr.outbox"/>
				</a>
			</li>
		</ul>
	</div>
	<div class="tabs-body">
		<div id="fax_inbox" class="tab-pane"
		     ng-show="$ctrl.activeTab == $ctrl.tabEnum.inbox">
			<fax-inbox ng-if="$ctrl.selectedFaxAccount"
			           fax-account="$ctrl.selectedFaxAccount"
			           component-style="$ctrl.componentStyle">
			</fax-inbox>
		</div>
		<div id="fax_outbox" class="tab-pane"
		     ng-show="$ctrl.activeTab == $ctrl.tabEnum.outbox">
			<fax-outbox ng-if="$ctrl.selectedFaxAccount"
			            fax-account="$ctrl.selectedFaxAccount"
			            component-style="$ctrl.componentStyle">
			</fax-outbox>
		</div>
	</div>
</div>