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

import {JUNO_STYLE, LABEL_POSITION, labelStyle} from "../junoComponentConstants";

angular.module('Common.Components').component('junoInput', {
	templateUrl: 'src/common/components/junoInput/junoInput.jsp',
	bindings: {
		ngModel: "=",
		ngChange: "&?",
		type: "<?",
		label: "@?",
		labelPosition: "<?",
		uppercase: "<?",
		componentStyle: "<?",
	},
	controller: [ "$scope", function ($scope) {
		let ctrl = this;

		$scope.LABEL_POSITION = LABEL_POSITION;

		ctrl.$onInit = () =>
		{
			ctrl.type = ctrl.type || 'text';
			ctrl.uppercase = ctrl.uppercase || false;
			ctrl.labelPosition = ctrl.labelPosition || LABEL_POSITION.LEFT;
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
		};

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle];
		}

		ctrl.inputClasses = () =>
		{
			return {
				uppercase: Boolean(ctrl.uppercase),
			};
		}

		ctrl.labelClasses = () =>
		{
			return [ctrl.labelPosition];
		};

		ctrl.onChange = () =>
		{
			if (ctrl.ngChange)
			{
				ctrl.ngChange({});
			}
		}
	}]
});