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

<div class="care-tracker-edit">
	<div class="flex-row justify-content-between align-items-center">
		<h1>Edit Care Tracker</h1>

		<div class="flex-row">
			<div class="save-button-wrapper">
				<juno-button component-style="$ctrl.componentStyle"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             disabled="$ctrl.isLoading"
				             click="$ctrl.onCancel()">
					Close
				</juno-button>
			</div>
			<div class="save-button-wrapper">
				<juno-button component-style="$ctrl.componentStyle"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.SUCCESS"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
				             disabled="$ctrl.isLoading || !$ctrl.canSave()"
				             title="{{$ctrl.saveButtonTooltip()}}"
				             click="$ctrl.onSave()">
					Save
				</juno-button>
			</div>
		</div>
	</div>
	<div ng-if="$ctrl.careTracker.systemManaged" class="alert alert-info">
		<i class="icon icon-info-circle"></i>
		<span>This is a system managed flowsheet and cannot be edited</span>
	</div>
	<div class="flex-row flex-grow">
		<div class="flex-column width-25 row-padding-r">
			<juno-input label="Care Tracker Name"
			            label-position="$ctrl.LABEL_POSITION.TOP"
			            disabled="$ctrl.isLoading || $ctrl.readOnly"
			            ng-model="$ctrl.careTracker.name">
			</juno-input>
		</div>
		<div class="flex-column flex-grow row-padding-l">
			<juno-input label="Description"
			            label-position="$ctrl.LABEL_POSITION.TOP"
			            disabled="$ctrl.isLoading || $ctrl.readOnly"
			            ng-model="$ctrl.careTracker.description">
			</juno-input>
		</div>
	</div>
	<div class="flex-column trigger-code-list">
		<span class="row-padding">Trigger Codes</span>
		<div class="flex-row flex-grow">
			<care-tracker-trigger ng-repeat="triggerCode in $ctrl.careTracker.triggerCodes"
			                      model="triggerCode"
			                      class="row-padding"
			                      disabled="$ctrl.isLoading || $ctrl.readOnly"
			                      on-delete="$ctrl.onDeleteTriggerCode(triggerCode)">
			</care-tracker-trigger>
			<div class="icon-only-button-wrapper">
				<juno-button component-style="$ctrl.componentStyle"
				             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
				             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
				             disabled="$ctrl.isLoading || $ctrl.readOnly"
				             click="$ctrl.onAddIcd9TriggerCode()">
					<i class="icon icon-add"></i>
				</juno-button>
			</div>
		</div>
	</div>

	<div class="flex-column">
		<care-tracker-item-group ng-repeat="itemGroup in $ctrl.careTracker.careTrackerItemGroups"
		                         model="itemGroup"
		                         show-delete="true"
		                         disabled="$ctrl.readOnly"
		                         on-delete="$ctrl.onRemoveGroup(group)">
			<div class="flex-column flex-grow">
				<div class="flex-row flex-grow justify-content-between">
					<div class="flex flex-grow">
						<juno-input label="Group description"
						            disabled="$ctrl.isLoading || $ctrl.readOnly"
						            label-position="$ctrl.LABEL_POSITION.TOP"
						            ng-model="itemGroup.description">
						</juno-input>
					</div>
					<div class="icon-only-button-wrapper">
						<juno-button component-style="$ctrl.componentStyle"
						             label="Rename"
						             label-position="$ctrl.LABEL_POSITION.TOP"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						             disabled="$ctrl.isLoading || $ctrl.readOnly"
						             click="$ctrl.onRenameGroup(itemGroup)">
							<i class="icon icon-gear"></i>
						</juno-button>
					</div>
				</div>
				<div ng-repeat="item in itemGroup.careTrackerItems" class="flex-row flex-grow align-items-center">
					<care-tracker-edit-item model="item"
					                        disabled="$ctrl.readOnly"
					                        on-delete="$ctrl.onRemoveItem(item, itemGroup)"
					                        class="flex-grow">
					</care-tracker-edit-item>
				</div>
				<div class="flex-row flex-grow">
					<div class="add-button-wrapper">
						<juno-button component-style="$ctrl.componentStyle"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						             disabled="$ctrl.isLoading || $ctrl.readOnly"
						             click="$ctrl.onAddNewMeasurementItem(itemGroup)">
							<i class="icon icon-add"></i>
							<span>Add Measurement Item</span>
						</juno-button>
					</div>
					<div class="add-button-wrapper">
						<juno-button component-style="$ctrl.componentStyle"
						             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
						             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
						             disabled="$ctrl.isLoading || $ctrl.readOnly"
						             click="$ctrl.onAddNewPreventionItem(itemGroup)">
							<i class="icon icon-add"></i>
							<span>Add Prevention Item</span>
						</juno-button>
					</div>
				</div>
			</div>
		</care-tracker-item-group>
	</div>
	<div class="add-button-wrapper">
		<juno-button component-style="$ctrl.componentStyle"
		             button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
		             button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.DEFAULT"
		             disabled="$ctrl.isLoading || $ctrl.readOnly"
		             click="$ctrl.onAddNewGroup()">
			<i class="icon icon-add"></i>
			<span>Add Group</span>
		</juno-button>
	</div>
</div>