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
		},
		controller: [
			function (
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.validationAlerts = [];

				ctrl.$onInit = async () =>
				{
					ctrl.newEntry = {
						value: "",
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

				ctrl.validate = () =>
				{
					ctrl.validationAlerts = [];
					if(ctrl.model.validationRules)
					{
						ctrl.model.validationRules.forEach((rule) =>
						{
							if (rule.validationRegex && !(new RegExp(rule.validationRegex).test(ctrl.newEntry.value)))
							{
								ctrl.validationAlerts.push(rule);
							}
						});
					}
					console.info(ctrl.validationAlerts);
					return ctrl.validationAlerts.length === 0;
				}

				ctrl.validateAndSubmit = () =>
				{
					if(ctrl.validate())
					{
						console.info("TODO submit");
						//submit
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