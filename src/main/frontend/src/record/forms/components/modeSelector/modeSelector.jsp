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

<div id="mode-selector-component" >
	<nav>
		<ul>
			<li class="library-list-item">
				<a href="javascript:" ng-click="$ctrl.onModeChange($ctrl.FORM_CONTROLLER_STATES.ADD)" ng-class="$ctrl.viewState === $ctrl.FORM_CONTROLLER_STATES.ADD ? 'selected' : ''">Form Library</a>
			</li>
			<li class="library-list-item">
				<a href="javascript:" ng-click="$ctrl.onModeChange($ctrl.FORM_CONTROLLER_STATES.COMPLETED)" ng-class="$ctrl.viewState === $ctrl.FORM_CONTROLLER_STATES.COMPLETED ? 'selected' : ''">Completed Forms</a>
			</li>
			<li class="library-list-item">
				<a href="javascript:" ng-click="$ctrl.onModeChange($ctrl.FORM_CONTROLLER_STATES.REVISION)" ng-class="$ctrl.viewState === $ctrl.FORM_CONTROLLER_STATES.REVISION ? 'selected' : ''">Form Revisions</a>
			</li>
			<li class="library-list-item">
				<a href="javascript:" ng-click="$ctrl.onModeChange($ctrl.FORM_CONTROLLER_STATES.DELETED)" ng-class="$ctrl.viewState === $ctrl.FORM_CONTROLLER_STATES.DELETED ? 'selected' : ''">Deleted Forms</a>
			</li>
		</ul>
	</nav>
</div>