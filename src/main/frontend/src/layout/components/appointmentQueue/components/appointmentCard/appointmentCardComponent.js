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
	JUNO_STYLE
} from "../../../../../common/components/junoComponentConstants";
import {CommunicationType} from "../../../../../lib/integration/aqs/model/CommunicationType";

angular.module('Layout.Components').component('appointmentCard', {
	templateUrl: 'src/layout/components/appointmentQueue/components/appointmentCard/appointmentCard.jsp',
	bindings: {
		ngModel: "=?",
		componentStyle: "<?",
		onDelete: "&?",
		onAdd: "&?",
	},
	controller: ["$scope", function ($scope)
	{
		let ctrl = this;

		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

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

		ctrl.getLeadingIcon = () =>
		{
			if (ctrl.isAppointmentInPerson())
			{
				return "icon-here";
			}
			else if (ctrl.isAppointmentVideo())
			{
				return "icon-video-2";
			}
			else if (ctrl.isAppointmentAudio())
			{
				return "icon-tele-call";
			}
			else if (ctrl.isAppointmentChat())
			{
				return "icon-chat";
			}
			else
			{
				return "icon-question";
			}
		}

		ctrl.getLeadingIconTooltip = () =>
		{
			if (ctrl.isAppointmentInPerson())
			{
				return "In person appointment."
			}
			else if (ctrl.isAppointmentVideo())
			{
				return "Telehealth appointment.";
			}
			else if (ctrl.isAppointmentAudio())
			{
				return "Telehealth appointment (audio only).";
			}
			else if (ctrl.isAppointmentChat())
			{
				return "Virtual chat.";
			}
			else
			{
				return "Unknown appointment type";
			}
		}

		ctrl.onAddBtnClick = () =>
		{
			if (ctrl.onAdd)
			{
				ctrl.onAdd({});
			}
		}

		ctrl.onDeleteBtnClick = () =>
		{
			if (ctrl.onDelete)
			{
				ctrl.onDelete({});
			}
		}

		ctrl.isAppointmentInPerson = () =>
		{
			return ctrl.ngModel != null && ctrl.ngModel.communicationType === CommunicationType.InPerson;
		}

		ctrl.isAppointmentVideo = () =>
		{
			return ctrl.ngModel != null && ctrl.ngModel.communicationType === CommunicationType.Video;
		}

		ctrl.isAppointmentAudio = () =>
		{
			return ctrl.ngModel != null && ctrl.ngModel.communicationType === CommunicationType.Audio;
		}

		ctrl.isAppointmentChat = () =>
		{
			return ctrl.ngModel != null && ctrl.ngModel.communicationType === CommunicationType.Chat;
		}
	}]
});