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
import CareTrackerItemData from "../../../../../lib/careTracker/model/CareTrackerItemData";
import {AlertSeverityType} from "../../../../../lib/careTracker/model/AlertSeverityType";
import {IAngularEvent} from "angular";
import {JUNO_RADIO_STYLE} from "../../../../../common/components/junoRadioSelect/junoRadioSelectConstants";

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
			'$scope',
			'$uibModal',
			'careTrackerApiService',
			function (
				$scope,
				$uibModal,
				careTrackerApiService,
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
				ctrl.JUNO_RADIO_STYLE = JUNO_RADIO_STYLE;

				ctrl.validationAlerts = [];
				ctrl.isLoading = false;
				ctrl.addToNoteOnSave = false;

				ctrl.dataTrueValue = "Yes";
				ctrl.dataFalseValue = "No";
				ctrl.inputRegexRestriction = null;

				ctrl.$onInit = (): void =>
				{
					// restrict input to numeric inputs, with decimals and negatives
					if(ctrl.model.valueTypeIsNumeric())
					{
						ctrl.inputRegexRestriction = /^-?\d*\.?\d*$/;
					}
					else if(ctrl.model.valueTypeIsBloodPressure())
					{
						// regex can't be super strict as it is applied during each input change
						ctrl.inputRegexRestriction = /^\d*\/?\d*$/;
					}

					ctrl.clearNewEntry();
				}

				ctrl.clearNewEntry = (): void =>
				{
					ctrl.newEntry = new CareTrackerItemData();
					ctrl.newEntry.observationDateTime = moment();
					ctrl.preventionGivenCheck = false;
					ctrl.booleanCheckYes = false;
					ctrl.booleanCheckNo = false;
					ctrl.dateValue = null;
					ctrl.addToNoteOnSave = false;
				}

				ctrl.getAlertClass = (severityLevel: AlertSeverityType): string =>
				{
					if(severityLevel === AlertSeverityType.Recommendation)
					{
						return "alert-info";
					}
					if(severityLevel === AlertSeverityType.Warning)
					{
						return "alert-warning";
					}
					if(severityLevel === AlertSeverityType.Danger)
					{
						return "alert-danger";
					}
				}

				ctrl.showData = (data): boolean =>
				{
					if(ctrl.filterDateBefore || ctrl.filterDateAfter)
					{
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
					return ctrl.model && ctrl.model.itemTypeIsMeasurement() &&
						(ctrl.model.valueTypeIsFreeText() || ctrl.model.valueTypeIsNumeric() || ctrl.model.valueTypeIsBloodPressure());
				}
				ctrl.showValueDateInput = (): boolean =>
				{
					return ctrl.model && ctrl.model.itemTypeIsMeasurement() && ctrl.model.valueTypeIsDate();
				}

				ctrl.isGraphable = (): boolean =>
				{
					return ctrl.model.valueTypeIsNumeric() || ctrl.model.valueTypeIsBloodPressure();
				}

				ctrl.onBooleanValueYes = (value: boolean): void =>
				{
					ctrl.booleanCheckNo = false;
					ctrl.newEntry.value = value ? ctrl.dataTrueValue : null;
				}

				ctrl.onBooleanValueNo = (value: boolean): void =>
				{
					ctrl.booleanCheckYes = false;
					ctrl.newEntry.value = value ? ctrl.dataFalseValue : null;
				}

				ctrl.onDateChangeValue = (value: Moment): void =>
				{
					ctrl.newEntry.value = value.isValid() ? Juno.Common.Util.formatMomentDate(value) : null;
				}

				ctrl.canSubmitItem = (): boolean =>
				{
					return (!ctrl.isLoading && ctrl.model
						&& ((ctrl.model.itemTypeIsPrevention() && ctrl.preventionGivenCheck)
							|| (ctrl.model.itemTypeIsMeasurement() && !Juno.Common.Util.isBlank(ctrl.newEntry.value)))
					);
				}

				ctrl.submitNewItemData = async (): Promise<CareTrackerItemData> =>
				{
					ctrl.isLoading = true;
					ctrl.validationAlerts = [];
					let newDataElement: CareTrackerItemData = null;
					try
					{
						newDataElement = await careTrackerApiService.addCareTrackerItemData(ctrl.demographicId, ctrl.trackerId, ctrl.model.id, ctrl.newEntry);
						ctrl.model = await careTrackerApiService.getDemographicCareTrackerItem(ctrl.demographicId, ctrl.trackerId, ctrl.model.id);
						ctrl.model.sortDataByObservationDate(false);
						ctrl.clearNewEntry();
					}
					catch (errorObject)
					{
						console.error(errorObject);
						const errorMessage = (errorObject) ?  errorObject.data.error.message : "Unknown Error";
						ctrl.validationAlerts.push({
							message: errorMessage,
						});
					}
					finally
					{
						ctrl.isLoading = false;
					}
					return newDataElement;
				}

				ctrl.saveAndAddToNote = async (): Promise<void> =>
				{
					if(ctrl.canSubmitItem())
					{
						const selected: boolean = ctrl.addToNoteOnSave;
						const newDataModel: CareTrackerItemData = await ctrl.submitNewItemData();

						if(newDataModel && selected)
						{
							const message = ctrl.model.toString() + ": " + newDataModel.toString();
							$scope.$emit("appendToCurrentNote", message);
						}
					}
				}

				$scope.$on("careTracker.savePendingData", async (event: IAngularEvent, callback: Function): Promise<void> =>
				{
					let newDataModel: CareTrackerItemData = null;
					const selected: boolean = ctrl.addToNoteOnSave;
					if(ctrl.canSubmitItem())
					{
						newDataModel = await ctrl.submitNewItemData();
					}
					if(callback)
					{
						callback(ctrl.model, selected, newDataModel);
					}
				})

				ctrl.getInputLabel = (): string =>
				{
					let label = ctrl.model.valueLabel;
					if(!label)
					{
						if(ctrl.model.itemTypeIsPrevention())
						{
							label = "Given";
						}
						else if(ctrl.model.valueTypeIsBoolean())
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

				ctrl.onShowDataGraph = () =>
				{
					$uibModal.open(
						{
							component: 'careTrackerItemGraphModal',
							backdrop: 'static',
							windowClass: "juno-simple-modal-window",
							resolve: {
								style: ctrl.componentStyle,
								model: ctrl.model,
							}
						}
					).result;
				}
			}]
	});