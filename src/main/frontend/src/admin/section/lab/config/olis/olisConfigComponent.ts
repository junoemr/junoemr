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

import LoadingQueue from "../../../../../lib/util/LoadingQueue";
import ToastService from "../../../../../lib/alerts/service/ToastService";
import {Moment} from "moment/moment";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN} from "../../../../../common/components/junoComponentConstants";

angular.module('Admin.Section.Lab.Olis').component('olisConfig',
	{
		templateUrl: 'src/admin/section/lab/config/olis/olisConfig.jsp',
		bindings: {

		},
		controller: [
			'labService',
			'systemPreferenceService',
			function (labService,
			          systemPreferenceService)
		{
			const ctrl = this;

			ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

			ctrl.loadingQueue = new LoadingQueue();
			ctrl.pollingPropName = "olis_polling_enabled";
			ctrl.labType = "OLIS_HL7"
			ctrl.frequencySelectOptions = [
				{
					label: "5 Minutes",
					value: 5,
				},
				{
					label: "10 Minutes",
					value: 10,
				},
				{
					label: "15 Minutes",
					value: 15,
				},
				{
					label: "30 Minutes",
					value: 30,
				},
				{
					label: "1 Hour",
					value: 60,
				},
				{
					label: "2 Hours",
					value: 120,
				},
				{
					label: "4 Hours",
					value: 240,
				},
			];

			ctrl.$onInit = async (): Promise<void> =>
			{
				try
				{
					ctrl.loadingQueue.pushLoadingState();
					const responseArray = await Promise.all([
						systemPreferenceService.isPreferenceEnabled(ctrl.pollingPropName, false),
						labService.getOlisSystemSettings(),
						labService.getOlisProviderSettings(),
					]);
					ctrl.pollingEnabled = responseArray[0];
					ctrl.systemSettings = responseArray[1];
					ctrl.providerSettingsList = responseArray[2];

					ctrl.loadingQueue.popLoadingState();
				}
				catch (e)
				{
					new ToastService().errorToast("Error loading olis configuration", true);
					console.error(e);
				}
			}

			ctrl.setPollingEnabled = (value: boolean): void =>
			{
				systemPreferenceService.setPreference(ctrl.pollingPropName, value.toString());
			}

			ctrl.manualLabPull = async (): Promise<void> =>
			{
				ctrl.loadingQueue.pushLoadingState();
				await labService.triggerLabPull(ctrl.labType);
				// refresh provider settings after pulling labs, as dates may change
				ctrl.providerSettingsList = await labService.getOlisProviderSettings();
				ctrl.loadingQueue.popLoadingState();
			}

			ctrl.labSearch = (): void =>
			{
				const target = "_blank";
				const url = "../olis/Search.jsp";
				window.open(url, target, "scrollbars=yes, location=no, width=1024, height=800");
			};

			ctrl.saveSystemSettings = async (): Promise<void> =>
			{
				ctrl.loadingQueue.pushLoadingState();
				await labService.updateOlisSystemSettings(ctrl.systemSettings);
				ctrl.loadingQueue.popLoadingState();
			}

			ctrl.startDateDisplay = (date: Moment): string =>
			{
				if(date)
				{
					return Juno.Common.Util.formatMomentDateTimeNoTimezone(date);
				}
				else
				{
					return "System Default";
				}
			}

			ctrl.configurationStatusDisplay = (configured: boolean): string =>
			{
				return (configured)? "OK" : "Not Configured";
			}
		}],
	}
);