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
	JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE,
	JUNO_TAB_TYPE
} from "../../../common/components/junoComponentConstants";
import {AqsQueuesApi, AqsQueuedAppointmentApi} from "../../../../generated";
import AppointmentBooking from "../../../common/modals/bookAppointmentModal/appointmentBooking";
import {ErrorCodes} from "../../../lib/integration/aqs/error/ErrorCodes";
import ToastService from "../../../lib/alerts/service/ToastService";

angular.module('Layout.Components').component('appointmentQueue', {
	templateUrl: 'src/layout/components/appointmentQueue/appointmentQueue.jsp',
	bindings: {
		componentStyle: "<?"
	},
	controller: [
		"$scope",
		"$rootScope",
		"$http",
		"$httpParamSerializer",
		"$uibModal",
		function (
			$scope,
			$rootScope,
			$http,
			$httpParamSerializer,
			$uibModal,
		)
	{
		let ctrl = this;

		$scope.JUNO_TAB_TYPE = JUNO_TAB_TYPE;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

		// load apis
		let aqsQueuesApi = new AqsQueuesApi($http, $httpParamSerializer,
				'../ws/rs');
		let aqsQueuedAppointmentApi = new AqsQueuedAppointmentApi($http, $httpParamSerializer,
				'../ws/rs');

		ctrl.toastService = new ToastService();
		ctrl.supportEmail = "support@junoemr.com"; // TODO read from centralized config?

		// if true show no queues zero state
		ctrl.noQueues = false;

		// hash of all queues
		ctrl.queues = [];
		// tab options used to select queue.
		ctrl.tabOptions = [];
		// currently selected queue (selected by tab bar)
		ctrl.currentQueue = null;

		// queue polling
		ctrl.QUEUE_POLLING_INVTERVAL = 30000; // 30 seconds
		ctrl.queueUpdateInterval = null;

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

		// ======= Drag N Drop ===========
		ctrl.dragStartY = 0;
		// ======= Drag N Drop ===========


		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;

			ctrl.listRef = angular.element(".appointment-queue .list");
			ctrl.listContentRef = angular.element(".appointment-queue .list .list-content ul");

			// recalculate scroll height on height change
			ctrl.resizeObserver = new ResizeObserver(() =>
			{
				ctrl.calculateScrollHeight(true);
			});
			ctrl.resizeObserver.observe(ctrl.listRef[0]);

			ctrl.loadQueues();

			ctrl.queueUpdateInterval = window.setInterval(() => {
				this.loadQueues();
			}, ctrl.QUEUE_POLLING_INVTERVAL);
		}

		ctrl.$doCheck = () =>
		{
			// update scroll bar
			ctrl.calculateScrollHeight(false);
		}

		ctrl.$onDestroy = () =>
		{
			window.clearInterval(ctrl.queueUpdateInterval);
		}

		ctrl.loadQueues = async () =>
		{
			ctrl.queues = [];

			try
			{
				let appointmentQueues = (await aqsQueuesApi.getAppointmentQueues()).data.body;

				if (appointmentQueues.length > 0)
				{
					for (let queue of appointmentQueues)
					{
						let queuedAppointments = (await aqsQueuedAppointmentApi.getAppointmentsInQueue(queue.id)).data.body;
						queuedAppointments.sort((firstAppt, secondAppt) => firstAppt.queuePosition - secondAppt.queuePosition);
						queue.items = queuedAppointments;
						ctrl.queues.push(queue);
					}

					ctrl.setupQueueTabs();

					if (ctrl.currentQueue == null)
					{	// set default selection to first queue.
						ctrl.currentQueue = ctrl.queues[0];
					} else
					{ // we have updated attempt to re-acquire queue
						ctrl.currentQueue = ctrl.queues.find((queue) => queue.queueName === ctrl.currentQueue.queueName)
					}
				}
				else
				{
					// no queues
					ctrl.noQueues = true;
				}
			}
			catch(err)
			{
				const message = "Appointment Queue: " +
					((err && err.data && err.data.error && err.data.error.message) ? err.data.error.message : "Unknown Error. please contact support");
				console.error("Failed to fetch appointment queues with error: " + message, err);
				ctrl.toastService.errorToast(message, false);
			}
		}

		ctrl.setupQueueTabs = () =>
		{
			ctrl.tabOptions = [];
			for (let queue of ctrl.queues)
			{
				let tabColor = queue.queueColor;
				if (!tabColor)
				{// default to primary base
					tabColor = "4d73bf";
				}

				ctrl.tabOptions.push({
					label: queue.queueName,
					value: queue,
					color: tabColor,
					tabCount: queue.items.length,
				});
			}
		}

		ctrl.calculateScrollHeight = (digest) =>
		{
			let childElement = ctrl.listContentRef.children()[0];
			if (childElement && ctrl.currentQueue)
			{
				let elementHeight = childElement.clientHeight;
				let elementsToDisplay = ctrl.currentQueue.items.length + ctrl.appointmentSlots;
				ctrl.listContentHeight = Math.max(elementHeight * elementsToDisplay, ctrl.listRef[0].clientHeight);

				if (digest)
				{
					$scope.$digest();
				}
			}
		}

		ctrl.deleteQueueItem = async (itemIndex) =>
		{
			try
			{
				let reason = await Juno.Common.Util.openInputDialog(
					$uibModal,
					"Delete Queued Appointment?",
					"Please provide a reason as to why you are deleting this appointment.",
					ctrl.componentStyle,
					"Proceed",
					"Please enter reason here",
					1024);
				if (reason)
				{
					try
					{
						await aqsQueuedAppointmentApi.deleteAppointment(ctrl.currentQueue.id, ctrl.currentQueue.items[itemIndex].id, reason);
						ctrl.currentQueue.items.splice(itemIndex, 1);
						ctrl.setupQueueTabs();
					}
					catch(err)
					{
						console.error("Failed to delete queued appointment, with error: " + err);
					}
				}
			}
			catch (err)
			{
				// user hit ESC.
				console.log(err);
			}
		}

		// add a queued appointment to a schedule
		ctrl.addToSchedule = async (itemIndex) =>
		{
			try
			{
				await $uibModal.open(
						{
							component: 'addQueuedAppointmentModal',
							backdrop: 'static',
							windowClass: "juno-simple-modal-window",
							resolve: {
								style: () => ctrl.componentStyle,
								queueId: () => ctrl.currentQueue.id,
								clinicId: () => ctrl.currentQueue.items[itemIndex].clinicId,
								siteId: () => ctrl.currentQueue.items[itemIndex].siteId,
								isVirtual: () => ctrl.currentQueue.items[itemIndex].virtual,
								queuedAppointmentId: () => ctrl.currentQueue.items[itemIndex].id,
								loadQueuesCallback: () => ctrl.loadQueues,
							}
						}
				).result;

				$rootScope.$broadcast('schedule:refreshEvents');
			}
			catch(err)
			{
				// ESC button pressed probably
				console.warn("Modal closed with rejection ", err);
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

		ctrl.onDragMoved = async (event, index) =>
		{
			// compensate for bug in dnd library
			// https://github.com/marceljuenemann/angular-drag-and-drop-lists/issues/500
			if (event.screenY - ctrl.dragStartY < 0)
			{
				index += 1
			}

			ctrl.currentQueue.items.splice(index, 1);
		}

		ctrl.onDragStart = (event) =>
		{
			ctrl.dragStartY = event.screenY;
		}

		// fires when item is droped in to list
		ctrl.onDragDrop = (index, item, event) =>
		{
			// compensate for bug in dnd library
			// https://github.com/marceljuenemann/angular-drag-and-drop-lists/issues/500
			if (event.screenY - ctrl.dragStartY > 0)
			{
				index -= 1;
			}

			aqsQueuedAppointmentApi.moveAppointment(this.currentQueue.id, item.id, { queuePosition: index});
			return item;
		}

		ctrl.openBookQueuedAppointmentModal = async () =>
		{
			try
			{
				let bookingData = new AppointmentBooking();
				// default duration to queue duration.
				bookingData.duration = ctrl.currentQueue.defaultAppointmentDurationMinutes;

				await $uibModal.open(
					{
						component: 'bookAppointmentModal',
						backdrop: 'static',
						windowClass: "juno-modal",
						resolve: {
							style: () => JUNO_STYLE.DEFAULT,
							title: () => "Queue Appointment",
							bookingData: () => bookingData,
							onCreateCallback: () => ctrl.bookNewQueuedAppointment,
						}
					}
				).result;

			}
			catch(err)
			{
				// ESC button pressed probably
				console.warn("Modal closed with rejection ", err);
			}
		}

		// create a new queued appointment
		ctrl.bookNewQueuedAppointment = async (appointmentBooking) =>
		{
			const queuedAppointmentBookingDto = {
				demographicNo: appointmentBooking.demographic.demographicNo,
				durationMinutes: parseInt(appointmentBooking.duration),
				notes: appointmentBooking.notes,
				reason: appointmentBooking.reason,
				reasonType: appointmentBooking.reasonType,
				siteId: appointmentBooking.siteId,
				virtual: appointmentBooking.virtual,
				critical: appointmentBooking.critical,
			}

			try
			{
				await aqsQueuedAppointmentApi.createQueuedAppointment(ctrl.currentQueue.id, queuedAppointmentBookingDto);
			}
			catch(errorResponse)
			{
				if (errorResponse.data.error.data.errorCode === ErrorCodes.QUEUE_AVAILABILITY_ERROR)
				{
					Juno.Common.Util.errorAlert($uibModal, "Failed to book", "The Queue has closed. You can adjust the queue hours in the Admin section.");
				}
			}
			
			// refresh display
			this.loadQueues();
		}
	}]
});