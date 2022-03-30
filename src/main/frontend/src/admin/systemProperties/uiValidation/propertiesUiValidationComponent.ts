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

import {SystemPreferenceApi} from "../../../../generated/api/SystemPreferenceApi";
import {SYSTEM_PROPERTIES} from "../../../common/services/systemPreferenceServiceConstants";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";

angular.module('Admin').component('systemPropertiesUiValidation',
	{
		templateUrl: 'src/admin/systemProperties/uiValidation/propertiesUiValidation.jsp',
		bindings: {},
		controller: ['$scope', '$http', '$httpParamSerializer', '$state', function ($scope, $http, $httpParamSerializer, $state)
		{
			const ctrl = this;
			ctrl.textModel = "CloudPractice";

			ctrl.selectModel = "Juno";
			ctrl.selectOptions = [
				{
					label: "Juno EMR",
					value: "Juno"
				},
				{
					label: "ClinicAid",
					value: "CA"
				},
				{
					label: "MyHealthAccess",
					value: "MHA"
				}
			]

			ctrl.toggleModel = false;

			ctrl.onClick = () =>
			{
				alert("Clack");
			}

			ctrl.iconList = [
				"icon-align-center",
				"icon-align-justified",
				"icon-align-left",
				"icon-align-right",
				"icon-bold",
				"icon-check-circle",
				"icon-contacts",
				"icon-erlenmeyer-flask",
				"icon-horizontal-rule",
				"icon-indent-less",
				"icon-indent",
				"icon-italic",
				"icon-list-bullet",
				"icon-list-numbers",
				"icon-long-arrow-down",
				"icon-long-arrow-left",
				"icon-long-arrow-right",
				"icon-long-arrow-up",
				"icon-minus",
				"icon-smart-code",
				"icon-strikethrough",
				"icon-subscript",
				"icon-superscript",
				"icon-table",
				"icon-tag",
				"icon-tags",
				"icon-underline",
				"icon-clipboard",
				"icon-clock",
				"icon-pause-circle",
				"icon-critical",
				"icon-dashboard",
				"icon-duplicate",
				"icon-edit",
				"icon-prescription-add",
				"icon-prescription-check",
				"icon-prescription-d-i",
				"icon-prescription-d-ii",
				"icon-prescription-d-iii",
				"icon-prescription-d-iv",
				"icon-prescription-d-v",
				"icon-prescription-d",
				"icon-prescription-ethical",
				"icon-prescription-g-i",
				"icon-prescription-g-ii",
				"icon-prescription-g-iii",
				"icon-prescription-g-iv",
				"icon-prescription-g-v",
				"icon-prescription-g",
				"icon-prescription-homeopathic",
				"icon-prescription-narc-i",
				"icon-prescription-narc-ii",
				"icon-prescription-narc-iii",
				"icon-prescription-narc-iv",
				"icon-prescription-narc-v",
				"icon-prescription-narc",
				"icon-prescription-no",
				"icon-prescription-otc",
				"icon-prescription-search",
				"icon-prescription-send",
				"icon-prescription",
				"icon-star-outline",
				"icon-1",
				"icon-2",
				"icon-3",
				"icon-4",
				"icon-5",
				"icon-6",
				"icon-7",
				"icon-8",
				"icon-9",
				"icon-10",
				"icon-11",
				"icon-12",
				"icon-13",
				"icon-14",
				"icon-15",
				"icon-16",
				"icon-17",
				"icon-18",
				"icon-19",
				"icon-20",
				"icon-21",
				"icon-22",
				"icon-23",
				"icon-24",
				"icon-account",
				"icon-add",
				"icon-alarm-off",
				"icon-alarm",
				"icon-archive",
				"icon-arrow-down",
				"icon-arrow-left",
				"icon-arrow-right",
				"icon-arrow-up",
				"icon-aside-closed",
				"icon-aside-open",
				"icon-asterisk",
				"icon-bars",
				"icon-billed",
				"icon-billing",
				"icon-blood",
				"icon-bloodpressure",
				"icon-bookmark",
				"icon-breathing",
				"icon-briefcase",
				"icon-calendar-add",
				"icon-calendar-search",
				"icon-calendar-week",
				"icon-calendar",
				"icon-cancel",
				"icon-cancer",
				"icon-chat",
				"icon-check",
				"icon-chevron-down",
				"icon-chevron-left",
				"icon-chevron-right",
				"icon-chevron-up",
				"icon-clinic",
				"icon-close",
				"icon-cloud-download",
				"icon-cloud-upload",
				"icon-cloud",
				"icon-comment",
				"icon-cpx",
				"icon-day-sheet",
				"icon-delete",
				"icon-dot",
				"icon-ellipsis-h",
				"icon-ellipsis-v",
				"icon-empty",
				"icon-exclamation",
				"icon-external-link",
				"icon-fax",
				"icon-file-2",
				"icon-file",
				"icon-filter",
				"icon-find-a-clinic",
				"icon-flag",
				"icon-flip",
				"icon-gear",
				"icon-gears",
				"icon-genetics",
				"icon-health-data",
				"icon-health-tracker",
				"icon-healthcare-2",
				"icon-healthcare",
				"icon-here",
				"icon-info-circle",
				"icon-linux",
				"icon-list",
				"icon-location-arrow",
				"icon-lock-open",
				"icon-lock",
				"icon-login",
				"icon-logout",
				"icon-mail",
				"icon-map-marker",
				"icon-mha",
				"icon-minimize",
				"icon-mobile-app",
				"icon-money",
				"icon-nbox",
				"icon-noshow",
				"icon-picked",
				"icon-play-circle",
				"icon-plus-circle",
				"icon-plus",
				"icon-print",
				"icon-private",
				"icon-question",
				"icon-refresh",
				"icon-reply-all",
				"icon-reply",
				"icon-resize-max",
				"icon-resize-min",
				"icon-rotate",
				"icon-search",
				"icon-send-round",
				"icon-send",
				"icon-share",
				"icon-sort-down",
				"icon-sort-up",
				"icon-sorted",
				"icon-star",
				"icon-starbill-1",
				"icon-starbill",
				"icon-stop",
				"icon-support",
				"icon-tele-call",
				"icon-tele-camera-flip",
				"icon-tele-camera",
				"icon-tele-doc",
				"icon-tele-hangup",
				"icon-tele-volume-down",
				"icon-tele-volume-mute",
				"icon-tele-volume-off",
				"icon-tele-volume-up",
				"icon-temperature",
				"icon-todo",
				"icon-tooth",
				"icon-user-add",
				"icon-user-group",
				"icon-user-md",
				"icon-user-search",
				"icon-user",
				"icon-video-2",
				"icon-video",
				"icon-view-off",
				"icon-view",
				"icon-weight",
				"icon-write"]
		}]
	});