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

import {SecurityPermissions} from "../../common/security/securityConstants";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../common/components/junoComponentConstants";
import DsRuleModel from "../../lib/decisionSupport/model/DsRuleModel";

angular.module('DecisionSupport').component('dsRuleEditModal',
	{
		templateUrl: 'src/decisionSupport/rules/dsRuleEditModal.jsp',
		bindings: {
			modalInstance: "<",
			resolve: "<",
		},
		controller: [
			'$uibModal',
			'decisionSupportApiService',
			function ($uibModal,
			          decisionSupportApiService)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.ruleSelectionOptions = [];
				ctrl.selectedRuleId = null;
				ctrl.selectedRule = null;

				ctrl.newRule = null;
				ctrl.checkUseExisting = true;
				ctrl.checkCreateNew = false;

				ctrl.isLoading = true;

				ctrl.$onInit = async (): Promise<void> =>
				{
					try
					{
						const rules = await decisionSupportApiService.getRules();
						ctrl.ruleSelectionOptions = rules.map((rule) =>
						{
							return {
								label: rule.name,
								value: rule.id,
								data: rule,
							};
						});
						ctrl.newRule = new DsRuleModel();
					}
					finally
					{
						ctrl.isLoading = false;
					}
				}

				ctrl.toggleRuleSelectionMode = (value): void =>
				{
					ctrl.checkUseExisting = !ctrl.checkUseExisting;
					ctrl.checkCreateNew = !ctrl.checkCreateNew;
				}

				ctrl.selectionModeExisting = (): boolean =>
				{
					return ctrl.checkUseExisting;
				}

				ctrl.selectionModeNewRule = (): boolean =>
				{
					return ctrl.checkCreateNew;
				}

				ctrl.onRuleSelect = (value, option): void =>
				{
					ctrl.selectedRule = option.data;
				}

				ctrl.getRule = (): DsRuleModel =>
				{
					if(ctrl.selectionModeExisting())
					{
						return ctrl.selectedRule;
					}
					else
					{
						return ctrl.newRule;
					}
				}

				ctrl.onCancel = (): void =>
				{
					ctrl.modalInstance.dismiss("cancelled");
				}

				ctrl.canSubmit = (): boolean =>
				{
					const rule = ctrl.getRule();
					return Boolean(rule && rule.isValid());
				}

				ctrl.onSubmit = async (): Promise<void> =>
				{
					try
					{
						if (ctrl.selectionModeNewRule())
						{
							ctrl.newRule = await decisionSupportApiService.createRule(ctrl.newRule);
						}
						ctrl.modalInstance.close(ctrl.getRule());
					}
					catch (error)
					{
						// @ts-ignore
						console.error(error);
					}
				}

			}]
	});
