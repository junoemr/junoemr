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
	JUNO_STYLE,
	JUNO_TAB_TYPE
} from "../../../common/components/junoComponentConstants";

angular.module('Layout.Components').component('appointmentQueue', {
	templateUrl: 'src/layout/components/appointmentQueue/appointmentQueue.jsp',
	bindings: {
		componentStyle: "<?"
	},
	controller: ["$scope", function ($scope)
	{
		let ctrl = this;

		$scope.JUNO_TAB_TYPE = JUNO_TAB_TYPE;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		// hash of all queues {queueName: [array of queue appts]}
		ctrl.queues = {
			starTrek: {
				metaDataAndStuff: 42,

				items: [
					{
						demographicNo: 1,
						patientName: "Kirk",
						reason: "Set phasers to kill *rips of shirt*",
						isTelehealth: true,
					},
					{
						demographicNo: 2,
						patientName: "Spock",
						reason: "Logic dictates that this is a test",
						isTelehealth: true,
					},
					{
						demographicNo: 3,
						patientName: "Dr. MiCoy",
						reason: "He's dead Jim",
						isTelehealth: true,
					},
				],

			},
			galactica: {
				metaDataAndStuff: 42,

				items: [
					{
						demographicNo: 1,
						patientName: "Admiral Adama",
						reason: "Fire the nukes!",
						isTelehealth: true,
					},
					{
						demographicNo: 2,
						patientName: "Starbuck",
						reason: "I'm dead but, I came back as an angle or some bull crap like that",
						isTelehealth: true,
					},
					{
						demographicNo: 3,
						patientName: "Boomer",
						reason: "def not a Cylon",
						isTelehealth: true,
					},
					{
						demographicNo: 4,
						patientName: "Gaius Baltar",
						reason: "Loves, Number Six, A Cylon",
						isTelehealth: false,
					},
				],
			},
			longList: {
				items: [...Array(16)].map((i) => {return {patientName: "Long, Long", reason: "lots of appts", isTelehealth: true}}),
			}
		};

		// tab options used to select queue.
		ctrl.tabOptions = [
			{label: "Star Trek", value: ctrl.queues["starTrek"], color: "#27ae60"},
			{label: "Battle Star Galactica", value: ctrl.queues["galactica"], color: "#f39c12"},
			{label: "Long List", value: ctrl.queues["longList"], color: "#e74c3c"},
		];

		// currently selected queue (selected by tab bar)
		ctrl.currentQueue = ctrl.queues["starTrek"];
		// max queue length
		ctrl.maxQueueLength = 128;

		// List container element reference
		ctrl.listRef = null;
		ctrl.listContentRef = null;
		// Height of the content of the appointment list. Used
		// to adjust scroll height.
		ctrl.listContentHeight = 0;
		// minimum number of blank appointment slots to show.
		ctrl.appointmentSlots = 4;
		ctrl.resizeObserver = null;

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			ctrl.listRef = angular.element(".appointment-queue .list");
			ctrl.listContentRef = angular.element(".appointment-queue .list .list-content");

			// recalculate scroll height on height change
			ctrl.resizeObserver = new ResizeObserver(() =>
			{
				ctrl.calculateScrollHeight(true);
			});
			ctrl.resizeObserver.observe(ctrl.listRef[0]);
		}

		ctrl.$doCheck = () =>
		{
			// update scroll bar
			ctrl.calculateScrollHeight(false);
		}

		ctrl.calculateScrollHeight = (digest) =>
		{
			let childElement = ctrl.listContentRef.children()[0];
			if (childElement)
			{
				let elementHeight = childElement.clientHeight;
				let elementsToDisplay = Math.max(ctrl.currentQueue.items.length + ctrl.appointmentSlots,
																	Math.floor(ctrl.listRef[0].clientHeight / elementHeight));
				ctrl.listContentHeight = elementHeight * elementsToDisplay;

				if (digest)
				{
					$scope.$digest();
				}
			}
		}

		ctrl.getListScrollHeightStyle = () =>
		{
			return {height: ctrl.listContentHeight};
		}

		ctrl.getComponentClasses = () =>
		{
			return [ctrl.componentStyle, ctrl.componentStyle + JUNO_BACKGROUND_STYLE.SECONDARY];
		}

		ctrl.getPrimaryBackgroundClass = () =>
		{
			return [ctrl.componentStyle + JUNO_BACKGROUND_STYLE.PRIMARY];
		}
	}]
});