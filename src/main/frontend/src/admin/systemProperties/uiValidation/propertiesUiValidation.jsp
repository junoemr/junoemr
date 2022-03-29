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

<div class="admin-system-properties-body" id="system-properties-ui-validation">
	<h3 class="title">UI Validation</h3>
	<div class="content">
		<div class="controls m-t-16">
			<h4>Controls</h4>
			<div class="labels flex-col w-512">
				<div class="flex-row m-8">
					<juno-input class="d-inline-block w-256"
								label="Juno Input"
								ng-model=$ctrl.textModel>
					</juno-input>
					<div class="d-flex align-items-center m-l-48">
						{{$ctrl.textModel}}
					</div>
				</div>
				<div class="flex-row m-8">
					<juno-select class="d-inline-block w-256"
								 label="Juno Select"
								 options="$ctrl.selectOptions"
								 ng-model="$ctrl.selectModel">
					</juno-select>
					<div class="d-flex align-items-center m-l-48">
						{{$ctrl.selectModel}}
					</div>
				</div>
				<div class="flex-row m-8">
					<juno-button class="d-inline-block w-256 flex-item-no-grow"
								 label="Juno Button"
								 click="$ctrl.onClick()"
					>
						Click
					</juno-button>
				</div>
				<div class="flex-row m-8">
					<juno-toggle class="d-inline-block w-256"
								 label="Juno Toggle"
								 ng-model="$ctrl.toggleModel">
					</juno-toggle>
					<div class="d-flex align-items-center m-l-48">
						{{$ctrl.toggleModel}}
					</div>
				</div>
			</div>
		</div>
		<div class="panels m-t-16"">
			<h4>Panels</h4>
			<panel>
				<panel-header>
					<h6>Panel Header</h6>
				</panel-header>
				<panel-body>
					Panel Body
				</panel-body>
			</panel>

			<panel no-header="true">
				<panel-body>
					Headless panel
				</panel-body>
			</panel>
		</div>
		<div class="icons m-t-16">
			<h4>Icons</h4>
			<div class="all-icons">
				<div ng-repeat="icon in $ctrl.iconList"
					 class="item d-flex align-items-center">
					<i class="{{icon}} d-inline-block"></i>
					<span class="d-inline-block">{{ icon }}</span>
				</div>
			</div>
		</div>
	</div>
</div>