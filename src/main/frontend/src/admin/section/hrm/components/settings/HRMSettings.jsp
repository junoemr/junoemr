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

<div id="hrm-settings" class="flex-col align-items-center justify-content-top h-100">
	<panel class="w-100">
		<panel-header class="flex-row justify-content-between align-items-center">
			<h6 class="d-inline-block">Account Settings</h6>
			<div class="flex-row">
				<juno-button ng-if="!$ctrl.isReadOnly"
							 class="flex-grow-0 w-256 m-r-8"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onSave()">
					Save
				</juno-button>
				<juno-button ng-if="$ctrl.isReadOnly"
							 class="flex-grow-0 w-256"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onEdit()"
							 disabled="!$ctrl.canEdit()">
					Edit Settings
				</juno-button>
				<juno-button ng-if="!$ctrl.isReadOnly"
							 class="flex-grow-0 w-256"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onCancel()"
							 disabled="$ctrl.isReadOnly">
					Cancel
				</juno-button>
			</div>
		</panel-header>
		<panel-body>
			<div class="flex-col">
				<juno-input label="User Name"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.userSettings.userName"
							disabled="$ctrl.isReadOnly"
				>
				</juno-input>
				<juno-input class="m-t-16"
							label="Mailbox Address"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.userSettings.mailBoxAddress"
							disabled="$ctrl.isReadOnly"
				>
				</juno-input>
				<juno-input class="m-t-16"
							label="Remote Path"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.userSettings.remotePath"
							disabled="$ctrl.isReadOnly"
				>
				</juno-input>
				<juno-input class="m-t-16"
							label="Port"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.userSettings.port"
							disabled="$ctrl.isReadOnly"
				>
				</juno-input>
		</panel-body>
	</panel>
	<panel class="w-100">
		<panel-header class="flex-row justify-content-between align-items-center">
			<h6 class="d-inline-block">Decryption Key</h6>
			<div class="flex-row">
				<juno-button ng-if="!$ctrl.isReadOnlyKey"
							 class="flex-grow-0 w-256 m-r-8"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onSaveKey()">
					Save
				</juno-button>
				<juno-button ng-if="$ctrl.isReadOnlyKey"
							 class="flex-grow-0 w-256"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.PRIMARY"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onEditKey()"
							 disabled="!$ctrl.canEdit()">
					Edit
				</juno-button>
				<juno-button ng-if="!$ctrl.isReadOnlyKey"
							 class="flex-grow-0 w-256"
							 button-color="$ctrl.JUNO_BUTTON_COLOR.DANGER"
							 button-color-pattern="$ctrl.JUNO_BUTTON_COLOR_PATTERN.FILL"
							 click="$ctrl.onCancelEditKey()"
							 disabled="$ctrl.isReadOnly">
					Cancel
				</juno-button>
			</div>
		</panel-header>
		<panel-body>
			<div class="flex-col">
				<juno-input ng-if="!$ctrl.isReadOnlyKey"
							class="m-t-16"
							label="New Decryption Key (32 chars)"
							label-position="$ctrl.LABEL_POSITION"
							ng-model="$ctrl.newDecryptionKey"
				>
			</div>
		</panel-body>
</div>