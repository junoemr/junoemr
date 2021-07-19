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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import DsRuleModel from "../../../lib/decisionSupport/model/DsRuleModel";
import {CareTrackerItemValueType} from "../../../lib/careTracker/model/CareTrackerItemValueType";

angular.module('CareTracker').component('careTrackerEditItem',
	{
		templateUrl: 'src/careTracker/components/careTrackerEditItem/careTrackerEditItem.jsp',
		bindings: {
			componentStyle: "<?",
			model: "<",
			onDelete: "&?",
		},
		controller: [
			'$uibModal',
			function ($uibModal)
			{
				const ctrl = this;

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.valueTypeOptions = [
					{label: "Text", value: CareTrackerItemValueType.STRING},
					{label: "Numeric", value: CareTrackerItemValueType.NUMERIC},
					{label: "Checkbox", value: CareTrackerItemValueType.BOOLEAN},
					{label: "Date", value: CareTrackerItemValueType.DATE},
				];

				ctrl.isLoading = true;
				ctrl.$onInit = (): void =>
				{
					ctrl.isLoading = false;
				}

				ctrl.addNewRule = (): void =>
				{
					$uibModal.open(
						{
							component: 'dsRuleEditModal',
							backdrop: 'static',
							windowClass: "juno-modal",
							resolve: {
								style: ctrl.componentStyle,
							}
						}
					).result.then((response: DsRuleModel) =>
					{
						// don't add duplicates
						if(!response.id || ctrl.model.rules.filter((rule) => rule.id === response.id).length === 0)
						{
							ctrl.model.rules.push(response);
						}
					}).catch((reason) =>
					{
						console.warn(reason);
						// do nothing on cancel
					});
				}

				ctrl.removeRule = async (rule): Promise<void> =>
				{
					// @ts-ignore
					let confirmation = await Juno.Common.Util.confirmationDialog($uibModal,
						"Remove decision support rule",
						"Are you sure you want to remove this rule from the flowsheet item?",
						ctrl.componentStyle);

					if(confirmation)
					{
						ctrl.model.rules = ctrl.model.rules.filter((entry) => entry.id !== rule.id);
					}
				}

				ctrl.onClose = () =>
				{
					if(ctrl.onDelete)
					{
						ctrl.onDelete({item: ctrl.model});
					}
				}
			}]
	});
