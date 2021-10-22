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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../common/components/junoComponentConstants";
import DsRuleConditionModel from "../../../lib/decisionSupport/model/DsRuleConditionModel";
import DsRuleConsequenceModel from "../../../lib/decisionSupport/model/DsRuleConsequenceModel";
import {Sex, sexToHuman} from "../../../lib/demographic/model/Sex";
import {ConditionType} from "../../../lib/decisionSupport/model/DsConditionType";
import {ConsequenceType} from "../../../lib/decisionSupport/model/DsConsequenceType";
import {ConsequenceSeverity} from "../../../lib/decisionSupport/model/DsConsequenceSeverity";

angular.module('DecisionSupport').component('dsRuleBuilder',
	{
		templateUrl: 'src/decisionSupport/rules/dsRuleBuilder/dsRuleBuilder.jsp',
		bindings: {
			componentStyle: "<?",
			model: "<",
			disabled: "<?",
		},
		controller: [
			function ()
			{
				const ctrl = this;

				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.conditionTypeOptions = [
					{label: "Months Since is more than", value: ConditionType.MonthsSinceGt},
					{label: "Months Since is less than", value: ConditionType.MonthsSinceLt},
					{label: "Never Given", value: ConditionType.NeverGiven},
					{label: "Last record is exactly", value: ConditionType.ValueEq},
					{label: "Last record is not", value: ConditionType.ValueNe},
					{label: "Last record is more than", value: ConditionType.ValueGt},
					{label: "Last record is less than", value: ConditionType.ValueLt},
					{label: "Patient age (years) more than", value: ConditionType.PatientAgeGt},
					{label: "Patient age (years) less than", value: ConditionType.PatientAgeLt},
					{label: "Patient gender matches", value: ConditionType.PatientGenderEq},
					{label: "Patient gender does not match", value: ConditionType.PatientGenderNe},
				];

				ctrl.consequenceTypeOptions = [
					{label: "Display Alert", value: ConsequenceType.Alert},
					{label: "Hide Item", value: ConsequenceType.Hidden},
				];

				ctrl.consequenceSeverityOptions = [
					{label: "Recommendation", value: ConsequenceSeverity.Recommendation},
					{label: "Warning", value: ConsequenceSeverity.Warning},
					{label: "Critical", value: ConsequenceSeverity.Danger},
				];

				ctrl.conditionValueOptions = {
					gender: [
						{label: sexToHuman(Sex.Male), value: Sex.Male},
						{label: sexToHuman(Sex.Female), value: Sex.Female},
						{label: sexToHuman(Sex.Transgender), value: Sex.Transgender},
						{label: sexToHuman(Sex.Other), value: Sex.Other},
						{label: sexToHuman(Sex.Undefined), value: Sex.Undefined},
					],
				};

				ctrl.conditionTypesWithTextInput = [
					ConditionType.MonthsSinceGt,
					ConditionType.MonthsSinceLt,
					ConditionType.ValueLt,
					ConditionType.ValueGt,
					ConditionType.ValueEq,
					ConditionType.ValueNe,
					ConditionType.PatientAgeGt,
					ConditionType.PatientAgeLt,
				] as ConditionType[];

				ctrl.$onInit = (): void =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
					ctrl.disabled = ctrl.disabled || false;
				}

				ctrl.onAddNewCondition = (): void =>
				{
					ctrl.model.conditions.push(new DsRuleConditionModel());
				}

				ctrl.onAddNewConsequence = (): void =>
				{
					ctrl.model.consequences.push(new DsRuleConsequenceModel());
				}

				ctrl.onConditionTypeChange = (condition: DsRuleConditionModel): void =>
				{
					condition.value = null;
				}

				ctrl.showConditionValueInput = (condition: DsRuleConditionModel): boolean =>
				{
					return ctrl.conditionTypesWithTextInput.includes(condition.type);
				}

				ctrl.showConditionValueSelect = (condition: DsRuleConditionModel): boolean =>
				{
					return (condition.type === ConditionType.PatientGenderEq
						|| condition.type === ConditionType.PatientGenderNe
					);
				}
				ctrl.getConditionValueInputLabel = (condition: DsRuleConditionModel): string =>
				{
					switch (condition.type)
					{
						case ConditionType.MonthsSinceLt:
						case ConditionType.MonthsSinceGt: return "";
					}
					return "value";
				}
				ctrl.getConditionSelectOptions = (condition: DsRuleConditionModel):Array<object> =>
				{
					switch (condition.type)
					{
						case ConditionType.PatientGenderEq:
						case ConditionType.PatientGenderNe:
						{
							return ctrl.conditionValueOptions.gender;
						}
					}
				}
				ctrl.removeCondition = (condition: DsRuleConditionModel): void =>
				{
					ctrl.model.conditions = ctrl.model.conditions.filter((entry) => entry != condition);
				}

				ctrl.showConsequenceValueInput = (consequence: DsRuleConsequenceModel): boolean =>
				{
					return (consequence.type === ConsequenceType.Alert);
				}
				ctrl.getConsequenceValueInputLabel = (consequence: DsRuleConsequenceModel): string =>
				{
					switch (consequence.type)
					{
						case ConsequenceType.Alert: return "with message";
					}
					return "value";
				}
				ctrl.showConsequenceSeveritySelect = (consequence: DsRuleConsequenceModel): boolean =>
				{
					return (consequence.type === ConsequenceType.Alert);
				}
				ctrl.removeConsequence = (consequence: DsRuleConsequenceModel): void =>
				{
					ctrl.model.consequences = ctrl.model.consequences.filter((entry) => entry != consequence);
				}
			}]
	});