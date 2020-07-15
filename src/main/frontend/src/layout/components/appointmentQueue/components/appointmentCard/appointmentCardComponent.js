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


import {
	JUNO_BACKGROUND_STYLE,
	JUNO_BUTTON_COLOR,
	JUNO_STYLE
} from "../../../../../common/components/junoComponentConstants";

angular.module('Layout.Components').component('appointmentCard', {
	templateUrl: 'src/layout/components/appointmentQueue/components/appointmentCard/appointmentCard.jsp',
	bindings: {
		ngModel: "=?",
		componentStyle: "<?",
	},
	controller: ["$scope", function ($scope)
	{
		let ctrl = this;

		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT
		}

		ctrl.getComponentClasses = () =>
		{
			let classes = [ctrl.componentStyle];
			if (!ctrl.ngModel)
			{
				classes.push("zero-state");
			}
			else
			{
				classes.push(ctrl.componentStyle + JUNO_BACKGROUND_STYLE.PRIMARY);
				classes.push("active");
			}
			return classes;
		}
	}]
});