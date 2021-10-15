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
<juno-modal class="security-role-set-modal" component-style="$ctrl.resolve.style">

	<modal-ctl-buttons>
		<i class="icon icon-modal-ctl icon-close" ng-click="$ctrl.onCancel()"></i>
	</modal-ctl-buttons>

	<modal-title>
		<h3>Manage Demographic Set Security</h3>
	</modal-title>

	<modal-body>
		<div class="overflow-auto height-100 flex-column">
			<div class="provider-selection-container">
				<juno-select ng-model="$ctrl.selectedProvider"
				             options="$ctrl.providerOptions"
				             on-change="$ctrl.onProviderSelected(value)">
				</juno-select>
			</div>
			<div class="set-selection-container flex-grow">
				<juno-list-item-selector ng-if="$ctrl.selectedProvider"
				                         label-options="Demographic Sets"
				                         label-selected="Black Listed"
				                         ng-model="$ctrl.selectedSetsList"
				                         on-change="$ctrl.onBlacklistChange(item, model)">
				</juno-list-item-selector>
				<p ng-if="!$ctrl.selectedProvider">Please select a provider</p>
			</div>
		</div>
	</modal-body>
	<modal-footer>
		<div class="row footer-wrapper">
			<div class="col-md-6">
				<div class="button-group-wrapper">
					<div class="button-wrapper">
						<juno-button component-style="$ctrl.resolve.style"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						             click="$ctrl.onCancel()">
							Done
						</juno-button>
					</div>
				</div>
			</div>
		</div>
	</modal-footer>
</juno-modal>
