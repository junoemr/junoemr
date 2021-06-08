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


const {JUNO_BUTTON_COLOR_PATTERN} = require("../../../common/components/junoComponentConstants");
const {JUNO_BUTTON_COLOR} = require("../../../common/components/junoComponentConstants");
const {LABEL_POSITION} = require("../../../common/components/junoComponentConstants");
const {SecurityPermissions} = require("../../../common/security/securityConstants");

angular.module('Record.Flowsheet').component('flowsheetItem',
	{
		templateUrl: 'src/record/flowsheet/components/flowsheetItem.jsp',
		bindings: {
			componentStyle: "<?",
			model: "<",
			demographicId: "<",
			flowsheetId: "<",
		},
		controller: [
			'flowsheetApiService',
			function (
				flowsheetApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.validationAlerts = [];
				ctrl.isLoading = false;

				ctrl.$onInit = async () =>
				{
					ctrl.newEntry = {
						value: "",
						observationDateTime: moment(),
					};
				}

				ctrl.valueIsBoolean = () =>
				{
					return ctrl.model.valueType === "BOOLEAN";
				}

				ctrl.valueIsNumeric = () =>
				{
					return ctrl.model.valueType === "NUMBER";
				}

				ctrl.isTypeMeasurement = () =>
				{
					return ctrl.model.type === "MEASUREMENT";
				}

				ctrl.isTypePrevention = () =>
				{
					return ctrl.model.type === "PREVENTION";
				}

				ctrl.getAlertClass = (strength) =>
				{
					if(strength === "RECOMMENDATION")
					{
						return "alert-info";
					}
					if(strength === "WARNING")
					{
						return "alert-warning";
					}
					if(strength === "DANGER")
					{
						return "alert-danger";
					}
				}

				ctrl.canSubmitItem = () =>
				{
					return (!ctrl.isLoading && (
							ctrl.isTypePrevention() ||
							ctrl.isTypeMeasurement() && !Juno.Common.Util.isBlank(ctrl.newEntry.value))
					);
				}

				ctrl.submitNewItemData = async () =>
				{
					ctrl.isLoading = true;
					ctrl.validationAlerts = [];
					try
					{
						let newDataElement = await flowsheetApiService.addFlowsheetItemData(ctrl.demographicId, ctrl.flowsheetId, ctrl.model.id, ctrl.newEntry);
						ctrl.model.data.push(newDataElement);
					}
					catch (errorObject)
					{
						return ctrl.validationAlerts.push({
							message: errorObject.data.error.message,
						});
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.getInputLabel = () =>
				{
					let label = ctrl.model.valueLabel;
					if(!label)
					{
						if(ctrl.valueIsBoolean())
						{
							label = "Complete";
						}
						else
						{
							label = "Add New";
						}
					}
					return label;
				}
			}]
	});