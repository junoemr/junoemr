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
import {AqsQueuesApi, AqsAppointmentsApi} from "../../../../generated";

angular.module('Layout.Components').component('appointmentQueue', {
	templateUrl: 'src/layout/components/appointmentQueue/appointmentQueue.jsp',
	bindings: {
		componentStyle: "<?"
	},
	controller: [
		"$scope",
		"$http",
		"$httpParamSerializer",
		"$uibModal",
		function (
			$scope,
			$http,
			$httpParamSerializer,
			$uibModal,
		)
	{
		let ctrl = this;

		$scope.JUNO_TAB_TYPE = JUNO_TAB_TYPE;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		// load apis
		let aqsQueuesApi = new AqsQueuesApi($http, $httpParamSerializer,
				'../ws/rs');
		// load apis
		let aqsAppointmentsApi = new AqsAppointmentsApi($http, $httpParamSerializer,
				'../ws/rs');

		// hash of all queues
		ctrl.queues = [];
		// tab options used to select queue.
		ctrl.tabOptions = [];
		// currently selected queue (selected by tab bar)
		ctrl.currentQueue = null;

		// ======= Scroll Height tracking ===========
		// List container element reference
		ctrl.listRef = null;
		ctrl.listContentRef = null;
		// Height of the content of the appointment list. Used
		// to adjust scroll height.
		ctrl.listContentHeight = 300;
		// minimum number of blank appointment slots to show.
		ctrl.appointmentSlots = 4;
		ctrl.resizeObserver = null;
		// ======= Scroll Height tracking ===========

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

			ctrl.loadQueues();
		}

		ctrl.$doCheck = () =>
		{
			// update scroll bar
			ctrl.calculateScrollHeight(false);
		}

		ctrl.loadQueues = async () =>
		{
			ctrl.queues = [];

			try
			{
				let appointmentQueues = (await aqsQueuesApi.getAppointmentQueues()).data.body;
				for(let queue of appointmentQueues)
				{
					queue.items = (await aqsAppointmentsApi.getAppointmentsInQueue(queue.id)).data.body;
					ctrl.queues.push(queue);
				}

				ctrl.setupQueueTabs();

				// set default selection to first queue.
				ctrl.currentQueue = ctrl.queues[0];
			}
			catch(err)
			{
				console.error("Failed to fetch appointment queues with error: " + err);
			}
		}

		ctrl.setupQueueTabs = () =>
		{
			ctrl.tabOptions = [];
			for (let queue of ctrl.queues)
			{
				ctrl.tabOptions.push({
					label: queue.queueName,
					value: queue,
					color: queue.queueColor,
				});
			}
		}

		ctrl.calculateScrollHeight = (digest) =>
		{
			let childElement = ctrl.listContentRef.children()[0];
			if (childElement && ctrl.currentQueue)
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

		ctrl.deleteQueueItem = async (itemIndex) => {
			try
			{
				let reason = await Juno.Common.Util.openInputDialog($uibModal,
				                                       "Delete Queued Appointment?",
				                                       "Please provide a reason as to why you are deleting this appointment.",
				                                       ctrl.componentStyle,
				                                       "Proceed");
				if (reason)
				{
					// TODO actually delete the queued appointment
					ctrl.currentQueue.items.splice(itemIndex, 1);
				}
			}
			catch (err)
			{
				// user hit ESC.
				console.log(err);
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