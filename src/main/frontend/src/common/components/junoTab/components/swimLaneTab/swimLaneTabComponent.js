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


import {JUNO_BUTTON_COLOR, JUNO_STYLE} from "../../../junoComponentConstants";

angular.module('Common.Components.JunoTab').component('swimLaneTab', {
	templateUrl: 'src/common/components/junoTab/components/swimLaneTab/swimLaneTab.jsp',
	bindings: {
		ngModel: "=",
		tabs: "<",
		componentStyle: "<?",
	},
	controller: ['$scope', function ($scope) {
		let ctrl = this;

		ctrl.isScrolling = false;
		ctrl.mouseStartX = 0;
		ctrl.clickMaxScroll = 8;
		ctrl.swimLane = null; // need angularjs 1.8 ref!

		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			window.addEventListener("mouseup", ctrl.endScroll);
			window.addEventListener("mousemove", ctrl.scrollUpdate);
		};

		ctrl.onTabSelect = (tab) =>
		{
			ctrl.ngModel = tab.value;
		}

		ctrl.tabClasses = (tab) =>
		{
			if (ctrl.ngModel)
			{
				if (ctrl.ngModel === tab.value)
				{
					return ["active"];
				}
			}
			return [];
		}

		ctrl.componentClasses = () =>
		{
			return [ctrl.componentStyle, ctrl.type];
		}

		// <--- mouse scroll tracking --->
		ctrl.startScroll = (event) =>
		{
			ctrl.isScrolling = true;

			ctrl.swimLane = event.currentTarget.parentElement;
			ctrl.mouseStartX = event.clientX;
			ctrl.initalScrollPosition = ctrl.swimLane.scrollLeft;
		}

		ctrl.scrollUpdate = (event) =>
		{
			if (ctrl.isScrolling)
			{
				ctrl.swimLane.scroll(ctrl.initalScrollPosition - (event.clientX - ctrl.mouseStartX), 0);
				$scope.$digest();
			}
		}

		ctrl.endScroll = (event, tab) =>
		{
			if (ctrl.isScrolling)
			{
				ctrl.isScrolling = false;

				if (Math.abs(ctrl.initalScrollPosition - ctrl.swimLane.scrollLeft) < ctrl.clickMaxScroll && tab)
				{
					ctrl.onTabSelect(tab);
				}
			}
		}
	}]
});