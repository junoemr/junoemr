/*
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

import {JUNO_STYLE, LABEL_POSITION} from "../junoComponentConstants";

angular.module('Common.Components').component('junoToggle', {
    templateUrl: 'src/common/components/junoToggle/junoToggle.jsp',
    bindings: {
        id: "<",
        ngModel: "=",
        label: "@?",
		labelPosition: "<?",
        change: "&?",
        disabled: "<?",
        componentStyle: "<?",
        toggleTrueValue: "<?",
        toggleFalseValue: "<?",
        showValueLabels: "<?",
        round: "<?"
    },
    controller: [ "$scope", function ($scope) {
        let ctrl = this;

        ctrl.$onInit = () =>
        {
            ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
            ctrl.toggleTrueValue = ctrl.toggleTrueValue || true;
            ctrl.toggleFalseValue = ctrl.toggleFalseValue || false;
            ctrl.showValueLabels = ctrl.showValueLabels || false;

			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
        };

        ctrl.componentClasses = () =>
        {
            return [ctrl.componentStyle];
        };

        ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition, "label-style"];
		}

        /**
         * Change event handler.  State of the checkbox is accessible as the checked parameter on your callback function
         */
        ctrl.onChange = () =>
        {
            if (ctrl.change && !ctrl.disabled)
            {
                ctrl.change({
                    checked: ctrl.ngModel,
                    value: ctrl.ngModel ? ctrl.toggleTrueValue : ctrl.toggleFalseValue,
                });
            }
        }
    }]
});