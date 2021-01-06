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

angular.module('Admin.Section').component('manageUsersAdmin',
{
	templateUrl: 'src/admin/section/manageUserPage/manageUserAdmin.jsp',
	bindings: {},
	controller: [
			'$scope',
			'$location',
			'$uibModal',
			'staticDataService',
			'NgTableParams',
			'providerService',
		function (
				$scope,
				$location,
				$uibModal,
				staticDataService,
				NgTableParams,
				providerService,
		)
	{
		let ctrl = this;
		ctrl.keyword = "";
		ctrl.providerType = null;
		ctrl.providerTypeOptions = staticDataService.getProviderTypes();
		ctrl.providerStatus = true;// Active
		ctrl.providerStatusOptions = staticDataService.getProviderStatuses();
		ctrl.sortMode = "providerNo";
		ctrl.providerList = [];

		ctrl.$onInit = function ()
		{
			ctrl.loadProviderList();
			ctrl.tableParams = new NgTableParams(
				{
					page: 1, // show first page
					count: -1, // unlimited
					sorting:
					{
						providerNo: 'desc',
					}
				},
				{
					// called when sort order changes
					getData: function (params) {
						ctrl.sortMode = params.orderBy();
					}
				}
			);

			$scope.$watch("$ctrl.providerStatus", function (newVal, oldVal)
			{// fetch provider list when provider status is changed.
				ctrl.loadProviderList();
			});
		};

		ctrl.loadProviderList = function()
		{
			providerService.searchProviders({active:ctrl.providerStatus}).then(
					function success(result)
					{
						ctrl.providerList = result
					},
					function error(result)
					{
						console.error("Failed to fetch provider list with error: " + result)
					}
			);
		};

		ctrl.filterProviders = function (provider, index, array)
		{
			// provider type filter
			if (ctrl.providerType)
			{
				if (provider.providerType !== ctrl.providerType)
				{
					return false;
				}
			}

			// search filter
			if (ctrl.keyword.length > 0)
			{
				if (!isNaN(ctrl.keyword))
				{// if number, filter on provider number
					if (!provider.providerNo.match("^" + ctrl.keyword))
					{
						return false;
					}
				}
				else
				{// filter on last, first name.
					let names = ctrl.keyword.split(',');
					names = names.map((name) => name.trim().toLowerCase());
					if (names.length === 1)
					{
						if (!provider.lastName.toLowerCase().match ("^" + names[0]) &&
								!provider.firstName.toLowerCase().match("^" + names[0]))
						{// match last name or first name.
							return false;
						}
					}
					if (names.length >= 2)
					{
						if (!provider.lastName.toLowerCase().match("^" + names[0]))
						{// first name
							return false;
						}
						if (!provider.firstName.toLowerCase().match("^" + names[1]))
						{ // last name
							return false;
						}
					}
				}
			}

			return true;
		};

		ctrl.toAddUser = function ()
		{
			$location.url("/admin/addUser");
		};

		ctrl.toViewUser = function (providerNo)
		{
			$location.url("/admin/viewUser?providerNo=" + providerNo)
		};

		ctrl.toEditUser = function (providerNo)
		{
			$location.url("/admin/editUser?providerNo=" + providerNo);
		};

		ctrl.changeProviderStatus = async function (providerNo, status)
		{
			try
			{
				let title = "";
				let message = "Are you sure you want to";
				if (status)
				{
					title = "Enable Provider?";
					message += " enable this provider?";
				}
				else
				{
					title = "Disable Provider?";
					message += " delete this provider?"
				}

				let choice = await Juno.Common.Util.confirmationDialog($uibModal, title, message);
				if (choice)
				{
					// change provider status
					providerService.enableProvider(providerNo, status).then(
							function success(result)
							{
								ctrl.loadProviderList();
							},
							function error(result)
							{
								console.error("Failed to update provider status with error: " + result);
							}
					);
				}
			}
			catch (e)
			{
				console.error("An error occurred while presenting inactivate provider dialog. error: " + e);
			}
		}
	}]
});