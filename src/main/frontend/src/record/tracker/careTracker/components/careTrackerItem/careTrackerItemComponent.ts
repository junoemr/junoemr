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

import {SecurityPermissions} from "../../../../../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../../../common/components/junoComponentConstants";
import moment, {Moment} from "moment";
import CareTrackerItemDataModel from "../../../../../lib/careTracker/model/CareTrackerItemDataModel";
import {AlertSeverityType} from "../../../../../lib/careTracker/model/AlertSeverityType";

angular.module('Record.Tracker.CareTracker').component('careTrackerItem',
	{
		templateUrl: 'src/record/tracker/careTracker/components/careTrackerItem/careTrackerItem.jsp',
		bindings: {
			componentStyle: "<?",
			model: "<",
			demographicId: "<",
			trackerId: "<",
			filterDateBefore: "<?",
			filterDateAfter: "<?",
			filterMaxEntries: "<?",
		},
		controller: [
			'careTrackerApiService',
			function (
				careTrackerApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.validationAlerts = [];
				ctrl.isLoading = false;

				ctrl.dataTrueValue = "Yes";
				ctrl.dataFalseValue = "No";

				ctrl.$onInit = (): void =>
				{
					ctrl.clearNewEntry();
				}

				ctrl.clearNewEntry = (): void =>
				{
					ctrl.newEntry = new CareTrackerItemDataModel();
					ctrl.newEntry.observationDateTime = moment();
					ctrl.checkboxValue = false;
					ctrl.dateValue = null;
				}

				ctrl.getAlertClass = (severityLevel: AlertSeverityType): string =>
				{
					if(severityLevel === AlertSeverityType.RECOMMENDATION)
					{
						return "alert-info";
					}
					if(severityLevel === AlertSeverityType.WARNING)
					{
						return "alert-warning";
					}
					if(severityLevel === AlertSeverityType.DANGER)
					{
						return "alert-danger";
					}
				}

				ctrl.showData = (data): boolean =>
				{
					if(ctrl.filterDateBefore || ctrl.filterDateAfter)
					{
						// @ts-ignore
						const observationDate = moment.utc(data.observationDateTime);
						if (!observationDate.isValid() ||
							(ctrl.filterDateAfter && ctrl.filterDateAfter.isValid() && !observationDate.isSameOrAfter(ctrl.filterDateAfter, "day")) ||
							(ctrl.filterDateBefore && ctrl.filterDateBefore.isValid() && !observationDate.isBefore(ctrl.filterDateBefore, "day")))
						{
							return false;
						}
					}
					if(ctrl.filterMaxEntries && (ctrl.model.data.findIndex((entry) => entry.id === data.id) > (ctrl.filterMaxEntries - 1)))
					{
						return false;
					}
					return true;
				}

				ctrl.showValueBooleanInput = (): boolean =>
				{
					return ctrl.model && ctrl.model.itemTypeIsMeasurement() && ctrl.model.valueTypeIsBoolean();
				}
				ctrl.showValueTextInput = (): boolean =>
				{
					return ctrl.model && ctrl.model.itemTypeIsMeasurement() && (ctrl.model.valueTypeIsFreeText() || ctrl.model.valueTypeIsNumeric());
				}
				ctrl.showValueDateInput = (): boolean =>
				{
					return ctrl.model && ctrl.model.itemTypeIsMeasurement() && ctrl.model.valueTypeIsDate();
				}

				ctrl.onBooleanValueChange = (value: boolean): void =>
				{
					ctrl.newEntry.value = value ? ctrl.dataTrueValue : ctrl.dataFalseValue;
				}

				ctrl.onDateChangeValue = (value: Moment): void =>
				{
					ctrl.newEntry.value = value.isValid() ? Juno.Common.Util.formatMomentDate(value) : null;
				}

				ctrl.canSubmitItem = (): boolean =>
				{
					return (!ctrl.isLoading && ctrl.model
						&& (ctrl.model.itemTypeIsPrevention() || (ctrl.model.itemTypeIsMeasurement() && !Juno.Common.Util.isBlank(ctrl.newEntry.value)))
					);
				}

				ctrl.submitNewItemData = async (): Promise<void> =>
				{
					ctrl.isLoading = true;
					ctrl.validationAlerts = [];
					try
					{
						let newDataElement = await careTrackerApiService.addCareTrackerItemData(ctrl.demographicId, ctrl.trackerId, ctrl.model.id, ctrl.newEntry);
						ctrl.model.data.push(newDataElement);
						ctrl.clearNewEntry();
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

				ctrl.getInputLabel = (): string =>
				{
					let label = ctrl.model.valueLabel;
					if(!label)
					{
						if(ctrl.model.valueTypeIsBoolean())
						{
							label = "Complete";
						}
						else if(ctrl.model.valueTypeIsDate())
						{
							label = "Date";
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