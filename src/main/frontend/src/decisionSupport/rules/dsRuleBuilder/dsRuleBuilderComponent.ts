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
					{label: "Months Since is more than", value: ConditionType.MONTHS_SINCE_GT},
					{label: "Months Since is less than", value: ConditionType.MONTHS_SINCE_LT},
					{label: "Never Given", value: ConditionType.NEVER_GIVEN},
					{label: "Last record is exactly", value: ConditionType.VALUE_EQ},
					{label: "Last record is not", value: ConditionType.VALUE_NE},
					{label: "Last record is more than", value: ConditionType.VALUE_GT},
					{label: "Last record is less than", value: ConditionType.VALUE_LT},
					{label: "Patient age (years) more than", value: ConditionType.PATIENT_AGE_GT},
					{label: "Patient age (years) less than", value: ConditionType.PATIENT_AGE_LT},
					{label: "Patient gender matches", value: ConditionType.PATIENT_GENDER_EQ},
					{label: "Patient gender does not match", value: ConditionType.PATIENT_GENDER_NE},
				];

				ctrl.consequenceTypeOptions = [
					{label: "Display Alert", value: ConsequenceType.ALERT},
					{label: "Hide Item", value: ConsequenceType.HIDDEN},
				];

				ctrl.consequenceSeverityOptions = [
					{label: "Recommendation", value: ConsequenceSeverity.RECOMMENDATION},
					{label: "Warning", value: ConsequenceSeverity.WARNING},
					{label: "Critical", value: ConsequenceSeverity.DANGER},
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
					ConditionType.MONTHS_SINCE_GT,
					ConditionType.MONTHS_SINCE_LT,
					ConditionType.VALUE_LT,
					ConditionType.VALUE_GT,
					ConditionType.VALUE_EQ,
					ConditionType.VALUE_NE,
					ConditionType.PATIENT_AGE_GT,
					ConditionType.PATIENT_AGE_LT,
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
					return (condition.type === ConditionType.PATIENT_GENDER_EQ
						|| condition.type === ConditionType.PATIENT_GENDER_NE
					);
				}
				ctrl.getConditionValueInputLabel = (condition: DsRuleConditionModel): string =>
				{
					switch (condition.type)
					{
						case ConditionType.MONTHS_SINCE_LT:
						case ConditionType.MONTHS_SINCE_GT: return "";
					}
					return "value";
				}
				ctrl.getConditionSelectOptions = (condition: DsRuleConditionModel):Array<object> =>
				{
					switch (condition.type)
					{
						case ConditionType.PATIENT_GENDER_EQ:
						case ConditionType.PATIENT_GENDER_NE:
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
					return (consequence.type === ConsequenceType.ALERT);
				}
				ctrl.getConsequenceValueInputLabel = (consequence: DsRuleConsequenceModel): string =>
				{
					switch (consequence.type)
					{
						case ConsequenceType.ALERT: return "with message";
					}
					return "value";
				}
				ctrl.showConsequenceSeveritySelect = (consequence: DsRuleConsequenceModel): boolean =>
				{
					return (consequence.type === ConsequenceType.ALERT);
				}
				ctrl.removeConsequence = (consequence: DsRuleConsequenceModel): void =>
				{
					ctrl.model.consequences = ctrl.model.consequences.filter((entry) => entry != consequence);
				}
			}]
	});