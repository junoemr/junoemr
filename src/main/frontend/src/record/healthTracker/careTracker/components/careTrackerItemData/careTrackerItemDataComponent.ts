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


import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE, LABEL_POSITION} from "../../../../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../../../../common/security/securityConstants";
import {Moment} from "moment/moment";

angular.module('Record.Tracker.CareTracker').component('careTrackerItemData',
	{
		templateUrl: 'src/record/healthTracker/careTracker/components/careTrackerItemData/careTrackerItemData.jsp',
		bindings: {
			componentStyle: "<?",
			model: "<",
		},
		controller: [
			function (
			)
			{
				const ctrl = this;

				ctrl.SecurityPermissions = SecurityPermissions;
				ctrl.LABEL_POSITION = LABEL_POSITION;
				ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
				ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

				ctrl.validationAlerts = [];

				ctrl.$onInit = async () =>
				{
					ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
				}

				ctrl.getDateForDisplay = (dateTime: Moment) =>
				{
					return (dateTime && dateTime.isValid()) ? Juno.Common.Util.formatDate(dateTime) : "Invalid Date";
				}
			}]
	});