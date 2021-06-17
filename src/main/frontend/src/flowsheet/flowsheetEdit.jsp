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

<div class="flowsheet-edit">
	<h1>Edit Flowsheet</h1>

	<div>
		<flowsheet-item-group ng-repeat="itemGroup in $ctrl.itemGroups" model="itemGroup">
			<div ng-repeat="item in itemGroup.flowsheetItems">
				<flowsheet-edit-item model="item">
				</flowsheet-edit-item>
			</div>
			<div class="flex-row">
				<div class="add-button-wrapper">
					<juno-button component-style="$ctrl.componentStyle"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
					             disabled="$ctrl.isLoading"
					             click="$ctrl.onAddNewItem($ctrl.ItemType.MEASUREMENT)">
						<i class="icon icon-add"></i>
						<span>Add Measurement Item</span>
					</juno-button>
				</div>
				<div class="add-button-wrapper">
					<juno-button component-style="$ctrl.componentStyle"
					             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
					             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
					             disabled="$ctrl.isLoading"
					             click="$ctrl.onAddNewItem($ctrl.ItemType.PREVENTION)">
						<i class="icon icon-add"></i>
						<span>Add Prevention Item</span>
					</juno-button>
				</div>
			</div>
		</flowsheet-item-group>
	</div>
	<div class="add-button-wrapper">
		<juno-button component-style="$ctrl.componentStyle"
		             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
		             disabled="$ctrl.isLoading"
		             click="$ctrl.onAddNewGroup()">
			<i class="icon icon-add"></i>
			<span>Add Group</span>
		</juno-button>
	</div>
</div>